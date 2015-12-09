category: mybatis-guice
date: 2015-12-09
title: Getting started
---
参考文档[mybatis-guice](http://mybatis.org/guice/index.html)

mybatis-guice需要依赖
```xml
<dependency>
    <groupId>com.google.inject</groupId>
    <artifactId>guice</artifactId>
    <version>4.0</version>
</dependency>
<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis-guice</artifactId>
    <version>3.7</version>
</dependency>
<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis</artifactId>
    <version>3.2.2</version>
</dependency>
<dependency>
    <groupId>com.google.inject.extensions</groupId>
    <artifactId>guice-multibindings</artifactId>
    <version>4.0</version>
</dependency>
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>5.1.37</version>
</dependency>
</dependencies>
```

一个完整的使用例子
```java
import com.google.inject.*;
import com.google.inject.name.Names;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.mybatis.guice.MyBatisModule;
import org.mybatis.guice.datasource.builtin.PooledDataSourceProvider;
import org.mybatis.guice.datasource.helper.JdbcHelper;
import org.mybatis.guice.transactional.Transactional;

import java.util.Properties;

public class GettingStarted {

	public static void main(String[] args) {
		Injector injector = Guice.createInjector(
				JdbcHelper.MySQL,	// initialize()方法里使用 install(JdbcHelper.MySQL);
				new MysqlModule()
		);

		FooService fooService = injector.getInstance(FooService.class);
		User user = fooService.getUser("1");
		System.out.println(user.name);
		user = fooService.getUserById("1");
		System.out.println(user.name);
	}
}

class MysqlModule extends MyBatisModule {

	@Override
	protected void initialize() {
		bindDataSourceProviderType(PooledDataSourceProvider.class);
		bindTransactionFactoryType(JdbcTransactionFactory.class);
		Names.bindProperties(binder(), createTestProperties());

		addMapperClass();
		bindService();
	}

	private void addMapperClass() {
		addMapperClass(UserMapper.class);
	}

	private void bindService() {
		bind(FooService.class).to(FooServiceMapperImpl.class);
	}

	private Properties createTestProperties() {
		Properties myBatisProperties = new Properties();
		myBatisProperties.setProperty("mybatis.environment.id", "test");
		myBatisProperties.setProperty("JDBC.schema", "mybatis-guice");
		myBatisProperties.setProperty("derby.create", "true");
		myBatisProperties.setProperty("JDBC.username", "root");
		myBatisProperties.setProperty("JDBC.password", "root");
		myBatisProperties.setProperty("JDBC.autoCommit", "false");
		return myBatisProperties;
	}
}

class User {
	public String userId;
	public String name;
}

interface UserMapper {
	@Select("SELECT * FROM user WHERE userId = #{userId}")
	User getUser(@Param("userId") String userId);
}

interface FooService {
	public User getUser(String userId);
	public User getUserById(String userId);
}

class FooServiceMapperImpl implements FooService{

	@Inject
	private UserMapper userMapper;

	@Transactional
	public User getUser(String userId) {
		return this.userMapper.getUser(userId);
	}

	public User getUserById(String userId) {
		return this.userMapper.getUser(userId);
	}
}
```
表结构
```sql
CREATE TABLE `user` (
  `userId` varchar(100) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8
```

我们在`MyBatisModule`里创建Mybatis的核心模块,包括
* `org.apache.ibatis.session.SqlSessionFactory`
* `org.apache.ibatis.session.SqlSessionManager`
* 以及添加那些映射关系类

