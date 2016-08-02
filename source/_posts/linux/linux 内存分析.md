## ps
如下例所示：
$ ps -e -o 'pid,comm,args,pcpu,rsz,vsz,stime,user,uid'  其中rsz是是实际内存
$ ps -e -o 'pid,comm,args,pcpu,rsz,vsz,stime,user,uid' | grep oracle |  sort -nrk5
其中rsz为实际内存，上例实现按内存排序，由大到小

## free
free命令可以显示Linux系统中空闲的、已用的物理内存及swap内存,及被内核使用的buffer
```bash
[root@c2xceshi ~]# free -m
             total       used       free     shared    buffers     cached
Mem:         24028      23828        199          0          8       1083
-/+ buffers/cache:      22737       1290
Swap:        12079       2514       9565
```
* 第一行, 总共物理内存24G, 
* 

http://www.cnblogs.com/peida/archive/2012/12/25/2831814.html
http://www.linuxidc.com/Linux/2011-03/33582.htm.
http://blog.itpub.net/23135684/viewspace-661824/

## top

## pmap


## sar
系统报告命令
* `sar -q 1 5`    察看cpu的load状况，每1s钟统计1次，共统计5次
* `sar -u 2 3`   察看cpu使用率，每2s统计1次，共统计3次
* `sar -r`   察看当日内存占用情况(默认每10分钟统计一次)
* `sar -b` 察看当日IO使用情况
* `sar -n SOCK`   察看网络sock连接
* `sar -n DEV` 察看网络流量

## Vmstat
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


## /proc/meminfo

```shell
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
