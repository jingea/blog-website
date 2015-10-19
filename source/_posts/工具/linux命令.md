category: 工具
date: 2015-10-15
title: Linux命令使用
---

## 常用命令
### gzip
将文件或者文件夹压缩成后缀为`.gz`的文件

* `-a` 　使用ASCII文字模式。 
* `-c` 　把压缩后的文件输出到标准输出设备，不去更动原始文件。 
* `-d` 　解开压缩文件。 
* `-f` 　强行压缩文件。不理会文件名称或硬连接是否存在以及该文件是否为符号连接。 
* `-l` 　列出压缩文件的相关信息。 
* `-L` 　显示版本与版权信息。 
* `-n` 　压缩文件时，不保存原来的文件名称及时间戳记。 
* `-N` 　压缩文件时，保存原来的文件名称及时间戳记。 
* `-q` 　不显示警告信息。 
* `-r` 　递归处理，将指定目录下的所有文件及子目录一并处理。 
* `-S` 　更改压缩字尾字符串。 
* `-t` 　测试压缩文件是否正确无误。 
* `-v` 　显示指令执行过程。 
* `-num` 用指定的数字num调整压缩的速度，-1或--fast表示最快压缩方法（低压缩比），-9或--best表示最慢压缩方法（高压缩比）。系统缺省值为6。 

常用命令
* `gzip *`  把test6目录下的每个文件压缩成.gz文件 
* `gzip -dv *` 把例1中每个压缩的文件解压，并列出详细的信息
* `gzip -l *` 详细显示例1中每个压缩的文件的信息，并不解压
* `gzip -r log.tar` 压缩一个tar备份文件，此时压缩文件的扩展名为.tar.gz
* `gzip -rv test6` 递归的压缩目录
* `gzip -dr test6` 递归地解压目录

### grep
一种强大的文本搜索工具，它能使用正则表达式搜索文本，并把匹 配的行打印出来

主要参数：
* `－c`：只输出匹配行的计数。
* `－I`：不区分大小写(只适用于单字符)。
* `－h`：查询多文件时不显示文件名。
* `－l`：查询多文件时只输出包含匹配字符的文件名。
* `－n`：显示匹配行及行号。
* `－s`：不显示不存在或无匹配文本的错误信息。
* `－v`：显示不包含匹配文本的所有行。

pattern正则表达式主要参数：
* `\`： 忽略正则表达式中特殊字符的原有含义。
* `^`：匹配正则表达式的开始行。
* `$`: 匹配正则表达式的结束行。
* `\<`：从匹配正则表达 式的行开始。
* `\>`：到匹配正则表达式的行结束。
* `[ ]`：单个字符，如[A]即A符合要求 。
* `[ - ]`：范围，如[A-Z]，即A、B、C一直到Z都符合要求 。
* `。`：所有的单个字符。
* `*` ：有字符，长度可以为0。

### tar
tar可用于建立、还原、查看、管理文件，也可方 便的追加新文件到备份文件中，或仅更新部分的备份文件，以及解压、删除指定的文件


## 系统性能监控命令

### Vmstat
vmstat是Virtual Meomory Statistics（虚拟内存统计）的缩写, 是实时系统监控工具。

一般vmstat工具的使用是通过两个数字参数来完成的，第一个参数是采样的时间间隔数，单位是秒，第二个参数是采样的次数，如:
```
[root@cvs /]# vmstat 2 1
=>
procs  -----------memory----------     ---swap--  -----io----  --system--   -----cpu-----
 r  b    swpd   free   buff  cache      si   so     bi    bo    in   cs     us sy id wa st
 1  0  1339624 525012 464316 6796908    0    0      5    32     0    0      2  0 98  0  0
```
参数
* -a：显示活跃和非活跃内存
* -f：显示从系统启动至今的fork数量 。
* -m：显示slabinfo
* -n：只在开始时显示一次各字段名称。
* -s：显示内存相关统计信息及多种系统活动数量。
* delay：刷新时间间隔。如果不指定，只显示一条结果。
* count：刷新次数。如果不指定刷新次数，但指定了刷新时间间隔，这时刷新次数为无穷。
* -d：显示磁盘相关统计信息。
* -p：显示指定磁盘分区统计信息
* -S：使用指定单位显示。参数有 k 、K 、m 、M ，分别代表1000、1024、1000000、1048576字节（byte）。默认单位为K（1024 bytes）
* -V：显示vmstat版本信息。

![](https://raw.githubusercontent.com/ming15/blog-website/images/other/%E7%BA%BF%E7%A8%8B%E7%8A%B6%E6%80%81.gif)
我们从上面那个图来解释各个参数
> porcs
* r：表示运行队列(分配到CPU进程数)
* b：阻塞状态的进程数

> memory
* swpd：虚拟内存已使用的大小，如果大于0，表示你的机器物理内存不足了
* free：空闲的物理内存的大小
* buff：作为buffer使用的内存数量（Linux/Unix系统是用来存储，目录里面有什么内容，权限等的缓存）
* cache：作为缓存使用的内存数量（ cache直接用来记忆我们打开的文件,给文件做缓冲）
* inact：非活跃内存数
* active： 活跃内存数

> swap
* si：从磁盘交换的内存量（每秒从磁盘读入虚拟内存的大小，如果这个值大于0，表示物理内存不够用或者内存泄露了）
* so：向磁盘交换的内存量（每秒虚拟内存写入磁盘的大小，如果这个值大于0，同上）

> io
* bi：从阻塞设备接受到的块数据数量。
* bo：向阻塞设备发送的块数据数量。

> system
* in：每秒CPU的中断次数，包括时间中断
* cs：每秒上下文切换次数，例如我们调用系统函数，就要进行上下文切换，线程的切换，也要进程上下文切换，这个值要越小越好，太大了，要考虑调低线程或者进程的数目

> cpu
* us：用户CPU时间
* sy：系统CPU时间
* id：空闲 CPU时间
* wa：等待IO CPU时间
* st


### pidstat
监控锁竞争
```
[root@cvs /]# pidstat
Linux 2.6.32-279.el6.x86_64 (cvs)       2015年10月15日  _x86_64_        (8 CPU)

PID    %usr %system  %guest    %CPU   CPU  Command
```
显式参数
* PID : 被监控的任务的进程号
* %usr :  当在用户层执行(应用程序)时这个任务的cpu使用率，和 nice 优先级无关。注意这个字段计算的cpu时间不包括在虚拟处理器中花去的时间。
* %system :  这个任务在系统层使用时的cpu使用率。
* %guest ：  任务花费在虚拟机上的cpu使用率（运行在虚拟处理器）。
* %CPU ：  任务总的cpu使用率。在SMP环境(多处理器)中，如果在命令行中输入-I参数的话，cpu使用率会除以你的cpu数量。
* CPU ： 正在运行这个任务的处理器编号。
* Command ： 这个任务的命令名称。

参数
* -u: pidstat将显示各活动进程的cpu使用统计
* -p: 我们可以查看特定进程的系统资源使用情况：
* -r: pidstat将显示各活动进程的内存使用统计：
* -d: 我们可以查看进程IO的统计信息

> -d:
* kB_rd/s: 每秒进程从磁盘读取的数据量(以kB为单位)
* kB_wr/s: 每秒进程向磁盘写的数据量(以kB为单位)
* Command: 拉起进程对应的命令

> -r:
* minflt/s: 每秒次缺页错误次数(minor page faults)，次缺页错误次数意即虚拟内存地址映射成物理内存地址产生的page fault次数
* majflt/s: 每秒主缺页错误次数(major page faults)，当虚拟内存地址映射成物理内存地址时，相应的page在swap中，这样的page fault为major page fault，一般在内存使用紧张时产生
* VSZ:      该进程使用的虚拟内存(以kB为单位)
* RSS:      该进程使用的物理内存(以kB为单位)
* %MEM:     该进程使用内存的百分比
* Command:  拉起进程对应的命令

 
### iostat
iostat用于输出CPU和磁盘I/O相关的统计信息. 

* `-c` 仅显示CPU统计信息.与-d选项互斥.
* `-d` 仅显示磁盘统计信息.与-c选项互斥.
* `-k` 以K为单位显示每秒的磁盘请求数,默认单位块.
* `-p device | ALL`  与-x选项互斥,用于显示块设备及系统分区的统计信息.也可以在-p后指定一个设备名,如:
> # iostat -p hda
 或显示所有设备
 # iostat -p ALL
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

### uname 
* `-a` 　显示全部的信息。 
* `-m`　显示电脑类型。 
* `-n`　显示在网络上的主机名称。 
* `-r`　显示操作系统的发行编号。 
* `-s`　显示操作系统名称。 
* `-v` 　显示操作系统的版本。 


### sar 
系统报告命令
* sar -q 1 5    察看cpu的load状况，每1s钟统计1次，共统计5次
* sar -u 2 3   察看cpu使用率，每2s统计1次，共统计3次
* sar -r   察看当日内存占用情况(默认每10分钟统计一次)
* sar -b 察看当日IO使用情况
* sar -n SOCK   察看网络sock连接
* sar -n DEV 察看网络流量


### top  

### ps 

### df 
检查文件系统的磁盘空间占用情况
* -a 显示所有文件系统的磁盘使用情况，包括0块（block）的文件系统，如/proc文件系统。
* -k 以k字节为单位显示。
* -i 显示i节点信息，而不是磁盘块。
* -t 显示各指定类型的文件系统的磁盘空间使用情况。
* -x 列出不是某一指定类型文件系统的磁盘空间使用情况（与t选项相反）。
* -T 显示文件系统类型。
```
文件系统                 1K-块      已用      可用 已用% 挂载点
/dev/sda2             10079084   6660892   2906192  70% /
tmpfs                  8141376         0   8141376   0% /dev/shm
/dev/sda1             10079084    173308   9393776   2% /boot
/dev/sda5            257592732 241557292   2950464  99% /opt
```

### du 
显示每个文件和目录的磁盘使用空间。
* -a或-all  显示目录中个别文件的大小。   
* -b或-bytes  显示目录或文件大小时，以byte为单位。   
* -c或--total  除了显示个别目录或文件的大小外，同时也显示所有目录或文件的总和。 
* -k或--kilobytes  以KB(1024bytes)为单位输出。
* -m或--megabytes  以MB为单位输出。   
* -s或--summarize  仅显示总计，只列出最后加总的值。
* -h或--human-readable  以K，M，G为单位，提高信息的可读性。
* -x或--one-file-xystem  以一开始处理时的文件系统为准，若遇上其它不同的文件系统目录则略过。 
* -L<符号链接>或--dereference<符号链接> 显示选项中所指定符号链接的源文件大小。   
* -S或--separate-dirs   显示个别目录的大小时，并不含其子目录的大小。 
* -X<文件>或--exclude-from=<文件>  在<文件>指定目录或文件。   
* --exclude=<目录或文件>         略过指定的目录或文件。    
* -D或--dereference-args   显示指定符号链接的源文件大小。   
* -H或--si  与-h参数相同，但是K，M，G是以1000为换算单位。   
* -l或--count-links   重复计算硬件链接的文件。  
```
# 对当前目前下所有文件按文件大小倒排序，大小相同按文件名字母倒排序
du -ak | sort -t$'\t' -l1 -nr -k2 -r 
```

## 配置文件

### /proc/cpuinfo
```
[root@cvs /]# cat /proc/cpuinfo
processor       : 0
vendor_id       : GenuineIntel
cpu family      : 6
model           : 45
model name      : Intel(R) Xeon(R) CPU E5-2603 0 @ 1.80GHz
stepping        : 7
cpu MHz         : 1800.009
cache size      : 10240 KB
physical id     : 0
siblings        : 4
core id         : 0
cpu cores       : 4
apicid          : 0
initial apicid  : 0
fpu             : yes
fpu_exception   : yes
cpuid level     : 13
wp              : yes
flags           : fpu vme de pse tsc msr pae mce ...
bogomips        : 3600.01
clflush size    : 64
cache_alignment : 64
address sizes   : 46 bits physical, 48 bits virtual
power management:
```
* `processor`       : CPU号
* `vendor_id`       : CPU制造商   
* `cpu family`      : CPU产品系列代号
* `model`           : CPU属于其系列中的哪一代的代号
* `model name`      : CPU属于的名字及其编号、标称主频
* `stepping`        : CPU属于制作更新版本
* `cpu MHz`         : CPU的实际使用主频
* `cache size`      : CPU二级缓存大小
* `physical id`     : 单个CPU的标号
* `siblings`        : 单个CPU逻辑物理核数
* `core id`         : 当前物理核在其所处CPU中的编号，这个编号不一定连续
* `cpu cores`       : 该逻辑核所处CPU的物理核数
* `apicid`          : 用来区分不同逻辑核的编号，系统中每个逻辑核的此编号必然不同，此编号不一定连续
* `initial apicid`  :
* `fpu`             : 是否具有浮点运算单元（Floating Point Unit）
* `fpu_exception`   : 是否支持浮点计算异常
* `cpuid level`     : 执行cpuid指令前，eax寄存器中的值，根据不同的值cpuid指令会返回不同的内容
* `wp`              : 表明当前CPU是否在内核态支持对用户空间的写保护（Write Protection）
* `flags`           : 当前CPU支持的功能
* `bogomips`        : 在系统内核启动时粗略测算的CPU速度（Million Instructions Per Second）
* `clflush size`    : 每次刷新缓存的大小单位
* `cache_alignment` : 缓存地址对齐单位
* `address sizes`   : 可访问地址空间位数
* `power management`: 对能源管理的支持，有以下几个可选支持功能

### /proc/meminfo 
```
[root@cvs /]# cat /proc/meminfo 
MemTotal:       16282756 kB
MemFree:         2012664 kB
Buffers:          491980 kB
Cached:          5477644 kB
SwapCached:       110344 kB
Active:          9224100 kB
Inactive:        4478716 kB
Active(anon):    6410680 kB
Inactive(anon):  1322576 kB
Active(file):    2813420 kB
Inactive(file):  3156140 kB
Unevictable:           0 kB
Mlocked:               0 kB
SwapTotal:      10239992 kB
SwapFree:        8921364 kB
Dirty:              1176 kB
Writeback:             0 kB
AnonPages:       7679964 kB
Mapped:            25344 kB
Shmem:                36 kB
Slab:             328340 kB
SReclaimable:     284284 kB
SUnreclaim:        44056 kB
KernelStack:        8504 kB
PageTables:        27520 kB
NFS_Unstable:          0 kB
Bounce:                0 kB
WritebackTmp:          0 kB
CommitLimit:    18381368 kB
Committed_AS:   11103356 kB
VmallocTotal:   34359738367 kB
VmallocUsed:      307784 kB
VmallocChunk:   34350792204 kB
HardwareCorrupted:     0 kB
AnonHugePages:   6842368 kB
HugePages_Total:       0
HugePages_Free:        0
HugePages_Rsvd:        0
HugePages_Surp:        0
Hugepagesize:       2048 kB
DirectMap4k:        5056 kB
DirectMap2M:     2045952 kB
DirectMap1G:    14680064 kB
```
* `MemTotal`: 
* `MemFree`:空闲内存
* `Buffers`:给文件的缓冲大小
* `Cached`: 高速缓冲存储器(http://baike.baidu.com/view/496990.htm)使用的大小
* `SwapCached`: 被高速缓冲存储用的交换空间大小
* `Active`: 活跃使用中的高速缓冲存储器页面文件大小
* `Inactive`: 不经常使用的高速缓冲存储器页面文件大小
* `Active(anon)`: 
* `Inactive(anon)`: 
* `Active(file)`: 
* `Inactive(file)`: 
* `Unevictable`:
* `Mlocked`:
* `SwapTotal`:交换空间总大小
* `SwapFree`: 空闲交换空间
* `Dirty`:等待被写回到磁盘的大小
* `Writeback`:正在被写回的大小
* `AnonPages`:未映射的页的大小
* `Mapped`: 设备和文件映射的大小
* `Shmem`:
* `Slab`: 内核数据结构缓存的大小，可减少申请和释放内存带来的消耗
* `SReclaimable`: 可收回slab的大小
* `SUnreclaim`: 不可收回的slab的大小23204+14164=37368
* `KernelStack`:
* `PageTables`: 管理内存分页的索引表的大小
* `NFS_Unstable`: 不稳定页表的大小
* `Bounce`: bounce:退回
* `WritebackTmp`: 
* `CommitLimit`:
* `Committed_AS`: 
* `VmallocTotal`: 虚拟内存大小
* `VmallocUsed`:已经被使用的虚拟内存大小
* `VmallocChunk`: 
* `HardwareCorrupted`:
* `AnonHugePages`:
* `HugePages_Total`:大页面的分配
* `HugePages_Free`: 
* `HugePages_Rsvd`: 
* `HugePages_Surp`: 
* `Hugepagesize`: 
* `DirectMap4k`:
* `DirectMap2M`:
* `DirectMap1G`:

### /proc/net/dev
```
[root@cvs /]# cat /proc/net/dev
Inter-|   Receive                                                |  Transmit
 face |bytes    packets errs drop fifo frame compressed multicast|bytes    packets errs drop fifo colls carrier compressed
    lo:365528559149 865504543    0    0    0     0          0         0 365528559149 865504543    0    0    0     0       0          0
   em1:542483270223 575346473    0    0    0    62          0   8267561 580200919340 586706511    0    0    0     0       0          0
   em2:       0       0    0    0    0     0          0         0        0       0    0    0    0     0       0          0
   em3:       0       0    0    0    0     0          0         0        0       0    0    0    0     0       0          0
   em4:       0       0    0    0    0     0          0         0        0       0    0    0    0     0       0          0
```

> Inter                                                     
* `face`:接口的名字
 
> Receive
* `bytes`: 收发的字节数   
* `packets`: 收发正确的包量
* `errs`: 收发错误的包量
* `drop`: 收发丢弃的包量
* `fifo`: 
* `frame`: 
* `compressed`: 
* `multicast`:

> Transmit
* `bytes`: 收发的字节数   
* `packets`: 收发正确的包量
* `errs`: 收发错误的包量
* `drop`: 收发丢弃的包量
* `fifo`: 
* `colls`: 
* `carrier`: 
* `compressed`:


![性能测评工具](https://raw.githubusercontent.com/ming15/blog-website/images/other/Linux%20%E6%80%A7%E8%83%BD%E6%B5%8B%E8%AF%84%E5%B7%A5%E5%85%B7.jpg)
![性能观测工具](https://raw.githubusercontent.com/ming15/blog-website/images/other/Linux%20%E6%80%A7%E8%83%BD%E8%A7%82%E6%B5%8B%E5%B7%A5%E5%85%B7.jpg)
![性能调优工具](https://raw.githubusercontent.com/ming15/blog-website/images/other/Linux%20%E6%80%A7%E8%83%BD%E8%B0%83%E4%BC%98%E5%B7%A5%E5%85%B7.jpg)
![](https://github.com/ming15/blog-website/blob/images/other/Linux%20observability%20sar.jpg)

## 其他小工具

### nload
可以分开来监控入站流量和出站流量

### iftop
可测量通过每一个套接字连接传输的数据

### nethogs
显示每个进程所使用的带宽

### bmon
一款类似nload的工具，它可以显示系统上所有网络接口的流量负载

### speedometer
绘制外观漂亮的图形，显示通过某个接口传输的入站流量和出站流量。

### pktstat
实时显示所有活动连接，并显示哪些数据通过这些活动连接传输的速度。它还可以显示连接类型，比如TCP连接或UDP连接；如果涉及HTTP连接，还会显示关于HTTP请求的详细信息。

### dstat
监控系统的不同统计信息，并使用批处理模式来报告，或者将相关数据记入到CSV或类似的文件
