category: Java
tag: 构建工具
date: 2015-11-18
title: Gradle 初探
---
## 概述
任何一个 Gradle 构建都是由一个或多个 projects 组成。每个 project 都由多个 tasks 组成。每个 task 都代表了构建执行过程中的一个原子性操作。

我使用idea创建一个gradle项目
```
group 'wang.ming15.gradle'
version '1.0-SNAPSHOT'

apply plugin: 'java'

sourceCompatibility = 1.8

repositories {
    mavenCentral()
}

dependencies {
    testCompile group: 'junit', name: 'junit', version: '4.11'
}

```


## 任务

### 自定义任务
我们可以在task内部自由的使用groovy脚本
```
task t1 {
    doLast {
        println 'task1'
    }
}
```
或者这种方式
```
task t2 << {
        println 't2'
}
```

> `defaultTasks`我们可以使用这个命令定义一些默认的task : `defaultTasks 't1', 't2'`

### 任务依赖
我们使用`dependsOn`语法可以让一个任务依赖于另外一个任务
```
task t2 << {
        println 't2'
}

task t3(dependsOn: t2) {
    println 't3'
}
```
这种情况下, t2任务会优先于t3任务执行

> 需要注意的是, 如果t1依赖于t2, 那么当t2执行的时候会先执行t1

### 延迟依赖
```
task t3(dependsOn: 't4') {
    println 'task3'
}

task t4 << {
    println 'task4'
}
```
我们还可以在定义一个任务之后,再定义其所依赖的任务, 执行顺序仍然是t3 优先于t4


### 任务操纵

```
task t4 << {
    println 'task4'
}

task t5 << {
    println 'task5'
}

t5.dependsOn t4
```
task还有其他一些api,参考[](https://docs.gradle.org/current/javadoc/org/gradle/api/Task.html)

## 插件
如前例所示我们已经使用过Gradle提供的插件
```
apply plugin: 'java'  
```
当我们的项目使用某个插件的时候, 这个项目里就包含了那个插件的任务依赖等等

### java插件任务
我们使用了java插件, 然后我们打开idea右侧的Gradle标签(我们会看到一些java插件自带的一些task.)：
![](https://raw.githubusercontent.com/ming15/blog-website/images/gradle/gradle_ui.jpg)

命令含义参考[](http://wiki.jikexueyuan.com/project/gradle/java-package.html)

## 文件
在Gradle编译脚本文件中我们还可以自如的使用文件
```
File configFile = file('src/config.xml')
```
使用`file()`方法我们就可以打开一个文件.

### 文件集合
我们还可以使用`files()`方法创建文件集合
```
FileCollection fils = files("t.txt", new File("d.txt"), ["a.txt", 'b.txt'])
```
我们还可以使用`+`, `-`符号增加或者删减文件
```
FileCollection filc = files("t.txt", new File("d.txt"), ["a.txt", 'b.txt'])
FileCollection newFiles1 = filc + files("c.txt")
FileCollection newFiles2 = filc - files("c.txt")
```
我们还可以使用`as `将其转换为`Set`或者`List`
```
Set set1 = filc.files
Set set2 = filc as Set
List list = filc as List
```