category: linux
date: 2015-10-15
title: Linux系统命令
---
### Vmstat
vmstat是Virtual Meomory Statistics（虚拟内存统计）的缩写, 是实时系统监控工具。

一般vmstat工具的使用是通过两个数字参数来完成的，第一个参数是采样的时间间隔数，单位是秒，第二个参数是采样的次数，如:
```shell
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
```shell
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

-d:
* kB_rd/s: 每秒进程从磁盘读取的数据量(以kB为单位)
* kB_wr/s: 每秒进程向磁盘写的数据量(以kB为单位)
* Command: 拉起进程对应的命令

-r:
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
* `-p device | ALL`  与-x选项互斥,用于显示块设备及系统分区的统计信息.也可以在-p后指定一个设备名,如:`iostat -p hda` 或显示所有设备`iostat -p ALL`
* `-t`    在输出数据时,打印搜集数据的时间.
* `-V`    打印版本号和帮助信息.
* `-x`    输出扩展信息.

```shell
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
```xml
[root@test game]# uname -a
Linux test 2.6.32-431.el6.x86_64 #1 SMP Fri Nov 22 03:15:09 UTC 2013 x86_64 x86_64 x86_64 GNU/Linux
```

> 查看发行版本 `cat /etc/issue`

### sar
系统报告命令
* `sar -q 1 5`    察看cpu的load状况，每1s钟统计1次，共统计5次
* `sar -u 2 3`   察看cpu使用率，每2s统计1次，共统计3次
* `sar -r`   察看当日内存占用情况(默认每10分钟统计一次)
* `sar -b` 察看当日IO使用情况
* `sar -n SOCK`   察看网络sock连接
* `sar -n DEV` 察看网络流量


### top  



### df
检查文件系统的磁盘空间占用情况
* `-a` 显示所有文件系统的磁盘使用情况，包括0块（block）的文件系统，如/proc文件系统。
* `-k` 以k字节为单位显示。
* `-i` 显示i节点信息，而不是磁盘块。
* `-t` 显示各指定类型的文件系统的磁盘空间使用情况。
* `-x` 列出不是某一指定类型文件系统的磁盘空间使用情况（与t选项相反）。
* `-T` 显示文件系统类型。
```shell
文件系统                 1K-块      已用      可用 已用% 挂载点
/dev/sda2             10079084   6660892   2906192  70% /
tmpfs                  8141376         0   8141376   0% /dev/shm
/dev/sda1             10079084    173308   9393776   2% /boot
/dev/sda5            257592732 241557292   2950464  99% /opt
```

### du
显示每个文件和目录的磁盘使用空间。
* `-a或-all`  显示目录中个别文件的大小。   
* `-b或-bytes`  显示目录或文件大小时，以byte为单位。   
* `-c或--total`  除了显示个别目录或文件的大小外，同时也显示所有目录或文件的总和。
* `-k或--kilobytes`  以KB(1024bytes)为单位输出。
* `-m或--megabytes`  以MB为单位输出。   
* `-s或--summarize`  仅显示总计，只列出最后加总的值。
* `-h或--human-readable`  以K，M，G为单位，提高信息的可读性。
* `-x或--one-file-xystem`  以一开始处理时的文件系统为准，若遇上其它不同的文件系统目录则略过。
* `-L<符号链接>或--dereference<符号链接>` 显示选项中所指定符号链接的源文件大小。   
* `-S或--separate-dirs`   显示个别目录的大小时，并不含其子目录的大小。
* `-X<文件>或--exclude-from=<文件>`  在<文件>指定目录或文件。   
* `--exclude=<目录或文件>`         略过指定的目录或文件。    
* `-D或--dereference-args`   显示指定符号链接的源文件大小。   
* `-H或--si`  与-h参数相同，但是K，M，G是以1000为换算单位。   
* `-l或--count-links`   重复计算硬件链接的文件。  
```shell
# 对当前目前下所有文件按文件大小倒排序，大小相同按文件名字母倒排序
du -ak | sort -t$'\t' -l1 -nr -k2 -r
```
