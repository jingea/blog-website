category: 编程语言
date: 2015-10-08
title: Clojure
---


[Clojure入门教程](http://xumingming.sinaapp.com/302/clojure-functional-programming-for-the-jvm-clojure-tutorial)学习笔记.

开发环境基于[IntelliJ IDEA 14 La Clojure](http://plugins.jetbrains.com/plugin/?id=4050)插件.

## Clojure数据类型
* 布尔值
* 浮点数
* 字符串
* 分数



## Clojure操作
Clojure里面的每个操作被实现成以下三种形式的一种: 
* 函数(function), 
* 宏(macro)
* special form. 

## 函数
defn 宏用来定义一个函数。它的参数包括一个函数名字，一个可选的注释字符串，参数列表，然后一个方法体。而函数的返回值则是方法体里面最后一个表达式的值。所有的函数都会返回一个值， 只是有的返回的值是nil。

通过宏defn- 定义的函数是私有的. 这意味着它们只在定义它们的名字空间里面可见. 其它一些类似定义私有函数/宏的还有：defmacro- 和defstruct- (在clojure.contrib.def里面)

### 定义函数
```clojure
(defn printHello ;定义函数名
  "println hello" ;注释
  [name]  ; 参数
  (println "hello") ; 方法体

  )

(printHello "") ;调用函数
```
需要注意的是,调用函数时,如果参数声明了,则必须加上参数

还有个有趣的特性是,我们可以在声明一个函数时, 添加多个参数列表以及与之对应的方法体.只要各个参数列表的参数数量不一致就可以了.
```clojure
(defn printHello
  "println hello"

  ([v] (println "hello" v))

  ([] (println "hello"))

  )

(printHello)
(printHello "world")
```


#### 匿名函数
我们可以通过`fn`或者`#(...)`定义匿名函数

fn 定义的匿名函数可以包含任意个数的表达式
```clojure
user=> (map (fn [num] (+ num 1)) [1 2 3])
(2 3 4)
```

`#(...)`定义的匿名函数则只能包含一个表达式，如果你想包含多个表达式，那么把它用do包起来
```clojure
user=> (map #(+ % 1) [1 2 3])
(2 3 4)
```
`%`代表参数,如果有多个参数可以使用`%1`, `%2`等.

### 函数重载
Clojure虽然只能根据参数个数进行重载,使用multimethods技术可以实现任意类型的重载。

multimethod使用宏`defmulti`和`defmethod`进行定义.
* `defmulti` :参数包括一个方法名以及一个dispatch函数，这个dispatch函数的返回值会被用来选择到底调用哪个重载的函数
* `defmethod`: 参数则包括方法名，dispatch的值， 参数列表以及方法体
```clojure
(defmulti printOne class)
(defmethod printOne Number [arg] (println "print " arg))
(defmethod printOne String [arg] (println "print " arg))
(defmethod printOne :default [arg] (println "print defult"))
(printOne 12)
(printOne "cde")
(printOne false)
```


### 函数参数占位符
`_`可以用来做函数的占位符 
```clojure
(defn call1 [n1] (println n1))
(defn call2 [n1 n2] (println n1 n2))
(defn call3 [n1 _] (println n1))
(call1 1)
(call2 2 3)
(call3 3 4)
```
占位符的作用是表示这个位置有一个参数,但是我并不会使用它. 这种参数在回调函数里比较有用.
```clojure
(defn call2 [n1 n2] (println n1 n2))
(defn call3 [n1 _] (println n1))
(defn caller [call n](call n n))
(caller call2 2)
(caller call3 3)
```

### 高阶函数
#### complement
接受一个函数作为参数，如果这个参数返回值是true， 那么它就返回false, 相当于一个取反的操作
```clojure

```

#### comp
comp把任意多个函数组合成一个，前面一个函数的返回值作为后面一个函数的参数
```clojure

```

#### partial
partial 函数创建一个新的函数 — 通过给旧的函数制定一个初始值， 然后再调用原来的函数
```clojure

```

#### memoize
函数接受一个参数，它的作用就是给原来的函数加一个缓存，所以如果同样的参数被调用了两次， 那么它就直接从缓存里面返回缓存了的结果
```clojure

```

#### time
```clojure

```

### 宏





### special form
Clojure支持以下几种special form

#### def
通过`def`来定义一个全局binding(还可以给他一个`root value`, 该值在所有线程里都是可见的).
```clojure
(def n 19)
(print n)
```

#### var
```clojure

```

#### do
```clojure

```


#### dot (‘.’)
```clojure

```


#### finally
```clojure

```


#### fn
```clojure

```

#### if
```clojure

```

#### let
let 这个special form 创建局限于一个 当前form的bindings. 它的第一个参数是一个vector, 里面包含名字-表达式的对子。表达式的值会被解析然后赋给左边的名字。这些binding可以在这个vector后面的表达式里面使用。这些binding还可以被多次赋值以改变它们的值，let命令剩下的参数是一些利用这个binding来进行计算的一些表达式。注意：如果这些表达式里面有调用别的函数，那么这个函数是无法利用let创建的这个binding的。
```clojure

```

#### loop
```clojure

```

#### monitor-enter
```clojure

```

#### monitor-exit
```clojure

```

#### new
```clojure

```

#### quote
```clojure

```

#### recur
```clojure

```

#### set!
```clojure

```

#### try catch throw
```clojure

```



## 安全共享数据
Clojure提供三种方法来安全地共享可修改的数据。所有三种方法的实现方式都是持有一个可以开遍的引用指向一个不可改变的数据。

* 它们都可以指向任意类型的对象。
* 都可以利用函数deref 以及宏@ 来读取它所指向的对象。
* 它们都支持验证函数，这些函数在它们所指向的值发生变化的时候自动调用。如果新值是合法的值，那么验证函数简单的返回true, 如果新值是不合法的，那么要么返回false， 要么抛出一个异常。如果只是简单地返回了false, 那么一个IllegalStateException 异常会被抛出，并且带着提示信息： "Invalid reference state" 。
* 如果是Agents的话，它们还支持watchers。如果被监听的引用的值发生了变化，那么Agent会得到通知， 详情见 "Agents" 一节。

### binding
Clojure不支持变量但是支持`binding`。`binding`跟变量有点像，但是在被赋值之前是不允许改的，bingding类型有：
* 全局binding: 通过`def`来定义一个全局binding
* 线程本地(thread local)binding:
* 函数内的本地binding:函数的参数是只在这个函数内可见的本地binding。
* 表达式内部的binding:

### vars
Vars数据被所有线程root binding, 并且每个线程线程对其有自己线程本地(thread-local)值的一种引用类型。这个类型是不是线程安全的,因为每个线程都有对其线程本地的拷贝,那么当A线程对值进行修改时,可能B线程会优先对其进行修改,但是这是B的修改对A是不可见的.

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
Refs是用来协调对于一个或者多个binding的并发修改的。这个协调机制是利用Software Transactional Memory (STM)来实现的。 

Refs指定在一个事务里面修改。执行事务的代码要包在`dosync`体内,当在一个事务里面对值进行修改，被改的其实是一个私有的、线程内的、直到事务提交才会被别的线程看到的一快内存。当事务结束的时候如果没有抛出异常的话,事务会正常提交. 如果在提交时发现已经有其他的线程修改了该值,那么事务会从头再重新执行一边.

要在事务里面执行的代码一定要是没有副作用的，这一点非常重要，因为前面提到的，事务可能会跟别的事务事务冲突，然后重试， 如果有副作用的话，那么出来的结果就不对了。不过要执行有副作用的代码也是可能的， 可以把这个方法调用包装给Agent, 然后这个方法会被hold住直到事务成功提交，然后执行一次。如果事务失败那么就不会执行。

创建
```clojure
(def name (ref initial-value)) ;ref函数创建了一个 Ref 对象
```

如果你要赋的新值是基于旧的值的话，那么就需要三个步骤了：
1. deference 这个 Ref 来获得它的旧值
2. 计算新值
3. 设置新值

下面的alter函数和commite函数都完成了这三个操作,要赋的新的值是基于旧的值计算出来的时候, 那么我们鼓励使用alter 和commute 而不是ref-set.
```clojure
(ref-set ref new-value) 必须在dosync里面调用 
(alter ref update-fn arguments) 必须在dosync里面调用
(commute ref update-fn arguments) 必须在dosync 里面调用
```
* alter:用来操作那些必须以特定顺序进行的修改.如果alter 试图修改的 Ref 在当前事务开始之后被别的事务改变了，那么当前事务会进行重试
* commite:操作那些修改顺序不是很重要,可以同时进行的修改.如果alter 试图修改的 Ref 在当前事务开始之后被别的事务改变了，commute 不会进行重试。它会以事务内的当前值进行计算。

dosync宏
```clojure
(dosync
  (ref-set name new-value)
 )
```

#### Validation函数
他验证所有赋给Ref的值是数字。

### Atoms 
Atoms 提供了一种比使用Refs&STM更简单的更新当个值的方法。它不受事务的影响


创建
```clojure
(def my-atom (atom initial-value))
```

有三个函数可以修改一个Atom的值：
#### reset! 
接受两个参数：要设值的Atom以及新值。它设置新的值，而不管你旧的值是什么。
```clojure
(reset! atom new-value) 
```

#### compare-and-set! 
接受三个参数：要被修改的Atom, 上次读取时候的值，新的值。 这个函数在设置新值之前会去读Atom现在的值。如果与上次读的时候的值相等， 那么设置新值并返回true, 否则不设置新值， 返回false
```clojure
(compare-and-set! atom current-value new-value)
```

#### swap!: 
接受一个要修改的 Atom, 一个计算Atom新值的函数以及一些额外的参数(如果需要的话)。这个计算Atom新的值的函数会以这个Atom以及一些额外的参数做为输入。swap！函数实际上是对compare-and-set!函数的一个封装，但是有一个显著的不同。 它首先把Atom的当前值存入一个变量，然后调用计算新值的函数来计算新值， 然后再调用compare-and-set!函数来赋值。如果赋值成功的话，那就结束了。如果赋值不成功的话， 那么它会重复这个过程，一直到赋值成功为止
```clojure
(swap! atom update-fn arguments)
```

### Agents 
Agents 是用把一些事情放到另外一个线程来做 -- 一般来说不需要事务控制的。它们对于修改一个单个对象的值(也就是Agent的值)来说很方便。这个值是通过在另外的一个thread上面运行一个“action”来修改的。一个action是一个函数， 这个函数接受Agent的当前值以及一些其它参数。 Only one action at a time will be run on a given Agent在任意一个时间点一个Agent实例上面只能运行一个action.


创建
```clojure
(def my-agent (agent initial-value))
```

有俩个个函数可以修改一个Agent的值：
#### send
end 函数把一个 action 分配给一个 Agent， 并且马上返回而不做任何等待。 这个action会在另外一个线程(一般是由一个线程池提供的)上面单独运行。 当这个action运行结束之后，返回值会被设置给这个Agent。

send 使用一个 "固定大小的" 线程吃 (java.util.concurrent.Executors里面的newFixedThreadPool ) ， 线程的个数是机器的处理器的个数加2。如果所有的线程都被占用，那么你如果要运行新的action， 那你就要等了
```clojure
(send agent update-fn arguments) 
```

#### send-off
send-off 函数也类似只是线程来自另外一个线程吃。

send-off 使用的是 "cached thread pool" (java.util.concurrent.Executors里面的?newCachedThreadPool) ， 这个线程池里面的线程的个数是按照需要来分配的。
```clojure 
(send-off agent update-fn arguments)
```


## 流程控制
```clojure

```




## 调用java
```clojure

```


## 谓词
Clojure 提供了很多函数来充当谓词的功能 — 测试条件是否成立

### 测试对象类型谓词
* class?,
* coll?,
* decimal?,
* delay?,
* float?,
* fn?,
* instance?,
* integer?,
* isa?,
* keyword?,
* list?,
* macro?,
* map?,
* number?,
* seq?,
* set?,
* string? 
* vector?

### 测试两个值关系
* <,
* <=,
* =,
* not=,
* ==,
* >,
* >=,
* compare,
* distinct? 
* identical?.

### 测试逻辑关系
* and,
* or,
* not,
* true?,
* false? 
* nil?


### 测试集合
* empty?,
* not-empty,
* every?,
* not-every?,
* some? 
* not-any?.

### 测试数字的谓词有
* even?,
* neg?,
* odd?,
* pos? 
* zero?.



## Clojure集合
所有的clojure集合有以下三个特性
* 不可修改的:集合产生之后，不能从集合里面删除一个元素，也往集合里面添加一个元素
* 异源的
* 持久的

Clojure提供这些集合类型:
* list
* vector
* set
* map

### list
Lists是一个有序的元素的集合

list有多种定义方式
* `(def stooges (list "Moe" "Larry" "Curly"))`
* `(def stooges (quote ("Moe" "Larry" "Curly")))`
* `(def stooges '("Moe" "Larry" "Curly"))`


### Vectors
Vectors也是一种有序的集合。这种集合对于从最后面删除一个元素，或者获取最后面一个元素是非常高效的(O(1))。这意味着对于向vector里面添加元素使用conj被使用cons更高效。Vector对于以索引的方式访问某个元素（用nth命令）或者修改某个元素(用assoc)来说非常的高效。函数定义的时候指定参数列表用的就是vector。

创建有多种定义方式
* `(def stooges (vector "Moe" "Larry" "Curly"))`
* `(def stooges ["Moe" "Larry" "Curly"])`

### Sets
一个包含不重复元素的集合。当我们要求集合里面的元素不可以重复，并且我们不要求集合里面的元素保持它们添加时候的顺序，那么sets是比较适合的。 Clojure 支持两种不同的set： 排序的和不排序的。如果添加到set里面的元素相互之间不能比较大小，那么一个ClassCastException 异常会被抛出来。

创建有多种定义方式
* (def stooges (hash-set "Moe" "Larry" "Curly"))
* (def stooges #{"Moe" "Larry" "Curly"})  
* (def stooges (sorted-set "Moe" "Larry" "Curly"))

### Maps
Maps 保存从key到value的a对应关系 

创建有多种定义方式
* (def popsicle-map (hash-map :red :cherry, :green :apple, :purple :grape))
* (def popsicle-map {:red :cherry, :green :apple, :purple :grape}) ; same as previous
* (def popsicle-map (sorted-map :red :cherry, :green :apple, :purple :grape))
  

### 集合函数:

#### count
统计集合里面的元素个数
```clojure
user=> (count [19 "yellow" true])
3
```

#### conj
添加一个元素到集合里面去
```clojure

```

#### reverse 
把集合里面的元素反转
```clojure
user=> (reverse [1 2 3])
(3 2 1)
```

#### map 
对一个给定的集合里面的每一个元素调用一个指定的方法，然后这些方法的所有返回值构成一个新的集合（LazySeq）返回
```clojure
user=> (map #(+ % 1) [1 2 3])
(2 3 4)
```

#### apply 
把给定的集合里面的所有元素一次性地给指定的函数作为参数调用，然后返回这个函数的返回值
```clojure
user=> (apply + [1 2 3])
6
```

#### first
取集合中第一个元素
```clojure
user=> (first [1 2 3])
1
```

#### second
```clojure
user=> (second [1 2 3])
2
```

#### last
```clojure
user=> (last [1 2 3])
3

```

#### nth
```clojure
user=> (nth [1 2 3] 2)
3
```

#### next
```clojure
user=> (next [1 2 3])
(2 3)
```

#### butlast
排除最后一个元素
```clojure
( print (butlast [1 2 3]))
```

#### drop-last
```clojure
( print (drop-last [1 2 3]))
```

#### filter

```clojure
( print (filter #(> % 1) [1 2 3]))
```

#### nthnext
```clojure
( print (nthnext  [1 2 3] 1))
```

#### some
判断集合中是否包含某个元素
```clojure
user=> (some #(= % 3) [1 2 3])
true
```

#### contains
```clojure
user=> (contains? [1 2 3] 3)
false
user=> (contains? (set [1 2 3]) 3)
true
```

#### conj
通过一个已有的集合来创建一个新的包含更多元素的集合
```clojure
user=> (conj [1 2 3] 4)
[1 2 3 4]
user=> (conj [1 2 3] [4 5 6])
[1 2 3 [4 5 6]]
```

#### cons
通过一个已有的集合来创建一个新的包含更多元素的集合
```clojure
user=> (cons [1 2 3] 4)
IllegalArgumentException Don't know how to create ISeq from: java.lang.Long  clojure.lang.RT.seqFrom (RT.java:505)

user=> (cons 4 [1 2 3] )
(4 1 2 3)
```

#### remove 
函数创建一个只包含所指定的谓词函数测试结果为false的元素的集合
```clojure
user=> (remove #(= % 2) [1 2 3])
(1 3)
```

#### into
把两个list里面的元素合并成一个新的list
```clojure
user=> (into [1 2] [3 4] )
[1 2 3 4]
```

#### peek
```clojure
user=> (peek [1 2 3])
3

```

#### pop
```clojure
user=> (pop [1 2 3])
[1 2]
```

#### get
```clojure

```

#### get
```clojure
user=> (get [1 2 3] 1 110)
2
user=> (get [1 2 3] 10 110)
110
```

#### assoc
替换指定位置元素
```clojure
user=> (assoc [1 2 3] 2 4)
[1 2 4]
```

#### subvec 
获取一个给定vector的子vector
```clojure
user=> (subvec [1 2 3 ] 1 2)
[2]
```

#### disj 
函数通过去掉给定的set里面的一些元素来创建一个新的set
```clojure

```


## 语法糖

### 注释
`; text` 单行注释
```clojure

```

### 字符
`\char` 
`\tab`
`\newline`
`\space`
`\uunicode-hex-value`

```clojure

```

### 字符串
`"text"`
```clojure

```

### 关键字
关键字是一个内部字符串; 两个同样的关键字指向同一个对象; 通常被用来作为map的key

`:name`
```clojure

```

### 命名空间关键字
`::name`
```clojure

```

### 正则表达式
`#"pattern"`
```clojure

```

### 逗号
逗号被当成空白（通常用在集合里面用来提高代码可读性）
```clojure

```

### 链表
`'(items)`(不会evaluate每个元素）	

函数 `(list items)`会evaluate每个元素
```clojure

```

### vector
`[items]`


函数`(vector items)`
```clojure

```

### set
`#{items}`建立一个hash-set	

函数`(hash-set items)`
`(sorted-set items)`
```clojure

```

### map	
`{key-value-pairs}`建立一个hash-map	

函数`(hash-map key-value-pairs)`
`(sorted-map key-value-pairs)`

```clojure

```

### 绑定元数据
给symbol或者集合绑定元数据	`#^{key-value-pairs}` object在读入期处理	

函数`(with-meta object metadata-map)`在运行期处理
```clojure

```

###获取symbol或者集合的元数据
`^object`	

函数`(meta object)`
```clojure

```

### 获取一个函数的参数列表（个数不定的）
`& name`
```clojure

```

### 创建一个java对象
`(class-name. args)	`

函数`(new class-name args)`
```clojure

```

### 调用java方法
* (. class-or-instance method-name args) 
* (.method-name class-or-instance args)
```clojure

```

### 创建匿名函数
`#(single-expression)` 用% (等同于 %1), %1, %2来表示参数	

函数`(fn [arg-names] expressions)`
```clojure

```

### 获取Ref, Atom 和Agent对应的valuea
@ref	

函数(deref ref)
```clojure

```

### syntax quote 
(使用在宏里面)  `
```clojure

```

### unquote 
使用在宏里面`~value`
```clojure

```

### unquote splicing 
(使用在宏里面)`~@value`
```clojure

```

### auto-gensym 
(在宏里面用来产生唯一的symbol名字)	
`prefix#`

函数`(gensym prefix )`
```clojure

```



