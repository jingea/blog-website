category: Java
tag: JavaSE
date: 2015-11-21
title: JAVA 写文件
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

## PrintStream 
标准IO重定向

打印输出流,用来装饰其它输出流。它能为其他输出流添加了功能，使它们能够方便地打印各种数据值表示形式。PrintStream永远不会抛出IOException；PrintStream提供了自动flush和字符集设置功能。所谓自动flush，就是往PrintStream写入的数据会立刻调用flush()函数。

System类提供了一些简单的静态方法调用,以允许我们对标准输入,输出和错误IO进行重定向IO重定向是对字节流的操纵而不是字符流,因此在该例中使用的是InputStream和OutputStream而不是Reader和Writer

示例 如果在显示器上创建大量输出,而这些输出滚动地太快而无法阅读时,IO重定向就显得很有用
```java
PrintStream console = System.out;
BufferedInputStream in = new BufferedInputStream(new FileInputStream("Redirecting.java"));

PrintStream out = new PrintStream(new BufferedOutputStream(new FileOutputStream("MapDB.test.out")));

System.setIn(in);
System.setOut(out);
System.setErr(out);

BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
String s;
while ((s = br.readLine()) != null)
	System.out.println(s);

out.close(); // Remember this!
System.setOut(console);
```

## PrintWriter 
用于向文本输出流打印对象的格式化表示形式。它实现在 PrintStream 中的所有 print 方法。它不包含用于写入原始字节的方法，对于这些字节，程序应该使用未编码的字节流进行写入。

FileWriter可以向文件输出数据. 首先创建一个与指定文件连接的FileWriter.然后使用BufferedWriter对其进行包装进行性能提升 最后使用PrintWriter提供格式化功能
```java
try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file)));) {
	out.println(string);
}
```
System.out 是一个PrintStream,而PrintStream是一个OutputStream而PrintWriter有一个参数是接受OutputStream,因此我们可以将System.out转换成PrintWriter
```java
try (PrintWriter out = new PrintWriter(System.out);) {
out.println(string);
}
```