category: Clojure
date: 2015-10-08
title: Clojure集合
---
[Clojure入门教程](http://xumingming.sinaapp.com/302/clojure-functional-programming-for-the-jvm-clojure-tutorial)学习笔记.

开发环境基于[IntelliJ IDEA 14 La Clojure](http://plugins.jetbrains.com/plugin/?id=4050)插件.

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
