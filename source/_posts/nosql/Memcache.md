category: NoSql
date: 2015-11-18
title: memcached
---
## 原理
memcached是一个高性能内存对象缓存系统. 它基于libevent,可方便地拓展为任意大小, 而且对防止内存swap和使用非阻塞IO做了大量优化工作.

memcached内存分配：
![](https://raw.githubusercontent.com/yu66/blog-website/images/memcached/20120314163538_438.png)
memcached默认情况下采用了名为Slab Allocator的机制分配、管理内存.

如果我们在启动memcached时没有指定`-m`参数的话, 那么memcached能使用的最大内存为默认的64M,但是memcached启动的时候并不会一次性就都分配出来,而是当发现memcached已被分配的内存不够用的时候才会进行申请. memcached申请内存时一次会申请一个Slab(默认为1M). 然后会将这一个Slab分成不同的Class, 每个Class内部都有N个大小相等的Chunk.每个chunk中都保存了一个item结构体、一对key value键值对.

## 安装
Memcached依赖libevent,所以我们首先需要安装libevent
```shell
wget http://jaist.dl.sourceforge.net/project/levent/libevent/libevent-2.0/libevent-2.0.22-stable.tar.gz
tar -zxvf libevent-2.0.22-stable.tar.gz
cd libevent-2.0.22-stable
./configure --prefix=/usr && make && make install
```
接下来安装Memcached
```shell
wget http://memcached.org/latest
tar -zxvf memcached-1.x.x.tar.gz
cd memcached-1.x.x
./configure --with-libevent=/usr && make && make test && sudo make install
```

## `memcached`命令选项

网络相关
* `-s <file>` : Unix socket path to listen on (disables network support).
* `-a <perms>` : 当通过s选项创建socket的时候,我们可以通过-a选项指定创建socket使用的权限(权限为八进制).
* `-l <ip_addr>` : 监听的主机地址. 默认是本机任何可用的地址.
* `-d` : 以后台进程方式运行memcached
* `-u <username>` : memcached不能以root用户运行，如果当前用户为root, 我们需要通过该参数指定用户为root
* `-c <num>` : 设置最大同时连接数.(默认是1024).
* `-C` : 关闭CAS. (每个对象都会减少8bytes大小).
* `-p <num>` : 设置监听TCP端口号, 默认是11211.
* `-P` : 设置pid存储文件.
* `-U <num>` : 设置监听UDP端口号, 默认是11211, 0 表示关闭UDP监听.
* `-r` : 将最大的核心文件大小限制提升到允许的最大值.
* `-v` : 设置为verbose 同时会输出发生的errors 和warnings.
* `-i` : 打印memcached 和libevent 授权.
* `-R <num>` : 这个选项是设置服务器可以处理一个独立客户端连接顺序请求的数量,以防止产生其他客户端饥饿的情况. 一旦设置了这个值当服务器处理一个连接超过20个(默认值)请求之后,就会尝试处理其他的连接请求.

内存相关
* `-m <num>` : 设置对象存储能使用的最大内存(单位是MB,默认是64M)
* `-M` : 关闭对象存储所需内存超过最大内存时,自动删除缓存对象的功能. 如果memcached的配置内存达到最大值就不可再存储新的对象.
* `-f <factor>` : Class的成长因子(默认是1.25). 也就是说如果Class1是100B,那么Class2就是125B.
* `-n <size>` : key, value, and flags分配到的最小字节数(默认是48字节). 如果你的键值对的值都很小,你可以调低这个值来达到更高的性能. 如果你的成长因子比较大,那么你可以调高这个值,提升命中率.
* `-t <threads>` : 处理请求的线程数(默认是4). 这个选项只有memcached被编译的时候指定了线程开启才有用.
* `-k` : 锁定所有的分页内存. 在巨大的缓存系统中,使用这个选项是非常危险的,使用的使用要参考README文件和memcached homepage进行配置.
* `-L` : 尝试使用尽可能使用到的内存叶. 增加内存叶大小可以减少TLB未命中和提供性能. 为了可以从OS获得更大的内存页,memcached会在一个巨大的chunk上分配所有的item
* `-I <size>` : 指定slab page大小(默认是1mb,最小是1k, 最大是128m). 改变这个值会增加每个item大小的值.  使用-vv来查看更改后的值
* `-F` : 关闭`flush_all`命令.

```shell
memcached  -d -p 10021 -l 10.234.10.12 -u root -c 1024  -P ./memcached1.pid
```

## java使用
我们使用spymemcached作为java客户端连接memcached. 在Maven项目中添加以下依赖
```xml
<groupId>net.spy</groupId>
	<artifactId>spymemcached</artifactId>
<version>2.12.0</version>
```
然后连接memcached
```java
MemcachedClient client = new MemcachedClient(new InetSocketAddress("10.234.10.12", 10021));
```
通过这一行我们就成功的连接上了memcached.然后我们就可以使用spymemcached提供的大量api来操作memcached

## memcached信息统计
我们可以使用telnet命令直接连接memcached`telnet 127.0.0.1 10021`,然后输入下列命令查看相关信息

### stats
统计memcached的各种信息
* `STAT pid 20401` memcache服务器的进程ID
* `STAT uptime 47`  服务器已经运行的秒数
* `STAT time 1447835371` 服务器当前的unix时间戳
* `STAT version 1.4.24`  memcache版本
* `STAT libevent 2.0.22-stable` libevent版本
* `STAT pointer_size 64` 当前操作系统的指针大小（32位系统一般是32bit）
* `STAT rusage_user 0.002999` 进程的累计用户时间
* `STAT rusage_system 0.001999` 进程的累计系统时间
* `STAT curr_connections 10` 当前打开着的连接数
* `STAT total_connections 11` 从服务器启动以后曾经打开过的连接数
* `STAT connection_structures 11` 服务器分配的连接构造数
* `STAT reserved_fds 20`
* `STAT cmd_get 0`  get命令（获取）总请求次数
* `STAT cmd_set 0`  set命令（保存）总请求次数
* `STAT cmd_flush 0`
* `STAT cmd_touch 0`
* `STAT get_hits 0`  总命中次数
* `STAT get_misses 0` 总未命中次数
* `STAT delete_misses 0` delete命令未命中次数
* `STAT delete_hits 0`  delete命令命中次数
* `STAT incr_misses 0`  incr命令未命中次数
* `STAT incr_hits 0`  incr命令命中次数
* `STAT decr_misses 0`  decr命令未命中次数
* `STAT decr_hits 0`  decr命令命中次数
* `STAT cas_misses 0`  cas命令未命中次数
* `STAT cas_hits 0`  cas命令命中次数
* `STAT cas_badval 0`
* `STAT touch_hits 0`  touch命令命中次数
* `STAT touch_misses 0`  touch命令未命中次数
* `STAT auth_cmds 0`
* `STAT auth_errors 0`
* `STAT bytes_read 7` 总读取字节数（请求字节数）
* `STAT bytes_written 0` 总发送字节数（结果字节数）
* `STAT limit_maxbytes 67108864`   分配给memcache的内存大小（字节）
* `STAT accepting_conns 1`
* `STAT listen_disabled_num 0`
* `STAT threads 4`     当前线程数
* `STAT conn_yields 0`
* `STAT hash_power_level 16`  hash等级
* `STAT hash_bytes 524288`  hash字节数
* `STAT hash_is_expanding 0`    
* `STAT malloc_fails 0`  分配失败次数
* `STAT bytes 0`   当前服务器存储items占用的字节数
* `STAT curr_items 0` 服务器当前存储的items数量
* `STAT total_items 0` 从服务器启动以后存储的items总数量
* `STAT expired_unfetched 0`
* `STAT evicted_unfetched 0`
* `STAT evictions 0` 为获取空闲内存而删除的items数（分配给memcache的空间用满后需
* `STAT reclaimed 0`
* `STAT crawler_reclaimed 0`
* `STAT crawler_items_checked 0`
* `STAT lrutail_reflocked 0`

我们也可以使用java获取这些信息
```java
MemcachedClient client = new MemcachedClient(new InetSocketAddress("10.234.10.12", 10021));
client.getStats().entrySet().stream().forEach(entry -> {
	System.out.println("Node : " + entry.getKey());
	entry.getValue().entrySet().stream().forEach(value -> {
		System.out.println("    " + value.getKey() + " : " + value.getValue());
	});
});
```

### stats reset
重新统计数据

### stats slabs
显示slabs信息，可以详细看到数据的分段存储情况
* `STAT active_slabs 0`
* `STAT total_malloced 0`

### stats items
显示slab中的item数目

### stats cachedump 1 0
列出slabs第一段里存的KEY值


### STAT evictions 0
表示要腾出新空间给新的item而移动的合法item数目
