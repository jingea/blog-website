title: JAVA IO
---
# IO模型


## IO概念

Linux的内核将所有外部设备都可以看做一个文件来操作。那么我们对与外部设备的操作都可以看做对文件进行操作。我们对一个文件的读写，都通过调用内核提供的系统调用；内核给我们返回一个file descriptor（fd,文件描述符）。而对一个socket的读写也会有相应的描述符，称为socketfd(socket描述符）。描述符就是一个数字，指向内核中一个结构体（文件路径，数据区，等一些属性）。那么我们的应用程序对文件的读写就通过对描述符的读写完成。

linux将内存分为内核区，用户区。linux内核给我们管理所有的硬件资源，应用程序通过调用系统调用和内核交互，达到使用硬件资源的目的。应用程序通过系统调用read发起一个读操作，这时候内核创建一个文件描述符，并通过驱动程序向硬件发送读指令，并将读的的数据放在这个描述符对应结构体的内核缓存区中，然后再把这个数据读到用户进程空间中，这样完成了一次读操作；但是大家都知道I/O设备相比cpu的速度是极慢的。linux提供的read系统调用，也是一个阻塞函数。这样我们的应用进程在发起read系统调用时，就必须阻塞，就进程被挂起而等待文件描述符的读就绪，那么什么是文件描述符读就绪，什么是写就绪？

* 读就绪：就是这个文件描述符的接收缓冲区中的数据字节数大于等于套接字接收缓冲区低水位标记的当前大小；
* 写就绪：该描述符发送缓冲区的可用空间字节数大于等于描述符发送缓冲区低水位标记的当前大小。（如果是socket fd，说明上一个数据已经发送完成）。

接收低水位标记和发送低水位标记：由应用程序指定，比如应用程序指定接收低水位为64个字节。那么接收缓冲区有64个字节，才算fd读就绪；
综上所述，一个基本的IO，它会涉及到两个系统对象，一个是调用这个IO的进程对象，另一个就是系统内核(kernel)。当一个read操作发生时，它会经历两个阶段：
* 通过read系统调用想内核发起读请求。
* 内核向硬件发送读指令，并等待读就绪。 
* 内核把将要读取的数据复制到描述符所指向的内核缓存区中。
* 将数据从内核缓存区拷贝到用户进程空间中。

###### 整个I/O流经历一下几个节点:

1. File System – 文件系统会根据文件与Block的映射关系,通过File System Manager将文件划分为多个Block,请求发送给HBA.
2. HBA  – HBA执行对这一系列的更小的工作单元进行操作,将这部分I/O转换为Fibre Channel协议,包装成不超过2KB的Frame传输到下一个连接节点FC Switch.
3. FC Switch          – FC Switch会通过FC Fabric网络将这些Frame发送到存储系统的前端口（Front Adapter）.
4. Storage FA         – 存储前端口会将这些FC 的Frame重新封装成和HBA初始发送I/O一致,然后FA会将数据传输到阵列缓存Storage Array Cache）
5. Storage Array Cache – 阵列缓存处理I/O通常有两种情况:
    > A.直接返回数据已经写入的讯号给HBA,这种叫作回写,也是大多数存储阵列处理的方式.
    > B. 数据写入缓存然后再刷新到物理磁盘,叫做写透.I/O存放在缓存中以后,交由后端控制器（Disk Adapter）继续处理,完成后再返回数据已经写入的讯号给HBA.
6. Disk Adapter       – 上述两种方式,最后都会将I/O最后写入到物理磁盘中.这个过程由后端Disk Adapter控制,一个I/O会变成两个或者多个实际的I/O.

###### 根据上述的I/O流向的来看,一个完整的I/O传输,经过的会消耗时间的节点可以概括为以下几个:

1. CPU – RAM, 完成主机文件系统到HBA的操作.
2. HBA – FA,完成在光纤网络中的传输过程.
3. FA – Cache,存储前端卡将数据写入到缓存的时间.
4. DA – Drive,存储后端卡将数据从缓存写入到物理磁盘的时间.


###### 阻塞IO
最流行的I/O模型是阻塞I/O模型，缺省情形下，所有文件操作都是阻塞的。我们以套接口为例来讲解此模型。在进程空间中调用recvfrom，其系统调用直到数据报到达且被拷贝到应用进程的缓冲区中或者发生错误才返回，期间一直在等待。我们就说进程在从调用recvfrom开始到它返回的整段时间内是被阻塞的。
![阻塞IO.jpg](/images/阻塞IO.jpg)

######非阻塞IO
进程把一个套接口设置成非阻塞是在通知内核：当所请求的I/O操作不能满足要求时候，不把本进程投入睡眠，而是返回一个错误。也就是说当数据没有到达时并不等待，而是以一个错误返回。
![非阻塞IO.jpg](/images/非阻塞IO.jpg)

###### SIGIO
首先开启套接口信号驱动I/O功能, 并通过系统调用sigaction安装一个信号处理函数（此系统调用立即返回，进程继续工作，它是非阻塞的）。当数据报准备好被读时，就为该进程生成一个SIGIO信号。随即可以在信号处理程序中调用recvfrom来读数据报，井通知主循环数据已准备好被处理中。也可以通知主循环，让它来读数据报。
![信号驱动IO.jpg](/images/信号驱动IO.jpg)

###### select and poll
linux提供select/poll，进程通过将一个或多个fd传递给select或poll系统调用，阻塞在select;这样select/poll可以帮我们侦测许多fd是否就绪。但是select/poll是顺序扫描fd是否就绪，而且支持的fd数量有限。linux还提供了一个epoll系统调用，epoll是基于事件驱动方式，而不是顺序扫描,当有fd就绪时，立即回调函数rollback；
![IO复用.jpg](/images/IO复用.jpg)

###### windows的IOCP
告知内核启动某个操作，并让内核在整个操作完成后(包括将数据从内核拷贝到用户自己的缓冲区)通知我们。这种模型与信号驱动模型的主要区别是：信号驱动I/O：由内核通知我们何时可以启动一个I/O操作；异步I/O模型：由内核通知我们I/O操作何时完成。
![异步IO.jpg](/images/异步IO.jpg)


### 非阻塞IO详解
通过上面，我们知道，所有的IO操作在默认情况下，都是属于阻塞IO。尽管上图中所示的反复请求的非阻塞IO的效率底下（需要反复在用户空间和进程空间切换和判断，把一个原本属于IO密集的操作变为IO密集和计算密集的操作），但是在后面IO复用中，需要把IO的操作设置为非阻塞的，此时程序将会阻塞在select和poll系统调用中。把一个IO设置为非阻塞IO有两种方式：在创建文件描述符时，指定该文件描述符的操作为非阻塞；在创建文件描述符以后，调用fcntl()函数设置相应的文件描述符为非阻塞。
创建描述符时，利用open函数和socket函数的标志设置返回的fd/socket描述符为O_NONBLOCK。

```c
int sd=socket(int domain, int type|O_NONBLOCK, int protocol);  
int fd=open(const char *pathname, int flags|O_NONBLOCK);  
```
创建描述符后，通过调用fcntl函数设置描述符的属性为O_NONBLOCK
```c
#include <unistd.h>  
#include <fcntl.h>  
  
int fcntl(int fd, int cmd, ... /* arg */ );  
  
//例子  
if (fcntl(fd, F_SETFL, fcntl(sockfd, F_GETFL, 0)|O_NONBLOCK) == -1) {  
    return -1;  
}  
    return 0;  
}  
```

### IO复用详解


在IO编程过程中，当需要处理多个请求的时，可以使用多线程和IO复用的方式进行处理。上面的图介绍了整个IO复用的过程，它通过把多个IO的阻塞复用到一个select之类的阻塞上，从而使得系统在单线程的情况下同时支持处理多个请求。和多线程/进程比较，I/O多路复用的最大优势是系统开销小，系统不需要建立新的进程或者线程，也不必维护这些线程和进程。IO复用常见的应用场景：
1. 客户程序需要同时处理交互式的输入和服务器之间的网络连接。
2. 客户端需要对多个网络连接作出反应。
3. 服务器需要同时处理多个处于监听状态和多个连接状态的套接字
4. 服务器需要处理多种网络协议的套接字。




### io#interface

* [Closeable](java/src/test/io/robertsing/cookios/io/TestCloseable.java)
```
	1. Closeable 是可以关闭的数据源或目标。调用 close 方法可释放对象保存的资源（如打开文件）。
```
* [DataInput](java/src/test/io/robertsing/cookios/io/TestDataInput.java)
```
	DataInput 接口用于从二进制流中读取字节，并重构所有 Java 基本类型数据。同时还提供根据 UTF-8 修改版格式的数据
	重构 String 的工具。

	对于此接口中的所有数据读取例程来说，如果在读取到所需字节数的数据之前已经到达文件末尾 (end of file)，
	则都将抛出 EOFException（IOException 的一种）。如果因为文件末尾以外的其他原因无法读取字节，则抛出 IOException
	而不是 EOFException。尤其在输入流已关闭的情况下，将抛出 IOException。
```
* [DataOutput](java/src/test/io/robertsing/cookios/io/TestDataOutput.java)
```
	DataOutput 接口用于将任意 Java 基本类型转换为一系列字节，并将这些字节写入二进制流。同时还提供了一个将 
	String 转换成 UTF-8 修改版格式并写入所得到的系列字节的工具。

对于此接口中写入字节的所有方法，如果由于某种原因无法写入某个字节，则抛出 IOException。
```
* [Externalizable](java/src/test/io/robertsing/cookios/io/TestExternalizable.java)
```
	Externalizable继承于Serializable，当使用该接口时，序列化的细节需要由程序员去完成。如上所示的代码，
	由于writeExternal()与readExternal()方法未作任何处理，那么该序列化行为将不会保存/读取任何一个字段。
```
* [FileFilter](java/src/test/io/robertsing/cookios/io/TestFileFilter.java)
```
	检测文件是否存在。FileFilter 和他的前身FilenameFilter 唯一的不同是FileFilter 提供文件对象的访问方法，
	而FilenameFilter 是按照目录和文件名的方式来工作的。
```
* [FilenameFilter](java/src/test/io/robertsing/cookios/io/TestFilenameFilter.java)
```
```
* [Flushable](java/src/test/io/robertsing/cookios/io/TestFlushable.java)
```
	实现了Flushable接口的类的对象，可以强制将缓存的输出写入到与对象关联的流中。
	写入流的所有I/O类都实现了Flushable接口。
```
* [ObjectInput](java/src/test/io/robertsing/cookios/io/TestObjectInput.java)
```
```
* [ObjectInputValidation](java/src/test/io/robertsing/cookios/io/TestObjectInputValidation.java)
```
	序列化流验证机制. 一般情况下，我们认为序列化流中的数据总是与最初写到流中的数据一致，这并没有问题。
	但当黑客获取流信息并篡改一些敏感信息重新序列化到流中后，用户通过反序列化得到的将是被篡改的信息。
	Java序列化提供一套验证机制。序列化类通过实现 java.io.ObjectInputValidation接口，就可以做到验证了
```
* [ObjectOutput](java/src/test/io/robertsing/cookios/io/TestObjectOutput.java)
```
```
* [ObjectStreamConstants](java/src/test/io/robertsing/cookios/io/TestObjectStreamConstants.java)
```
	Java序列化序列化对象的信息包括：类元数据描述、类的属性、父类信息以及属性域的值。Java将这些信息分成3部分：
	序列化头信息、类的描述部分以及属性域的值部分。现在对a.txt文件加以分析，其中包含一些序列化机制中提供的特殊字段，
	这些字段被定义在java.io.ObjectStreamConstants接口中。 
```
* [Serializable](java/src/test/io/robertsing/cookios/io/TestSerializable.java)
```
```
### io#class

* [BufferedInputStream](java/src/test/io/robertsing/cookios/io/TestBufferedInputStream.java)
```
	BufferedInputStream是一个带有缓冲区域的InputStream, 支持“mark()标记”和“reset()重置方法”。
	输入到byte[]数组里.
```
* [BufferedOutputStream](java/src/test/io/robertsing/cookios/io/TestBufferedOutputStream.java)
```
	缓冲输出流。它继承于FilterOutputStream。作用是为另一个输出流提供“缓冲功能”。
	输出byte[]字节数组
```
* [BufferedReader](java/src/test/io/robertsing/cookios/io/TestBufferedReader.java)
```
	BufferedReader 从字符输入流中读取文本，缓冲各个字符。
	提供字符、数组和行的高效读取。
```
* [BufferedWriter](java/src/test/io/robertsing/cookios/io/TestBufferedWriter.java)
```
	1. 支持字符串输出
	2. 支持换行输出
	3. 支持文件追加输出
```
* [ByteArrayInputStream](java/src/test/io/robertsing/cookios/io/TestByteArrayInputStream.java)
```
	从byte[]数组中读取数据到缓存中.可以将字节数组转化为输入流
	此类中的方法在关闭此流后仍可被调用，而不会产生任何 IOException。
```
* [ByteArrayOutputStream](java/src/test/io/robertsing/cookios/io/TestByteArrayOutputStream.java)
```
	输出数据到byte[]数组里，可以捕获内存缓冲区的数据，转换成字节数组。
	缓冲区会随着数据的不断写入而自动增长。可使用 toByteArray()和 toString()获取数据。
	关闭 ByteArrayOutputStream 无效。此类中的方法在关闭此流后仍可被调用，而不会产生任何IOException。
```
* [CharArrayReader](java/src/test/io/robertsing/cookios/io/TestCharArrayReader.java)
```
	与ByteArrayInputStream对应。 支持mark和reset
	读取char[] 数组
```
* [CharArrayWriter](java/src/test/io/robertsing/cookios/io/TestCharArrayWriter.java)
```
	向内部char[] 缓冲区存储数据.  支持rest, 文件追加写操作, 支持string write 
```
* [Console](java/src/test/io/robertsing/cookios/io/TestConsole.java)
```
	专用来访问基于字符的控制台设备。如果你的Java程序要与Windows下的cmd或者Linux下的Terminal交互，
	就可以用这个Java Console类
	java.io.Console 只能用在标准输入、输出流未被重定向的原始控制台中使用，在 Eclipse 或者其他 IDE 
	的控制台是用不了的。
```
* [DataInputStream](java/src/test/io/robertsing/cookios/io/TestDataInputStream.java)
```
	用来装饰其它输入流，它“允许应用程序以与机器无关方式从底层输入流中读取基本 Java 数据类型”
```
* [DataOutputStream](java/src/test/io/robertsing/cookios/io/TestDataOutputStream.java)
```
	用来装饰其它输出流，将DataOutputStream和DataInputStream输入流配合使用，“允许应用程序以与机器无关方式
	从底层输入流中读写基本 Java 数据类型”。
```
* [File](java/src/test/io/robertsing/cookios/io/TestFile.java)
```
	1. 删除文件
	2. 文件重命名
	3. 创建新的文件
	4. 创建新的文件
	5. 获取文件的最后修改时间
	6. 设置文件只读
	7. 设置文件可写
	8. 获取文件长度(总字节数)
	9. 获取文件路径
	10. 获取绝对文件路径
	11. 文件是否隐藏
	12. 获得剩余磁盘空间？
	13. 拷贝文件夹
	14. 遍历文件夹
	15. 检查文件夹是否为空？
```
* [FileDescriptor](java/src/test/io/robertsing/cookios/io/TestFileDescriptor.java)
```
	用来表示开放文件、开放套接字等。当FileDescriptor表示某文件时，我们可以通俗的将FileDescriptor看成是该文件。
	但是，我们不能直接通过FileDescriptor对该文件进行操作；若需要通过FileDescriptor对该文件进行操作，
	则需要新创建FileDescriptor对应的FileOutputStream，再对文件进行操作。
	
	类实例作为一个不透明的句柄底层机器特有的结构表示一个打开的文件，打开的套接字或其他来源或字节的接收器。
	以下是关于FileDescriptor要点：
	1. 主要实际使用的文件描述符是创建一个FileInputStream或FileOutputStream来遏制它。
	2. 应用程序不应创建自己的文件描述符。
```
* [FileInputStream](java/src/test/io/robertsing/cookios/io/TestFileInputStream.java)
```
	一个字节一个字节的从文件里读取数据
```
* [FileOutputStream](java/src/test/io/robertsing/cookios/io/TestFileOutputStream.java)
```
	一个字节一个字节的向文件里输出数据
```
* [FilePermission](java/src/test/io/robertsing/cookios/io/TestFilePermission.java)
```
	
```
* [FileReader](java/src/test/io/robertsing/cookios/io/TestFileReader.java)
```
	一个字符一个字符地读取
```
* [FileWriter](java/src/test/io/robertsing/cookios/io/TestFileWriter.java)
```
	一个字符一个字符地输出
```
* [FilterInputStream](java/src/test/io/robertsing/cookios/io/TestFilterInputStream.java)
```
	用来“封装其它的输入流，并为它们提供额外的功能”。它的常用的子类有BufferedInputStream和DataInputStream。
```
* [FilterOutputStream](java/src/test/io/robertsing/cookios/io/TestFilterOutputStream.java)
```
	作用是用来“封装其它的输出流，并为它们提供额外的功能”。它主要包括BufferedOutputStream, 
	DataOutputStream和PrintStream。
```
* [FilterReader](java/src/test/io/robertsing/cookios/io/TestFilterReader.java)
```
	用于读取已过滤的字符流的抽象类。抽象类 FilterReader 自身提供了一些将所有请求传递给所包含的流的默认方法。
```
* [FilterWriter](java/src/test/io/robertsing/cookios/io/TestFilterWriter.java)
```
	用于写入已过滤的字符流的抽象类。抽象类 FilterWriter 自身提供了一些将所有请求传递给所包含的流的默认方法
```
* [InputStream](java/src/test/io/robertsing/cookios/io/TestInputStream.java)
```
```
* [InputStreamReader](java/src/test/io/robertsing/cookios/io/TestInputStreamReader.java)
```
	是字节流通向字符流的桥梁：它使用指定的 charset 读写字节并将其解码为字符。
	将“字节输入流”转换成“字符输入流”。它继承于Reader。
```
* [LineNumberInputStream](java/src/test/io/robertsing/cookios/io/TestLineNumberInputStream.java)
```
	此类是一个输入流过滤器，它提供跟踪当前行号的附加功能。
	行是以回车符 ('\r')、换行符 ('\n')或回车符后面紧跟换行符结尾的字节序列。
	在所有这三种情况下，都以单个换行符形式返回行终止字符。行号以 0 开头，并在 read 返回换行符时递增 1。
```
* [LineNumberReader](java/src/test/io/robertsing/cookios/io/TestLineNumberReader.java)
```
	跟踪行号的缓冲字符输入流。此类定义了方法 setLineNumber(int) 和 getLineNumber()，它们可分别用于设置和
	获取当前行号。默认情况下，行编号从 0 开始。该行号随数据读取在每个行结束符处递增，并且可以通过调用 
	setLineNumber(int) 更改行号。但要注意的是，setLineNumber(int) 不会实际更改流中的当前位置；它只更改将由
	getLineNumber() 返回的值。可认为行在遇到以下符号之一时结束：换行符（'\n'）、回车符（'\r'）、回车后紧跟换行符。
```
* [ObjectInputStream](java/src/test/io/robertsing/cookios/io/TestObjectInputStream.java)
```
	用于从底层输入流中读取对象类型的数据和对象类型的数据写入到底层输出流。将对象中所有成员变量的取值保存起来就
	等于保存了对象，将对象中所有成员变量的取值还原就相等于读取了对象。
```
* [ObjectOutputStream](java/src/test/io/robertsing/cookios/io/TestObjectOutputStream.java)
```
	用于从底层输入流中读取对象类型的数据和对象类型的数据写入到底层输出流。将对象中所有成员变量的取值保存起来就
	等于保存了对象，将对象中所有成员变量的取值还原就相等于读取了对象。
```
* [ObjectStreamField](java/src/test/io/robertsing/cookios/io/TestObjectStreamField.java)
```
	Serializable 类中 Serializable 字段的描述。ObjectStreamField 的数组用于声明类的 Serializable 字段。
```
* [OutputStream](java/src/test/io/robertsing/cookios/io/TestOutputStream.java)
```
```
* [OutputStreamWriter](java/src/test/io/robertsing/cookios/io/TestOutputStreamWriter.java)
```
	OutputStreamWriter 将字节流转换为字符流。是字节流通向字符流的桥梁。如果不指定字符集编码，该解码过程将使用平台
	默认的字符编码，如：GBK。
```
* [PipedInputStream](java/src/test/io/robertsing/cookios/io/TestPipedInputStream.java)
```
	管道输入流是让多线程可以通过管道进行线程间的通讯
```
* [PipedOutputStream](java/src/test/io/robertsing/cookios/io/TestPipedOutputStream.java)
```
	管道输出流是让多线程可以通过管道进行线程间的通讯
```
* [PipedReader](java/src/test/io/robertsing/cookios/io/TestPipedReader.java)
```
	PipedWriter 是字符管道输出流,可以通过管道进行线程间的通讯。
```
* [PipedWriter](java/src/test/io/robertsing/cookios/io/TestPipedWriter.java)
```
	PipedReader 是字符管道输入流,可以通过管道进行线程间的通讯。
```
* [PrintStream](java/src/test/io/robertsing/cookios/io/TestPrintStream.java)
```
	打印输出流, 用来装饰其它输出流。它能为其他输出流添加了功能，使它们能够方便地打印各种数据值表示形式。
	PrintStream 永远不会抛出 IOException；PrintStream 提供了自动flush 和 字符集设置功能。
	所谓自动flush，就是往PrintStream写入的数据会立刻调用flush()函数。
```
* [PrintWriter](java/src/test/io/robertsing/cookios/io/TestPrintWriter.java)
```
	用于向文本输出流打印对象的格式化表示形式。它实现在 PrintStream 中的所有 print 方法。
	它不包含用于写入原始字节的方法，对于这些字节，程序应该使用未编码的字节流进行写入。
```
* [PushbackInputStream](java/src/test/io/robertsing/cookios/io/TestPushbackInputStream.java)
```
	拥有一个PushBack缓冲区，从PushbackInputStream读出数据后，只要PushBack缓冲区没有满，
	就可以使用unread()将数据推回流的前端。
```
* [PushbackReader](java/src/test/io/robertsing/cookios/io/TestPushbackReader.java)
```
	允许将字符推回到流的字符流 reader。
```
* [RandomAccessFile](java/src/test/io/robertsing/cookios/io/TestRandomAccessFile.java)
```
	用来访问那些保存数据记录的文件的，你就可以用seek( )方法来访问记录，并进行读写了。这些记录的大小不必相同；
	但是其大小和位置必须是可知的。但是该类仅限于操作文件。
```
* [SequenceInputStream](java/src/test/io/robertsing/cookios/io/TestSequenceInputStream.java)
```
	从多个输入流中向程序读入数据。此时，可以使用合并流，将多个输入流合并成一个SequenceInputStream流对象。 
	SequenceInputStream会将与之相连接的流集组合成一个输入流并从第一个输入流开始读取，直到到达文件末尾，
	接着从第二个输入流读取，依次类推，直到到达包含的最后一个输入流的文件末 尾为止。 
	合并流的作用是将多个源合并合一个源。
```
* [SerializablePermission](java/src/test/io/robertsing/cookios/io/TestSerializablePermission.java)
```
```
* [StreamTokenizer](java/src/test/io/robertsing/cookios/io/TestStreamTokenizer.java)
```
	获取输入流并将其解析为“标记”，允许一次读取一个标记。解析过程由一个表和许多可以设置为各种状态的标志控制。
	该流的标记生成器可以识别标识符、数字、引用的字符串和各种注释样式等。
```
* [StringBufferInputStream](java/src/test/io/robertsing/cookios/io/TestStringBufferInputStream.java)
```
```
* [StringReader](java/src/test/io/robertsing/cookios/io/TestStringReader.java)
```
```
* [StringWriter](java/src/test/io/robertsing/cookios/io/TestStringWriter.java)
```
```
### nio

* [Buffer](java/src/test/io/robertsing/cookios/nio/TestBuffer.java)
```
```
* [ByteBuffer](java/src/test/io/robertsing/cookios/nio/TestByteBuffer.java)
```
```
* [ByteOrder](java/src/test/io/robertsing/cookios/nio/TestByteOrder.java)
```
```
* [CharBuffer](java/src/test/io/robertsing/cookios/nio/TestCharBuffer.java)
```
```
* [DoubleBuffer](java/src/test/io/robertsing/cookios/nio/TestDoubleBuffer.java)
```
```
* [FloatBuffer](java/src/test/io/robertsing/cookios/nio/TestFloatBuffer.java)
```
```
* [IntBuffer](java/src/test/io/robertsing/cookios/nio/TestIntBuffer.java)
```
```
* [LongBuffer](java/src/test/io/robertsing/cookios/nio/TestLongBuffer.java)
```
```
* [MappedByteBuffer](java/src/test/io/robertsing/cookios/nio/TestMappedByteBuffer.java)
```
```
* [ShortBuffer](java/src/test/io/robertsing/cookios/nio/TestShortBuffer.java)
```
```
### nio#channels#Interfaces

* [AsynchronousByteChannel](java/src/test/io/robertsing/cookios/nio/channels/TestAsynchronousByteChannel.java)
```
```
* [AsynchronousChannel](java/src/test/io/robertsing/cookios/nio/channels/TestAsynchronousChannel.java)
```
```
* [ByteChannel](java/src/test/io/robertsing/cookios/nio/channels/TestByteChannel.java)
```
```
* [Channel](java/src/test/io/robertsing/cookios/nio/channels/TestChannel.java)
```
```
* [CompletionHandler](java/src/test/io/robertsing/cookios/nio/channels/TestCompletionHandler.java)
```
```
* [GatheringByteChannel](java/src/test/io/robertsing/cookios/nio/channels/TestGatheringByteChannel.java)
```
```
* [InterruptibleChannel](java/src/test/io/robertsing/cookios/nio/channels/TestInterruptibleChannel.java)
```
```
* [MulticastChannel](java/src/test/io/robertsing/cookios/nio/channels/TestMulticastChannel.java)
```
```
* [NetworkChannel](java/src/test/io/robertsing/cookios/nio/channels/TestNetworkChannel.java)
```
```
* [ReadableByteChannel](java/src/test/io/robertsing/cookios/nio/channels/TestReadableByteChannel.java)
```
```
* [ScatteringByteChannel](java/src/test/io/robertsing/cookios/nio/channels/TestScatteringByteChannel.java)
```
```
* [SeekableByteChannel](java/src/test/io/robertsing/cookios/nio/channels/TestSeekableByteChannel.java)
```
```
* [WritableByteChannel](java/src/test/io/robertsing/cookios/nio/channels/TestWritableByteChannel.java)
```
```
### nio#channels#Classes

* [AsynchronousChannelGroup](java/src/test/io/robertsing/cookios/nio/channels/TestAsynchronousChannelGroup.java)
```
```
* [AsynchronousFileChannel](java/src/test/io/robertsing/cookios/nio/channels/TestAsynchronousFileChannel.java)
```
```
* [AsynchronousServerSocketChannel](java/src/test/io/robertsing/cookios/nio/channels/TestAsynchronousServerSocketChannel.java)
```
```
* [AsynchronousSocketChannel](java/src/test/io/robertsing/cookios/nio/channels/TestAsynchronousSocketChannel.java)
```
```
* [Channels](java/src/test/io/robertsing/cookios/nio/channels/TestChannels.java)
```
```
* [DatagramChannel](java/src/test/io/robertsing/cookios/nio/channels/TestDatagramChannel.java)
```
```
* [FileChannel](java/src/test/io/robertsing/cookios/nio/channels/TestFileChannel.java)
```
```
* [FileChannel.MapMode](java/src/test/io/robertsing/cookios/nio/channels/TestFileChannel.MapMode.java)
```
```
* [FileLock](java/src/test/io/robertsing/cookios/nio/channels/TestFileLock.java)
```
```
* [MembershipKey](java/src/test/io/robertsing/cookios/nio/channels/TestMembershipKey.java)
```
```
* [Pipe](java/src/test/io/robertsing/cookios/nio/channels/TestPipe.java)
```
```
* [SelectableChannel](java/src/test/io/robertsing/cookios/nio/channels/TestSelectableChannel.java)
```
```
* [SelectionKey](java/src/test/io/robertsing/cookios/nio/channels/TestSelectionKey.java)
```
```
* [Selector](java/src/test/io/robertsing/cookios/nio/channels/TestSelector.java)
```
```
* [ServerSocketChannel](java/src/test/io/robertsing/cookios/nio/channels/TestServerSocketChannel.java)
```
```
* [SocketChannel](java/src/test/io/robertsing/cookios/nio/channels/TestSocketChannel.java)
```
```
### nio#file#Interfaces

* [CopyOption](java/src/test/io/robertsing/cookios/nio/file/TestCopyOption.java)
```
```
* [DirectoryStream](java/src/test/io/robertsing/cookios/nio/file/TestDirectoryStream.java)
```
	遍历某个文件夹内的所有文件,但是不会遍历子目录. 也就是这会遍历当前路径中的所有文件
```
* [FileVisitor](java/src/test/io/robertsing/cookios/nio/file/TestFileVisitor.java)
```
	@SimpleFileVisitor
```
* [OpenOption](java/src/test/io/robertsing/cookios/nio/file/TestOpenOption.java)
```
```
* [Path](java/src/test/io/robertsing/cookios/nio/file/TestPath.java)
```
```
* [PathMatcher](java/src/test/io/robertsing/cookios/nio/file/TestPathMatcher.java)
```
```
* [SecureDirectoryStream](java/src/test/io/robertsing/cookios/nio/file/TestSecureDirectoryStream.java)
```
```
* [Watchable](java/src/test/io/robertsing/cookios/nio/file/TestWatchable.java)
```
```
* [WatchEvent](java/src/test/io/robertsing/cookios/nio/file/TestWatchEvent.java)
```
```
* [WatchEvent.Kind](java/src/test/io/robertsing/cookios/nio/file/TestWatchEvent.Kind.java)
```
```
* [WatchEvent.Modifier](java/src/test/io/robertsing/cookios/nio/file/TestWatchEvent.Modifier.java)
```
```
* [WatchKey](java/src/test/io/robertsing/cookios/nio/file/TestWatchKey.java)
```
```
* [WatchService](java/src/test/io/robertsing/cookios/niofile//TestWatchService.java)
```
```
### nio#file#Classes

* [Files](java/src/test/io/robertsing/cookios/nio/file/TestFiles.java)
```
	1. copy
	2. createDirectories
	3. createDirectory
	4. createFile
	5. createLink
	6. createSymbolicLink
	7. createTempDirectory
	8. createTempFile
	9. delete
	10. deleteIfExists
	11. exists
	12. getAttribute
	13. getFileAttributeView
	14. getFileStore
	15. getLastModifiedTime
	16. getOwner
	17. getPosixFilePermissions
	18. isDirectory
	19. isExecutable
	20. isHidden
	21. isReadable
	22. isRegularFile
	23. isSameFile
	24. isSymbolicLink
	25. isWritable
	26. move
	27. newBufferedReader
	28. newBufferedWriter
	29. newByteChannel
	30. newDirectoryStream
	31. newInputStream
	32. newOutputStream
	33. notExists
	34. probeContentType
	35. readAllBytes
	36. readAllLines
	37. readAttributes
	38. readSymbolicLink
	39. setAttribute
	40. setLastModifiedTime
	41. setOwner
	42. setPosixFilePermissions
	43. walkFileTree
	44. write

```
* [FileStore](java/src/test/io/robertsing/cookios/nio/file/TestFileStore.java)
```
	代表了真正的存储设备，提供了设备的详尽信息
```
* [FileSystem](java/src/test/io/robertsing/cookios/nio/file/TestFileSystem.java)
```
```
* [FileSystems](java/src/test/io/robertsing/cookios/nio/file/TestFileSystems.java)
```
```
* [LinkPermission](java/src/test/io/robertsing/cookios/nio/file/TestLinkPermission.java)
```
```
* [Paths](java/src/test/io/robertsing/cookios/nio/file/TestPaths.java)
```
```
* [SimpleFileVisitor](java/src/test/io/robertsing/cookios/nio/file/TestSimpleFileVisitor.java)
```
	与DirectoryStream 不同的是，这个类会遍历目录下包括子目录的所有文件
	并且提供了多种处理接口方法.
```
* [StandardWatchEventKinds](java/src/test/io/robertsing/cookios/nio/file/TestStandardWatchEventKinds.java)
```
```
### nio#charset

* [Charset](java/src/test/io/robertsing/cookios/nio/charset/TestCharset.java)
```
```
* [CharsetDecoder](java/src/test/io/robertsing/cookios/nio/charset/TestCharsetDecoder.java)
```
```
* [CharsetEncoder](java/src/test/io/robertsing/cookios/nio/charset/TestCharsetEncoder.java)
```
```
* [CoderResult](java/src/test/io/robertsing/cookios/nio/charset/TestCoderResult.java)
```
```
* [CodingErrorAction](java/src/test/io/robertsing/cookios/nio/charset/TestCodingErrorAction.java)
```
```
* [StandardCharsets](java/src/test/io/robertsing/cookios/nio/charset/TestStandardCharsets.java)
```
```


# 零拷贝

Java 类库通过 `java.nio.channels.FileChannel` 中的 `transferTo()` 方法来在 Linux 和 UNIX 系统上支持零拷贝。可以使用 `transferTo()` 方法直接将字节从它被调用的通道上传输到另外一个可写字节通道上，数据无需流经应用程序。本文首先展示了通过传统拷贝语义进行的简单文件传输引发的开销，然后展示了使用 `transferTo()` 零拷贝技巧如何提高性能。


###### 数据传输：传统方法
![传统的数据拷贝方法.gif](/images/net/传统的数据拷贝方法.gif)
![传统上下文切换.gif](/images/net/传统上下文切换.gif)

1. read() 调用(参见 图 2)引发了一次从用户模式到内核模式的上下文切换。在内部，发出 sys_read()(或等效内容)以从文件中读取数据。直接内存存取(direct memory access，DMA)引擎执行了第一次拷贝(参见 图 1)，它从磁盘中读取文件内容，然后将它们存储到一个内核地址空间缓存区中。

2. 所需的数据被从读取缓冲区拷贝到用户缓冲区，read() 调用返回。该调用的返回引发了内核模式到用户模式的上下文切换(又一次上下文切换)。现在数据被储存在用户地址空间缓冲区。

3. send() 套接字调用引发了从用户模式到内核模式的上下文切换。数据被第三次拷贝，并被再次放置在内核地址空间缓冲区。但是这一次放置的缓冲区不同，该缓冲区与目标套接字相关联。

4. send() 系统调用返回，结果导致了第四次的上下文切换。DMA 引擎将数据从内核缓冲区传到协议引擎，第四次拷贝独立地、异步地发生 。

使用中间内核缓冲区(而不是直接将数据传输到用户缓冲区)看起来可能有点效率低下。但是之所以引入中间内核缓冲区的目的是想提高性能。在读取方面使用中间内核缓冲区，可以允许内核缓冲区在应用程序不需要内核缓冲区内的全部数据时，充当 “预读高速缓存(readahead cache)” 的角色。这在所需数据量小于内核缓冲区大小时极大地提高了性能。在写入方面的中间缓冲区则可以让写入过程异步完成。

不幸的是，如果所需数据量远大于内核缓冲区大小的话，这个方法本身可能成为一个性能瓶颈。数据在被最终传入到应用程序前，在磁盘、内核缓冲区和用户缓冲区中被拷贝了多次。

零拷贝通过消除这些冗余的数据拷贝而提高了性能。

![使用 transferTo() 方法的数据拷贝.gif](/images/net/使用 transferTo() 方法的数据拷贝.gif)
![使用 transferTo() 方法的上下文切换.gif](/images/net/使用 transferTo() 方法的上下文切换.gif)
```
	1. transferTo() 方法引发 DMA 引擎将文件内容拷贝到一个读取缓冲区。然后由内核将数据拷贝到与输出套接字相关联的内核缓冲区。

	2. 数据的第三次复制发生在 DMA 引擎将数据从内核套接字缓冲区传到协议引擎时。
	改进的地方：我们将上下文切换的次数从四次减少到了两次，将数据复制的次数从四次减少到了三次(其中只有一次涉及到了 CPU)。但是这个代码尚未达到我们的零拷贝要求。如果底层网络接口卡支持收集操作 的话，那么我们就可以进一步减少内核的数据复制。在 Linux 内核 2.4 及后期版本中，套接字缓冲区描述符就做了相应调整，以满足该需求。这种方法不仅可以减少多个上下文切换，还可以消除需要涉及 CPU 的重复的数据拷贝。对于用户方面，用法还是一样的，但是内部操作已经发生了改变：

	A. transferTo() 方法引发 DMA 引擎将文件内容拷贝到内核缓冲区。
	B. 数据未被拷贝到套接字缓冲区。取而代之的是，只有包含关于数据的位置和长度的信息的描述符被追加到了套接字缓冲区。DMA 引擎直接把数据从内核缓冲区传输到协议引擎，从而消除了剩下的最后一次 CPU 拷贝。
```
![结合使用 transferTo() 和收集操作时的数据拷贝.gif](/images/net/结合使用 transferTo() 和收集操作时的数据拷贝.gif)







