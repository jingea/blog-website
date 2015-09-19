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

### 整个I/O流经历一下几个节点:

1. File System – 文件系统会根据文件与Block的映射关系,通过`File System Manager`将文件划分为多个Block,请求发送给HBA.
2. HBA  – HBA执行对这一系列的更小的工作单元进行操作,将这部分I/O转换为Fibre Channel协议,包装成不超过2KB的Frame传输到下一个连接节点FC Switch.
3. FC Switch          – FC Switch会通过FC Fabric网络将这些Frame发送到存储系统的前端口（Front Adapter）.
4. Storage FA         – 存储前端口会将这些FC 的Frame重新封装成和HBA初始发送I/O一致,然后FA会将数据传输到阵列缓存Storage Array Cache）
5. Storage Array Cache – 阵列缓存处理I/O通常有两种情况:
    * 直接返回数据已经写入的讯号给HBA,这种叫作回写,也是大多数存储阵列处理的方式.
    * 数据写入缓存然后再刷新到物理磁盘,叫做写透.I/O存放在缓存中以后,交由后端控制器（Disk Adapter）继续处理,完成后再返回数据已经写入的讯号给HBA.
6. Disk Adapter       – 上述两种方式,最后都会将I/O最后写入到物理磁盘中.这个过程由后端Disk Adapter控制,一个I/O会变成两个或者多个实际的I/O.

##### 根据上述的I/O流向的来看,一个完整的I/O传输,经过的会消耗时间的节点可以概括为以下几个:

1. CPU – RAM, 完成主机文件系统到HBA的操作.
2. HBA – FA,完成在光纤网络中的传输过程.
3. FA – Cache,存储前端卡将数据写入到缓存的时间.
4. DA – Drive,存储后端卡将数据从缓存写入到物理磁盘的时间.

## IO类型

### 阻塞IO
最流行的I/O模型是阻塞I/O模型，缺省情形下，所有文件操作都是阻塞的。我们以套接口为例来讲解此模型。在进程空间中调用recvfrom，其系统调用直到数据报到达且被拷贝到应用进程的缓冲区中或者发生错误才返回，期间一直在等待。我们就说进程在从调用recvfrom开始到它返回的整段时间内是被阻塞的。
![阻塞IO.jpg](https://raw.githubusercontent.com/wanggnim/website/images/net/阻塞IO.jpg)

### 非阻塞IO
进程把一个套接口设置成非阻塞是在通知内核：当所请求的I/O操作不能满足要求时候，不把本进程投入睡眠，而是返回一个错误。也就是说当数据没有到达时并不等待，而是以一个错误返回。
![非阻塞IO.jpg](https://raw.githubusercontent.com/wanggnim/website/images/net/非阻塞IO.jpg)

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

### SIGIO
首先开启套接口信号驱动I/O功能, 并通过系统调用sigaction安装一个信号处理函数（此系统调用立即返回，进程继续工作，它是非阻塞的）。当数据报准备好被读时，就为该进程生成一个SIGIO信号。随即可以在信号处理程序中调用recvfrom来读数据报，井通知主循环数据已准备好被处理中。也可以通知主循环，让它来读数据报。
![信号驱动IO.jpg](https://raw.githubusercontent.com/wanggnim/website/images/net/信号驱动IO.jpg)

### select and poll
linux提供select/poll，进程通过将一个或多个fd传递给select或poll系统调用，阻塞在select;这样select/poll可以帮我们侦测许多fd是否就绪。但是select/poll是顺序扫描fd是否就绪，而且支持的fd数量有限。linux还提供了一个epoll系统调用，epoll是基于事件驱动方式，而不是顺序扫描,当有fd就绪时，立即回调函数rollback；
![IO复用.jpg](https://raw.githubusercontent.com/wanggnim/website/images/net/IO复用.jpg)

##### IO复用详解

在IO编程过程中，当需要处理多个请求的时，可以使用多线程和IO复用的方式进行处理。上面的图介绍了整个IO复用的过程，它通过把多个IO的阻塞复用到一个select之类的阻塞上，从而使得系统在单线程的情况下同时支持处理多个请求。和多线程/进程比较，I/O多路复用的最大优势是系统开销小，系统不需要建立新的进程或者线程，也不必维护这些线程和进程。IO复用常见的应用场景：
1. 客户程序需要同时处理交互式的输入和服务器之间的网络连接。
2. 客户端需要对多个网络连接作出反应。
3. 服务器需要同时处理多个处于监听状态和多个连接状态的套接字
4. 服务器需要处理多种网络协议的套接字。

### windows的IOCP
告知内核启动某个操作，并让内核在整个操作完成后(包括将数据从内核拷贝到用户自己的缓冲区)通知我们。这种模型与信号驱动模型的主要区别是：信号驱动I/O：由内核通知我们何时可以启动一个I/O操作；异步I/O模型：由内核通知我们I/O操作何时完成。
![异步IO.jpg](https://raw.githubusercontent.com/wanggnim/website/images/net/异步IO.jpg)



## 零拷贝

Java 类库通过 `java.nio.channels.FileChannel` 中的 `transferTo()` 方法来在 Linux 和 UNIX 系统上支持零拷贝。可以使用 `transferTo()` 方法直接将字节从它被调用的通道上传输到另外一个可写字节通道上，数据无需流经应用程序。本文首先展示了通过传统拷贝语义进行的简单文件传输引发的开销，然后展示了使用 `transferTo()` 零拷贝技巧如何提高性能。


###### 数据传输：传统方法
![传统的数据拷贝方法.gif](https://raw.githubusercontent.com/wanggnim/website/images/net/transfer_copy.jpg)
![传统上下文切换.gif](https://raw.githubusercontent.com/wanggnim/website/images/net/transfer_context.gif)

1. read() 调用引发了一次从用户模式到内核模式的上下文切换。在内部，发出 `sys_read()`(或等效内容)以从文件中读取数据。直接内存存取(`direct memory access`，DMA)引擎执行了第一次拷贝，它从磁盘中读取文件内容，然后将它们存储到一个内核地址空间缓存区中。
2. 所需的数据被从读取缓冲区拷贝到用户缓冲区，read() 调用返回。该调用的返回引发了内核模式到用户模式的上下文切换(又一次上下文切换)。现在数据被储存在用户地址空间缓冲区。
3. `send()` 套接字调用引发了从用户模式到内核模式的上下文切换。数据被第三次拷贝，并被再次放置在内核地址空间缓冲区。但是这一次放置的缓冲区不同，该缓冲区与目标套接字相关联。
4. `send()` 系统调用返回，结果导致了第四次的上下文切换。DMA 引擎将数据从内核缓冲区传到协议引擎，第四次拷贝独立地、异步地发生 。

使用中间内核缓冲区(而不是直接将数据传输到用户缓冲区)看起来可能有点效率低下。但是之所以引入中间内核缓冲区的目的是想提高性能。在读取方面使用中间内核缓冲区，可以允许内核缓冲区在应用程序不需要内核缓冲区内的全部数据时，充当 “预读高速缓存(readahead cache)” 的角色。这在所需数据量小于内核缓冲区大小时极大地提高了性能。在写入方面的中间缓冲区则可以让写入过程异步完成。

不幸的是，如果所需数据量远大于内核缓冲区大小的话，这个方法本身可能成为一个性能瓶颈。数据在被最终传入到应用程序前，在磁盘、内核缓冲区和用户缓冲区中被拷贝了多次。

零拷贝通过消除这些冗余的数据拷贝而提高了性能。

![使用 transferTo() 方法的数据拷贝.gif](https://raw.githubusercontent.com/wanggnim/website/images/net/transferTo_copy.gif)
![使用 transferTo() 方法的上下文切换.gif](https://raw.githubusercontent.com/wanggnim/website/images/net/transferTo_context.gif)

1. transferTo() 方法引发 DMA 引擎将文件内容拷贝到一个读取缓冲区。然后由内核将数据拷贝到与输出套接字相关联的内核缓冲区。

2. 数据的第三次复制发生在 DMA 引擎将数据从内核套接字缓冲区传到协议引擎时。
	改进的地方：我们将上下文切换的次数从四次减少到了两次，将数据复制的次数从四次减少到了三次(其中只有一次涉及到了 CPU)。但是这个代码尚未达到我们的零拷贝要求。如果底层网络接口卡支持收集操作 的话，那么我们就可以进一步减少内核的数据复制。在 Linux 内核 2.4 及后期版本中，套接字缓冲区描述符就做了相应调整，以满足该需求。这种方法不仅可以减少多个上下文切换，还可以消除需要涉及 CPU 的重复的数据拷贝。对于用户方面，用法还是一样的，但是内部操作已经发生了改变：

A. transferTo() 方法引发 DMA 引擎将文件内容拷贝到内核缓冲区。
B. 数据未被拷贝到套接字缓冲区。取而代之的是，只有包含关于数据的位置和长度的信息的描述符被追加到了套接字缓冲区。DMA 引擎直接把数据从内核缓冲区传输到协议引擎，从而消除了剩下的最后一次 CPU 拷贝。

![结合使用 transferTo() 和收集操作时的数据拷贝.gif](https://raw.githubusercontent.com/wanggnim/website/images/net/transferTo_collect.gif)


