category: Netty
date: 2016-03-17
title: Netty 压测
---
我们知道Netty的性能是非常好的,那究竟有多好呢? 今天我写了一个小程序测试了一下
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
上面是一个简单的Socket服务器, 不做任何的编解码工作, 当接收到数据之后, 直接返回给客户端.

在启动的这个服务器的时候加上指定虚拟机的堆大小(我们指定了大小为固定的30M)
```java
-Xmx30m -Xms30m
````
然后写一个Socket客户端程序(原谅我客户端是用python写的, 现在我正在把我业余时间写代码的语言替换成python)
```python
import socket
import threading
import time

count = 0
def socketSendData():
    client=socket.socket(socket.AF_INET,socket.SOCK_STREAM)
    client.connect(('localhost',8881))
    client.send('2')
    time.sleep(60)



for i in range(0, 2000, 1):
    try:
       t = threading.Thread(target=socketSendData)
       info = t.start()
    except:
       count += 1
       print "Error: unable to start thread  " + str(count)
```
由于测试机配置问题, 我的python程序只能启动2000个Socket连接, 但是也无所谓, 我们看一下在这俩千个Socket连接时, Netty服务器的消耗

我们首先看一下客户端连接运行之前的Netty程序的内存占用
![](https://raw.githubusercontent.com/ming15/blog-website/images/netty/netty%20压测1.jpg)
我们看到了在top中为46M, 在visualVM中分配的30M堆内存也只使用了10M, 而且一直在GC.

那么我们再看一下压测之后的内?
![](https://raw.githubusercontent.com/ming15/blog-website/images/netty/Netty%20压测2.jpg)
top中显示占用了58M的内存, 而在visualVM只不过偶尔比10M多一点的内存,而且又很快的GC掉了.

那么top中多出的12M内存是怎么回事呢?这是因为Netty默认的使用的是UnpooledDirectByteBuf(姑且是这么个名字吧), 它使用的是非池化的直接内存, 也就是说在接受网络连接数据的时候,它并没有直接使用堆内内存,而是使用的堆外的.
