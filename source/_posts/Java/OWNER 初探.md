category: Java
date: 2016-01-19
title: OWNER 初探
---
OWNER是一个Java库，目标是最大限度的减少应用程序中处理Java properties的代码。

主要功能
* 加载策略：OWNER通过匹配接口类名和properties文件名自动解析并映射；也可以通过注解定制properties文件名。
* 导入properties：另外一种加载properties文件到映射接口的方法。
* 参数化properties：另外一个实用功能，给接口方法提供参数，通过参数配置。
* 类型转换：支持从String类型到基本类型和枚举类型的转换。
* 变量扩展：引用properties中的其他属性。
* 热加载：支持热加载。
* 可访问和可变：可以继承Config的子接口Accessible或者Mutable实现属性的可访问和可变。
* 调试：支持调试功能。
* 禁用功能：可禁用引起问题的功能。
* 配置ConfigFactory：ConfigFactory也是可配置的。
* XML支持：支持XML配置。
* 事件支持：OWNER实现了功能丰富的事件系统，使你知道热加载的发生和属性变化。
* 单例模式：配置信息在一个应用中是单例的。

OWNER同样是开源的, 我们可以使用maven来引用它
```xml
<dependencies>
    <dependency>
        <groupId>org.aeonbits.owner</groupId>
        <artifactId>owner</artifactId>
        <version>1.0.8</version>
    </dependency>
</dependencies>
```
或者使用java8版本
```xml
<dependencies>
        <dependency>
            <groupId>org.aeonbits.owner</groupId>
            <artifactId>owner-java8</artifactId>
            <version>1.0.6</version>
        </dependency>
</dependencies>
```

## 基本用法
我们现在在MAVEN项目中测试一下
首先我们在`test\src\main\java\ownerTest`目录下创建一个配置类
```java
package ownerTest;

import org.aeonbits.owner.Config;

public interface ServerConfig extends Config {
	int port();
	String hostname();
	@DefaultValue("42")
	int maxThreads();
}
```
然后在`test\src\main\resources\ownerTest`目录下创建配置文件`ServerConfig.properties`
```java
port=80
hostname=foobar.com
maxThreads=100
```
然后我们书写一个测试类
```java
package ownerTest;

import org.aeonbits.owner.ConfigFactory;

public class Main {

	public static void main(String[] args) {
		ServerConfig cfg = ConfigFactory.create(ServerConfig.class);
		System.out.println("Server " + cfg.hostname() + ":" + cfg.port() + " will run " + cfg.maxThreads());
	}
}
```
这时OWNER会自动地将`ServerConfig.properties`配置文件匹配到`ServerConfig`上, 输出结果为
```java
Server foobar.com:80 will run 100
```
我们看到在`ServerConfig`里我们使用了一个`@DefaultValue`注解, 当在配置文件里找不到这个值的时候, 这个注解值会为我们设置上注解里的默认值.
> 如果在配置文件里找不到这个设置也没有添加`@DefaultValue`会产生一个空指针异常

有时候我们在配置文件里会使用`server.http.port=80`的配置, 那么这种情况下我们就在使用`@Key`注解
```java
package ownerTest;

import org.aeonbits.owner.Config;

public interface ServerConfig extends Config {
	@Key("server.http.port")
	int port();

	@Key("server.host.name")
	String hostname();

	@Key("server.max.threads")
	@DefaultValue("42")
	int maxThreads();
}
```
我们的配置文件如下
```java
server.http.port=80
server.host.name=foobar.com
server.max.threads=100
```

## 加载策略
正如上文所说, OWNER是按照classpath去自动匹配配置类和配置文件的, 但是其实我们可以自定义配置文件的加载策略, 如下例
```java
@Sources({ "file:~/.myapp.config",
           "file:/etc/myapp.config",
           "classpath:foo/bar/baz.properties" })
public interface ServerConfig extends Config {
    @Key("server.http.port")
    int port();

    @Key("server.host.name")
    String hostname();

    @Key("server.max.threads");
    @DefaultValue("42")
    int maxThreads();
}
```
我们使用了`@Sources`指定配置文件的路径, 按照上面的代码, 它依次按照下面的流程进行文件匹配,一旦匹配成功就进行加载忽略后面的文件
1. `file:~/.myapp.config`  从home目录开始查找
2. `file:/etc/myapp.config`  从绝对目录进行查找
3. `classpath:foo/bar/baz.properties`  classpath从classpath中查找

其实上面的加载策略称为`LoadType.FIRST`(完整注解`@LoadPolicy(LoadType.FIRST)`). 我们还有其他选择`LoadType.MERGE`, 示例如下:
```java
@LoadPolicy(LoadType.MERGE)
@Sources({ "file:~/.myapp.config",
           "file:/etc/myapp.config",
           "classpath:foo/bar/baz.properties" })
public interface ServerConfig extends Config {
    ...
}
```
上面的加载策略是, 不管当前路径下是否找到了都会进行路径下查找, 但与`LoadType.FIRST`不同的是, 当找到之后它会替换之前知道的配置文件.
> TODO 是局部替换还是整个文件一起替换呢？

## 参数化
我们还可以向配置里传递参数
```java
public interface Sample extends Config {
    @DefaultValue("Hello Mr. %s!")
    String helloMr(String name);
}

Sample cfg = ConfigFactory.create(Sample.class);
print(cfg.helloMr("Luigi")); // will println 'Hello Mr. Luigi!'
```
> OWNER还为我们提供了`@DisableFeature`注解, 让我们关闭参数化功能. 这个注解可以用在类或者方法的级别上

## 自定义类型
当我们使用OWNER的时候, 不仅仅可以使用原生类型, 还可以使用数组, 集合甚至是自定义类型

下来我们自定义一个类型
```java

```

下面我们定义一个数组和集合
```java
public class MyConfig extends Config {

  @DefaultValue("apple, pear, orange")
  public String[] fruit();

  @Separator(";")
  @DefaultValue("0; 1; 1; 2; 3; 5; 8; 13; 21; 34; 55")
  public int[] fibonacci();

  @DefaultValue("1, 2, 3, 4")
  List<Integer> ints();

  @DefaultValue(
    "http://aeonbits.org, http://github.com, http://google.com")
  MyOwnCollection<URL> myBookmarks();

  // Concrete class are allowed (in this case java.util.Stack)
  // when type is not specified <String> is assumed as default
  @DefaultValue(
    "The Lord of the Rings,The Little Prince,The Da Vinci Code")
  Stack books();

}
```
OWNER默认使用`,`分割元素. 但是我们可以通过`@Separator(";")`指定使用`;`进行切割, 需要注意的是OWNER只支持数组, 集合, Java原生类型, 并不支持`Map`.
> 支持的集合有`Collection, List, Set, SortedSet`

虽然我们可以使用`@Separator(";")`进行切割, 但是如果我们有更复杂的切割逻辑的话, 这可能就不再符合需求了, 我们可以使用`@TokenizerClass`来实现更复杂的切割逻辑
```java
public class MyConfig extends Config {

    @Separator(";")
    @DefaultValue("0; 1; 1; 2; 3; 5; 8; 13; 21; 34; 55")
    public int[] fibonacci();

    @TokenizerClass(CustomDashTokenizer.class)
    @DefaultValue("foo-bar-baz")
    public String[] withSeparatorClass();

}

public class CustomDashTokenizer implements Tokenizer {

    // this logic can be as much complex as you need
    @Override
    public String[] tokens(String values) {
        return values.split("-", -1);
    }
}
```
> 虽然 `@Separator(";")` 和  `@TokenizerClass(CustomDashTokenizer.class)`都可以在方法和类的级别上进行注解, 但是他们不允许同时出现在同一个级别上, 而且当分别出现在了方法和类的级别上后, 方法上的注解会替换类上的注解.

OWNER还提供了`@ConverterClass`注解来实现更加复杂的转换逻辑
```java
interface MyConfig extends Config {
    @DefaultValue("foobar.com:8080")
    @ConverterClass(ServerConverter.class)
    Server server();

    @DefaultValue(
      "google.com, yahoo.com:8080, owner.aeonbits.org:4000")
    @ConverterClass(ServerConverter.class)
    Server[] servers();
}

class Server {
    private final String name;
    private final Integer port;

    public Server(String name, Integer port) {
        this.name = name;
        this.port = port;
    }
}

public class ServerConverter implements Converter<Server> {
    public Server convert(Method targetMethod, String text) {
        String[] split = text.split(":", -1);
        String name = split[0];
        Integer port = 80;
        if (split.length >= 2)
            port = Integer.valueOf(split[1]);
        return new Server(name, port);
    }
}

MyConfig cfg = ConfigFactory.create(MyConfig.class);
Server s = cfg.server(); // will return a single server
Server[] ss = cfg.servers(); // it works also with collections
```

OWNER 支持的全部自动转换类型

* 原生类型: boolean, byte, short, integer, long, float, double.
* 枚举
* java.lang.String
* java.net.URL, java.net.URI.
* java.io.File
* java.lang.Class
* 公有构造器只有一个`java.lang.String`的类
* 公有构造器只有一个`java.lang.Object`的类
* 被`public static`修饰, 签名为`valueOf(java.lang.String)`返回自身的方法
* 带有上述元素的数组
* 带有上述类的集合(`Set, List, SortedSet or concrete implementations like LinkedHashSet`). Map and sub-interfaces are not supported.

## 变量表达式
OWNER还提供了一个非常霸道的功能 —— 变量表达式, 参考如下配置文件
```
story=The ${animal} jumped over the ${target}
animal=quick ${color} fox
target=${target.attribute} dog
target.attribute=lazy
color=brown
```
然后定义一个配置类
```java
public interface ConfigWithExpansion extends Config {
    String story();
}
```
猜猜会输出什么, 对了
```java
The quick brown fox jumped over the lazy dog
```
我们可以在配置文件里引用其他的配置

同样的我们还可以在配置类完成这样的功能
```java
public interface ConfigWithExpansion
        extends Config {

    @DefaultValue(
        "The ${animal} jumped over the ${target}")
    String story();

    @DefaultValue("quick ${color} fox")
    String animal();

    @DefaultValue("${target.attribute} dog")
    String target();

    @Key("target.attribute")
    @DefaultValue("lazy")
    String targetAttribute();

    @DefaultValue("brown")
    String color();
}

ConfigWithExpansion conf = ConfigFactory
    .create(ConfigWithExpansion.class);

String story = conf.story();
```

如果我们不需要开启变量表达式的话, 我们可以使用`@DisableFeature(VARIABLE_EXPANSION)`
```java
public interface Sample extends Config {
    @DefaultValue("Earth")
    String world();

    @DisableFeature(VARIABLE_EXPANSION)
    @DefaultValue("Hello ${world}.")

    // will return the string "Hello ${world}."
    String sayHello();
}
```
