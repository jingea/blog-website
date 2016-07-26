category: Java工具
date: 2016-04-12
title: Mybatis SqlSession
---
一直在使用Mybatis, 但是一直对Mybatis中的SqlSession的实际操作过程没有深入了解过, 今天在项目中引用了Mybatis-Guice模块, 很好奇Mybatis-Guice是如何做的SqlSeesion自动资源释放,因此今天就找时间好好研究一下`SqlSession`.

首先看一下[Mybatis3官方文档](http://www.mybatis.org/mybatis-3/zh/getting-started.html)中对`SqlSessionFactoryBuilder`, `SqlSessionFactory`和`SqlSession`的描述：
* `SqlSessionFactoryBuilder` : 用于构建`SqlSessionFactory`. 我们可以使用它来构建一个单数据源或者多数据源的应用程序.
* `SqlSessionFactory` : 创建`SqlSession`, 该实例的生命周期应该是整个应用程序.
* `SqlSession` : 包含了面向数据库执行 SQL 命令所需的所有方法, 但它不是线程安全的. 每当向数据库发起一个请求时都应该打开一个`SqlSession`,然后操作完之后再`finally`中关闭它.

## 使用示例
`SqlSessionFactoryBuilder`是通过xml配置文件或者`Configuration`建构出`SqlSessionFactory`, 然后`SqlSessionFactory`通过`openSession()`来获得一个`SqlSession`. `SqlSession`接口实现自`Closeable`接口. 按照官网所说, 我们应该这样使用`SqlSession`
```java
SqlSession session = sqlSessionFactory.openSession();
try {
  // do work
} finally {
  session.close();
}
```
但其实我们可以使用Java7提供的AutoClose语法
```java
try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
	// do work
}
```
这样代码就精简了很多.

## SqlSession 源码
`SqlSession`接口中定义了大量的我们操作SQL提供的接口
* <T> T selectOne(String statement);
* <E> List<E> selectList(String statement);
* void select(String statement, ResultHandler handler);
等等. 我们看一下它的实现类`DefaultSqlSession`的实现:
```java
private Configuration configuration;
private Executor executor;
private boolean dirty;
```
* `Configuration` 是我们通过`SqlSessionFactoryBuilder`构建出`SqlSessionFactory`时使用的配置
* `Executor` 是真正的sql执行的部分

我们在`DefaultSqlSessionFactory`看一下`SqlSession`的真实创建过程
```java
private SqlSession openSessionFromDataSource(ExecutorType execType, TransactionIsolationLevel level, boolean autoCommit) {
  Transaction tx = null;
  try {
    final Environment environment = configuration.getEnvironment();
    final TransactionFactory transactionFactory = getTransactionFactoryFromEnvironment(environment);
    tx = transactionFactory.newTransaction(environment.getDataSource(), level, autoCommit);
    final Executor executor = configuration.newExecutor(tx, execType, autoCommit);
    return new DefaultSqlSession(configuration, executor);
  } catch (Exception e) {
    closeTransaction(tx); // may have fetched a connection so lets call close()
    throw ExceptionFactory.wrapException("Error opening session.  Cause: " + e, e);
  } finally {
    ErrorContext.instance().reset();
  }
}
```
在`final Executor executor = configuration.newExecutor(tx, execType, autoCommit);`会根据execType创建出不同类型的`Executor`
* BatchExecutor
* ReuseExecutor
* SimpleExecutor
* CachingExecutor

下来我们看一下`selectList()`实现
```java
  public <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds) {
    try {
      MappedStatement ms = configuration.getMappedStatement(statement);
      List<E> result = executor.<E>query(ms, wrapCollection(parameter), rowBounds, Executor.NO_RESULT_HANDLER);
      return result;
    } catch (Exception e) {
      throw ExceptionFactory.wrapException("Error querying database.  Cause: " + e, e);
    } finally {
      ErrorContext.instance().reset();
    }
  }
```
> MappedStatement类在Mybatis框架中用于表示XML文件中一个sql语句节点,即一个`<select />、<update />`或者`<insert />`标签.Mybatis框架在初始化阶段会对XML配置文件进行读取,将其中的sql语句节点对象化为一个个`MappedStatement`对象.
从配置中拿到一个`MappedStatement`然后交给executor去真正的执行, 真正的有query逻辑的只有`BaseExecutor`和`CachingExecutor`, 为了简单起见,我们看一下`BaseExecutor`. 由于中间的过程还涉及到了Mybatis的本地存储, 我们也跳过这部分.
> `BaseExecutor#query()` -> `BaseExecutor#queryFromDatabase()`-> `SimpleExecutor#doQuery()`
```java
public <E> List<E> doQuery(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
    Statement stmt = null;
    try {
      Configuration configuration = ms.getConfiguration();
	  // StatementHandler用于管理java.sql.Statement, 执行真正的与数据库操作
      StatementHandler handler = configuration.newStatementHandler(wrapper, ms, parameter, rowBounds, resultHandler, boundSql);
	  // 通过StatementHandler实例化出一个Statement
      stmt = prepareStatement(handler, ms.getStatementLog());
	  // Statement开始执行查询逻辑
      return handler.<E>query(stmt, resultHandler);
    } finally {
      closeStatement(stmt);
    }
  }
```
我们看一下`SimpleStatementHandler`里的查询逻辑
```java
 public <E> List<E> query(Statement statement, ResultHandler resultHandler) throws SQLException {
    String sql = boundSql.getSql();
    statement.execute(sql);
    return resultSetHandler.<E>handleResultSets(statement);
  }
```
> `SqlSource`接口只有一个`getBoundSql(Object parameterObject)`方法,返回一个`BoundSql`对象.一个`BoundSql`对象,代表了一次sql语句的实际执行,而`SqlSource`对象的责任,就是根据传入的参数对象,动态计算出这个`BoundSql`,也就是说Mapper文件中的<if />节点的计算,是由SqlSource对象完成的.`SqlSource`最常用的实现类是`DynamicSqlSource`

那么`close()`执行的是什么操作呢？
```java
  public void close() {
    try {
      executor.close(isCommitOrRollbackRequired(false));
      dirty = false;
    } finally {
      ErrorContext.instance().reset();
    }
  }
```
我们还是看`BaseExecutor`的`close()`
```java
public void close(boolean forceRollback) {
    try {
      try {
        rollback(forceRollback);
      } finally {
		// 真正关闭资源
        if (transaction != null) transaction.close();
      }
    } catch (SQLException e) {
      log.debug("Unexpected exception on closing transaction.  Cause: " + e);
    } finally {
      transaction = null;
      deferredLoads = null;
      localCache = null;
      localOutputParameterCache = null;
      closed = true;
    }
  }
```
我们看一下`JdbcTransaction`的`close()`
```java
  public void close() throws SQLException {
    if (connection != null) {
      resetAutoCommit();
      if (log.isDebugEnabled()) {
        log.debug("Closing JDBC Connection [" + connection + "]");
      }
      connection.close();
    }
  }
```
* `Transaction` : 包装了一个数据库连接. 处理整个网络连接过程中的所有操作, 例如creation, preparation, commit/rollback and close. 

## 异常测试
如果我们不关闭SqlSession会有什么情况发生呢?
```java
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class TestMybatis {

    public static void main(String[] args) throws IOException {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        List<SqlSession> list = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            SqlSession session = sqlSessionFactory.openSession();
            QueryMapper mapper = session.getMapper(QueryMapper.class);
            list.add(session);
            System.out.println(i + "  :  " + mapper.select() + " - " + new Date().toLocaleString());
        }
        list.forEach(session -> {
            try {
                System.out.println(session.getConnection().isClosed());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}

interface QueryMapper {
    @Select("select name from user")
    public List<String> select();
}

```
下面是我们的配置文件
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <property name="driver" value="com.mysql.jdbc.Driver"/>
                <property name="url" value="jdbc:mysql://localhost:3306/test?autoReconnect=true"/>
                <property name="username" value="root"/>
                <property name="password" value="root"/>
                <property name="poolMaximumActiveConnections" value="3"/>
                <property name="poolMaximumIdleConnections" value="1"/>
                <property name="poolTimeToWait" value="5"/>
            </dataSource>
        </environment>
    </environments>
    <mappers>
        <mapper class="QueryMapper"/>
    </mappers>
</configuration>
```
我们配置了最大可用连接数为3, 最大的闲置连接为1. 结果为
```java
1  :  [123] - 2016-4-12 18:37:28
2  :  [123] - 2016-4-12 18:37:28
3  :  [123] - 2016-4-12 18:37:28
4  :  [123] - 2016-4-12 18:37:48
5  :  [123] - 2016-4-12 18:37:48
6  :  [123] - 2016-4-12 18:37:48
7  :  [123] - 2016-4-12 18:38:08
8  :  [123] - 2016-4-12 18:38:08
9  :  [123] - 2016-4-12 18:38:08
java.sql.SQLException: Error accessing PooledConnection. Connection is invalid.
	at org.apache.ibatis.datasource.pooled.PooledConnection.checkConnection(PooledConnection.java:254)
	at org.apache.ibatis.datasource.pooled.PooledConnection.invoke(PooledConnection.java:243)
	at com.sun.proxy.$Proxy3.isClosed(Unknown Source)
	at TestMybatis.lambda$main$0(TestMybatis.java:29)
	at java.util.ArrayList.forEach(ArrayList.java:1249)
	at TestMybatis.main(TestMybatis.java:27)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at com.intellij.rt.execution.application.AppMain.main(AppMain.java:144)
java.sql.SQLException: Error accessing PooledConnection. Connection is invalid.
	at org.apache.ibatis.datasource.pooled.PooledConnection.checkConnection(PooledConnection.java:254)
	at org.apache.ibatis.datasource.pooled.PooledConnection.invoke(PooledConnection.java:243)
	at com.sun.proxy.$Proxy3.isClosed(Unknown Source)
	at TestMybatis.lambda$main$0(TestMybatis.java:29)
	at java.util.ArrayList.forEach(ArrayList.java:1249)
	at TestMybatis.main(TestMybatis.java:27)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at com.intellij.rt.execution.application.AppMain.main(AppMain.java:144)
java.sql.SQLException: Error accessing PooledConnection. Connection is invalid.
	at org.apache.ibatis.datasource.pooled.PooledConnection.checkConnection(PooledConnection.java:254)
	at org.apache.ibatis.datasource.pooled.PooledConnection.invoke(PooledConnection.java:243)
	at com.sun.proxy.$Proxy3.isClosed(Unknown Source)
	at TestMybatis.lambda$main$0(TestMybatis.java:29)
	at java.util.ArrayList.forEach(ArrayList.java:1249)
	at TestMybatis.main(TestMybatis.java:27)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at com.intellij.rt.execution.application.AppMain.main(AppMain.java:144)
java.sql.SQLException: Error accessing PooledConnection. Connection is invalid.
	at org.apache.ibatis.datasource.pooled.PooledConnection.checkConnection(PooledConnection.java:254)
	at org.apache.ibatis.datasource.pooled.PooledConnection.invoke(PooledConnection.java:243)
	at com.sun.proxy.$Proxy3.isClosed(Unknown Source)
	at TestMybatis.lambda$main$0(TestMybatis.java:29)
	at java.util.ArrayList.forEach(ArrayList.java:1249)
	at TestMybatis.main(TestMybatis.java:27)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at com.intellij.rt.execution.application.AppMain.main(AppMain.java:144)
java.sql.SQLException: Error accessing PooledConnection. Connection is invalid.
	at org.apache.ibatis.datasource.pooled.PooledConnection.checkConnection(PooledConnection.java:254)
	at org.apache.ibatis.datasource.pooled.PooledConnection.invoke(PooledConnection.java:243)
	at com.sun.proxy.$Proxy3.isClosed(Unknown Source)
	at TestMybatis.lambda$main$0(TestMybatis.java:29)
	at java.util.ArrayList.forEach(ArrayList.java:1249)
	at TestMybatis.main(TestMybatis.java:27)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at com.intellij.rt.execution.application.AppMain.main(AppMain.java:144)
java.sql.SQLException: Error accessing PooledConnection. Connection is invalid.
	at org.apache.ibatis.datasource.pooled.PooledConnection.checkConnection(PooledConnection.java:254)
	at org.apache.ibatis.datasource.pooled.PooledConnection.invoke(PooledConnection.java:243)
	at com.sun.proxy.$Proxy3.isClosed(Unknown Source)
	at TestMybatis.lambda$main$0(TestMybatis.java:29)
	at java.util.ArrayList.forEach(ArrayList.java:1249)
	at TestMybatis.main(TestMybatis.java:27)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at com.intellij.rt.execution.application.AppMain.main(AppMain.java:144)
false
false
false
```
