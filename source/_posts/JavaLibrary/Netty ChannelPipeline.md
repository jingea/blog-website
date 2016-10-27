category: Java Library
tag: Netty
date: 2016-02-02
title: Netty ChannelPipeline
---

## 简介
`ChannelPipeline`是一个`ChannelHandler`的集合, 用于处理或者截断`Channel`的`inbound events`和`outbound operations`. `ChannelPipeline`是[Intercepting Filter](http://www.oracle.com/technetwork/java/interceptingfilter-142169.html)的一个高级实现, 它保证了用户对事件处理的完整控制权以及确保了`ChannelHandler`在pipeline中的运行方式.

每当创建一个`Channel`的时候, 都会创建出一个对应的`ChannelPipeline`, 也就是说每个`Channel`都有其自己的`ChannelPipeline`

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

## handler实现
下面我们看一下如何自己实现一个inbound和outbound handler
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

可以预想到的是, 用户在使用pipeline中肯定最少会有一个`ChannelHandler`用来接受IO事件(例如read操作)和响应IO操作(例如write和close). 例如一个标准的服务器在每个channel中的pipeline中会有如下的handler
* Protocol Decoder ： 将二进制的字节码(例如`ByteBuf`中的数据)解析成Java对象
* Protocol Encoder :  将Java对象转换成二进制数据进行网络传输
* Business Logic Handler : 执行真正的业务逻辑

下面我们将上面的流程转换成一个真实的示例
```java
static final EventExecutorGroup group = new DefaultEventExecutorGroup(16);
...

ChannelPipeline} pipeline = ch.pipeline();

pipeline.addLast("decoder", new MyProtocolDecoder());
pipeline.addLast("encoder", new MyProtocolEncoder());

// Tell the pipeline to run MyBusinessLogicHandler's event handler methods
// in a different thread than an I/O thread so that the I/O thread is not blocked by
// a time-consuming task.
// If your business logic is fully asynchronous or finished very quickly, you don't
// need to specify a group.
pipeline.addLast(group, "handler", new MyBusinessLogicHandler());
```

我们可以在任何时间在`ChannelPipeline`上添加或者移除`ChannelHandler`, 因为`ChannelPipeline`是线程安全的. 例如我们可以在线上环境中因为业务原因动态的添加或者移除handler.

## 事件处理过程
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

## 源码剖析
下来我们看一下`ChannelPipeline`的默认实现`DefaultChannelPipeline`里的数据结构
```java
final AbstractChannel channel;

final DefaultChannelHandlerContext head;
final DefaultChannelHandlerContext tail;
```
`DefaultChannelPipeline`采用链式方式存储`ChannelHandlerContext`(内部存储的的是`ChannelHandler`). 在构造器中, 它将头尾相连了起来
```java
public DefaultChannelPipeline(AbstractChannel channel) {
        if (channel == null) {
            throw new NullPointerException("channel");
        }
        this.channel = channel;

        tail = new TailContext(this);
        head = new HeadContext(this);

        // 将链表首尾相连
        head.next = tail;
        tail.prev = head;
}
```
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

最终我们看到了, 在`DeaultChannelPipeline`的内部只是维持了一个链表头和链表尾.那么当`Unsafe`里调用`fireXXX()`相关的方法时就会由头或者尾context来触发
```java
@Override
    public ChannelPipeline fireChannelActive() {
        head.fireChannelActive();

        if (channel.config().isAutoRead()) {
            channel.read();
        }

        return this;
    }

    @Override
    public ChannelPipeline fireChannelRead(Object msg) {
        head.fireChannelRead(msg);
        return this;
    }
```
在上面我们贴出了俩个方法, 下面我们看一下`fireChannelRead()`的处理流程.

在`AbstractChannelHandlerContext#fireChannelRead()`
```java
@Override
    public ChannelHandlerContext fireChannelRead(final Object msg) {
        final AbstractChannelHandlerContext next = findContextInbound();
        EventExecutor executor = next.executor();
        if (executor.inEventLoop()) {
            next.invokeChannelRead(msg);
        } else {
            executor.execute(new OneTimeTask() {
                @Override
                public void run() {
                    next.invokeChannelRead(msg);
                }
            });
        }
        return this;
    }

    private AbstractChannelHandlerContext findContextInbound() {
        AbstractChannelHandlerContext ctx = this;
        do {
            ctx = ctx.next;
        } while (!ctx.inbound);
        return ctx;
    }
```
> 直接fireChannelRead() 会跳过第一个handler???

上面的`fireChannelRead()`逻辑很简单, 我们接下来看一下`invokeChannelRead()`
```java
private void invokeChannelRead(Object msg) {
        try {
            ((ChannelInboundHandler) handler()).channelRead(this, msg);
        } catch (Throwable t) {
            notifyHandlerException(t);
        }
    }
```
这里就直接找到了handler, 触发了我们最终自己实现的`channelRead()`方法.

也许你已经注意到了, 在handler中不得不调用`ChannelHandlerContext`的事件传播方法, 将事件传递给下一个handler. 下面的是
能够触发`inbound`事件的方法
* `ChannelHandlerContext#fireChannelRegistered()` Channel注册事件. (``触发)
* `ChannelHandlerContext#fireChannelActive()` TCP链路建立成功,Channel激活事件. (``触发)
* `ChannelHandlerContext#fireChannelRead(Object var1)` 读事件. (``触发)
* `ChannelHandlerContext#fireChannelReadComplete()` 读操作完成通知事件. (``触发)
* `ChannelHandlerContext#fireExceptionCaught(Throwable var1)` 异常通知事件. (``触发)
* `ChannelHandlerContext#fireUserEventTriggered(Object var1)` 用户自定义事件. (``触发)
* `ChannelHandlerContext#fireChannelWritabilityChanged()` Channel的可写状态变化通知事件. (``触发)
* `ChannelHandlerContext#fireChannelInactive()` TCP链路关闭, 链路不可用通知事件. (``触发)
触发`outbound`事件的方法有
* `ChannelHandlerContext#bind(SocketAddress var1, ChannelPromise var2)` 绑定本地地址事件
* `ChannelHandlerContext#connect(SocketAddress var1, ChannelPromise var2)` 连接服务端事件
* `ChannelHandlerContext#flush()` 刷新事件
* `ChannelHandlerContext#read()` 读事件
* `ChannelHandlerContext#disconnect(ChannelPromise var1)` 断开连接事件
* `ChannelHandlerContext#close(ChannelPromise var1)` 关闭当前Channel事件
