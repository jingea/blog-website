category: Java
date: 2015-06-10
title: Maven POM文件配置
---

## maven属性
内置属性
* `${basedir}`: 表示项目根目录,即包含`pom.xml`文件的目录
* `${version}`:项目版本

POM属性
该类属性引用POM文件中对应的元素值,例如：
* `${project.artifactId}`: 引用`<project><artifactId>`值
* `${project.build.sourceDirectory}`: 项目的主源码目录
* `${project.build.directory}`: 项目构建的输出目录

自定义属性, 在`<Properties><property>`里定义的属性

setting属性, 与POM属性同理,但是以`settings`开头. 这个属性引用的是`setting.xml`文件中XML元素的值.

Java系统属性, 所有JAVA系统中的属性都可以使用Maven属性引用,使用`mvn help:system`查看所有Java系统属性

环境变量属性, 所有环境变量属性属性都可以使用`env`开头的属性引用,例如`${env.JAVA_HOME}`

