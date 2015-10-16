category: 工具
date: 2015-10-15
title: Linux命令使用
---

## 常用命令
### gzip

### unzip

### grep

### tar

### rm 

### cp 

### mv 


## 系统性能监控命令

### Vmstat
vmstat是Virtual Meomory Statistics（虚拟内存统计）的缩写, 是实时系统监控工具。

一般vmstat工具的使用是通过两个数字参数来完成的，第一个参数是采样的时间间隔数，单位是秒，第二个参数是采样的次数，如:
```
vmstat 2 1
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

![]()
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
 pidstat
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

### uname 

### sar 

### top  

### ps 

### df 

### du 
```
# 对当前目前下所有文件按文件大小倒排序，大小相同按文件名字母倒排序
du -ak | sort -t$'\t' -l1 -nr -k2 -r 
```

## 配置文件

### /proc/cpuinfo
```
cat /proc/cpuinfo
```
* `processor`       :
* `vendor_id`       :
* `cpu family`      :
* `model`           :
* `model name`      :
* `stepping`        :
* `cpu MHz`         :
* `cache size`      :
* `physical id`     :
* `siblings`        :
* `core id`         :
* `cpu cores`       :
* `apicid`          :
* `initial apicid`  :
* `fpu`             :
* `fpu_exception`   :
* `cpuid level`     :
* `wp`              :
* `flags`           :
* `bogomips`        :
* `clflush size`    :
* `cache_alignment` :
* `address sizes`   :
* `power management`:

### /proc/meminfo 
```
cat /proc/meminfo 
```
* `MemTotal`:           
* `MemFree`:            
* `Buffers`:            
* `Cached`:             
* `SwapCached`:         
* `Active`:             
* `Inactive`:           
* `Active(anon)`:       
* `Inactive(anon)`:     
* `Active(file)`:       
* `Inactive(file)`:     
* `Unevictable`:        
* `Mlocked`:            
* `SwapTotal`:          
* `SwapFree`:           
* `Dirty`:              
* `Writeback`:          
* `AnonPages`:          
* `Mapped`:             
* `Shmem`:              
* `Slab`:               
* `SReclaimable`:       
* `SUnreclaim`:         
* `KernelStack`:        
* `PageTables`:         
* `NFS_Unstable`:       
* `Bounce`:             
* `WritebackTmp`:       
* `CommitLimit`:        
* `Committed_AS`:       
* `VmallocTotal`:       
* `VmallocUsed`:        
* `VmallocChunk`:       
* `HardwareCorrupted`:  
* `AnonHugePages`:      
* `HugePages_Total`:    
* `HugePages_Free`:     
* `HugePages_Rsvd`:     
* `HugePages_Surp`:     
* `Hugepagesize`:       
* `DirectMap4k`:        
* `DirectMap2M`:        
* `DirectMap1G`:        

### /proc/net/dev
> Inter                                                     
* `face`:
 
> Receive
* `bytes`:    
* `packets`: 
* `errs`: 
* `drop`: 
* `fifo`: 
* `frame`: 
* `compressed`: 
* `multicast`:

> Transmit
* `bytes`:    
* `packets`: 
* `errs`: 
* `drop`: 
* `fifo`: 
* `colls`: 
* `carrier`: 
* `compressed`:

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
