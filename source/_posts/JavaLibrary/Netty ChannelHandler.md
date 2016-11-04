category: Java Library
tag: Netty
date: 2016-01-23
title: Netty ChannelHandler
---
在写Netty Application的时候, 首先我们看一下`ChannelHandler`的继承结构
![](https://raw.githubusercontent.com/ming15/blog-website/images/netty/netty%20ChannelHadler.jpg)
* `ChannelHandler`
* `ChannelInboundHandler`
* `ChannelOutboundHandler`
* `ChannelInboundHandlerAdapter`
* `ChannelOutboundHandlerAdapter`
* `ChannelHandlerAdapter`

```java
public interface ChannelHandler
public interface ChannelInboundHandler extends ChannelHandler
public interface ChannelOutboundHandler extends ChannelHandler
public abstract class ChannelHandlerAdapter implements ChannelHandler
public class ChannelInboundHandlerAdapter extends ChannelHandlerAdapter implements ChannelInboundHandler
public class ChannelOutboundHandlerAdapter extends ChannelHandlerAdapter implements ChannelOutboundHandler
```
之所以在提供了handle的接口之后还提供Adapter, 是因为如果我们直接实现handler接口的话, 那么我们就需要实现handler里的所有方法, 但是我们可能要在不同的handler里实现不同的功能, 而这些功能恰巧由不同的handler里的方法实现, 那么每个实现了handler接口的类都会有大量的冗余代码. 但是如果我们继承Adapter的话, 我们只需要重写需要实现功能的方法就可以了.

> IdleStateHandler



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


## ByteToMessageDecoder
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

## MessageToMessageDecoder
`MessageToMessageDecoder`一般作为二次解码器, 当我们在`ByteToMessageDecoder`将一个bytes数组转换成一个java对象的时候, 我们可能还需要将这个对象进行二次解码成其他对象, 我们就可以继承这个类,
```java
public abstract class MessageToMessageDecoder<I> extends ChannelInboundHandlerAdapter
```
然后实现
```java
protected abstract void decode(ChannelHandlerContext ctx, I msg, List<Object> out) throws Exception;
```
这个方法就可以了

## LineBasedFrameDecoder
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


## DelimiterBasedFrameDecoder
使用`DelimiterBasedFrameDecoder`我们可以自定义设定分隔符
```java
...
ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
ch.pipline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
```
在上面的例子中我们使用了自定义的分隔符`$_`, 同样的如果在1024个字节中找不到`$_`, 也会抛出.

## FixedLengthFrameDecoder
`FixedLengthFrameDecoder`为定长解码器, 它会按照指定长度对消息进行解码.
```java
ch.pipline().addLast(new FixedLengthFrameDecoder(1024));
```
上面的例子会每隔1024个长度之后进行消息解码,如果不足1024,则会将消息缓存起来,然后再进行解码

## ProtobufVarint32FrameDecoder
`ProtoBufVarint32FrameDecoder`是Netty为我们提供的Protobuf半包解码器, 通过它配合使用`ProtobufDecoder`和`ProtobufEncoder`我们就可以使用Protobuf进行通信了
```java
ch.pipline().addLast(new ProtobufVarint32FrameDecoder());
ch.pipline().addLast(new ProtobufDecoder());
ch.pipline().addLast(new ProtobufEncoder());
```

## LengthFieldBasedFrameDecoder
`LengthFieldBasedFrameDecoder`是Netty为我们提供的通用半包解码器.
```java
public class LengthFieldBasedFrameDecoder extends ByteToMessageDecoder
```
这个类的半包读取策略由下面的属性控制
* `lengthFieldOffset` : 标志长度字段的偏移量. 也就是在一个bytes字节流中,表示消息长度的字段是从流中哪个位置开始的.
* `lengthFieldLength` : 长度字段的长度(单位byte)
* `lengthAdjustment` : 当消息长度包含了消息头的长度的时候,需要使用这个变量进行校正, 例如lengthFieldOffset为0,lengthFieldLength为2, 那么消息正体在解析时就需要校正2个字节, 故这里为-2.
* `initialBytesToStrip`: 这个是当我们解析`ByteBuf`时要跳过的那些字段, (一般为lengthFieldOffset + lengthFieldLength)

## MessageToByteEncoder
该类负责将java对象编码成`ByteBuf`, 我们只需要继承该类然后实现
```java
protected abstract void encode(ChannelHandlerContext ctx, I msg, ByteBuf out) throws Exception;
```
方法就可以了

## MessageToMessageEncoder
如果要将java对象不编码成`ByteBuf`, 而是编译成, 其他对象, 那我们可以继承这个类实现
```java
protected abstract void encode(ChannelHandlerContext ctx, I msg, List<Object> out) throws Exception;
```
这个方法就可以了

> 这个类与`MessageToByteEncoder`的不同是, 将java对象放到一个`List<Object> out`, 而不是编码成`ByteBuf`发送

## LengthFieldPrepender
`LengthFieldPrepender`是一个非常实用的工具类, 如果我们在发送消息的时候采用的是:消息长度字段+原始消息的形式, 那么我们就可以使用`LengthFieldPrepender`了. 这是因为`LengthFieldPrepender`可以将待发送消息的长度(二进制字节长度)写到`ByteBuf`的前俩个字节.例如:
```java
Hello,World
```
编码前是12个字节,但是经过`LengthFieldPrepender`编码后变成了
```java
0x000E Hello,World
```
成为了14个字节

## HTTP解码器

> 使用`HttpObjectAggregator`是因为在解码Http消息中会产生多个对象(`HttpRequest`, `HttpResponse`, `HttpContent`, `LastHttpContent`), 使用`HttpObjectAggregator`我们可以将这些对象都组合到一起去. 然后当我们自己在处理消息时就可以直接使用`FullHttpRequest了`


```java
ch.pipline().addLast("http-decoder", new HttpRequestDecoder());
ch.pipline().addLast("http-aggregator", new HttpObjectAggregator());
ch.pipline().addLast("http-encoder", new HttpResponseEncoder());
ch.pipline().addLast("http-chunked", new ChunkedWriteHandler());
```
