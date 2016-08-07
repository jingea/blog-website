category: linux
date: 2016-08-02
title: linux 网络监控一探
---
最近服务器在跑机器人, 看过内存和cpu之后, 想要看一下2500个机器人的流量情况, 打开zabix一看, 我擦嘞 Outgoing network traffic on eth0 的流量居然是从 20M~60M之间浮动, 这不科学啊. 但是后来一想这可能是整个机器的网络带宽, 于是便想找到一款可以以进程或者以端口为单元的监控工具. 

在百度上搜索了一下, 居然有这么多的网络监控工具 (参考[一些你可能不知道的Linux网络工具](http://www.linuxdiyf.com/linux/12131.html))
* nethogs
* ntopng
* nload
* iftop
* iptraf
* bmon
* slurm
* tcptrack
* cbm
* netwatch
* collectl
* trafshow
* cacti
* etherape
* ipband
* jnettop
* netspeed 
* speedometer
下面就开始了我的探索之旅

## iftop
首先找到的是这个软件, 听说它可以实现我的要求, 于是开始安装

首先执行下列命令进行安装
```
yum install flex byacc  libpcap ncurses ncurses-devel libpcap-devel 
```
ok, 依赖安装完成,但是在`yum install iftop`时提示
```
[root@~]# yum install iftop
Loaded plugins: fastestmirror, refresh-packagekit, security
Loading mirror speeds from cached hostfile
 * base: mirrors.tuna.tsinghua.edu.cn
 * epel: mirrors.neusoft.edu.cn
 * extras: mirrors.tuna.tsinghua.edu.cn
 * updates: mirrors.tuna.tsinghua.edu.cn
Setting up Install Process
Resolving Dependencies
--> Running transaction check
---> Package iftop.x86_64 0:1.0-0.7.pre4.el5 will be installed
--> Processing Dependency: libpcap.so.0.9.4()(64bit) for package: iftop-1.0-0.7.pre4.el5.x86_64
--> Finished Dependency Resolution
Error: Package: iftop-1.0-0.7.pre4.el5.x86_64 (epel)
           Requires: libpcap.so.0.9.4()(64bit)
 You could try using --skip-broken to work around the problem
 You could try running: rpm -Va --nofiles --nodigest
```
对libpcap的依赖和我们安装的版本不一致, 于是我尝试下载安装包手动安装试试
```
wget http://www.ex-parrot.com/~pdw/iftop/download/iftop-0.17.tar.gz
cd iftop-0.17
./configure
make && make install
```
安装完成, 试试有没有安装成功
```
iftop
```
ok成功进入.


界面上面显示的是类似刻度尺的刻度范围，为显示流量图形的长条作标尺用的。中间的<= =>这两个左右箭头，表示的是流量的方向。

* TX：发送流量
* RX：接收流量
* TOTAL：总流量
* Cumm：运行iftop到目前时间的总流量
* peak：流量峰值
* rates：分别表示过去 2s 10s 40s 的平均流量

这个界面是以每个客户端连接到服务器的连接为单位进行显示的. 虽然达不到要以本机为单位显示端口和进程的带宽和流量, 但是也蛮不错的了.

> 具体这个软件的其他参数,大家可以百度一下

## iptraf
后来又找到了iptraf这个软件, 是一个基于ncurses的IP局域网监控器，用来生成包括TCP信息、UDP计数、ICMP和OSPF信息、以太网负载信息、节点状态信息、IP校验和错误等等统计数据。


这个软件安装很简单, 不需要安装什么依赖
```
yum install iptraf
```
打开看了一下, 这个软件更多的是基于网卡, 和IP进行监听. 虽然在Staticstics breakdowns上可以看到端口信息, 但是不知道为什么没有看到应用程序的端口. 没办法接着百度, 于是在这篇文章里找到了答案[Linux中iptraf命令详解](http://blog.csdn.net/quiet_girl/article/details/50777210)原来在Configure里的Additional ports里设置需要监听的端口, 默认只监听1000以下的. 

设置完以后, 再次进入Staticstics breakdowns可以看到我们要监控的端口了, 但是监测的出来的还有其他没有在我设定那个范围里的端口。 嗯，又研究了一下,发现在端口配置里是可以选择多个端口的. 只要配置多个端口就可以了. 但是还有个问题, 就是统计的流量都是总计的,没有实时或者平均值.

## nethogs
nethogs 会根据进程来进行分组(ok,第一个要求达到了), 而且打开之后也会显示该进程的实时流量.
```bash
[root@~]# nethogs -p eth0

NetHogs version 0.8.0

PID USER     PROGRAM                                                                        DEV       SEN         RECEIVED       
23095 root   java                                                                           eth0      421.746     138.566 KB/sec
10867 root     sshd: root@pts/1                                                             eth0      1.825       0.059 KB/sec
?     root     192.168.15.25:10050-192.168.15.12:48691                                            0.000       0.014 KB/sec
?     root     192.168.15.25:19001-192.168.10.220:26680                                               0.000       0.000 KB/sec
?     root     192.168.15.25:10050-192.168.15.12:48608                                                0.000       0.000 KB/sec
?     root     192.168.15.25:10050-192.168.15.12:48096                                                0.000       0.000 KB/sec
?     root     192.168.15.25:10050-192.168.15.12:48082                                                0.000       0.000 KB/sec
23182 root     java                                                                        eth0       0.000       0.000 KB/sec
?     root     unknown TCP                                                                            0.000       0.000 KB/sec

TOTAL                                                                                                 423.571     138.639 KB/sec 
```
完美, nethogs完美解决了我们的问题

## vnStat
vnStat是一个基于控制台的网络流量监控工具，是为Linux和BSD设计的。它可以保留某个或多个所选择的网络接口的网络流量日志。为了生成日志，vnStat使用内核提供的信息。换句话说，它不会嗅探网络流量，确保尽量少用系统资源。
```bash
[root@ ~]# vnstat --help
 vnStat 1.6 by Teemu Toivola <tst at iki dot fi>

         -q,  --query          query database
         -h,  --hours          show hours
         -d,  --days           show days
         -m,  --months         show months
         -w,  --weeks          show weeks
         -t,  --top10          show top10
         -s,  --short          use short output
         -u,  --update         update database
         -i,  --iface          select interface (default: eth0)
         -?,  --help           short help
         -v,  --version        show version
         -tr, --traffic        calculate traffic
         -l,  --live           实时显示流量数据

See also "--longhelp" for complete options list and "man vnstat".
```
测试
```bash
[root@ ~]# vnstat 
Database updated: Tue Aug  2 14:08:15 2016

        eth0

           received:     686.90 MB (22.6%)
        transmitted:       2.30 GB (77.4%)
              total:       2.97 GB

                        rx     |     tx     |  total
        -----------------------+------------+-----------
            today    686.90 MB |    2.30 GB |    2.97 GB
        -----------------------+------------+-----------
        estimated      1.14 GB |    3.91 GB |    5.04 GB
[root@ ~]# vnstat -l -u
Monitoring eth0...    (press CTRL-C to stop)

   rx:     791.08 kB/s 11969 p/s            tx:    2482.06 kB/s 21979 p/s^C


 eth0  /  traffic statistics

                             rx       |       tx
--------------------------------------+----------------------------------------
  bytes                      7.27 MB  |      22.35 MB
--------------------------------------+----------------------------------------
          max            894.73 kB/s  |     2.60 MB/s
      average            620.59 kB/s  |     1.86 MB/s
          min            599.12 kB/s  |     1.82 MB/s
--------------------------------------+----------------------------------------
  packets                     111771  |        200661
--------------------------------------+----------------------------------------
          max              13430 p/s  |     22767 p/s
      average               9314 p/s  |     16721 p/s
          min               9019 p/s  |     16711 p/s
--------------------------------------+----------------------------------------
  time                    12 seconds

[root@ ~]# vnstat 
Database updated: Tue Aug  2 14:08:56 2016

        eth0

           received:     712.66 MB (22.6%)
        transmitted:       2.38 GB (77.4%)
              total:       3.07 GB

                        rx     |     tx     |  total
        -----------------------+------------+-----------
            today    712.66 MB |    2.38 GB |    3.07 GB
        -----------------------+------------+-----------
        estimated      1.18 GB |    4.03 GB |    5.21 GB
[root@ ~]#  
```
我们看到只有使用了`-u`参数将数据更新到数据库之后, 才能再次使用vnstat时将数据显示出来. 因此cnstat更像是一个历史记录工具

## tcpdump
对网络上的数据包进行截获的包分析工具. 虽然这个工具, 不是解决今天这个问题, 但是还是在这里记录一下, 万一用得到呢

> 另外还有tcpflow工具, 与tcpdump不同的是它是以流为单位显示数据内容，而cpdump以包为单位显示数据。