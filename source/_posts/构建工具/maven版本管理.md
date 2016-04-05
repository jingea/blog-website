category: 构建工具
date: 2015-06-08
title: mavenmaven版本管理
---

### 自动化版本发布
自动化版本发布基于正确的版本号. 一般我们的版本号构成为`主要版本号.次要版本号.增量版本号-里程碑版本号`. 下面是pom文件中插件设置：
```xml
<plugin>
	<groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-release-plugin</artifactId>
    <version>2.5.2</version>
    <configuration>
		<!-- 如果项目结构不是采用标准的SVN布局(平行的trunk/tags/branches),则需要配置下面俩项 -->
		<tagBase>https://svn.mycompany.com/repos/myapplication/tags</tagBase>
		<branchBase>https://svn.mycompany.com/repos/myapplication/branchs</branchBase>
    </configuration>
</plugin>
```
下来我们在命令行执行命令：
```java
mvn release:clean
```
先执行清除操作,然后执行下列命令准备版本发布：
```java
mvn release:prepare
```
该命令包含下列操作：
* 检查项目是否有未提交代码
* 检查项目是否有快照版本依赖
* 根据用户的输入将快照版本升级为发布版
* 将POM中的SCM信息更新为TAG地址
* 基于修改后的POM执行MAVEN构建
* 提交POM变更
* 基于用户的输入将代码打TAG
* 将代码从发布版升级为新的快照版
* 提交POM变更

当前俩项检查ok之后,插件会提示用户输出想要发布的版本号,TAG名称和新的快照版本号

我们还可以执行回滚:
```java
mvn release:rollback
```
回滚`release:prepare`所执行的操作. 但是需要注意的是在`release:prepare`步骤中打出的TAG并不会被删除,需要手动删除.

接下来就可以执行版本发布了：
```java
mvn release:perform
```
它会检出`release:prepare`生成的TAG源码,并在此基础上执行`mvn deploy`,打包并部署到仓库.


```java
mvn release:stage
```

还有一个更棒的功能：打分支
```java
mvn release:branch
```
通过maven打分支,执行下列操作
* 检查项目是否有未提交代码
* 为分支更改POM的版本,例如从`1.1.00SNAPSHOT`改变为`1.1.1-SNAPSHOT`
* 将POM中的SCM信息更新为分支地址
* 提交以上更改
* 将主干代码更新为分支代码
* 修改本地代码使之回退到之前的版本(用户可以指定新的版本)
* 提交本地更改


```java
mvn release:update-versions
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


## Maven插件

### 自定义Manifest
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-jar-plugin</artifactId>
    <configuration>
        <archive>
            <manifestFile>
                src/main/resources/META-INF/MANIFEST.MF
            </manifestFile>
            <manifest>
                <addClasspath>true</addClasspath>
                <classpathPrefix>lib/</classpathPrefix>
                <mainClass>
                    wang.ming15.instrument.core.App
                </mainClass>
            </manifest>
        </archive>
    </configuration>
</plugin>
```
