category: Netty
date: 2016-02-22
title: NettyServerBootstrap
---

```java
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

public class NettyServerBootstrap {
	public static void main(String[] args) {
		int cpuSize = Runtime.getRuntime().availableProcessors();
		// 配置服务端的NIO线程组
		EventLoopGroup bossGroup = new NioEventLoopGroup();			// mainReactor负责监听server socket,accept新连接
		EventLoopGroup workerGroup = new NioEventLoopGroup(cpuSize);// subReactor负责多路分离已连接的socket,读写网 络数据,对业务处理功能,其扔给worker线程池完成

		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
					// 设置nio类型的channel,根据channel.class来实例化ChannelFactory对象
					.channel(NioServerSocketChannel.class)
					.option(ChannelOption.SO_BACKLOG, 128)
					.option(ChannelOption.TCP_NODELAY, true)
					.option(ChannelOption.AUTO_READ, true)
					// 设置AbstractBootstrap(bossGroup) handler,该handler每个ServerBootstrap 只有一个
					.handler(new LoggingHandler(LogLevel.INFO))
					//有连接到达时会创建一个channel
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) {
							// pipeline管理channel中的Handler,在channel队列中添加一个handler来处理业务
							ChannelPipeline pipeline = ch.pipeline();

							pipeline.addLast(new ProtobufVarint32FrameDecoder());
							pipeline.addFirst(new IdleStateHandler(5, 5, 10));

							pipeline.addLast(new NioEventLoopGroup(128), new ProtobufVarint32FrameDecoder());
						}
					});

			// 绑定端口,同步等待成功
			ChannelFuture f = null;
			try {
				// 配置完成,开始绑定server,通过调用sync同步方法阻塞直到绑定成功
				f = b.bind(8880).sync();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		} finally {
			// 优雅退出,释放线程池资源
			//关闭EventLoopGroup,释放掉所有资源包括创建的线程
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

}

```