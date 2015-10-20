category: java基础
date: 2014-09-08
title: java网络
---
# Java里使用的是TCP/IP
* 应用层协议：(例如Http协议) 该层数据由下三层协议共同制定
* 传输层协议：(常用TCP,UDP)(ICMP Ping命令基于该协议). 该层协议用于确保数据报以发送时的顺序接受,并且不会丢包. 如果发现顺序有误,或者数据丢失,则可要求对方重新发送数据(TCP会要求这一点, 但是UDP协议只是检查数据发送顺序,以及数据是否丢失并不要求对方重传数据)
* 网络层协议：(使用最广泛的是IP协议)
> 网络层第一任务是对数据位或者字节进行分组,打成包(包内数据称为数据报).网络层第二任务定义了主机彼此间的寻址方式(例如IPV4用四个字节来标识一个地址).在JAVA里,IP协议是它唯一理解的网络层协议.

## IP数据报格式.
链路层协议：定义了网络接口(以太网接口或者环牌接口)

谈一下Internet地址分类 (具体定义参考 WIKI IP地址), IP地址分为A,B,C,D,E,F类 (E,F分别作为广播地址这里不说了)
* A类地址 第一个字节固定
* B类地址 前俩个地址固定
* C类地址 前三个地址固定
这里所说的固定指的是ISP给你的时候就固定了,你只能使用固定之后几位的地址.例如给了你一个C类地址 那么你只有256个地址可以使用.

后来为了节约地址,出现了CIDR  用/nn 指定前几位为固定的.例如/24 为前24位即前三个字节是固定的也就是一个c类地址.这么着就拟补了有的组织使用的IP大于c类却远远小于B类而造成的地址浪费.

在这里需要特殊说明的是有一些非路由地址,例如10; 192.16或者172.16到172.31 开头的地址.这些地址用于构建组织内部网路(例如家里只有一个IP但是却有很多设备,这时就需要通过路由为这些设备分配IP地址了),或者一些大型组织使用C类地址时非常有用
路由器会将非路由地址转换为外部地址

# URI
```
scheme:scheme-specific-part (模式:模式特有部分)
```
## 模式
模式包含 data, file, ftp, http, news, telnet, urn (还有基于JAVA的rmi, jndi 等非标准模式,也称为protocol)
例如：`http://www.ming15.wang/2015/10/13/%E5%B7%A5%E5%85%B7/2015-10-12-AWK/`这个例子中模式为`http`, 负责解析该URI的机构`ming15.wang` 负责将`/2015/10/13/%E5%B7%A5%E5%85%B7/2015-10-12-AWK/`地址映射到主机资源

还有的URI路径中含有? 这是URI的查询部分.后面紧跟查询参数,多个参数用&分割. 例如：`git@github.com:ming15/VertxServer.git`该URI中模式为`git` 解析结构为`github.com` 还可以在git和@之间加上用户名和密码`git://username:password@github.com:ming15/VertxServer.git`

URI一般由以下组成
* 模式 
* URI解析结构 
* 资源路径 
* 查询参数构成


## URI分为类
1. URL ： 指向Internet上某个位置的某个文件.用于标识Internet上的资源位置. 指定访问服务器的协议, 服务器名, 文件在次服务器上的位置`protocol://username@hostname:port/path/filename?query#fragment`协议可以看成是模式但是它不包含URN.
2. URN ：不指向位置的资源名.  (具体的内容参考例子磁力链接)`urn:namespace:resource_name`. `namespace`:某个授权机构维护的某类资源的集合名.  `resource_name` 集合中的资源名


这里简述一下相对URL. 举例来说<a href="java.html"> 这个超链接会继承父文档(当前文档)的协议, 主机名, 资源路径.java.html会替换掉,父文档里最后的文件名,还有例如<a href="/demo/java.html"> 那这个超链接会将主机名后的资源路径一起换掉 ，用该路径替换

# Java网卡信息获取
网络接口的命名
* eth0: ethernet的简写，一般用于以太网接口。
* wifi0:wifi是无线局域网，因此wifi0一般指无线网络接口。
* ath0: Atheros的简写，一般指Atheros芯片所包含的无线网络接口。
*　lo: local的简写，一般指本地环回接口。

lo: 虚拟网络接口,其并不真实地从外界接收和发送数据包，而是在系统内部接收和发送数据包，因此虚拟网络接口不需要驱动程序.硬件网卡的网络接口由驱动程序创建。而虚拟的网络接口由系统创建或通过应用层程序创建。假如包是由一个本地进程为另一个本地进程产生的, 它们将通过外出链的’lo’接口,然后返回进入链的’lo’接口

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