category: Java Library
date: 2016-05-12
title: Log4J2 笔记
---

## 示例
Log4J2会使用`ConfigurationFactory`从classpath上依次尝试加载下面的配置文件, 一旦找到就停止查找过程
1. log4j.configurationFil
2. log4j2-test.properties
3. log4j2-test.yaml or log4j2-test.yml
4. log4j2-test.json or log4j2-test.jsn
5. log4j2-test.xml
6. log4j2.properties
7. log4j2.yaml or log4j2.yml
8. log4j2.json or log4j2.jsn
9. log4j2.xml
从上面的配置文件,我们可以看到Log4J2支持, JSON, YAML, properties, XML 等四种格式的配置文件.

如果找不到配置文件的话, 就会使用默认的配置
* 想root logger 关联一个ConsoleAppender (root logger的默认等级是Level.ERROR)
* ConsoleAppender指定一个PatternLayout, 其格式内容为`%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n`

官网给出了一个简单的示例
```java
import com.foo.Bar;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
 
public class MyApp {
 
    // Define a static logger variable so that it references the
    // Logger instance named "MyApp".
    private static final Logger logger = LogManager.getLogger(MyApp.class);
 
    public static void main(final String... args) {
 
        // Set up a simple configuration that logs on the console.
 
        logger.trace("Entering application.");
        Bar bar = new Bar();
        if (!bar.doIt()) {
            logger.error("Didn't do it.");
        }
        logger.trace("Exiting application.");
    }
}

package com.foo;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
 
public class Bar {
  static final Logger logger = LogManager.getLogger(Bar.class.getName());
 
  public boolean doIt() {
    logger.entry();
    logger.error("Did it again!");
    return logger.exit(false);
  }
}
```
配置文件为
```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN" monitorInterval="30">
  <Appenders>
    <Console name="Console" target="SYSTEM_OUT">
      <PatternLayout pattern="%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
    </Console>
  </Appenders>
  <Loggers>
    <Root level="error">
      <AppenderRef ref="Console"/>
    </Root>
  </Loggers>
</Configuration>
```
输出结果为
```bash
17:13:01.540 [main] TRACE MyApp - Entering application.
17:13:01.540 [main] TRACE com.foo.Bar - entry
17:13:01.540 [main] ERROR com.foo.Bar - Did it again!
17:13:01.540 [main] TRACE com.foo.Bar - exit with (false)
17:13:01.540 [main] ERROR MyApp - Didn't do it.
17:13:01.540 [main] TRACE MyApp - Exiting application.
```

我们在配置里使用了一个`monitorInterval`属性, 这个属性是用来监控日志文件的, 每隔多少秒刷新一次.

下面我们展示一个只有`com.foo.Bar`才会trace全部日志, 而其他的日志则只会输出ERROR级别的.
```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN">
  <Appenders>
    <Console name="Console" target="SYSTEM_OUT">
      <PatternLayout pattern="%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
    </Console>
  </Appenders>
  <Loggers>
    <Logger name="com.foo.Bar" level="trace" additivity="false">
      <AppenderRef ref="Console"/>
    </Logger>
    <Root level="error">
      <AppenderRef ref="Console"/>
    </Root>
  </Loggers>
</Configuration>
```
结果为
```bash
17:13:01.540 [main] TRACE com.foo.Bar - entry
17:13:01.540 [main] ERROR com.foo.Bar - Did it again!
17:13:01.540 [main] TRACE com.foo.Bar - exit (false)
17:13:01.540 [main] ERROR MyApp - Didn't do it.
```
需要注意的是,我们在com.foo.Bar这个Logger后面添加了一个`additivity="false"`的属性.

关于日志级别我们来测试一下, 我们写一个Java程序
```java
public class TestLevel {
	static final Logger logger = LogManager.getLogger(TestLookups.class.getName());

	public static void main(String[] args) {

		logger.trace("test");
		logger.debug("test");
		logger.info("test");
		logger.warn("test");
		logger.error("test");
	}
}
```
log4j2的配置文件为
```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN" monitorInterval="30">
    <Appenders>
        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout>
                <pattern>%d %p %c{1.} [%t] $${ctx:loginId} %m%n</pattern>
            </PatternLayout>
        </Console>
    </Appenders>
    <Loggers>
        <Root level="trace">
            <AppenderRef ref="Console"/>
        </Root>
    </Loggers>
</Configuration>
```
结果为
```bash
2016-05-12 18:29:55,570 TRACE t.TestLookups [main] ${ctx:loginId} test
2016-05-12 18:29:55,571 DEBUG t.TestLookups [main] ${ctx:loginId} test
2016-05-12 18:29:55,572 INFO t.TestLookups [main] ${ctx:loginId} test
2016-05-12 18:29:55,572 WARN t.TestLookups [main] ${ctx:loginId} test
2016-05-12 18:29:55,572 ERROR t.TestLookups [main] ${ctx:loginId} test
```
级别改为debug后结果为
```bash
2016-05-12 18:30:13,574 DEBUG t.TestLookups [main] ${ctx:loginId} test
2016-05-12 18:30:13,575 INFO t.TestLookups [main] ${ctx:loginId} test
2016-05-12 18:30:13,575 WARN t.TestLookups [main] ${ctx:loginId} test
2016-05-12 18:30:13,575 ERROR t.TestLookups [main] ${ctx:loginId} test
```
级别改为info后结果为
```bash
2016-05-12 18:30:43,042 INFO t.TestLookups [main] ${ctx:loginId} test
2016-05-12 18:30:43,043 WARN t.TestLookups [main] ${ctx:loginId} test
2016-05-12 18:30:43,044 ERROR t.TestLookups [main] ${ctx:loginId} test
```
级别改为warn后结果为
```bash
2016-05-12 18:31:18,095 WARN t.TestLookups [main] ${ctx:loginId} test
2016-05-12 18:31:18,096 ERROR t.TestLookups [main] ${ctx:loginId} test
```
级别改为error后结果为
```bash
2016-05-12 18:31:43,894 ERROR t.TestLookups [main] ${ctx:loginId} test
```

如果出现重复日志输出, 多半原因因为, root收集到了子logger反馈的日志, 只需要将子logger设置为`additivity="false"`就可以了,例如
```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN" monitorInterval="30">
    <Appenders>
        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout>
                <pattern>%d %p %c{1.} [%t] $${ctx:loginId} %m%n</pattern>
            </PatternLayout>
        </Console>
    </Appenders>
    <Loggers>
		<Logger name="stdout" level="info" additivity="false">
			<appender-ref ref="Console"/>
		</Logger>
        <Root level="trace">
            <AppenderRef ref="Console"/>
        </Root>
    </Loggers>
</Configuration>
```

动态修改日志级别
```java
		final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		final Configuration config = ctx.getConfiguration();

		LoggerConfig loggerConfig = config.getLoggerConfig(logger.getName());
		LoggerConfig specificConfig = loggerConfig;

		if (!loggerConfig.getName().equals(logger.getName())) {
			specificConfig = new LoggerConfig(logger.getName(), level, true);
			specificConfig.setParent(loggerConfig);
			config.addLogger(logger.getName(), specificConfig);
		}
		specificConfig.setLevel(level);
		ctx.updateLoggers();
```

## Appender
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
官方推荐使用[lock-free Async Loggers](http://logging.apache.org/log4j/2.x/manual/async.html)

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
```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="warn" name="MyApp" packages="">
  <Appenders>
    <RandomAccessFile name="MyFile" fileName="logs/app.log">
      <PatternLayout>
        <Pattern>%d %p %c{1.} [%t] %m%n</Pattern>
      </PatternLayout>
    </RandomAccessFile>
  </Appenders>
  <Loggers>
    <Root level="error">
      <AppenderRef ref="MyFile"/>
    </Root>
  </Loggers>
</Configuration>
```

## 异步日志
采用异步的方式记录日志首先是AsyncLogger
1. 异步日志通过在单独的线程中记录日志，能快速地返回日志调用。通常来说如果所有的logger都采用异步的方式的话，能获得最好的性能
2. 异步日志内部采用了Disruptor取代了java内部队列。这个特性能获得更高的吞吐量和更低的延迟。
3. 异步日志通过缓存方式，批量将日志输出
尽管异步日志有诸多特性，但是有时候仍然是需要采用同步地方式记录日志。我们下来首先看一下异步日志的优点：
1. 更高的吞吐量。异步日志与同步方式相比，它能提供6~68倍的日志输出量。当突然遇到日志洪峰时,采用异步方式能获得更快地日志响应
2. 更低的延迟. 异步日志比同步方式记录日志和采用基于队列实现的异步日志方式都获得了更低的延迟。
下面我们再看看异步记录方式的缺点
1. 当在写日志的时候，如果发生了问题或者抛出了异常，使用异步记录方式很难定位问题究竟是出现在哪里。因此在异步日志里推荐配置一个`ExceptionHandler`，但是哪怕配置了一个handler，仍然有可能出现日志丢失等问题。因为对于日志依赖严重的地方，推荐使用同步方式记录日志
2. 在单核的情况下，异步日志并不能获得更好地性能。当然现在的服务器动辄就是16核，32核，这个情况一般就不考虑了。
最后我们看一下如果所有的日志都采用异步方式的配置方式
```bash
-DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector
```
当我们在java进程的启动脚本里进行了这样的配置后(我们也可以在程序启动后再系统属性里进行设置)，log4j会采用`AsyncLoggerContextSelector`进行异步日志输出。但是在配置文件里要使用`<root>`和`<logger>`,不能使用`<asyncRoot>`和`<asyncLogger>`, 因为这种异步的logger是为了在同步异步混合logger的时候使用的。
