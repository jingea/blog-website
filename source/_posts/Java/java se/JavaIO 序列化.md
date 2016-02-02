category: Java
tag: JavaSE
date: 2015-11-21
title: JAVA 序列化
---

## Externalizable 
Externalizable继承于Serializable，当使用该接口时，序列化的细节需要由程序员去完成. 如上所示的代码，由于writeExternal()与readExternal()方法未作任何处理，那么该序列化行为将不会保存/读取任何一个字段. 

## Flushable 
实现了Flushable接口的类的对象，可以强制将缓存的输出写入到与对象关联的流中. 写入流的所有I/O类都实现了Flushable接口. 

## ObjectInputValidation 
序列化流验证机制.一般情况下，我们认为序列化流中的数据总是与最初写到流中的数据一致，这并没有问题. 但当黑客获取流信息并篡改一些敏感信息重新序列化到流中后，用户通过反序列化得到的将是被篡改的信息. Java序列化提供一套验证机制. 序列化类通过实现 java.io.ObjectInputValidation接口，就可以做到验证了

## ObjectStreamConstants 
Java序列化序列化对象的信息包括：类元数据描述、类的属性、父类信息以及属性域的值. Java将这些信息分成3部分：序列化头信息、类的描述部分以及属性域的值部分. 现在对a.txt文件加以分析，其中包含一些序列化机制中提供的特殊字段，这些字段被定义在java.io.ObjectStreamConstants接口中.  

## DataInputStream 
用来装饰其它输入流，它“允许应用程序以与机器无关方式从底层输入流中读取基本 Java 数据类型”

从DataInputStream一次一个字节地读取字符,那么任何值都是合法的,因此返回值不能用来检测输入是否结束.但是可以使用available()函数来查看还有多少字符可供读取

available()函数的工作方式会随之所读取的媒介类不同而不同, 该函数从字面上的意思来讲就是"在没有阻塞的情况下所能读取的字节数".对于文件这指的是整个文件,而对于其他流可能就不是这样的

格式化的内存输入 当读取格式化数据时可以使用DataInputStream，它是一个面向字节的IO类
```java
try(DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(TestDataInputStream.class.getCanonicalName())));) {
	while (in.available() != 0)
		System.out.print((char) in.readByte());
	
}
```
> DataInput 接口用于从二进制流中读取字节，并重构所有 Java 基本类型数据. 同时还提供根据 UTF-8 修改版格式的数据重构 String 的工具. 对于此接口中的所有数据读取例程来说，如果在读取到所需字节数的数据之前已经到达文件末尾 (end of file)，则都将抛出 EOFException（IOException 的一种）. 如果因为文件末尾以外的其他原因无法读取字节，则抛出 IOException而不是 EOFException. 尤其在输入流已关闭的情况下，将抛出 IOException. 

## DataOutputStream 
用来装饰其它输出流，将DataOutputStream和DataInputStream输入流配合使用，“允许应用程序以与机器无关方式从底层输入流中读写基本 Java 数据类型”. 

我们可以使用DataOutputStream指定格式存储数据, 然后使用DataInputStream轻松的再次指定读取格式来恢复这些数据.
```java
DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("Data.txt")));
out.writeDouble(3.14159);
out.writeUTF("That was pi");
out.writeDouble(1.41413);
out.writeUTF("Square root of 2");
out.close();

DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream("Data.txt")));
System.out.println(in.readDouble());
// Only readUTF() will recover the Java-UTF String properly:
System.out.println(in.readUTF());
System.out.println(in.readDouble());
System.out.println(in.readUTF());

in.close();
```
> DataOutput 接口用于将任意 Java 基本类型转换为一系列字节，并将这些字节写入二进制流. 同时还提供了一个将 String 转换成 UTF-8 修改版格式并写入所得到的系列字节的工具. 对于此接口中写入字节的所有方法，如果由于某种原因无法写入某个字节，则抛出 IOException.

## ObjectInputStream 
用于从底层输入流中读取对象类型的数据和对象类型的数据写入到底层输出流。将对象中所有成员变量的取值保存起来就等于保存了对象，将对象中所有成员变量的取值还原就相等于读取了对象。

## ObjectOutputStream 
用于从底层输入流中读取对象类型的数据和对象类型的数据写入到底层输出流。将对象中所有成员变量的取值保存起来就等于保存了对象，将对象中所有成员变量的取值还原就相等于读取了对象。 