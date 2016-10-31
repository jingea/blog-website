category: Java Library
tag: Spring
date: 2015-04-08
title: SpringBoot
---
## SpringBoot
SpringBoot提供了一种快速构建应用的方式. 它会根据Classpath和beans上的现有配置来推测你缺少哪些配置, 从而将缺少的配置自动加载.

我们来构建一个最简单的应用来测试一下这个功能

我们首先在Maven中添加一个非常简单的Spring依赖
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
</dependency>
```
然后上我们的Java代码
```java
package testSpringBoot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class App {

	public static void main(String[] args) {
		ConfigurableApplicationContext app = SpringApplication.run(App.class, args);

		PrintNameService printHelloWorldService = app.getBean("printHelloWorldService", PrintNameService.class);
		printHelloWorldService.printHelloWorld();
	}

}
```
`@SpringBootApplication`聚合了以下注解
* `@Configuration` 标记该class为应用上下文的bean资源.
* `@EnableAutoConfiguration` 该注解是让Spring自动从classpath, 其他beans以及配置文件中加载bean.
* `@EnableWebMvc` 开启Spring的MVN功能, 但是当Spring Boot 在classpath中发现SpringMVN依赖后会自动开启该模块(依赖了`@EnableAutoConfiguration`). 这注解会将当前应用作为一个web应用启动.
* `@ComponentScan` Spring 会在指定包下搜索components, configurations, 和services. 在本例中, 它会在hello包中所搜, 以及找到一个`HelloController`.

* `Autowired` : 注入值. 可以用于属性, 方法, 构造器等.

然后我们可以使用下面提供的注解来标记Spring中的Bean
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

我们采用Service
```java
package testSpringBoot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PrintNameService {

	public void printHelloWorld() {
		System.out.println(nameGenerateService.lily());
	}

	@Autowired
	private NameGenerateService nameGenerateService;
}

package testSpringBoot;

import org.springframework.stereotype.Service;

@Service
public class NameGenerateService {

	public String lily() {
		return "Lily";
	}
}
```
最后就输出了Lily.

> 注意, SpringBoot不能放在启动程序的根目录下, 例如`src/main/java`这样的目录下, 必须再封装一层, 例如`src/main/java/testSpringBoot`下


## web
```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-web</artifactId>
	<version>1.2.3.RELEASE</version>
</dependency>
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot</artifactId>
	<version>1.2.3.RELEASE</version>
</dependency>
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-actuator</artifactId>
	<version>1.2.3.RELEASE</version>
</dependency>
```

构建应用
```java
@Controller
// 告诉Spring Boot根据添加的jar依赖猜测如何配置Spring
@EnableAutoConfiguration	
@ComponentScan
public class App {

    public static void main(String[] args) {
    	SpringApplication.run(App.class, new String[0]);
    }
}

@Component
@RequestMapping("/")
public class TestAction {

	// http://localhost:8080/h
	@RequestMapping("/h")
	@ResponseBody
	String home() {
		return "home:Hello World!";
	}
}
```
上面给出了一个最简单的HTTP应用. 上面默认的就是一个GET请求, 我们还可以自己定义其他请求`@RequestMapping(value = "post", method = RequestMethod.POST)`.

我们还可以指定路径参数
```java
// http://localhost:8080/pathVariable/a
@RequestMapping("/pathVariable/{paramV}")
@ResponseBody
String pathVariable(@PathVariable String paramV) {
	return paramV;
}
```

### Get路径参数
```java
// http://localhost:8080/requestParam?v=v&n=n
@RequestMapping("/requestParam")
@ResponseBody
String requestParam(@RequestParam String v) {
	return v;
}
```
常用来处理以下情况
* 常用来处理简单类型的绑定,可以处理get 方式中queryString的值,也可以处理post方式中 body data的值;
* 用来处理Content-Type: 为application/x-www-form-urlencoded编码的内容,提交方式GET、POST;
* 该注解有两个属性： value、required; value用来指定要传入值的id名称,required用来指示参数是否必须绑定;


### 完整路径
该注解常用来处理Content-Type:
* 不是application/x-www-form-urlencoded编码的内容,例如application/json, application/xml等;
* 它是通过使用HandlerAdapter 配置的HttpMessageConverters来解析post data body,然后绑定到相应的bean上的.
* 因为配置有FormHttpMessageConverter,所以也可以用来处理application/x-www-form-urlencoded的内容,处理完的结果放在一个MultiValueMap<String, String>里,这种情况在某些特殊需求下使用,详情查看FormHttpMessageConverter api;
```java
@RequestMapping("/requestBody")
@ResponseBody
String requestBody(@RequestBody String v) {
	return v;
}
```

### 指定HEADER
```java
// http://localhost:8080/requestHeader
@RequestMapping("/requestHeader")
@ResponseBody
String requestHeader(@RequestHeader("Accept-Encoding") String encoding) {
	return "Accept-Encoding : " + encoding;
}
```

### 设置Cookie
```java
@RequestMapping("/cookieValue")
@ResponseBody
String cookieValue(@CookieValue("SESSION_ID") String v) {
	return v;
}
```

### RestController
Spring Boot官网还提供了一种更简单的配置
```java
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class HelloController {

    @RequestMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

}
```
使用`RestController`表示HelloController要使用Spring MVC来处理web请求了. `RestController`内部聚合了`@Controller `和`@ResponseBody`注解. `RequestMapping`将`/`映射到`index()`方法. 