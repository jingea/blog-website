category: Java
tag: http客户端
date: 2016-01-20
title: Retrofit 初探
---
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