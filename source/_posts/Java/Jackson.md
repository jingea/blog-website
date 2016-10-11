category: Java
date: 2016-10-11
title: Jackson
---
Jackson 提供了三种对JSON处理的方式
* Data Binding 
* Tree Model
* Streaming API

## Data Binding
这种方式提供了JSON数据和Java对象之间的无缝转换，而且这种方式是相当便利的. 它内部基于 Streaming API 的JSON 读写系统, 尽管Data Binding 是非常高效地,但是相比纯　streaming/incremental 方式，仍然有一些额外的性能消耗．

序列化
```java
    @Test
	public void test_Serialization() throws IOException {
		Obj obj = new Obj();
		obj.platform = "qq";
		StringWriter stringWriter = new StringWriter();

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.writeValue(stringWriter, obj);

		System.out.println(stringWriter);
	}
```
输出为`{"platform":"qq"}`

数据绑定
```java
	@Test
	public void test_ObjectMapperRead () throws IOException {
		String jsonStr = "{\"platform\":\"1\"}";
		ObjectMapper objectMapper = new ObjectMapper();
		Obj obj = objectMapper.readValue(jsonStr, Obj.class);
		System.out.println(obj.platform);
	}
```
输出为`1`

泛型数据绑定
```java
	@Test
	public void test_Serialization() throws IOException {
		Map<Integer, String> map = new HashMap<>();
		map.put(2016, "10/11");
		StringWriter stringWriter = new StringWriter();

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.writeValue(stringWriter, map);

		Map<Integer, String> newMap = objectMapper.readValue(stringWriter.toString(), new TypeReference<Map<Integer, String>>() {});
		System.out.println(newMap.get(2016));
	}
```
输出为`10/11`

数组绑定
```java
@Test
	public void test_Binding() throws IOException {
		String[] array = {"2016"};
		StringWriter stringWriter = new StringWriter();

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.writeValue(stringWriter, array);

		String[] arr = objectMapper.readValue(stringWriter.toString(), String[].class);
		System.out.println(arr[0]);
	}
```

## Tree Model
Tree Model 和XML 处理方式 非常类似.

```java
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

public class TestTreeModel {

	@Test
	public void test_() throws Exception {
		Obj1 obj1 = new Obj1();
		Obj2 obj2 = new Obj2();
		Obj3 obj3 = new Obj3();
		obj1.obj2 = obj2;
		obj2.obj3 = obj3;
		obj3.string = "hello";

		ObjectMapper objectMapper = new ObjectMapper();
		String str = objectMapper.writeValueAsString(obj1);
		JsonNode objtree = objectMapper.readTree(str);
		System.out.println("get obj2 : " + objtree.get("obj2"));
		System.out.println("get obj3 : " + objtree.get("obj3"));
		System.out.println("get string : " + objtree.get("string"));

		System.out.println("path obj2 : " + objtree.path("obj2"));
		System.out.println("path obj3 : " + objtree.path("obj3"));
		System.out.println("path string : " + objtree.path("string"));

		System.out.println("path string obj2 -> obj3 -> string : " + objtree.path("obj2").path("obj3").path("string"));
	}

	private static class Obj1 {
		public Obj2 obj2;
	}

	private static class Obj2 {
		public Obj3 obj3;
	}

	private static class Obj3 {
		public String string;
	}
}
```
输出结果为
```bash
get obj2 : {"obj3":{"string":"hello"}}
get obj3 : null
get string : null
path obj2 : {"obj3":{"string":"hello"}}
path obj3 : 
path string : 
path string obj2 -> obj3 -> string : "hello"
```
当调用`get()`方法时, 如果找不到的话, 会返回`null`, 而`path()`找不到的话则会返回`MissingNode`

还有一个方法`with(int index)`我们没有演示, 这个方法是如果找不到就添加.

`get()`方法还有一个特别有用的地方就是用来处理数组.
```java
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

public class TestTreeModel {

	@Test
	public void test_() throws Exception {
		Obj1 obj1 = new Obj1();

		ObjectMapper objectMapper = new ObjectMapper();
		String str = objectMapper.writeValueAsString(obj1);
		System.out.println(str);

		JsonNode tree = objectMapper.readTree(str);
		System.out.println(tree.get("strings").get(1));
	}

	private static class Obj1 {
		public String[] strings = {"123", "456"};
	}
}
```
结果为
```bash
{"strings":["123","456"]}
"456"
```

## Streaming API
Streaming API 是 Jackson处理 JSON最高效地方式. 但是它的易用性却大大地降低了, 我们不能像Data Binding 或者 Tree Model 那样随机访问元素.

## SerializationFeature
* `WRAP_ROOT_VALUE` :是否环绕根元素，默认false，如果为true，则默认以类名作为根元素，也可以通过@JsonRootName来自定义根元素名称
```java
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Test;

import java.io.IOException;

public class TestDataBinding {

	@Test
	public void test_SerializationFeature () throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
		System.out.println( objectMapper.writeValueAsString(new Obj()));
	}

	public static class Obj {
		public String platform = "example";
	}
}
```
结果为`{"Obj":{"platform":"example"}}` 如果不开启`WRAP_ROOT_VALUE`的话, 结果为`{"platform":"example"}`

* `INDENT_OUTPUT` : 是否缩放排列输出
```java
objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
```
结果为
```bash
{
  "platform" : "example"
}
```

* `FAIL_ON_EMPTY_BEANS` :
```java

```
结果为

* `FAIL_ON_SELF_REFERENCES` :
```java

```
结果为

* `FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS` :
```java

```
结果为

* `WRITE_DATES_AS_TIMESTAMPS` : 序列化日期时以timestamps输出
```java
public class TestDataBinding {

	@Test
	public void test_SerializationFeature () throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		System.out.println( objectMapper.writeValueAsString(new Obj()));
	}

	public static class Obj {
		public Date now = new Date();
	}
}
```
结果为
```bash
{"now":1476179780913}
```

* `WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS` : 序列化char[]时以json数组输出


* `WRITE_NULL_MAP_VALUES` :
```java

```
结果为

* `WRITE_EMPTY_JSON_ARRAYS` :
```java

```
结果为

* `WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED` :
```java

```
结果为

* `WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS` :
```java

```
结果为

* `ORDER_MAP_ENTRIES_BY_KEYS` : 序列化Map时对key进行排序操作

* `EAGER_SERIALIZER_FETCH` :
```java

```
结果为
                                
## DeserializationFeature
* `FAIL_ON_UNKNOWN_PROPERTIES` : 在反序列化时, 如果Java对象中不包含json串的某个数据 属性, 则会报错.
```java
public class TestDataBinding {

	@Test
	public void test_SerializationFeature () throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

		String str = "{\"strings1\":[\"123\"],\"strings2\":[\"456\"]}";
		objectMapper.readValue(str, Obj.class);
	}

	public static class Obj {
		public String[] strings1 = {"123"};
	}
}
```