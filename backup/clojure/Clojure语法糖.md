category: Clojure
date: 2015-10-08
title: Clojure语法糖
---
[Clojure入门教程](http://xumingming.sinaapp.com/302/clojure-functional-programming-for-the-jvm-clojure-tutorial)学习笔记.

开发环境基于[IntelliJ IDEA 14 La Clojure](http://plugins.jetbrains.com/plugin/?id=4050)插件.

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

