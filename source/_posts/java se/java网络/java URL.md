category: JAVA SE
tag: JAVA NET
date: 2014-09-21
title: Java URL
---

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
```
URL url = new URL("http" , "www.eff.org", "/blueribbon.html#intro");
```

带端口构造`URL`
```
URL url = new URL("http" , "www.eff.org", 8080, "/blueribbon.html#intro");
```

根据相对URL和基础URL构建一个绝对URL,当希望迭代处理位于相同目录下的一组文件时, 可以考虑使用该构造器
```
URL url = new URL("http://ibiblio.org/javafaq/index.html");
URL newURL = new URL(url, "mailinglists.html");
Assert.assertEquals("http://ibiblio.org/javafaq/mailinglists.html", newURL.toString());
```

利用ClassLoader可以加载资源,例如图片 音频等
```
URL url = ClassLoader.getSystemResource("resource/simple.txt");
Assert.assertEquals(null, url);
```

利用ClassLoader可以加载资源,例如图片 音频等
```
URL url = getClass().getResource("resource/simple.txt");
Assert.assertEquals(null, url);
```

查看URL中的模式
```
URL url = new URL("http://ibiblio.org/javafaq/index.html");
Assert.assertEquals("http", url.getProtocol());
URL url = new URL("http://www.ibiblio.org/javafaq/index.html");
Assert.assertEquals("www.ibiblio.org", url.getHost());
```

查看URL中的路径 (范围：主机名后面的第一个/ 到片段标示符# 之前) 包含查询字符串
```	
URL url = new URL("http://ibiblio.org/nywc/compositions.pthml?category=Piano");
Assert.assertEquals("/nywc/compositions.pthml?category=Piano", url.getFile());
```

查看URL中的路径 (范围：主机名后面的第一个/ 到片段标示符# 之前)  不包含查询字符串
```
URL url = new URL("http://ibiblio.org/nywc/compositions.pthml?category=Piano");
Assert.assertEquals("/nywc/compositions.pthml", url.getPath());
```

查看URL中的查询字符串
```
URL url = new URL("http://ibiblio.org/nywc/compositions.pthml?category=Piano");
Assert.assertEquals("category=Piano", url.getQuery());
```

查看URL中的查询字符串
```
URL url = new URL("ftp://mp3:secret@ftp.example.com/c%3a/stuff/mp3");
Assert.assertEquals("mp3:secret", url.getUserInfo());
```

查看URL中的Authority(授权机构,包含用户信息,主机和端口.一般都回返回主机信息,但是不一定包含用户信息和端口) 
```
URL url = new URL("ftp://mp3:secret@ftp.example.com/c%3a/stuff/mp3");
Assert.assertEquals("mp3:secret@ftp.example.com", url.getAuthority());
```

sameFile 只是简单的测试url中的主机名是否是别名, 需要更细致的测试, sameFile 与 equals的区别是sameFile不考虑标示符儿equals需要考虑标示符
```
URL u1 = new URL("http://www.ncsa.uiuc.edu/HTMLPrimer.html#GS");
URL u2 = new URL("http://www.ncsa.uiuc.edu/HTMLPrimer.html#HD");
if(u1.sameFile(u2))
	System.out.println(u1 + " is same file with " + u2);
else
	System.out.println(u1 + " is not same file with " + u2);
```

连接URl所指向的资源.执行客户端和服务器之间任何必要的握手.返回一个可以读取数据的`InputStream`,该流读取文件里的原始内容,不包括任何HTTP首部或者任何与协议有关的信息
```
try(InputStream in = new URL("http://www.baidu.com").openStream()) {
	int c = 0;
	while((c = in.read()) != -1) {
	if(c == '<') System.out.println();
		System.out.write(c);
	}
}
```

`openConnection`打开指定URL的socket,返回URLConnection对象(一个打开网络资源的连接)
```
URLConnection conn = new URL("http://www.baidu.com").openConnection();
try(InputStream in = conn.getInputStream()) {
	int c = 0;
	while((c = in.read()) != -1) {
		System.out.write(c);
	}
}
```