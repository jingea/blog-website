category: Spring
date: 2015-04-08
title: SpringBoot 
---
SpringBoot提供了一种快速构建应用的方式. 它会根据Classpath和beans上的现有配置来推测你缺少哪些配置, 从而将缺少的配置自动加载.

```java
package hello;

import java.util.Arrays;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(Application.class, args);

        System.out.println("Let's inspect the beans provided by Spring Boot:");

        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            System.out.println(beanName);
        }
    }

}
```
`@SpringBootApplication`聚合了以下注解
* `@Configuration` 标记该class为应用上下文的bean资源.
* `@EnableAutoConfiguration` 该注解是让Spring自动从classpath, 其他beans以及配置文件中加载bean. 
* `@EnableWebMvc` 开启Spring的MVN功能, 但是当Spring Boot 在classpath中发现SpringMVN依赖后会自动开启该模块(依赖了`@EnableAutoConfiguration`). 这注解会将当前应用作为一个web应用启动.
* `@ComponentScan` Spring 会在指定包下搜索components, configurations, 和services. 在本例中, 它会在hello包中所搜, 以及找到一个`HelloController`.

* `@Configuration` 注解类等价与XML中配置beans. (用@Bean标注方法等价于XML中配置bean)