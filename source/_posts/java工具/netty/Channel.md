category: java工具
tag: netty
date: 2015-11-23
title: 初探Channel
---
`Channel`是Netty网络抽象类. 它的功能包括网络IO的读写,链路的连接和关闭, 通信双方的通信地址等.

下面我们看一下Channel提供的API
* `parent()` : 获取父Channel
* `isRegistered()` : 是否注册
* `write()` : 将消息通过ChannelPipeline写入到目标Channel中
* `connect()` : 建立连接
* `read()` : 从Channel中读取数据到inbound缓冲区
* `close()` : 关闭连接
* `flush()` : 
* `unsafe()` : 
* `isOpen()` : 
* `bind()` : 
* `localAddress()` : 
* `eventLoop()` : 
* `config()` : 
* `newProgressivePromise()` : 
* `newSucceededFuture()` : 
* `remoteAddress()` : 
* `metadata()` : 
* `isWritable()` : 
* `closeFuture()` : 
* `isActive()` : 
* `pipeline()` : 
* `alloc()` : 
* `newPromise()` : 
* `newFailedFuture()` : 
* `voidPromise()` : 
* `disconnect()` : 
* `deregister()` : 
* `writeAndFlush()` : 
* `attr()` : 
* `compareTo()` :

## AbstractChannel
`AbstractChannel`聚合了所有Channel使用到的能力的对象. 如果某个功能和子类相关则定义抽象方法,由子类去实现
```java
static final ClosedChannelException CLOSED_CHANNEL_EXCEPTION = new ClosedChannelException();
static final NotYetConnectedException NOT_YET_CONNECTED_EXCEPTION = new NotYetConnectedException();

static {
    CLOSED_CHANNEL_EXCEPTION.setStackTrace(EmptyArrays.EMPTY_STACK_TRACE);
    NOT_YET_CONNECTED_EXCEPTION.setStackTrace(EmptyArrays.EMPTY_STACK_TRACE);
}

private MessageSizeEstimator.Handle estimatorHandle;

private final Channel parent;
private final Unsafe unsafe;
private final ChannelPipeline pipeline;
private final ChannelFuture succeededFuture = new SucceededChannelFuture(this, null);
private final VoidChannelPromise voidPromise = new VoidChannelPromise(this, true);
private final VoidChannelPromise unsafeVoidPromise = new VoidChannelPromise(this, false);
private final CloseFuture closeFuture = new CloseFuture(this);

private volatile SocketAddress localAddress;
private volatile SocketAddress remoteAddress;
private volatile EventLoop eventLoop;
private volatile boolean registered;

/** Cache for the string representation of this channel */
private boolean strValActive;
private String strVal;
```
* `CLOSED_CHANNEL_EXCEPTION` : 链路已经关闭异常
* `NOT_YET_CONNECTED_EXCEPTION` : 链路尚未连接异常
* `parent` : 该Channel的父Channel
* `estimatorHandle` : 用于预测下一个报文的大小.
* `unsafe` : 
* `pipeline` : 
* `succeededFuture` : 
* `voidPromise` : 
* `unsafeVoidPromise` : 
* `closeFuture` : 
* `localAddress` : 
* `remoteAddress` : 
* `eventLoop` : 该Channel注册到的EventLoop
* `registered` :  

