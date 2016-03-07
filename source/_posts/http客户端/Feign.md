category: http客户端
date: 2015-12-23
title: Feign 初探
---
RESTful 服务的HTTP客户端连接器
```
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.httpclient.ApacheHttpClient;
import feign.slf4j.Slf4jLogger;

import java.util.List;

public class Main {

	public static void main(String... args) {
		GitHub github = Feign.builder()
				.decoder(new GsonDecoder())		// 设置解码器, 我们使用Gson将应答解析成Contributor
				.encoder(new GsonEncoder())
				.logger(new Slf4jLogger())		// 设置日志
				.client(new ApacheHttpClient())		// 我们使用Apache的HttpClient组件
				.target(GitHub.class, "https://api.github.com");

		List<Contributor> contributors = github.contributors("netflix", "feign");
		System.out.println("netflix :" + contributors.size());

		List<Contributor> contributors1 = github.contributors("ming15", "VertxServer");
		System.out.println("ming15 :" + contributors1.size());
	}
}
interface GitHub {
	@RequestLine("GET /repos/{owner}/{repo}/contributors")
	@Headers("Content-Type: application/json")
	List<Contributor> contributors(@Param("owner") String owner, @Param("repo") String repo);
}

class Contributor {
	String login;
	int contributions;
}
```