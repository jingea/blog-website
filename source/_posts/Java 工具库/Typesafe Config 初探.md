category: Java工具
date: 2016-01-30
title: Typesafe Config 初探
---
Typesafe Config 是为JVM平台语言提供的配置类库.

目前config只支持配置文件，如果想从数据库获取配置文件，需要自己diy。 config库很擅长合并配置。

我们可以直接使用maven方式依赖该库
```xml
<dependency>
    <groupId>com.typesafe</groupId>
    <artifactId>config</artifactId>
    <version>1.3.0</version>
</dependency>
```

API示例
```java
import com.typesafe.config.ConfigFactory

Config conf = ConfigFactory.load();
int bar1 = conf.getInt("foo.bar");
Config foo = conf.getConfig("foo");
int bar2 = foo.getInt("bar");
```

在上面的例子中我们使用了最简单的方式`ConfigFactory.load()`加载出了一个配置类`Config`, config会自动去classPath中查找`reference.conf`. `ConfigFactory.load()`会按照下面的优先级依次从classpath中查找文件进行加载
1. `system properties`
2. `application.conf` (all resources on classpath with this name)
3. `application.json` (all resources on classpath with this name)
4. `application.properties` (all resources on classpath with this name)
5. `reference.conf` (all resources on classpath with this name)
如果我们不想要使用上面的文件名或者我们将配置分配到多个文件中,那么我们可以使用`ConfigFactory.load("test.conf");`

当然如果你想自己创建`Config`也可以调用`ConfigFactory`的`parseXXX`方法
```java
ConfigFactory.parseFile(new File(""));
ConfigFactory.parseMap(new HashMap<>());
ConfigFactory.parseProperties(new Properties());
```

> 需要注意的从`Config`实例中得到的`Config`, `ConfigParseOptions`, `ConfigResolveOptions`, `ConfigObject` 得到的对象都是不可变的.
