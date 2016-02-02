category: java工具
tag: jmh
date: 2015-12-28
title: JMH 环境搭建
---
JMH是一个用于java或者其他JVM语言的, 提供构建,运行,分析多种基准的工具.
我们通过使用引入依赖的方式,在我们的项目中添加基准测试
```xml
<dependency>
    <groupId>org.openjdk.jmh</groupId>
    <artifactId>jmh-generator-annprocess</artifactId>
    <version>1.11.2</version>
</dependency>
<dependency>
    <groupId>org.openjdk.jmh</groupId>
    <artifactId>jmh-core</artifactId>
    <version>1.11.2</version>
</dependency>
```

但是, JMH的官方推荐使用方式是,使用MAVEN构建一个基于要测试项目的一个独立的项目. 

我们使用archetype生成基准测试项目
```
mvn archetype:generate -DinteractiveMode=false -DarchetypeGroupId=org.openjdk.jmh -DarchetypeArtifactId=jmh-java-benchmark-archetype -DgroupId=wang.ming15.jmh -DartifactId=test -Dversion=1.0
```
执行完该命令后会生成一个新的项目,里面会有一个测试文件
```java
package wang.ming15.jmh;

import org.openjdk.jmh.annotations.Benchmark;

public class MyBenchmark {

    @Benchmark
    public void testMethod() {
        // This is a demo/sample template for building your JMH benchmarks. Edit as needed.
        // Put your benchmark code here.
    }

}
```

然后我们依赖要测试的项目就可以在新的项目中写基准测试到代码了