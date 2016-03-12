category: linux
date: 2015-10-15
title: Linux系统文件
---

### /proc/cpuinfo
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

### /proc/meminfo
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

### /proc/net/dev
```shell
[root@cvs /]# cat /proc/net/dev
Inter-|   Receive                                                |  Transmit
 face |bytes    packets errs drop fifo frame compressed multicast|bytes    packets errs drop fifo colls carrier compressed
    lo:365528559149 865504543    0    0    0     0          0         0 365528559149 865504543    0    0    0     0       0          0
   em1:542483270223 575346473    0    0    0    62          0   8267561 580200919340 586706511    0    0    0     0       0          0
   em2:       0       0    0    0    0     0          0         0        0       0    0    0    0     0       0          0
   em3:       0       0    0    0    0     0          0         0        0       0    0    0    0     0       0          0
   em4:       0       0    0    0    0     0          0         0        0       0    0    0    0     0       0          0
```

Inter                                                     
* `face`:接口的名字

Receive
* `bytes`: 收发的字节数   
* `packets`: 收发正确的包量
* `errs`: 收发错误的包量
* `drop`: 收发丢弃的包量
* `fifo`:
* `frame`:
* `compressed`:
* `multicast`:

Transmit
* `bytes`: 收发的字节数   
* `packets`: 收发正确的包量
* `errs`: 收发错误的包量
* `drop`: 收发丢弃的包量
* `fifo`:
* `colls`:
* `carrier`:
* `compressed`:
