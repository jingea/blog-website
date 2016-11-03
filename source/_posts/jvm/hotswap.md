category: JVM
date: 2016-11-02
title: Hotswap
---

如果想要动态替换JVM中的字节码, Hotspot JVM 为我们提供了俩种机制
* Hotswap
* instrument

[instrument](http://www.yu66.wang/2015/11/24/jvm/instrument%20premain/) 我已经在其他文章中有过详细介绍, 今天这篇文章主要就在探究 what's the Hotswap???

## JDK

Hotspot的HotSwap技术实现可以为我们在单步调试代码中, 改变代码继续单步调试而不用重启服务. 也就实现了, 当我们想要修复一个bug时, 不必关闭服务器直接可以热更

Hotspot 是通过 Java Platform Debugger Architecture (JPDA) 实现hotswap技术的. 但是这个实现有一个很大的缺陷, 就是我们要开启Java的debug功能
```bash
-Xrunjdwp:transport=dt_socket,server=y,onuncaught=y,launch=myDebuggerLaunchScript
```
在生产阶段我们是不可能这样做的

参考文章
* [Java Platform Debugger Architecture
Java SE 1.4 Enhancements](https://docs.oracle.com/javase/8/docs/technotes/guides/jpda/enhancements1.4.html)
* [Java调试那点事](https://yq.aliyun.com/articles/56)

## DCE VM
[Dynamic Code Evolution Virtual Machine (DCE VM)](http://ssw.jku.at/dcevm/)是Hotspot JVM的一个强化版本, 针对Hotspot JVM的Hotswap只能进行方法的修改, 这个增强版的JVM可以动态的添加删除方法或者属性字段.

但是目前为止, 这个项目还处于实验阶段, 虽然能够在debug阶段稳定运行, 但是并不推荐在生产环境中使用.

## HotswapAgent
[HotswapAgent](https://github.com/HotswapProjects/HotswapAgent)也可以为我们带来像DCE VM那样替换JVM字节码的功能(实际它也是依赖了DCE VM). 但是这个项目当前也是beta版本, 在生成环境阶段. 我也是不敢使用. 

现在项目中采用的是instrument 对方法体进行小bug热更, 但是还是希望能找到更加灵活的热更方式, 目前只能寄希望于Spring-loaded和JRebel了.