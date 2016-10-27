category: Java Library
tag: Spring
date: 2016-04-26
title: Spring 任务调度
---
[官方文档](https://spring.io/guides/gs/scheduling-tasks/)学习


测试代码
```java
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@SpringBootApplication
@EnableScheduling
public class TestScheduledTasks {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(TestScheduledTasks.class);
	}
}

@Component
class ScheduledTasks {
	// 当任务完成之后, 再延迟fixedDelay毫秒后执行
	@Scheduled(fixedDelay = 3000)
	public void fixedDelay() throws InterruptedException {
		System.out.println("fixedDelay : " + new Date());
		TimeUnit.SECONDS.sleep(3);
	}

	// 每三秒钟执行一次
	@Scheduled(fixedRate = 3000)
	public void fixedRate() throws InterruptedException {
		TimeUnit.SECONDS.sleep(3);
		System.out.println("fixedRate : " + new Date());
	}

}
```
结果为
```bash
fixedRate : Tue Apr 26 15:59:15 CST 2016
fixedDelay : Tue Apr 26 15:59:15 CST 2016
fixedRate : Tue Apr 26 15:59:21 CST 2016
fixedRate : Tue Apr 26 15:59:24 CST 2016
fixedRate : Tue Apr 26 15:59:27 CST 2016
fixedDelay : Tue Apr 26 15:59:27 CST 2016
```
`Scheduled`的注解还有
* cron : 这个可以让我们使用cron表达式

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