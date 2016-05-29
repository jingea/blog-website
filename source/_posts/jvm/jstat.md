category: JVM
date: 2014-10-03
title: jstat
---
虚拟机统计信息监视工具

用于监视虚拟机各种运行状态信息的命令行工具.它可以显示本地或远程虚拟机进程中的类装载,内存,垃圾收集,JIT编译等运行数据.

jstat命令格式
```java
jstat -<option> [-t] [-h<lines>] <vmid> [<interval> [<count>]]
```
对于命令格式中的VMID与LVMID需要特别说明一下:如果是本地虚拟机进程,VMID和LVMID是一致的,如果是远程虚拟机进程,那么VMID的格式应该是:
```java
[protocol:] [//]lvmid[@hostname [:port] /servername]
```

option参数代表着用户希望查询的虚拟机信息,主要分为三类:类装载,垃圾收集,运行期编译状况, 可选值有:
* `-class`: 监视类装载,卸载数量,总空间及类装载所耗费的时间
* `-gc`: 监视java堆状况,包括Eden区,2个survivor区,老年代,永久代等的容量,已用空间,GC时间合计等信息.
* `-gccapacity`: 监视内容与-gc基本相同,但输出主要关注java堆各个区域使用到最大和最小空间.
* `-gcutil`: 监视内容与-gc基本相同,但输出主要关注已使用空间占总空间的百分比.
* `-gccause`: 与-gcutil功能一样,但是会额外输出导致上一次GC产生的原因.
* `-gcnew`:监视新生代GC的状况.
* `-gcnewcapacity`: 监视内容与-gcnew基本相同输出主要关注使用到的最大和最小空间
* `-gcold`: 监视老年代GC的状况.
* `-gcoldcapacity`: 监视内容与-gcold基本相同,但输出主要关注使用到的最大和最小空间
* `-gcpermcapacity`: 输出永久代使用到呃最大和最小空间
* `-compiler`: 输出JIT编译器编译过的方法,耗时等信息
* `-printcompilation`: 输出已经被JIT编译的方法.

其他参数
* `-t`: 在输出信息前添加一个时间, 表示程序运行的时间
* `-h`: 表示输出多少行后,输出一个表头信息
* `interval`: 统计数据的周期, 单位是毫秒
* `count`: 统计的次数

我们才有Spring Boot Web开启一个Http服务
```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@EnableAutoConfiguration
public class HTTPServer {

    @RequestMapping("/")
    @ResponseBody
    String home() {
        return "Hello World!";
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(HTTPServer.class, args);
    }
}
```
然后使用jstat看一下这个进程的运行
```bash
➜  test jstat -gc  -t  2028 5000 5
Timestamp        S0C    S1C    S0U    S1U      EC       EU        OC         OU       MC     MU    CCSC   CCSU   YGC     YGCT    FGC    FGCT     GCT
          200.8 4608.0 4096.0  0.0   3152.8 39936.0   8518.2   17920.0    13639.7   27520.0 27031.4 3456.0 3372.0     23    0.078   2      0.149    0.227
          205.9 4608.0 4096.0  0.0   3152.8 39936.0   8518.2   17920.0    13639.7   27520.0 27031.4 3456.0 3372.0     23    0.078   2      0.149    0.227
          210.9 4608.0 4096.0  0.0   3152.8 39936.0   8518.2   17920.0    13639.7   27520.0 27031.4 3456.0 3372.0     23    0.078   2      0.149    0.227
          215.9 4608.0 4096.0  0.0   3152.8 39936.0   8518.2   17920.0    13639.7   27520.0 27031.4 3456.0 3372.0     23    0.078   2      0.149    0.227
          220.9 4608.0 4096.0  0.0   3152.8 39936.0   8518.2   17920.0    13639.7   27520.0 27031.4 3456.0 3372.0     23    0.078   2      0.149    0.227
```
* `Timestamp` 程序运行的时间
* `S0C` survive0的大小(单位KB)
* `S1C` survive1的大小(单位KB)
* `S0U` survive0使用的大小(单位KB)
* `S1U` survive1使用的大小(单位KB)
* `EC` eden区大小(单位KB)
* `EU` eden区使用的大小(单位KB)
* `OC` 老年代大小(单位KB)
* `OU` 老年代使用的大小(单位KB)
* `MC` 元数据区大小(单位KB)
* `MU` 元数据区使用大小(单位KB)
* `CCSC`
* `CCSU`
* `YGC` 新生代GC次数
* `YGCT` 新生代GC耗时
* `FGC` FullGC次数
* `FGCT` FullGC耗时
* `GCT` GC总耗时
