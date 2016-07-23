category: Spring Guice
date: 2015-04-09
title: SpringBoot Web 服务
---
## 添加依赖
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

## 构建应用
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

## Get路径参数
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


## 完整路径
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

## 指定HEADER
```java
// http://localhost:8080/requestHeader
@RequestMapping("/requestHeader")
@ResponseBody
String requestHeader(@RequestHeader("Accept-Encoding") String encoding) {
	return "Accept-Encoding : " + encoding;
}
```

## 设置Cookie
```java
@RequestMapping("/cookieValue")
@ResponseBody
String cookieValue(@CookieValue("SESSION_ID") String v) {
	return v;
}
```

## RestController
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