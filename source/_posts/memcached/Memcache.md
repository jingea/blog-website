category: memcached
date: 2015-11-18
title: memcached
---
## 原理
memcached是一个高性能内存对象缓存系统. 它基于libevent,可方便地拓展为任意大小, 而且对防止内存swap和使用非阻塞IO做了大量优化工作.

memcached内存分配：

## 适用场景
* 最适合存储小数据，并且存储的数据是大小一致的
* Memcached在很多时候都是作为数据库前端cache使用
* 虚机上不适合部署Memcached
* 确保Memcached的内存不会被Swap出去
* 不能便利所有数据，这将导致严重性能问题
* Local Cache+ Memcached这种分层Cache还是很有价值的
* Memcached启动预热是一个好办法

	   
## 安装
Memcached依赖libevent,所以我们首先需要安装libevent
```
wget http://jaist.dl.sourceforge.net/project/levent/libevent/libevent-2.0/libevent-2.0.22-stable.tar.gz
tar -zxvf libevent-2.0.22-stable.tar.gz
cd libevent-2.0.22-stable
./configure --prefix=/usr && make && make install
```
接下来安装Memcached
```
wget http://memcached.org/latest
tar -zxvf memcached-1.x.x.tar.gz
cd memcached-1.x.x
./configure --with-libevent=/usr && make && make test && sudo make install
```

## 启动
`memcached`命令选项 
* `-s <file>` : Unix socket path to listen on (disables network support).
* `-a <perms>` : 当通过s选项创建socket的时候,我们可以通过-a选项指定创建socket使用的权限(权限为八进制).
* `-l <ip_addr>` : 监听的主机地址. 默认是本机任何可用的地址. 
* `-d` : 以后台进程方式运行memcached
* `-u <username>` : 当memcached是以root用户启动时，我们需要通过该参数指定用户(被指定的用户必须存在)
* `-c <num>` : 设置最大同时连接数.(默认是1024).
* `-C` : 关闭CAS. (每个对象都会减少8bytes大小).
* `-k` : 锁定所有的分页内存. 在巨大的缓存系统中,使用这个选项是非常危险的,使用的使用要参考README文件和memcached homepage进行配置.
* `-p <num>` : 设置监听TCP端口号, 默认是11211.
* `-P` : 设置pid存储文件.
* `-U <num>` : 设置监听UDP端口号, 默认是11211, 0 表示关闭UDP监听.
* `-m <num>` : 设置对象存储能使用的最大内存(单位是MB,默认是64M)
* `-M` : 关闭对象存储所需内存超过最大内存时,自动删除缓存对象的功能. 如果memcached的配置内存达到最大值就不可再存储新的对象.
* `-r` : 将最大的核心文件大小限制提升到允许的最大值.
* `-v` : 设置为verbose 同时会输出发生的errors 和warnings.
* `-R <num>` : This  option  seeks  to prevent client starvation by setting a limit to the number of sequential requests the server will process from an individual client connection. Once a connection has exceeded this value, the server will attempt to process I/O on other connections before handling any further request from this connection. The default value for this option is 20.
* `-f <factor>` : Use <factor>  as the multiplier for computing the sizes of memory chunks that items are stored in. A lower value may result in less wasted memory depending on the total amount of memory available and the distribution of item sizes.  The default is 1.25.
* `-n <size>` : Allocate  a  minimum  of <size>  bytes for the item key, value, and flags. The default is 48. If you have a lot of small keys and values, you can get a significant memory efficiency gain with a lower value. If you use a high chunk growth factor (-f option), on the other hand, you may want to increase the size to allow a bigger percentage of your items to fit in the most densely packed (smallest) chunks.
* `-i`     Print memcached and libevent licenses.
* `-P <filename>` : Print pidfile to <filename>, only used under -d option.
* `-t <threads>` : Number  of  threads  to use to process incoming requests. This option is only meaningful if memcached was compiled with thread support enabled. It is typically not useful to set this higher than the number of CPU cores on the memcached server. The default is 4.
* `-D <char>` : Use <char> as the delimiter between key prefixes and IDs. This is used for per-prefix stats reporting. The default is ":" (colon). If this option is specified, stats collection is turned on automat- ically; if not, then it may be turned on by sending the "stats detail on" command to the server.
* `-L` : Try  to  use  large memory pages (if available). Increasing the memory page size could reduce the number of TLB misses and improve the performance. In order to get large pages from the OS, memcached will allocate the total item-cache in one large chunk. Only available if supported on your OS.
* `-B <proto>` : Specify the binding protocol to use.  By default, the server will autonegotiate client connections.  By using this option, you can specify the protocol clients  must  speak.   Possible  options  are "auto" (the default, autonegotiation behavior), "ascii" and "binary".
* `-I <size>` : Override  the default size of each slab page. Default is 1mb. Default is 1m, minimum is 1k, max is 128m. Adjusting this value changes the item size limit.  Beware that this also increases the number of slabs (use -v to view), and the overal memory usage of memcached.
* `-F` : Disables the "flush_all" command. The cmd_flush counter will increment, but clients will receive an error message and the flush will not occur.
* `-o <options>` : Comma separated list of extended or experimental options. See -h or wiki for up to date list.

```
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
* `STAT libevent 2.0.22-stable`
* `STAT pointer_size 64` 当前操作系统的指针大小（32位系统一般是32bit）
* `STAT rusage_user 0.002999`
* `STAT rusage_system 0.001999`
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
* `STAT delete_misses 0`
* `STAT delete_hits 0`
* `STAT incr_misses 0`
* `STAT incr_hits 0`
* `STAT decr_misses 0`
* `STAT decr_hits 0`
* `STAT cas_misses 0`
* `STAT cas_hits 0`
* `STAT cas_badval 0`
* `STAT touch_hits 0`
* `STAT touch_misses 0`
* `STAT auth_cmds 0`
* `STAT auth_errors 0`
* `STAT bytes_read 7` 总读取字节数（请求字节数） 
* `STAT bytes_written 0` 总发送字节数（结果字节数） 
* `STAT limit_maxbytes 67108864`   分配给memcache的内存大小（字节）
* `STAT accepting_conns 1`
* `STAT listen_disabled_num 0`
* `STAT threads 4`     当前线程数 
* `STAT conn_yields 0`
* `STAT hash_power_level 16`
* `STAT hash_bytes 524288`
* `STAT hash_is_expanding 0`
* `STAT malloc_fails 0`
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