category: Java
date: 2015-11-21
title: JAVA IO
---

## 文件操作

### FileFilter          
检测文件是否存在.FileFilter 和他的前身FilenameFilter 唯一的不同是FileFilter 提供文件对象的访问方法,而FilenameFilter 是按照目录和文件名的方式来工作的.
```java
FileFilter fileFilter = pathname -> {
	System.out.println(pathname.getPath());
	return pathname.isFile();
};

fileFilter.accept(new File("D:\\hazelcast-documentation-3.5.3.pdf"));
```

### FilenameFilter
```java
FilenameFilter filenameFilter = (dir, name) -> {
	System.out.println(dir);
	System.out.println(name);
	return true;
};

filenameFilter.accept(new File("D"), "hazelcast-documentation-3.5.3.pdf");
```

### File
File对象给我们提供了以下的功能
* 删除文件
* 文件重命名
* 创建新的文件
* 创建新的文件
* 获取文件的最后修改时间
* 设置文件只读
* 设置文件可写
* 获取文件长度(总字节数)
* 获取文件路径
* 获取绝对文件路径
* 文件是否隐藏
* 获得剩余磁盘空间？
* 拷贝文件夹
* 遍历文件夹
* 检查文件夹是否为空？

### FileDescriptor
用来表示开放文件、开放套接字等.当FileDescriptor表示某文件时,我们可以通俗的将FileDescriptor看成是该文件.但是,我们不能直接通过FileDescriptor对该文件进行操作；若需要通过FileDescriptor对该文件进行操作,则需要新创建FileDescriptor对应的FileOutputStream,再对文件进行操作.

类实例作为一个不透明的句柄底层机器特有的结构表示一个打开的文件,打开的套接字或其他来源或字节的接收器.以下是关于FileDescriptor要点：
1. 主要实际使用的文件描述符是创建一个FileInputStream或FileOutputStream来遏制它.
2. 应用程序不应创建自己的文件描述符.


### FileLock
锁定文件

ByteBuffer.allocate()语句改为ByteBuffer.allocateDirect().用来证实性能之间的差异,但是请注意程序的启动时间是否发生了明显的改变.

修改{@link JGrep}让其使java的nio内存映射文件.

JDK1.4引入了文件加锁机制,它允许我们同步访问某个作为共享资源的文件.不过,竞争同一文件的两个线程可能在不同的Java虚拟机上;或者一个是Java线程,另一个是操作系统中其他的某个本地线程.

文件锁对其他的操作系统进程是可见的,因为Java的文件加锁直接映射到了本地操作系统的加锁工具.通过对FileChannel调用tryLock()或lock(),就可以获得整个文件的FileLock.

(SocketChannel、DatagramChannel和 ServerSocketChannel不需要加锁,因为他们是从单进程实体继承而来;我们通常不在两个进程之间共享网络socket.)

tryLock()是非阻塞式的,它设法获取锁,但是如果不能获得(当其他一些进程已经持有相同的锁,并且不共享时),它将直接从方法调用返回.lock()则是阻塞式的,它要阻塞进程直至锁可以获得,或调用lock()的线程中断,或调用lock()的通道关闭.

使用FileLock.release()可以释放锁.

也可以使用此方法对文件上锁tryLock()或者lock()其中,加锁的区域由size-position决定.第三个参数指定是否是共享锁.

尽管无参数的加锁方法将根据文件尺寸的变化而变化,但是具有固定尺寸的锁不随文件尺寸的变化而变化.如果你获得了某一区域(从position到position+size)上的锁,当文件增大超出position+size时,那么在position+size之外的部分不会被锁定.无参数的加锁方法会对 整个文件进行加锁,甚至文件变大后也是如此.

对独占锁或者共享锁的支持必须由底层的操作系统提供.如果操作系统不支持共享锁并为每一个请求都创建一个锁,那么它就会使用独占锁.

锁的 类型(共享或独占)可以通过FileLock.isShared()进行查询.
```java
FileOutputStream fos = new FileOutputStream("file.txt");
FileLock fl = fos.getChannel().tryLock();
if (fl != null) {
	System.out.println("Locked File");
	TimeUnit.MILLISECONDS.sleep(100);
	fl.release();
	System.out.println("Released Lock");
}
fos.close()
```

### DirectoryStream     
遍历某个文件夹内的所有文件,但是不会遍历子目录. 也就是这会遍历当前路径中的所有文件
```java
DirectoryStream<Path> paths = Files.newDirectoryStream(Paths.get("E:"));
paths.forEach(path -> {
	System.out.println(path.getFileName());
});
```
> SecureDirectoryStream

### FileVisitor
遍历某个文件夹内的所有文件接口.

`SimpleFileVisitor`实现了这个接口. 与`DirectoryStream` 不同的是,这个类会遍历目录下包括子目录的所有文件并且提供了多种处理接口方法.
```java
SimpleFileVisitor visitor = new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				System.out.println(file.getFileName());
				return super.visitFile(file, attrs);
			}
		};

		Files.walkFileTree(Paths.get("E:"), visitor);
```

### 监控文件变化
```java
WatchService service = FileSystems.getDefault().newWatchService();
		Paths.get("D:/").register(service,
		ENTRY_CREATE,
		ENTRY_DELETE,
		ENTRY_MODIFY);

WatchKey watchKey = service.take();

watchKey.pollEvents().stream().forEach(watchEvent -> {
	System.out.println(watchEvent.context() + "  " + watchEvent.kind());
});
watchKey.reset();
```


### FileStore
代表了真正的存储设备,提供了设备的详尽信息

### FileSystems   
* FileSystems.getDefault() ：返回 JVM 默认的 FileSystem – 一般说来,也就是操作系统的默认文件系统
* FileSystems.getFileSystem(uri) ： 可以获取远程主机的FileSystem
```java
FileSystem system = FileSystems.getDefault();
// 得到文件系统支持的属性视图列表
Set<String> views = system.supportedFileAttributeViews();
```
> FileSystem


### Paths          
> Path 类可以在任何文件系统（FileSystem）和任何存储空间 Path 类引用默认文件系统（计算机的文件系统）的文件,但是 NIO.2是完全模块化的—— FileSystem 的具体实现是在内存中的一组数据,因此在网络环境或在虚拟文件系统中,NIO.2 也完全适用.NIO.2提供给我们在文件系统中操作文件、文件夹或链接的所有方法

## 写文件

### BufferedOutputStream
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

### BufferedWriter
1. 支持字符串输出
2. 支持换行输出
3. 支持文件追加输出
```java
BufferedWriter writer = Files.newBufferedWriter(Paths.get("new.txt"), StandardCharsets.UTF_8);
writer.write("123456\n"); // 换行输出
```
> 另外还有一点需要提到的是FileWriter, 它一个字符一个字符地输出

### OutputStreamWriter
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

### PrintStream
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

### PrintWriter
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


## 读文件

### BufferedInputStream
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
`BufferedInputStream`是一个带有缓冲区域的`InputStream`, 支持`mark()`标记和`reset()`重置方法.输入到byte[]数组里.只将数据读取进byte字节数组里, 因此这种方式只能读取二进制字节流
> FileInputStream 一个字节一个字节的从文件里读取数据

### BufferedReader
BufferedReader 从字符输入流中读取文本,缓冲各个字符.提供字符、数组和行的高效读取.
我们有俩种方式创建BufferedReader.
* 使用带缓冲区的写入器 `Files.newBufferedReader(Paths.get("new.txt"), StandardCharsets.UTF_8);`;
* 读取UTF-8格式编码的文件 `new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))`

> InputStreamReader 是字节流通向字符流的桥梁：它使用指定的 charset 读写字节并将其解码为字符.将“字节输入流”转换成“字符输入流”.它继承于Reader.

```java
BufferedReader reader = Files.newBufferedReader(Paths.get("new.txt"), StandardCharsets.UTF_8);
reader.lines().forEach(line -> System.out.println(line));
```
我们可以使用JAVA8中的Stream来快捷的遍历每一行
> 从标准IO中输入. 按照标准的IO模型,Java提供了`System.out, System.out, System.err System.out,System.err` 已经被包装成了`PrintStream`对象,但是`System.in`作为原生`InputStream`却没有进行过任何包装. 所以在使用`System.in`时必须对其进行包装,下例中展示了,我们使用`InputStreamReader`将`System.in`包装`Reader`,然后再包装一层`BufferedReader`

> 另外还有一点需要提到的是FileReader, 它一个字符一个字符地读取.

### LineNumberInputStream
此类是一个输入流过滤器,它提供跟踪当前行号的附加功能.行是以回车符 (`\r`)、换行符 (`\n`)或回车符后面紧跟换行符结尾的字节序列.在所有这三种情况下,都以单个换行符形式返回行终止字符.行号以 0 开头,并在 read 返回换行符时递增 1.

### LineNumberReader
跟踪行号的缓冲字符输入流.此类定义了方法 `setLineNumber(int)` 和 `getLineNumber()`,它们可分别用于设置和获取当前行号.默认情况下,行编号从 0 开始.该行号随数据读取在每个行结束符处递增,并且可以通过调用 `setLineNumber(int)` 更改行号.但要注意的是,`setLineNumber(int)` 不会实际更改流中的当前位置；它只更改将由`getLineNumber() `返回的值.可认为行在遇到以下符号之一时结束：换行符（`\n`）、回车符（`\r`）、回车后紧跟换行符.
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

### RandomAccessFile  
读写随机访问文件 `RandomAccessFile`除了实现了`DataInput`和`DataOutput`接口之外,有效地与IO继承层次结构的其他部分实现了分离.

因为它不支持装饰模式,所以不能将其与`InputStream`和`OutputStream`子类的任何部分组合起来而且必须假定`RandomAccessFile`已经被正确的缓冲

用来访问那些保存数据记录的文件的,你就可以用`seek()`方法来访问记录,并进行读写了.这些记录的大小不必相同；但是其大小和位置必须是可知的.但是该类仅限于操作文件.
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
try (FileChannel fc = new RandomAccessFile(new File("temp.tmp"), "rw").getChannel();) {
	IntBuffer ib = fc.map(FileChannel.MapMode.READ_WRITE, 0, fc.size()).asIntBuffer();
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

### getResourceAsStream
我们还可以使用类加载器的`getResourceAsStream()`从指定路径或者jar包中加载文件资源
1. Class.getResourceAsStream(String path) ： path 不以’/'开头时默认是从此类所在的包下取资源，以’/'开头则是从ClassPath根下获取。其只是通过path构造一个绝对路径，最终还是由ClassLoader获取资源。
2. Class.getClassLoader.getResourceAsStream(String path) ：默认则是从ClassPath根下获取，path不能以’/'开头，最终是由ClassLoader获取资源。
```java
public class TestReadFile {
	public static void main(String[] args) throws IOException {

		InputStream in = TestReadFile.class.getClassLoader().getResourceAsStream("./mybatis-config.xml");
		BufferedReader buffer = new BufferedReader(new InputStreamReader(in));
		System.out.println(buffer.readLine());
		in = new TestReadFile().getClass().getResourceAsStream("./mybatis-config.xml");
		buffer = new BufferedReader(new InputStreamReader(in));
		System.out.println(buffer.readLine());

		System.out.println(new File(".").getCanonicalPath());
		System.out.println(new File(".").getAbsolutePath());
		System.out.println(new File(".").getPath());
	}
}
```
输出结果为
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<?xml version="1.0" encoding="UTF-8" ?>
D:\ming\test
D:\ming\test\.
.
```

### 读取压缩包文件
```java
public static Map<String, byte[]> getLoadedClass(String jarPath) {
	Map<String, byte[]> loadClass = new HashMap<>();
	try(InputStream in = new BufferedInputStream(new FileInputStream(new File(jarPath)));
		ZipInputStream zin = new ZipInputStream(in)) {
        ZipEntry ze;
        while ((ze = zin.getNextEntry()) != null) {
            if (ze.isDirectory()) {
                // TODO 检查是否还有其他操作要做
            } else {
                if (ze.getSize() > 0) {
                    String fileName = ze.getName();
                    if (!fileName.endsWith(".class")) {
                        continue;
                    }
					try(ZipFile zf = new ZipFile(jarPath); InputStream input = zf.getInputStream(ze);
						ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
						if (input == null) {
							logger.error("Code Reload cant find file : " + fileName);
							continue;
						}
						int b = 0;
						while ((b = input.read()) != -1) {
							byteArrayOutputStream.write(b);
						}
						byte[] bytes = byteArrayOutputStream.toByteArray();
						// TODO 
					}
                }
            }
        }
    } catch (final Exception e) {
        e.printStackTrace();
    }
	return loadClass;
}
```
在写这个东西的时候遇到个小坑, 刚开始使用的是`input.available()`读取的文件内容, 但是大概要读取100多个文件, 总有6个文件读取的不完整,查看JDK的文档,上面说
```bash
Returns an estimate of the number of bytes that can be read (or skipped over) from this input stream without blocking by the next invocation of a method for this input stream. The next invocation might be the same thread or another thread.  A single read or skip of this many bytes will not block, but may read or skip fewer bytes.

Note that while some implementations of InputStream will return the total number of bytes in the stream, many will not.  It is never correct to use the return value of this method to allocate a buffer intended to hold all data in this stream.
```
这个方法返回的是在非阻塞情况下对该流进行下一次某个方法(读或者skip操作)调用时的预估字节数.下一个方法调用可以在同一个线程或者在不同的线程中。下一次读取或者skip掉available预估字节数不会产生阻塞的情况，但是实际读取出来的字节数可能小于预估数。

因此在ZipFileInputStream中，它依赖于native方法实现，我们也不知道它返回的究竟是流里剩下的全部的字节数还是部分的。最后文档也说了，永远不要使用这个方法返回的预估字节数分配buffer大小的buffer.

## 内存IO

### ByteArrayInputStream
从byte[]数组中读取数据到缓存中.可以将字节数组转化为输入流此类中的方法在关闭此流后仍可被调用，而不会产生任何 `IOException`。
```java
byte[] buff = {1, 2, 3, 4, 5};
try(ByteArrayInputStream in = new ByteArrayInputStream(buff)) {

	while(in.available() != 0)
		System.out.println(in.read());

} catch (IOException e) {
	e.printStackTrace();
}
```

### ByteArrayOutputStream
输出数据到byte[]数组里，可以捕获内存缓冲区的数据，转换成字节数组。缓冲区会随着数据的不断写入而自动增长。可使用 toByteArray()和 toString()获取数据。	关闭 `ByteArrayOutputStream `无效。此类中的方法在关闭此流后仍可被调用，而不会产生任何`IOException`。
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

### CharArrayReader
与ByteArrayInputStream对应。 支持mark和reset读取char[] 数组
```java
char[] array = {'a', 'z', 'g'};
try(CharArrayReader in = new CharArrayReader(array)) {
	while(in.ready())
		System.out.println(in.read());
} catch (IOException e) {
	e.printStackTrace();
}
```

### CharArrayWriter
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

### PushbackInputStream
拥有一个PushBack缓冲区，从PushbackInputStream读出数据后，只要PushBack缓冲区没有满，就可以使用unread()将数据推回流的前端。

### PushbackReader
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

### PipedReader
PipedWriter 是字符管道输出流,可以通过管道进行线程间的通讯。

### PipedWriter
PipedReader 是字符管道输入流,可以通过管道进行线程间的通讯。

### PipedInputStream
管道输入流是让多线程可以通过管道进行线程间的通讯

### PipedOutputStream
管道输出流是让多线程可以通过管道进行线程间的通讯

### SequenceInputStream
从多个输入流中向程序读入数据。此时，可以使用合并流，将多个输入流合并成一个SequenceInputStream流对象。SequenceInputStream会将与之相连接的流集组合成一个输入流并从第一个输入流开始读取，直到到达文件末尾，接着从第二个输入流读取，依次类推，直到到达包含的最后一个输入流的文件末 尾为止。 合并流的作用是将多个源合并合一个源。

### StreamTokenizer
获取输入流并将其解析为“标记”，允许一次读取一个标记。解析过程由一个表和许多可以设置为各种状态的标志控制。该流的标记生成器可以识别标识符、数字、引用的字符串和各种注释样式等。

### Console
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

## 序列化

### Externalizable 
Externalizable继承于Serializable，当使用该接口时，序列化的细节需要由程序员去完成. 如上所示的代码，由于writeExternal()与readExternal()方法未作任何处理，那么该序列化行为将不会保存/读取任何一个字段. 

### Flushable 
实现了Flushable接口的类的对象，可以强制将缓存的输出写入到与对象关联的流中. 写入流的所有I/O类都实现了Flushable接口. 

### ObjectInputValidation 
序列化流验证机制.一般情况下，我们认为序列化流中的数据总是与最初写到流中的数据一致，这并没有问题. 但当黑客获取流信息并篡改一些敏感信息重新序列化到流中后，用户通过反序列化得到的将是被篡改的信息. Java序列化提供一套验证机制. 序列化类通过实现 java.io.ObjectInputValidation接口，就可以做到验证了

### ObjectStreamConstants 
Java序列化序列化对象的信息包括：类元数据描述、类的属性、父类信息以及属性域的值. Java将这些信息分成3部分：序列化头信息、类的描述部分以及属性域的值部分. 现在对a.txt文件加以分析，其中包含一些序列化机制中提供的特殊字段，这些字段被定义在java.io.ObjectStreamConstants接口中.  

### DataInputStream 
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

### DataOutputStream 
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

### ObjectInputStream 
用于从底层输入流中读取对象类型的数据和对象类型的数据写入到底层输出流。将对象中所有成员变量的取值保存起来就等于保存了对象，将对象中所有成员变量的取值还原就相等于读取了对象。

### ObjectOutputStream 
用于从底层输入流中读取对象类型的数据和对象类型的数据写入到底层输出流。将对象中所有成员变量的取值保存起来就等于保存了对象，将对象中所有成员变量的取值还原就相等于读取了对象。 