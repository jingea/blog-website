category: Java Library
tag: Netty
date: 2016-02-02
title: Netty ChannelHandler 和 ChannelPipeline
---
## ChannelHandler
在写Netty Application的时候, 我们一般要做的就是俩件事
1. 使用ServerBootstrap构建一个启动器, 监听网络事件
2. 实现ChannelHandler接口完成网络事件的decoder和encoder功能

> 之所以在提供了handle的接口之后还提供Adapter, 是因为如果我们直接实现handler接口的话, 那么我们就需要实现handler里的所有方法, 但是我们可能要在不同的handler里实现不同的功能, 而这些功能恰巧由不同的handler里的方法实现, 那么每个实现了handler接口的类都会有大量的冗余代码. 但是如果我们继承Adapter的话, 我们只需要重写需要实现功能的方法就可以了.

今天就来看看ChannelHandler的写法和实现, 首先我们看一下`ChannelHandler`的继承结构
![](https://raw.githubusercontent.com/yu66/blog-website/images/netty/netty%20ChannelHadler.jpg)
我们用`ChannelInboundHandler`处理写入事件, 用`ChannelOutboundHandler`处理写出事件. 下面我们看一下如何自己实现一个inbound和outbound handler
```java
public class MyInboundHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Connected!");
        ctx.fireChannelActive();
    }
}

public clas MyOutboundHandler extends ChannelOutboundHandlerAdapter {
    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise} promise) {
        System.out.println("Closing ..");
        ctx.close(promise);
    }
}
```

## ChannelPipeline
简单地介绍了一下`ChannelHandler`, 但是在说它之前, 还是不得不先介绍一下`ChannelPipeline`, 它是一个基于链表实现的`ChannelHandler`的集合, 用于处理或者截断`Channel`的`inbound events`和`outbound operations`. (`ChannelPipeline`是[Intercepting Filter](http://www.oracle.com/technetwork/java/interceptingfilter-142169.html)的一个高级实现, 它保证了用户对事件处理的完整控制权以及确保了`ChannelHandler`在pipeline中的运行方式.)

```java
public class DefaultChannelPipeline implements ChannelPipeline {

    final AbstractChannelHandlerContext head;
    final AbstractChannelHandlerContext tail;

    private final Channel channel;

    protected DefaultChannelPipeline(Channel channel) {
        this.channel = ObjectUtil.checkNotNull(channel, "channel");

        tail = new TailContext(this);
        head = new HeadContext(this);

        head.next = tail;
        tail.prev = head;
    }
}
```

每当创建一个`Channel`的时候, 都会创建出一个对应的`ChannelPipeline`, 也就是说每个`Channel`都有其自己的`ChannelPipeline`. 在`AbstractChannel`中
```java
public abstract class AbstractChannel extends DefaultAttributeMap implements Channel {

    private final Channel parent;
    private final DefaultChannelPipeline pipeline;

    protected AbstractChannel(Channel parent) {
        this.parent = parent;
        pipeline = newChannelPipeline();
    }

    protected DefaultChannelPipeline newChannelPipeline() {
        return new DefaultChannelPipeline(this);
    }
}
```
看到这里就需要点出一点了, 网络事件是从Channel中传递到Pipeline中, 然后在Pipeline中遍历ChannelHandler链表, 触发相应的方法.

当我们增加一个`ChannelHandler`时
```java
@Override
public ChannelPipeline addFirst(EventExecutorGroup group, final String name, ChannelHandler handler) {
    synchronized (this) {
        checkDuplicateName(name);
        // 我们将handler和ChannelPipeline, EventLoop封装到一个Context里
        DefaultChannelHandlerContext newCtx = new DefaultChannelHandlerContext(this, group, name, handler);
        addFirst0(name, newCtx);
    }

    return this;
}

private void addFirst0(String name, DefaultChannelHandlerContext newCtx) {
    checkMultiplicity(newCtx);

    // 我们将添加的handler放到链表的第一个位置上
    DefaultChannelHandlerContext nextCtx = head.next;
    newCtx.prev = head;
    newCtx.next = nextCtx;
    head.next = newCtx;
    nextCtx.prev = newCtx;

    name2ctx.put(name, newCtx);

    callHandlerAdded(newCtx);
}

private void callHandlerAdded(final ChannelHandlerContext ctx) {
    if (ctx.channel().isRegistered() && !ctx.executor().inEventLoop()) {
        // 如果Channel已经注册到eventLoop上, 且当前线程与eventLoop中的线程不是同一个, 也就是说当前操作是多线程进行的,
        // 则将callHandlerAdded0()逻辑放到任务队列中进行执行
        ctx.executor().execute(new Runnable() {
            @Override
            public void run() {
                callHandlerAdded0(ctx);
            }
        });
        return;
    }
    callHandlerAdded0(ctx);
}

private void callHandlerAdded0(final ChannelHandlerContext ctx) {
    try {
        ctx.handler().handlerAdded(ctx);
    } catch (Throwable t) {

    }
}
```
我们看到最终的时候在`ChannelHandler`里添加了`ChannelHandlerContext`. 但是经过查看`ByteToMessageDecoder`, `ChannelInboundHandlerAdapter`, `ChannelHandlerAdapter`
这个都是空实现, 也就是说, 如果用户自己没有重载的话, 那么这里不会有任何的逻辑产生.
> 我们可以在任何时间在`ChannelPipeline`上添加或者移除`ChannelHandler`, 因为`ChannelPipeline`是线程安全的. 例如我们可以在线上环境中因为业务原因动态的添加或者移除handler.


下面的图给出了IO事件是如何在`ChannelPipeline`里的`ChannelHandler`进行传递处理的. IO事件由`ChannelInboundHandler`或者`ChannelOutboundHandler`处理, 我们在handler中调用`ChannelHandlerContext`中的事件传播方法将event传播给下一个handler继续执行, 例如调用`ChannelHandlerContext#fireChannelRead(Object)`和`ChannelHandlerContext#write(Object)`
```java
                                               I/O Request
                                          via Channel} or
                                      ChannelHandlerContext}
                                                    |
+---------------------------------------------------+---------------+
|                           ChannelPipeline         |               |
|                                                  \|/              |
|    +---------------------+            +-----------+----------+    |
|    | Inbound Handler  N  |            | Outbound Handler  1  |    |
|    +----------+----------+            +-----------+----------+    |
|              /|\                                  |               |
|               |                                  \|/              |
|    +----------+----------+            +-----------+----------+    |
|    | Inbound Handler N-1 |            | Outbound Handler  2  |    |
|    +----------+----------+            +-----------+----------+    |
|              /|\                                  .               |
|               .                                   .               |
| ChannelHandlerContext.fireIN_EVT() ChannelHandlerContext.OUT_EVT()|
|        [ method call]                       [method call]         |
|               .                                   .               |
|               .                                  \|/              |
|    +----------+----------+            +-----------+----------+    |
|    | Inbound Handler  2  |            | Outbound Handler M-1 |    |
|    +----------+----------+            +-----------+----------+    |
|              /|\                                  |               |
|               |                                  \|/              |
|    +----------+----------+            +-----------+----------+    |
|    | Inbound Handler  1  |            | Outbound Handler  M  |    |
|    +----------+----------+            +-----------+----------+    |
|              /|\                                  |               |
+---------------+-----------------------------------+---------------+
                |                                  \|/
+---------------+-----------------------------------+---------------+
|               |                                   |               |
|       [ Socket.read() ]                    [ Socket.write() ]     |
|                                                                   |
|  Netty Internal I/O Threads (Transport Implementation)            |
+-------------------------------------------------------------------+
```
从上图中我们可以看出左边是`inbound`handler(从下向上进行处理), 右图是`outbound`流程(从上向下进行处理).`inbound`handler通常处理的是由IO线程生成的`inbound`数据(例如`SocketChannel#read(ByteBuffer)`).
`outbound`handler一般由write请求生成或者转换传输数据. 如果`outbound`数据传输到上图的底部后, 它就会被绑定到`Channel`上的IO线程进行操作. IO线程一般会进行`SocketChannel#write(ByteBuffer)`数据输出操作.

> 底层的`SocketChannel#read()`方法读取`ByteBuf`, 然后由IO线程`NioEventLoop`调用`ChannelPipeline#fireChannelRead()`方法,将消息`ByteBuf`传递到`ChannelPipeline`中.

可以预想到的是, 用户在使用pipeline中肯定最少会有一个`ChannelHandler`用来接受IO事件(例如read操作)和响应IO操作(例如write和close). 例如一个标准的服务器在每个channel中的pipeline中会有如下的handler
* Protocol Decoder ： 将二进制的字节码(例如`ByteBuf`中的数据)解析成Java对象
* Protocol Encoder :  将Java对象转换成二进制数据进行网络传输
* Business Logic Handler : 执行真正的业务逻辑


在下面的示例中我们分别在pipeline中添加俩个`inbound`handler和俩个`outbound`handler.(以`Inbound`开头的类名表示为一个`inbound`handler, 以`Outbound`开头的类名表示为一个`outbound`handler.)
```java
ChannelPipeline p = ...;
p.addLast("1", new InboundHandlerA());
p.addLast("2", new InboundHandlerB());
p.addLast("3", new OutboundHandlerA());
p.addLast("4", new OutboundHandlerB());
p.addLast("5", new InboundOutboundHandlerX());
```
事件在`inbound`handler中的执行过程是`1, 2, 3, 4, 5`. 事件在`outbound`handler中的执行过程是`5, 4, 3, 2, 1`.

但是在真实的执行过程中, 由于`3, 4`并没有实现`ChannelInboundHandler`, 因此inbound流程中真正执行的handler只有`1, 2, 5`. 而由于`1, 2`并没有实现`ChannelOutboundHandler`因此在outbound流程中真正执行的handler只有`5, 4, 3`.
如果`5`都实现了`ChannelInboundHandler`和`ChannelOutboundHandler`, 那么事件的执行顺序分别是`125`和`543`.


## fireXXX
在`AbstractChannel`中持有一个`ChannelPipeline`的实例, 一般是由`Unsafe`里通过调用`ChannelPipeline`的`fireXXX()`方法时, 去调用`AbstractChannelHandlerContext`的`invokeChannelXXX`静态方法来完成ChannelHandler链表遍历, 这个遍历咋完成的呢？用`fireChannelActive()`举个例子

```java
@Override
public final ChannelPipeline fireChannelActive() {
    AbstractChannelHandlerContext.invokeChannelActive(head);
    return this;
}
```
在上面我们贴出了俩个方法, 下面我们看一下`fireChannelRead()`的处理流程.

在`AbstractChannelHandlerContext#fireChannelRead()`
```java
static void invokeChannelActive(final AbstractChannelHandlerContext next) {
    EventExecutor executor = next.executor();
    if (executor.inEventLoop()) {
        next.invokeChannelActive();
    } else {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                next.invokeChannelActive();
            }
        });
    }
}

// 进行处理, 是用当前Context的ChannelHandler处理真实逻辑,还是要继续遍历ChannelHandler链表,找下一个合适的ChannelHandler
private void invokeChannelActive() {
    if (invokeHandler()) {
        try {
            ((ChannelInboundHandler) handler()).channelActive(this);
        } catch (Throwable t) {
            notifyHandlerException(t);
        }
    } else {
        fireChannelActive();
    }
}

// ChannelHandler链表遍历,　关键就是在这实现的
public ChannelHandlerContext fireChannelActive() {
    final AbstractChannelHandlerContext next = findContextInbound();
    invokeChannelActive(next);
    return this;
}
// 一直遍历整个链表直到找到inbound handler
private AbstractChannelHandlerContext findContextInbound() {
    AbstractChannelHandlerContext ctx = this;
    do {
        ctx = ctx.next;
    } while (!ctx.inbound);
    return ctx;
}
```
这里就直接找到了handler, 触发了我们最终自己实现的`channelRead()`方法.

也许你已经注意到了, 在handler中不得不调用`ChannelHandlerContext`的事件传播方法, 将事件传递给下一个handler. 下面的是能够触发`inbound`事件的方法(`ChannelInboundHandler`)

### channelRegistered
Channel注册事件
```bash
SingleThreadEventLoop#register()  
                ↓
AbstractChannel#AbstractUnsafe#register()
                ↓
AbstractChannel#AbstractUnsafe#register0()
                ↓				
DefaultChannelPipeline#fireChannelRegistered()
                ↓
ChannelInboundHandler#channelRegistered()
```

### channelActive
TCP链路建立成功,Channel激活事件
```bash
SingleThreadEventLoop#register()  
                ↓
AbstractChannel#AbstractUnsafe#register()
                ↓
AbstractChannel#AbstractUnsafe#register0()
                ↓				
DefaultChannelPipeline#fireChannelActive()
                ↓
ChannelInboundHandler#channelActive()
```

### channelRead
读事件

### channelReadComplete
读操作完成通知事件

### exceptionCaught
异常通知事件

### userEventTriggered
用户自定义事件

### channelWritabilityChanged
Channel的可写状态变化通知事件

### channelInactive
TCP链路关闭, 链路不可用通知事件

触发`outbound`事件的方法有(ChannelOutboundHandler)

### bind
绑定本地地址事件

### connect
连接服务端事件

### flush
刷新事件

### read
读事件

### disconnect
断开连接事件

### close
关闭当前Channel事件



## 解码器
为了解决网络数据流的拆包粘包问题,Netty为我们内置了如下的解码器
* ByteToMessageDecoder
* MessageToMessageDecoder
* LineBasedFrameDecoder
* StringDecoder
* DelimiterBasedFrameDecoder
* FixedLengthFrameDecoder
* ProtoBufVarint32FrameDecoder
* ProtobufDecoder
* LengthFieldBasedFrameDecoder

Netty还内置了如下的编码器
* ProtobufEncoder
* MessageToByteEncoder
* MessageToMessageEncoder
* LengthFieldPrepender

Netty还为我们提供HTTP相关的编解码器
* `HttpRequestDecoder` : Http消息解码器
* `HttpObjectAggregator` : 将多个消息转换为单一的`FullHttpRequest`或者`FullHttpResponse`
* `HttpResponseEncoder` : 对Http消息影响进行编码
* `ChunkedWriteHandler` : 异步大码流消息发送


### ByteToMessageDecoder
如果我们自己想要实现自己的半包解码器,我们可以继承`ByteToMessageDecoder`, 实现更加复杂的半包解码
```java
public abstract class ByteToMessageDecoder extends ChannelInboundHandlerAdapter
```
> [ChannelInboundHandlerAdapter]()参考

我们只需要继承该类并实现
```java
protected abstract void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception;
```
这个方法, 在这个方法里完成byte字节到java对象的转换, 也就是我们将`ByteBuf`解析成java对象然后抛给`List<Object> out`就可以了.
> 需要注意的这个类没有实现粘包组包等情况, 这个就需要我们自己实现了.

### MessageToMessageDecoder
`MessageToMessageDecoder`一般作为二次解码器, 当我们在`ByteToMessageDecoder`将一个bytes数组转换成一个java对象的时候, 我们可能还需要将这个对象进行二次解码成其他对象, 我们就可以继承这个类,
```java
public abstract class MessageToMessageDecoder<I> extends ChannelInboundHandlerAdapter
```
然后实现
```java
protected abstract void decode(ChannelHandlerContext ctx, I msg, List<Object> out) throws Exception;
```
这个方法就可以了

### LineBasedFrameDecoder
`LineBasedFrameDecoder`的原理是从`ByteBuf`的可读字节中找到`\n`或者`\r\n`,找到之后就以此为结束,然后将当前读取到的数据组成一行. 如果我们设置每一行的最大长度, 但是当达到最大长度之后还没有找到结束符,就会抛出异常,同时将读取的数据舍弃掉.

`LineBasedFrameDecoder`的用法很简单, 我们可以向其指定大小或者不指定大小
```java
...
ch.pipline().addLast(new LineBasedFrameDecoder());
...
或者
...
ch.pipline().addLast(new LineBasedFrameDecoder(1024));
...
```
它的源码也很简单
```java
    protected Object decode(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
        // 找到 \n 的位置 (如果是\n\r的话, 则向前移动一位,只取\n)
        final int eol = findEndOfLine(buffer);
        //
        if (!discarding) {
            if (eol >= 0) {
                // 找到了 \n ，开始截取有效数据
                final ByteBuf frame;
                final int length = eol - buffer.readerIndex();
                // 如果分隔符是\n的话,分隔符长度是1, 如果分隔符是\n\r的话,则分隔符长度是2
                final int delimLength = buffer.getByte(eol) == '\r'? 2 : 1;

                if (length > maxLength) {
                    // 超过最大长度, 将读取的数据舍掉
                    buffer.readerIndex(eol + delimLength);
                    fail(ctx, length);
                    return null;
                }

                if (stripDelimiter) {
                    // 读取数据不带分隔符, 读取有效数据后将分隔符去掉
                    frame = buffer.readRetainedSlice(length);
                    buffer.skipBytes(delimLength);
                } else {
                    // 有效数据中带有分隔符
                    frame = buffer.readRetainedSlice(length + delimLength);
                }

                return frame;
            } else {
                // 没有找到分隔符, 返回null
                final int length = buffer.readableBytes();
                if (length > maxLength) {
                    // 如果数据超过最大长度, 则将多余的数据舍弃
                    discardedBytes = length;
                    buffer.readerIndex(buffer.writerIndex());
                    discarding = true;
                    if (failFast) {
                        fail(ctx, "over " + discardedBytes);
                    }
                }
                return null;
            }
        } else {
            if (eol >= 0) {
                final int length = discardedBytes + eol - buffer.readerIndex();
                final int delimLength = buffer.getByte(eol) == '\r'? 2 : 1;
                buffer.readerIndex(eol + delimLength);
                discardedBytes = 0;
                discarding = false;
                if (!failFast) {
                    fail(ctx, length);
                }
            } else {
                discardedBytes += buffer.readableBytes();
                buffer.readerIndex(buffer.writerIndex());
            }
            return null;
        }
    }
```


### DelimiterBasedFrameDecoder
使用`DelimiterBasedFrameDecoder`我们可以自定义设定分隔符
```java
...
ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
ch.pipline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
```
在上面的例子中我们使用了自定义的分隔符`$_`, 同样的如果在1024个字节中找不到`$_`, 也会抛出.

### FixedLengthFrameDecoder
`FixedLengthFrameDecoder`为定长解码器, 它会按照指定长度对消息进行解码.
```java
ch.pipline().addLast(new FixedLengthFrameDecoder(1024));
```
上面的例子会每隔1024个长度之后进行消息解码,如果不足1024,则会将消息缓存起来,然后再进行解码

### ProtobufVarint32FrameDecoder
`ProtoBufVarint32FrameDecoder`是Netty为我们提供的Protobuf半包解码器, 通过它配合使用`ProtobufDecoder`和`ProtobufEncoder`我们就可以使用Protobuf进行通信了
```java
ch.pipline().addLast(new ProtobufVarint32FrameDecoder());
ch.pipline().addLast(new ProtobufDecoder());
ch.pipline().addLast(new ProtobufEncoder());
```

### LengthFieldBasedFrameDecoder
`LengthFieldBasedFrameDecoder`是Netty为我们提供的通用半包解码器.
```java
public class LengthFieldBasedFrameDecoder extends ByteToMessageDecoder
```
这个类的半包读取策略由下面的属性控制
* `lengthFieldOffset` : 标志长度字段的偏移量. 也就是在一个bytes字节流中,表示消息长度的字段是从流中哪个位置开始的.
* `lengthFieldLength` : 长度字段的长度(单位byte)
* `lengthAdjustment` : 当消息长度包含了消息头的长度的时候,需要使用这个变量进行校正, 例如lengthFieldOffset为0,lengthFieldLength为2, 那么消息正体在解析时就需要校正2个字节, 故这里为-2.
* `initialBytesToStrip`: 这个是当我们解析`ByteBuf`时要跳过的那些字段, (一般为lengthFieldOffset + lengthFieldLength)

### MessageToByteEncoder
该类负责将java对象编码成`ByteBuf`, 我们只需要继承该类然后实现
```java
protected abstract void encode(ChannelHandlerContext ctx, I msg, ByteBuf out) throws Exception;
```
方法就可以了

### MessageToMessageEncoder
如果要将java对象不编码成`ByteBuf`, 而是编译成, 其他对象, 那我们可以继承这个类实现
```java
protected abstract void encode(ChannelHandlerContext ctx, I msg, List<Object> out) throws Exception;
```
这个方法就可以了

> 这个类与`MessageToByteEncoder`的不同是, 将java对象放到一个`List<Object> out`, 而不是编码成`ByteBuf`发送

### LengthFieldPrepender
`LengthFieldPrepender`是一个非常实用的工具类, 如果我们在发送消息的时候采用的是:消息长度字段+原始消息的形式, 那么我们就可以使用`LengthFieldPrepender`了. 这是因为`LengthFieldPrepender`可以将待发送消息的长度(二进制字节长度)写到`ByteBuf`的前俩个字节.例如:
```java
Hello,World
```
编码前是12个字节,但是经过`LengthFieldPrepender`编码后变成了
```java
0x000E Hello,World
```
成为了14个字节

### HTTP解码器

> 使用`HttpObjectAggregator`是因为在解码Http消息中会产生多个对象(`HttpRequest`, `HttpResponse`, `HttpContent`, `LastHttpContent`), 使用`HttpObjectAggregator`我们可以将这些对象都组合到一起去. 然后当我们自己在处理消息时就可以直接使用`FullHttpRequest了`

```java
ch.pipline().addLast("http-decoder", new HttpRequestDecoder());
ch.pipline().addLast("http-aggregator", new HttpObjectAggregator());
ch.pipline().addLast("http-encoder", new HttpResponseEncoder());
ch.pipline().addLast("http-chunked", new ChunkedWriteHandler());
```



