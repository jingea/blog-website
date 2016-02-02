category: Java
tag: Netty
date: 2016-02-02
title: Netty ChannelPipeline
---
`ChannelPipeline`是一个`ChannelHandler`的集合, 用于处理或者截断`Channel`的`inbound events`和`outbound operations`. `ChannelPipeline`是[Intercepting Filter](http://www.oracle.com/technetwork/java/interceptingfilter-142169.html)的一个高级实现, 它保证了用户对事件处理的完整控制权以及确保了`ChannelHandler`在pipeline中的运行方式.

每当创建一个`Channel`的时候, 都会创建出一个对应的`ChannelPipeline`, 也就是说每个`Channel`都有其自己的`ChannelPipeline`

下面的图给出了IO事件是如何在`ChannelPipeline`里的`ChannelHandler`进行传递处理的. IO事件由`ChannelInboundHandler`或者`ChannelOutboundHandler`处理, 我们在handler中调用`ChannelHandlerContext`中的事件传播方法将event传播给下一个handler继续执行, 例如调用`ChannelHandlerContext#fireChannelRead(Object)`和`ChannelHandlerContext#write(Object)`
```
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

也许你已经注意到了, 在handler中不得不调用`ChannelHandlerContext`的事件传播方法, 将事件传递给下一个handler. 下面的是事件inbound过程涉及到的方法
* 
* `ChannelHandlerContext#fireChannelRegistered()`
* `ChannelHandlerContext#fireChannelActive()`
* `ChannelHandlerContext#fireChannelRead(Object)`
* `ChannelHandlerContext#fireChannelReadComplete()`
* `ChannelHandlerContext#fireExceptionCaught(Throwable)`
* `ChannelHandlerContext#fireUserEventTriggered(Object)`
* `ChannelHandlerContext#fireChannelWritabilityChanged()`
* `ChannelHandlerContext#fireChannelInactive()`
* `ChannelHandlerContext#fireChannelUnregistered()`
事件outbound流程传播方法
* `ChannelHandlerContext#bind(SocketAddress, ChannelPromise)`
* `ChannelHandlerContext#connect(SocketAddress, SocketAddress, ChannelPromise)`
* `ChannelHandlerContext#write(Object, ChannelPromise)`
* `ChannelHandlerContext#flush()`
* `ChannelHandlerContext#read()`
* `ChannelHandlerContext#disconnect(ChannelPromise)`
* `ChannelHandlerContext#close(ChannelPromise)`
* `ChannelHandlerContext#deregister(ChannelPromise)`


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
