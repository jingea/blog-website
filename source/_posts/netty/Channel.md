category: netty
date: 2015-11-23
title: 初探Channel
---
`Channel`是Netty网络抽象类. 

下面我们看一下Channel提供的API
* `parent()` : 
* `isRegistered()` : 
* `write()` : 
* `write()` : 
* `connect()` : 
* `connect()` : 
* `connect()` : 
* `connect()` : 
* `read()` : 
* `close()` : 
* `close()` : 
* `flush()` : 
* `unsafe()` : 
* `isOpen()` : 
* `bind()` : 
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
* `disconnect()` : 
* `deregister()` : 
* `deregister()` : 
* `writeAndFlush()` : 
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
