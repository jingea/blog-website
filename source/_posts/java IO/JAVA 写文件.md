category: JAVA IO
date: 2015-11-21
title: JAVA 读文件
---

## BufferedOutputStream
我们使用`FileOutputStream`, `BufferedOutputStream`来读取文件
```java
try (BufferedOutputStream bf = new BufferedOutputStream(new FileOutputStream(new File("")));) {
	bf.write(1);
} catch (final IOException e) {
	e.printStackTrace();
}
```
BufferedOutputStream 缓冲输出流。它继承于FilterOutputStream。作用是为另一个输出流提供“缓冲功能”。输出byte[]字节数组
BufferedOutputStream只提供了输出byte数据的方式,因此这种方式只能读取二进制流

> FileOutputStream 一个字节一个字节的向文件里输出数据

## BufferedWriter 
1. 支持字符串输出
2. 支持换行输出
3. 支持文件追加输出
```
BufferedWriter writer = Files.newBufferedWriter(Paths.get("new.txt"), StandardCharsets.UTF_8);
writer.write("123456\n"); // 换行输出
```
> 另外还有一点需要提到的是FileWriter, 它一个字符一个字符地输出

## OutputStreamWriter 
OutputStreamWriter 将字节流转换为字符流。是字节流通向字符流的桥梁。如果不指定字符集编码，该解码过程将使用平台默认的字符编码，如：GBK。
```java
// 写入UTF-8格式编码的文件
StringBuffer buffer = new StringBuffer();
try (Writer out = new BufferedWriter(new OutputStreamWriter(
		new FileOutputStream(file), "UTF8"))) {

	out.append("Website UTF-8").append("\r\n");
	out.append("中文 UTF-8").append("\r\n");

	out.flush();
} catch (final Exception e) {
	e.printStackTrace();
}
```