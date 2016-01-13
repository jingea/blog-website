category: 平台工具
tag: Elasticsearch
date: 2015-11-18
title: Elasticsearch 基本操作
---

使用Elasticsearch自带的客户端,添加maven依赖
```xml
<dependency>
    <groupId>org.elasticsearch</groupId>
    <artifactId>elasticsearch</artifactId>
    <version>2.1.0</version>
</dependency>
```
> 注意,依赖里的版本号要和Elasticsearch服务器的版本号一致


## 节点客户端
节点客户端以一个 无数据节点 的身份加入了一个集群。换句话说，它自身是没有任何数据的，但是他知道什么数据在集群中的哪一个节点上，然后就可以请求转发到正确的节点上并进行连接。
```java
Settings.Builder settings = Settings.settingsBuilder()
		.put("http.enabled", false)
		.put("path.home", "D:\\log\\elasticsearch-1.7.1")
		;

Node node = nodeBuilder()
		.local(true)
		.settings(settings)
		.node();

Client client = node.client();
```

## 传输客户端
更加轻量的传输客户端可以被用来向远程集群发送请求。他并不加入集群本身，而是把请求转发到集群中的节点。
```java
Settings settings = Settings.settingsBuilder().build();

Client client = TransportClient.builder().settings(settings).build()
		.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
```

## 索引数据
我们可以使用客户端提供的`prepareIndex()`方法索引数据,也就是向Elasticsearch添加数据
```
IndexResponse indexResponse = client.prepareIndex("idx01", "type01", "01").setSource(jsonObject.toJSONString()).execute().actionGet();

System.out.println("IndexResponse : " + indexResponse.getId());
System.out.println("IndexResponse : " + indexResponse.getIndex());
System.out.println("IndexResponse : " + indexResponse.getType());
System.out.println("IndexResponse : " + indexResponse.getVersion());
```
结果输出为
```
IndexResponse : 01
IndexResponse : idx01
IndexResponse : type01
IndexResponse : 3
```
idx01为索引值, type01为类型值, 01为id. 当我们重复的使用这三个值向 Elasticsearch 索引数据时是合法的,但是我们会得不到不同的版本号，也就是最好的输出的那个值

## 检索数据
我们可以使用客户端提供的`prepareGet()`方法检索数据
```java
GetResponse getResponse = client.prepareGet("idx01", "type01", "01")
				.setOperationThreaded(false)
				.get();
System.out.println("GetResponse : " + getResponse.getId());
System.out.println("GetResponse : " + getResponse.getIndex());
System.out.println("GetResponse : " + getResponse.getType());
System.out.println("GetResponse : " + getResponse.getVersion());
System.out.println("GetResponse : " + getResponse.getSourceAsString());
```
结果输出为
```
GetResponse : 01
GetResponse : idx01
GetResponse : type01
GetResponse : 4
GetResponse : {"age":"19","name":"Tom"}
```

> `setOperationThreaded()`这个方法是用来设置, 操作是否在其他的线程中执行, 设置为true就是在其他的线程中执行. 注意该操作仍然是异步执行的.

#### 检索多个数据
```java
client.prepareMultiGet()
	  .add("idx01", "type01", "01")
	  .add("idx01", "type01", "02")
	  .get()
	  .forEach(response -> System.out.println(JSON.toJSONString(response, true)));
```

## 删除数据
```java
DeleteResponse deleteResponse = client.prepareDelete("idx01", "type01", "01").get();
System.out.println("DeleteResponse : " + deleteResponse.getId());
System.out.println("DeleteResponse : " + deleteResponse.getIndex());
System.out.println("DeleteResponse : " + deleteResponse.getType());
System.out.println("DeleteResponse : " + deleteResponse.getVersion());
```
结果为
```java
DeleteResponse : 01
DeleteResponse : idx01
DeleteResponse : type01
DeleteResponse : 6
```
如果我们再检索一边数据的话,会得到结果
```java
GetResponse : 01
GetResponse : idx01
GetResponse : type01
GetResponse : -1
GetResponse : null
```
说明那个文档已经被删除了

## 更新数据
我们可以使用`UpdateRequest`或者`prepareUpdate()`方法来对数据进行更新
```java
UpdateRequest updateRequest = new UpdateRequest();
updateRequest.index("idx01");
updateRequest.type("type01");
updateRequest.id("01");
updateRequest.doc(jsonObject.toJSONString());

client.update(updateRequest).get();
```
当我们在通过`get`获取数据的时候,会发现数据已经发生了变化

更新还有一个有用的功能,就是在更新的时候能实现如果有就更新,没有就插入
```java
IndexRequest indexRequest = new IndexRequest()
		.index("idx01")
		.type("type01")
		.id("01")
		.source(jsonObject.toJSONString());

UpdateRequest updateRequest = new UpdateRequest()
		.index("idx01")
		.type("type02")
		.id("01")
		.doc(jsonObject.toJSONString())
		.upsert(indexRequest);

client.update(updateRequest).get();
```

## 批处理
elasticsearch内置的客户端中还有批处理功能,但是批处理只支持增删改, 不支持查询的功能
```java
BulkRequestBuilder bulkRequest = client.prepareBulk();

JSONObject jsonObject = new JSONObject();
jsonObject.put("name", "Tom");
jsonObject.put("age", "18");

IndexRequest indexRequest = new IndexRequest()
		.index("idx02")
		.type("type01")
		.id("01")
		.source(jsonObject.toJSONString());
bulkRequest.add(indexRequest);

UpdateRequest updateRequest = new UpdateRequest()
		.index("idx02")
		.type("type01")
		.id("01")
		.doc(jsonObject.toJSONString());
bulkRequest.add(updateRequest);

DeleteRequest deleteRequest = new DeleteRequest()
		.index("idx02")
		.type("type01")
		.id("01");
bulkRequest.add(deleteRequest);
BulkResponse bulkResponse = bulkRequest.get();
bulkResponse.forEach(response -> System.out.println(response.getId()));

GetResponse getResponse = client.prepareGet("idx02", "type01", "01")
		.setOperationThreaded(false)
		.get();
System.out.println("GetResponse : " + getResponse.getId());
System.out.println("GetResponse : " + getResponse.getIndex());
System.out.println("GetResponse : " + getResponse.getType());
System.out.println("GetResponse : " + getResponse.getVersion());
System.out.println("GetResponse : " + getResponse.getSourceAsString());
```

