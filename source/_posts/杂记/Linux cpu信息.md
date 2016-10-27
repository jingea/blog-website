category: 杂记
date: 2015-10-15
title: Linux io相关
---

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

## /proc/cpuinfo
```shell
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
