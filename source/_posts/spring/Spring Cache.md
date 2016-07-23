category: Spring Guice
date: 2016-06-15
title: Spring Cache
---
[官方文档](https://spring.io/guides/)学习


我们先看一段基准测试代码, 我们不开启缓存功能
```java
package testSpringCache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@SpringBootApplication(scanBasePackages = "testSpringCache")
public class CacheApp {
	public static void main(String[] args) {
		ConfigurableApplicationContext app = SpringApplication.run(CacheApp.class, args);
		PrintService printService = app.getBean("printService", PrintService.class);
		printService.printTime(1);
		printService.printTime(1);
		printService.printTime(1);
	}
}

@Service
class PrintService {
	@Autowired
	TimeRepository timeRepository;

	public void printTime(int type) {
		System.out.println(timeRepository.currentTimeMillis(type));
	}
}

@Component
class TimeRepository {

	public String currentTimeMillis(int type) {
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {}
		return type + " : " + System.currentTimeMillis();
	}
}
```
最后的时间输出为
```bash
1 : 1465959306110
1 : 1465959307110
1 : 1465959308110
```
我们看到是每隔一秒输出的，然后我们加上Cache功能
```java
package testSpringCache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@SpringBootApplication(scanBasePackages = "testSpringCache")
@EnableCaching
public class CacheApp {
	public static void main(String[] args) {
		ConfigurableApplicationContext app = SpringApplication.run(CacheApp.class, args);
		PrintService printService = app.getBean("printService", PrintService.class);
		printService.printTime(1);
		printService.printTime(1);
		printService.printTime(1);
	}
}

@Service
class PrintService {
	@Autowired
	TimeRepository timeRepository;

	public void printTime(int type) {
		System.out.println(timeRepository.currentTimeMillis(type));
	}
}

@Component
class TimeRepository {

	@Cacheable("currentTimeMillis")
	public String currentTimeMillis(int type) {
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {}
		return type + " : " + System.currentTimeMillis();
	}
}
```
然后再看一下时间输出
```bash
1 : 1465959376657
1 : 1465959376657
1 : 1465959376657
```
我们看到时间立马就输出了,也就是说下面俩次的请求其实是请求的是第一次的缓存结果，

也许你注意到了，我在代码里添加了一个变量称为type, 那么我为什么要添加它呢？其实这是为了测试是否取缓存的前提是请求的参数是否相同，也就是咱们的方法参数是否相同, 刚才我使用的都是1,1,1. 下面我修改为1,2,2看一下结果
```bash
1 : 1465959531983
2 : 1465959532983
2 : 1465959532983
```
好了结果很显而易见了， 当我们修改了参数之后，spring就不会再取缓存结果而是又重新获取了一遍


最后添加所需要的Maven依赖
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.springframework</groupId>
    <artifactId>gs-scheduling-tasks</artifactId>
    <version>0.1.0</version>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>1.3.3.RELEASE</version>
    </parent>

    <properties>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```