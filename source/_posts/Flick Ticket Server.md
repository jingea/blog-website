title: Flick Ticket Server
---
#### 为什么采用Ticket Server ####
Flickr采用分库分表的方式来拓展数据库. 有时候需要合并不同数据库之间的数据,那么就需要保证全局唯一的key.另外FlickerMysql是基于主-主复制的。 这就意味着我们在分库分表时必须确保唯一性,以避免主键的重复.虽然使用MYSQL的自增长主键是极好的,但是它却不能确保无论是在物理主机还是逻辑主机上的唯一性.


#### 考虑GUID ####

GUID是非常大的,他们在MYSQL索引时性能比较差.我们使用MYSQL查询非常快的一个原因就是,我们对想要查询的东西都会简历索引,那么在查询的时候,我们只需要查询这些索引就好了. 所以索引的大小是一个关键性的选择.另外TickerServer内含了序列性,这对于报告或者debug是很有好处的.


#### 一致性哈希 ####
像Amazon Dynamo等项目提出了在数据存储顶部采用一致性哈希环,来解决GUID/sharding问题.这种解决方案更适合write-cheap(大量写操作)这种场景,然后MYSQL是针对快速随机读 优化的


#### 集中自动增量 ####
如果不能让mysql在多个数据库中实现自动增长的话,那么为什么不仅仅只是更新一个数据库呢？如果我们每次只是在一个数据库中插入一行数据, 那么某人在上传一张照片时 我们可以只使用从那个表中生成的主键ID.

当然如果每秒钟上传60张照片的话,这个表会变得非常大. 那我们可以将这张照片的图像数据去掉, 在中心数据库中只保留ID. 可即便那样, 这个表有可能仍然会变得非常大. 而且还会产生评论,分组,标记等等其他信息, 这些数据都需要ID


#### 替换(重新插入) ####

大概十多年前,mysql对ANSI SQL实现了一个非标准化的拓展-“REPLACE INTO”. 随后“INSERT ON DUPLICATE KEY UPDATE”做为一个新的语法出现了, 它的出现更好的解决了那个初始问题. 但是“REPLACE INTO” 仍然被支持着.

REPLACE 的操作极像 INSERT, 如果新插入的一行和原有行中的PRIMARY KEY或者 UNIQUE index重复的话,那么会先将原来的整行删掉,然后再插入新的一行.


#### 组装 ####

Flicker ticket server 专用于database服务器, 该服务器上有且仅有一个数据库. 在该数据库内有一些表,像表示32位ID的Tickets32, 或者表示64位ID的 Tickets64.


1. 下面展示了一下Ticket64 schema

```sql
	CREATE TABLE ` Tickets64` (
	` id` bigint(20) unsigned NOT NULL auto_increment,
	` stub` char(1) NOT NULL default ' ' ,
	PRIMARY KEY (` id` ) ,
	UNIQUE KEY ` stub` (` stub` )
	) ENGINE=MyISAM
```

2. 当我们执行sql:`SELECT * from Tickets64` 返回下面一个结果

```
	+-------------------+------+
	| id 				|stub  |
	+-------------------+------+
	| 72157623227190423 | a    |
	+-------------------+------+
```

3. 当我需要一个新的64位ID时,我执行面貌这个sql

```
	REPLACE INTO Tickets64 (stub) VALUES (' a' ) ;
	SELECT LAST_INSERT_ID() ;
```

4. SPOF(单点故障) ####

```
	你无法预料到准备好给你的ID会产生单点故障. 故我们同事运行俩台ticket server来达到高可用. 同时在不同的数据库中
	大量的发生写/更新操作也会产生问题, 如果加锁的话就会使服务器白白丧失掉大量的性能.
	我们的解决办法是通过拆分ID空间 在不同的数据库间进行责任拆分, 如下所示：

	TicketServer1:
	auto-increment-increment = 2
	auto-increment-offset = 1

	TicketServer2:
	auto-increment-increment = 2
	auto-increment-offset = 2
```

5. 我们通过在不同的服务器间循环操作来达到负载均衡以及减少运行时间.

#### More Sequences ##
```
 在Ticket server我们不单单只有Tickets32 and Tickets64 这俩张表,我们还有更多的表. 例如针对照片, 账号, 离线任务等等 其他的表.
```
