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

### WRAP_ROOT_VALUE
是否环绕根元素，默认false，如果为true，则默认以类名作为根元素，也可以通过`@JsonRootName`来自定义根元素名称
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

### INDENT_OUTPUT
 是否缩放排列输出
```java
objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
```
结果为
```bash
{
  "platform" : "example"
}
```

### WRITE_DATES_AS_TIMESTAMPS
序列化日期时以timestamps输出
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

### WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS
序列化char[]时以json数组输出

### ORDER_MAP_ENTRIES_BY_KEYS
序列化Map时对key进行排序操作
                  
## DeserializationFeature

### FAIL_ON_UNKNOWN_PROPERTIES
在反序列化时, 如果Java对象中不包含json串的某个数据 属性, 则会报错.
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

## MapperFeature

### ACCEPT_CASE_INSENSITIVE_PROPERTIES
 在反序列化时是否忽略大小写
```java
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

public class TestDataBinding {

	@Test
	public void test_SerializationFeature () throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		Upper upper = new Upper();
		upper.setFirstName("John");
		System.out.println(objectMapper.writeValueAsString(upper));

		Lower lower = new Lower();
		lower.setLastName("li");
		System.out.println(objectMapper.writeValueAsString(lower));

	}

	@Test
	public void test_DeserializationFeature () throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);

		Upper obj1 = objectMapper.readValue("{\"firstName\":\"John\"}", Upper.class);
		System.out.println(obj1.getFirstName());

		Upper obj2 = objectMapper.readValue("{\"FirstName\":\"John\"}", Upper.class);
		System.out.println(obj2.getFirstName());
	}
}

class Upper {
	private String FirstName;
	public String getFirstName() {return FirstName;}
	public void setFirstName(String firstName) {FirstName = firstName;}
}

class Lower {
	private String lastName;
	public String getLastName() {return lastName;}
	public void setLastName(String lastName) {this.lastName = lastName;}
}
```
在`test_SerializationFeature()`这个测试中, 我们可以通过结果看到, Upper的`FirstName`在JSON串 中成了`firstName`. 当反序列化得时候, 如果不指定`ACCEPT_CASE_INSENSITIVE_PROPERTIES`, 那么当JSON串中的`FirstName`为大写的时候, 是没办法序列化出来的.

## 注解

### JsonProperty 
jackson 默认是根据Getter来进行注值反序列化的, 但是有时候为了节省存储空间, 字段名远小于Getter名称, 这样就造成了字段名和方法名不一致, 此时就可以利用JsonProperty注解重命名来解决这个问题
```java
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

public class TestDataBinding {

	@Test
	public void test_Deferent () throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);

		Different different = new Different();
		different.setMiddle("Nice");
		System.out.println(objectMapper.writeValueAsString(different));

		Different obj2 = objectMapper.readValue("{\"middle\":\"Nice\"}", Different.class);
		System.out.println(obj2.getMiddle());

		Different obj3 = objectMapper.readValue("{\"Mid\":\"Nice\"}", Different.class);
		System.out.println(obj3.getMiddle());

		Different obj4 = objectMapper.readValue("{\"mid\":\"Nice\"}", Different.class);
		System.out.println(obj4.getMiddle());
	}
}

class Different {
	@JsonProperty("mid")
	private String Mid;
	public String getMiddle() {return Mid;}
	public void setMiddle(String middle) {this.Mid = middle;}
}
```

### JsonInclude 
指定在序列化时, 可以输出哪些值. 例如只输出非默认值(包含类型默认值和初始化默认值)
```java
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

public class TestDefault {
	@Test
	public void test_() throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		Foo foo = new Foo();
		foo.string1 = "123";
		System.out.println(objectMapper.writeValueAsString(foo));
	}
}

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
class Foo {
	public String string1;
	public String string2 = "xxx";
	public String string3;
}
```

### JsonSerialize
实现浮点数只保留俩位
```java
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.junit.Test;

import java.io.IOException;
import java.text.DecimalFormat;

public class TestDouble {

	@Test
	public void test_() throws JsonProcessingException {
		DoubleObject doubleObject = new DoubleObject();
		doubleObject.aDouble = 1.101010101010101;
		doubleObject.aFloat = 1.101010101010101f;

		ObjectMapper objectMapper = new ObjectMapper();
		String string = objectMapper.writeValueAsString(doubleObject);
		System.out.println(string);
	}

	public static class CustomDoubleSerializer extends JsonSerializer<Number> {
		@Override
		public void serialize(Number value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
			if (null == value) {
				jgen.writeNull();
			} else if (value instanceof Double || value instanceof Float){
				final String pattern = ".##";
				final DecimalFormat myFormatter = new DecimalFormat(pattern);
				final String output = myFormatter.format(value);
				jgen.writeNumber(output);
			}
		}
	}

	public static class DoubleObject {
		@JsonSerialize(using = CustomDoubleSerializer.class)
		private double aDouble;
		@JsonSerialize(using = CustomDoubleSerializer.class)
		private float aFloat;
		public double getaDouble() {return aDouble;}
		public void setaDouble(double aDouble) {this.aDouble = aDouble;}
		public float getaFloat() {return aFloat;}
		public void setaFloat(float aFloat) {this.aFloat = aFloat;}
	}
}
```