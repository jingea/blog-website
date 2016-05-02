category: Java 项目构建
date: 2015-06-08
title: Maven生命周期
---
Maven的生命周期就是对所有的构建过程进行抽象和统一.Maven的生命周期是抽象的,因此生命周期本身是并不做任何实际工作的,实际的任务交给插件来完成.

Maven拥有如下三套相互独立的生命周期,每个生命周期都包含一些阶段(phase),阶段是按照顺序执行的,而且当PhaseA在PhaseB之前,那么当执行PhaseB时会先执行PhaseA. 但是这三套生命周期是完全独立的.

### clean生命周期
清理项目,下列是该生命周期的阶段
* `pre-clean`
* `clean`
* `post-clean`
该生命周期包含的Maven命令：
```java
mvn clean
```

### default生命周期
构建项目,下列是该生命周期的阶段
* `validate`
* `initialize`
* `generate-sources`
* `process-sources`
* `generate-resources`
* `process-resources`
* `compile`
* `process-class`
* `generate-test-sources`
* `process-test-sourcs`
* `generate-test-resources`
* `process-test-resources`
* `test-compile`
* `process-test-classes`
* `test`
* `generate-package`
* `package`
* `pre-interation-test`
* `interation-test`
* `post-interatopm-test`
* `verify`
* `install`
* `deploy`
该生命周期包含的Maven命令：
```java
mvn validate
mvn compile
mvn test
mvn package
mvn verify
mvn install
mvn deploy
```

安装jar包到本地库
```java
mvn install:install-file -DgroupId=demo -DartifactId=test -Dversion=1.0 -Dpackaging=jar -Dfile=E:\XingeApp.jar
```
安装到自己搭建的中央仓库
```java
mvn deploy:deploy-file -DgroupId=demo -DartifactId=demo -Dversion=1.0 -Dpackaging=jar -Dfile=E:\XingeApp.jar -Durl=[url] -DrepositoryId=[id]
```

maven依赖本地tools.jar
```xml
<dependency>
    <groupId>com.sun</groupId>
    <artifactId>tools</artifactId>
    <version>1.8</version>
    <scope>system</scope>
    <systemPath>D:/Program Files/Java/jdk1.8.0_77/lib/tools.jar</systemPath>
</dependency>
```

### site生命周期
建立项目站点,下列是该生命周期的阶段
* `pre-site`
* `site`
* `post-site`
* `site-deploy`
该生命周期包含的Maven命令：
```java
mvn site
```


## maven属性
### 内置属性
* `${basedir}`: 表示项目根目录,即包含`pom.xml`文件的目录
* `${version}`:项目版本

### POM属性
该类属性引用POM文件中对应的元素值,例如：
* `${project.artifactId}`: 引用`<project><artifactId>`值
* `${project.build.sourceDirectory}`: 项目的主源码目录
* `${project.build.directory}`: 项目构建的输出目录

### 自定义属性
在`<Properties><property>`里定义的属性

### setting属性
与POM属性同理,但是以`settings`开头. 这个属性引用的是`setting.xml`文件中XML元素的值.

### Java系统属性
所有JAVA系统中的属性都可以使用Maven属性引用,使用`mvn help:system`查看所有Java系统属性

### 环境变量属性
所有环境变量属性属性都可以使用`env`开头的属性引用,例如`${env.JAVA_HOME}`
