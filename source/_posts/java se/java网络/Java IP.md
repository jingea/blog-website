category: JAVA SE
tag: JAVA NET
date: 2014-09-23
title: Java IP
---
InetAddress是对IP的高级表示

InetAddress将equals方法重写,如果俩个InetAddress对象的ip地址相同则判断这俩个对象相等.但是并不判断主机名是否相等
```java
InetAddress i1 = InetAddress.getByName("www.ibiblio.org");
InetAddress i2 = InetAddress.getByName("helios.metalab.unc.edu");
```

InetAddress将hashCode方法重写,只对ip地址进行hashCode计算.如果俩个InetAddress对象的ip地址相同则判断这俩个对象的hashCode相等
```java
InetAddress i1 = InetAddress.getByName("www.ibiblio.org");
InetAddress i2 = InetAddress.getByName("helios.metalab.unc.edu");
Assert.assertEquals(true, i1.hashCode() == i2.hashCode());
```
下例中toString将主机名一起打印了出来. 但不是所有的InetAddress都含有主机名.java 1.4之后,如果没有主机名就会将其打印成空字符串,而不是像1.3之前的打印点分四段式ip地址
```java
InetAddress i1 = InetAddress.getByName("www.ibiblio.org");
Assert.assertEquals("www.ibiblio.org/152.19.134.40", i1.toString());
```

使用DNS查找主机IP地址,该方法会试图连接本地DNS服务器. 如果没有找到主机会抛出UnknownHostException异常
```java 
InetAddress address = InetAddress.getByName("localhost");
Assert.assertEquals("localhost/127.0.0.1", address.toString());
```

直接为IP地址创建一个InetAddress对象,但是它不会检查DNS服务器(不会主动查找主机名). 如果没有找到主机也不会抛出UnknownHostException异常. 只有当使用getHostName() 或者使用toString()时才会通过DNS查找主机名.如果没有找到主机名,那它会使用默认值(即点分四段或者16进制式地址)
```java
InetAddress address = InetAddress.getByName("180.149.131.98");
Assert.assertEquals("/180.149.131.98", address.toString());
Assert.assertEquals("180.149.131.98", address.getHostName());
```

返回对应该主机名的所有地址,该方法会试图连接本地DNS服务器
```java
InetAddress[] address = InetAddress.getAllByName("www.baidu.com");
for (InetAddress inetAddress : address) {
System.out.println("testGetAllByName_ok : " + inetAddress);
}
```

显示当前机器的IP地址.该方法会试图连接本地DNS服务器.just-PC 为本地DSN服务器为本地域中主机返回的主机名.该地址是路由分配地址(即内网使用的路由地址)
```java
InetAddress address = InetAddress.getLocalHost();
Assert.assertEquals("just-PC/192.168.1.101", address.toString()); 
```

该方法会试图连接本地DNS服务器
```
InetAddress address = InetAddress.getByName("");
Assert.assertEquals("localhost/127.0.0.1", address.toString());
```

该方法会试图连接本地DNS服务器, 无法找到抛出UnknownHostException异常
```java
InetAddress.getByName("asd");
```

获取一个主机的字符串形式的主机名. 如果该主机没有主机名(没有在DNS注册)或者安全管理器(SecurityManager)确定阻止该主机名,就会返回点分四段式ip地址
```
InetAddress address = InetAddress.getLocalHost();
System.out.println("Host Name : " + address.getHostName()); // 本地主机名取决于本地NDS在解析本地主机名时的行为
InetAddress address1 = InetAddress.getByName("180.149.131.98");
Assert.assertEquals("180.149.131.98", address1.getHostName());  // 为什么没有返回主机名？？
```

返回点分四段式ip地址
```
InetAddress address = InetAddress.getLocalHost();
Assert.assertEquals("192.168.1.101", address.getHostAddress());
```

主要是用来测试地址类型是ipv4还是ipv6
```
InetAddress address = InetAddress.getLocalHost();
// 返回网络字节顺序(最高位是数组的第一个字节)的字节数组形式的ip地址
byte[] arr = address.getAddress();  
if(arr.length == 4)
System.out.print("Address Type : IPv4---");
else if(arr.length == 16)
System.out.print("Address Type : IPv6---");
for (byte b : arr) {
System.out.print(b);
}
```

测试地址可达性,尝试连接远程主机的echo接口,查看是否可达. 该方法在全球Internet上并不可靠,防火墙会拦截java用于查看主机是否可大的网络协议
```
InetAddress address = InetAddress.getByName("180.149.131.98");
System.out.println("is Rechable : " + address.isReachable(5)); 
System.out.println(address.isReachable(InetAddress., ttl, timeout)); 
```

测试是否是通配符地址. 通配符地址可匹配本地系统中所有地址. IPv4中通配符地址是0.0.0.0 IPv6是::
```
InetAddress address = InetAddress.getLocalHost();
address.isAnyLocalAddress();
```
测试是否是回路地址. 回路地址在IP层连接同一台电脑,不使用任何物理硬件. 这就绕过了可能有问题的硬件设备进行测试  地址是127.0.0.1 
```
InetAddress address = InetAddress.getLocalHost();
address.isLoopbackAddress();
```

测试是否是IPv6本地连接地址(以FE80开头地址,后8个字节用以太网mac地址(本地地址)填充). 这个地址有助于实现IPv6网络自动配置 ,并且不会将包转发出本地子网
```
InetAddress address = InetAddress.getLocalHost();
address.isLinkLocalAddress();
```

测试是否是IPv6本地网站地址(以FEC0开头地址,后8个字节用以太网mac地址(本地地址)填充). 这个地址只会被路由器在网站内转发
```
InetAddress address = InetAddress.getLocalHost();
address.isSiteLocalAddress();
```
是否是组广播地址(IPV4:224.0.0.0-239.255.255.255) 向预定计算机进行广播
```
InetAddress address = InetAddress.getLocalHost();
address.isMulticastAddress();
```

测试是否是全球广播地
```
InetAddress address = InetAddress.getLocalHost();
address.isMCGlobal();
```
组织范围内广播地址
```
InetAddress address = InetAddress.getLocalHost();
address.isMCOrgLocal();
```

是否是网站内组播地址
```
InetAddress address = InetAddress.getLocalHost();
address.isMCSiteLocal();
```
子网范围内组播地址
```
InetAddress address = InetAddress.getLocalHost();
address.isMCLinkLocal();
```
本地接口组播地址
```
InetAddress address = InetAddress.getLocalHost();
address.isMCNodeLocal();
```