category: JAVA IO
date: 2015-08-08
title: JAVA 内存IO
---
## ByteArrayInputStream 
从byte[]数组中读取数据到缓存中.可以将字节数组转化为输入流此类中的方法在关闭此流后仍可被调用，而不会产生任何 IOException。
```java
byte[] buff = {1, 2, 3, 4, 5};
try(ByteArrayInputStream in = new ByteArrayInputStream(buff)) {
	
	while(in.available() != 0)
		System.out.println(in.read());
	
} catch (IOException e) {
	e.printStackTrace();
}
```

## ByteArrayOutputStream 
输出数据到byte[]数组里，可以捕获内存缓冲区的数据，转换成字节数组。缓冲区会随着数据的不断写入而自动增长。可使用 toByteArray()和 toString()获取数据。	关闭 ByteArrayOutputStream 无效。此类中的方法在关闭此流后仍可被调用，而不会产生任何IOException。
```java
byte[] buff = {1, 2, 3, 4, 5};
try(ByteArrayOutputStream out = new ByteArrayOutputStream()) {
	out.write(buff);
	
	byte[] byteArray = out.toByteArray();
	for (byte b : byteArray) {
		System.out.println("flush before : " + b);
	}
	
	out.flush();
	
	byteArray = out.toByteArray();
	for (byte b : byteArray) {
		System.out.println("flush after : " + b);
	}
} catch (IOException e) {
	e.printStackTrace();
}
```

## CharArrayReader 
与ByteArrayInputStream对应。 支持mark和reset读取char[] 数组
```
char[] array = {'a', 'z', 'g'};
try(CharArrayReader in = new CharArrayReader(array)) {
	while(in.ready())
		System.out.println(in.read());
} catch (IOException e) {
	e.printStackTrace();
}
```

## CharArrayWriter 
向内部char[] 缓冲区存储数据.  支持rest, 文件追加写操作, 支持string write 
```java
try(CharArrayWriter out = new CharArrayWriter()) {
	out.write("TestChararray");
	System.out.println(out.toString());
	out.append("test_");
	System.out.println(out.toString());
} catch (IOException e) {
	e.printStackTrace();
} 
```

## PushbackInputStream 
拥有一个PushBack缓冲区，从PushbackInputStream读出数据后，只要PushBack缓冲区没有满，就可以使用unread()将数据推回流的前端。

## PushbackReader 
允许将字符推回到流的字符流 reader。当程序调用推回输入流的unread()方法时，系统会把指定数组的内容推回到该缓冲区中，而推回输入流每次调用read()方法时，总是先从推回缓冲区读取内容，只有完全读取了推回缓冲区里的内容后，但是还没有装满read()所需要的数组时才会从原输入流中读取
```java
try (
// 创建一个PushbackReader对象，指定推回缓冲区的长度为64
PushbackReader pr = new PushbackReader(new FileReader("PushBackTest.java"), 64);
char[] buf = new char[32];
// 用以保存上次读取字符串的内容
String lastContent = "";
int hasRead = 0;

// 循环读取文件内容
while ((hasRead = pr.read(buf)) > 0) {
	// 将读取的内容转化为字符串
	String content = new String(buf, 0, hasRead);
	int targetIndex = 0;

	// 将上次读取的字符串和本次读取的字符串拼接起来
	// 查看是否包含目标字符串，
	// 如果包含目标字符串
	if ((targetIndex = (lastContent + content)
			.indexOf("new PushbackReader")) > 0) {
		// 将本次的内容和上次的内容一起推回缓冲区
		pr.unread((lastContent + content).toCharArray());

		// 重现定义一个长度为targetIndex的char类型的数组
		if (targetIndex > 32) {
			buf = new char[targetIndex];
		}

		// 再次读取指定长度的内容，即目标字符串之前的内容
		pr.read(buf, 0, targetIndex);

		// 答应读取指定长度的内容
		System.out.println(new String(buf, 0, targetIndex));
		System.exit(0);
	} else {

		// 打印上次读取的内容
		System.out.println(lastContent);
		// 将本次读取的内容设置为上次读取的内容
		lastContent = content;

	}

}
```


## PipedReader 
PipedWriter 是字符管道输出流,可以通过管道进行线程间的通讯。

## PipedWriter 
PipedReader 是字符管道输入流,可以通过管道进行线程间的通讯。

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

## PipedInputStream 
管道输入流是让多线程可以通过管道进行线程间的通讯

## PipedOutputStream 
管道输出流是让多线程可以通过管道进行线程间的通讯

## SequenceInputStream 
从多个输入流中向程序读入数据。此时，可以使用合并流，将多个输入流合并成一个SequenceInputStream流对象。SequenceInputStream会将与之相连接的流集组合成一个输入流并从第一个输入流开始读取，直到到达文件末尾，接着从第二个输入流读取，依次类推，直到到达包含的最后一个输入流的文件末 尾为止。 合并流的作用是将多个源合并合一个源。

## StreamTokenizer 
获取输入流并将其解析为“标记”，允许一次读取一个标记。解析过程由一个表和许多可以设置为各种状态的标志控制。该流的标记生成器可以识别标识符、数字、引用的字符串和各种注释样式等。

## StringBufferInputStream 


## StringReader 


## StringWriter 


## Console 
专用来访问基于字符的控制台设备。如果你的Java程序要与Windows下的cmd或者Linux下的Terminal交互，就可以用这个Java Console类java.io.Console 只能用在标准输入、输出流未被重定向的原始控制台中使用，在 Eclipse 或者其他 IDE 的控制台是用不了的。
```java
Console cons = System.console();
if (cons != null) {
	// -------------------------
	PrintWriter printWriter = cons.writer();
	printWriter.write("input:");
	cons.flush();
	String str1 = cons.readLine();
	cons.format("%s", str1);
}

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



