category: Spring
date: 2016-
title: Spring Cache
---
[官方文档](https://spring.io/guides/gs/scheduling-tasks/)学习


测试代码
```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Date;

@SpringBootApplication
public class CacheApp {
    public static void main(String[] args) {
        ConfigurableApplicationContext app = SpringApplication.run(CacheApp.class, args);
        StringRepository simpleBookRepository = app.getBean("stringRepository", StringRepository.class);
        String time1 = simpleBookRepository.time();
        String time2 = simpleBookRepository.time();
        String time3 = simpleBookRepository.time();
        System.out.println(time1);
        System.out.println(time2);
        System.out.println(time3);
    }
}

interface TimeRepository {
    String time();
}

@Component
class StringRepository implements TimeRepository {

    @Override
    @Cacheable("time")
    public String time() {
        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {}
        return new Date().toLocaleString();
    }
}
```
结果为
```bash

```

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