category: Clojure
date: 2015-10-08
title: Clojure谓词
---
[Clojure入门教程](http://xumingming.sinaapp.com/302/clojure-functional-programming-for-the-jvm-clojure-tutorial)学习笔记.

开发环境基于[IntelliJ IDEA 14 La Clojure](http://plugins.jetbrains.com/plugin/?id=4050)插件.

## 谓词
Clojure 提供了很多函数来充当谓词的功能 — 测试条件是否成立

### 测试对象类型谓词
* `class?`
* `coll?`
* `decimal?`
* `delay?`
* `float?`
* `fn?`
* `instance?`
* `integer?`
* `isa?`
* `keyword?`
* `list?`
* `macro?`
* `map?`
* `number?`
* `seq?`
* `set?`
* `string?` 
* `vector?`

### 测试两个值关系
* `<`
* `<=`
* `=`
* `not=`
* `==`
* `>`
* `>=`
* `compare`
* `distinct?` 
* `identical?`

### 测试逻辑关系
* `and`
* `or`
* `not`
* `true?`
* `false?` 
* `nil?`


### 测试集合
* `empty?`
* `not-empty`
* `every?`
* `not-every?`
* `some?` 
* `not-any?`

### 测试数字的谓词有
* `even?`
* `neg?`
* `odd?`
* `pos?` 
* `zero?`



