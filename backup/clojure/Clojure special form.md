category: Clojure
date: 2015-10-08
title: Clojure special form
---
[Clojure入门教程](http://xumingming.sinaapp.com/302/clojure-functional-programming-for-the-jvm-clojure-tutorial)学习笔记.

开发环境基于[IntelliJ IDEA 14 La Clojure](http://plugins.jetbrains.com/plugin/?id=4050)插件.

## Clojure操作
Clojure里面的每个操作被实现成以下三种形式的一种: 
* 函数(function), 
* 宏(macro)
* special form. 

#### special form
Clojure支持以下几种special form

##### def
通过`def`来定义一个全局binding(还可以给他一个`root value`, 该值在所有线程里都是可见的).
```clojure
(def n 19)
(print n)
```

##### var
```clojure

```

##### do
```clojure

```


##### dot (‘.’)
```clojure

```


##### finally
```clojure

```


##### fn
```clojure

```

##### if
```clojure

```

##### let
let 这个special form 创建局限于一个 当前form的bindings. 它的第一个参数是一个vector, 里面包含名字-表达式的对子。表达式的值会被解析然后赋给左边的名字。这些binding可以在这个vector后面的表达式里面使用。这些binding还可以被多次赋值以改变它们的值，let命令剩下的参数是一些利用这个binding来进行计算的一些表达式。注意：如果这些表达式里面有调用别的函数，那么这个函数是无法利用let创建的这个binding的。
```clojure

```

##### loop
```clojure

```

##### monitor-enter
```clojure

```

##### monitor-exit
```clojure

```

##### new
```clojure

```

##### quote
```clojure

```

##### recur
```clojure

```

##### set!
```clojure

```

##### try catch throw
```clojure

```