category: JavaSE
tag: JAVA NET
date: 2014-09-22
title: Java Socket
---

## socket api

### `Socket(String, int, InetAddress, int)`
这个构造函数创建一个指向指定主机指定端口的TCP socket,并尝试连接远程socket.它连接到前俩个参数指定的主机上,从后俩个参数指定的本地网络接口和端口进行连接.本地网络接口
可以是物理的(如不同的以太网卡)或者虚拟的(一个多宿主主机).如果本地端口为0,会从1024-65535之间随机可用端口.之所以希望显示选择本地地址,这种情况可能出现在使用双以太网端口的路由器/防火墙上.入站连接会在一个接口接受,处理并通过另一个接口转发到本地网络.

> `Socket(InetAddress, int, InetAddress, int)`作用与`Socket(String, int, InetAddress, int)`相同,只不过连接地址是通过InteAdderss进行连接

### `Socket()`
如果要要派生`Socket`子类或者实现一种特殊的socket,从而能加密事务或者理解本地代理服务器,此时就要用到这个函数.新的socket类的大部分实现要在一个`SocketImpl`对象中编写.该构造函数安装默认的`SocketImpl`(来自工厂方法或者`PlainSocketImpl`).

### `Socket(String, int)`

这个构造函数创建一个指向指定主机指定端口的TCP socket,并尝试连接远程socket.

### `Socket(InetAddress, int)`
 
这个构造函数创建一个指向指定主机指定端口的TCP socket,并尝试连接远程socket.在很少的情况下,当你向相同的主机打开很多socket时,先将主机名转换为InetAddress,然后重复使用此InetAddress是更有效的.

### `connect(SocketAddress)`
 
我们一般使用INetSocketAddress

### `close()`
对于socket敏感的程序,在垃圾回收器介入之前,系统会很快达到所能打开socket的上限.关闭一个`Socket`之后,其`InetAddress`,端口号,本地地址,本地端口号仍然可以通过`getInetAddress(),getPort(),getLocalAddress(),getLocalPort()`

方法访问.不过虽然扔可以调用`getInputStream`或`getOutputStream()`,但试图从`InputStream`读取数据或者`OutputStream`写入数据抛出一个`IOException`异常.

### `getInputStream()`

 
返回一个输入流,可以将socket的数据读入程序

### `getPort()`
获取Socket连接(或过去连接或将要连接)远程主机的哪个端口返回一个原始的`OutputStream`,用于将应用程序的数据写入socket另一端.出于性能原因,将其缓冲也是个好主意.

### `getInetAddress()`
socket连接哪台远程服务器,或者当连接已关闭时,告知它连接时所连接的是哪台主机

### `getLocalAddress()`

获取socket绑定于哪个网络接口,一般会在多宿主主机或有多个网络接口的主机使用此方法

### `setTcpNoDelay(boolean)`
设置`TCP_NODELAY`为`true`,可确保包会尽快地发送,而无论包的大小.正常情况下,小的包(1byte)在发送前会组合为大点的包.在发送另一个包之前,本地主机要等待远程系统对前一个包的响应,这称为`Nagle`算法.`Nagle`算法的问题是,如果远程系统没有尽可能快地将回应发送回本地系统,那么依赖于小数据量信息稳定传输的应用程序会变得很慢.为true关闭socket缓冲,为false再次打开socket缓冲. 


### `setSoLinger(boolean, int)`
该设置规定了当socket关闭时如何处理尚未发送的数据报.如果socket关闭(close方法)系统仍会将剩余的数据发送出去.如果延迟时间为0,那所有未发送的数据都会被丢弃.如果延迟时间为任意正数,close方法会被堵塞指定秒数,等待数据发送和接受回应,该段时间过去后socket被关闭,将会关闭输出输入流,既不会接收到数据也不会在发送数据. 

### `sendUrgentData(int)`


### `setOOBInline(boolean)`
用于发送紧急数据

### `setSoTimeout(int)`
当socket尝试读取数据时,`read`方法会阻塞尽可能长的时间来得到足够的字节.该选项就是确保此次调用阻塞的时间不会大于某个固定的毫秒数,如果阻塞时间长于固定毫秒数就会抛出`InterruptedIoException`.尽管抛出了该异常但是socket仍然是连接的.此次read失败,但是仍然可以尝试再次读取该socket

### `setSendBufferSize(int)`
设置socket网络输出的缓冲区字节数

### `setReceiveBufferSize(int)`
设置socket网络输入的缓冲区的字节数.大多数TCP栈使用缓冲区提升网络性能,较大的缓冲区会提升快速连接(比如10M或更快)的网络性能,而较慢的拨号连接在较小的缓冲区下表现更加.一般来讲,传输大的连续的数据块(在FTP和HTTP很常见),这可以从大缓冲区收益;而大缓冲区对交互式会话如`telnet`和许多游戏则没有多大帮助.

### `setKeepAlive(boolean)`
启用`SO_KEEPALIVE`客户端会偶尔(一般俩个小时)利用一个空闲连接发送一个数据包,确保服务器没有崩溃.如果服务器没有响应,客户端会在11分钟之内持续发送此包,直到接受到服务器的回馈或者到12分钟左右直接将客户端关闭. 

### `setTrafficClass(int)`


### `setReuseAddress(boolean)`
 
设置主机地址可重用

### `getReuseAddress()`

socket关闭时,可能不会立即释放本地地址,一般会等待一段时间,确保所有寻址到待端口在网络上传输的数据接受到.关闭后一般接收到的数据报不会再进行任何处理,这么做是为了当有新的进程关联到该端口的时候不会接受到莫名其妙的数据.要想使用这个设置必须将老的socket如下设置

### `shutdownInput()`

半关闭连接.关闭socket的输入流,实际上这并不会关闭socket,但是它会影响与之连接的流认为已经到了流的末尾.即使半关闭了连接,甚至关闭了俩次,仍需要在结束使用时关闭socket.该方法只影响输出流,他们不释放与socket关联的资源,如占用的端口.

### `shutdownOutput()`



### `isConnected()`
指向远端服务器.这个方法不能告诉你socket当前是否连接到远程主机,而是告知socket是否曾经连接过主机.如果socket能够连接到远程主机,则此主机返回为`true`,即使此时socket已被关闭.要判断socket当前是否打开,需要检查`isConnected()`返回`true`,并且`isClosed()`返回`false`.

### `isBound()`

 指的是本地端,它告知socket是否绑定于本地系统的向外端口.

### `isClosed()`

 在socket已经关闭时返回`true`,未关闭时返回`false`.但是如果socket从未连接过也降返回`false`.

### `isInputShutdown()`



### `isOutputShutdown()`



### `setPerformancePreferences(int, int, int)`


## ServerSocket api
socket实现进程通信(实质上提供了进程通信的端点). 每一个socket用一个半相关描述:(协议，本地地址，本地端口). 一个完整的socket有一个本地唯一的socket号，由操作系统分配。于一个网络连接来说，套接字是平等的，并没有差别，不因为在服务器端或在客户端而产生不同级别.

适用于在绑定端口前设置服务器socket选项
```
ServerSocket socket = new ServerSocket();
// 设置socket选项
SocketAddress address = new InetSocketAddress(port);
socket.bind(address); // 绑定端口
```

在当前主机的所有网络接口或者所有IP地址的指定port上进行入站监听,如果port为0系统会随意指定一个端口
```
new ServerSocket(port);
```

在当前主机的所有网络接口或者所以IP地址的该指定port上进行入站监听.如果port为0系统会随意指定一个端口,queueLenght设置入栈请求的队列长度(如果队列超过最大值,会使用系统最大值)

第二个参数为backlog参数,accept()方法，该方法从队列中取出连接请求，使得队列能够及时的腾出空间，以容纳新的连接请求。 即ServerSocket构造函数中的backlog参数时，是可以serverSocket在不调用accept方法取出连接时，能接受的最大连接数 
```
return new ServerSocket(port, queueLenght);
```

在当前主机的指定的IP地址的指定port上进行入站监听(适用于多IP地址系统上运行的服务器)如果port为0系统会随意指定一个端口queueLenght设置入栈请求的队列长度(如果队列超过最大值,会使用系统最大值)
```
new ServerSocket(port, queueLenght, address);
```

关闭ServerSocket ：释放本地主机的绑定端口,允许其他程序继续使用释放掉的端口。终端ServerSocket接受的目前处于打开状态的所以Socket 关闭Socket .尽管在程序结束时ServerSocket会自动关闭但是尽量还是在程序中保证,当ServerSocket结束时将其手动关闭
```
connect.close();
```

检查ServerSocket是否打开.当关闭后isClosed会返回true, isBound指的是是否曾经绑定过端口,但是并不指现在的状态

```
if(!connect.isClosed() && connect.isBound())
	return false;
```

该选项是在accept抛出java.ioInterruptedIOException前等待入栈连接的时间,以毫秒计
```
setSoTimeout(timeout)
```

指定如果仍有旧的数据在网络上传输,新的程序是否可以绑定到该端口
```
connect.setReuseAddress(isSet);
```


这相当于调用accept()返回的socket的socket.setReceiveBufferSize(size);可以在绑定服务器socket之前或之后设置此选项 除非要设置大于64K的缓冲区大小,这时对于未绑定的ServerSocket必须在绑定他之前设置这个选项
```
public static void setRcvBuf(ServerSocket connect) {
	int size;
	try {
		size = connect.getReceiveBufferSize();
		if(size < 131072)
			connect.setReceiveBufferSize(131072);
		connect.bind(new InetSocketAddress(8080));
	} catch (final Exception e) {
		e.printStackTrace();
	}
	
}
```	
