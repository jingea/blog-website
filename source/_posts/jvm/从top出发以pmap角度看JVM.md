category: JVM
date: 2016-08-25
title: 从top出发以pmap角度看JVM
---

首先我写了一个简单的测试程序
```java
import com.alibaba.fastjson.JSONObject;
import java.util.concurrent.TimeUnit;
public class Test {
	public static void main(String[] args) throws InterruptedException {
		JSONObject json = new JSONObject();
		TimeUnit.HOURS.sleep(1);
	}
}
```
然后在centos编译它
```bash
javac -cp .:./* Test.java 
```
然后我运行俩次这个Java程序
```bash
[root@c2xceshi wangming]# java -cp .:./* Test &
[1] 7920
[root@c2xceshi wangming]# java -cp .:./* Test &
[2] 7935
```
然后执行一下top命令
```bash
[root@c2xceshi wangming]# top -p 7920,7935 
top - 20:11:17 up 41 days, 22:18,  4 users,  load average: 0.01, 0.01, 0.01
Tasks:   2 total,   0 running,   2 sleeping,   0 stopped,   0 zombie
Cpu(s): 10.8%us,  3.7%sy,  0.0%ni, 83.7%id,  0.8%wa,  0.0%hi,  1.1%si,  0.0%st
Mem:  24604788k total,  9408816k used, 15195972k free,   198684k buffers
Swap: 12369912k total,  3386316k used,  8983596k free,  1593972k cached

  PID USER      PR  NI  VIRT  RES  SHR S %CPU %MEM    TIME+  COMMAND                                                                                                                                                                         
 7920 root      20   0 8565m  27m  10m S  0.0  0.1   0:00.13 java                                                                                                                                                                            
 7935 root      20   0 8565m  27m  10m S  0.0  0.1   0:00.12 java 
```

好执行到这呢, 我们就看一下`pmap`执行的结果, `pmap 7920`
> pmap用于查看进程的内存映像信息. 
```bash
7920:   java -cp .:./* Test
0000000000400000      4K r-x--  /home/jdk1.8/bin/java
0000000000600000      4K rw---  /home/jdk1.8/bin/java
00000000023da000    132K rw---    [ anon ]
0000000648800000 257024K rw---    [ anon ]
0000000658300000 3844608K -----    [ anon ]
0000000742d80000 128000K rw---    [ anon ]
000000074aa80000 1922560K -----    [ anon ]
00000007c0000000    512K rw---    [ anon ]
00000007c0080000 1048064K -----    [ anon ]
00000038b1a00000    128K r-x--  /lib64/ld-2.12.so
00000038b1c1f000      4K r----  /lib64/ld-2.12.so
00000038b1c20000      4K rw---  /lib64/ld-2.12.so
00000038b1c21000      4K rw---    [ anon ]
00000038b1e00000   1576K r-x--  /lib64/libc-2.12.so
00000038b1f8a000   2048K -----  /lib64/libc-2.12.so
00000038b218a000     16K r----  /lib64/libc-2.12.so
00000038b218e000      4K rw---  /lib64/libc-2.12.so
00000038b218f000     20K rw---    [ anon ]
00000038b2200000      8K r-x--  /lib64/libdl-2.12.so
00000038b2202000   2048K -----  /lib64/libdl-2.12.so
00000038b2402000      4K r----  /lib64/libdl-2.12.so
00000038b2403000      4K rw---  /lib64/libdl-2.12.so
00000038b2600000     92K r-x--  /lib64/libpthread-2.12.so
00000038b2617000   2048K -----  /lib64/libpthread-2.12.so
00000038b2817000      4K r----  /lib64/libpthread-2.12.so
00000038b2818000      4K rw---  /lib64/libpthread-2.12.so
00000038b2819000     16K rw---    [ anon ]
00000038b2a00000     28K r-x--  /lib64/librt-2.12.so
00000038b2a07000   2044K -----  /lib64/librt-2.12.so
00000038b2c06000      4K r----  /lib64/librt-2.12.so
00000038b2c07000      4K rw---  /lib64/librt-2.12.so
00000038b2e00000    524K r-x--  /lib64/libm-2.12.so
00000038b2e83000   2044K -----  /lib64/libm-2.12.so
00000038b3082000      4K r----  /lib64/libm-2.12.so
00000038b3083000      4K rw---  /lib64/libm-2.12.so
00007f2290000000    132K rw---    [ anon ]
00007f2290021000  65404K -----    [ anon ]
...
00007f22a004e000  65224K -----    [ anon ]
00007f22a616f000  96836K r----  /usr/lib/locale/locale-archive
00007f22ac000000    132K rw---    [ anon ]
...
00007f22e02b5000     12K -----    [ anon ]
00007f22e04b7000      4K -----    [ anon ]
00007f22e04b8000  17420K rw---    [ anon ]
00007f22e15bb000   1888K r--s-  /home/jdk1.8/jre/lib/rt.jar
00007f22e1793000   5348K rw---    [ anon ]
00007f22e1ccc000   3840K -----    [ anon ]
...
00007f22e479c000   2496K rwx--    [ anon ]
00007f22e4a0c000 243264K -----    [ anon ]
00007f22f379c000    104K r-x--  /home/jdk1.8/jre/lib/amd64/libzip.so
00007f22f37b6000   2048K -----  /home/jdk1.8/jre/lib/amd64/libzip.so
00007f22f39b6000      4K rw---  /home/jdk1.8/jre/lib/amd64/libzip.so
00007f22f39b7000     48K r-x--  /lib64/libnss_files-2.12.so
00007f22f39c3000   2048K -----  /lib64/libnss_files-2.12.so
00007f22f3bc3000      4K r----  /lib64/libnss_files-2.12.so
00007f22f3bc4000      4K rw---  /lib64/libnss_files-2.12.so
00007f22f3bc5000    168K r-x--  /home/jdk1.8/jre/lib/amd64/libjava.so
00007f22f3bef000   2048K -----  /home/jdk1.8/jre/lib/amd64/libjava.so
00007f22f3def000      8K rw---  /home/jdk1.8/jre/lib/amd64/libjava.so
00007f22f3df1000     52K r-x--  /home/jdk1.8/jre/lib/amd64/libverify.so
00007f22f3dfe000   2048K -----  /home/jdk1.8/jre/lib/amd64/libverify.so
00007f22f3ffe000      8K rw---  /home/jdk1.8/jre/lib/amd64/libverify.so
00007f22f4000000   1136K rw---    [ anon ]
00007f22f411c000  64400K -----    [ anon ]
00007f22f804a000    472K rw---    [ anon ]
00007f22f80c0000     12K -----    [ anon ]
00007f22f80c3000   1016K rw---    [ anon ]
00007f22f81c1000  13048K r-x--  /home/jdk1.8/jre/lib/amd64/server/libjvm.so
00007f22f8e7f000   2044K -----  /home/jdk1.8/jre/lib/amd64/server/libjvm.so
00007f22f907e000    864K rw---  /home/jdk1.8/jre/lib/amd64/server/libjvm.so
00007f22f9156000    288K rw---    [ anon ]
00007f22f919e000     84K r-x--  /home/jdk1.8/lib/amd64/jli/libjli.so
00007f22f91b3000   2048K -----  /home/jdk1.8/lib/amd64/jli/libjli.so
00007f22f93b3000      4K rw---  /home/jdk1.8/lib/amd64/jli/libjli.so
00007f22f93b4000      4K rw---    [ anon ]
00007f22f93b6000     20K r--s-  /home/wangming/fastjson-1.2.11.jar
00007f22f93bb000     32K rw-s-  /tmp/hsperfdata_root/7920
00007f22f93c3000      4K rw---    [ anon ]
00007f22f93c4000      4K r----    [ anon ]
00007f22f93c5000      4K rw---    [ anon ]
00007fff87f51000     84K rw---    [ stack ]
00007fff87fff000      4K r-x--    [ anon ]
ffffffffff600000      4K r-x--    [ anon ]
 total          8771308K
```
为了简便可读性性, 我将中间的那部分用`...`代替了.

然后就来分析一下上面的内容
`0000000000400000      4K r-x--  /home/jdk1.8/bin/java` 每一行都分为四个部分
* 0000000000400000    虚拟内存地址
* 4K					大小
* r-x--					权限	
* /home/jdk1.8/bin/java		源码地址()

然后我们再一行一行来看

第一行`0000000000400000      4K r-x--  /home/jdk1.8/bin/java`, 这个是当我们运行Java程序(执行java命令时生成的). 它所做的就是将JVM代码的共享库加载进内存.

接下来我们就看到了好几行映射地址为`anon`的, 这些事被Java堆和内部数据所占用的. 我测试使用的是Hotspot虚拟机, 它会将heap切割成好几部分, 每个部分都会有它自己的内存块. 需要注意的是JVM会根据`Xmx`的值来分配虚拟内存, 这样子JVM就会有一块连续的内存空间了. 



我使用Beyond Compare 对比了一下7920和7935这俩个进程pmap出来的内容, 发现
```bash
0000000000400000      4K r-x--  /home/jdk1.8/bin/java
0000000000600000      4K rw---  /home/jdk1.8/bin/java
00000000023da000    132K rw---    [ anon ]
0000000648800000 257024K rw---    [ anon ]
0000000658300000 3844608K -----    [ anon ]
0000000742d80000 128000K rw---    [ anon ]
000000074aa80000 1922560K -----    [ anon ]
00000007c0000000    512K rw---    [ anon ]
00000007c0080000 1048064K -----    [ anon ]
00000038b1a00000    128K r-x--  /lib64/ld-2.12.so
00000038b1c1f000      4K r----  /lib64/ld-2.12.so
00000038b1c20000      4K rw---  /lib64/ld-2.12.so
00000038b1c21000      4K rw---    [ anon ]
00000038b1e00000   1576K r-x--  /lib64/libc-2.12.so
00000038b1f8a000   2048K -----  /lib64/libc-2.12.so
00000038b218a000     16K r----  /lib64/libc-2.12.so
00000038b218e000      4K rw---  /lib64/libc-2.12.so
00000038b218f000     20K rw---    [ anon ]
00000038b2200000      8K r-x--  /lib64/libdl-2.12.so
00000038b2202000   2048K -----  /lib64/libdl-2.12.so
00000038b2402000      4K r----  /lib64/libdl-2.12.so
00000038b2403000      4K rw---  /lib64/libdl-2.12.so
00000038b2600000     92K r-x--  /lib64/libpthread-2.12.so
00000038b2617000   2048K -----  /lib64/libpthread-2.12.so
00000038b2817000      4K r----  /lib64/libpthread-2.12.so
00000038b2818000      4K rw---  /lib64/libpthread-2.12.so
00000038b2819000     16K rw---    [ anon ]
00000038b2a00000     28K r-x--  /lib64/librt-2.12.so
00000038b2a07000   2044K -----  /lib64/librt-2.12.so
00000038b2c06000      4K r----  /lib64/librt-2.12.so
00000038b2c07000      4K rw---  /lib64/librt-2.12.so
00000038b2e00000    524K r-x--  /lib64/libm-2.12.so
00000038b2e83000   2044K -----  /lib64/libm-2.12.so
00000038b3082000      4K r----  /lib64/libm-2.12.so
00000038b3083000      4K rw---  /lib64/libm-2.12.so
```
只有`00000000023da000    132K rw---    [ anon ]`这一行的内存地址不一样, 其他的都是一样的



参考
(Virtual Memory Usage from Java under Linux, too much memory used)[http://stackoverflow.com/questions/561245/virtual-memory-usage-from-java-under-linux-too-much-memory-used]
