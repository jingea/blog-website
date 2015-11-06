category: Clojure
date: 2015-10-08
title: Clojure引用类型和共享数据
---
[Clojure入门教程](http://xumingming.sinaapp.com/302/clojure-functional-programming-for-the-jvm-clojure-tutorial)学习笔记.

开发环境基于[IntelliJ IDEA 14 La Clojure](http://plugins.jetbrains.com/plugin/?id=4050)插件.

在说安全修改共享数据之前,我首先要说明的一点是Clojure的引用类型:`Vars,Refs,Atoms,Agents`. 这四种引用类型有如下的共同特征.

* 它们都可以指向任意类型的对象.
* 都可以利用函数`deref` 以及宏`@`来读取它所指向的对象.
* 它们都支持验证函数,这些函数在它们所指向的值发生变化的时候自动调用.如果新值是合法的值,那么验证函数简单的返回true, 如果新值是不合法的,那么要么返回false, 要么抛出一个异常.如果只是简单地返回了false, 那么一个IllegalStateException 异常会被抛出,并且带着提示信息： "Invalid reference state" .
* 如果是Agents的话,它们还支持watchers.如果被监听的引用的值发生了变化,那么Agent会得到通知.


但是只有`Refs,Atoms,Agents`这三种方式可以安全地修改共享数据.这三种方式的实现方式都是持有一个可变引用指向一个不可改变的数据.
* `Refs`通过使用`Software Transactional Memory(STM`来提供对于多块共享数据的同步访问)
* `Atoms` 提供对于单个共享数据的同步访问。
* `Agents` 提供对于单个共享数据的异步访问。

### binding
Clojure不支持变量但是支持`binding`.`binding`跟变量有点像,但是在被赋值之前是不允许改的,bingding类型有：
* 全局binding: 通过`def`来定义一个全局binding
* 线程本地(thread local)binding:
* 函数内的本地binding:函数的参数是只在这个函数内可见的本地binding.
* 表达式内部的binding:

### vars
Vars数据被所有线程root binding, 并且每个线程线程对其有自己线程本地(thread-local)值的一种引用类型.这个类型是不是线程安全的,因为每个线程都有对其线程本地的拷贝,那么当A线程对值进行修改时,可能B线程会优先对其进行修改,但是这是B的修改对A是不可见的.

创建
```clojure
(def name initial-value) ;如果不设置值的话,则默认是'unbound'
```
修改值
```clojure
(def name new-value) 可以赋新的值 
(alter-var-root (var name) update-fn args) 自动设置新值
(set! name new-value) 在一个binding form 里满设置一个新的、线程本地的值
```
创建线程本地binding
```
(binding [name expression] body)
(set! name expression) ;
```

### Refs 
Refs是用来协调对于一个或者多个binding的并发修改的.这个协调机制是利用Software Transactional Memory (STM)来实现的. 

Refs指定在一个事务里面修改.执行事务的代码要包在`dosync`体内,当在一个事务里面对值进行修改,被改的其实是一个私有的、线程内的、直到事务提交才会被别的线程看到的一快内存.当事务结束的时候如果没有抛出异常的话,事务会正常提交. 如果在提交时发现已经有其他的线程修改了该值,那么事务会从头再重新执行一边.

要在事务里面执行的代码一定要是没有副作用的,这一点非常重要,因为前面提到的,事务可能会跟别的事务事务冲突,然后重试, 如果有副作用的话,那么出来的结果就不对了.不过要执行有副作用的代码也是可能的, 可以把这个方法调用包装给Agent, 然后这个方法会被hold住直到事务成功提交,然后执行一次.如果事务失败那么就不会执行.

创建
```clojure
(def name (ref initial-value)) ;ref函数创建了一个 Ref 对象
```

如果你要赋的新值是基于旧的值的话,那么就需要三个步骤了：
1. deference 这个 Ref 来获得它的旧值
2. 计算新值
3. 设置新值

下面的alter函数和commite函数都完成了这三个操作,要赋的新的值是基于旧的值计算出来的时候, 那么我们鼓励使用alter 和commute 而不是ref-set.
```clojure
(ref-set ref new-value) 必须在dosync里面调用 
(alter ref update-fn arguments) 必须在dosync里面调用
(commute ref update-fn arguments) 必须在dosync 里面调用
```
* alter:用来操作那些必须以特定顺序进行的修改.如果alter 试图修改的 Ref 在当前事务开始之后被别的事务改变了,那么当前事务会进行重试
* commite:操作那些修改顺序不是很重要,可以同时进行的修改.如果alter 试图修改的 Ref 在当前事务开始之后被别的事务改变了,commute 不会进行重试.它会以事务内的当前值进行计算.

dosync宏
```clojure
(dosync
  (ref-set name new-value)
 )
```

#### Validation函数
他验证所有赋给Ref的值是数字.

### Atoms 
Atoms 提供了一种比使用Refs&STM更简单的更新当个值的方法.它不受事务的影响


创建
```clojure
(def my-atom (atom initial-value))
```

有三个函数可以修改一个Atom的值：
#### reset! 
接受两个参数：要设值的Atom以及新值.它设置新的值,而不管你旧的值是什么.
```clojure
(reset! atom new-value) 
```

#### compare-and-set! 
接受三个参数：要被修改的Atom, 上次读取时候的值,新的值. 这个函数在设置新值之前会去读Atom现在的值.如果与上次读的时候的值相等, 那么设置新值并返回true, 否则不设置新值, 返回false
```clojure
(compare-and-set! atom current-value new-value)
```

#### swap!: 
接受一个要修改的 Atom, 一个计算Atom新值的函数以及一些额外的参数(如果需要的话).这个计算Atom新的值的函数会以这个Atom以及一些额外的参数做为输入.`swap!`函数实际上是对`compare-and-set!`函数的一个封装,但是有一个显著的不同. 它首先把Atom的当前值存入一个变量,然后调用计算新值的函数来计算新值, 然后再调用`compare-and-set!`函数来赋值.如果赋值成功的话,那就结束了.如果赋值不成功的话, 那么它会重复这个过程,一直到赋值成功为止
```clojure
(swap! atom update-fn arguments)
```

### Agents 
Agents 是用把一些事情放到另外一个线程来做 -- 一般来说不需要事务控制的.它们对于修改一个单个对象的值(也就是Agent的值)来说很方便.这个值是通过在另外的一个thread上面运行一个“action”来修改的.一个action是一个函数, 这个函数接受Agent的当前值以及一些其它参数. Only one action at a time will be run on a given Agent在任意一个时间点一个Agent实例上面只能运行一个action.


创建
```clojure
(def my-agent (agent initial-value))
```

有俩个个函数可以修改一个Agent的值：
#### send
`end`函数把一个 action 分配给一个 Agent, 并且马上返回而不做任何等待. 这个action会在另外一个线程(一般是由一个线程池提供的)上面单独运行. 当这个action运行结束之后,返回值会被设置给这个Agent.

send 使用一个 "固定大小的" 线程吃 (`java.util.concurrent.Executors`里面的`newFixedThreadPool` ) , 线程的个数是机器的处理器的个数加2.如果所有的线程都被占用,那么你如果要运行新的action, 那你就要等了
```clojure
(send agent update-fn arguments) 
```

#### send-off
`send-off` 函数也类似只是线程来自另外一个线程吃.

send-off 使用的是 "cached thread pool" (java.util.concurrent.Executors里面的?newCachedThreadPool) , 这个线程池里面的线程的个数是按照需要来分配的.
```clojure 
(send-off agent update-fn arguments)
```


