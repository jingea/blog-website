category: 网络
date: 2016-04-06
title: IP地址在网卡上的表现
---
今天遇到一个比较奇葩的问题, 在一个JDBC程序连接数据库的时候, 使用IPV4地址就无法访问, 使用localhost就成功, 现在就对这个问题一探究竟
```java
jdbc:mysql://localhost:3306/c2x?autoReconnect=true
```

首先要说明一下localhost, 127.0.0.1和ipv4之间的关系
* localhost : 首先这不是一个地址, 而是一个域名, 类似于www.baidu.com这种东西
* 127.0.0.1 : 127开头的网段默认都是属于loopback接口, 用于测试本机TCP/IP协议栈. 它被分配在了一个虚拟网卡上
* ipv4 : 是一个真实网卡上的ip地址.

首先我们看一下win7系统上的C:\Windows\System32\drivers\etc的host配置文件
```xml
# localhost name resolution is handled within DNS itself.
#	127.0.0.1       localhost
#	::1             localhost
```
我们看到127.0.0.1就映射到了localhost, ::1是在ipv6的前提下分配到的localhost. 通过这个配置文件我们就看到了localhost和 127.0.0.1这二者之间的关系.

下面我们再使用`ipconfig/all`这个命令看一下Windows的网卡配置
```xml
Windows IP 配置

   主机名  . . . . . . . . . . . . . : OA1503P0256
   主 DNS 后缀 . . . . . . . . . . . : xxx(lol)
   节点类型  . . . . . . . . . . . . : 混合
   IP 路由已启用 . . . . . . . . . . : 否
   WINS 代理已启用 . . . . . . . . . : 否
   DNS 后缀搜索列表  . . . . . . . . : xxx(lol)

以太网适配器 本地连接:

   连接特定的 DNS 后缀 . . . . . . . : xxx(lol)
   描述. . . . . . . . . . . . . . . : Realtek PCIe GBE Family Controller
   物理地址. . . . . . . . . . . . . : F0-79-59-64-74-71
   DHCP 已启用 . . . . . . . . . . . : 是
   自动配置已启用. . . . . . . . . . : 是
   本地链接 IPv6 地址. . . . . . . . : fe80::1d00:63da:2b98:8c05%11(首选) 
   IPv4 地址 . . . . . . . . . . . . : 192.168.10.220(首选) 
   子网掩码  . . . . . . . . . . . . : 255.255.255.0
   获得租约的时间  . . . . . . . . . : 2016年3月29日 10:05:04
   租约过期的时间  . . . . . . . . . : 2016年4月6日 16:32:42
   默认网关. . . . . . . . . . . . . : 192.168.10.254
   DHCP 服务器 . . . . . . . . . . . : 192.168.10.202
   DHCPv6 IAID . . . . . . . . . . . : 235430502
   DHCPv6 客户端 DUID  . . . . . . . : 00-01-00-01-1C-AE-A2-19-F0-79-59-64-74-71
   DNS 服务器  . . . . . . . . . . . : 192.168.10.12
                                       192.168.15.3
   TCPIP 上的 NetBIOS  . . . . . . . : 已启用

隧道适配器 isatap.xxx(lol):

   媒体状态  . . . . . . . . . . . . : 媒体已断开
   连接特定的 DNS 后缀 . . . . . . . : xxx(lol)
   描述. . . . . . . . . . . . . . . : Microsoft ISATAP Adapter
   物理地址. . . . . . . . . . . . . : 00-00-00-00-00-00-00-E0
   DHCP 已启用 . . . . . . . . . . . : 否
   自动配置已启用. . . . . . . . . . : 是

隧道适配器 Teredo Tunneling Pseudo-Interface:

   媒体状态  . . . . . . . . . . . . : 媒体已断开
   连接特定的 DNS 后缀 . . . . . . . : 
   描述. . . . . . . . . . . . . . . : Teredo Tunneling Pseudo-Interface
   物理地址. . . . . . . . . . . . . : 00-00-00-00-00-00-00-E0
   DHCP 已启用 . . . . . . . . . . . : 否
   自动配置已启用. . . . . . . . . . : 是

```

好了, 最后验证一下刚开始谈到的那个数据库的问题. 当我分别使用`192.168.10.20`和`localhost`通过SQLyog连接时, 果真看到的是俩个不一样的数据库....

然后我又写了一个测试程序
```java
public class TestLocalhost {

    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress("192.168.10.220", 9051));
//        serverSocket.bind(new InetSocketAddress("localhost", 9051));
        serverSocket.accept();
        System.out.println("connected!!!");
    }
}
```
然后使用http请求测试
* `http://localhost:9051/` 不可连接成功
* `http://192.168.10.220:9051/`  连接成功
说明`192.168.10.20`和`localhost`本身是不能互通的.

接下来我改进一下这个程序
```java
public class TestLocalhost {

    public static void main(String[] args) throws InterruptedException {
        SocketAcceptor socketAcceptor1 = new SocketAcceptor("192.168.10.220");
        SocketAcceptor socketAcceptor2 = new SocketAcceptor("localhost");
        socketAcceptor1.start();
        socketAcceptor2.start();

        TimeUnit.SECONDS.sleep(100);
    }

    public static class SocketAcceptor extends Thread {

        public final String ip;
        public SocketAcceptor(String ip) {
            this.ip = ip;
        }

        @Override
        public void run() {
            try {
                ServerSocket serverSocket1 = new ServerSocket();
                serverSocket1.bind(new InetSocketAddress(ip, 9051));
                serverSocket1.accept();
                System.out.println(ip + " accepted");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
```
然后继续使用上面俩个地址进行访问, 发现最后都进行了输出
```xml
192.168.10.220 accepted
localhost accepted
```
这个例子也说明了不同的网卡可以绑定不同的端口, 即使是虚拟网卡也有一套自己端口