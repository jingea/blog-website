category: Java
date: 2015-09-08
title: Archiva 初探
---

## 安装步骤
1. 从[Archiva官网]()下载Archiva后解压到`D:\archiva`里
2. 运行`bin\archiva.bat install`, archiva就启动成功了
3. 在浏览器运行`http://localhost:8080/`就可以进入archiva本地主页了
4. 当进入之后我们需要创建一个账号：![](https://raw.githubusercontent.com/wanggnim/website/images/Archiva/0.jpg)
5. 接着我们创建一个私有的仓库![](https://raw.githubusercontent.com/wanggnim/website/images/Archiva/1.jpg)
6. 我们创建一个最简单的私有仓库：![](https://raw.githubusercontent.com/wanggnim/website/images/Archiva/2.jpg)
7. 创建一个连接器![](https://raw.githubusercontent.com/wanggnim/website/images/Archiva/3.jpg)
8. 同样我们只选用必须的![](https://raw.githubusercontent.com/wanggnim/website/images/Archiva/4.jpg)
9. 接着如图操作![](https://raw.githubusercontent.com/wanggnim/website/images/Archiva/5.jpg)
10. 然后我们修改项目中的`pom.xml`文件!
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>testMaven</groupId>
    <artifactId>testDeply</artifactId>
    <version>1.0-SNAPSHOT</version>

    <repositories>
        <repository>
            <id>ID2015_09_17</id>
            <name>NAME2015_09_17</name>
            <url>http://localhost:8080/repository/ID2015_09_17</url>
        </repository>
    </repositories>

    <pluginRepositories>
        <pluginRepository>
            <id>ID2015_09_17</id>
            <name>NAME2015_09_17</name>
            <url>http://localhost:8080/repository/ID2015_09_17</url>
        </pluginRepository>
    </pluginRepositories>

    <distributionManagement>
        <repository>
            <id>ID2015_09_17</id>
            <name>NAME2015_09_17</name>
            <url>http://localhost:8080/repository/ID2015_09_17</url>
        </repository>
    </distributionManagement>
</project>
```
11. 修改本地仓库中的`setting.xml`文件(我的目录`C:\Users\Administrator\.m2`),我们添加私有仓库的用户名和密码!
```xml
<settings xmlns="http://maven.apache.org/settings/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">

	<servers>
        <server>
            <id>ID2015_09_17</id>
            <username>admin</username>
            <password>admin1</password>
        </server>
    </servers>

 </settings>
 ```
