category: ELK
tag: Elasticsearch
date: 2015-11-18
title: Elasticsearch 搜索
---

首先我们向库里准备一下数据
```java
JSONObject son = new JSONObject();
son.put("name", "Jerry");
son.put("age", "18");
son.put("address", "baoding");
son.put("country", "china");

JSONObject jsonObject = new JSONObject();
jsonObject.put("name", "Tom");
jsonObject.put("age", "48");
jsonObject.put("address", "beijing");
jsonObject.put("country", "china");
jsonObject.put("age", "18");
jsonObject.put("son", son);

IndexRequest indexRequest = new IndexRequest()
		.index("idx04")
		.type("type02")
		.id("02")
		.source(jsonObject.toJSONString());

client.index(indexRequest).get();
```

* Leaf query clauses : 在指定的字段中搜索指定的值,例如使用`match`, `term` 或者 `range` 搜索时
* Compound query clauses : 

首先我们来看一个简单的搜索案例
```java
SearchResponse response = client.prepareSearch("idx01", "idx02")
				.setTypes("type01", "type02")
				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
				.setQuery(QueryBuilders.termQuery("multi", "test"))                 // Query
				.setPostFilter(QueryBuilders.rangeQuery("age").from(12).to(18))     // Filter
				.setFrom(0).setSize(60).setExplain(true)
				.execute()
				.actionGet();
```



### Match All Query
最简单的搜索, 这个搜索将库中所有的文档都搜索出来
```java
QueryBuilder qb = matchAllQuery();
```

### 全文检索

#### Match Query
标准搜索, 包含模糊搜索, 短语搜索, 相近搜索
```java
QueryBuilder qb = matchQuery(
				"age",	// 搜索的字段
				"18"	// 搜索的字段值
		);

		SearchResponse searchResponse = client.prepareSearch("idx04").setQuery(qb).execute().get();
		searchResponse.getHits().forEach(hit -> {
			System.out.println(hit.getSourceAsString());
		});
```
结果为
```
{"address":"beijing","age":"18","country":"china","name":"Tom","son":{"address":"baoding","age":"18","country":"china","name":"Jerry"}}
```


#### Multi Match Query
`match`搜索的多键版本, 会在多个指定字段中进行值匹配查找
```java
QueryBuilder qb = multiMatchQuery(
		"baoding",	// 搜索值
		"city", "country"	// 搜索的字段
);

SearchResponse searchResponse = client.prepareSearch("idx04").setQuery(qb).execute().get();
searchResponse.getHits().forEach(hit -> {
	System.out.println(hit.getSourceAsString());
});
```


#### Common Terms Query
```java
QueryBuilder qb = commonTermsQuery("name", "kimchy"); 
```


#### Query String Query
```java
QueryBuilder qb = queryStringQuery("+kimchy -elasticsearch"); 
```


#### Simple Query String Query
```java
QueryBuilder qb = simpleQueryStringQuery("+kimchy -elasticsearch");
```

### Term level queries

#### Term Query
```java

```


#### Terms Query
```java

```


#### Range Query
```java

```


#### Exists Query.
```java

```

#### Missing Query
```java

```


#### Prefix Query
```java

```


#### Wildcard Query
```java

```


#### Regexp Query
```java

```


#### Fuzzy Query
```java

```


#### Type Query
```java

```


#### Ids Query
```java

```

### Joining queries

#### Nested Query
```java

```


#### Has Child Query
```java

```


#### Has Parent
```java

```

### Compound queries

#### Constant Score Query
```java

```


#### Bool Query
```java

```


#### Dis Max Query
```java

```


#### Function Score Query.
```java

```

####  Boosting Query
```java

```


#### Indices Query
```java

```


#### Not Query
```java

```

### MultiSearch API Query 


### Terminate After