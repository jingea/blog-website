category: linux
date: 2016-08-02
title: linux 内存分析
---
和[linux 网络监控一探]()一样, 今天的目的是要找出要查看进程的所占用的物理内存, 虚拟内存和缓存等

## ps
ps命令是Process Status的缩写。ps命令用来列出系统中当前运行的那些进程. 我们看一下它的帮助手册
```bash
ERROR: Unsupported SysV option.
********* simple selection *********  ********* selection by list *********
-A all processes                      -C by command name
-N negate selection                   -G by real group ID (supports names)
-a all w/ tty except session leaders  -U by real user ID (supports names)
-d all except session leaders         -g by session OR by effective group name
-e all processes                      -p by process ID
T  all processes on this terminal     -s processes in the sessions given
a  all w/ tty, including other users  -t by tty
g  OBSOLETE -- DO NOT USE             -u by effective user ID (supports names)
r  only running processes             U  processes for specified users
x  processes w/o controlling ttys     t  by tty
*********** output format **********  *********** long options ***********
-o,o user-defined  -f full            --Group --User --pid --cols --ppid
-j,j job control   s  signal          --group --user --sid --rows --info
-O,O preloaded -o  v  virtual memory  --cumulative --format --deselect
-l,l long          u  user-oriented   --sort --tty --forest --version
-F   extra full    X  registers       --heading --no-heading --context
                    ********* misc options *********
-V,V  show version      L  list format codes  f  ASCII art forest
-m,m,-L,-T,H  threads   S  children in sum    -y change -l format
-M,Z  security data     c  true command name  -c scheduling class
-w,w  wide output       n  numeric WCHAN,UID  -H process hierarchy
```



## free
free命令可以显示Linux系统中空闲的、已用的物理内存及swap内存,及被内核使用的buffer
```bash
[root@c2xceshi ~]# free -m
             total       used       free     shared    buffers     cached
Mem:         24028      23828        199          0          8       1083
-/+ buffers/cache:      22737       1290
Swap:        12079       2514       9565
```
* 第一行, 总共物理内存24G, 物理内存已经使用了23.8个G, 剩余199M. 其中buffer占用8M, cached占用了1083M.
* 第二行, 刨去系统缓存, 使用的内存和剩余内存. 刨去buffer和cached,应用程序实际上使用了22个G. 还有1290M的内存是可被应用程序使用的
* 第三行, 交换区的内存使用情况. 交换区总共12个G, 使用了2.5个G,还剩余9.5个G. 

上面涉及了shared，buffers，cached，Swep等四个概念。我们依次看一下

### Swap Space
Linux支持虚拟内存（virtual memory），虚拟内存是指使用磁盘当作RAM的扩展，这样可用的内存的大小就相应地增大了。内核会将暂时不用的内存块的内容写到硬盘上，这样一来，这块内存就可用于其它目的。当需要用到原始的内容时，它们被重新读入内存。这些操作对用户来说是完全透明的；Linux下运行的程序只是看到有大量的内存可供使用而并没有注意到时不时它们的一部分是驻留在硬盘上的。当然，读写硬盘要比直接使用真实内存慢得多（要慢数千倍），所以程序就不会象一直在内存中运行的那样快。用作虚拟内存的硬盘部分被称为交换空间（Swap Space）。
一般，在交换空间中的页面首先被换入内存；如果此时没有足够的物理内存来容纳它们又将被交换出来（到其他的交换空间中）。如果没有足够的虚拟内存来容纳所有这些页面，Linux就会波动而不正常；但经过一段较长的时间Linux会恢复，但此时系统已不可用了。
有时，尽管有许多的空闲内存，仍然会有许多的交换空间正被使用。这种情况是有可能发生的，例如如果在某一时刻有进行交换的必要，但后来一个占用很多物理内存的大进程结束并释放内存时。被交换出的数据并不会自动地交换进内存，除非有这个需要时。此时物理内存会在一段时间内保持空闲状态。对此并没有什么可担心的，但是知道了是怎么一回事，也就无所谓了。
许多操作系统使用了虚拟内存的方法。因为它们仅在运行时才需要交换空间，以解决不会在同一时间使用交换空间，因此，除了当前正在运行的操作系统的交换空间，其它的就是一种浪费。所以让它们共享一个交换空间将会更有效率。
注意，如果会有几个人同时使用这个系统，他们都将消耗内存。然而，如果两个人同时运行一个程序，内存消耗的总量并不是翻倍，因为代码页以及共享的库只存在一份。
Linux系统常常动不动就使用交换空间，以保持尽可能多的空闲物理内存。即使并没有什么事情需要内存，Linux也会交换出暂时不用的内存页面。这可以避免等待交换所需的时间：当磁盘闲着，就可以提前做好交换。
可以将交换空间分散在几个硬盘之上。针对相关磁盘的速度以及对磁盘的访问模式，这样做可以提高性能。
与访问（真正的）的内存相比，磁盘的读写是很慢的。另外，在相应较短的时间内多次读磁盘同样的部分也是常有的事。例如，某人也许首先阅读了一段e-mail消息，然后为了答复又将这段消息读入编辑器中，然后又在将这个消息拷贝到文件夹中时，使得邮件程序又一次读入它。或者考虑一下在一个有着许多用户的系统中 ls命令会被使用多少次。通过将信息从磁盘上仅读入一次并将其存于内存中，除了第一次读以外，可以加快所有其它读的速度。这叫作磁盘缓冲（disk buffering），被用作此目的的内存称为高速缓冲（Buffer Cache）。
但是，由于内存是一种有限而又不充足的资源，高速缓冲不可能做的很大（它不可能包容要用到的所有数据）。当缓冲充满了数据时，其中最长时间不用的数据将被舍弃以腾出内存空间用于新的数据。
对写磁盘操作来说磁盘缓冲技术同样有效。一方面，被写入磁盘的数据常常会很快地又被读出（例如，原代码文件被保存到一个文件中，又被编译器读入），所以将要被写的数据放入缓冲中是个好主意。另一方面，通过将数据放入缓冲中，而不是将其立刻写入磁盘，程序可以加快运行的速度。以后，写的操作可以在后台完成，而不会拖延程序的执行。
大多数操作系统都有高速缓冲（尽管可能称呼不同），但是并不是都遵守上面的原理。有些是直接写（write-through）：数据将被立刻写入磁盘（当然，数据也被放入缓存中）。如果写操作是在以后做的，那么该缓存被称为后台写（write-back）。后台写比直接写更有效，但也容易出错：如果机器崩溃，或者突然掉电，缓冲中改变过的数据就被丢失了。如果仍未被写入的数据含有重要的薄记信息，这甚至可能意味着文件系统（如果有的话）已不完整。
针对以上的原因，出现了很多的日志文件系统，数据在缓冲区修改后，同时会被文件系统记录修改信息，这样即使此时系统掉电，系统重启后会首先从日志记录中恢复数据，保证数据不丢失。当然这些问题不再本文的叙述范围。
由于上述原因，在使用适当的关闭过程之前，绝对不要关掉电源，sync命令倾空（flushes）缓冲，也即，强迫所有未被写的数据写入磁盘，可用以确定所有的写操作都已完成。在传统的UNIX系统中，有一个叫做update的程序运行于后台，每隔30秒做一次sync操作，因此通常无需手工使用sync命令了。Linux另外有一个后台程序，bdflush，这个程序执行更频繁的但不是全面的同步操作，以避免有时sync的大量磁盘I/O操作所带来的磁盘的突然冻结。
在Linux中，bdflush是由update启动的。通常没有理由来担心此事，但如果由于某些原因bdflush进程死掉了，内核会对此作出警告，此时你就要手工地启动它了（/sbin/update）。
缓存（cache）实际并不是缓冲文件的，而是缓冲块的，块是磁盘I/O操作的最小单元（在Linux中，它们通常是1KB）。这样，目录、超级块、其它文件系统的薄记数据以及非文件系统的磁盘数据都可以被缓冲了。
缓冲的效力主要是由它的大小决定的。缓冲太小的话等于没用：
它只能容纳一点数据，因此在被重用时，所有缓冲的数据都将被倾空。实际的大小依赖于数据读写的频次、相同数据被访问的频率。只有用实验的方法才能知道。
如果缓存有固定的大小，那么缓存太大了也不好，因为这会使得空闲的内存太小而导致进行交换操作（这同样是慢的）。为了最有效地使用实际内存，Linux自动地使用所有空闲的内存作为高速缓冲，当程序需要更多的内存时，它也会自动地减小缓冲的大小。
这就是一般情况下Linux内存的一般机制，当然Linux内存的运行机制远远比这个复杂，但是只有了解了这个机制，我们管理服务器才能得心应手！
 


## top
```bash
Processes: 242 total, 2 running, 5 stuck, 235 sleeping, 1138 threads   02:35:51
Load Avg: 2.38, 2.40, 2.38  CPU usage: 6.9% user, 5.36% sys, 88.53% idle
SharedLibs: 155M resident, 20M data, 19M linkedit.
MemRegions: 39333 total, 1602M resident, 82M private, 525M shared.
PhysMem: 4868M used (1159M wired), 1274M unused.
VM: 653G vsize, 535M framework vsize, 5083(0) swapins, 6580(0) swapouts.
Networks: packets: 10717311/13G in, 7678812/1169M out.
Disks: 353264/11G read, 895849/24G written.

PID   COMMAND      %CPU TIME     #TH   #WQ  #PORT MEM    PURG   CMPRS  PGRP PPID
6652  top          2.1  00:00.33 1/1   0    21    2164K  0B     0B     6652 4942
6651  Preview      0.0  00:00.84 4     1    220   20M    192K   0B     6651 1
6471  ShipIt       0.0  00:00.02 2     1    43    1276K  0B     0B     6471 1
6436  crashpad_han 0.0  00:00.01 3     0    25    824K   0B     0B     6435 1
6434  Atom Helper  0.0  00:00.29 4     0    71    10M    0B     0B     6432 6432
6432  Atom         0.0  00:11.99 26    0    326   50M    0B     0B     6432 1
6333  com.apple.We 0.0  00:00.67 9     0    221   19M    0B     0B     6333 1
6332  com.apple.We 0.0  00:10.10 12    0    197   150M   22M    0B     6332 1
5142  mdworker     0.0  00:00.07 3     0    49    3116K  0B     0B     5142 1
5090  mdworker     0.0  00:00.09 3     0    49    3104K  0B     0B     5090 1
5089  mdworker     0.0  00:00.11 3     0    49    3128K  0B     0B     5089 1
5088  mdworker     0.0  00:00.08 3     0    49    3116K  0B     0B     5088 1
5087  mdworker     0.0  00:00.11 3     0    49    3052K  0B     0B     5087 1
5086  mdworker     0.0  00:00.12 3     0    49    3116K  0B     0B     5086 1
5085  mdworker     0.0  00:00.11 3     0    49    3076K  0B     0B     5085 1
```

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
上面的ps, top等工具都是从`/proc/meminfo`这个文件里获取的数据, 因此看一下这个文件
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



本文参考自
* [你值得拥有：25个Linux性能监控工具](http://os.51cto.com/art/201412/460698_all.htm)
* [10个重要的Linux ps命令实战](https://linux.cn/article-4743-1.html)
