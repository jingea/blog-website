category: : 编程语言
tag: Clojure
date: 2015-10-08
title: Clojure函数
---
[Clojure入门教程](http://xumingming.sinaapp.com/302/clojure-functional-programming-for-the-jvm-clojure-tutorial)学习笔记.

开发环境基于[IntelliJ IDEA 14 La Clojure](http://plugins.jetbrains.com/plugin/?id=4050)插件.

## Clojure操作
Clojure里面的每个操作被实现成以下三种形式的一种: 
* 函数(function), 
* 宏(macro)
* special form. 

### 函数
`defn`宏用来定义一个函数。它的参数包括一个函数名字，一个可选的注释字符串，参数列表，然后一个方法体。而函数的返回值则是方法体里面最后一个表达式的值。所有的函数都会返回一个值， 只是有的返回的值是nil。

通过宏`defn`定义的函数是私有的. 这意味着它们只在定义它们的名字空间里面可见. 其它一些类似定义私有函数/宏的还有：`defmacro`- 和`defstruct`- (在`clojure.contrib.def`里面)

#### 定义函数
```clojure
(defn printHello ;定义函数名
  "println hello" ;注释
  [name]  ; 参数
  (println "hello") ; 方法体
  )

(printHello "") ;调用函数
```
需要注意的是,调用函数时,如果参数声明了,则必须加上参数

还有个有趣的特性,我们可以在声明一个函数时, 添加多个参数列表以及与之对应的方法体.只要各个参数列表的参数数量不一样就可以了.
```clojure
(defn printHello
  "println hello"

  ([v] (println "hello" v))

  ([] (println "hello"))

  )

(printHello)
(printHello "world")
```


##### 匿名函数
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

#### 函数重载
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


#### 函数参数占位符
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

#### 高阶函数
##### complement
接受一个函数作为参数，如果参数函数返回值是true， 那么它就返回false, 相当于一个取反的操作
```clojure

```

##### comp
comp把任意多个函数组合成一个，前面一个函数的返回值作为后面一个函数的参数
```clojure

```

##### partial
partial 函数创建一个新的函数 — 通过给旧的函数制定一个初始值， 然后再调用原来的函数
```clojure

```

##### memoize
函数接受一个参数，它的作用就是给原来的函数加一个缓存，所以如果同样的参数被调用了两次， 那么它就直接从缓存里面返回缓存了的结果
```clojure

```

##### time
```clojure

```

