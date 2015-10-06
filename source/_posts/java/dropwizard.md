category: java
title: Dropwizard
---

# Setting Up Maven

在MAVEN的dependency里添加`metrics-core`库
```xml
<dependencies>
    <dependency>
        <groupId>io.dropwizard.metrics</groupId>
        <artifactId>metrics-core</artifactId>
        <version>${metrics.version}</version>
    </dependency>
</dependencies>
```
注意，使用上面依赖你需要在pom文件里声明了`metrics.version`属性,并且该属性值是`3.1.0`

# Meters

`meter`表示的是单位时间内事件数的比例(例如每秒请求数). 除了平均速率之外, `meter`仍然会追踪`1-,5-,15-`分钟的移动平均数.
```java
private final Meter requests = metrics.meter("requests");

public void handleRequest(Request request, Response response) {
    requests.mark();
    // etc
}
```
上面的`meter`表示每秒请求数的比例。

# Console Reporter

`Console Reporter`正如其名,向控制台进行输出日志,下面的示例将每秒进行输出一次.
```java
ConsoleReporter reporter = ConsoleReporter.forRegistry(metrics)
       .convertRatesTo(TimeUnit.SECONDS)
       .convertDurationsTo(TimeUnit.MILLISECONDS)
       .build();
   reporter.start(1, TimeUnit.SECONDS);
```

# Complete getting started

下面是一个完整的示例：
```java
  package sample;
  import com.codahale.metrics.*;
  import java.util.concurrent.TimeUnit;

  public class GetStarted {
    static final MetricRegistry metrics = new MetricRegistry();
    public static void main(String args[]) {
      startReport();
      Meter requests = metrics.meter("requests");
      requests.mark();
      wait5Seconds();
    }

  static void startReport() {
      ConsoleReporter reporter = ConsoleReporter.forRegistry(metrics)
          .convertRatesTo(TimeUnit.SECONDS)
          .convertDurationsTo(TimeUnit.MILLISECONDS)
          .build();
      reporter.start(1, TimeUnit.SECONDS);
  }

  static void wait5Seconds() {
      try {
          Thread.sleep(5*1000);
      }
      catch(InterruptedException e) {}
  }
}
```
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <groupId>somegroup</groupId>
  <artifactId>sample</artifactId>
  <version>0.0.1-SNAPSHOT</version>
  <name>Example project for Metrics</name>

  <dependencies>
    <dependency>
      <groupId>io.dropwizard.metrics</groupId>
      <artifactId>metrics-core</artifactId>
      <version>${metrics.version}</version>
    </dependency>
  </dependencies>
</project>
```

注意：使用上面依赖你需要在pom文件里声明了`metrics.version`属性,并且该属性值是`3.1.0`

```
mvn package exec:java -Dexec.mainClass=sample.First
```

# The Registry

Metrics的核心部分是`MetricRegistry`类,这个类是应用程序中所有的metrics的容器. 下面的示例创建一个新的`MetricRegistry`:
```java
final MetricRegistry metrics = new MetricRegistry();
```
如果你在应用程序中嵌入一个自己创建的`MetricRegistry`实例，你应该将这个属性置为静态的.

# Gauges

`gauge`表示的是一个瞬时值. 例如我们获取队列里待执行的任务数
```xml
public class QueueManager {
    private final Queue queue;

    public QueueManager(MetricRegistry metrics, String name) {
        this.queue = new Queue();
        metrics.register(MetricRegistry.name(QueueManager.class, name, "size"),
                         new Gauge<Integer>() {
                             @Override
                             public Integer getValue() {
                                 return queue.size();
                             }
                         });
    }
}
```
当完成计算之后,它将会返回队列里的任务数。

在`registry`里的每个`metric`都有一个唯一的名字,其命名规范为用`.`分割的字符串,例如`things.count`或者`com.example.Thing.latency`. `MetricRegistry`类提供了一个静态方法来构建这些名字.
```xml
MetricRegistry.name(QueueManager.class, "jobs", "size")
```
上面的调用会返回`com.example.QueueManager.jobs.size`。

对于大多数队列或者类队列结构,你也许仅想要获得`queue.size()`这个值. 大多数`java.util`和`java.util.concurrent`包都实现了`size()`方法,它的复杂度是`O(n)`,这意味着你的`gauge`也许会很慢(也许还会持有锁)

# Counters

`counter`是一个内部采用`AtomicLong`计数器的`gauge`实现. 你可以增加或者减少这个值.例如,我们想要一种更加高效的计算队列大小的方式:
```xml
private final Counter pendingJobs = metrics.counter(name(QueueManager.class, "pending-jobs"));

public void addJob(Job job) {
    pendingJobs.inc();
    queue.offer(job);
}

public Job takeJob() {
    pendingJobs.dec();
    return queue.take();
}
```
每一次业务逻辑的调用，counter都会被计算一次,它会返回队列中的任务数.

正如你看到的,counter的API是非常不同的是,`counter(String)`取代了`register(String, Metric)`，然而你可以仍然可以使用`register`方法创建你自己的`Counter`实例,实际上`counter(String)`在内部里已经将这些工作都为你做好了,还允许你使用相同的名字对metric进行复用

还需要说明一点,在上例中,我们静态引入了`MetricRegistry`的`name`方法.

# Histograms

`histogram`表示的是流中数据值的静态分布. 除了计算`minimum, maximum, mean, etc`等值,它还计算中间值或者`75th, 90th, 95th, 98th, 99th, 99.9th`等百分比.
```xml
private final Histogram responseSizes = metrics.histogram(name(RequestHandler.class, "response-sizes"));

public void handleRequest(Request request, Response response) {
    // etc
    responseSizes.update(response.getContent().length);
}
```
上面的`histogram`统计了响应中的字节数.

# Timers
`timer`可以计算某个代码段的调用比例,和调用期间的分布状况.
```xml
private final Timer responses = metrics.timer(name(RequestHandler.class, "responses"));

public String handleRequest(Request request, Response response) {
    final Timer.Context context = responses.time();
    try {
        // etc;
        return "OK";
    } finally {
        context.stop();
    }
}
```
This timer will measure the amount of time it takes to process each request in nanoseconds and provide a rate of requests in requests per second.


# Health Checks

Metrics还可以通过`metrics-healthchecks`模块集中检查你的服务的健康.

首先创建一个新的`HealthCheckRegistry`实例
```xml
final HealthCheckRegistry healthChecks = new HealthCheckRegistry();
Second, implement a HealthCheck subclass:

public class DatabaseHealthCheck extends HealthCheck {
    private final Database database;

    public DatabaseHealthCheck(Database database) {
        this.database = database;
    }

    @Override
    public HealthCheck.Result check() throws Exception {
        if (database.isConnected()) {
            return HealthCheck.Result.healthy();
        } else {
            return HealthCheck.Result.unhealthy("Cannot connect to " + database.getUrl());
        }
    }
}
```
然后将Metrics注册到它身上：
```xml
healthChecks.register("postgres", new DatabaseHealthCheck(database));
```
接下来运行所有的health checks:
```xml
final Map<String, HealthCheck.Resultresults = healthChecks.runHealthChecks();
for (Entry<String, HealthCheck.Resultentry : results.entrySet()) {
    if (entry.getValue().isHealthy()) {
        System.out.println(entry.getKey() + " is healthy");
    } else {
        System.err.println(entry.getKey() + " is UNHEALTHY: " + entry.getValue().getMessage());
        final Throwable e = entry.getValue().getError();
        if (e != null) {
            e.printStackTrace();
        }
    }
}
```
Metrics内置了一种health check：`ThreadDeadlockHealthCheck`,它使用了java内置的线程死锁检测来查找死锁线程.

# Reporting Via JMX

通过`JMX`报告metrics：
```xml
final JmxReporter reporter = JmxReporter.forRegistry(registry).build();
reporter.start();
```
一旦reporter启动了,registry中的所有的metrics都可以通过`JConsole`或者`VisualVM`看到.

Metrics被包装成`JMX MBeans`,可以在`VisualVM's MBeans browser`查看`Metrics`.

注意：在VisualVM中，你双击任一metric属性,VisualVM将会将这些属性数据通过图形化的方式展示给你.

# Reporting Via HTTP

Metrics仍然可以通过servlet(AdminServlet)展示给你, 提供JSON形式的数据. 它可以报告`health checks`,打印`thread dump`,或者提供一个负载均衡的简单响应. (它还提供了其他的`servlets–MetricsServlet`,例如`HealthCheckServlet, ThreadDumpServlet`或者`PingServlet`.)

如果想要使用servlet你必须在pom文件中依赖`metrics-servlets`.
```xml
<dependency>
    <groupId>io.dropwizard.metrics</groupId>
    <artifactId>metrics-servlets</artifactId>
    <version>${metrics.version}</version>
</dependency>
```

# Other Reporting

除了`JMX`和`HTTP`以外,Metrics还提供了下面的报告方式

* `STDOUT`: 使用`metrics-core`的`ConsoleReporter`报告
* `CSV files`, 使用`metrics-core`的`CsvReporter`报告
* `SLF4J loggers`, 使用`metrics-core`的`Slf4jReporter`报告
* `Ganglia`, 使用`metrics-ganglia`的`GangliaReporter`报告
* `Graphite`, 使用`metrics-graphite`的`GraphiteReporter`报告 