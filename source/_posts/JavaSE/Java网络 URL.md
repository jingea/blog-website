category: Java
date: 2014-09-21
title: Java URL
---
## URI
```java
scheme:scheme-specific-part (模式:模式特有部分)
```
URI模式包含 data, file, ftp, http, news, telnet, urn (还有基于JAVA的rmi, jndi 等非标准模式,也称为protocol)
例如：`http://www.ming15.wang/2015/10/13/%E5%B7%A5%E5%85%B7/2015-10-12-AWK/`这个例子中模式为`http`, 负责解析该URI的机构`ming15.wang` 负责将`/2015/10/13/%E5%B7%A5%E5%85%B7/2015-10-12-AWK/`地址映射到主机资源

还有的URI路径中含有? 这是URI的查询部分.后面紧跟查询参数,多个参数用&分割. 例如：`git@github.com:ming15/VertxServer.git`该URI中模式为`git` 解析结构为`github.com` 还可以在git和@之间加上用户名和密码`git://username:password@github.com:ming15/VertxServer.git`

URI一般由以下组成
* 模式
* URI解析结构
* 资源路径
* 查询参数构成

1. URL ： 指向Internet上某个位置的某个文件.用于标识Internet上的资源位置. 指定访问服务器的协议, 服务器名, 文件在次服务器上的位置`protocol://username@hostname:port/path/filename?query##fragment`协议可以看成是模式但是它不包含URN.
2. URN ：不指向位置的资源名.  (具体的内容参考例子磁力链接)`urn:namespace:resource_name`. `namespace`:某个授权机构维护的某类资源的集合名.  `resource_name` 集合中的资源名

这里简述一下相对URL. 举例来说`<a href="java.html">` 这个超链接会继承父文档(当前文档)的协议, 主机名, 资源路径`.java.html`会替换掉,父文档里最后的文件名,还有例如`<a href="/demo/java.html"> `那这个超链接会将主机名后的资源路径一起换掉 ，用该路径替换

## URL
Java支持的传输协议：
* 超文本传输协议: `http://www.baidu.com`		
* 安全http协议: `https://www.amazon.com/exec/obidos/order2`
* 文件传输协议: `ftp://metalab.unc.edu/pub/languages/java/javafaq`
* 简单邮件传输协议: `mailto:elharo@metalab.unc.edu`
* telnet协议: `telnet://dibner.poly.edu`
* 本地文件访问协议: `file:`
* gopher: `gopher://gopher.anc.org.za`
* 轻量级目录访问协议: `ldap://ldap.itd.umich.edu`
* jar: `jar://`
* NFS,网络文件协议: `nfs://utopia.poly.edu/usr/tmp`
* JDBC 定制协议   通过java.sql包支持: `jdbc:mysql://luna.matalab.unc.edu:3306/NEWS`
* rmi  远程方法的调用协议   通过java.rmi包支持: `rmi://metalab.unc.edu/RenderEngine`
* HotJava的定制协议: `doc:/UserGuide/release.html`,`netdoc:/UserGuide/release.html`, `systemresource://www.adc.org/+/index.html`, `verbatim:http://www.adc.org`


不带端口构造`URL`(需要注意的是：该构造器生成的URL端口为-1,所以回使用该协议的默认端口   第三个参数加反斜线也是需要注意的)
```java
URL url = new URL("http" , "www.eff.org", "/blueribbon.html#intro");
```

带端口构造`URL`
```java
URL url = new URL("http" , "www.eff.org", 8080, "/blueribbon.html#intro");
```

根据相对URL和基础URL构建一个绝对URL,当希望迭代处理位于相同目录下的一组文件时, 可以考虑使用该构造器
```java
URL url = new URL("http://ibiblio.org/javafaq/index.html");
URL newURL = new URL(url, "mailinglists.html");
Assert.assertEquals("http://ibiblio.org/javafaq/mailinglists.html", newURL.toString());
```

利用ClassLoader可以加载资源,例如图片 音频等
```java
URL url = ClassLoader.getSystemResource("resource/simple.txt");
Assert.assertEquals(null, url);
```

利用ClassLoader可以加载资源,例如图片 音频等
```java
URL url = getClass().getResource("resource/simple.txt");
Assert.assertEquals(null, url);
```

查看URL中的模式
```java
URL url = new URL("http://ibiblio.org/javafaq/index.html");
Assert.assertEquals("http", url.getProtocol());
URL url = new URL("http://www.ibiblio.org/javafaq/index.html");
Assert.assertEquals("www.ibiblio.org", url.getHost());
```

查看URL中的路径 (范围：主机名后面的第一个/ 到片段标示符# 之前) 包含查询字符串
```java
URL url = new URL("http://ibiblio.org/nywc/compositions.pthml?category=Piano");
Assert.assertEquals("/nywc/compositions.pthml?category=Piano", url.getFile());
```

查看URL中的路径 (范围：主机名后面的第一个/ 到片段标示符# 之前)  不包含查询字符串
```java
URL url = new URL("http://ibiblio.org/nywc/compositions.pthml?category=Piano");
Assert.assertEquals("/nywc/compositions.pthml", url.getPath());
```

查看URL中的查询字符串
```java
URL url = new URL("http://ibiblio.org/nywc/compositions.pthml?category=Piano");
Assert.assertEquals("category=Piano", url.getQuery());
```

查看URL中的查询字符串
```java
URL url = new URL("ftp://mp3:secret@ftp.example.com/c%3a/stuff/mp3");
Assert.assertEquals("mp3:secret", url.getUserInfo());
```

查看URL中的Authority(授权机构,包含用户信息,主机和端口.一般都回返回主机信息,但是不一定包含用户信息和端口)
```java
URL url = new URL("ftp://mp3:secret@ftp.example.com/c%3a/stuff/mp3");
Assert.assertEquals("mp3:secret@ftp.example.com", url.getAuthority());
```

sameFile 只是简单的测试url中的主机名是否是别名, 需要更细致的测试, sameFile 与 equals的区别是sameFile不考虑标示符儿equals需要考虑标示符
```java
URL u1 = new URL("http://www.ncsa.uiuc.edu/HTMLPrimer.html#GS");
URL u2 = new URL("http://www.ncsa.uiuc.edu/HTMLPrimer.html#HD");
if(u1.sameFile(u2))
	System.out.println(u1 + " is same file with " + u2);
else
	System.out.println(u1 + " is not same file with " + u2);
```

连接URl所指向的资源.执行客户端和服务器之间任何必要的握手.返回一个可以读取数据的`InputStream`,该流读取文件里的原始内容,不包括任何HTTP首部或者任何与协议有关的信息
```java
try(InputStream in = new URL("http://www.baidu.com").openStream()) {
	int c = 0;
	while((c = in.read()) != -1) {
	if(c == '<') System.out.println();
		System.out.write(c);
	}
}
```

`openConnection`打开指定URL的socket,返回URLConnection对象(一个打开网络资源的连接)
```java
URLConnection conn = new URL("http://www.baidu.com").openConnection();
try(InputStream in = conn.getInputStream()) {
	int c = 0;
	while((c = in.read()) != -1) {
		System.out.write(c);
	}
}
```
