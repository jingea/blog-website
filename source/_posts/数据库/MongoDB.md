category: 数据库
date: 2015-03-08
title: MongoDB
---
# Run MongoDB

## Run MongoDB On Windows
如果在没有进行auth设置且在Secure Mode运行, 那么就不要使 mongod.exe在公共网络上可见.

### 设置MOngoDB环境

#### 设置环境变量
在环境变量里添加环境变量 `D:\Program Files\MongoDB\Server\3.0\` 然后在Path里添加： `%MONGODB_HOME%\bin`

#### data directory
```
MongoDB 需要一个data directory来存储全部的数据. MongoDB默认的data directory路径是\data\db, 
所以我们需要创建一个data directory. 假设我们在D盘创建了一个这样的目录: D:\mongodb\data\db.

你可以通过--dbpath选项给mongod.exe设置另一个data directory.
mongod.exe --dbpath D:\mongodb\data\db

如果你的data directory包含空格的话,那么就需要使用""将他们包含起来：
mongod.exe --dbpath "d:\test\mongo db data"
```

## 启动MongoDB

### 使用mongod.exe命令启动mongoDB
```
	mongod.exe
```

### 启动日志
最后我们在启动日志里看到
```
waiting for connections on port 27017
```

### 命令行方式启动

MongoDB 默认存储数据目录为/data/db/ (或者 c:/data/db), 默认端口 27017,默认 HTTP 端口 28017.
```
mongod --dbpath=/data/db
```

### 配置文件方式启动
MongoDB 也支持同 mysql 一样的读取启动配置文件的方式来启动数据库,配置文件的内容如下:
```
cat /etc/mongodb.cnf
```
启动时加上”-f”参数,并指向配置文件即可:
```
mongod -f /etc/mongodb.cnf
```

#### Daemon 方式启动
MongoDB 提供了一种后台 Daemon 方式启动的选择,只需加上一个” --fork”参数即可,,但如果用到了 ” --fork”参数就必须也启用 ”--logpath”参数,这是强制的
```
mongod --dbpath=/data/db --logpath=/data/log/r3.log --fork
```

#### mongod 参数说明
mongod 的参数分为一般参数, windows 参数, replication 参数, replica set 参数,以及隐含参数.上面列举的都是一般参数

mongod 的参数中,没有设置内存大小相关的参数,是的, MongoDB 使用 os mmap 机制来缓存数据文件数据,自身目前不提供缓存机制.这样好处是代码简单,
mmap 在数据量不超过内存时效率很高.但是数据量超过系统可用内存后,则写入的性能可能不太稳定,容易出现大起大落,不过在最新的 1.8 版本中,这个情况相对以前的版本已经
有了一定程度的改善.

##### mongod 的主要参数有：
* dbpath —— 数据文件存放路径,每个数据库会在其中创建一个子目录,用于防止同一个实例多次运行的 mongod.lock 也保存在此目录中.
* logpath —— 错误日志文件
* logappend —— 错误日志采用追加模式（默认是覆写模式）
* bind_ip —— 对外服务的绑定 ip,一般设置为空,及绑定在本机所有可用 ip 上,如有需要可以单独指定
* port —— 对外服务端口 . Web 管理端口在这个 port 的基础上+1000
* fork —— 以后台 Daemon 形式运行服务
* journal —— 开启日志功能,通过保存操作日志来降低单机故障的恢复时间,在 1.8 版本后正式加入,取代在 1.7.5 版本中的 dur 参数.
* syncdelay —— 系统同步刷新磁盘的时间,单位为秒,默认是 60 秒.
* directoryperdb —— 每个 db 存放在单独的目录中,建议设置该参数.与 MySQL 的独立表空间类似
* maxConns —— 最大连接数
* repairpath —— 执行 repair 时的临时目录.在如果没有开启 journal,异常 down 机后重启 ,必须执行 repair操作.

## 停止数据库

* Control-C
* shutdownServer()指令
```
mongo --port 28013
use admin
db.shutdownServer()
```

## 常用工具集
MongoDB 在 bin 目录下提供了一系列有用的工具,这些工具提供了 MongoDB 在运维管理上的方便。
* bsondump: 将 bson 格式的文件转储为 json 格式的数据
* mongo: 客户端命令行工具,其实也是一个 js 解释器,支持 js 语法
* mongod: 数据库服务端,每个实例启动一个进程,可以 fork 为后台运行
* mongodump/ mongorestore: 数据库备份和恢复工具
* mongoexport/ mongoimport: 数据导出和导入工具
* mongofiles: GridFS 管理工具,可实现二制文件的存取
* mongos: 分片路由,如果使用了 sharding 功能,则应用程序连接的是 mongos 而不是mongod
* mongosniff: 这一工具的作用类似于 tcpdump,不同的是他只监控 MongoDB 相关的包请求,并且是以指定的可读性的形式输出
* mongostat: 实时性能监控工具

## 部署 Replica Sets
* 创建数据文件存储路径
```
mkdir E:/mongoData/data/r0
mkdir E:/mongoData/data/r1
mkdir E:/mongoData/data/r2
```
* 创建日志文件路径
```
mkdir E:/mongoData/log
```
* 创建主从 key 文件，用于标识集群的私钥的完整路径，如果各个实例的 key file 内容不一致，程序将不能正常用。
```
mkdir E:/mongoData/key
echo "this is rs1 super secret key" > E:/mongoData/key/r0
echo "this is rs1 super secret key" > E:/mongoData/key/r1
echo "this is rs1 super secret key" > E:/mongoData/key/r2
```
* 启动 3 个实例
```
mongod --replSet rs1 --keyFile E:/mongoData/key/r0 -fork --port 28010 --dbpath E:/mongoData/data/r0 --logpath=E:/mongoData/log/r0.log --logappend
mongod --replSet rs1 --keyFile E:/mongoData/key/r1 -fork --port 28011 --dbpath E:/mongoData/data/r1 --logpath=E:/mongoData/log/r1.log --logappend
mongod --replSet rs1 --keyFile E:/mongoData/key/r2 -fork --port 28012 --dbpath E:/mongoData/data/r2 --logpath=E:/mongoData/log/r2.log --logappend
```
* 配置及初始化 Replica Sets
```
mongo -port 28010
```


# Introduction

## A Quick Tour

使用java 驱动开发是非常简单的,首先你要确保你的`classpath`中包含`mongo.jar`

### Making a Connection

为了能够连接上MongoDB,最低的要求也是你要知道连接的database的名称. 这个数据库可以不存在,如果不存在的话,MongoDB会自动创建这个数据库

另外,你可以指定连接的服务器的地址和端口,下面的例子展示了三种连接本地`mydb`数据库的方式
```java
import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.BulkWriteResult;
import com.mongodb.Cursor;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.ParallelScanOptions;
import com.mongodb.ServerAddress;

import java.util.List;
import java.util.Set;

import static java.util.concurrent.TimeUnit.SECONDS;

// To directly connect to a single MongoDB server (note that this will not auto-discover the primary even
// if it's a member of a replica set:
MongoClient mongoClient = new MongoClient();
// or
MongoClient mongoClient = new MongoClient( "localhost" );
// or
MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
// or, to connect to a replica set, with auto-discovery of the primary, supply a seed list of members
MongoClient mongoClient = new MongoClient(Arrays.asList(new ServerAddress("localhost", 27017),
                                      new ServerAddress("localhost", 27018),
                                      new ServerAddress("localhost", 27019)));

DB db = mongoClient.getDB( "mydb" );
```

在这个例子中`db`对象保持着一个对MongoDB服务器指定数据库的一个连接. 通过这个对象你可以做很多其他操作

> Note:
>
> `MongoClient`实例实际上维持着对这个数据库的一个连接池. 即使在多线程的情况下,你也只需要一个`MongoClient`实例, 参考[concurrency doc page]()


`MongoClient`被设计成一个线程安全且线程共享的类. 一个典型例子是,你对一个数据库集群仅仅创建了一个`MongoClient`实例,然后在你的整个应用程序中都使用这一个实例. 如果出于一些特殊原因你不得不创建多个`MongoClient`实例,那么你需要注意下面俩点：

* all resource usage limits (max connections, etc) apply per MongoClient instance
* 当关闭一个实例时,你必须确保你调用了`MongoClient.close()`清理掉了全部的资源

New in version 2.10.0: The MongoClient class is new in version 2.10.0. For releases prior to that, please use the Mongo class instead.

### Authentication (Optional)

MongoDB可以在安全模式下运行, 这种模式下,需要通过验证才能访问数据库. 当在这种模式下运行的时候, 任何客户端都必须提供一组证书.在java Driver中,你只需要在创建`MongoClient`实例时提供一下证书.
```java
MongoCredential credential = MongoCredential.createMongoCRCredential(userName, database, password);
MongoClient mongoClient = new MongoClient(new ServerAddress(), Arrays.asList(credential));
```

MongoDB支持不同的认证机制,具体参考[the access control tutorials]()

### Getting a Collection

如果想要使用一个collection,那么你仅仅需要调用`getCollection(String collectionName)`方法,然后指定该collection名称就好

```java
DBCollection coll = db.getCollection("testCollection");
```

一旦你有了collection对象,那你就可以执行例如插入数据,查询数据等等的操作了

### Setting Write Concern

在2.10.0这个版本里,默认的write concern是`WriteConcern.ACKNOWLEDGED`不过你可以通过下面的方法轻松改变它
```java
mongoClient.setWriteConcern(WriteConcern.JOURNALED);
```

对应write concern提供了很多种选项. 另外,这个默认的write concern分别可以在数据库,collection,以及单独的更新操作上重载.


### Inserting a Document

一旦你拥有了collection对象,你就可以向该collection中插入document. 例如,我们可以插入一个像下面这样的一个json文档
```json
{
   "name" : "MongoDB",
   "type" : "database",
   "count" : 1,
   "info" : {
               x : 203,
               y : 102
             }
}
```

注意,上面的例子中我们有一个内嵌的文档.想要插入这样一个文档,我们可以使用`BasicDBObject`类来实现：
```java
BasicDBObject doc = new BasicDBObject("name", "MongoDB")
        .append("type", "database")
        .append("count", 1)
        .append("info", new BasicDBObject("x", 203).append("y", 102));
coll.insert(doc);
```


### findOne()

如果想要查看刚才插入的文档,我们可以简单地调用`findOne()`,这个操作会获得该collection中的第一个文档.这个方法只是返回一个文档对象(而`find()`会返回一个`DBCursor`对象),当collection中只有一个文档的时候,这是非常有用的.
```java
DBObject myDoc = coll.findOne();
System.out.println(myDoc);
```
结果如下：
```json
{ "_id" : "49902cde5162504500b45c2c" ,
  "name" : "MongoDB" ,
  "type" : "database" ,
  "count" : 1 ,
  "info" : { "x" : 203 , "y" : 102}}

```

>Note:
>
> `_id`元素是MongoDB自动添加到你的文档中的. 记住,MongoDB内部以“_”/”$”开头储存元素名称

### Adding Multiple Documents

当测试一些其他查询的时候,我们需要大量的数据,让我们添加一些简单的文档到collection中.
```json
{
   "i" : value
}
```

我们可以在一个循环中不断地插入数据
```java
for (int i=0; i < 100; i++) {
    coll.insert(new BasicDBObject("i", i));
}
```

注意：我们可以向同一个collection中插入包含不同元素的文档.所以MongoDB也被称为`schema-free`

### Counting Documents in A Collection

通过以上的操作我们已经插入了101个文档,我们通过`getCount()`方法来检查一下.
```java
System.out.println(coll.getCount());
```

### Using a Cursor to Get All the Documents

如果想要获得collection中的全部文档,我们可以使用`find()`方法. `find()`返回一个`DBCursor`对象,我们可以通过遍历该对象获取所有匹配我们需求的文档.
```java
DBCursor cursor = coll.find();
try {
   while(cursor.hasNext()) {
       System.out.println(cursor.next());
   }
} finally {
   cursor.close();
}
```

### Getting A Single Document with A Query

我们可以向`find()`方法传递一个查询参数, 通过该参数找到集合中符合需求的文档子集. 下例中展示了我们想要找到i是7的所有文档.
```java
BasicDBObject query = new BasicDBObject("i", 71);

cursor = coll.find(query);

try {
   while(cursor.hasNext()) {
       System.out.println(cursor.next());
   }
} finally {
   cursor.close();
}
```

该代码只会输出一个文档
```json
{ "_id" : "49903677516250c1008d624e" , "i" : 71 }
```

你也可以从其他的实例和文档中查看`$`操作符的用法：
```java
db.things.find({j: {$ne: 3}, k: {$gt: 10} });
```

使用内嵌的`DBObject`,`$`可以看作是正则表达式字串
``` java
query = new BasicDBObject("j", new BasicDBObject("$ne", 3))
        .append("k", new BasicDBObject("$gt", 10));

cursor = coll.find(query);

try {
    while(cursor.hasNext()) {
        System.out.println(cursor.next());
    }
} finally {
    cursor.close();
}
```


### Getting A Set of Documents With a Query

我们可以使用查询来获得collection中的一个文档集合.例如,我们使用下面的语法来获取所有i > 50的文档
```java
// find all where i > 50
query = new BasicDBObject("i", new BasicDBObject("$gt", 50));

cursor = coll.find(query);
try {
    while (cursor.hasNext()) {
        System.out.println(cursor.next());
    }
} finally {
    cursor.close();
}
```

我们还可以获得一个区间(20 < i <= 30)文档集合
```java
query = new BasicDBObject("i", new BasicDBObject("$gt", 20).append("$lte", 30));
cursor = coll.find(query);

try {
    while (cursor.hasNext()) {
        System.out.println(cursor.next());
    }
} finally {
    cursor.close();
}
```


### MaxTime

MongoDB2.6 添加查询超时的能力

```java
coll.find().maxTime(1, SECONDS).count();
```

在上面的例子中将`maxTime`设置为1s,当时间到后查询将被打断

### Bulk operations

Under the covers MongoDB is moving away from the combination of a write operation followed by get last error (GLE) and towards a write commands API. These new commands allow for the execution of bulk insert/update/remove operations. There are two types of bulk operations:

1. Ordered bulk operations. 按顺序执行全部的操作,当遇到第一个写失败的时候,退出
2. Unordered bulk operations. 并行执行全部操作, 同时收集全部错误.该操作不保证按照顺序执行

下面展示了上面所说的俩个示例
```java
// 1. Ordered bulk operation
BulkWriteOperation builder = coll.initializeOrderedBulkOperation();
builder.insert(new BasicDBObject("_id", 1));
builder.insert(new BasicDBObject("_id", 2));
builder.insert(new BasicDBObject("_id", 3));

builder.find(new BasicDBObject("_id", 1)).updateOne(new BasicDBObject("$set", new BasicDBObject("x", 2)));
builder.find(new BasicDBObject("_id", 2)).removeOne();
builder.find(new BasicDBObject("_id", 3)).replaceOne(new BasicDBObject("_id", 3).append("x", 4));

BulkWriteResult result = builder.execute();

// 2. Unordered bulk operation - no guarantee of order of operation
builder = coll.initializeUnorderedBulkOperation();
builder.find(new BasicDBObject("_id", 1)).removeOne();
builder.find(new BasicDBObject("_id", 2)).removeOne();

result = builder.execute();
```


> Note:
> 
For servers older than 2.6 the API will down convert the operations. To support the correct semantics for BulkWriteResult and BulkWriteException, the operations have to be done one at a time. It’s not possible to down convert 100% so there might be slight edge cases where it cannot correctly report the right numbers.


### parallelScan

MongoDB 2.6 增加了`parallelCollectionScan`命令, 该命令通过使用多个游标读取整个collection.
```java
ParallelScanOptions parallelScanOptions = ParallelScanOptions
        .builder()
        .numCursors(3)
        .batchSize(300)
        .build();

List<Cursor> cursors = coll.parallelScan(parallelScanOptions);
for (Cursor pCursor: cursors) {
    while (pCursor.hasNext()) {
        System.out.println((pCursor.next()));
    }
}
```

其对collection进行IO吞吐量的优化.

> Note:
>
> `ParallelScan`不能通过`mongos`运行

## Quick Tour of the Administrative Functions

### Getting A List of Databases

通过下面的代码你可以获取一个可用数据库列表
```java
MongoClient mongoClient = new MongoClient();

for (String s : mongoClient.getDatabaseNames()) {
   System.out.println(s);
}
```

调用`mongoClient.getDB()`并不会创建一个数据库. 仅仅当尝试向数据库写入数据时,该数据库才会被创建. 例如尝试创建一个所以或者一个collection或者插入一个文档.

### Dropping A Database

通过`MongoClient`实例你也可以`drop`掉一个数据库
```java
MongoClient mongoClient = new MongoClient();
mongoClient.dropDatabase("databaseToBeDropped");
```

### Creating A Collection

有俩种方式创建collection：
1. 如果向一个不存在的collection中尝试插入一个文档,那么该collection会被创建出来
2. 或者直接调用`createCollection`命令

下面的例子展示了创建1M大小的collection
```java
db = mongoClient.getDB("mydb");
db.createCollection("testCollection", new BasicDBObject("capped", true)
        .append("size", 1048576));
```

### Getting A List of Collections

你可以通过下面的方式获得一个数据库当中可用collection列表
```java
for (String s : db.getCollectionNames()) {
   System.out.println(s);
}
```

上面的例子会输出：
```
system.indexes
testCollection
```

>Note:
>
> `system.indexes` collection是自动创建的, 它里面是数据库中所有的索引, 所以不应该直接访问它

### Dropping A Collection

你可以通过`drop()`方法直接drop掉一个collection
```java
DBCollection coll = db.getCollection("testCollection");
coll.drop();
System.out.println(db.getCollectionNames());
```

### Getting a List of Indexes on a Collection

下例展示了如何获得一个collection中索引的列表
```java
List<DBObject> list = coll.getIndexInfo();

for (DBObject o : list) {
   System.out.println(o.get("key"));
}
```

上面的实例会进行下面的输出：
```json
{ "v" : 1 , "key" : { "_id" : 1} , "name" : "_id_" , "ns" : "mydb.testCollection"}
{ "v" : 1 , "key" : { "i" : 1} , "name" : "i_1" , "ns" : "mydb.testCollection"}
{ "v" : 1 , "key" : { "loc" : "2dsphere"} , "name" : "loc_2dsphere" , ... }
{ "v" : 1 , "key" : { "_fts" : "text" , "_ftsx" : 1} , "name" : "content_text" , ... }
```


### Creating An Index

MongoDB支持索引,而且它们可以轻松地插入到一个集合中.创建索引的过程非常简单,你只需要指定被索引的字段,你还可以指定该索引是上升的(1)还是下降的(-1).
```java
coll.createIndex(new BasicDBObject("i", 1));  // create index on "i", ascending
```


### Geo indexes

MongoDB支持不同的地理空间索引,在下面的例子中,我们将窗口一个`2dsphere`索引, 我们可以通过标准`GeoJson`标记进行查询. 想要创建一个`2dsphere`索引,我们需要在索引文档中指定`2dsphere`这个字面量.
```java
coll.createIndex(new BasicDBObject("loc", "2dsphere"));
```

有不同的方式去查询`2dsphere`索引,下面的例子中找到了500m以内的位置.
```java
BasicDBList coordinates = new BasicDBList();
coordinates.put(0, -73.97);
coordinates.put(1, 40.77);
coll.insert(new BasicDBObject("name", "Central Park")
                .append("loc", new BasicDBObject("type", "Point").append("coordinates", coordinates))
                .append("category", "Parks"));

coordinates.put(0, -73.88);
coordinates.put(1, 40.78);
coll.insert(new BasicDBObject("name", "La Guardia Airport")
        .append("loc", new BasicDBObject("type", "Point").append("coordinates", coordinates))
        .append("category", "Airport"));


// Find whats within 500m of my location
BasicDBList myLocation = new BasicDBList();
myLocation.put(0, -73.965);
myLocation.put(1, 40.769);
myDoc = coll.findOne(
            new BasicDBObject("loc",
                new BasicDBObject("$near",
                        new BasicDBObject("$geometry",
                                new BasicDBObject("type", "Point")
                                    .append("coordinates", myLocation))
                             .append("$maxDistance",  500)
                        )
                )
            );
System.out.println(myDoc.get("name"));
```

更多参考[geospatial]()文档

### Text indexes

MongoDB还支持`text`索引,该索引用来支持从String中搜索文本. `text`索引可以包含任何字段,但是该字段的值必须是String或者String数组.想要创建一个`text`索引,只需要在索引文档中指定`text`字面量.
```java
// create a text index on the "content" field
coll.createIndex(new BasicDBObject("content", "text"));
```

MongoDB2.6 以后`text`索引融进了主要的查询语言中,并且成为了一种默认的方式.
```java
// Insert some documents
coll.insert(new BasicDBObject("_id", 0).append("content", "textual content"));
coll.insert(new BasicDBObject("_id", 1).append("content", "additional content"));
coll.insert(new BasicDBObject("_id", 2).append("content", "irrelevant content"));

// Find using the text index
BasicDBObject search = new BasicDBObject("$search", "textual content -irrelevant");
BasicDBObject textSearch = new BasicDBObject("$text", search);
int matchCount = coll.find(textSearch).count();
System.out.println("Text search matches: "+ matchCount);

// Find using the $language operator
textSearch = new BasicDBObject("$text", search.append("$language", "english"));
matchCount = coll.find(textSearch).count();
System.out.println("Text search matches (english): "+ matchCount);

// Find the highest scoring match
BasicDBObject projection = new BasicDBObject("score", new BasicDBObject("$meta", "textScore"));
myDoc = coll.findOne(textSearch, projection);
System.out.println("Highest scoring document: "+ myDoc);
```

上面的代码应该输出：
```java
Text search matches: 2
Text search matches (english): 2
Highest scoring document: { "_id" : 1 , "content" : "additional content" , "score" : 0.75}
```

更多关于text search,参考[text index and $text query operator]()

# Replica
# Deploy a Replica Set

这篇教程讲述的是如何基于正在运行的不进行控制访问的`mongod`创建三个`replica set`.

如果想要创建带有控制访问功能的`replica set`,参考[Deploy Replica Set and Configure Authentication and Authorization](http://docs.mongodb.org/manual/tutorial/deploy-replica-set-with-auth/). 如果你想要在一个单独的MongoDB上部署`replica set`, 可以参考[Convert a Standalone to a Replica Set](http://docs.mongodb.org/manual/tutorial/convert-standalone-to-replica-set/). 关于更多的`replica set`部署信息,参考[Replication](http://docs.mongodb.org/manual/replication/)和[Replica Set Deployment Architectures](http://docs.mongodb.org/manual/core/replica-set-architectures/)

## Overview

带有三个成员的`replica sets`就足够应付网络切分和其他类型的系统失败. 那些sets有足够的能力来应付分布式类型的读操作. `Replica sets`应该保证它的成员数量维持在一个奇数上. 这条规则能够保证正常的[elections](http://docs.mongodb.org/manual/core/replica-set-elections/). 更多关于对`replica sets`的设计,参考[Replication overview](http://docs.mongodb.org/manual/core/replication-introduction/)

基本的步骤是: 首先启动要成为`replica set`成员的`mongod`, 然后配置`replica set`, 最后将`mongod`添加到`replica set`上.

## Requirements

在生产部署阶段, 你应该尽量在不同的主机上部署代理`mongod`的成员. 当使用虚拟主机进行生产部署时, 你应该在不同的主机服务器上都部署一个'mongod'.

在你创建`replica set`之前, 你必须先检查你的网络配置能够允许每一个成员都能够相互连接上. 一个成功的`replica set`部署, 每一个成员都能够连接得上其他成员. 关于如何检查连接,参考[Test Connections Between all Members](http://docs.mongodb.org/manual/tutorial/troubleshoot-replica-sets/#replica-set-troubleshooting-check-connection)

## Considerations When Deploying a Replica Set

### Architecture

在生产阶段, 将`replica set`和它的成员部署到同一台机器上. 如果可能的话, 绑定到MongoDB标准端口27017上. 使用`bind_ip`选项确保MongoDB会根据配置好的地址监听来自应用程序的连接.

如果`replica set`在不同的机房内部署, 那么应该确保大多数的`mongod`实例部署在第一站点上.参考[Replica Set Deployment Architectures]()

### Connectivity

确保网络中所有的`replica set`成员和客户端的流量能够安全和高效地传输:

* 创建一个虚拟的私有网络. 确保该网络上一个单独站点可以路由不同成员间 间所有的流量.
* 配置访问控制能够阻止未知的客户端连接到 `replica set`上
* 配置网络和防火墙规则以便进站和出站的网络包仅仅是在MongoDB的默认端口和你的配置上.

最终确保`replica set`中每个成员都可以通过可解析的`DNS`或者`hostname`访问到. 你应该恰当地设置上`DNS`名称或者通过`/etc/hosts`文件来映射这个配置

### Configuration
Specify the run time configuration on each system in a configuration file stored in /etc/mongodb.conf or a related location. Create the directory where MongoDB stores data files before deploying MongoDB.

For more information about the run time options used above and other configuration options, see Configuration File Options.

## Procedure

下面的步骤概括了在`access control`失效的情况下如何部署replica set

### Start each member of the replica set with the appropriate options.

启动`mongod`然后通过`replSet`选项设定`replica set`名字, 向`replica set`中添加一个成员. 如果想要配置其他特有参数,参考[Replication Options]()

如果你的应用程序连接了多个`replica set`, 每一个`replica set`都应该有一个独立的名字. 某些驱动会根据`replica set`名称将`replica set`连接进行分组.

下面是一个示例：
```
mongod --replSet "rs0"
```

你也通过配置文件设置`replica set`名字. 如果想要通过配置文件启动`mongod`, 那么你需要`--config`选项指定配置文件
```
mongod --config $HOME/.mongodb/config
```
在生产部署阶段, 你可以通过配置一个控制脚本来管理这个进程. 但是控制脚本的使用超过了该教程的介绍范围.

> 注意:
>
> 如果你的c盘没有创建C:/data/db, 那么会抛出 ：Hotfix KB2731284 or later update is not installed. 以及 C:\data\db not found 的字样. 
>
> 那么你就需要在命令上加上 --dbpath 选项了

### Connect a mongo shell to a replica set member.

下例展示了如何连接到在`localhost:27017`上运行的`mongod`:
```
mongo
```

### Initiate the replica set.

接着这`mongo`shell里使用`rs.initiate()`设置成员.
```
rs.initiate()
```
MongoDB使用`replica set`默认配置启动了一个包含当前成员的`replica set`

> 注意:
>
> 这个过程大概需要几分钟的时间, 所以需要耐心的稍等一下.

### Verify the initial replica set configuration.

在`mongo`shell中使用`rs.conf()`输出`replica set`配置:
```
rs.conf()
```

输出的`replica set`配置类似于下面的结构
```json
{
   "_id" : "rs0",
   "version" : 1,
   "members" : [
      {
         "_id" : 1,
         "host" : "mongodb0.example.net:27017"
      }
   ]
}
```

### Add the remaining members to the replica set.

在`mongo`shell中使用`rs.add()`方法添加俩个成员:
```
rs.add("mongodb1.example.net")
rs.add("mongodb2.example.net")
```

完成这一步之后,你就获得了一个拥有完整功能的`replica set`. 新的`replica set`会选出一个主要的来.

### Check the status of the replica set.

在`mongo`shell中使用`rs.status()`方法查看`replica set`状态.
```
rs.status()
```

## Replication Introduction

`Replication` 是用于多台服务器间数据同步的一个进程.
