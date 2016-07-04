category: linux
date: 2015-07-04
title: Linux /proc/meminfo
---

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
