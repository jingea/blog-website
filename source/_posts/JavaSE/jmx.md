参考[Java SE Monitoring and Management Guide](http://docs.oracle.com/javase/8/docs/technotes/guides/management/toc.html)

## MBean Server
MBean Server 是MBean的一个仓库, 我们并不直接访问MBean, 而是通过通过一个唯一的`ObjectName`通过 MBean Server来进行访问.

如果要实现一个MBean Server必须实现`javax.management.MBeanServer`接口.

如果我们要创建自己的MBean Server的话, 可以使用
* `MBeanServerFactory.createMBeanServer();`
* `MBeanServerFactory.newMBeanServer();`

`createMBeanServer()`内部会调用`newMBeanServer()`, 但是`createMBeanServer()`会将`newMBeanServer()`创建出来的MBean Server缓存到`ArrayList<MBeanServer> mBeanServerList`一个列表里. 这点先姑且不去讨论, 集中精力看看Platform MBean Server.

官方也建议我们使用Platform MBean Server(也就是自带的那个MBean Server), 在没有特殊需求之下, 没有必要建立自己的MBean Server.

```java
import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;

public class PlatformMBeanServerTest {

    public static void main(String[] args) {
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        System.out.println("MBeanCount : " + server.getMBeanCount());
    }
}
```

## MBean
看完MBServer, 我们来看一下MBean.

### MXBean 连接
首先先看一下[MXBean](http://docs.oracle.com/javase/8/docs/technotes/guides/management/overview.html#gdeuk), 什么是MXBean呢? 它是一种用来监控和管理Java VM的MBean.

我们可以通过三种方式来访问MXBean
* 通过`ManagementFactory`直接进行访问.
* 通过`MXBean proxy`直接访问.
* 通过`MBeanServerConnection`间接访问.

如果在同一个VM上的话, 我们可以通过ManagementFactory 提供的API进行直接访问
* `getClassLoadingMXBean()`
* `getGarbageCollectorMXBeans()`
* `getRuntimeMXBean()`
等等

例如
```java
RuntimeMXBean mxbean = ManagementFactory.getRuntimeMXBean();
String vendor = mxbean.getVmVendor(); 
```

如果不在同一个VM的话, 我们可以通过MXBean Proxy的方式进行远程访问.
```java
MBeanServerConnection mbs;
...
// Get a MBean proxy for RuntimeMXBean interface
RuntimeMXBean proxy = ManagementFactory.newPlatformMXBeanProxy(mbs,
                                             ManagementFactory.RUNTIME_MXBEAN_NAME,
                                             RuntimeMXBean.class);
// Get standard attribute "VmVendor"
String vendor = proxy.getVmVendor();
```

通过
```java
MBeanServerConnection mbs;
...
try {
  ObjectName oname = new ObjectName(ManagementFactory.RUNTIME_MXBEAN_NAME);
  // Get standard attribute "VmVendor"
  String vendor = (String) mbs.getAttribute(oname, "VmVendor");
} catch (....) {
  // Catch the exceptions thrown by ObjectName constructor
  // and MBeanServer.getAttribute method
  ...
}
```

当我们通过上述方式拿到MXBean之后, 就可以访问它里面的各种属性了.
```java
com.sun.management.OperatingSystemMXBean mxbean =
  (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

// Get the number of processors
int numProcessors = mxbean.getAvailableProcessors();

// Get the Oracle JDK-specific attribute Process CPU time
long cpuTime = mxbean.getProcessCpuTime();
```

### 监控线程和CPU

`ThreadMXBean `提供了对线程和CPU的监控. 
在使用这个功能之前, 我们可能需要判断一下, Java VM是否开启了线程content监控
```java
ThreadMXBean.isThreadContentionMonitoringSupported()
```
如果没有开启的话, 我们调用一下
```java
setThreadContentionMonitoringEnabled()
```
开启它就可以了.

还有对线程统计的支持
* `isThreadCpuTimeSupported()`检测是否开启
* `isCurrentThreadCpuTimeSupported()` 上面那个是对任意线程的统计检测, 这个则是对并发线程的检测

同理, 对CPU需要同样地处理
* `isThreadCpuTimeEnabled` 检测CPU
* `setThreadCpuTimeEnabled()` 开启检测CPU

### 操作系统的管理
通过`OperatingSystem `可以拿到操作系统相关的信息
* CPU的运行时间(Process CPU time).
* 物理内存剩余和总共大小.
* committed virtual memory数量. (这个值表示的是当前运行的进程还可使用的虚拟内存的大小, 也就是在程序启动时分配的虚拟内存现在还剩下多少).
* 交换分区剩余和总共大小.
* 打开的文件()数量(因为Uniux哲学是一切皆文件, 所以这个值只支持Solaris, Linux, or Mac OS X).


### 日志管理
为了记录日志, Java特地提供了一个特殊的接口`LoggingMXBean`, 使用这个接口我们可以完成下面任务

* 获取指定logger的日志级别  
* 获取到当前注册的logger列表
* 获取到指定logger的父名称  
* 设置指定logger新的日志级别


`LoggingMXBean` 的`ObjectName `为`java.util.logging:type=Logging`. 这个名称存储在`LogManager.LOGGING_MXBEAN_NAME`里

```java
LoggingMXBean loggingMXBean = LogManager.getLoggingMXBean();
System.out.println(loggingMXBean.getLoggerNames());
```
得到的loggerName有
* `javax.management.snmp`
* `global`
* `javax.management.notification`
* `javax.management.modelmbean`
* `javax.management.timer`
* `javax.management`
* `javax.management.mlet`
* `javax.management.mbeanserver`
* `javax.management.snmp.daemon`
* `javax.management.relation`
* `javax.management.monitor`
* `javax.management.misc`
由于我的测试没有使用任何的日志系统, 因此它只打印了Java自带的一些logger, 但是如果你在SpringBoot或者自己添加上log4j的话, 会获取到更多的logger, 我们可以通过这个接口, 在服务器运行阶段动态修改logger级别.

> 尽管以前也做过修改日志级别的事情, 但是是通过第三方框架自带的api进行修改的, 但是使用这个MXBean, 貌似可以跨框架了, O(∩_∩)O~























































