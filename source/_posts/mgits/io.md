category: mgits
date: 2015-09-08
title: IO
---

## IO概念
linux内核将内分分成内核区和用户区俩个区域：
1. 用户区进程A向内核发起一个读文件的系统调用(进程A阻塞)
2. linux在内核区生成一个文件句柄fd(该文件句柄指向了一个内核缓冲区)
3. linux通过驱动程序向磁盘读取指定文件
4. 驱动程序将读取到的数据存储在内核区的缓冲区内
5. 当内核缓冲区的大小大于进程A设置的读取文件的大小时发生读就绪,内核缓冲区的数据刷新到用户区内

## IO类型

### 阻塞IO
在进程空间中调用recvfrom,其系统调用直到数据报到达且被拷贝到应用进程的缓冲区中或者发生错误才返回,期间一直在等待.我们就说进程在从调用recvfrom开始到它返回的整段时间内是被阻塞的.
![阻塞IO.jpg](https://raw.githubusercontent.com/wanggnim/website/images/net/阻塞IO.jpg)

### 非阻塞IO
这种情况下用户空间的进程不断地向内核空间轮询是否有数据到达,如果没有达到不进入阻塞(睡眠)而是直接返回一个错误.
![非阻塞IO.jpg](https://raw.githubusercontent.com/wanggnim/website/images/net/非阻塞IO.jpg)
这种操作效率是及其低下的,因为它把密集型IO操作变成了IO密集和计算密集的操作.

### SIGIO
首先开启套接口信号驱动I/O功能,并通过系统调用sigaction安装一个信号处理函数(此系统调用立即返回,进程继续工作,它是非阻塞的).当数据报准备好被读时,就为该进程生成一个SIGIO信号.随即可以在信号处理程序中调用recvfrom来读数据报,井通知主循环数据已准备好被处理中.也可以通知主循环,让它来读数据报.
![信号驱动IO.jpg](https://raw.githubusercontent.com/wanggnim/website/images/net/信号驱动IO.jpg)

### IO复用
在`select/poll`上注册多个`fd`, 然后`select/poll`顺序轮询所有的`fd`,如果轮询到某个`fd`读就绪或者写就绪就取出该`fd`进行读写数据,但是由于`select/poll`支持的`fd`数量有限而且是顺序扫描的,因此它的性能仍然会达不到要求. 于是`epoll`出现了,它是基于事件驱动方式,而不是顺序扫描,当有fd就绪时,立即回调函数rollback；
![IO复用.jpg](https://raw.githubusercontent.com/wanggnim/website/images/net/IO复用.jpg)

### windows的IOCP
告知内核启动某个操作,并让内核在整个操作完成后(包括将数据从内核拷贝到用户自己的缓冲区)通知我们.这种模型与信号驱动模型的主要区别是：
* 信号驱动I/O：由内核通知我们何时可以启动一个I/O操作；
* 异步I/O模型：由内核通知我们I/O操作何时完成.
![异步IO.jpg](https://raw.githubusercontent.com/wanggnim/website/images/net/异步IO.jpg)

