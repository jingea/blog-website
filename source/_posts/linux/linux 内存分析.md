## ps
��������ʾ��
$ ps -e -o 'pid,comm,args,pcpu,rsz,vsz,stime,user,uid'  ����rsz����ʵ���ڴ�
$ ps -e -o 'pid,comm,args,pcpu,rsz,vsz,stime,user,uid' | grep oracle |  sort -nrk5
����rszΪʵ���ڴ棬����ʵ�ְ��ڴ������ɴ�С

## free
free���������ʾLinuxϵͳ�п��еġ����õ������ڴ漰swap�ڴ�,�����ں�ʹ�õ�buffer
```bash
[root@c2xceshi ~]# free -m
             total       used       free     shared    buffers     cached
Mem:         24028      23828        199          0          8       1083
-/+ buffers/cache:      22737       1290
Swap:        12079       2514       9565
```
* ��һ��, �ܹ������ڴ�24G, 
* 

http://www.cnblogs.com/peida/archive/2012/12/25/2831814.html
http://www.linuxidc.com/Linux/2011-03/33582.htm.
http://blog.itpub.net/23135684/viewspace-661824/

## top

## pmap


## sar
ϵͳ��������
* `sar -q 1 5`    �쿴cpu��load״����ÿ1s��ͳ��1�Σ���ͳ��5��
* `sar -u 2 3`   �쿴cpuʹ���ʣ�ÿ2sͳ��1�Σ���ͳ��3��
* `sar -r`   �쿴�����ڴ�ռ�����(Ĭ��ÿ10����ͳ��һ��)
* `sar -b` �쿴����IOʹ�����
* `sar -n SOCK`   �쿴����sock����
* `sar -n DEV` �쿴��������

## Vmstat
vmstat��Virtual Meomory Statistics�������ڴ�ͳ�ƣ�����д, ��ʵʱϵͳ��ع��ߡ�

һ��vmstat���ߵ�ʹ����ͨ���������ֲ�������ɵģ���һ�������ǲ�����ʱ����������λ���룬�ڶ��������ǲ����Ĵ�������:
```shell
[root@cvs /]# vmstat 2 1
=>
procs  -----------memory----------     ---swap--  -----io----  --system--   -----cpu-----
 r  b    swpd   free   buff  cache      si   so     bi    bo    in   cs     us sy id wa st
 1  0  1339624 525012 464316 6796908    0    0      5    32     0    0      2  0 98  0  0
```
����
* -a����ʾ��Ծ�ͷǻ�Ծ�ڴ�
* -f����ʾ��ϵͳ���������fork���� ��
* -m����ʾslabinfo
* -n��ֻ�ڿ�ʼʱ��ʾһ�θ��ֶ����ơ�
* -s����ʾ�ڴ����ͳ����Ϣ������ϵͳ�������
* delay��ˢ��ʱ�����������ָ����ֻ��ʾһ�������
* count��ˢ�´����������ָ��ˢ�´�������ָ����ˢ��ʱ��������ʱˢ�´���Ϊ���
* -d����ʾ�������ͳ����Ϣ��
* -p����ʾָ�����̷���ͳ����Ϣ
* -S��ʹ��ָ����λ��ʾ�������� k ��K ��m ��M ���ֱ����1000��1024��1000000��1048576�ֽڣ�byte����Ĭ�ϵ�λΪK��1024 bytes��
* -V����ʾvmstat�汾��Ϣ��

![](https://raw.githubusercontent.com/ming15/blog-website/images/other/%E7%BA%BF%E7%A8%8B%E7%8A%B6%E6%80%81.gif)
���Ǵ������Ǹ�ͼ�����͸�������
> porcs
* r����ʾ���ж���(���䵽CPU������)
* b������״̬�Ľ�����

> memory
* swpd�������ڴ���ʹ�õĴ�С���������0����ʾ��Ļ��������ڴ治����
* free�����е������ڴ�Ĵ�С
* buff����Ϊbufferʹ�õ��ڴ�������Linux/Unixϵͳ�������洢��Ŀ¼������ʲô���ݣ�Ȩ�޵ȵĻ��棩
* cache����Ϊ����ʹ�õ��ڴ������� cacheֱ�������������Ǵ򿪵��ļ�,���ļ������壩
* inact���ǻ�Ծ�ڴ���
* active�� ��Ծ�ڴ���

> swap
* si���Ӵ��̽������ڴ�����ÿ��Ӵ��̶��������ڴ�Ĵ�С��������ֵ����0����ʾ�����ڴ治���û����ڴ�й¶�ˣ�
* so������̽������ڴ�����ÿ�������ڴ�д����̵Ĵ�С��������ֵ����0��ͬ�ϣ�

> io
* bi���������豸���ܵ��Ŀ�����������
* bo���������豸���͵Ŀ�����������

> system
* in��ÿ��CPU���жϴ���������ʱ���ж�
* cs��ÿ���������л��������������ǵ���ϵͳ��������Ҫ�����������л����̵߳��л���ҲҪ�����������л������ֵҪԽСԽ�ã�̫���ˣ�Ҫ���ǵ����̻߳��߽��̵���Ŀ

> cpu
* us���û�CPUʱ��
* sy��ϵͳCPUʱ��
* id������ CPUʱ��
* wa���ȴ�IO CPUʱ��
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
* `MemFree`:�����ڴ�
* `Buffers`:���ļ��Ļ����С
* `Cached`: ���ٻ���洢��(http://baike.baidu.com/view/496990.htm)ʹ�õĴ�С
* `SwapCached`: �����ٻ���洢�õĽ����ռ��С
* `Active`: ��Ծʹ���еĸ��ٻ���洢��ҳ���ļ���С
* `Inactive`: ������ʹ�õĸ��ٻ���洢��ҳ���ļ���С
* `Active(anon)`:
* `Inactive(anon)`:
* `Active(file)`:
* `Inactive(file)`:
* `Unevictable`:
* `Mlocked`:
* `SwapTotal`:�����ռ��ܴ�С
* `SwapFree`: ���н����ռ�
* `Dirty`:�ȴ���д�ص����̵Ĵ�С
* `Writeback`:���ڱ�д�صĴ�С
* `AnonPages`:δӳ���ҳ�Ĵ�С
* `Mapped`: �豸���ļ�ӳ��Ĵ�С
* `Shmem`:
* `Slab`: �ں����ݽṹ����Ĵ�С���ɼ���������ͷ��ڴ����������
* `SReclaimable`: ���ջ�slab�Ĵ�С
* `SUnreclaim`: �����ջص�slab�Ĵ�С23204+14164=37368
* `KernelStack`:
* `PageTables`: �����ڴ��ҳ��������Ĵ�С
* `NFS_Unstable`: ���ȶ�ҳ��Ĵ�С
* `Bounce`: bounce:�˻�
* `WritebackTmp`:
* `CommitLimit`:
* `Committed_AS`:
* `VmallocTotal`: �����ڴ��С
* `VmallocUsed`:�Ѿ���ʹ�õ������ڴ��С
* `VmallocChunk`:
* `HardwareCorrupted`:
* `AnonHugePages`:
* `HugePages_Total`:��ҳ��ķ���
* `HugePages_Free`:
* `HugePages_Rsvd`:
* `HugePages_Surp`:
* `Hugepagesize`:
* `DirectMap4k`:
* `DirectMap2M`:
* `DirectMap1G`:
