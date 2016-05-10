category: Java工具
date: 2016-05-09
title: JMeter
---
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

聚合报告为每个请求都创建了一个结果记录. 在结果记录中不仅仅统计了相应信息, 还提供了对请求数，最小值，最大值，平均数，错误比，吞吐量以及每秒吞吐产生的字节数。JMeter在统计时以及考虑了生成消息所消耗的时间. 如果其他的采样器以及定时器在同一个线程中, 那么这将会增加总的时间统计, 从而降低吞吐量. 因此俩个名称完全不相同的采样器只有俩个相同名称的采样器的吞吐量的一半. 
The aggregate report creates a table row for each differently named request in your test. For each request, it totals the response information and provides request count, min, max, average, error rate, approximate throughput (request/second) and Kilobytes per second throughput. Once the test is done, the throughput is the actual through for the duration of the entire test.
The thoughput is calculated from the point of view of the sampler target (e.g. the remote server in the case of HTTP samples). JMeter takes into account the total time over which the requests have been generated. If other samplers and timers are in the same thread, these will increase the total time, and therefore reduce the throughput value. So two identical samplers with different names will have half the throughput of two samplers with the same name. It is important to choose the sampler names correctly to get the best results from the Aggregate Report.

Calculation of the Median and 90% Line (90th percentile) values requires additional memory. JMeter now combines samples with the same elapsed time, so far less memory is used. However, for samples that take more than a few seconds, the probability is that fewer samples will have identical times, in which case more memory will be needed. Note you can use this listener afterwards to reload a CSV or XML results file which is the recommended way to avoid performance impacts. See the Summary Report for a similar Listener that does not store individual samples and so needs constant memory.

* Label - The label of the sample. If "Include group name in label?" is selected, then the name of the thread group is added as a prefix. This allows identical labels from different thread groups to be collated separately if required.
* # Samples - The number of samples with the same label
* Average - The average time of a set of results
* Median - The median is the time in the middle of a set of results. 50% of the samples took no more than this time; the remainder took at least as long.
* 90% Line - 90% of the samples took no more than this time. The remaining samples took at least as long as this. (90th percentile)
* 95% Line - 95% of the samples took no more than this time. The remaining samples took at least as long as this. (95th percentile)
* 99% Line - 99% of the samples took no more than this time. The remaining samples took at least as long as this. (99th percentile)
* Min - The shortest time for the samples with the same label
* Max - The longest time for the samples with the same label
* Error % - Percent of requests with errors
* Throughput - the Throughput is measured in requests per second/minute/hour. The time unit is chosen so that the displayed rate is at least 1.0. When the throughput is saved to a CSV file, it is expressed in requests/second, i.e. 30.0 requests/minute is saved as 0.5.
* Kb/sec - The throughput measured in Kilobytes per second


接下来我们看一下Summy Report
![](https://raw.githubusercontent.com/ming15/blog-website/images/jmeter/JMeter7.png)
The summary report creates a table row for each differently named request in your test. This is similar to the Aggregate Report , except that it uses less memory.
The thoughput is calculated from the point of view of the sampler target (e.g. the remote server in the case of HTTP samples). JMeter takes into account the total time over which the requests have been generated. If other samplers and timers are in the same thread, these will increase the total time, and therefore reduce the throughput value. So two identical samplers with different names will have half the throughput of two samplers with the same name. It is important to choose the sampler labels correctly to get the best results from the Report.

* Label - The label of the sample. If "Include group name in label?" is selected, then the name of the thread group is added as a prefix. This allows identical labels from different thread groups to be collated separately if required.
* # Samples - The number of samples with the same label
* Average - The average elapsed time of a set of results
* Min - The lowest elapsed time for the samples with the same label
* Max - The longest elapsed time for the samples with the same label
* Std. Dev. - the Standard Deviation of the sample elapsed time
* Error % - Percent of requests with errors
* Throughput - the Throughput is measured in requests per second/minute/hour. The time unit is chosen so that the displayed rate is at least 1.0. When the throughput is saved to a CSV file, it is expressed in requests/second, i.e. 30.0 requests/minute is saved as 0.5.
* Kb/sec - The throughput measured in Kilobytes per second
* Avg. Bytes - average size of the sample response in bytes. (in JMeter 2.2 it wrongly showed the value in kB)