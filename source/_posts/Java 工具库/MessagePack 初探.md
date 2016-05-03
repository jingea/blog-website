category: Java工具
date: 2016-02-03
title: MessagePack 初探
---
MessagePack 是一个高效的二进制数据序列化工具. 使用它可以让你像使用JSON那样在多种语言中进行数据交换, 但是它比JSON更加小巧,迅捷.

在Java中使用的话, 我们首先添加maven依赖
```xml
 <dependency>
    <groupId>org.msgpack</groupId>
    <artifactId>msgpack</artifactId>
    <version>0.6.12</version>
</dependency>
```
首先看一个简单的示例
```java
import org.msgpack.MessagePack;
import org.msgpack.annotation.Message;

public class Main1 {
    @Message // Annotation
    public static class MyMessage {
        // public fields are serialized.
        public String name;
        public double version;
    }

    public static void main(String[] args) throws Exception {
        MyMessage src = new MyMessage();
        src.name = "msgpack";
        src.version = 0.6;

        MessagePack msgpack = new MessagePack();
        // Serialize
        byte[] bytes = msgpack.write(src);
        // Deserialize
        MyMessage dst = msgpack.read(bytes, MyMessage.class);
    }
}
```
在上面的例子中我们看到将一个`MyMessage`序列化成byte数组, 同时又将其反序列化出来了. 但是如果我们要同时序列化多个对象呢？那么我们就可以使用`Packer`和`Unpacker`.
```java
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.msgpack.MessagePack;
import org.msgpack.annotation.Message;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;

public class Main2 {
    @Message
    public static class MyMessage {
        public String name;
        public double version;
    }

    public static void main(String[] args) throws Exception {
        MyMessage src1 = new MyMessage();
        src1.name = "msgpack";
        src1.version = 0.6;
        MyMessage src2 = new MyMessage();
        src2.name = "muga";
        src2.version = 10.0;
        MyMessage src3 = new MyMessage();
        src3.name = "frsyukik";
        src3.version = 1.0;

        MessagePack msgpack = new MessagePack();
        // Serialize
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Packer packer = msgpack.createPacker(out);
        packer.write(src1);
        packer.write(src2);
        packer.write(src3);
        byte[] bytes = out.toByteArray();

        // Deserialize
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        Unpacker unpacker = msgpack.createUnpacker(in);
        MyMessage dst1 = unpacker.read(MyMessage.class);
        MyMessage dst2 = unpacker.read(MyMessage.class);
        MyMessage dst3 = unpacker.read(MyMessage.class);
    }
}
```
其实`Packer`和`Unpacker`内置了非常多了序列化类型
```java
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;

import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;

public class Main3 {
    public static void main(String[] args) throws Exception {
        MessagePack msgpack = new MessagePack();

        // Serialization
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Packer packer = msgpack.createPacker(out);

        // 对原生类型进行序列化
        packer.write(true); // boolean value
        packer.write(10); // int value
        packer.write(10.5); // double value

        // 对原生包装类型进行序列化
        packer.write(Boolean.TRUE);
        packer.write(new Integer(10));
        packer.write(new Double(10.5));

        // 对数组进行序列化
        packer.write(new int[] { 1, 2, 3, 4 });
        packer.write(new Double[] { 10.5, 20.5 });
        packer.write(new String[] { "msg", "pack", "for", "java" });
        packer.write(new byte[] { 0x30, 0x31, 0x32 }); // byte array

        // 对其他引用类型进行序列化
        packer.write("MessagePack"); // String object
        packer.write(ByteBuffer.wrap(new byte[] { 0x30, 0x31, 0x32 })); // ByteBuffer object
        packer.write(BigInteger.ONE); // BigInteger object

        // 反序列化
        byte[] bytes = out.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        Unpacker unpacker = msgpack.createUnpacker(in);

        // 反序列化出原生类型
        boolean b = unpacker.readBoolean(); // boolean value
        int i = unpacker.readInt(); // int value
        double d = unpacker.readDouble(); // double value

        // 反序列化出原生包装类型
        Boolean wb = unpacker.read(Boolean.class);
        Integer wi = unpacker.read(Integer.class);
        Double wd = unpacker.read(Double.class);

        // 反序列化出数组
        int[] ia = unpacker.read(int[].class);
        Double[] da = unpacker.read(Double[].class);
        String[] sa = unpacker.read(String[].class);
        byte[] ba = unpacker.read(byte[].class);

        // 反序列化出 String, ByteBuffer, BigInteger, List 和 Map
        String ws = unpacker.read(String.class);
        ByteBuffer buf = unpacker.read(ByteBuffer.class);
        BigInteger bi = unpacker.read(BigInteger.class);
    }
}
```
对于`List, Map`这俩种类型的序列化, 我们需要额外说明一下. 在序列化`List, Map`的时候, 我们需要使用`Template`对其进行转换.
```java
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;

import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;
import org.msgpack.template.Template;
import org.msgpack.unpacker.Unpacker;
import static org.msgpack.template.Templates.tList;
import static org.msgpack.template.Templates.tMap;
import static org.msgpack.template.Templates.TString;

public class Main4 {
    public static void main(String[] args) throws Exception {
        MessagePack msgpack = new MessagePack();

		// 创建序列化/反序列化 List 和 Map 对象的模板
		Template<List<String>> listTmpl = tList(TString);
		Template<Map<String, String>> mapTmpl = tMap(TString, TString);

		// 开始序列化
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Packer packer = msgpack.createPacker(out);

		// 序列化 List 对象
		List<String> list = new ArrayList<String>();
		list.add("msgpack");
		list.add("for");
		list.add("java");
		packer.write(list); // List object

		// 序列化 Map 对象
		Map<String, String> map = new HashMap<String, String>();
		map.put("sadayuki", "furuhashi");
		map.put("muga", "nishizawa");
		packer.write(map); // Map object

		// 开始反序列化
		byte[] bytes = out.toByteArray();
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		Unpacker unpacker = msgpack.createUnpacker(in);

		// 反序列化出 List 对象
		List<String> dstList = unpacker.read(listTmpl);

		//  反序列化出 Map 对象
		Map<String, String> dstMap = unpacker.read(mapTmpl);
    }
}
```

有时候我们可能没有办法将POJO类添加上`Message`注解(可能没有访问那个类的权限), 但是我们又想对其进行序列化, 那怎么办呢？
```
MessagePack msgpack = new MessagePack();
msgpack.register(MyMessage2.class);
```
我们只要向需要序列化的类注册到`MessagePack`上就可以了.

有时候,我们可能不需要对某个属性进行序列化, 例如线上某个老版本里并没有A属性, 但是在新的版本里填上了A属性, 但是又要兼容老版本怎么办呢？我们可以使用`@Optional`注解
```java
@Message
public static class MyMessage {
    public String name;
    public double version;

    // new field
    @Optional
    public int flag = 0;
}
```

MessagePack还提供了动态类型特性.
```java
import java.util.*;

import org.msgpack.MessagePack;
import org.msgpack.type.Value;
import org.msgpack.unpacker.Converter;

import static org.msgpack.template.Templates.*;

public class Main5 {
    public static void main(String[] args) throws Exception {
        // Create serialize objects.
        List<String> src = new ArrayList<String>();
        src.add("msgpack");
        src.add("kumofs");
        src.add("viver");

        MessagePack msgpack = new MessagePack();
        // Serialize
        byte[] raw = msgpack.write(src);

        // Deserialize directly using a template
        List<String> dst1 = msgpack.read(raw, tList(TString));

        // Or, Deserialze to Value then convert type.
        Value dynamic = msgpack.read(raw);
        List<String> dst2 = new Converter(dynamic).read(tList(TString));
    }
}
```
当从MessagePack里读取数据的时候, 我们可以先不指定其转换的类型,　使用`Value`来代替, 等后期我们再使用`Converter` 对其转换.

接下来我们对其与JSON进行对比
```java
public class msgpack {

	public static void main(String[] args) throws Exception {
		MessagePack msgpack = new MessagePack();

		List<SeObj> list = list();
		long start1 = System.currentTimeMillis();
		byte[] bytes1 = msgpack.write(list);
		long end1 = System.currentTimeMillis();
		System.out.println((end1 - start1) + " : " + bytes1.length);

		long start2 = System.currentTimeMillis();
		String json = JSON.toJSONString(list);
		long end2 = System.currentTimeMillis();
		System.out.println((end2 - start2) + " : " + json.getBytes().length);
	}

	public static List<SeObj> list() {
		List<SeObj> list = new ArrayList<>();
		Random random = new Random();
		for (int i = 0; i < 10000; i++) {
			SeObj seObj = new SeObj();
			seObj.id = random.nextInt(100000);
			seObj.tall = random.nextFloat();
			seObj.name = "name" + random.nextInt(100000);
			list.add(seObj);
		}
		return list;
	}
}

@Message
class SeObj {
	public int id;
	public String name;
	public float tall;
}
```
结果为
```java
100的结果
194 : 1960
50 : 4947
1000的结果
210 : 19589
70 : 49424
10000的结果
212 : 195765
160 : 493987
100000的结果
283 : 1956964
330 : 4940270
1000000的结果
649 : 19573508
1878 : 49404120
```
我们发现MessagePack的耗时相对来说是比较稳定的, 但是在生成的数据量上比JSON要强一倍多
