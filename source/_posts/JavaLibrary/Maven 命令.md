category: Java Library
date: 2015-06-07
title: Maven 命令
---
## 版本管理
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

## 生命周期
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

