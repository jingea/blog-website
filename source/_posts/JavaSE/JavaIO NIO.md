category: JavaSE
date: 2015-11-23
title: JDK4 NIO
---
JDK1.4 引入的NIO(Non-block IO)是为了拟补原来阻塞IO的不足,它提供了高速的,面向块的IO. 

## 缓冲区
在JDK1.4的NIO中所有的数据都是通过缓冲区(Buffer)处理的. 缓冲区本质上就是一个字节数组, 但是JDK还提供了其他种类的缓冲区：
*  ByteBuffer 
*  ByteOrder 
*  CharBuffer
*  DoubleBuffer
*  FloatBuffer         
*  IntBuffer          
*  LongBuffer         
*  MappedByteBuffer   
*  ShortBuffer  

## channel
在JDK1.4的NIO中还引入了如下的Channel     
* DatagramChannel : 数据报相关的Channel
* FileChannel : 文件Channel
* ServerSocketChannel  : 用于接受入站连接的SocketChannel(不可进行读写)
* SocketChannel  : 用于读写数据的Channel

## NIO服务器
使用ServerSocketChannel可以像使用`ServerSocket`一样开发网络服务器
```java
// 创建ServerSocketChannel,监听所有客户端连接
ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

// 绑定监听端口
serverSocketChannel.socket().bind(new InetSocketAddress("localhost", 8085));

// 设置为非阻塞模式
serverSocketChannel.configureBlocking(false);

while (true) {
	SocketChannel ssc = serverSocketChannel.accept();
	if (ssc == null) {
		continue;
	}
	ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
	try {
		ssc.read(byteBuffer);
		System.out.println(new String(byteBuffer.array(), "utf8"));
		ssc.write(byteBuffer);
	} catch (IOException e) {
		e.printStackTrace();
	}
}
```
Select版本
```java
// 创建ServerSocketChannel,监听所有客户端连接
ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

// 绑定监听端口
serverSocketChannel.socket().bind(new InetSocketAddress("localhost", 8085));

// 设置为非阻塞模式
serverSocketChannel.configureBlocking(false);

// 创建多路复用器
Selector selector = Selector.open();

// 将ServerSocketChannel注册到多路复用器上, 监听accept事件. 然后Selector会不断的轮询(基于系统的select/poll)
// SocketChannel是否有新的连接到达达到，selectedKeys()方法就会将准备就绪的连接作为一个集合返回.
// 在这里我们只能注册accept事件,其他的读写事件我们要在
// accept之后获得的SocketChannel上注册Selector,进行读写事件注册. 也就是说我们不能在ServerSocketChannel上进行读写,
// 我们只能在SocketChannel上进行读写
serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

while (true) {
	// 进行多路复用,这里对Selector进行复用,复用是对一个ServerSocketChannel和多个SocketChannel进行复用.
	selector.select();
	selector.selectedKeys().forEach(selectionKey -> {
		if (selectionKey.isConnectable()) {
			System.out.println("isConnectable");
		}

		if (selectionKey.isAcceptable()) {
			// 由于在Selector上是对ServerSocketChannel进行的accept事件监听,因此此处,我们需要将Channel转换成ServerSocketChannel
			ServerSocketChannel ssc = (ServerSocketChannel) selectionKey.channel();
			try {
				SocketChannel socketChannel = ssc.accept();
				socketChannel.configureBlocking(false);
				// 此处我们将accept的SocketChannel注册到Selector上, 进行读写处理
				socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (selectionKey.isReadable()) {
			// selectionKey.isAcceptable()这个判断中我们将SocketChannel注册到Selector上接受读事件,
			// 因此我们在此处需要将Channel转换成SocketChannel
			SocketChannel ssc = (SocketChannel) selectionKey.channel();
			ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
			try {
				ssc.read(byteBuffer);
				System.out.println(new String(byteBuffer.array(), "utf8"));
				ssc.write(byteBuffer);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (selectionKey.isWritable()) {
			System.out.println("isWritable");
		}

		if (selectionKey.isValid()) {
			//
			System.out.println("isValid");
		}


	});
}
```

我们看一下`Selector`

``是`SelectableChannel`的多路复用器. 

我们可以通过调用`Selector#open()`方法来创建一个selector. 当这个selector创建出来之后, 当我们调用`Selector#close()`方法之前, 该selector会一直存在.



























