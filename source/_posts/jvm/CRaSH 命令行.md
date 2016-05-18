category: JVM
date: 2016-05-15
title: CRaSH 安装启动
---
CRaSH 的全名是 Common Reusable SHell . 网上对其介绍是: 基于 Java 发布提供与 JVM 进行交互的 SHELL 环境. 作为入门, 这篇文章介绍一下, 作为单独程序的使用.

我在mac上使用brew安装上里CRaSH, 大家可以根据自己的环境自行安装.

我启动了一个SpringBoot HTTP服务, 然后使用CRaSH连接到这个服务上.
```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@EnableAutoConfiguration
public class HTTPServer {

    @RequestMapping("/")
    @ResponseBody
    String home() {
        return "Hello World!";
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(HTTPServer.class, args);
    }
}
```
然后在命令行找到这个进程id
```bash
➜  ~ jps -l
467
488 org.jetbrains.idea.maven.server.RemoteMavenServer
696 com.intellij.rt.execution.application.AppMain
697 org.jetbrains.jps.cmdline.Launcher
845 sun.tools.jps.Jps
➜  ~ crash.sh 696

   _____     ________                 _______    ____ ____
 .'     `.  |        `.             .'       `. |    |    | 1.3.0
|    |    | |    |    |  .-------.  |    |    | |    |    |
|    |____| |    `   .' |   _|    |  .    '~_ ` |         |
|    |    | |    .   `.  .~'      | | `~_    `| |         |
|    |    | |    |    | |    |    | |    |    | |    |    |
 `._____.'  |____|____| `.________|  `._____.'  |____|____|

Follow and support the project on http://www.crashub.org
Welcome to localhost + !
It is Sun May 15 10:44:03 CST 2016 now
%
```

最后出现`%`说明我们已经连接到SpringBoot服务所在的虚拟机里了. 同样的在SpringBoot服务的日志里也有输出
```bash
2016-05-15 10:44:00.005  INFO 696 --- [Attach Listener] org.crsh.standalone.Agent                : CRaSH agent loaded
2016-05-15 10:44:00.007  INFO 696 --- [Attach Listener] org.crsh.standalone.Agent                : Spawned CRaSH thread 23 for further processing
2016-05-15 10:44:00.097  INFO 696 --- [       Thread-3] org.crsh.standalone.Bootstrap            : Configuring property vfs.refresh_period=1 from properties
2016-05-15 10:44:00.203  INFO 696 --- [       Thread-3] org.crsh.standalone.Bootstrap            : Configuring property ssh.port=2000 from properties
2016-05-15 10:44:00.203  INFO 696 --- [       Thread-3] org.crsh.standalone.Bootstrap            : Configuring property ssh.auth_timeout=600000 from properties
2016-05-15 10:44:00.204  INFO 696 --- [       Thread-3] org.crsh.standalone.Bootstrap            : Configuring property ssh.idle_timeout=600000 from properties
2016-05-15 10:44:00.204  INFO 696 --- [       Thread-3] org.crsh.standalone.Bootstrap            : Configuring property ssh.default_encoding=UTF-8 from properties
2016-05-15 10:44:00.204  INFO 696 --- [       Thread-3] org.crsh.standalone.Bootstrap            : Configuring property auth=simple from properties
2016-05-15 10:44:00.204  INFO 696 --- [       Thread-3] org.crsh.standalone.Bootstrap            : Configuring property telnet.port=5000 from properties
2016-05-15 10:44:00.204  INFO 696 --- [       Thread-3] org.crsh.standalone.Bootstrap            : Configuring property mail.debug=false from properties
2016-05-15 10:44:00.205  INFO 696 --- [       Thread-3] org.crsh.standalone.Bootstrap            : Configuring property auth.simple.username=admin from properties
2016-05-15 10:44:00.205  INFO 696 --- [       Thread-3] org.crsh.standalone.Bootstrap            : Configuring property auth.simple.password=admin from properties
2016-05-15 10:44:02.111  INFO 696 --- [       Thread-6] net.wimpi.telnetd.net.PortListener       : Listening to Port 5,000 with a connectivity queue size of 5.
2016-05-15 10:44:02.700  INFO 696 --- [       Thread-3] org.crsh.standalone.Agent                : Callback back remote on port 50656
```

当我们在启动时, 还可以指定一些参数设置
* `--cmd` : 这个选项用于指定, 我们要执行的命令所在的目录. 如果不指定的话, 会默认地从当前classpath下的`/crash/commands/`进行查找
* `--conf` : 用于指定配置文件所在的目录, 可以指定多个目录
* `--property` : `--cmd`可选参数, 可以覆盖配置文件中的配置, 示例`crash.sh --property crash.telnet.port=3000`

下面我们看一下在CRaSH中都可以使用哪些命令
```bash
% help
Try one of these commands with the -h or --help switch:

NAME      DESCRIPTION
clock
cron      manages the cron plugin
dashboard a monitoring dashboard
date      show the current time
egrep     search file(s) for lines that match a pattern
env       display the term env
filter    a filter for a stream of map
hello
java      various java language commands
jdbc      JDBC connection
jmx       Java Management Extensions
jndi      Java Naming and Directory Interface
jpa       Java persistance API
jul       java.util.logging commands
jvm       JVM informations
less      opposite of more
mail      interact with emails
man       format and display the on-line manual pages
shell     shell related command
sleep     sleep for some time
sort      sort a map
system    vm system properties commands
thread    JVM thread commands
help      provides basic help
repl      list the repl or change the current repl
```
CRaSH为我们提供了非常多的命令, 对于命令的具体用法, 大家可以使用`jvm --help`这样的用法具体看一下. CRaSH还提供了pipline, 我们可以使用`|`管道符使用多个命令.


具体的命令这篇文章就不再多介绍了, 下面我们看一下如何将CRaSH内嵌到Spring里. 为了简单, 我并没有直接使用CRaSH官网里介绍的那样, 使用xml配置Spring
```java
import org.crsh.spring.SpringBootstrap;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        SpringBootstrap springBootstrap = new SpringBootstrap();
        Properties properties = new Properties();
        properties.setProperty("crash.vfs.refresh_period", "1");
        properties.setProperty("crash.ssh.port", "2000");
        properties.setProperty("crash.ssh.idle_timeout", "3000000");
        properties.setProperty("crash.telnet.port", "5000");
        properties.setProperty("crash.ssh.auth_timeout", "300000");
        properties.setProperty("crash.auth", "simple");
        properties.setProperty("crash.auth.simple.username", "admin");
        properties.setProperty("crash.auth.simple.password", "admin");
        springBootstrap.setConfig(properties);

        TimeUnit.DAYS.sleep(1);
    }
}
```
然后启动一个客户端
```bash
➜  ~ telnet localhost 5000
Trying ::1...
Connected to localhost.
Escape character is '^]'.

   _____     ________                 _______    ____ ____
 .'     `.  |        `.             .'       `. |    |    | 1.3.0
|    |    | |    |    |  .-------.  |    |    | |    |    |
|    |____| |    `   .' |   _|    |  .    '~_ ` |         |
|    |    | |    .   `.  .~'      | | `~_    `| |         |
|    |    | |    |    | |    |    | |    |    | |    |    |
 `._____.'  |____|____| `.________|  `._____.'  |____|____|

Follow and support the project on http://www.crashub.org
Welcome to localhost + !
It is Sun May 15 11:29:56 CST 2016 now

%
```
通过telnet, 我们也成功连接进来了.

这个需要添加的CRaSH的maven依赖有
```xml
<!-- http://mvnrepository.com/artifact/org.crashub/crash.shell -->
<dependency>
    <groupId>org.crashub</groupId>
    <artifactId>crash.shell</artifactId>
    <version>1.3.2</version>
</dependency>

<!-- http://mvnrepository.com/artifact/org.crashub/crash.cli -->
<dependency>
    <groupId>org.crashub</groupId>
    <artifactId>crash.cli</artifactId>
    <version>1.3.2</version>
</dependency>

<!-- http://mvnrepository.com/artifact/org.crashub/crash.packaging -->
<dependency>
    <groupId>org.crashub</groupId>
    <artifactId>crash.packaging</artifactId>
    <version>1.3.2</version>
</dependency>

<!-- http://mvnrepository.com/artifact/org.crashub/crash.embed.spring -->
<dependency>
    <groupId>org.crashub</groupId>
    <artifactId>crash.embed.spring</artifactId>
    <version>1.3.2</version>
</dependency>
```
