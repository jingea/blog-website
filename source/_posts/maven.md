title: maven
---
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
#### mvn release:clean

#### mvn release:prepare`
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

#### mvn release:rollback
回滚`release:prepare`所执行的操作. 但是需要注意的是在`release:prepare`步骤中打出的TAG并不会被删除,需要手动删除.

#### mvn release:perform
执行版本发布. 检出`release:prepare`生成的TAG源码,并在此基础上执行`mvn deploy`,打包并部署到仓库.

#### mvn release:stage

#### mvn release:branch
通过maven打分支,执行下列操作
* 检查项目是否有未提交代码
* 为分支更改POM的版本,例如从`1.1.00SNAPSHOT`改变为`1.1.1-SNAPSHOT`
* 将POM中的SCM信息更新为分支地址
* 提交以上更改
* 将主干代码更新为分支代码
* 修改本地代码使之回退到之前的版本(用户可以指定新的版本)
* 提交本地更改

#### mvn release:update-versions


















