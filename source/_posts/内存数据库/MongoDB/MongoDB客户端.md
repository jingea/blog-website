category: 内存数据库
tag: MongoDB
date: 2015-03-08
title: MongoDB客户端
---
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