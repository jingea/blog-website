category: ZooKeeper
date: 2013-09-13
title: ZooKeeper 服务器
---
演示windows系统下快速使用`Zookeeper3.4.6`版本

我们从Zookeeper官网下载下其最新的压缩包之后,然后解压得到下面的目录：
```shell
├───bin
├───conf
├───contrib
├───datadir
├───dist-maven
├───docs
├───lib
├───recipes
└───src
```
> `datadir`是我自己创建的,用于存放内存数据的快照文件。

1. 进入到`conf`目录，将`zoo_sample.cfg`修改为`zoo.cfg`文件
2. 修改`zoo.cfg`文件内容,`dataDir=E:/zookeeper-3.4.6/datadir`这个是我修改过的路径
3. 进入到`bin`目录, 执行`.\zkServer.cmd start` .最后见到` Established session 0x150eb438ceb0000 with negotiated timeout 30000 for client /127.0.0.1:54408`就启动成功了

> 如果遇到`java.lang.NumberFormatException: For input string: "E:\zookeeper-3.4.6\bin\..\conf\zoo.cfg"`这个提示,那就要去修改`bin/zkServer.cmd`文件, 将`%*`这个去掉就好了

```xml
call %JAVA% "-Dzookeeper.log.dir=%ZOO_LOG_DIR%" "-Dzookeeper.root.logger=%ZOO_LOG4J_PROP%" -cp "%CLASSPATH%" %ZOOMAIN% "%ZOOCFG%" %*
```
修改成
```xml
call %JAVA% "-Dzookeeper.log.dir=%ZOO_LOG_DIR%" "-Dzookeeper.root.logger=%ZOO_LOG4J_PROP%" -cp "%CLASSPATH%" %ZOOMAIN% "%ZOOCFG%"
```