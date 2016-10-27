category: Java Library
tag: Netty
date: 2015-11-23
title: Netty Channel
---
`Channel`是Netty网络抽象类. 它的功能包括网络IO的读写,链路的连接和关闭, 通信双方的通信地址等.
![](https://raw.githubusercontent.com/ming15/blog-website/images/netty/channel.jpg)

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
我们首先看一下`AbstractChannel`里定义的成员
```java
// 链路已经关闭异常
static final ClosedChannelException CLOSED_CHANNEL_EXCEPTION = new ClosedChannelException();
// 链路尚未连接异常
static final NotYetConnectedException NOT_YET_CONNECTED_EXCEPTION = new NotYetConnectedException();

static {
    CLOSED_CHANNEL_EXCEPTION.setStackTrace(EmptyArrays.EMPTY_STACK_TRACE);
    NOT_YET_CONNECTED_EXCEPTION.setStackTrace(EmptyArrays.EMPTY_STACK_TRACE);
}

// 用于预测下一个报文的大小.
private MessageSizeEstimator.Handle estimatorHandle;

private final Channel parent;
private final Unsafe unsafe;
private final ChannelPipeline pipeline;
private final ChannelFuture succeededFuture = new SucceededChannelFuture(this, null);
private final VoidChannelPromise voidPromise = new VoidChannelPromise(this, true);
private final VoidChannelPromise unsafeVoidPromise = new VoidChannelPromise(this, false);
private final CloseFuture closeFuture = new CloseFuture(this);

// 本地IP地址
private volatile SocketAddress localAddress;
// 网络通信对端的IP地址
private volatile SocketAddress remoteAddress;
private volatile EventLoop eventLoop;
// Channel是否注册到了EventLoop上
private volatile boolean registered;

/** Cache for the string representation of this channel */
private boolean strValActive;
private String strVal;
```
`AbstractChannel`聚合了所有Channel使用到的能力的对象. 如果某个功能和子类相关则定义抽象方法,由子类去实现.

在这里我们主要关注三个变量
* `unsafe` : 真实网络IO的操作类
* `pipeline` : 当前Channel对应的ChannelPipeline. 负责
* `eventLoop` : 该Channel注册到的EventLoop
在实例化的时候, 会对`pipeline`和`unsafe`进行赋值.
```java
protected AbstractChannel(Channel parent) {
      this.parent = parent;
      unsafe = newUnsafe();
      pipeline = new DefaultChannelPipeline(this);
}
```
> unsafe实例化由子类实现, 这是因为unsafe的类型是个Unsafe接口, 而且AbstractChannel的内部类AbstractUnsafe是个抽象类, 那么我们就不知道如果要实例化这个类型究竟要使用哪个类型, 因此让AbstractChannel的子类继续实现自己的Unsafe接口的内部类和newUnsafe()方法, unsafe实质类型就有很大的可扩展性

我们看到每一个Channel都有一个自己的`pipeline`和`unsafe`. `eventLoop`是在`AbstractUnsafe`中`register()`方法调用时进行赋值的
```java
public final void register(EventLoop eventLoop, final ChannelPromise promise) {
        AbstractChannel.this.eventLoop = eventLoop;
}
```
`AbstractChannel`完成的功能很少, 只是实现了一些初始化的工作, 然后将网络相关的建立,数据读写操作等交给`pipeline`来完成.
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

Override
public Channel read() {
    pipeline.read();
    return this;
}

@Override
public ChannelFuture write(Object msg) {
    return pipeline.write(msg);
}
```
还提供了一个`unsafe()`方法
```java
public Unsafe unsafe() {
   return unsafe;
}
```
我们看一下`AbstractUnsafe`的定义`protected abstract class AbstractUnsafe implements Unsafe`, 它是作为一个`AbstractChannel`的抽象内部类, 这种关系也很容易让`AbstractUnsafe`访问`AbstractChannel`定义的一些空实现方法. 例如`AbstractUnsafe`中调用`AbstractChannel`的方法如下
* `beginRead()` -> `doBeginRead()`
* `doBind()` -> `doBind()`
* `doDisconnect()` -> `doDisconnect()()`
* `doClose()` -> `doClose()`
* `register()` -> `doRegister()`以及调用pipeline的相关方法(fireChannelRegistered()和fireChannelActive())


## AbstractNioChannel
`AbstractNioChannel`主要是实现了`AbstractChannel`的`doRegister(), doDeregister(), doBeginRead()`方法. 通过下面的变量我们也可以看出这个类主要是为了完成`SelectableChannel`向`Selector`的注册功能.
```java
private final SelectableChannel ch;
protected final int readInterestOp;
volatile SelectionKey selectionKey;
```
`java.nio.channels.ServerSocketChannel`和`java.nio.channels.SocketChannel`都是实现了`java.nio.channels.SelectableChannel`接口. 而`NioSocketChannel`和`NioServerSocketChannel`实现了`AbstractNioChannel`接口, 因此我们在`AbstractNioChannel`内定义了一个`SelectableChannel`成员用于实现`ServerSocketChannel`和`SocketChannel`的共用

然后我们看一下`doRegister()`方法
```java
@Override
protected void doRegister() throws Exception {
    boolean selected = false;
    for (;;) {
        try {
			// 我们将ServerSocketChannel或者SocketChannel注册到NioEventLoop里的Selector上
			// 0表示我们对任何事件Channel里的任何事件都不感兴趣
			// 同时我们将this作为附件传送进去,
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

    // 获取selectionKey的操作位
    final int interestOps = selectionKey.interestOps();
    if ((interestOps & readInterestOp) == 0) {
        // 如果slectionKey不对读事件感兴趣, 那么就修改selectionKey的操作位, 开始设置对读事件感兴趣
        selectionKey.interestOps(interestOps | readInterestOp);
    }
}
```
还记得在`AbstractChannel`中的`AbstractUnsafe`吗?里面有个`beginRead()`, 这个`doBeginRead()`正是由其调用的.

## AbstractNioByteChannel
`AbstractNioByteChannel`内部只有一个`Runnable`类型的`flushTask`属性, 它是用来写半包的, 当我们使用到它的时候,我们再具体分析. 我们来重点看一下`doWrite()`方法
```java
protected void doWrite(ChannelOutboundBuffer in) throws Exception {
       int writeSpinCount = -1;

       for (;;) {
           // 从环形数组ChannelOutboundBuffer中弹出一个消息对象
           Object msg = in.current();
           if (msg == null) {
               // 如果全部消息都发送完毕累,则清除半包标志, clearOpWrite() 内部操作 TODO ???
               clearOpWrite();
               break;
           }

           if (msg instanceof ByteBuf) {
               ByteBuf buf = (ByteBuf) msg;
               int readableBytes = buf.readableBytes();
               if (readableBytes == 0) {
                   // 当前消息没有可读内容, 也就是没有内容需要向外发送,
                   // 则将其从还行数组中删除, 然后继续处理下一个消息
                   in.remove();
                   continue;
               }

               //设置半包标志
               boolean setOpWrite = false;
               // 设置消息是否发送完毕
               boolean done = false;
               // 设置消息发送的总得数量
               long flushedAmount = 0;
               if (writeSpinCount == -1) {
                   // 从配置中我们获取每次写半包消息进行的最大次数. 也即是如果环形数组里的消息一次性发送
                   // 不完, 需要循环发送的次数,至于为什么不一直发送, 这是因为如果网络阻塞或者对方接受数据很慢,可能会造成网络IO线程假死
                   writeSpinCount = config().getWriteSpinCount();
               }
               for (int i = writeSpinCount - 1; i >= 0; i --) {
                   // 将buf内部的数据进行发送, 返回值是数据发送量
                   int localFlushedAmount = doWriteBytes(buf);
                   if (localFlushedAmount == 0) {
                       // 数量为0,说明一个数据都没有发送出去, 可能是TCP缓冲区满了. 因此设置写半包标志
                       // 同时退出写循环,这是因为下次写数据还可能TCP缓冲区处于已满状态,导致IO线程空循环
                       setOpWrite = true;
                       break;
                   }

                   // 数据发送成功, 将发送的数据量累加到flushedAmount上.
                   flushedAmount += localFlushedAmount;
                   if (!buf.isReadable()) {
                       // 当前消息里的数据已经发送完毕, 退出buf发送循环,继续处理环形队列中下一个消息
                       done = true;
                       break;
                   }
               }

               // 将发送的数据量同步到环形队列中
               in.progress(flushedAmount);

               if (done) {
                   // buf数据已经发送完, 则将该消息从环形队列中删除
                   in.remove();
               } else {
                   // 在写半包消息最大循环次数之内都没有将buf数据写完, 可能是数据量太多或者TCP缓冲区已满
                   // 释放当前IO线程,让其进行其他工作.
                   incompleteWrite(setOpWrite);
                   break;
               }
           } else if (msg instanceof FileRegion) {
               FileRegion region = (FileRegion) msg;
               boolean done = region.transfered() >= region.count();
               boolean setOpWrite = false;

               if (!done) {
                   long flushedAmount = 0;
                   if (writeSpinCount == -1) {
                       writeSpinCount = config().getWriteSpinCount();
                   }

                   for (int i = writeSpinCount - 1; i >= 0; i--) {
                       long localFlushedAmount = doWriteFileRegion(region);
                       if (localFlushedAmount == 0) {
                           setOpWrite = true;
                           break;
                       }

                       flushedAmount += localFlushedAmount;
                       if (region.transfered() >= region.count()) {
                           done = true;
                           break;
                       }
                   }

                   in.progress(flushedAmount);
               }

               if (done) {
                   in.remove();
               } else {
                   incompleteWrite(setOpWrite);
                   break;
               }
           } else {
               // Should not reach here.
               throw new Error();
           }
       }
   }
```
`doWrite()`方法是由`AbstractUnsafe`的`flush()`调用的. 从`AbstractUnsafe`我们可以看到每个Unsafe类都有一个`ChannelOutboundBuffer`属性.

下来我们看一下`incompleteWrite()`方法实现
```java
protected final void incompleteWrite(boolean setOpWrite) {
        // 从doWrite()方法中可以看到只有当TCP缓冲区已满的时候才会设置写半包操作
        if (setOpWrite) {
            // 设置累写半包的话,则将SelectionKey注册为OP_WRITE, 让多路复用器不断的轮训对应的Channel,
            // 继续处理没有发送完的消息
            setOpWrite();
        } else {
            // 如果没有半包,则让eventLoop继续执行写半包操作
            Runnable flushTask = this.flushTask;
            if (flushTask == null) {
                flushTask = this.flushTask = new Runnable() {
                    @Override
                    public void run() {
                        flush();
                    }
                };
            }
            eventLoop().execute(flushTask);
        }
    }
```

## AbstractNioMessageChannel

```java
protected void doWrite(ChannelOutboundBuffer in) throws Exception {
       final SelectionKey key = selectionKey();
       final int interestOps = key.interestOps();

       for (;;) {
           // 从环形队列中获取一条消息
           Object msg = in.current();
           if (msg == null) {
               // 消息为空,说明所有的消息都已经发送出去了. TODO
               if ((interestOps & SelectionKey.OP_WRITE) != 0) {
                   key.interestOps(interestOps & ~SelectionKey.OP_WRITE);
               }
               break;
           }
           try {
               boolean done = false;
               for (int i = config().getWriteSpinCount() - 1; i >= 0; i--) {
                   // 在配置的最大次数下,将msg发送出去
                   if (doWriteMessage(msg, in)) {
                       done = true;
                       break;
                   }
               }

               if (done) {
                   // 如果消息发送完毕累, 则将其从环形数组中删除
                   in.remove();
               } else {
                   //  如果没有发送完毕, 则设置SelectionKey为写操作位, 让多路复用器不断的轮训channel,发送剩余的数据
                   if ((interestOps & SelectionKey.OP_WRITE) == 0) {
                       key.interestOps(interestOps | SelectionKey.OP_WRITE);
                   }
                   break;
               }
           } catch (IOException e) {
               if (continueOnWriteError()) {
                   in.remove(e);
               } else {
                   throw e;
               }
           }
       }
   }
```

## NioServerSocketChannel
`NioServerSocketChannel`的主要作用是接受客户端连接
```java
protected int doReadMessages(List<Object> buf) throws Exception {
       SocketChannel ch = javaChannel().accept();

       try {
           if (ch != null) {
               buf.add(new NioSocketChannel(this, ch));
               return 1;
           }
       } catch (Throwable t) {
           logger.warn("Failed to create a new channel from an accepted socket.", t);

           try {
               ch.close();
           } catch (Throwable t2) {
               logger.warn("Failed to close a socket.", t2);
           }
       }

       return 0;
   }
```
这个方法调用主要是由`NioMessageUnsafe`的`read()`方法调用

## NioSocketChannel
