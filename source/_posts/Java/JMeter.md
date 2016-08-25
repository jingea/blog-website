category: Java
date: 2016-05-09
title: JMeter
---
## Aggregate report
使用JMeter压测服务器登录压力,首先给出几张图看一下我们的配置
![](https://raw.githubusercontent.com/ming15/blog-website/images/jmeter/JMeter1.png)
![](https://raw.githubusercontent.com/ming15/blog-website/images/jmeter/JMeter2.png)
![](https://raw.githubusercontent.com/ming15/blog-website/images/jmeter/JMeter3.png)
![](https://raw.githubusercontent.com/ming15/blog-website/images/jmeter/JMeter4.png)
![](https://raw.githubusercontent.com/ming15/blog-website/images/jmeter/JMeter5.png)
> 最后一张图是概要结果, 测试GameCenter结果.csv 是聚合报告结果

![](https://raw.githubusercontent.com/ming15/blog-website/images/jmeter/JMeter6.png)
这个是我们要配置的统计结果, 我们只统计了延迟, 耗时以及消息的字节数.

下面我们看一下, JMeter官方对Aggregate report(聚合报告)的说明:

聚合报告为每个不同名的Sampler(注意是不同名的哦)都创建了一个结果记录. 在结果记录中不仅仅统计了请求响应信息, 还提供了对请求的数，最小延迟值，最大延迟值，平均延迟，请求产生的错误比，吞吐量以及每秒吞吐产生的字节数的统计。JMeter在统计时已经考虑了生成消息所消耗的时间. 如果其他的采样器以及定时器在同一个线程中, 那么这将会增加总的时间统计, 从而降低吞吐量. 因此俩个名称不相同的采样器产生的吞吐量加在一起才是总的吞吐量. 

在聚合报告中, 计算Median和90% Line值需要消耗额外的内存. JMeter现在将耗时相同的采样都合并到了一起,如此一来可以尽量减少内存占用．然而在某些情况下，　可能还会产生大量消耗内存的情况，因此推荐的方式是使用listener，然后从CSV或者XML文件中重新加载结果进行计算.

* Label - 统计标签
* # Samples - 相同名称的标签下采样的次数
* Average - 统计数据结果的平均耗时时间
* Median - 统计数据中中间的耗时时间, 50%的采样不会超过这个时间. 剩下的则大于等于这个值.
* 90% Line - 统计结果中90%的不会超过这个时间.剩下的则大于等于这个值.
* 95% Line - 统计结果中95%的不会超过这个时间.剩下的则大于等于这个值.
* 99% Line - 统计结果中99%的不会超过这个时间.剩下的则大于等于这个值.
* Min - 统计结果中最短的耗时时间. 
* Max - 统计结果中最长的耗时时间. 
* Error % - 统计结果中发生错误的百分比. 
* Throughput - 吞吐量是在可以通过second/minute/hour这三种单位进行测量. 通过选择不同的单位可以让结果值最小的可能也是1.0. 当吞吐量被存在CSV 文件时, 吞吐量是通过requests/second表示的, 例如30.0 requests/minute 就被保存为0.5.
* Kb/sec - The throughput measured in Kilobytes per second

## Summy Report
接下来我们看一下Summy Report
![](https://raw.githubusercontent.com/ming15/blog-website/images/jmeter/JMeter7.png)
summary 报告为每个不同名的请求(注意是不同名的哦)都创建了一个结果记录. 这个和聚合报告非常像, 但不同的是它所使用的内存要比聚合报告少.

* Label - 统计标签
* # Samples - 相同名称的标签下采样的次数
* Average - 该组统计的平均耗时
* Min - 该组采样中最短的耗时时间
* Max - 该组采样中最长的耗时时间
* Std. Dev. - 采样耗时的标准偏差(Standard Deviation )
* Error % - 请求发生错误的百分比
* Throughput -  吞吐量是在可以通过second/minute/hour这三种单位进行测量. 通过选择不同的单位可以让结果值最小的可能也是1.0. 当吞吐量被存在CSV 文件时, 吞吐量是通过requests/second表示的, 例如30.0 requests/minute 就被保存为0.5.
* Kb/sec - The throughput measured in Kilobytes per second
* Avg. Bytes - average size of the sample response in bytes. (in JMeter 2.2 it wrongly showed the value in kB)

## SSL
![](https://raw.githubusercontent.com/ming15/blog-website/images/jmeter/ssl.png)