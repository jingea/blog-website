category: JVM
date: 2014-09-07
title: 垃圾收集算法及垃圾回收器
---
## 垃圾回收算法

### 标记-清除算法
算法分为标记和清除俩个部分. 首先标记出要所有要回收的对象, 然后统一回收掉所有被标记的对象.
![标记-清除算法](https://raw.githubusercontent.com/ming15/blog-website/images/jvm/%E6%A0%87%E8%AE%B0%20-%20%E6%B8%85%E9%99%A4%E7%AE%97%E6%B3%95a.jpg)
![标记-清除算法](https://raw.githubusercontent.com/ming15/blog-website/images/jvm/%E6%A0%87%E8%AE%B0%20-%20%E6%B8%85%E9%99%A4%E7%AE%97%E6%B3%95b.jpg)

这种算法的缺点主要是:
1. 效率问题. 标记和清除的效率都不高.
2. 空间问题. 标记和清除之后会存在大量不连续空间碎片. 空间碎片太多可能导致,在程序以后运行过程中需要分配较大对象时,无法找到足够的内存连续内存,而不得不提前触发另一次的垃圾收集动作.

### 复制算法

为了解决效率问题, 复制算法将可用内存按照容量划分为大小相等的俩块,每次只使用其中的一块. 当这一块内存用完了,就将还活着的对象复制到另一块上面,然后再把已经使用过的内存一次清理掉.
![复制算法a](https://raw.githubusercontent.com/ming15/blog-website/images/jvm/%E5%A4%8D%E5%88%B6%E7%AE%97%E6%B3%95a.jpg)
![复制算法b](https://raw.githubusercontent.com/ming15/blog-website/images/jvm/%E5%A4%8D%E5%88%B6%E7%AE%97%E6%B3%95b.jpg)

商业虚拟机都采用这种算法来收集新生代. 由于大多数的新生代对象都是朝生夕死, 因此按照1:1 的比例分配新生代内存代价就有点高了. 因此现在的做法一般是将新生代分为一块
较大的Eden区和俩块较小的Survivor区. 每次都使用Eden和一块Survivor区,当回收时, 将Eden还活着的对象一次性拷贝到另一块Survivor区,最后清理掉Eden区和刚才使用过的Survivor区.
HotSpot默认Eden和Survivor的大小比例是8:1, 也就是每次新生代可用内存空间为90%(8 + 1), 如果发现剩余的存活对象多余10%,另一块Survivor不够的话,需要依赖其他内存(老年代)进行分配担保.

### 标记-整理算法
由于新生代的对象是朝生夕死的，因此复制算法每次复制的对象都不会太多, 但是老年代都是一些大对象, 如果在老年代仍然采用复制算法的话, 效率会变低, 因此提出了标记-整理算法. 标记过程和"标记-清除算法"一样,但后续步骤不是直接对可回收对象进行清理,而是让所有存活的对象都向一端移动,然后直接清理掉端边界以外的内存.

[标记整理算法](http://www.jianshu.com/p/698eb5e1ccb9)
![标记  - 整理算法a](https://raw.githubusercontent.com/ming15/blog-website/images/jvm/%E6%A0%87%E8%AE%B0%20%20-%20%E6%95%B4%E7%90%86%E7%AE%97%E6%B3%95a.jpg)
![标记  - 整理算法b](https://raw.githubusercontent.com/ming15/blog-website/images/jvm/%E6%A0%87%E8%AE%B0%20%20-%20%E6%95%B4%E7%90%86%E7%AE%97%E6%B3%95b.jpg)

### 分代收集算法
这种算法的思想是:把java堆分为新生代和老年代,这样就可以根据各个年代的特点采用最适当的收集算法.

在新生代,每次垃圾收集时发现有大批对象死去,只有少量对象存活,那就采用复制算法,只要付出少量存活对象的复制成本就可以完成收集. 而老年代对象存活效率高,没有额外的空间对它进行分配担保,就必须采用"标记清除"或者"标记整理"来进行回收


## 垃圾收集器
![https://blogs.oracle.com/jonthecollector/resource/Collectors.jpg](https://blogs.oracle.com/jonthecollector/resource/Collectors.jpg)

### 新生代收集器

#### Serial收集器
Serial收集器是最基本、历史最悠久的收集器,曾经（在JDK 1.3.1之前）是虚拟机`新生代`收集的唯一选择.作为单线程收集器, 它不仅仅只会使用一个CPU或一条收集线程完成GC,更重要的是在它进行GC时, 其他的工作线程(业务线程)也必须暂停工作, 也就是发生著名的Stop The World, 直到它收集结束. 这种收集器更多的是在桌面应用程序和单核的机器上能取得更好的效果,因为它比多线程收集器减少了线程交互等额外开销.

#### ParNew收集器

`ParNew`收集器是`Serial`收集器的多线程版本,除了使用多线程进行垃圾收集之外,其余行为包括Serial收集器可用的所有控制参数（例如：`-XX:SurvivorRatio`、 `-XX:PretenureSizeThreshold`、`-XX:HandlePromotionFailure`等）、收集算法、Stop The World、对象分配规则、回收策略等都与Serial收集器完全一样.

> 需要指出的是, ParNew收集器是许多运行在Server模式下的虚拟机中首选的新生代收集器,其中有一个与性能无关但很重要的原因是,除了Serial收集器外,目前只有它能与CMS收集器配合工作.

如果机器的CPU核数比较多, 我们要控制一下收集线程数的话,可以使用`-XX:ParallelGCThreads`参数来限制垃圾收集的线程数.

JVM现在的多线程垃圾收集器分为并发和并行俩种:
* 并行（Parallel）：指多条垃圾收集线程并行工作,但此时用户线程仍然处于等待状态.
* 并发（Concurrent）：指用户线程与垃圾收集线程同时执行（但不一定是并行的,可能会交替执行）,用户程序继续运行,而垃圾收集程序运行于另一个CPU上.

#### Parallel Scavege收集器
Parallel Scavenge收集器也是一个新生代收集器,它也是使用复制算法的收集器,又是并行的多线程收集器……看上去和ParNew都一样,那它有什么特别之处呢？

它的关注点与其他收集器不同,CMS等收集器的关注点尽可能地缩短垃圾收集时用户线程的停顿时间,而Parallel Scavenge收集器的目标则是达到一个可控制的吞吐量（Throughput）.

> 所谓吞吐量就是CPU用于运行用户代码的时间与CPU总消耗时间的比值,即吞吐量 = 运行用户代码时间 /（运行用户代码时间 + 垃圾收集时间）,虚拟机总共运行了100分钟,其中垃圾收集花掉1分钟,那吞吐量就是99%.

停顿时间越短就越适合需要与用户交互的程序,良好的响应速度能提升用户的体验;而高吞吐量则可以最高效率地利用CPU时间,尽快地完成程序的运算任务,主要适合在后台运算而不需要太多交互的任务.

Parallel Scavenge收集器提供了两个参数用于精确控制吞吐量,分别是控制最大垃圾收集停顿时间的`-XX:MaxGCPauseMillis`参数及直接设置吞吐量大小的 `-XX:GCTimeRatio`参数.
* `MaxGCPauseMillis` : 参数允许的值是一个大于0的毫秒数,收集器将尽力保证内存回收花费的时间不超过设定值.不过大家不要异想天开地认为如果把这个参数的值设置得稍小一点就能使得系统的垃圾收集速度变得更快,GC停顿时间缩短是以牺牲吞吐量和新生代空间来换取的：系统把新生代调小一些,收集300MB新生代肯定比收集500MB快吧,这也直接导致垃圾收集发生得更频繁一些,原来10秒收集一次、每次停顿100毫秒,现在变成5秒收集一次、每次停顿70毫秒.停顿时间的确在下降,但吞吐量也降下来了.

* `GCTimeRatio` : 参数的值应当是一个大于0小于100的整数,也就是垃圾收集时间占总时间的比率,相当于是吞吐量的倒数.如果把此参数设置为19,那允许的最大GC时间就占总时间的5%（即1 /（1+19））,默认值为99,就是允许最大1%（即1 /（1+99））的垃圾收集时间.

由于与吞吐量关系密切,Parallel Scavenge收集器也经常被称为“吞吐量优先”收集器.除上述两个参数之外,Parallel Scavenge收集器还有一个参数`-XX:+UseAdaptiveSizePolicy`值得关注.
* `-XX:+UseAdaptiveSizePolicy` :这是一个开关参数,当这个参数打开之后,就不需要手工指定新生代的大小（-Xmn）、Eden与Survivor区的比例（`-XX:SurvivorRatio`）、晋升老年代对象年龄（`-XX:PretenureSizeThreshold`）等细节参数了,虚拟机会根据当前系统的运行情况收集性能监控信息,动态调整这些参数以提供最合适的停顿时间或最大的吞吐量,这种调节方式称为GC自适应的调节策略（GC Ergonomics）.
> 如果读者对于收集器运作原理不太了解,手工优化存在困难的时候,使用Parallel Scavenge收集器配合自适应调节策略,把内存管理的调优任务交给虚拟机去完成将是一个很不错的选择.只需要把基本的内存数据设置好（如-Xmx设置最大堆）,然后使用`MaxGCPauseMillis`参数（更关注最大停顿时间）或`GCTimeRatio`参数（更关注吞吐量）给虚拟机设立一个优化目标,那具体细节参数的调节工作就由虚拟机完成了.自适应调节策略也是`Parallel Scavenge`收集器与`ParNew`收集器的一个重要区别.

> 注意ParallelScavenge收集器的收集策略里就有直接进行Major GC的策略选择过程,而不必先进行Minor GC

### 老年代收集器

#### Serial Old收集器
Serial Old是Serial收集器的老年代版本,它同样是一个单线程收集器,使用`标记-整理`算法.这个收集器的主要意义也是被Client模式下的虚拟机使用.如果在Server模式下,它主要还有两大用途：一个是在JDK 1.5及之前的版本中与Parallel Scavenge收集器搭配使用,另外一个就是作为CMS收集器的后备预案,在并发收集发生Concurrent Mode Failure的时候使用.

#### Parallel old收集器
Parallel Old是Parallel Scavenge收集器的老年代版本,使用多线程和“标记－整理”算法.这个收集器是在JDK 1.6中才开始提供的,在此之前,新生代的Parallel Scavenge收集器一直处于比较尴尬的状态.原因是,如果新生代选择了Parallel Scavenge收集器,老年代除了Serial Old（PS MarkSweep）收集器外别无选择（还记得上面说过Parallel Scavenge收集器无法与CMS收集器配合工作吗？）.由于单线程的老年代Serial Old收集器在服务端应用性能上的“拖累”,即便使用了Parallel Scavenge收集器也未必能在整体应用上获得吞吐量最大化的效果,又因为老年代收集中无法充分利用服务器多CPU的处理能力,在老年代很大而且硬件比较高级的环境中,这种组合的吞吐量甚至还不一定有ParNew加CMS的组合“给力”.

直到Parallel Old收集器出现后,“吞吐量优先”收集器终于有了比较名副其实的应用组合,在注重吞吐量及CPU资源敏感的场合,都可以优先考虑Parallel Scavenge加Parallel Old收集器.

#### CMS收集器
在JDK 1.5时期,HotSpot推出了一款在强交互应用中几乎可称为有划时代意义的垃圾收集器—CMS收集器Concurrent Mark Sweep）,这款收集器是HotSpot虚拟机中第一款真正意义上的并发（Concurrent）收集器,它第一次实现了让垃圾收集线程与用户线程（基本上）同时工作.

不幸的是,CMS收集器作为老年代的收集器,却无法与JDK 1.4.0中已经存在的新生代收集器Parallel Scavenge配合工作,所以在JDK1.5中使用CMS来收集老年代的时候,新生代只能选择ParNew或Serial收集器中的一个.ParNew收集器也是使用`-XX: +UseConcMarkSweepGC`选项后的默认新生代收集器,也可以使用 `-XX:+UseParNewGC`选项来强制指定它.

CMS（Concurrent Mark Sweep）收集器是一种以获取最短回收停顿时间为目标的收集器.目前很大一部分的Java应用都集中在互联网站或B/S系统的服务端上,这类应用尤其重视服务的响应速度,希望系统停顿时间最短,以给用户带来较好的体验.CMS收集器就非常符合这类应用的需求.

从名字（包含“Mark Sweep”）上就可以看出CMS收集器是基于“标记-清除”算法实现的,它的运作过程相对于前面几种收集器来说要更复杂一些,整个过程分为4个步骤,包括：
1. 初始标记（CMS initial mark）
2. 并发标记（CMS concurrent mark）
3. 重新标记（CMS remark）
4. 并发清除（CMS concurrent sweep）

其中初始标记、重新标记这两个步骤仍然需要“Stop The World”.初始标记仅仅只是标记一下GC Roots能直接关联到的对象,速度很快,并发标记阶段就是进行GC Roots Tracing的过程,而重新标记阶段则是为了修正并发标记期间,因用户程序继续运作而导致标记产生变动的那一部分对象的标记记录,这个阶段的停顿时间一般会比初始标记阶段稍长一些,但远比并发标记的时间短.

由于整个过程中耗时最长的并发标记和并发清除过程中,收集器线程都可以与用户线程一起工作,所以总体上来说,CMS收集器的内存回收过程是与用户线程一起并发地执行的.通过图3-10可以比较清楚地看到CMS收集器的运作步骤中并发和需要停顿的时间.

CMS是一款优秀的收集器,它的最主要优点在名字上已经体现出来了：并发收集、低停顿,Sun的一些官方文档里面也称之为并发低停顿收集器（Concurrent Low Pause Collector）.但是CMS还远达不到完美的程度,它有以下三个显著的缺点：

CMS收集器对CPU资源非常敏感.其实,面向并发设计的程序都对CPU资源比较敏感.在并发阶段,它虽然不会导致用户线程停顿,但是会因为占用了一部分线程（或者说CPU资源）而导致应用程序变慢,总吞吐量会降低.CMS默认启动的回收线程数是（CPU数量+3）/ 4,也就是当CPU在4个以上时,并发回收时垃圾收集线程最多占用不超过25%的CPU资源.但是当CPU不足4个时（譬如2个）,那么CMS对用户程序的影响就可能变得很大,如果CPU负载本来就比较大的时候,还分出一半的运算能力去执行收集器线程,就可能导致用户程序的执行速度忽然降低了50%,这也很让人受不了.为了解决这种情况,虚拟机提供了一种称为“增量式并发收集器”（Incremental Concurrent Mark Sweep / i-CMS）的CMS收集器变种,所做的事情和单CPU年代PC机操作系统使用抢占式来模拟多任务机制的思想一样,就是在并发标记和并发清理的时候让GC线程、用户线程交替运行,尽量减少GC线程的独占资源的时间,这样整个垃圾收集的过程会更长,但对用户程序的影响就会显得少一些,速度下降也就没有那么明显,但是目前版本中,i-CMS已经被声明为“deprecated”,即不再提倡用户使用.

CMS收集器无法处理浮动垃圾（Floating Garbage）,可能出现“Concurrent Mode Failure”失败而导致另一次Full GC的产生.由于CMS并发清理阶段用户线程还在运行着,伴随程序的运行自然还会有新的垃圾不断产生,这一部分垃圾出现在标记过程之后,CMS无法在本次收集中处理掉它们,只好留待下一次GC时再将其清理掉.这一部分垃圾就称为“浮动垃圾”.也是由于在垃圾收集阶段用户线程还需要运行,即还需要预留足够的内存空间给用户线程使用,因此CMS收集器不能像其他收集器那样等到老年代几乎完全被填满了再进行收集,需要预留一部分空间提供并发收集时的程序运作使用.在默认设置下,CMS收集器在老年代使用了68%的空间后就会被激活,这是一个偏保守的设置,如果在应用中老年代增长不是太快,可以适当调高参数`-XX:CMSInitiatingOccupancyFraction`的值来提高触发百分比,以便降低内存回收次数以获取更好的性能.要是CMS运行期间预留的内存无法满足程序需要,就会出现一次“Concurrent Mode Failure”失败,这时候虚拟机将启动后备预案：临时启用Serial Old收集器来重新进行老年代的垃圾收集,这样停顿时间就很长了.所以说参数`-XX:CMSInitiatingOccupancyFraction`设置得太高将会很容易导致大量“Concurrent Mode Failure”失败,性能反而降低.

还有最后一个缺点,在本节在开头说过,CMS是一款基于“标记-清除”算法实现的收集器,如果读者对前面这种算法介绍还有印象的话,就可能想到这意味着收集结束时会产生大量空间碎片.空间碎片过多时,将会给大对象分配带来很大的麻烦,往往会出现老年代还有很大的空间剩余,但是无法找到足够大的连续空间来分配当前对象,不得不提前触发一次Full GC.为了解决这个问题,CMS收集器提供了一个`-XX:+UseCMSCompactAtFullCollection`开关参数,用于在“享受”完Full GC服务之后额外免费附送一个碎片整理过程,内存整理的过程是无法并发的.空间碎片问题没有了,但停顿时间不得不变长了.虚拟机设计者们还提供了另外一个参数`-XX: CMSFullGCsBeforeCompaction`,这个参数用于设置在执行多少次不压缩的Full GC后,跟着来一次带压缩的.

#### G1收集器

G1（Garbage First）收集器是当前收集器技术发展的最前沿成果,在JDK 1.6_Update14中提供了EarlyAccess版本的G1收集器以供试用.在将来JDK 1.7正式发布的时候,G1收集器很可能会有一个成熟的商用版本随之发布.这里只对G1收集器进行简单介绍.

G1收集器是垃圾收集器理论进一步发展的产物,它与前面的CMS收集器相比有两个显著的改进：一是G1收集器是基于“标记-整理”算法实现的收集器,也就是说它不会产生空间碎片,这对于长时间运行的应用系统来说非常重要.二是它可以非常精确地控制停顿,
既能让使用者明确指定在一个长度为M毫秒的时间片段内,消耗在垃圾收集上的时间不得超过N毫秒,这几乎已经是实时Java（RTSJ）的垃圾收集器的特征了.

G1收集器可以实现在基本不牺牲吞吐量的前提下完成低停顿的内存回收,这是由于它能够极力地避免全区域的垃圾收集,之前的收集器进行收集的范围都是整个新生代或老年代,而G1将整个Java堆（包括新生代、老年代）划分为多个大小固定的独立区域（Region）,并且跟踪这些区域里面的垃圾堆积程度,在后台维护一个优先列表,每次根据允许的收集时间,优先回收垃圾最多的区域（这就是Garbage First名称的来由）.区域划分及有优先级的区域回收,保证了G1收集器在有限的时间内可以获得最高的收集效率.


## 参数设置

### ExplicitGCInvokesConcurrent
`-XX:`:当收到System.gc()方法提交的垃圾收集申请时,使用CMS收集器收集

### UseSerialGC
`-XX:`:打开此开关后使用Serial+SerialOld的收集器组合进行内存回收.

### UseParNewGC
`-XX:`:虚拟机运行在Client模式下的默认值,打开此开关后,使用ParNew+SeialOld的收集器组合进行垃圾收集

### UseConcMarkSweepGc
`-XX:`:打开次开关后使用`ParNew+CMS+SerialOld`收集器组合进行垃圾收集.如果CMS收集器出现`ConcurrentModeFailure`,则`SeialOld`收集器将作为后备收集器.

### UseParallelGC
`-XX:`:虚拟机运行在Server模式下的默认值,打开此开关后,使用ParallelScavenge+SerialOld的收集器组合进行内存回收

### UseParaelOldGC
`-XX:`:打开此开关后,使用ParallelScavenge+ParallelOld的收集器组合进行内存回收

### ParallelGCThreads
`-XX:`:设置并行GC时进行内存回收的线程数(少于或等于8个CPU时默认值为CPU数量值,多于8个CPU时比CPU数量值小)

### CMSInitiatingOccupancyFraction
`-XX:`:设置CMS收集器在老年代空间被使用多少后触发垃圾收集

### UseCMSCompactAtFullCollection
`-XX:`:设置CMS收集器在完成垃圾收集后是否要进行一次内存碎片整理

### CMSFullGCBeforeCompaction
`-XX:`:设置CMS收集器在进行若干次垃圾收集后再启动一次内存碎片整理

### UseParallelOldGC
`-XX:-UseParallelOldGC`:所有的集合使用并行垃圾收集器.能够自动化地设置这个选项-XX:+UseParallelGC

###　ConcGCThreads
`-XX:ConcGCThreads=n`:`concurrentgarbagecollectors`使用的线程数.(默认值与JVM所在平台有关).

### UseG1GC
`-XX:+UseG1GC`:使用`GarbageFirst(G1)`收集器