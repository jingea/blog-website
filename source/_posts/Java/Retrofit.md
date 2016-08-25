category: Java
date: 2016-01-20
title: Retrofit 初探
---
首先添加Maven依赖
```xml
<dependency>
	<groupId>com.squareup.retrofit2</groupId>
	<artifactId>retrofit</artifactId>
	<version>2.0.2</version>
</dependency>
```
> 注意我们使用的是retrofit2

## Get请求
Retrofit 将HTTP API转换为了接口形式, 如下
```java
public interface GitHubService {
  @GET("users/{user}/repos")
  Call<List<Repo>> listRepos(@Path("user") String user);
}
```
然后`Retrofit`会自动完成其实现类
```java
Retrofit retrofit = new Retrofit.Builder()
    .baseUrl("https://api.github.com")
    .build();

GitHubService service = retrofit.create(GitHubService.class);
```
在上面的示例中我们完成了一个`Get`请求, 然后使用`@Path`进行参数替换

## Post请求
上面我们展示的是一个`Get`请求, 下面我们再看一个`Post`请求的示例
```java
@POST("users/new")
Call<User> createUser(@Body User user);
```
`@Body`注解会将`User`对象转换为请求体

## form-encoded
我们我们想要将格式转换为`form-encoded`形式, 参考如下示例
```java
@FormUrlEncoded
@POST("user/edit")
Call<User> updateUser(@Field("first_name") String first, @Field("last_name") String last);
```
`@Field`注解会组成一个个字典结构的数据进行发送.

## HEADER
有时候我们也许想要设置其他的消息头, 我们可以如此做
```java
@Headers("Cache-Control: max-age=640000")
@GET("widget/list")
Call<List<Widget>> widgetList();
```

## 异步
我们在`Call`对象中分别可以调用异步和同步方法进行通信
```java
Retrofit retrofit = new Retrofit.Builder()
		.baseUrl("https://api.github.com")
		.build();

GitHubService service = retrofit.create(GitHubService.class);

Call<List<String>> repos = service.listRepos("octocat");
	// 同步调用
	Response<List<String>> result = repos.execute();

	// 异步调用
	repos.enqueue(new Callback() {

		@Override
		public void onResponse(Response response) {

		}

		@Override
		public void onFailure(Throwable t) {

		}
	});
```

## 测试
在我的windows的机器上进行测试, 只是测试一下Retrofit的性能消耗
```java
	@Test
	public void testIte1Minite() throws IOException {
		int count = 0;
		long start = System.currentTimeMillis();
		while (true) {
			long end = System.currentTimeMillis();
			if ((end - start) > 1000 * 60) {
				break;
			}
			Retrofit retrofit = new Retrofit.Builder()
					.baseUrl("http://192.168.15.20:9091")
					.addConverterFactory(GsonConverterFactory.create())
					.build();
			ServerListService loginServerPushService = retrofit.create(ServerListService.class);
			loginServerPushService.serverlist().execute().body();
			count++;
		}
		System.out.println(count);
	}

	public interface ServerListService {
		@GET("server/serverlist")
		Call<MyServer> serverlist();
	}

	public class MyServer {
		public List<Map<String, String>> serverlist;
		public List<Map<String, String>> serverlogin;
	}
```
结果如图
![]()
```java
	@Test
	public void testIte1Minite() throws IOException {
		int count = 0;
		long start = System.currentTimeMillis();
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl("http://192.168.15.20:9091")
				.addConverterFactory(GsonConverterFactory.create())
				.build();
		while (true) {
			long end = System.currentTimeMillis();
			if ((end - start) > 1000 * 60) {
				break;
			}
			ServerListService loginServerPushService = retrofit.create(ServerListService.class);
			loginServerPushService.serverlist().execute().body();
			count++;
		}
		System.out.println(count);
	}
```
结果如图
![]()
这俩次的结果都能达到每分钟13000个请求, 吞吐量和性能消耗是差不多.

## Converter
Retrofit2为我们提供了多种转换器
* Gson: com.squareup.retrofit2:converter-gson
* Jackson: com.squareup.retrofit2:converter-jackson
* Moshi: com.squareup.retrofit2:converter-moshi
* Protobuf: com.squareup.retrofit2:converter-protobuf
* Wire: com.squareup.retrofit2:converter-wire
* Simple XML: com.squareup.retrofit2:converter-simplexml
* Scalars (primitives, boxed, and String): com.squareup.retrofit2:converter-scalars
在使用Retrofit2的时候, 必须指定Converter, 否则程序在运行中会报错. Scalars 只是支持String和基本类型的装包和拆包