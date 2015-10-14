category: 工具
date: 2015-06-08
title: maven
---
# maven生命周期
Maven的生命周期就是对所有的构建过程进行抽象和统一.Maven的生命周期是抽象的,因此生命周期本身是并不做任何实际工作的,实际的任务交给插件来完成

Maven拥有如下三套相互独立的生命周期,每个生命周期都包含一些阶段(phase),阶段是按照顺序执行的,而且当PhaseA在PhaseB之前,那么当执行PhaseB时会先执行PhaseA. 但是这三套生命周期是完全独立的.

## clean
清理项目,下列是该生命周期的阶段
* `pre-clean`
* `clean`
* `post-clean`
该生命周期包含的Maven命令：
```
mvn clean
```

## default
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
```
mvn validate
mvn compile
mvn test
mvn package
mvn verify
mvn install
mvn deploy
```

## site
建立项目站点,下列是该生命周期的阶段
* `pre-site`
* `site`
* `post-site`
* `site-deploy` 
该生命周期包含的Maven命令：
```
mvn clean
```

# 插件目标
插件里会包含多个目标,每个目标都对应着特定的功能,也就是说插件里的功能是通过目标来实现了.

例如`maven-compiler-plugin`的`compile`目标的写法为`compiler:compile`.

## 插件绑定
我们可以将插件的目标与生命周期的阶段相绑定. 

### default生命周期与内置插件绑定关系及具体任务
生命周期阶段                  | 插件目标                              |执行任务
-----------------------------|--------------------------------------|--------------
process-resources            |maven-resources-plugin:resources      |复制主资源文件至主输出目录
compile                      |maven-compile-plugin:compile	        |编译主代码至主输出目录
process-test-resources       |maven-resources-plugin:testRresources |复制测试资源文件至测试输出目录
test-compile                 |maven-compiler-plugin:testCompile     |编译测试代码至测试输出目录
test	                     |maven-surefire-plugin:test            |执行测试用例
package	                     |maven-jar-plugin:jar                  |创建项目jar包
install	                     |maven-install-plugin:install          |将项目输出构件安装到本地仓库
deploy                       |maven-deploy-plugin:deploy            |将项目输出构件部署到远程仓库

### 自定义绑定
首先我们来给个定义：
```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.codehaus.mojo</groupId>
            <artifactId>exec-maven-plugin</artifactId>
            <version>1.1.1</version>
            <executions>
                <execution>
                    <phase>install</phase>
                    <goals>
                        <goal>java</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```
我们在`install`阶段绑定了`exec-maven-plugin`插件的`java`目标.

# maven版本管理
## 自动化版本发布
自动化版本发布基于正确的版本号. 一般我们的版本号构成为`主要版本号.次要版本号.增量版本号-里程碑版本号`

### 插件
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
### 执行命令
#### `mvn release:clean`

#### `mvn release:prepare`
准备版本发布,执行下列操作
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

#### `mvn release:rollback`
回滚`release:prepare`所执行的操作. 但是需要注意的是在`release:prepare`步骤中打出的TAG并不会被删除,需要手动删除.

#### `mvn release:perform`
执行版本发布. 检出`release:prepare`生成的TAG源码,并在此基础上执行`mvn deploy`,打包并部署到仓库.

#### `mvn release:stage`

#### `mvn release:branch`
通过maven打分支,执行下列操作
* 检查项目是否有未提交代码
* 为分支更改POM的版本,例如从`1.1.00SNAPSHOT`改变为`1.1.1-SNAPSHOT`
* 将POM中的SCM信息更新为分支地址
* 提交以上更改
* 将主干代码更新为分支代码
* 修改本地代码使之回退到之前的版本(用户可以指定新的版本)
* 提交本地更改

#### `mvn release:update-versions`


# maven属性
## 内置属性
* `${basedir}`: 表示项目根目录,即包含`pom.xml`文件的目录
* `${version}`:项目版本 

## POM属性
该类属性引用POM文件中对应的元素值,例如：
* `${project.artifactId}`: 引用`<project><artifactId>`值
* `${project.build.sourceDirectory}`: 项目的主源码目录
* `${project.build.directory}`: 项目构建的输出目录

## 自定义属性
在`<Properties><property>`里定义的属性

## setting属性
与POM属性同理,但是以`settings`开头. 这个属性引用的是`setting.xml`文件中XML元素的值.

## Java系统属性
所有JAVA系统中的属性都可以使用Maven属性引用,使用`mvn help:system`查看所有Java系统属性

## 环境变量属性
所有环境变量属性属性都可以使用`env`开头的属性引用,例如`${env.JAVA_HOME}`








