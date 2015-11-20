category: JAVA IO
date: 2015-08-08
title: JAVA 文件操作
---

## FileFilter          
检测文件是否存在。FileFilter 和他的前身FilenameFilter 唯一的不同是FileFilter 提供文件对象的访问方法，而FilenameFilter 是按照目录和文件名的方式来工作的。

## FilenameFilter 

## File 
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

## FileDescriptor 
用来表示开放文件、开放套接字等。当FileDescriptor表示某文件时，我们可以通俗的将FileDescriptor看成是该文件。但是，我们不能直接通过FileDescriptor对该文件进行操作；若需要通过FileDescriptor对该文件进行操作，则需要新创建FileDescriptor对应的FileOutputStream，再对文件进行操作。
	
类实例作为一个不透明的句柄底层机器特有的结构表示一个打开的文件，打开的套接字或其他来源或字节的接收器。以下是关于FileDescriptor要点：
1. 主要实际使用的文件描述符是创建一个FileInputStream或FileOutputStream来遏制它。
2. 应用程序不应创建自己的文件描述符。


## FileLock
锁定文件

ByteBuffer.allocate()语句改为ByteBuffer.allocateDirect().用来证实性能之间的差异,但是请注意程序的启动时间是否发生了明显的改变。

修改{@link JGrep}让其使java的nio内存映射文件。

JDK1.4引入了文件加锁机制,它允许我们同步访问某个作为共享资源的文件。不过,竞争同一文件的两个线程可能在不同的Java虚拟机上;或者一个是Java线程,另一个是操作系统中其他的某个本地线程。

文件锁对其他的操作系统进程是可见的,因为Java的文件加锁直接映射到了本地操作系统的加锁工具。通过对FileChannel调用tryLock()或lock(),就可以获得整个文件的FileLock.

(SocketChannel、DatagramChannel和 ServerSocketChannel不需要加锁,因为他们是从单进程实体继承而来;我们通常不在两个进程之间共享网络socket.)

tryLock()是非阻塞式的,它设法获取锁,但是如果不能获得(当其他一些进程已经持有相同的锁,并且不共享时),它将直接从方法调用返回。lock()则是阻塞式的,它要阻塞进程直至锁可以获得,或调用lock()的线程中断,或调用lock()的通道关闭。

使用FileLock.release()可以释放锁。

也可以使用此方法对文件上锁tryLock()或者lock()其中,加锁的区域由size-position决定。第三个参数指定是否是共享锁。

尽管无参数的加锁方法将根据文件尺寸的变化而变化,但是具有固定尺寸的锁不随文件尺寸的变化而变化。如果你获得了某一区域(从position到position+size)上的锁,当文件增大超出position+size时,那么在position+size之外的部分不会被锁定。无参数的加锁方法会对 整个文件进行加锁,甚至文件变大后也是如此。

对独占锁或者共享锁的支持必须由底层的操作系统提供。如果操作系统不支持共享锁并为每一个请求都创建一个锁,那么它就会使用独占锁。

锁的 类型(共享或独占)可以通过FileLock.isShared()进行查询。
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

## CopyOption          


## DirectoryStream     
遍历某个文件夹内的所有文件,但是不会遍历子目录. 也就是这会遍历当前路径中的所有文件

## FileVisitor          FileVisitor 


## OpenOption          OpenOption 


## Path          
Path 类可以在任何文件系统（FileSystem）和任何存储空间 Path 类引用默认文件系统（计算机的文件系统）的文件，但是 NIO.2是完全模块化的—— FileSystem 的具体实现是在内存中的一组数据，因此在网络环境或在虚拟文件系统中，NIO.2 也完全适用。NIO.2提供给我们在文件系统中操作文件、文件夹或链接的所有方法

## PathMatcher          


## SecureDirectoryStream


## Watchable        


## WatchEvent       


## WatchEvent.Kind      


## WatchEvent.Modifier  


## WatchKey    


## WatchService

## Files 

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


## FileStore 
代表了真正的存储设备，提供了设备的详尽信息

## FileSystem         


## FileSystems        
```java
// 返回 JVM 默认的 FileSystem – 一般说来，也就是操作系统的默认文件系统
FileSystems.getDefault();
// 可以获取远程主机的FileSystem
FileSystems.getFileSystem(uri);
// 得到文件系统支持的属性视图列表
FileSystem system = FileSystems.getDefault();
Set<String> views = system.supportedFileAttributeViews();
```

## LinkPermission          


## Paths          


## SimpleFileVisitor          
与DirectoryStream 不同的是，这个类会遍历目录下包括子目录的所有文件并且提供了多种处理接口方法.

## StandardWatchEventKinds