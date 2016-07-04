category: linux
date: 2015-10-15
title: Linux /proc/cpuinfo
---

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