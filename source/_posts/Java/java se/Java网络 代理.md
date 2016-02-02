category: Java
tag: JavaSE
date: 2014-09-25
title: java代理
---
## java 代理
通过系统属性来指出本地代理服务器地址。 采用的参数有`http.proxyHost,http.proxyPort,http.nonProxyHost`以及三个相同的ftp协议开头的代理参数。 java不支持其他任何应用层协议,但如果对所有TCP连接都使用socks代理则可以使用`socksProxyHost`和`socksProxyPort`系统属性来确定
```java
// 设置代理服务器的ip地址
System.setProperty("http.proxyHost", "192.168.254.254");
// 设置代理服务器的端口
System.setProperty("http.proxyPort", "9000");
// 设置java.oreilly.com和xml.oreilly.com主机不被代理而是直接连接
System.setProperty("http.nonProxyHost", "java.oreilly.com|xml.oreilly.com");

SocketAddress add = new InetSocketAddress("proxy.example.com", 80);
Proxy proxy = new Proxy(Proxy.Type.HTTP, add);
```

虚拟机都有一个为不同连接定位代理服务器的`ProxySelector` 对象. 默认的`ProxySelector`只检查各种系统属性和URL协议,决定如何连接到不同的主机. 

下面`LocalProxySelect`是一个自己实现的选择器
```java
public void testLocalProxySelect() {
	ProxySelector select = new LocalProxySelect();
	// 每个虚拟机只运行着一个ProxySelector对象.setDefault之后所以的连接都会询问这个代理
	// 因此不能在公共的环境下改变代理连接.  那ProxySelector要如何使用
	ProxySelector.setDefault(select);
}

public static class LocalProxySelect extends ProxySelector {

	private List<Object> failed = new ArrayList<>();
	
	@Override
	public List<Proxy> select(URI uri) {
		// uri 连接所需的主机
		
		List<Proxy> result = new ArrayList<>();
		if(failed.contains(uri) || "http".equalsIgnoreCase(uri.getScheme())) {
			result.add(Proxy.NO_PROXY);
		} else {
			// 所有的连接都会使用proxy.example.com 进行代理
			SocketAddress address = new InetSocketAddress("proxy.example.com", 8000);
			Proxy proxy = new Proxy(Proxy.Type.HTTP, address);
			result.add(proxy);
		}
		return result;
	}

	@Override
	public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
		failed.add(uri);
	}
	
}
```
下面实现一个socket代理
```
Socket socket = null;
try {
	SocketAddress proxyAddress = new InetSocketAddress("myproxy.example.com", 1080);
	Proxy  proxy = new Proxy(Proxy.Type.SOCKS, proxyAddress);
	socket = new Socket(proxy);
	SocketAddress remote = new InetSocketAddress("login.ibiblio.org", 25);
	socket.connect(remote);
} catch (IOException e) {
	e.printStackTrace();
} finally {
	if(socket != null)
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
}
```
