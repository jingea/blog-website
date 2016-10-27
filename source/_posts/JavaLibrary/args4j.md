category: Java Library
date: 2015-12-30
title: args4j 初探
---
args4j可以方便地解析CUI（命令行接口）程序的输入参数或选项, 将其解析到一个参数类中.

我们使用MAVEN测试一下这个工具, 首先我们创建一个MAVEN项目
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>wang.ming15.args4j</groupId>
    <artifactId>demo</artifactId>
    <version>1.0-SNAPSHOT</version>

    <dependencies>
        <dependency>
            <groupId>args4j</groupId>
            <artifactId>args4j</artifactId>
            <version>2.32</version>
        </dependency>
    </dependencies>
</project>
```
然后创建一个参数类
```java
public class Args {
	public String name;

	public void run() {
		System.out.println("Args Init");
		System.out.println("- name: " + name);
	}
}
```
然后写一个主类
```java
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public class Main {
	public static void main(String[] args) {
		Args args1 = new Args();
		CmdLineParser parser = new CmdLineParser(args1);
		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			// handling of wrong arguments
			System.err.println(e.getMessage());
			parser.printUsage(System.err);
		}
		System.out.println(args1.name);
	}
}
```
接着执行`mvn package`打出一个包, 然后执行命令
```java
java -jar ./target/demo-1.0-SNAPSHOT.jar Args -name "Hello World"
```
最后我们会看到输出
