category: nio
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
* Channels          
* DatagramChannel     
* FileChannel         
* MembershipKey     
* Pipe          
* SelectableChannel 
* SelectionKey      
* Selector          
* ServerSocketChannel   只有一个用途--接受入站连接 它是无法读取,写入或者连接的
* SocketChannel  

## NIO服务器
