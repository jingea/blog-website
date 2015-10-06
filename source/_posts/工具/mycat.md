category: 工具
title: Mycat
---
[Mycat权威指南V1](https://item.taobao.com/item.htm?spm=a230r.1.14.8.eRsdoe&id=44263828402&ns=1&abbucket=17#detail)学习总结

# Mycat配置文件

## schema.xml

### schema标签
定义MyCat实例中的逻辑库
* `dataNode` : 对应实际的物理库(如果设置了这个属性,该逻辑库就不能实现分库功能了,但是该逻辑库就可以用作读写分离和主从切换). 它的值对应下面的dataNode标签.
* `checkSQLschema` : 把schema字符去掉(在sql中将库名去掉)
* `sqlMaxLimit` : 在分库中执行sql时,为sql添加`limit`字段
* `name` : 逻辑库名称

#### table标签
定义MyCat中的逻辑表
* `name` : 逻辑表表名
* `dataNode` : 定义逻辑表所属的dataNode, 该属性值与schema标签dataNode属性值相对应
* `rule` : 指定逻辑表要使用定义在rule.xml中的规则名字
* `ruleRequired` : 指定表是否绑定分片规则(如果指定绑定但是在rule.xml中找不到则会报错)
* `primaryKey` : 逻辑表对应真实表的主键
* `type` : 逻辑表类型(可选值为global,不设置的话为非全局表)
* `autoIncrement` : 自增长主键,但是需要在Mysql中设置auto_increment属性
* `needAddLimit` : 如果为false, 会屏蔽sqlMaxLimit功能

##### childTable标签
用于定义E-R分片的子表
* `name` : 子表名
* `joinKey` : 插入子表的时候会使用这个列的值查找父表存储的数据节点
* `parentKey` : 
* `primaryKey` : 逻辑表对应真实表的主键
* `needAddLimit` : 如果为false, 会屏蔽sqlMaxLimit功能

### dataNode标签
定义MyCat中的数据节点(也就是数据分片). 
* `name` : 数据节点名字
* `dataHost` : 定义该分片属于哪个数据库实例
* `database` : 分片属性哪个具体数据库实例上的具体库

### dataHost标签
定义了具体的数据库实例、读写分离配置和心跳语句。
* `name` : dataHost标签名
* `maxCon` : 每个读写实例连接池的最大连接
* `minCon` : 每个读写实例连接池的最小连接，初始化连接池的大小。
* `balance` : 负载均衡类型(`0`:所有读操作都发送到当前可用的writeHost上。'1`:所有读操作都随机的发送到readHost。`2`:所有读操作都随机的在writeHost、readhost上分发。)
* `writeType` : 同balance属性
* `dbType` : 后端连接的数据库类型(mysql,oracle等等)
* `dbDriver` : 连接后端数据库使用的Driver，目前可选的值有native和JDBC

#### heartbeat标签
用于和后端数据库进行心跳检查的语句 

#### writeHost, readHost
用于实例化后端连接池.在一个dataHost内可以定义多个writeHost和readHost。但是，如果writeHost指定的后端数据库宕机，那么这个writeHost绑定的所有readHost都将不可用。另一方面，由于这个writeHost宕机系统会自动的检测到，并切换到备用的writeHost上去。
* `host` : 标识不同实例
* `url` : 后端实例连接地址
* `password` : 
* `user` : 后端存储实例需要的用户名字
* `password` :  后端存储实例需要的密码

### schema.xml配置示例
下面的例子中我创建了一个db1的逻辑库, 该库下有一张idTable逻辑表,该逻辑表对应dn1,dn2这俩个物理库.
```xml
<?xml version="1.0"?>
<!DOCTYPE mycat:schema SYSTEM "schema.dtd">
<mycat:schema xmlns:mycat="http://org.opencloudb/">

	<schema name="idDB" checkSQLschema="false" sqlMaxLimit="100">
		<table name="idTable" primaryKey="ID" dataNode="dn1,dn2" rule="auto-sharding-long" />
	</schema>

	<dataNode name="dn1" dataHost="localhost1" database="db1" />
	<dataNode name="dn2" dataHost="localhost1" database="db2" />
	
	<dataHost name="localhost1" maxCon="1000" minCon="10" balance="0" 
		writeType="0" dbType="mysql" dbDriver="native">
		<heartbeat>select user()</heartbeat>
		<writeHost host="hostM1" url="localhost:3306" user="root" password="root">
		</writeHost>
	</dataHost>
	
</mycat:schema>
```

## server.xml
保存mycat需要的系统配置信息.

### user标签
定义登录mycat的用户和权限,它可以定义`property`子标签.`property`子标签的name值可以是：
* `password` : 该用户的密码
* `schemas` : 该用户可访问的schema
* `readOnly` : 该用户的读写权限

### system标签
与系统配置有关.它同样使用`property`子标签.`property`子标签的name值可以是：

* `defaultSqlParser` : 指定默认的解析器
* `processors` : 指定系统可用的线程数.(主要影响processorBufferPool、processorBufferLocalPercent、processorExecutor属性)
* `processorBufferChunk` : 这个属性指定每次分配Socket Direct Buffer的大小，默认是4096个字节。这个属性也影响buffer pool的长度。
* `processorBufferPool` : 这个属性指定bufferPool计算 比例值.
* `processorBufferLocalPercent` : 控制分配ThreadLocalPool的大小用的，但其也并不是一个准确的值，也是一个比例值。这个属性默认值为100。
* `processorExecutor` : 指定NIOProcessor上共享的businessExecutor固定线程池大小
* `sequnceHandlerType` : 指定使用Mycat全局序列的类型。0为本地文件方式，1为数据库方式。
###### TCP连接相关属性
* `frontSocketSoRcvbuf` : 
* `frontSocketSoSndbuf` : 
* `frontSocketNoDelay` : 
###### Mysql连接相关属性
* `packetHeaderSize` : 指定Mysql协议中的报文头长度
* `maxPacketSize` : 指定Mysql协议可以携带的数据最大长度
* `idleTimeout` :  指定连接的空闲超时时间。某连接在发起空闲检查下，发现距离上次使用超过了空闲时间，那么这个连接会被回收，就是被直接的关闭掉。默认30分钟。
* `charset` : 连接的初始化字符集。默认为utf8。
* `txIsolation` : 前端连接的初始化事务隔离级别，只在初始化的时候使用，后续会根据客户端传递过来的属性对后端数据库连接进行同步。默认为REPEATED_READ。
* `sqlExecuteTimeout` : SQL执行超时的时间，Mycat会检查连接上最后一次执行SQL的时间，若超过这个时间则会直接关闭这连接。默认时间为300秒。
###### 周期间隔相关属性
* `processorCheckPeriod` : 
* `dataNodeIdleCheckPeriod` : 
* `dataNodeHeartbeatPeriod` : 
###### 服务相关属性
* `bindIp` :  mycat服务监听的IP地址，默认值为0.0.0.0。
* `serverPort` : mycat的使用端口，默认值为8066。
* `managerPort` : mycat的管理端口，默认值为9066。

### 示例
```xml
<mycat:server xmlns:mycat="http://org.opencloudb/">
	<system>
		<property name="defaultSqlParser">druidparser</property>
	</system>
	<user name="test">
		<property name="password">test</property>
		<property name="schemas">idDB</property>
	</user>
</mycat:server>
```
 
## rule.xml
对表进行拆分所涉及到的规则定义

### tableRule标签
定义表规则
* `name` : 表规则名称

#### rule标签
指定对物理表中的哪一列进行拆分和使用什么路由算法。

##### columns标签
指定要拆分的列名字。

##### algorithm标签
使用function标签中的name属性。

### function标签
* `name` : 算法的名字
* `class` : 制定路由算法具体的类名字

#### property标签
具体算法需要用到的一些属性
* `name` : 


### 示例
```xml

```
