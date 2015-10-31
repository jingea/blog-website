category: 计算机基础
date: 2015-10-26
title: NUMA和RDMA 
---

# 第一题
```
名称			所属组织	类型	性质	开发语言	你的理解
hadoop		Apache基金会	文件	开源	java	分布式文件系统，能够以并行的方式处理超大规模的数据，并进行存储计算
storm		Twitter	计算	开源	Clojure	
spark		UC Berkeley AMP lab				
mycat					
openstack					
hive					
zookeeper					
ice					
thrift					
hazelcast					
rabbitmq					
kafka					
glusterFs					
ceph					
```
内存/文件/计算/消息/基础设施/平台

开源/商业版和社区版


# 第二题

## NUMA的CPU

英特尔系列的
* Nehalem和Tukwila系列之后的处理器
* Xeon
* 至强处理器 E5-2690 
* i3、i5、i7

HP系列的
* Superdome
* SGI的Altix 3000
 
IBM的
* p690
* x440
 
NEC
* TX7

AMD
* Opteron


目前有哪些服务器配置了这类CPU？

## Linux中关于NUMA的命令
* NUMACTL ：设定进程NUMA策略的命令行工具。
* NUMASTAT ：获取NUMA内存访问统计信息的命令行工具

开启NUMA
```
numactl --cpunodebind=0 --membind=0 myapp
```


# 第三题：
JAVA中关于NUMA的参数及其含义？

`-XX:+UseNUMA`启用Numa, 默认情况下,JVM所占内存会随机的分配到不同的NUMA节点上,当cpu运算时可能会到不同的NUMA节点的告诉缓存上进行数据查找,降低系统速度,可使用numactl工具将JVM进程绑定到特定的NUMA节点上,只让其访问自己所在节点的内存.

# 第四题：
NUMA用于MySQL调优的时候，有哪些关键配置项，需要注意什么


# 第五题：
RDMA 技术需要怎样的硬件，寻找一篇RDMA用于数据库或者Java的文章，对其性能和用法做简单的阐述。
远程直接数据存取，就是为了解决网络传输中服务器端数据处理的延迟而产生的。

# 第六题：
IO性能度量的几个指标，Linux中怎么查看这几个指标，结合当前主流的服务器的IO性能，给出Linux中磁盘达到瓶颈时的IO性能阀值（80%的极限性能）
全SSD硬盘的IO阀值
1万转机械磁盘的IO阀值
给出你的分析思路。