title: spring boot
---
# Spring Boot 注解

## 类注解

#### @RestController
这被称为一个构造型(stereotype) 注解.告诉Spring以字符串的形式渲染结果, 并直接返回给调用者

#### @RequestMapping
提供路由信息. 它告诉Spring任何来自 "/"路径的HTTP请求都应该被映射到 home 方法.

#### @EnableAutoConfiguration
 这个注解告诉Spring Boot根据添加的jar依赖猜测你想如何配置Spring.由于 spring- boot- starter- web 添加了 Tomcat和Spring MVC, 所以auto-configuration将假定你正在开发一个web应用并相应地对Spring进行设置.
 
###### 如果发现应用了你不想要的特定自 动配置类, 你可以使用 @EnableAutoConfiguration 注解的排除属性来禁用它们.
```java
@Configuration
@EnableAutoConfiguration( exclude={DataSourceAutoConfiguration. class})
public class MyConfiguration {
}
```
 
#### @Configuration 

#### @SpringBootApplication
很多Spring Boot开发者总是使用 @Configuration , @EnableAutoConfiguration 和 @ComponentScan 注解他们的main类. 由于这些
注解被如此频繁地一块使用 , Spring Boot提供一个方便的 @SpringBootApplication 选择
```java
@SpringBootApplication // same as @Configuration @EnableAutoConfiguration @ComponentScan
public class Application {
public static void main( String[] args) {
SpringApplication. run( Application. class, args) ;
}
}
```

#### @Import 
注解可以用来导入其他配置类

#### @ComponentScan 
注解自 动收集所有的Spring组件.经常使用 @ComponentScan 注解搜索beans, 并结合 @Autowired 构造器注入.
添加 @ComponentScan 注解而不需要任何参数. 你的所有应用程序组件( @Component , @Service , @Repository , @Controller 等) 将被自 动注册为Spring Beans.
```java
@Service
public class DatabaseAccountService implements AccountService {
private final RiskAssessor riskAssessor;
@Autowired
public DatabaseAccountService( RiskAssessor riskAssessor) {
this. riskAssessor = riskAssessor;
}
}
```
#### @ImportResource 
注解加载XML配置文件

> 你可以通过将 @EnableAutoConfiguration 或 @SpringBootApplication 注解添加到一个 @Configuration 类上来选择自 动配置.
> 你只需要添加一个 @EnableAutoConfiguration 注解. 我们建议你将它添加到主 @Configuration 类上.














