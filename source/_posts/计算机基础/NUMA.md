category: 计算机基础
date: 2015-10-26
title: NUMA和RDMA 
---

## 支持NUMA的CPU

英特尔系列的
* Nehalem和Tukwila系列之后的处理器
* Xeon
* 至强处理器 E5-2690 
* i3、i5、i7

HP系列的
* Superdome
* SGI的Altix 3000
 
IBM的
* p690
* x440
 
NEC
* TX7

AMD
* Opteron


## Linux中关于NUMA的命令
* NUMACTL ：设定进程NUMA策略的命令行工具。
* NUMASTAT ：获取NUMA内存访问统计信息的命令行工具

开启NUMA
```
numactl --cpunodebind=0 --membind=0 myapp
```


# JAVA中的NUMA
`-XX:+UseNUMA`启用Numa, 默认情况下,JVM所占内存会随机的分配到不同的NUMA节点上,当cpu运算时可能会到不同的NUMA节点的告诉缓存上进行数据查找,降低系统速度,可使用numactl工具将JVM进程绑定到特定的NUMA节点上,只让其访问自己所在节点的内存.

# NUMA用于MySQL
调优的时候，有哪些关键配置项，需要注意什么

* `numactl --interleave=all`
* 在MySQL进程启动前，使用`sysctl -q -w vm.drop_caches=3`清空文件缓存所占用的空间
* Innodb在启动时，就完成整个`Innodb_buffer_pool_size`的内存分配

# RDMA
RDMA 技术需要怎样的硬件，寻找一篇RDMA用于数据库或者Java的文章，对其性能和用法做简单的阐述。

RDMA是一种网卡技术,采用该技术可以使一台计算机直接将信息放入另一台计算机的内存中。RDMA通过在网卡上将可靠传输协议固化于硬件,以及支持绕过内核的零拷贝网络这两种途径来达到这一目标。绕过内核使应用程序不必执行内核调用就可以向网卡发出命令。当一个应用程序执行RDMA读/写请求时,系统并不执行数据拷贝动作。这就减少了处理 网络通信时在内核空间和用户空间上下文切换的次数。RDMA请求的完成,或者完全在用户空间中进行,或者在应用程序希望进入睡眠直到完成信号出现的情况下 通过内核进行。

RDMA实现：
* 利用传统的网络硬件,以TCP/IP及以太网络标准来建立因特网
* InfiniBand网络和实现虚拟接口架构的网络支持RDMA.

采用RDMA来获取高性能的协议包括
* Sockets Direct Protocol
* SCSI RDMA Protocol（SRP）
* Direct Access File System（DAFS）

## Java 7 SDP
在Solaris系统上只要有物理InfiniBand网卡，Java 7 SDP就可以立即工作。

Linux则通过Open Fabrics Enterprise Distribution（OFED）包支持SDP。
> 确认Linux版本有没有配置OFED设备驱动器，`egrep "^[ \t]+ib" /proc/net/dev`可以使用该命令测试.

Java只通过`java.net.Socket、java.net.ServerSocket、java.net.Datagram`对传输层进行抽象,通过`Java.net.InetAddress`对网络层进行抽象.只需要将JVM和InfiniBand操作系统设备和库进行设定,java就可以通过传输层抽象直接与物理层进行访问,从而绕过了网络层和数据链路层.

SDP也能让Java具备非常强大的“零拷贝”,这个零拷贝并不是指的是`java.nio.channels.FileChannel`的`transferTo()`实现的零拷贝.而是直接使用原生的InfiniBand零拷贝协议实现。现在CPU不用将一个内存区域的数据拷贝到另一个内存区域。CPU可以继续处理其他任务，数据拷贝则由机器的另一部分并行处理，这样就提升了性能。此外，零拷贝操作减少了在用户空间和内核空间之间切换所消耗的时间。

![](https://raw.githubusercontent.com/ming15/blog-website/images/net/javasdp.jpg)

### 配置JVM7支持SDP
SDP配置文件是个文本文件，JVM启动时会从本地文件系统读取该文件。我们有俩种规则来定义：
1. bind规则：只要TCP套接字绑定到与规则匹配的地址和端口，就会使用SDP协议进行传输。
2. connect规则：没有绑定的TCP套接字尝试连接匹配规则的地址和端口时，就会使用SDP协议进行传输。
第一个关键字用来表明规则是bind还是connect。第二部分指定主机名或IP地址。当指定为IP地址时，你也可以指定表示IP地址范围的前缀。第三部分即最后一个部分是端口号或端口号范围。
```
# 绑定到192.168.1.196主机所有端口使用SDP
bind 192.168.1.196 *

# 连接到192.168.2.*上的所有应用服务时都使用SDP
connect 192.168.2.0/24 1024-*
```
### 使用SDP的JVM7
```
java \
-Dcom.sun.sdp.conf=sdp.conf \
-Djava.net.preferIPv4Stack=true \
Application.class
```
> 注意要指定网络格式为`IPv4Stack`。尽管Java 7和InfiniBand都支持IPv6网络格式，但Solaris和Linux都不支持两者之间的映射。所以启动支持SDP的Java 7 VM时，还是要使用基础、可靠的IPv4网络格式。

## IO性能度量
iostat用于输出CPU和磁盘I/O相关的统计信息. 

* `-c` 仅显示CPU统计信息.与-d选项互斥.
* `-d` 仅显示磁盘统计信息.与-c选项互斥.
* `-k` 以K为单位显示每秒的磁盘请求数,默认单位块.
* `-p device | ALL`  与-x选项互斥,用于显示块设备及系统分区的统计信息.也可以在-p后指定一个设备名,如:`iostat -p hda` 或显示所有设备`iostat -p ALL`
* `-t`    在输出数据时,打印搜集数据的时间.
* `-V`    打印版本号和帮助信息.
* `-x`    输出扩展信息.
* 
```
[root@cvs /]# iostat
Linux 2.6.32-279.el6.x86_64 (cvs)       2015年10月16日  _x86_64_        (8 CPU)

avg-cpu:  %user   %nice %system %iowait  %steal   %idle
           1.67    0.00    0.21    0.38    0.00   97.74

Device:            tps   Blk_read/s   Blk_wrtn/s   Blk_read   Blk_wrtn
sda              18.64        72.79       512.65  951732770 6702726216
```
avg-cpu:  
* `%user`: 在用户级别运行所使用的CPU的百分比.
* `%nice`: nice操作所使用的CPU的百分比.
* `%system`: 在系统级别(kernel)运行所使用CPU的百分比.
* `%iowait`: CPU等待硬件I/O时,所占用CPU百分比.
* `%steal`: 
* `%idle`: CPU空闲时间的百分比.
           

Device:            
* `tps`: 每秒钟发送到的I/O请求数.
* `Blk_read/s`: 每秒读取的block数.
* `Blk_wrtn/s`: 每秒写入的block数.
* `Blk_read`: 读入的block总数.
* `Blk_wrtn`: 写入的block总数.


* `Blk_read` 读入块的当总数.
* `Blk_wrtn` 写入块的总数.
* `kB_read/s` 每秒从驱动器读入的数据量,单位为K.
* `kB_wrtn/s` 每秒向驱动器写入的数据量,单位为K.
* `kB_read` 读入的数据总量,单位为K.
* `kB_wrtn` 写入的数据总量,单位为K.
* `rrqm/s`  将读入请求合并后,每秒发送到设备的读入请求数.
* `wrqm/s`  将写入请求合并后,每秒发送到设备的写入请求数.
* `r/s`     每秒发送到设备的读入请求数.
* `w/s`     每秒发送到设备的写入请求数.
* `rsec/s`  每秒从设备读入的扇区数.
* `wsec/s`  每秒向设备写入的扇区数.
* `rkB/s`  每秒从设备读入的数据量,单位为K.
* `wkB/s`  每秒向设备写入的数据量,单位为K.
* `avgrq-sz`  发送到设备的请求的平均大小,单位是扇区.
* `avgqu-sz` 发送到设备的请求的平均队列长度.
* `await`  I/O请求平均执行时间.包括发送请求和执行的时间.单位是毫秒.
* `svctm` 发送到设备的I/O请求的平均执行时间.单位是毫秒.
* `%util`  在I/O请求发送到设备期间,占用CPU时间的百分比.用于显示设备的带宽利用率.当这个值接近100%时,表示设备带宽已经占满.

### 全SSD硬盘的IO阀值
SSD 硬盘传输速率取 500M/S
```
0.1ms + 0 + 4K/500MB = 0.1 + 0 + 0.008 = 0.108

IOPS = 1/0.108 ms = 9259 IOPS
```
吞吐率 = `9259 * 4K = 37M / 500M = 7.4%`


### 1万转机械磁盘的IO阀值
1 万转机械磁盘传输速率取 200M/S
```
5ms + (60sec/10000RPM/2) + 4K/200MB = 5 + 3 + 0.02 = 8.02 IOPS = 1/8.02s ms = 125 IOPS
```
吞吐率 = `125 * 4K = 500K / 200M = 0.25%`