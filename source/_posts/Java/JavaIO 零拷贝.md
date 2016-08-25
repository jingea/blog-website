category: Java
date: 2016-08-16
title: Java 中的零拷贝
---

## 传统方法数据传输

![传统的数据拷贝方法.gif](https://raw.githubusercontent.com/wanggnim/website/images/net/transfer_copy.jpg)
![传统上下文切换.gif](https://raw.githubusercontent.com/wanggnim/website/images/net/transfer_context.gif)

1. read() 调用引发了一次从用户模式到内核模式的上下文切换.在内部,发出 `sys_read()`(或等效内容)以从文件中读取数据.直接内存存取(`direct memory access`,DMA)引擎执行了第一次拷贝,它从磁盘中读取文件内容,然后将它们存储到一个内核地址空间缓存区中.
2. 所需的数据被从读取缓冲区拷贝到用户缓冲区,read() 调用返回.该调用的返回引发了内核模式到用户模式的上下文切换(又一次上下文切换).现在数据被储存在用户地址空间缓冲区.
3. `send()` 套接字调用引发了从用户模式到内核模式的上下文切换.数据被第三次拷贝,并被再次放置在内核地址空间缓冲区.但是这一次放置的缓冲区不同,该缓冲区与目标套接字相关联.
4. `send()` 系统调用返回,结果导致了第四次的上下文切换.DMA 引擎将数据从内核缓冲区传到协议引擎,第四次拷贝独立地、异步地发生 .

使用中间内核缓冲区(而不是直接将数据传输到用户缓冲区)看起来可能有点效率低下.但是之所以引入中间内核缓冲区的目的是想提高性能.在读取方面使用中间内核缓冲区,可以允许内核缓冲区在应用程序不需要内核缓冲区内的全部数据时,充当 “预读高速缓存(readahead cache)” 的角色.这在所需数据量小于内核缓冲区大小时极大地提高了性能.在写入方面的中间缓冲区则可以让写入过程异步完成.

## 零拷贝
Java 类库通过 `java.nio.channels.FileChannel` 中的 `transferTo()` 方法来在 Linux 和 UNIX 系统上支持零拷贝.可以使用 `transferTo()` 方法直接将字节从它被调用的通道上传输到另外一个可写字节通道上,数据无需流经应用程序.本文首先展示了通过传统拷贝语义进行的简单文件传输引发的开销,然后展示了使用 `transferTo()` 零拷贝技巧如何提高性能.

不幸的是,如果所需数据量远大于内核缓冲区大小的话,这个方法本身可能成为一个性能瓶颈.数据在被最终传入到应用程序前,在磁盘、内核缓冲区和用户缓冲区中被拷贝了多次.零拷贝通过消除这些冗余的数据拷贝而提高了性能.

![使用 transferTo() 方法的数据拷贝.gif](https://raw.githubusercontent.com/wanggnim/website/images/net/transferTo_copy.gif)
![使用 transferTo() 方法的上下文切换.gif](https://raw.githubusercontent.com/wanggnim/website/images/net/transferTo_context.gif)

1. transferTo() 方法引发 DMA 引擎将文件内容拷贝到一个读取缓冲区.然后由内核将数据拷贝到与输出套接字相关联的内核缓冲区.

2. 数据的第三次复制发生在DMA引擎将数据从内核套接字缓冲区传到协议引擎时.改进的地方：我们将上下文切换的次数从四次减少到了两次,将数据复制的次数从四次减少到了三次(其中只有一次涉及到了CPU).但是这个代码尚未达到我们的零拷贝要求.如果底层网络接口卡支持收集操作的话,那么我们就可以进一步减少内核的数据复制.在Linux内核2.4及后期版本中,套接字缓冲区描述符就做了相应调整,以满足该需求.这种方法不仅可以减少多个上下文切换,还可以消除需要涉及CPU的重复的数据拷贝.对于用户方面,用法还是一样的,但是内部操作已经发生了改变：

A. transferTo() 方法引发 DMA 引擎将文件内容拷贝到内核缓冲区.
B. 数据未被拷贝到套接字缓冲区.取而代之的是,只有包含关于数据的位置和长度的信息的描述符被追加到了套接字缓冲区.DMA 引擎直接把数据从内核缓冲区传输到协议引擎,从而消除了剩下的最后一次 CPU 拷贝.

![结合使用 transferTo() 和收集操作时的数据拷贝.gif](https://raw.githubusercontent.com/wanggnim/website/images/net/transferTo_collect.gif)

[linux下零拷贝技术介绍](http://www.jb51.net/LINUXjishu/86937.html)
