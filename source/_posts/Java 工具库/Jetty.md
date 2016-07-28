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
* `ContextHandlerCollection`：

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
In the previous examples, the Server instance is passed a port number and it internally creates a default instance of a Connector that listens for requests on that port. However, often when embedding Jetty it is desirable to explicitly instantiate and configure one or more Connectors for a Server instance.

A Jetty server with multiple connectors.

When configuring multiple connectors (for example, HTTP and HTTPS), it may be
desirable to share configuration of common parameters for HTTP. To achieve
this you need to explicitly configure the ServerConnector class with
ConnectionFactory instances, and provide them with common HTTP configuration.

The ManyConnectors example, configures a server with two ServerConnector
instances: the http connector has a HTTPConnectionFactory instance; the https
connector has a SslConnectionFactory chained to a HttpConnectionFactory. Both
HttpConnectionFactory are configured based on the same HttpConfiguration
instance, however the HTTPS factory uses a wrapped configuration so that a
SecureRequestCustomizer can be added.

```java
public class ManyConnectors {
	public static void main(String[] args) throws Exception {
		// 加载ssl需要的keystore
		File keystoreFile = new File("./keystore");
		if (!keystoreFile.exists()) {
			throw new FileNotFoundException(keystoreFile.getAbsolutePath());
		}

		// 开启一个JettyServer,但是此时我们并不设置端口,在Connectors里设置端口
		Server server = new Server();

		// HTTP配置. 使用HttpConfiguration进行对HTTP和HTTPS进行设置. HTTP默认的scheme是http,HTTPS默认的scheme是https
		// 这里是一个通用的HTTP配置, 我们在这里设置
		HttpConfiguration httpConfig = new HttpConfiguration();
		httpConfig.setSecureScheme("https");
		httpConfig.setSecurePort(8443);
		httpConfig.setOutputBufferSize(32768);

		ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(httpConfig));
		http.setPort(8080);
		http.setIdleTimeout(30000);

		SslContextFactory sslContextFactory = new SslContextFactory();
		sslContextFactory.setKeyStorePath(keystoreFile.getAbsolutePath());
		sslContextFactory.setKeyStorePassword("OBF:1vny1zlo1x8e1vnw1vn61x8g1zlu1vn4");
		sslContextFactory.setKeyManagerPassword("OBF:1u2u1wml1z7s1z7a1wnl1u2g");

		// HTTPS Configuration
		// A new HttpConfiguration object is needed for the next connector and
		// you can pass the old one as an argument to effectively clone the
		// contents. On this HttpConfiguration object we add a
		// SecureRequestCustomizer which is how a new connector is able to
		// resolve the https connection before handing control over to the Jetty
		// Server.
		HttpConfiguration httpsConfig = new HttpConfiguration(httpConfig);
		SecureRequestCustomizer src = new SecureRequestCustomizer();
		src.setStsMaxAge(2000);
		src.setStsIncludeSubDomains(true);
		httpsConfig.addCustomizer(src);

		// HTTPS connector
		// We create a second ServerConnector, passing in the http configuration
		// we just made along with the previously created ssl context factory.
		// Next we set the port and a longer idle timeout.
		ServerConnector https = new ServerConnector(server,
				new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()),
				new HttpConnectionFactory(httpsConfig));
		https.setPort(8443);
		https.setIdleTimeout(500000);

		// Here you see the server having multiple connectors registered with
		// it, now requests can flow into the server from both http and https
		// urls to their respective ports and be processed accordingly by jetty.
		// A simple handler is also registered with the server so the example
		// has something to pass requests off to.

		// Set the connectors
		server.setConnectors(new Connector[] { http, https });

		server.setHandler(new HelloHandler("Many Connectors"));

		server.start();
		server.join();
	}
}
```

## ContextHandler
`ContextHandler`实现自`ScopedHandler`, 

A ContextHandler is a ScopedHandler that responds only to requests that have a URI prefix that matches the configured context path. Requests that match
the context path have their path methods updated accordingly and the contexts scope is available, which optionally may include:

A Classloader that is set as the Thread context classloader while request handling is in scope. A set of attributes that is available via the
ServletContext API. A set of init parameters that is available via the ServletContext API. A base Resource which is used as the document root for
static resource requests via the ServletContext API. A set of virtual host names. The following OneContext example shows a context being established
that wraps the HelloHandler:

When many contexts are present, you can embed a ContextHandlerCollection to efficiently examine a request URI to then select the matching
ContextHandler(s) for the request. The ManyContexts example shows how many such contexts you can configure:

```java
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

import javax.servlet.http.HttpServlet;

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
		servletContextHandler.setContextPath("/");
		servletContextHandler.setResourceBase(System.getProperty("java.io.tmpdir"));

		servletContextHandler.addServlet(HttpServlet.class, "/dump/*");
		servletContextHandler.addServlet(DefaultServlet.class, "/");

		ContextHandlerCollection contexts = new ContextHandlerCollection();
		contexts.setHandlers(new Handler[] { context, contextFR, contextIT, contextV, servletContextHandler});

		server.setHandler(contexts);

		server.start();
		server.join();
	}
}
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