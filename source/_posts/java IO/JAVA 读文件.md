category: JAVA IO
date: 2015-11-21
title: JAVA 读文件
---

## BufferedInputStream
我们使用`FileInputStream`, `BufferedInputStream`来读取文件

```java
// 读取二进制文件
try (BufferedInputStream bf = new BufferedInputStream(
		new FileInputStream(new File("")));) {
	
	byte[] data = new byte[bf.available()];
	bf.read(data);
	
} catch (final IOException e) {
	e.printStackTrace();
}
```
BufferedInputStream是一个带有缓冲区域的`InputStream`, 支持`mark()`标记和`reset()`重置方法。输入到byte[]数组里.
BufferedInputStream只将数据读取进byte字节数组里, 因此这种方式只能读取二进制字节流

> FileInputStream 一个字节一个字节的从文件里读取数据

## BufferedReader
BufferedReader 从字符输入流中读取文本，缓冲各个字符。提供字符、数组和行的高效读取。
我们有俩种方式创建BufferedReader. 
* 使用带缓冲区的写入器 `Files.newBufferedReader(Paths.get("new.txt"), StandardCharsets.UTF_8);`;
* 读取UTF-8格式编码的文件 `new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))`

> InputStreamReader 是字节流通向字符流的桥梁：它使用指定的 charset 读写字节并将其解码为字符。将“字节输入流”转换成“字符输入流”。它继承于Reader。

```java
BufferedReader reader = Files.newBufferedReader(Paths.get("new.txt"), StandardCharsets.UTF_8);
reader.lines().forEach(line -> System.out.println(line));
```
我们可以使用JAVA8中的Stream来快捷的遍历每一行
> 从标准IO中输入. 按照标准的IO模型,Java提供了System.out, System.out, System.err System.out,System.err 已经被包装成了PrintStream对象,但是System.in作为原生InputStream却没有进行过任何包装. 所以在使用System.in时必须对其进行包装,下例中展示了,我们使用InputStreamReader将System.in包装Reader,然后再包装一层BufferedReader

> 另外还有一点需要提到的是FileReader, 它一个字符一个字符地读取。

## LineNumberInputStream 
此类是一个输入流过滤器，它提供跟踪当前行号的附加功能。行是以回车符 ('\r')、换行符 ('\n')或回车符后面紧跟换行符结尾的字节序列。在所有这三种情况下，都以单个换行符形式返回行终止字符。行号以 0 开头，并在 read 返回换行符时递增 1。

## LineNumberReader 
跟踪行号的缓冲字符输入流。此类定义了方法 setLineNumber(int) 和 getLineNumber()，它们可分别用于设置和获取当前行号。默认情况下，行编号从 0 开始。该行号随数据读取在每个行结束符处递增，并且可以通过调用 setLineNumber(int) 更改行号。但要注意的是，setLineNumber(int) 不会实际更改流中的当前位置；它只更改将由getLineNumber() 返回的值。可认为行在遇到以下符号之一时结束：换行符（'\n'）、回车符（'\r'）、回车后紧跟换行符。
```java
//  获取行数
int lineCount = 0;
try (FileReader reader = new FileReader(IOUtils.newFile(""));
		LineNumberReader lnr = new LineNumberReader(reader);) {
	while (lnr.readLine() != null) {
		lineCount++;
	}
} catch (final Exception e) {
	e.printStackTrace();
}
```

## RandomAccessFile  
读写随机访问文件 `RandomAccessFile`除了实现了`DataInput`和`DataOutput`接口之外,有效地与IO继承层次结构的其他部分实现了分离.

因为它不支持装饰模式,所以不能将其与`InputStream`和`OutputStream`子类的任何部分组合起来而且必须假定`RandomAccessFile`已经被正确的缓冲

用来访问那些保存数据记录的文件的，你就可以用`seek()`方法来访问记录，并进行读写了。这些记录的大小不必相同；但是其大小和位置必须是可知的。但是该类仅限于操作文件。
```java
// 读取所有的行
try (RandomAccessFile r = new RandomAccessFile(file, "rw")) {
	for (int i = 0; i < r.length(); i++) {
		r.read();	// r.readLine();
	}
} 

// 写入数据,第二个参数必须为 "r", "rw", "rws", or "rwd"
try (RandomAccessFile w = new RandomAccessFile(file, "rw")) {
	for (int i = 0; i < 1024 * 1024 * 10; i++)
		w.writeByte(1);
}

try (FileChannel fc = new RandomAccessFile(new File("temp.tmp"), "rw")
		.getChannel();) {
	
	IntBuffer ib = fc.map(FileChannel.MapMode.READ_WRITE, 0, fc.size())
			.asIntBuffer();
	
	for (int i = 1; i < 10000; i++)
		ib.put(ib.get(i - 1));
	
}

RandomAccessFile raf = new RandomAccessFile(new File("temp.tmp"), "rw");
raf.writeInt(1);

for (int i = 0; i < 2000000; i++) {
	raf.seek(raf.length() - 4);
	raf.writeInt(raf.readInt());
}

raf.close();
```