title: springboot
---
# 添加依赖
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
<!--<dependency>-->
	<!--<groupId>org.springframework.boot</groupId>-->
	<!--<artifactId>spring-boot-starter-security</artifactId>-->
	<!--<version>1.2.3.RELEASE</version>-->
<!--</dependency>-->
<!--<dependency>-->
	<!--<groupId>org.springframework.boot</groupId>-->
	<!--<artifactId>spring-boot-starter-remote-shell</artifactId>-->
	<!--<version>1.2.3.RELEASE</version>-->
<!--</dependency>-->
<!-- -->
<!--<dependency>-->
	<!--<groupId>org.springframework.boot</groupId>-->
	<!--<artifactId>spring-boot-starter-data-mongodb</artifactId>-->
	<!--<version>1.2.3.RELEASE</version>-->
<!--</dependency>-->
<!--<dependency>-->
	<!--<groupId>org.springframework.boot</groupId>-->
	<!--<artifactId>spring-boot-starter-jetty</artifactId>-->
	<!--<version>1.2.3.RELEASE</version>-->
<!--</dependency>-->
<!--<dependency>-->
	<!--<groupId>org.springframework.boot</groupId>-->
	<!--<artifactId>spring-boot-starter-redis</artifactId>-->
	<!--<version>1.2.3.RELEASE</version>-->
<!--</dependency>-->
<!--<dependency>-->
	<!--<groupId>org.springframework.boot</groupId>-->
	<!--<artifactId>spring-boot-starter-data-elasticsearch</artifactId>-->
	<!--<version>1.2.3.RELEASE</version>-->
<!--</dependency>-->
```

# 构建应用
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
```

```java
@Component
@RequestMapping("/")
// 该注解用来绑定HttpSession中的attribute对象的值,便于在方法中的参数里使用.
@SessionAttributes("test")
public class TestAction {

	// http://localhost:8080/h
	@RequestMapping("/h")
	@ResponseBody
	String home() {
		return "home:Hello World!";
	}

	@RequestMapping(value = "post", method = RequestMethod.POST)
	@ResponseBody
	String post() {
		return "post:Hello World!";
	}

	// http://localhost:8080/pathVariable/a
	@RequestMapping("/pathVariable/{paramV}")
	@ResponseBody
	String pathVariable(@PathVariable String paramV) {
		return paramV;
	}

	// http://localhost:8080/requestHeader
	@RequestMapping("/requestHeader")
	@ResponseBody
	String requestHeader(@RequestHeader("Accept-Encoding") String encoding) {
		return "Accept-Encoding : " + encoding;
	}

	//
	@RequestMapping("/cookieValue")
	@ResponseBody
	String cookieValue(@CookieValue("SESSION_ID") String v) {
		return v;
	}

	/**
	 * A） 常用来处理简单类型的绑定,可以处理get 方式中queryString的值,也可以处理post方式中 body data的值;
	 * 
	 * B）用来处理Content-Type: 为
	 * application/x-www-form-urlencoded编码的内容,提交方式GET、POST;
	 * 
	 * C) 该注解有两个属性： value、required; value用来指定要传入值的id名称,required用来指示参数是否必须绑定;
	 * 
	 * @param v
	 * @return
	 */
	// http://localhost:8080/requestParam?v=v&n=n
	@RequestMapping("/requestParam")
	@ResponseBody
	String requestParam(@RequestParam String v) {
		return v;
	}

	/**
	 * 该注解常用来处理Content-Type:
	 * 不是application/x-www-form-urlencoded编码的内容,例如application/json,
	 * application/xml等;
	 * 
	 * 它是通过使用HandlerAdapter 配置的HttpMessageConverters来解析post data
	 * body,然后绑定到相应的bean上的.
	 * 
	 * 因为配置有FormHttpMessageConverter,所以也可以用来处理application/x-www-form-urlencoded的内容,
	 * 处理完的结果放在一个MultiValueMap<String, String>里,这种情况在某些特殊需求下使用,详情查看FormHttpMessageConverter api;
	 * 
	 * @param v
	 * @return
	 */
	//
	@RequestMapping("/requestBody")
	@ResponseBody
	String requestBody(@RequestBody String v) {
		return v;
	}

	//
	@RequestMapping("/modelAttribute")
	@ResponseBody
	String modelAttribute(@ModelAttribute String v) {
		return v;
	}
}

```