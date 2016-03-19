category: Netty
date: 2016-03-19
title: Netty Unsafe
---
首先我们来看一下`Unsafe`的继承
![](https://raw.githubusercontent.com/ming15/blog-website/images/netty/unsafe.jpg)

## Unsafe
`Unsafe`是`Channel`的内部接口, 它定义了下面的相关功能

* `localAddress()` : 获得本地绑定的`SocketAddress`对象
* `remoteAddress()` : 返回与网络对等端绑定的地址`SocketAddress`
* `register()` : 将`EventLoop`注册到`Channel`上, 一旦注册成功就返回`ChannelFuture`
* `bind()` : 将`SocketAddress`绑定到`Channel`上.
* `connect()` : `Channel`与对端的`SocketAddress`进行连接
* `disconnect()` : `Channel`与网络对端断开连接
* `close()` : 关闭`Channel`与网络对端的连接
* `deregister()` : `Channel`与`EventLoop`解除注册关系
* `beginRead()` : Schedules a read operation that fills the inbound buffer of the first {@link ChannelInboundHandler} in the {@link ChannelPipeline}.  If there's already a pending read operation, this method does nothing.
* `write()` : 调度一个写操作
* `flush()` : 通过`write()`将全部的写操作进行调用
* `outboundBuffer()` : Returns the {@link ChannelOutboundBuffer} of the {@link Channel} where the pending write requests are stored.

## AbstractUnsafe
`AbstractUnsafe`是`AbstractChannel`的内部类, 主要是提供了对`AbstractChannel`的辅助功能, 它内部实现了N个, 这些方法最终都会调用`AbstractChannel`子类实现的`doXXX()`相关方法. 例如:
* `register()` -> `doRegister()`(`AbstractNioChannel`实现), 调用完成之后调用pipline的`fireChannelRegistered()`和`fireChannelActive()`.
* `bind()` -> `doBind()`
* `disconnect()` -> `doDisconnect()`
* `close()` -> `doClose()`
* `deregister` -> `doDeregister()`(`AbstractNioChannel`实现)
* `beginRead()` -> `doBeginRead()`(`AbstractNioChannel`实现)
* `flush()` -> `doWrite()`

需要仔细看一下的是`AbstractUnsafe`内部消息存储的一个环形数组`ChannelOutboundBuffer`成员
```java
private ChannelOutboundBuffer outboundBuffer = new ChannelOutboundBuffer(AbstractChannel.this);
```
下来我们看一下它的`write()`方法
```java
@Override
        public final void write(Object msg, ChannelPromise promise) {
            ChannelOutboundBuffer outboundBuffer = this.outboundBuffer;
            if (outboundBuffer == null) {
                // outboundBuffer为空, 说明channel已经关闭了, 那么现在就需要立刻做快速失败处理
                safeSetFailure(promise, CLOSED_CHANNEL_EXCEPTION);
                // 将消息释放掉, 避免发生内存泄漏
                ReferenceCountUtil.release(msg);
                return;
            }

            int size;
            try {
                msg = filterOutboundMessage(msg);
                size = estimatorHandle().size(msg);
                if (size < 0) {
                    size = 0;
                }
            } catch (Throwable t) {
                safeSetFailure(promise, t);
                ReferenceCountUtil.release(msg);
                return;
            }

            // 我们看到, 在写消息的时候,最后就是将msg写到了一个环形数组里
            outboundBuffer.addMessage(msg, size, promise);
        }
```
将消息写到`outboundBuffer`之后, 最终我们还是需要调用`flush()`将其真正刷到TCP中
```java
@Override
        public final void flush() {
            ChannelOutboundBuffer outboundBuffer = this.outboundBuffer;
            outboundBuffer.addFlush();
            flush0();
        }

        protected void flush0() {
            final ChannelOutboundBuffer outboundBuffer = this.outboundBuffer;

            try {
                doWrite(outboundBuffer);
            } catch (Throwable t) {

            } finally {
                inFlush0 = false;
            }
        }

```
我们看到最终的写到channel中也是由`doWrite()`方法实现的.

## AbstractNioUnsafe
`AbstractNioUnsafe`是`AbstactNioChannel`的内部类. 与`AbstractUnsafe`类似, 它也是提供了一些对Channel的代理调用
* `connect()` -> `doConnect()`
* `finishConnect()` -> `doFinishConnect()`

## NioByteUnsafe
我们重点看一下`read()`和`write()`方法

我们首先看一下`read()`方法
```java
@Override
        public void read() {
            final ChannelConfig config = config();
            if (!config.isAutoRead() && !isReadPending()) {
                // ChannelConfig.setAutoRead(false) was called in the meantime
                removeReadOp();
                return;
            }

            final ChannelPipeline pipeline = pipeline();
            final ByteBufAllocator allocator = config.getAllocator();
            // 我们配置的每次读取数据最多读取的数据量
            final int maxMessagesPerRead = config.getMaxMessagesPerRead();
            RecvByteBufAllocator.Handle allocHandle = this.allocHandle;
            if (allocHandle == null) {
                this.allocHandle = allocHandle = config.getRecvByteBufAllocator().newHandle();
            }

            ByteBuf byteBuf = null;
            int messages = 0;
            boolean close = false;
            try {
                // 读取的总量
                int totalReadAmount = 0;
                //
                boolean readPendingReset = false;
                do {
                    // 获取一个byteBuf对象, 用于这次读取数据, 具体的获取策略, 本文最后有介绍
                    byteBuf = allocHandle.allocate(allocator);
                    int writable = byteBuf.writableBytes();
                    // 调用NioSocketChannel的doReadBytes()实现, 将数据读进byteBuf中
                    int localReadAmount = doReadBytes(byteBuf);
                    if (localReadAmount <= 0) {
                        // 如果没有读到数据,则将ByteBuf释放掉
                        byteBuf.release();
                        close = localReadAmount < 0;
                        break;
                    }
                    if (!readPendingReset) {
                        readPendingReset = true;
                        setReadPending(false);
                    }
                    // 开始在pipeline里进行ByteBuf数据传播
                    pipeline.fireChannelRead(byteBuf);
                    byteBuf = null;

                    if (totalReadAmount >= Integer.MAX_VALUE - localReadAmount) {
                        // Avoid overflow.
                        totalReadAmount = Integer.MAX_VALUE;
                        break;
                    }

                    // 统计所有的读到的数据
                    totalReadAmount += localReadAmount;

                    // stop reading
                    if (!config.isAutoRead()) {
                        break;
                    }

                    if (localReadAmount < writable) {
                        // Read less than what the buffer can hold,
                        // which might mean we drained the recv buffer completely.
                        break;
                    }
                    // 如果仍然有未读数据的话, 则继续读取
                } while (++ messages < maxMessagesPerRead);

                // 当前所有数据都读取完了, 触发
                pipeline.fireChannelReadComplete();
                allocHandle.record(totalReadAmount);

                if (close) {
                    closeOnRead(pipeline);
                    close = false;
                }
            } catch (Throwable t) {
                handleReadException(pipeline, byteBuf, t, close);
            } finally {
                // Check if there is a readPending which was not processed yet.
                // This could be for two reasons:
                // * The user called Channel.read() or ChannelHandlerContext.read() in channelRead(...) method
                // * The user called Channel.read() or ChannelHandlerContext.read() in channelReadComplete(...) method
                //
                // See https://github.com/netty/netty/issues/2254
                if (!config.isAutoRead() && !isReadPending()) {
                    removeReadOp();
                }
            }
        }
    }
```

## NioMessageUnsafe
`NioMessageUnsafe`是`AbstractNioMessageChannel`的内部类. 它的`read()`方法与`NioByteMessage`的类似, 只不过这个是用于
服务`NioServerSocketChannel`的, 它内部的`doReadMessages()`会调用的`NioServerSocketChannel`的实现.
```java
@Override
        public void read() {
            assert eventLoop().inEventLoop();
            final ChannelConfig config = config();
            if (!config.isAutoRead() && !isReadPending()) {
                // ChannelConfig.setAutoRead(false) was called in the meantime
                removeReadOp();
                return;
            }

            final int maxMessagesPerRead = config.getMaxMessagesPerRead();
            final ChannelPipeline pipeline = pipeline();
            boolean closed = false;
            Throwable exception = null;
            try {
                try {
                    for (;;) {
                        int localRead = doReadMessages(readBuf);
                        if (localRead == 0) {
                            break;
                        }
                        if (localRead < 0) {
                            closed = true;
                            break;
                        }

                        // stop reading and remove op
                        if (!config.isAutoRead()) {
                            break;
                        }

                        if (readBuf.size() >= maxMessagesPerRead) {
                            break;
                        }
                    }
                } catch (Throwable t) {
                    exception = t;
                }
                setReadPending(false);
                int size = readBuf.size();
                for (int i = 0; i < size; i ++) {
                    pipeline.fireChannelRead(readBuf.get(i));
                }

                readBuf.clear();
                pipeline.fireChannelReadComplete();

                if (exception != null) {
                    if (exception instanceof IOException && !(exception instanceof PortUnreachableException)) {
                        // ServerChannel should not be closed even on IOException because it can often continue
                        // accepting incoming connections. (e.g. too many open files)
                        closed = !(AbstractNioMessageChannel.this instanceof ServerChannel);
                    }

                    pipeline.fireExceptionCaught(exception);
                }

                if (closed) {
                    if (isOpen()) {
                        close(voidPromise());
                    }
                }
            } finally {
                // Check if there is a readPending which was not processed yet.
                // This could be for two reasons:
                // * The user called Channel.read() or ChannelHandlerContext.read() in channelRead(...) method
                // * The user called Channel.read() or ChannelHandlerContext.read() in channelReadComplete(...) method
                //
                // See https://github.com/netty/netty/issues/2254
                if (!config.isAutoRead() && !isReadPending()) {
                    removeReadOp();
                }
            }
        }
    }
```
