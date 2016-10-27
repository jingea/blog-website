category: Java Library
tag: Netty
date: 2016-02-22
title: NettyServerBootstrap
---
我们首先给出一个Netty上的一个Example示例
```java
public class NettyServer {
    public static void main(String[] args) throws InterruptedException {
        int cpuSize = Runtime.getRuntime().availableProcessors();
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(cpuSize);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.AUTO_READ, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new InHandler());
                        }
                    });

            ChannelFuture f = b.bind(8881).sync();
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}

class InHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ctx.write(msg);
    }
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }
}
```
在这个示例中, 我们采用了主从Reactor线程模型, 然后将接受到的数据写回给客户端.

下来我们分析一下`ServerBootstrap`的源码. 我们从`bind()`方法入手.

由于`bind()`最终调用的是父类`AbstractBootstrap`的`doBind()`方法, 因此我们从父类入手
```java
private ChannelFuture doBind(final SocketAddress localAddress) {
				// 初始化NioServerSocketChannel, 并将其注册主Reactor线程池的IO多路复用器上
        final ChannelFuture regFuture = initAndRegister();
        final Channel channel = regFuture.channel();
        if (regFuture.isDone()) {
            ChannelPromise promise = channel.newPromise();
            doBind0(regFuture, channel, localAddress, promise);
            return promise;
        } else {
            ...
            return promise;
        }
    }
```

接下来我们看一下`AbstractBootstrap#initAndRegister()`方法
```java
final ChannelFuture initAndRegister() {
				// 因为我们调用过channel(NioServerSocketChannel.class)方法, 因此下面这个Channel是NioServerSocketChannel类型
        final Channel channel = channelFactory().newChannel();
        try {
					  // init方法主要是对NioServerSocketChannel添加一个ServerBootstrapAcceptor的Handler(继承自ChannelInboundHandlerAdapter)
            // 当channel接受到网络连接的时候, 会生成NioSocketChannel, 将NioSocketChannel与从Reactor进行绑定
            init(channel);
        } catch (Throwable t) {

        }

				// 将Reactor模型中的主Reactor线程注册到NioServerSocketChannel的Unsafe对象里.
				// 此时就将NioServerSocketChannel与Reactor主线程关联起来了
				ChannelFuture regFuture = group().register(channel);
        if (regFuture.cause() != null) {
            if (channel.isRegistered()) {
                channel.close();
            } else {
                channel.unsafe().closeForcibly();
            }
        }
    }
```

`init()`的`ServerBootstrap`实现的. 这个方法主要是在`NioServerSocketChannel`的pipeline里增加一个`ServerBootstrapAcceptor`handler.
这个handler就是用于处理`NioMessageUnsafe#read()`方法调用`NioServerSocketChannel#doReadMessage()`方法后`List<NioSocketChannel>`的消息列表
```java
@Override
void init(Channel channel) throws Exception {
			 // 获取NioServerSocketChannel的Pipeline
			 ChannelPipeline p = channel.pipeline();
			 if (handler() != null) {
					 p.addLast(handler());
			 }

			 final EventLoopGroup currentChildGroup = childGroup;
			 final ChannelHandler currentChildHandler = childHandler;
			 final Entry<ChannelOption<?>, Object>[] currentChildOptions;
			 final Entry<AttributeKey<?>, Object>[] currentChildAttrs;

			 p.addLast(new ChannelInitializer<Channel>() {
					 @Override
					 public void initChannel(Channel ch) throws Exception {
						 	 // 这里主要是产生网络连接时将处理数据的channel与Reactor从线程关联起来
							 ch.pipeline().addLast(new ServerBootstrapAcceptor(
											 currentChildGroup, currentChildHandler, currentChildOptions, currentChildAttrs));
					 }
			 });
	 }
```

我们看到`ServerBootstrapAcceptor`也是实现自`ChannelInboundHandlerAdapter`, 因此它也是一个handler. 在`NioMessageUnsafe#read()`方法里会遍历
`List<NioSocketChannel>`这个消息列表后触发`NioServerSocketChannel`的pipeline的`fireChannelRead()`方法, 接着就会触发`ServerBootstrapAcceptor#channelRead()`,
```java
private static class ServerBootstrapAcceptor extends ChannelInboundHandlerAdapter {

        private final EventLoopGroup childGroup;
        private final ChannelHandler childHandler;
        ServerBootstrapAcceptor(
                EventLoopGroup childGroup, ChannelHandler childHandler) {
            this.childGroup = childGroup;
            this.childHandler = childHandler;
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            // msg实际是NioSocketChannel类型
            final Channel child = (Channel) msg;
            child.pipeline().addLast(childHandler);
            try {
							  // 将处理数据的NioSocketChannel与主从Reactor模型中的从Reactor线程关联起来
                childGroup.register(child).addListener(new ChannelFutureListener());
            } catch (Throwable t) {
            }
        }
    }
```
然后我们看一下`NioEventLoop`的`register()`方法过程. 这个方法调用其实最终调用的是
```java
SingleThreadEventLoop

@Override
	 public ChannelFuture register(final Channel channel, final ChannelPromise promise) {
			 channel.unsafe().register(this, promise);
			 return promise;
	 }
```
然后后续调用到了`AbstractUnsafe`的`register()`方法
```java
@Override
        public final void register(EventLoop eventLoop, final ChannelPromise promise) {
              AbstractChannel.this.eventLoop = eventLoop;

            if (eventLoop.inEventLoop()) {
                register0(promise);
            } else {
                try {
                    eventLoop.execute(new OneTimeTask() {
                        @Override
                        public void run() {
                            register0(promise);
                        }
                    });
                } catch (Throwable t) {
                }
            }
        }

				private void register0(ChannelPromise promise) {
            try {
                doRegister();
                pipeline.fireChannelRegistered();
                // Only fire a channelActive if the channel has never been registered. This prevents firing
                // multiple channel actives if the channel is deregistered and re-registered.
                if (firstRegistration && isActive()) {
                    pipeline.fireChannelActive();
                }
            } catch (Throwable t) {
            }
        }
```
接着调用`AbstractNioChannel`的`doRegister()`
```java
protected void doRegister() throws Exception {
        boolean selected = false;
        for (;;) {
            try {
                selectionKey = javaChannel().register(eventLoop().selector, 0, this);
                return;
            } catch (CancelledKeyException e) {
            }
        }
    }
```
最终我们看到了, 当前JDK里的Channel注册到了EventLoop的IO多路复用器上面

看到这里之后, 我们再接着返回到`doBind()`方法继续看`doBind0()`方法
```java
private static void doBind0(
            final ChannelFuture regFuture, final Channel channel,
            final SocketAddress localAddress, final ChannelPromise promise) {

        // This method is invoked before channelRegistered() is triggered.  Give user handlers a chance to set up
        // the pipeline in its channelRegistered() implementation.
        channel.eventLoop().execute(new Runnable() {
            @Override
            public void run() {
                if (regFuture.isSuccess()) {
                    channel.bind(localAddress, promise).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                } else {
                    promise.setFailure(regFuture.cause());
                }
            }
        });
    }
```
我们看到当在bind的时候也是调用的channel的bind(), 真实的bind是在`AbstractChannel`里发生的
```java
public ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
		return pipeline.bind(localAddress, promise);
}
```
然后调用的是`DefaultChannelPipeline`的`bind()`方法
```java
@Override
 public ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
		 return tail.bind(localAddress, promise);
 }
```
再具体的bind的话, 就要参考`DeaultChannelPipeline`的实现了

最后我们总结一下
1. 首先将NioServerSocketChannel与主Reactor线程池的Selector进行注册绑定
2. 当NioServerSocketChannel接收到网络连接的时候(doReadMessage())会生成一个`NioSocketChannel`的消息列表
3. 然后`ServerBootstrapAcceptor`负责将`NioSocketChannel`与从Reactor的Selector进行注册绑定
4. 最后由从Reactor线程池中的Selector进行IO调度, 读写网络数据
