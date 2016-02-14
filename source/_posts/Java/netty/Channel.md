category: Java
tag: Netty
date: 2015-11-23
title: Netty Channel
---
`Channel`是Netty网络抽象类. 它的功能包括网络IO的读写,链路的连接和关闭, 通信双方的通信地址等.

下面我们看一下Channel提供的API

* `parent()` : 获取父Channel
* `unsafe()` : 
* `localAddress()` : 当前Channel的本地绑定地址
* `eventLoop()` : 当前Channel注册到的EventLoop对象
* `config()` : 获取当前Channel的配置信息
* `remoteAddress()` : 当前Channel通信的远程Socket地址
* `metadata()` : 当前Channel的元数据描述信息,例如TCP参数等等
* `isOpen()` : 判断当初Channel是否已经打开
* `isWritable()` : 当前Channel是否可写
* `isRegistered()` : 是否注册当EventLoop上
* `isActive()` : 当前Channel是否处于激活状态
* `pipeline()` : 当前Channel的ChannelPipeline对象

下面的网络IO操作会直接调用ChannelPipeline里的方法, 在ChannelPipeline里进行事件传播

* `read()` : 从Channel中读取数据到inbound缓冲区
* `write()` : 将消息通过ChannelPipeline写入到目标Channel中
* `close()` : 主动关闭与网络对端的连接
* `flush()` : 将之前写到环形队列里的消息全部写到目标Channel中,发送给网络对端
* `connect()` : 与网络对端发起连接请求(一般由客户端调用这个方法)
* `bind()` : 
* `disconnect()` : 请求关闭与网络对端的连接.



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
* `unsafe` : 真实网络IO的操作类
* `pipeline` : 当前Channel对应的ChannelPipeline
* `succeededFuture` : 
* `voidPromise` : 
* `unsafeVoidPromise` : 
* `closeFuture` : 
* `localAddress` : 本地IP地址
* `remoteAddress` : 网络通信对端的IP地址
* `eventLoop` : 该Channel注册到的EventLoop
* `registered` : Channel是否注册到了EventLoop上

通过看`AbstractChannel`的源码我们可以看到了,除了像`pipeline(), eventLoop(), remoteAddress(), newPromise()`这类的方法外, 在刚开始我们提到的与网络IO操作相关的方法会直接调用ChannelPipleline里的方法,由ChannelPipleline里对应的ChannelHandler进行处理, 例如
```java
@Override
    public ChannelFuture disconnect(ChannelPromise promise) {
        return pipeline.disconnect(promise);
    }

    @Override
    public ChannelFuture close(ChannelPromise promise) {
        return pipeline.close(promise);
    }
    
    @Override
    public ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
        return pipeline.bind(localAddress, promise);
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
        return pipeline.connect(remoteAddress, promise);
    }
    
   @Override
    public Channel read() {
        pipeline.read();
        return this;
    }

    @Override
    public ChannelFuture write(Object msg) {
        return pipeline.write(msg);
}
```
这个类还为我们提供了一些抽象方法,用于网络连接处理,这些抽象方法API在Unsafe里使用
```java
protected abstract void doBeginRead() throws Exception;

    /**
     * Flush the content of the given buffer to the remote peer.
     */
    protected abstract void doWrite(ChannelOutboundBuffer in) throws Exception;
    protected void doRegister() throws Exception {
        // NOOP
    }

    /**
     * Bind the {@link Channel} to the {@link SocketAddress}
     */
    protected abstract void doBind(SocketAddress localAddress) throws Exception;

    /**
     * Disconnect this {@link Channel} from its remote peer
     */
    protected abstract void doDisconnect() throws Exception;

    /**
     * Close the {@link Channel}
     */
    protected abstract void doClose() throws Exception;
```

## AbstractNioChannel
`AbstractNioChannel`主要是实现了`AbstractChannel`的``方法

下面我们还是先看看其内部定义的变脸
```java
private final SelectableChannel ch;
protected final int readInterestOp;
volatile SelectionKey selectionKey;
```
`java.nio.channels.ServerSocketChannel`和`java.nio.channels.SocketChannel`都是实现了`java.nio.channels.SelectableChannel`接口,而且`NioSocketChannel`和`NioServerSocketChannel`都是实现了`AbstractNioChannel`接口,因此我们在`AbstractNioChannel`内定义了一个`SelectableChannel`属性用于实现`ServerSocketChannel`和`SocketChannel`的共用

然后我们看一下`doRegister()`方法

```java
 @Override
    protected void doRegister() throws Exception {
        boolean selected = false;
        for (;;) {
            try {
                selectionKey = javaChannel().register(eventLoop().selector, 0, this);
                return;
            } catch (CancelledKeyException e) {
                if (!selected) {
                    // Force the Selector to select now as the "canceled" SelectionKey may still be
                    // cached and not removed because no Select.select(..) operation was called yet.
                    eventLoop().selectNow();
                    selected = true;
                } else {
                    // We forced a select operation on the selector before but the SelectionKey is still cached
                    // for whatever reason. JDK bug ?
                    throw e;
                }
            }
        }
    }
```
    
然后我们看一下`doDeregister()`方法
```java
  @Override
    protected void doDeregister() throws Exception {
        eventLoop().cancel(selectionKey());
    }
```
最后我们看一下`doBeginRead()`方法
```java
@Override
    protected void doBeginRead() throws Exception {
        // Channel.read() or ChannelHandlerContext.read() was called
        if (inputShutdown) {
            return;
        }

        final SelectionKey selectionKey = this.selectionKey;
        if (!selectionKey.isValid()) {
            return;
        }

        readPending = true;

        final int interestOps = selectionKey.interestOps();
        if ((interestOps & readInterestOp) == 0) {
            selectionKey.interestOps(interestOps | readInterestOp);
        }
    }
```

## AbstractNioMessageChannel
TODO

## AbstractNioMessageServerChannel
TODO

## ServerChannel
TODO

## ServerSocketChannel
TODO

## NioServerScketChannel
TODO