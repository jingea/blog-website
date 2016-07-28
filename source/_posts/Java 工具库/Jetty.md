category: Java工具
date: 2016-07-25
title: Jetty 嵌入模式
---
添加依赖
```xml
<!-- https://mvnrepository.com/artifact/org.eclipse.jetty/jetty-server -->
<dependency>
    <groupId>org.eclipse.jetty</groupId>
    <artifactId>jetty-server</artifactId>
    <version>9.4.0.M0</version>
</dependency>
```
* `HandlerCollection` ：维持一个handlers 集合, 然后按顺序依次调用每个handler(注意这个里的handler不管发生什么情况都会执行一遍, 这通常可以作为一个切面用于统计和记日志). 
* `HandlerList`：同样维持一个handlers 集合,也是按顺序调用每个handler.但是当有异常抛出, 或者有response返回, 或者 `request.isHandled()`被设为true.
* `HandlerWrapper`：继承自`HandlerWrapper`的类可以以面向切面编程的方式将handler通过链式的形式组合在一起.例如一个标准的web应用程序就实现了一个这样的规则, 他将context,session,security,servlet的handler以链式的方式组合在一起.

## 文件服务器
```java
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;

public class FileServer {
	public static void main(String[] args) throws Exception {
		Server server = new Server(8080);

		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setDirectoriesListed(true);
		resourceHandler.setWelcomeFiles(new String[] { "index.html" });
		resourceHandler.setResourceBase("D:\\repository");

		GzipHandler gzip = new GzipHandler();
		server.setHandler(gzip);
		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { resourceHandler, new DefaultHandler() });
		gzip.setHandler(handlers);

		server.start();
		server.join();
	}
}
```

## Servlets
Servlet是一种标准的处理HTTP请求逻辑的方式. Servlet和Jetty Handler非常像, 但是Servlet得request对象是不可变的.
Jetty的ServletHandler采用将请求映射到一个标准路径上.
```java
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

public class MinimalServlets {
	public static void main(String[] args) throws Exception {
		Server server = new Server(8080);

		ServletHandler handler = new ServletHandler();
		server.setHandler(handler);

		// 下面我们配置一个原生的Servlet, 还有其他的Servlet(例如通过web.xml或者@WebServlet注解生成的)可配置
		handler.addServletWithMapping(HelloServlet.class, "/*");

		server.start();
		server.join();
	}

	public static class HelloServlet extends HttpServlet {
		@Override
		protected void doGet(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
			response.setContentType("text/html");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().println("<h1>Hello from HelloServlet</h1>");
		}
	}
}
```

## Connectors
当创建N个connectors应用时, 一般我们会将通用的配置首先提取出来, 使用一个配置类配置这些通用配置. 然后在其他具体Connectors配置时将这个配置传递到具体配置类中, 这样一来就形成了一个链式配置形式.
 
首先生成一个keystore文件
```bash
D:\ssl>keytool -genkey -alias wm -keyalg RSA -keysize 1024 -keypass 123456 -validity 365 -keystore D:\ssl\wm.keystore -storepass 123456
您的名字与姓氏是什么?
  [Unknown]:  wm
您的组织单位名称是什么?
  [Unknown]:  wm
您的组织名称是什么?
  [Unknown]:  wm
您所在的城市或区域名称是什么?
  [Unknown]:  bj
您所在的省/市/自治区名称是什么?
  [Unknown]:  bj
该单位的双字母国家/地区代码是什么?
  [Unknown]:  cn
CN=wm, OU=wm, O=wm, L=bj, ST=bj, C=cn是否正确?
  [否]:  y

D:\ssl>
```
然后看一下服务器代码

```java
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ManyConnectors {
	public static void main(String[] args) throws Exception {
		// 加载ssl需要的keystore
		File keystoreFile = new File("D:\\ssl\\wm.keystore");
		if (!keystoreFile.exists()) {
			throw new FileNotFoundException(keystoreFile.getAbsolutePath());
		}

		// 开启一个JettyServer,但是此时我们并不设置端口,在Connectors里设置端口
		Server server = new Server();

		// 一个通用的HTTP配置. 使用HttpConfiguration进行对HTTP和HTTPS进行设置. HTTP默认的scheme是http,HTTPS默认的scheme是https
		HttpConfiguration httpConfig = new HttpConfiguration();
		httpConfig.setSecureScheme("https");
		httpConfig.setSecurePort(8443);
		httpConfig.setOutputBufferSize(32768);

		// 构建一个HTTP Connectors
		ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(httpConfig));
		http.setPort(8080);
		http.setIdleTimeout(30000);

		// 根据SSL证书生成ssl信息
		SslContextFactory sslContextFactory = new SslContextFactory();
		sslContextFactory.setKeyStorePath(keystoreFile.getAbsolutePath());
		sslContextFactory.setKeyStorePassword("123456");
		sslContextFactory.setKeyManagerPassword("123456");

		// 现在为HTTP Connectors创建一个http配置对象, 这个对象是必须的, 我们不能在http Connectors里复用httpConfig对象,
		// 即便是将httpConfig对象作为参数进行构建，它的内部也是对它的一个克隆而已
		HttpConfiguration httpsConfig = new HttpConfiguration(httpConfig);
		// SecureRequestCustomizer用于处理从Jetty接手的https连接
		SecureRequestCustomizer src = new SecureRequestCustomizer();
		src.setStsMaxAge(2000);
		src.setStsIncludeSubDomains(true);
		httpsConfig.addCustomizer(src);

		ServerConnector https = new ServerConnector(server,
				new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()),
				new HttpConnectionFactory(httpsConfig));
		https.setPort(8443);
		https.setIdleTimeout(500000);

		server.setConnectors(new Connector[] { http, https });

		ServletHandler handler = new ServletHandler();
		handler.addServletWithMapping(HelloServlet.class, "/*");
		server.setHandler(handler);

		server.start();
		server.join();
	}

	public static class HelloServlet extends HttpServlet {
		@Override
		protected void doGet(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
			response.setContentType("text/html");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().println("<h1>Hello from HelloServlet</h1>");
		}
	}
}
```
我们使用JMeter模拟客户端进行访问

## ContextHandler
`ContextHandler`实现自`ScopedHandler`

```java
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ManyContexts {
	public static void main(String[] args) throws Exception {
		Server server = new Server(8080);

		ContextHandler context = new ContextHandler("/");
		context.setContextPath("/");
		context.setHandler(new HelloHandler("Root Hello"));

		ContextHandler contextFR = new ContextHandler("/fr");
		contextFR.setHandler(new HelloHandler("Bonjoir"));

		ContextHandler contextIT = new ContextHandler("/it");
		contextIT.setHandler(new HelloHandler("Bongiorno"));

		ContextHandler contextV = new ContextHandler("/");
		contextV.setVirtualHosts(new String[] { "127.0.0.2" });
		contextV.setHandler(new HelloHandler("Virtual Hello"));

		ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		servletContextHandler.setContextPath("/servlet");
		servletContextHandler.setResourceBase("d:\\ssl");
		servletContextHandler.addServlet(HelloServlet.class, "/abc");

		ContextHandlerCollection contexts = new ContextHandlerCollection();
		contexts.setHandlers(new Handler[] { context, contextFR, contextIT, contextV, servletContextHandler});

		server.setHandler(contexts);

		server.start();
		server.join();
	}

	public static class HelloServlet extends HttpServlet {
		@Override
		protected void doGet(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
			response.setContentType("text/html");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().println("<h1>Hello from HelloServlet</h1>");
		}
	}

	public static class HelloHandler extends AbstractHandler {

		private String path;
		public HelloHandler(String path) {
			this.path = path;
		}

		@Override
		public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
			ServletOutputStream out = response.getOutputStream();
			out.print(path);
			out.flush();
			response.setStatus(200);
		}
	}
}
```
我们看一下测试结果
```bash
ζ curl http://192.168.10.220:8080/
Root Hello%                                                                                         
# wangming@OA1503P0256: ~                                                               (16:55:48)
ζ curl http://192.168.10.220:8080/fr
# wangming@OA1503P0256: ~                                                               (16:55:55)
ζ curl http://192.168.10.220:8080/it
# wangming@OA1503P0256: ~                                                               (16:56:07)
ζ curl 127.0.0.2:8080/
Virtual HelloRoot Hello%                                                                            
# wangming@OA1503P0256: ~                                                               (16:56:34)
# wangming@OA1503P0256: ~                                                               (16:56:48)
ζ curl http://192.168.10.220:8080/servlet/abc
<h1>Hello from HelloServlet</h1>
```

## JMX
服务器开启JMX
```java
import java.lang.management.ManagementFactory;

import javax.management.remote.JMXServiceURL;

import org.eclipse.jetty.jmx.ConnectorServer;
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.Server;

public class ServerWithJMX {
	public static void main(String[] args) throws Exception {
		// === jetty-jmx.xml ===
		MBeanContainer mbContainer = new MBeanContainer(ManagementFactory.getPlatformMBeanServer());

		Server server = new Server(8080);
		server.addBean(mbContainer);

		ConnectorServer jmx = new ConnectorServer(
				new JMXServiceURL("rmi", null, 1999, "/jndi/rmi://localhost:1999/jmxrmi"),
				"org.eclipse.jetty.jmx:name=rmiconnectorserver");
		server.addBean(jmx);

		server.start();
		server.dumpStdErr();
		server.join();
	}
}
```