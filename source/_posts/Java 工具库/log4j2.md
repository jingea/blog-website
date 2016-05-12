category: Java工具
date: 
title: Log4J2 初探
---

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