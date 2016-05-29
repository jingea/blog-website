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

标记Bean的注解
* `@Configuration` 注解类等价与XML中配置beans.
* `@Bean` 标注方法等价于XML中配置bean
* `@Import`
* `@DependsOn`
* `@Component` : 通用注解, 可以用于注解任何bean.
* `@Repository` : `Component`的子注解, 一般用来注解DAO类
* `@Service` : `Component`的子注解, 一般用来注解Service类
* `@Controller` : `Component`的子注解, 一般用来注解控制层
* `@Scope` : 指定bean的作用域
> `@Component`可以作为元注解去注解我们自定义的注解, 然后我们自定义的注解注解到类的时候, 也会被自动加载到SpringContext中.

注入bean值的注解
* `Required` 适用于bean属性的setter方法.
* `Autowired` : 注入值. 可以用于属性, 方法, 构造器等.
