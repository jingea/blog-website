category: Java
date: 2016-09-02
title: Log4J2 高性能的Appender
---
Log4j2为我们提供了非常多的Appender, 我们就是通过Appender最终将日志输出到磁盘的.

* Async
* Console
* Failover
* File
* Flume
* JDBC
* JMS Queue
* JMS Topic
* JPA
* Kafka
* Memory Mapped File
* NoSQL
* Output Stream
* Random Access File
* Rewrite
* Rolling File
* Rolling Random Access File
* Routing
* SMTP
* Socket
* Syslog
* ZeroMQ/JeroMQ

## AsyncAppender
首先我们看一下AsyncAppender。 AsyncAppender通过一个单独的线程将LogEvent发送给它内部代理的其他的Appender，业务逻辑线程可以快速返回调用。AsyncAppender内部封装了一个`java.util.concurrent.ArrayBlockingQueue`用于接收日志事件。在多线程的情况下并不推荐使用这个Appender，因为BlockingQueue对于锁争夺是非常敏感的，在多线程并发写日志的时候，性能会下降。
官方推荐使用(lock-free Async Loggers )[http://logging.apache.org/log4j/2.x/manual/async.html]

下来我们看一下这个Appender的几个重点参数
* blocking：如果设置为tue的话(默认值), 当BlockingQueue满的时候，新的日志文件会一直阻塞, 直到可以入列为止. 如果为false的话, 不能入列的日志会被写到 error appender 里。
* bufferSize：队列里的最大日志事件数量(默认是128)
* errorRef：当没有Appender可用或者写日志发生错误或者队列满的时候，新的日志事件会被写到这个errorRef里。如果不设置的话，那些日志都会丢失
```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="warn" name="MyApp" packages="">
  <Appenders>
    <File name="MyFile" fileName="logs/app.log">
      <PatternLayout>
        <Pattern>%d %p %c{1.} [%t] %m%n</Pattern>
      </PatternLayout>
    </File>
    <Async name="Async">
      <AppenderRef ref="MyFile"/>
    </Async>
  </Appenders>
  <Loggers>
    <Root level="error">
      <AppenderRef ref="Async"/>
    </Root>
  </Loggers>
</Configuration>
```

## MemoryMappedFileAppender
MemoryMappedFileAppender是在2.1的版本是行新增加的。 其通过将指定的文件映射到内存，然后将日志直接写到映射内存里。这个Appender主要依赖于操作系统的虚拟内存将映射内存里的数据同步到磁盘上。相比与传统的通过系统调用方式将数据写到磁盘上，这里只是将数据同步到内存里。速度提升了很多倍。在大多数的操作系统里，memory region实际上映射的是内核区的page cache，这意味着，在用户空间之内就不需要再创建一份数据拷贝了。

但是将一个文件映射到内存里还是有一些消耗的，特别是映射一些特别大的文件(500M以上)，默认的region大小是32M。

和FileAppender和RandomAccessFileAppender很像，MemoryMappedFileAppender使用MemoryMappedFileManager来执行IO操作。

同样的来看几个比较重要的属相
* append：新的日志事件是否追加在日志文件末尾（如果不是原先的内容会被刷新掉）
* regionLength：映射的region的大小，默认是32M。这个值必须在`256`和`1,073,741,824`字节之间。

```java
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="warn" name="MyApp" packages="">
  <Appenders>
    <MemoryMappedFile name="MyFile" fileName="logs/app.log">
      <PatternLayout>
        <Pattern>%d %p %c{1.} [%t] %m%n</Pattern>
      </PatternLayout>
    </MemoryMappedFile>
  </Appenders>
  <Loggers>
    <Root level="error">
      <AppenderRef ref="MyFile"/>
    </Root>
  </Loggers>
</Configuration>
```

## RandomAccessFileAppender
RandomAccessFileAppender与标准的`FileAppender`非常像, 只不过RandomAccessFileAppender不能像FileAppender关闭缓冲功能。RandomAccessFileAppender内部使用`ByteBuffer` 和 `RandomAccessFile`实现(FileAppender基于BufferedOutputStream)

在官方的测试中RandomAccessFileAppender比开启了缓冲功能的FileAppender的性能提升了`20 ~ 200`倍.

* append:新的日志事件是否追加在日志文件末尾（如果不是原先的内容会被刷新掉）
* immediateFlush: 设置为true的话(默认为true)，每一次写入都会强制执行一次flush, 这种情况下回保证数据肯定被写到磁盘上,但是对性能有消耗. 只有当使用同步logger的时候，每一次写入日志都flush才有意义。这是因为当使用异步logger或者Appender的时候，当events达到固定数量的时候回自动执行flush操作（即使immediateFlush为false,也会执行刷新），使用异步的方式同样保证数据会写入到磁盘而且更高效。
* bufferSize：缓冲大小，默认是 262,144 bytes (256 * 1024).
