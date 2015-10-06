category: 
- java
tag:
- java加密解密
title: javax_net_ssl
---
## HttpsURLConnection
HttpsURLConnection 拓展了URLConnection, 支持各种特定于Https的功能
```java
URL url = new URL("www.baidu.com");
conn = (HttpsURLConnection) url.openConnection();
// 打开输入模式
conn.setDoInput(true);
// 打开输出模式
// 设置当此实例为安全Https URL连接创建套接字时使用的SSLSocketFactory
conn.setDoOutput(true);			
// 获得握手期间的相关的证书链
conn.setSSLSocketFactory(get());			
// 返回握手期间发送给服务器的证书
conn.getLocalCertificates();
// 返回服务器的证书链，它是作为定义会话的一部分而建立的
// 获取握手期间发送到服务器的主体
conn.getServerCertificates();
conn.getLocalPrincipal();
// 获取服务器的主体，它是作为定义会话一部分而建立的
conn.getPeerPrincipal();// 获取在此链接之上的密码套件
conn.getCipherSuite();	

// 构建SSLSocketFactory
KeyStore keyStore = null;
keyStore = KeyStore.getInstance("JKS");
// 加载秘钥库文件
FileInputStream in = new FileInputStream(""));
keyStore.load(in, "password".toCharArray());
KeyManagerFactory keyManagerFactory = null;
TrustManagerFactory trustManagerFactory = null;
// 指定算法名获得实例化对象
keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
keyManagerFactory.init(keyStore, "password".toCharArray());
trustManagerFactory.init(keyStore);
SSLContext ctx = null;
ctx = SSLContext.getInstance("SSL");
// 初始化上下文
ctx.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
```

## KeyManagerFactory
用来管理秘钥,设定秘钥库. 此类充当基于秘钥内容源的秘钥管理器的工厂.每个秘钥管理器管理特定类型的,由套接字所使用的秘钥内容
```java
System.out.println("KeyManagerFactory 默认算法：" + KeyManagerFactory.getDefaultAlgorithm());		
KeyManagerFactory fatory = null;
// 指定算法名获得实例化对象
fatory = KeyManagerFactory.getInstance("SunX509");			
for (KeyManager keyManager : fatory.getKeyManagers()) {
		System.out.println(keyManager.toString());
}	
KeyStore keyStore = null;
keyStore = KeyStore.getInstance("JKS");
// 加载秘钥库文件
FileInputStream in = new FileInputStream(""));
// 使用秘钥内容源初始化此KeyManagerFactory 对象, 另外还可使用特定于提供者的秘钥内容源初始化此对象
keyStore.load(in, "password".toCharArray());	
fatory.init(keyStore, "password".toCharArray());// 另外一种设置秘钥库的方式
System.setProperty("javax.net.ssl.keyStore", "D:\\server.keystore");
System.setProperty("javax.net.ssl.keyStorePassword", "123456");
```

## SSLContext
表示安全套接字上下文,安全套接字协议的实现,它充当于安全套接字工厂或者SSLEngine的工厂用可选的一组秘钥和信任管理器及安全随机字节初始化此类
```java
// 构建SSLSocketFactory
KeyStore keyStore = null;
keyStore = KeyStore.getInstance("JKS");
// 加载秘钥库文件
FileInputStream in = new FileInputStream(""));
keyStore.load(in, "password".toCharArray());		
KeyManagerFactory keyManagerFactory = null;
TrustManagerFactory trustManagerFactory = null;
// 指定算法名获得实例化对象
keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
keyManagerFactory.init(keyStore, "password".toCharArray());
trustManagerFactory.init(keyStore);
SSLContext ctx = SSLContext.getInstance("SSL");
// 初始化上下文
ctx.init(keyManagerFactory.getKeyManagers(),
		trustManagerFactory.getTrustManagers(), null);
// 返回此上下文的SSLSocketFactory 对象
SSLSocketFactory socketFactory = ctx.getSocketFactory();
// 返回此上下文的 ServerSocketFactory 对象
SSLServerSocketFactory serverSocketFactory = ctx
		.getServerSocketFactory();
// 返回服务器会话上下文，它表示可提供给服务器端SSL套接字握手阶段使用的SSL会话集
ctx.getServerSessionContext();
// 返回客户端会话上下文，它表示可提供给客户端SSL套接字握手阶段使用的SSL会话集
ctx.getClientSessionContext();
// 使用上下文创建新的SSLEngine(另一个create方法还可指定主机和端口)
ctx.createSSLEngine();
```

## SSLServerSocket
SSLServerSocket 是专用于服务器端的SSLSocket, 是ServerSocket的子类
```java
// 构建SSLSocketFactory
KeyStore keyStore = null;
keyStore = KeyStore.getInstance("JKS");
// 加载秘钥库文件
FileInputStream in = new FileInputStream(""));
keyStore.load(in, "password".toCharArray());
KeyManagerFactory keyManagerFactory = null;
TrustManagerFactory trustManagerFactory = null;
// 指定算法名获得实例化对象
keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
keyManagerFactory.init(keyStore, "password".toCharArray());
trustManagerFactory.init(keyStore);
SSLContext ctx = null;
ctx = SSLContext.getInstance("SSL");
// 初始化上下文
ctx.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
ServerSocketFactory factory = ctx.getServerSocketFactory();
SSLServerSocket serverSocket = (SSLServerSocket) factory.createServerSocket(8080);
serverSocket.accept();
```
   
## SSLServerSocketFactory
SSLSocketFactory 操作几乎一致
```java
SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
factory.createServerSocket(8080);
```
   
## SSLSession
SSLSession 接口用于保持SSL协议网络交互会话状态. 用来描述俩个实体之间的会话关系.

在SSL的会话中,可以获得加密套件(CipherSuite),数字证书等CipherSuite 明确给出了加密参数, 具体包括：协议,秘钥交换算法,加密算法,工作模式和消息摘要算法
如 TLS_RSA_TITH_AES_256_CBC_SHA 就是一个完成加密套件信息, 它表示：使用TLS协议,迷药交换算法为RSA,对称加密算法为AES(长度256),使用CBC模式,并使用SHA消息摘要算法

## SSLSocket
SSLSocket是基于SSL协议的Socket,用于设置加密套件,处理握手结束事件,并管理SSLSession目前Java环境中支持的协议有：SSLLv2Hello,SSLv3,TLSv1,TlSv1.1, TLSv1.2

通常默认的是SSLv3和TLSv1.1

* setEnabledProtocols(protocols);  通过该方法设置SSL链接可用协议
* getSupportedCipherSuites()		获得可支持的加密套件
* setEnabledCipherSuites(suites)	为当前SSL链接设置可用的加密套件
* getEnabledCipherSuites()			获得当前SSL链接可用的加密套件

有时候需要与远程服务器建立基于SSLSocket的链接. 远程服务仅通过SSLSocket传递数字证书.这时候,就不能通过HttpsURLConnection来获得数字证书了,本方法就是通过SSLSocket来获得SSLSession
并最终获得数字证书
```java
// 输出当前网络的debug日志
// 将在控制台获得当前网络链接操作过程中使用到的数字证书信息和经过经过加密后的网络传输数据
System.setProperty("javax.net.debug", "all");		
SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();		
SSLSession session = null;
SSLSocket sslSocket = (SSLSocket) factory.createSocket("localhost", 8080);
// 完成了加密套件和协议配置后，就可以开始握手协议，建立加密套接字进行加密通信了。
// 在当前链接上建立SSL握手
sslSocket.startHandshake();			// 获得当前会话的SSLSession实例
session = sslSocket.getSession();			
sslSocket.close();			
return session.getPeerCertificates();
```

## SSLSocketFactory
通过SSLSocketFactory 可创建SSLSocket, 并获得相应的加密套件
```java
SSLSocketFactory sslFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();

// 创建SSLSocket实例
Socket socket = sslFactory.createSocket("localhost", 8080);

SSLSocket sslSocket = (SSLSocket)socket;

//获得默认加密套件
sslFactory.getDefaultCipherSuites();

// 获得当前SSL链接可支持的加密套件
sslFactory.getSupportedCipherSuites();
```
   
## TrustManagerFactory
管理信任材料的管理工厂,设定信任库,每个信任管理器管理特定类型的由安全套接字使用的信任材料

```java
TrustManagerFactory factory = TrustManagerFactory.getInstance("SunX509");

//  加载秘钥库文件
try(FileInputStream in = new FileInputStream("")) {
	KeyStore ks = KeyStore.getInstance("JKS");
	ks.load(in, "password".toCharArray());
	// 用证书授权源和相关的信任材料初始化此工厂
	factory.init(ks);
	// 另外一种设置信任库的方式
	System.setProperty("javax.net.ssl.trustStore", "D:\\server.keystore");
	System.setProperty("javax.net.ssl.trustStorePassword", "123456");
	
}
```
