category: JavaSE
date: 2014-09-20
title: Java网络接口
---
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
