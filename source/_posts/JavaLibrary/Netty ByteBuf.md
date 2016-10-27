category: Java Library
tag: Netty
date: 2015-11-20
title: Netty ByteBuf
---
首先我们来看一下netty buffer包的继承结构
![](https://raw.githubusercontent.com/ming15/blog-website/images/netty/bytebuf.jpg)
接下来我会对几个类进行代码测试.

首先我们来看一下如何使用Netty提供的工具类构建一个ByteBuf
```java
ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(1024);
Assert.assertEquals(1024, buf.capacity());
```
我们使用`ByteBufAllocator`这个工具类构建了一个`1024`大小的`ByteBuf`出来.

ByteBuf提供了 `readerIndex` 和 `writerIndex` 进行缓冲区的顺序读写操作.
* `readerIndex`标志读取索引
* `writerIndex`标志写入索引
* [0, readerIndex] 已经读取多的缓冲区区间
* [readerIndex, writerIndex] 可读的缓冲区区间
* [writerIndex, capacity]  可写的缓冲区区间

> 每个索引移动的单位是`bytes`, 在下例中我们向ByteBuf写入一个int数值, `writerIdex`会移动4个`bytes`

## ByteBuf API
我们首先看一下ByteBuf提供的API

### ByteBuf write
接下来我们看一下向ByteBuf缓冲区写入数据的API

####  writeInt

```java
public void testWriteInt() {
	ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(1024);
	buf.writeInt(1);
	// 写入一个Int数值, writerIndex向后移动4个字节
	Assert.assertEquals(4, buf.writerIndex());
}
```

#### writeChar

```java
public void testWriteChar() {
	ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(1024);
	buf.writeChar('a');
	// 写入一个Char字符, writerIndex向后移动2个字节
	Assert.assertEquals(2, buf.writerIndex());
}
```

#### writeBytes
 
```java
public void testWriteBytes() {
	ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(1024);
	byte[] bytes = new byte[]{100};
	buf.writeBytes(bytes);
	// 写入一个byte数组, 由于byte数组只有一个元素, writerIndex向后移动1个字节
	Assert.assertEquals(1, buf.writerIndex());
}

```

#### writeBytes

```java
public void testWriteBytesWithStartEndIndex() {
	ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(1024);
	byte[] bytes = new byte[]{100, 1, 3};
	buf.writeBytes(bytes, 1, 1);
	// 我们将三个元素的byte数组写入ByteBuf中,但是在写入的时候我们指定了开始索引和结束索引,
	// 由于我们的开始索引和结束索引相等, 因此ByteBuf中只写入了1这个元素
	Assert.assertEquals(1, buf.writerIndex());
}

```

#### writeBytes

```java
public void testWriteBytes3() {
	ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(1024);
	ByteBuf buf1 = ByteBufAllocator.DEFAULT.buffer(1024);
	buf1.writeInt(1);
	buf.writeBytes(buf1);
	// 我们向ByteBuf中写入另一个ByteBuf, 它的索引仍然是增长4. ByteBuf不仅仅可以写入BuyeBuf,还可以写入InputStream和ByteBuffer
	Assert.assertEquals(4, buf.writerIndex());
}
```

#### writeFloat

```java
public void testWriteFloat() {
	ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(1024);
	buf.writeFloat(0.1f);
	// 写入一个float, 由于float也是占用4个字节, 因此writerIndex向后移动4个字节
	Assert.assertEquals(4, buf.writerIndex());
}

```

#### writeByte

```java
public void testWriteByte() {
	ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(1024);
	buf.writeByte(1);
	Assert.assertEquals(1, buf.writerIndex());
	buf.writeByte(1000);
	// 写入一个byte, writerIndex向后移动1个字节,至于写进去的数字大于128,会发生什么,我们在read的时候看一下结果
	Assert.assertEquals(2, buf.writerIndex());
}
```

#### writeShort

```java
public void testWriteShort() {
	ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(1024);
	buf.writeShort(1000);
	// 写入一个short, writerIndex向后移动2个字节
	Assert.assertEquals(2, buf.writerIndex());
}
```

#### writeDouble

```java
public void testWriteDouble() {
	ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(1024);
	buf.writeDouble(1000.0d);
	// 写入一个double, writerIndex向后移动8个字节
	Assert.assertEquals(8, buf.writerIndex());
}
```

#### writeBoolean

```java
public void testWriteBoolean() {
	ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(1024);
	buf.writeBoolean(false);
	// 写入一个boolean, writerIndex向后移动1个字节
	Assert.assertEquals(1, buf.writerIndex());
}
```

#### writeLong

```java
public void testWriteLong() {
	ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(1024);
	buf.writeLong(100l);
	// 写入一个long, writerIndex向后移动8个字节
	Assert.assertEquals(8, buf.writerIndex());
}

```

#### writeBytes

```java
public void testWriteOverLoadMaxCapacity() {
	ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(5);
	buf.writeBytes("123456".getBytes());
	// 虽然在分配的时候我们只分配了5个字节大小的缓冲区,但是我们写入6个字节它也并不报错,
	// 而且我们观察到writerIndex确实增长到了6,说明ByteBuf会进行自动拓容.
	Assert.assertEquals(6, buf.writerIndex());
}
```

### ByteBuf read
刚才我们看了向ByteBuf缓冲区写入数据的API,接下来我们看一下从ByteBuf缓冲区读取数据的API

#### readInt

```java
public void testReadInt() {
	ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(1024);
	buf.writeInt(1);
	int read = buf.readInt();
	// 读取Int, readerIndex向后移动4字节
	Assert.assertEquals(4, buf.readerIndex());
	Assert.assertEquals(1, read);
}
```

#### readChar

```java
public void testReadChar() {
	ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(1024);
	buf.writeChar('1');
	char read = buf.readChar();
	// 读取Char, readerIndex向后移动2字节
	Assert.assertEquals(2, buf.readerIndex());
	Assert.assertEquals('1', read);
}

```

#### readBytes

```java
public void testReadBytes() {
	ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(1024);
	buf.writeBytes(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0});
	byte[] read = new byte[10];
	buf.readBytes(read);
	// 读取byte数组, 这里需要注意的是, read字节数组的长度不能大于ByteBuf的readerIndex的值,否则会产生数组越界
	Assert.assertEquals(10, buf.readerIndex());
	Assert.assertEquals(0, read[9]);
}
```

```java
public void testReadBytesWithStartEndIndex() {
	ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(1024);
	buf.writeBytes(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0});
	byte[] read = new byte[10];
	buf.readBytes(read, 2, 3);
	// 从第三个索引开始读取到第4个索引的位置, 读取2个字节, readerIndex移动到第4个索引位置上
	Assert.assertEquals(3, buf.readerIndex());
	Assert.assertEquals(3, read[0]);
}
```

```java
public void testRead3Bytes() {
	ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(1024);
	buf.writeBytes(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0});
	buf.readBytes(3);
	// 读取3个字节, readerIndex向后移动3字节
	Assert.assertEquals(3, buf.readerIndex());
}
```

#### readFloat

```java
public void testReadFloat() {
	ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(1024);
	buf.writeFloat(10.0f);
	float read = buf.readFloat();
	// 读取Float, readerIndex向后移动4字节
	Assert.assertEquals(4, buf.readerIndex());
	Assert.assertEquals(10.f, read);
}

```

#### readLong

```java
public void testReadLong() {
	ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(1024);
	buf.writeLong(10l);
	buf.readLong();
	// 读取long, readerIndex向后移动8字节
	Assert.assertEquals(8, buf.readerIndex());

}
```

#### readByte

```java
public void testReadByte() {
	ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(1024);
	buf.writeBytes(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0});
	buf.readByte();
	// 读取byte, readerIndex向后移动1字节
	Assert.assertEquals(1, buf.readerIndex());
}
```

#### readShort

```java
public void testReadShort() {
	ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(1024);
	buf.writeShort(10);
	buf.readShort();
	// 读取short, readerIndex向后移动2字节
	Assert.assertEquals(2, buf.readerIndex());
}
```

#### readBoolean

```java
public void testReadBoolean() {
	ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(1024);
	buf.writeBoolean(true);
	buf.readBoolean();
	// 读取boolean, readerIndex向后移动1字节
	Assert.assertEquals(1, buf.readerIndex());
}
```

#### readDouble

```java
public void testReadDouble() {
	ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(1024);
	buf.writeDouble(10.0d);
	buf.readDouble();
	// 读取double, readerIndex向后移动8字节
	Assert.assertEquals(8, buf.readerIndex());
}
```

#### readUnsignedByte

```java
public void testReadUnsignedByte() {
	ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(1024);
	buf.writeByte(-10);
	short read = buf.readUnsignedByte();
	// 读取无符号byte, readerIndex向后移动1字节
	Assert.assertEquals(1, buf.readerIndex());
	Assert.assertEquals(246, read);
}

```

#### readUnsignedShort

```java
public void testReadUnsignedShort() {
	ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(1024);
	buf.writeShort(-1024);
	// 我们首先读取出-1024,这个负数,然后转化成无符号数字64512
	int read = buf.readUnsignedShort();
	// 读取无符号Short, readerIndex向后移动2字节
	Assert.assertEquals(2, buf.readerIndex());
	Assert.assertEquals(64512, read);
}

```

#### readerIndex

```java
public void testReaderIndex() {
	ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(1024);
	buf.writeBytes(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0});
	Assert.assertEquals(0, buf.readerIndex());
}

```

#### readByte

```java
public void testReadableBytes() {
	ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(1024);
	buf.writeBytes(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0});
	Assert.assertEquals(10, buf.readableBytes());
	buf.readByte();
	// 我们读取一个byte之后, 可读取字节变成了9个字节
	Assert.assertEquals(9, buf.readableBytes());
}

```

#### readUnsignedInt

```java
public void testReadUnsignedInt() {
	ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(1024);
	buf.writeInt(10);
	long read = buf.readUnsignedInt();
	Assert.assertEquals(4, buf.readerIndex());
	Assert.assertEquals(10, read);
}

```

#### readSlice

```java
public void testReadSlice() {
	ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(1024);
	buf.writeBytes(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0});
	ByteBuf read = buf.readSlice(5);
	// slice出来的ByteBuf与原ByteBuf共享缓冲区
	Assert.assertEquals(5, buf.readerIndex());
	Assert.assertEquals(1, read.readByte());
	Assert.assertEquals(6, buf.readByte());

}

```

#### readInt

```java
public void testWriteBytesReadInt() {
	ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(1024);
	buf.writeBytes(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0});
	int read = buf.readInt();
	// 从一个byte数组中读取一个int, 会读取出1, 2, 3, 4这四个byte转换成int为16909060
	Assert.assertEquals(16909060, read);
}
```

### discard bytes
在前面的测试中我们看到了,当向ByteBuf写入数据时,当超出分配内存大小时,ByteBuf会进行自动拓容(重新生成一个数组缓冲区,然后将原先的缓冲区内容拷贝到新的缓冲区中),这样一来ByteBuf占用的内从会越来越大. 我们可以是`discardReadBytes()`这个方法重用以前的缓冲区, 它会将[0, readerIndex]区间的内存舍弃掉(内部也是数组复制), 这么着就节间的重用了以前的缓冲区,但是这种方式有一点就是如果频繁的调用这个方法会带来性能问题.
```java
ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(50);
buf.writeBytes("123456789".getBytes());
buf.readBytes(3);	// 读取三个字节
System.out.println(buf.readerIndex());		// readerIndex位置 : 3
System.out.println(buf.writerIndex());		// writerIndex位置: 9
System.out.println(buf.readableBytes());	// 可读字节 9 - 3 = 6
System.out.println(buf.writableBytes());	// 可写字节 50 - 9 = 41

// 舍弃已读字节, readerIndex重置为0
buf.discardReadBytes();
System.out.println(buf.readerIndex());		// readerIndex位置 : 0
System.out.println(buf.writerIndex());		// writerIndex位置: 6
System.out.println(buf.readableBytes());	// 可读字节 6
System.out.println(buf.writableBytes());	// 可写字节 50 - 6 = 44
```

### clear
这个操作并不会情况缓冲区的内容只是用来将readerIndex和writerIndex重置为0. 但是缓冲区的内容我们是仍然可以读到的. 
```java
ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(50);
buf.writeBytes("123456789".getBytes());
buf.readBytes(3);	// 读取三个字节
System.out.println(buf.readerIndex());		// readerIndex位置 : 3
System.out.println(buf.writerIndex());		// writerIndex位置: 9
System.out.println(buf.readableBytes());	// 可读字节 9 - 3 = 6
System.out.println(buf.writableBytes());	// 可写字节 50 - 9 = 41

// 重置readerIndex和writerIndex
buf.clear();
System.out.println(buf.readerIndex());		// readerIndex位置 : 0
System.out.println(buf.writerIndex());		// writerIndex位置: 0
System.out.println(buf.readableBytes());	// 可读字节 readerIndex = 0
System.out.println(buf.writableBytes());	// 可写字节 capacity - writerIndex = 50
// 设置writerIndex
buf.writerIndex(6);
System.out.println(buf.readerIndex());		// readerIndex位置 : 0
System.out.println(buf.writerIndex());		// writerIndex位置: 6
System.out.println(buf.readableBytes());	// 可读字节 writerIndex - readerIndex = 6
System.out.println(buf.writableBytes());	// 可写字节 44
System.out.println(buf.readByte());
```

### mark reset
mark reset相关的四个方法也是对指针位置的操作
* `markReaderIndex()` 记录readerIndex
* `markWriterIndex()` 记录writerIndex
* `resetReaderIndex()`  将记录的readerIndex重置到当前的readerIndex值
* `resetWriterIndex()`  将记录的writerIndex重置到当前的writerIndex值

```java
public void testReaderIndex() {
	ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(50);
	buf.writeBytes("123456789".getBytes());
	buf.readBytes(3);
	buf.markReaderIndex();
	buf.readBytes(1);
	Assert.assertEquals(4, buf.readerIndex());
	buf.resetReaderIndex();
	Assert.assertEquals(3, buf.readerIndex());
}


```java
public void testWriterIndex() {
	ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(50);
	buf.writeBytes("123456789".getBytes());
	buf.markWriterIndex();
	buf.writeByte(1);
	Assert.assertEquals(10, buf.writerIndex());
	buf.resetWriterIndex();
	Assert.assertEquals(9, buf.writerIndex());
}
```

### 查找
ByteBuf提供丰富的API让我查找某个Byte
```java
ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(50);
buf.writeBytes(new byte[]{1, 2, 3, 4 ,5, 6, 7, 8, 9});

// 在指定的范围内查找某个byte
int idx = buf.indexOf(0, buf.writerIndex(), (byte)2);
System.out.println(idx);	// 1

idx = buf.indexOf(3, buf.writerIndex(), (byte)2);
System.out.println(idx);	// -1

// 在[readerIndex, writerIndex]之间查找值
idx = buf.bytesBefore((byte)2);
System.out.println(idx);  // 4

buf.readBytes(3);
idx = buf.bytesBefore((byte)2);
System.out.println(idx); // -1

// 在[readerIndex, writerIndex]之间遍历查找值
idx = buf.forEachByte(b -> b == (byte) 6);
System.out.println(idx); // 3
```

### derived buffers
ByteBuf提供多种API用于创建某个ByteBuf的视图或者复制版本
* `duplicate()` 复制ByteBuf对象, 俩个对象共享同一个缓冲区,但是各自维护自己的索引(readerIndex, writerIndex)
* `copy()` 复制ByteBuf对象, 俩个对象共享有自己的缓冲区, 缓冲区和索引都不共享
* `slice()`  复制Bytebuf对象,但是只复制[readerIndex, writerIndex]区间的缓冲区, 俩个对象的缓冲区是共享的,但是维护各自的索引

### get set
ByteBuf不仅仅支持read, write的顺序读写还支持get,set的随机读取。 但是get/set不会进行自动拓容.
```java
ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(50);
buf.writeBytes(new byte[]{1, 2, 3, 4 ,5, 6, 7, 8, 9});

byte b = buf.getByte(2);
System.out.println(buf.readableBytes());	// 9
System.out.println(buf.readerIndex());		// 0
System.out.println(b);		// 3
```

## 内存池
Netty的内存池由`PoolArea`. `PoolArea`由多个`PoolChunk`组成. 

## ButeBuf 类型
看完ByteBuf的API操作我们来看一下ByteBuf的分类,在内存使用种类上ByteBuf分为以下俩类
* DirectByteBuf : 使用JVM堆外内存分配. 虽然分配和回收速度慢一些,但是从SocketChannel中写入或者读取数据由于少了一次内存复制,因此速度较快.(SocketIO通信时适合使用)
* HeapByteBuf: 使用JVM堆内内存分配. 内存分配和回收速度较快,但是读写Socket IO的时候由于会额外进行一次内存复制,堆内存对应的缓冲区复制到内核Channel中,性能会有下降.(后端业务在编解码时适合使用)

在内存使用种类上由分为以下俩类
* PooledByteBuf: 基于内存对象池的ByteBuf, 
* UnpooledByteBuf: 

> UnpooledDirectByteBuf, UnpooledHeapByteBuf, UnpooledUnsafeDirectByteBuf ,PooledDirectByteBuf, PooledHeapByteBuf

## AbstractByteBuf
`AbstractByteBuf`继承自`ByteBuf`, 它内部并没有定义ByteBuf的缓冲区实现,只是通过定义`readerIndex`, `writerIndex`, `capacity`等实现ByteBuf接口中的各种API, 具体的缓冲区实现则由子类实现
```java
static final ResourceLeakDetector<ByteBuf> leakDetector = new ResourceLeakDetector<ByteBuf>(ByteBuf.class);

int readerIndex;
private int writerIndex;
private int markedReaderIndex;
private int markedWriterIndex;

private int maxCapacity;

private SwappedByteBuf swappedBuf;
```

除了操作具体缓冲区API没有实现之外 `AbstractByteBuf`为我们实现了大量的API,首先我们看一下读数据的API
```java
@Override
public ByteBuf readBytes(byte[] dst, int dstIndex, int length) {
	// 检查当前缓冲区中的可读数据是否满足length长度
    checkReadableBytes(length);
    // 将当前缓冲区的数据从readerIndex开始读取length个长度到目标dst缓冲区中. 
    // 这个方法也就是拷贝一部分数据到新的缓冲区中,但是并不会改变当前缓冲区的readerIndex和writerIndex
    getBytes(readerIndex, dst, dstIndex, length);
    readerIndex += length;
    return this;
}
```
下面我们看一下写数据的API实现
```java
@Override
public ByteBuf writeBytes(byte[] src, int srcIndex, int length) {
    ensureWritable(length);
    setBytes(writerIndex, src, srcIndex, length);
    writerIndex += length;
    return this;
}
```
同样的`setBytes();`是由子类具体实现, 我们着重看一下`ensureWritable()`方法实现
```java
@Override
public ByteBuf ensureWritable(int minWritableBytes) {
	// 如果要写入数据的字节小于0的话, 则直接抛出异常
    if (minWritableBytes < 0) {
        throw new IllegalArgumentException(String.format(
                "minWritableBytes: %d (expected: >= 0)", minWritableBytes));
    }

	// minWritableBytes <= capacity() - writerIndex, 要写入的字节数小于可写的字节数则直接返回
    if (minWritableBytes <= writableBytes()) {
        return this;
    }

    if (minWritableBytes > maxCapacity - writerIndex) {
        throw new IndexOutOfBoundsException(String.format(
                "writerIndex(%d) + minWritableBytes(%d) exceeds maxCapacity(%d): %s",
                writerIndex, minWritableBytes, maxCapacity, this));
    }

    // Normalize the current capacity to the power of 2.
    int newCapacity = calculateNewCapacity(writerIndex + minWritableBytes);

    // Adjust to the new capacity.
    capacity(newCapacity);
    return this;
}
```

### ResourceLeakDetector
`ResourceLeakDetector`用于检测内存泄漏. 它被所有ByteBuf实例共享.

### SwappedByteBuf

## AbstractReferenceCountedByteBuf

## UnPooledHeapByteBuf
不使用对象池的基于堆内存分配的字节缓冲区. 每次IO读写的时候都会创建一个新的UnPooledHeapByteBuf.
















