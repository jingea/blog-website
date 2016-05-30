category: JavaSE
date: 2015-03-09
title: ForkJoin框架简介
---
# ForkJoin框架


## Fork/Join

Fork/Join框架是ExecutorService接口的另一种实现,用来解决特殊问题.
Fork/Join框架是用来解决  能够通过分治技术将问题拆分成小任务的  问题.

### Fork/Join框架 与 执行器框架 的区别:

Fork/Join框架 与 执行器框架 的区别在于invokeAll()方法.(线程F)使用Join操作让一个主任务A等待它所创建的子任务B的完成,执行这个任务的线程F称为工作者线程.工作者线程寻找其他仍未被执行的任务,然后开始执行.在执行器框架中,所有的任务必须发送给执行器,在ForkJoin框架中,执行器包含了待执行的方法,任务的控制也是在执行器中执行的


### Fork/Join框架基于以下俩种操作：
* 分解(Fork) : 当需要将一个任务拆分成若干个小任务时,在框架中执行这些任务
* 合并(Join) : 当一个主任务等待其创建的更多个子任务的完成执行


### Fork/Join框架的组成
1. ForkJoinPool  这个实现了ExecutorService接口,并实现了工作窃取算法.它管理工作者线程,并提供任务的状态信息,执行信息
2. ForkJoinTask	这个是在ForkJoinPool里执行任务的基类
3. RecusiveAction	用于没有返回结果的任务
4. RecusiveTask		用于有返回结果的任务


### 在使用Fork/Join框架需要注意以下几点：
1. 任务只能使用fork()/join()操作当作同步机制.如果使用其他的同步机制,工作者线程就不能执行其他(需要同步的)任务.
2. 任务里不能执行IO操作
3. 任务不能抛出运行时异常


ForkJoinPool可以接受Runnable对象或者ForkJoinTask对象.
但是当接受Runnable对象的时候,ForkJoinPool不再采用工作窃取算法.
同样地当调用ForkJoinPool#invokeALL(),invokeAny()时传进Callable对象也是允许的,但是如此ForkJoinPool也就不再采用工作窃取算法.

## 创建Fork/Join线程池
## 取消任务
ForkJoinTask 提供的cancel()方法允许取消一个仍没有被执行的任务.如果任务已经执行,那么调用cancel()方法也无法取消.ForkJoin框架的局限性在于,ForkJoinPool线程池里的任务不允许被取消.为了克服这种局限性,我们将ForkJoinTask任务存储在TaskManager里.
## 合并任务的结果
## 在任务中抛出异常
## 异步运行任务
一旦主任务处理完指定文件夹里的所有内容,它将调用join()方法等待发送到线程池里的所以子任务执行完成

* join()方法在主任务中被调用,然后等待任务执行结束,并通过compute()方法返回值.
* 主任务将所以的子任务结果进行合并,这些子任务发送到线程池中时带有自己的结果列表,
* 通过调用compute()方法返回这个列表并作为主任务的返回值.
