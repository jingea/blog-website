category: Java
date: 2014-09-20
title: Java 网络接口
---
## 基本概念
Java里使用的是TCP/IP
* 应用层协议：(例如Http协议) 该层数据由下三层协议共同制定
* 传输层协议：(常用TCP,UDP)(ICMP Ping命令基于该协议). 该层协议用于确保数据报以发送时的顺序接受,并且不会丢包. 如果发现顺序有误,或者数据丢失,则可要求对方重新发送数据(TCP会要求这一点, 但是UDP协议只是检查数据发送顺序,以及数据是否丢失并不要求对方重传数据)
* 网络层协议：(使用最广泛的是IP协议)
> 网络层第一任务是对数据位或者字节进行分组,打成包(包内数据称为数据报).网络层第二任务定义了主机彼此间的寻址方式(例如IPV4用四个字节来标识一个地址).在JAVA里,IP协议是它唯一理解的网络层协议.

IP数据报格式
链路层协议：定义了网络接口(以太网接口或者环牌接口)

谈一下Internet地址分类 (具体定义参考 WIKI IP地址), IP地址分为A,B,C,D,E,F类 (E,F分别作为广播地址这里不说了)
* A类地址 第一个字节固定
* B类地址 前俩个地址固定
* C类地址 前三个地址固定
这里所说的固定指的是ISP给你的时候就固定了,你只能使用固定之后几位的地址.例如给了你一个C类地址 那么你只有256个地址可以使用.

后来为了节约地址,出现了CIDR  用/nn 指定前几位为固定的.例如/24 为前24位即前三个字节是固定的也就是一个c类地址.这么着就拟补了有的组织使用的IP大于c类却远远小于B类而造成的地址浪费.

在这里需要特殊说明的是有一些非路由地址,例如10; 192.16或者172.16到172.31 开头的地址.这些地址用于构建组织内部网路(例如家里只有一个IP但是却有很多设备,这时就需要通过路由为这些设备分配IP地址了),或者一些大型组织使用C类地址时非常有用
路由器会将非路由地址转换为外部地址


## 网络接口
网络接口的命名
* eth0: ethernet的简写，一般用于以太网接口。
* wifi0:wifi是无线局域网，因此wifi0一般指无线网络接口。
* ath0: Atheros的简写，一般指Atheros芯片所包含的无线网络接口。
* lo: local的简写，一般指本地环回接口。

lo:虚拟网络接口,其并不真实地从外界接收和发送数据包，而是在系统内部接收和发送数据包，因此虚拟网络接口不需要驱动程序硬件网卡的网络接口由驱动程序创建。而虚拟的网络接口由系统创建或通过应用层程序创建。
假如包是由一个本地进程为另一个本地进程产生的, 它们将通过外出链的’lo’接口,然后返回进入链的’lo’接口

Java网络接口相关主要用到`java.net.NetworkInterface`这个类，其表示一个本地IP地址,该类可以是一个物理接口或者绑定于同一个物理接口的虚拟接口.

获取某个名的网络接口对象
```java
NetworkInterface net = NetworkInterface.getByName("eth0");
```

获取一个绑定于制定ip地址的网络接口对象
```java
InetAddress address = InetAddress.getLocalHost();
NetworkInterface net = NetworkInterface.getByInetAddress(address);
```

列出本机所有的网络接口  包括物理或者虚拟网络接口
```java
Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
while(nets.hasMoreElements()) {
 System.out.println("网络接口 ： " + nets.nextElement());
}
```

列出本机所有绑定到该网络接口上的ip地址
```java
NetworkInterface net = NetworkInterface.getByName("eth0");
 Enumeration<InetAddress> address = net.getInetAddresses();

 while(address.hasMoreElements()) {
	 System.out.println("IP 地址 ： " + address.nextElement());
 }
```


```java
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class PrintNet {


	public static void main() throws Exception {
		// 获取全部的网络接口(由操作系统设置,每个硬件网卡(一个MAC)对应一个网络接口)
		Enumeration<?> nets = NetworkInterface.getNetworkInterfaces();
		while (nets.hasMoreElements()) {
			NetworkInterface net = (NetworkInterface) nets.nextElement();
			printNetworkInterface(net);
			Enumeration<?> addresses = net.getInetAddresses();  // 返回该接口中所有绑定的ip
			System.out.println("该接口下所有的ip:");
			while (addresses.hasMoreElements()) {
				InetAddress ip = (InetAddress) addresses.nextElement();
                pickUpHosyAddress(ip);
				printInetAddress(ip);
			}
			System.out.println();
			System.out.println();
		}
	}

	private static void printNetworkInterface(NetworkInterface net) throws Exception{
		System.out.println("网络接口的显示名称   :" + net.getDisplayName());
		System.out.println("网络接口的名称       :" + net.getName());
		System.out.println("idx                	:" + net.getIndex());
		System.out.println("最大传输单元         :" + net.getMTU());
		System.out.println("mac地址              :" + displayMac(net.getHardwareAddress()));
		System.out.println("是否是回送接口       :" + net.isLoopback());
		System.out.println("是否是点对点接口     :" + net.isPointToPoint());
		System.out.println("是否已经开启并运行   :" + net.isUp());
	}

	/**
	 * 输出ip地址
	 * @param ip
	 */
	private static void pickUpHosyAddress(InetAddress ip) {
		if (!ip.isLoopbackAddress() && !ip.isSiteLocalAddress() && ip.getHostAddress().indexOf(":") == -1) {
			System.out.println("外网 HostAddress   :" + ip.getHostAddress());
		}
		if (ip.isLoopbackAddress() && !ip.isSiteLocalAddress() && ip.getHostAddress().indexOf(":") == -1) {
			System.out.println("内网 HostAddress   :" + ip.getHostAddress());
		}
		if (ip != null && !ip.isLoopbackAddress() && ip instanceof Inet4Address) {
			System.out.println("HostAddress        :" + ip.getHostAddress());
		}
	}

	/**
	 * 打印InetAddress 相关信息
	 * @param ip
	 * @throws Exception
	 */
	private static void printInetAddress(InetAddress ip) throws Exception{
		System.out.println("远程主机的主机名         :" + ip.getCanonicalHostName());
		System.out.println("主机地址                 :" + ip.getHostAddress());
		System.out.println("远程主机的别名           :" + ip.getHostName());
		System.out.println("mac Address             :" + displayMac(ip.getAddress()));
		System.out.println("本机主机名               :" + ip.getLocalHost().getHostName());
		System.out.println("回环地址 主机名          :" + ip.getLoopbackAddress().getHostName());
		// (127.0.0.0 ~ 127.255.255.255)
		System.out.println("是否是本机的IP地址       :" + ip.isLoopbackAddress());
		//(10.0.0.0 ~ 10.255.255.255)(172.16.0.0 ~ 172.31.255.255)(192.168.0.0 ~ 192.168.255.255)
		System.out.println("是否是地区本地地址       :" + ip.isSiteLocalAddress());
		// 允许服务器主机接受来自任何网络接口的客户端连接
		System.out.println("是否是通配符地址         :" + ip.isAnyLocalAddress());
		// (169.254.0.0 ~ 169.254.255.255)
		System.out.println("是否是本地连接地址       :" + ip.isLinkLocalAddress());
		// (224.0.0.0 ~ 239.255.255.255)广播地址可以向网络中的所有计算机发送信息
		System.out.println("是否是 广播地址           :" + ip.isMulticastAddress());
		//  除了(224.0.0.0)和第一个字节是239的IP地址都是全球范围的广播地址
		System.out.println("是否是全球范围的广播地址:" + ip.isMCGlobal());
		// (224.0.0.0 ~ 224.0.0.255)
		System.out.println("是否是子网广播地址         :" + ip.isMCLinkLocal());
		// 本地接口广播地址不能将广播信息发送到产生广播信息的网络接口
		// 所有的IPv4广播地址都不是本地接口广播地址。
		System.out.println("是否是本地接口广播地址      :" + ip.isMCNodeLocal());
		// 可以向公司或企业内部的所有的计算机发送广播信息
		// IPv4的组织范围广播地址的第一个字节是239，第二个字节不小于192，第三个字节不大于195
		System.out.println("是否是组织范围的广播地址:" + ip.isMCOrgLocal());
	}

	private static String displayMac(byte[] mac) {
		if (mac == null) {
			return "";
		}
		StringBuilder bufferBuilder = new StringBuilder();
		for (int i = 0; i < mac.length; i++) {
			byte b = mac[i];
			int intValue = 0;
			if (b >= 0)
				intValue = b;
			else
				intValue = 256 + b;
			bufferBuilder.append(Integer.toHexString(intValue));

			if (i != mac.length - 1)
				bufferBuilder.append("-");
		}
		return bufferBuilder.toString();
	}
}

```
InetAddress 是对IP的高级表示。

InetAddress 相关API使用

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
```java
InetAddress address = InetAddress.getLocalHost();
System.out.println("Host Name : " + address.getHostName()); // 本地主机名取决于本地NDS在解析本地主机名时的行为
InetAddress address1 = InetAddress.getByName("180.149.131.98");
Assert.assertEquals("180.149.131.98", address1.getHostName());  // 为什么没有返回主机名？？
```

返回点分四段式ip地址
```java
InetAddress address = InetAddress.getLocalHost();
Assert.assertEquals("192.168.1.101", address.getHostAddress());
```

主要是用来测试地址类型是ipv4还是ipv6
```java
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
```java
InetAddress address = InetAddress.getByName("180.149.131.98");
System.out.println("is Rechable : " + address.isReachable(5));
System.out.println(address.isReachable(InetAddress., ttl, timeout));
```

测试是否是通配符地址. 通配符地址可匹配本地系统中所有地址. IPv4中通配符地址是0.0.0.0 IPv6是::
```java
InetAddress address = InetAddress.getLocalHost();
address.isAnyLocalAddress();
```
测试是否是回路地址. 回路地址在IP层连接同一台电脑,不使用任何物理硬件. 这就绕过了可能有问题的硬件设备进行测试  地址是127.0.0.1
```java
InetAddress address = InetAddress.getLocalHost();
address.isLoopbackAddress();
```

测试是否是IPv6本地连接地址(以FE80开头地址,后8个字节用以太网mac地址(本地地址)填充). 这个地址有助于实现IPv6网络自动配置 ,并且不会将包转发出本地子网
```java
InetAddress address = InetAddress.getLocalHost();
address.isLinkLocalAddress();
```

测试是否是IPv6本地网站地址(以FEC0开头地址,后8个字节用以太网mac地址(本地地址)填充). 这个地址只会被路由器在网站内转发
```java
InetAddress address = InetAddress.getLocalHost();
address.isSiteLocalAddress();
```
是否是组广播地址(IPV4:224.0.0.0-239.255.255.255) 向预定计算机进行广播
```java
InetAddress address = InetAddress.getLocalHost();
address.isMulticastAddress();
```

测试是否是全球广播地
```java
InetAddress address = InetAddress.getLocalHost();
address.isMCGlobal();
```
组织范围内广播地址
```java
InetAddress address = InetAddress.getLocalHost();
address.isMCOrgLocal();
```

是否是网站内组播地址
```java
InetAddress address = InetAddress.getLocalHost();
address.isMCSiteLocal();
```
子网范围内组播地址
```java
InetAddress address = InetAddress.getLocalHost();
address.isMCLinkLocal();
```
本地接口组播地址
```java
InetAddress address = InetAddress.getLocalHost();
address.isMCNodeLocal();
```
