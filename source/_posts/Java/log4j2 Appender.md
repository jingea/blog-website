category: Java
date: 2016-05-12
title: Log4J2 Appender
---
Log4j2为我们提供了非常多的Appender, 我们就是通过Appender最终将日志输出的.

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

下面我们重点看一下, 日常开发中常用的一些

## Async
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
AsyncAppender会在一个单独的线程中将日志写到其他的Appender中. 

## ConsoleAppender
将日志输出到控制台
```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="warn" name="MyApp" packages="">
  <Appenders>
    <Console name="STDOUT" target="SYSTEM_OUT">
      <PatternLayout pattern="%m%n"/>
    </Console>
  </Appenders>
  <Loggers>
    <Root level="error">
      <AppenderRef ref="STDOUT"/>
    </Root>
  </Loggers>
</Configuration>
```

## FailoverAppender
FailoverAppender主要是用于当日志没办法输出到指定Appender的时候对这些日志的一个备选方案.
```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="warn" name="MyApp" packages="">
  <Appenders>
    <RollingFile name="RollingFile" fileName="logs/app.log" filePattern="logs/app-%d{MM-dd-yyyy}.log.gz"
                 ignoreExceptions="false">
      <PatternLayout>
        <Pattern>%d %p %c{1.} [%t] %m%n</Pattern>
      </PatternLayout>
      <TimeBasedTriggeringPolicy />
    </RollingFile>
    <Console name="STDOUT" target="SYSTEM_OUT" ignoreExceptions="false">
      <PatternLayout pattern="%m%n"/>
    </Console>
    <Failover name="Failover" primary="RollingFile">
      <Failovers>
        <AppenderRef ref="Console"/>
      </Failovers>
    </Failover>
  </Appenders>
  <Loggers>
    <Root level="error">
      <AppenderRef ref="Failover"/>
    </Root>
  </Loggers>
</Configuration>
```

## FileAppender
将日志输出到fileName 文件里
```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="warn" name="MyApp" packages="">
  <Appenders>
    <File name="MyFile" fileName="logs/app.log">
      <PatternLayout>
        <Pattern>%d %p %c{1.} [%t] %m%n</Pattern>
      </PatternLayout>
    </File>
  </Appenders>
  <Loggers>
    <Root level="error">
      <AppenderRef ref="MyFile"/>
    </Root>
  </Loggers>
</Configuration>
```

## RandomAccessFileAppender
根据Log4j2的官网介绍RandomAccessFileAppender相比FileAppender有20-200% 的性能提升
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

## RollingFileAppender
```xml

```

## RollingRandomAccessFileAppender
同样RollingRandomAccessFileAppender比RollingFileAppender有20%~200%的性能提升

## SyslogAppender
将日志输出到RSyslog中
```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="warn" name="MyApp" packages="">
  <Appenders>
    <Syslog name="bsd" host="localhost" port="514" protocol="TCP"/>
    <Syslog name="RFC5424" format="RFC5424" host="localhost" port="8514"
            protocol="TCP" appName="MyApp" includeMDC="true"
            facility="LOCAL0" enterpriseNumber="18060" newLine="true"
            messageId="Audit" id="App"/>
  </Appenders>
  <Loggers>
    <Logger name="com.mycorp" level="error">
      <AppenderRef ref="RFC5424"/>
    </Logger>
    <Root level="error">
      <AppenderRef ref="bsd"/>
    </Root>
  </Loggers>
</Configuration>
```
