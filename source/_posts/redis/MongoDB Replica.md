category: NoSql
date: 2015-03-08
title: MongoDB Replica
---
# Deploy a Replica Set

这篇教程讲述的是如何基于正在运行的不进行控制访问的`mongod`创建三个`replica set`.

如果想要创建带有控制访问功能的`replica set`,参考[Deploy Replica Set and Configure Authentication and Authorization](http://docs.mongodb.org/manual/tutorial/deploy-replica-set-with-auth/). 如果你想要在一个单独的MongoDB上部署`replica set`, 可以参考[Convert a Standalone to a Replica Set](http://docs.mongodb.org/manual/tutorial/convert-standalone-to-replica-set/). 关于更多的`replica set`部署信息,参考[Replication](http://docs.mongodb.org/manual/replication/)和[Replica Set Deployment Architectures](http://docs.mongodb.org/manual/core/replica-set-architectures/)

## Overview

带有三个成员的`replica sets`就足够应付网络切分和其他类型的系统失败. 那些sets有足够的能力来应付分布式类型的读操作. `Replica sets`应该保证它的成员数量维持在一个奇数上. 这条规则能够保证正常的[elections](http://docs.mongodb.org/manual/core/replica-set-elections/). 更多关于对`replica sets`的设计,参考[Replication overview](http://docs.mongodb.org/manual/core/replication-introduction/)

基本的步骤是: 首先启动要成为`replica set`成员的`mongod`, 然后配置`replica set`, 最后将`mongod`添加到`replica set`上.

## Requirements

在生产部署阶段, 你应该尽量在不同的主机上部署代理`mongod`的成员. 当使用虚拟主机进行生产部署时, 你应该在不同的主机服务器上都部署一个'mongod'.

在你创建`replica set`之前, 你必须先检查你的网络配置能够允许每一个成员都能够相互连接上. 一个成功的`replica set`部署, 每一个成员都能够连接得上其他成员. 关于如何检查连接,参考[Test Connections Between all Members](http://docs.mongodb.org/manual/tutorial/troubleshoot-replica-sets/#replica-set-troubleshooting-check-connection)

## Considerations When Deploying a Replica Set

### Architecture

在生产阶段, 将`replica set`和它的成员部署到同一台机器上. 如果可能的话, 绑定到MongoDB标准端口27017上. 使用`bind_ip`选项确保MongoDB会根据配置好的地址监听来自应用程序的连接.

如果`replica set`在不同的机房内部署, 那么应该确保大多数的`mongod`实例部署在第一站点上.参考[Replica Set Deployment Architectures]()

### Connectivity

确保网络中所有的`replica set`成员和客户端的流量能够安全和高效地传输:

* 创建一个虚拟的私有网络. 确保该网络上一个单独站点可以路由不同成员间 间所有的流量.
* 配置访问控制能够阻止未知的客户端连接到 `replica set`上
* 配置网络和防火墙规则以便进站和出站的网络包仅仅是在MongoDB的默认端口和你的配置上.

最终确保`replica set`中每个成员都可以通过可解析的`DNS`或者`hostname`访问到. 你应该恰当地设置上`DNS`名称或者通过`/etc/hosts`文件来映射这个配置

### Configuration
Specify the run time configuration on each system in a configuration file stored in /etc/mongodb.conf or a related location. Create the directory where MongoDB stores data files before deploying MongoDB.

For more information about the run time options used above and other configuration options, see Configuration File Options.

## Procedure

下面的步骤概括了在`access control`失效的情况下如何部署replica set

### Start each member of the replica set with the appropriate options.

启动`mongod`然后通过`replSet`选项设定`replica set`名字, 向`replica set`中添加一个成员. 如果想要配置其他特有参数,参考[Replication Options]()

如果你的应用程序连接了多个`replica set`, 每一个`replica set`都应该有一个独立的名字. 某些驱动会根据`replica set`名称将`replica set`连接进行分组.

下面是一个示例：
```shell
mongod --replSet "rs0"
```

你也通过配置文件设置`replica set`名字. 如果想要通过配置文件启动`mongod`, 那么你需要`--config`选项指定配置文件
```shell
mongod --config $HOME/.mongodb/config
```
在生产部署阶段, 你可以通过配置一个控制脚本来管理这个进程. 但是控制脚本的使用超过了该教程的介绍范围.

> 注意:
>
> 如果你的c盘没有创建C:/data/db, 那么会抛出 ：Hotfix KB2731284 or later update is not installed. 以及 C:\data\db not found 的字样.
>
> 那么你就需要在命令上加上 --dbpath 选项了

### Connect a mongo shell to a replica set member.

下例展示了如何连接到在`localhost:27017`上运行的`mongod`:
```shell
mongo
```

### Initiate the replica set.

接着这`mongo`shell里使用`rs.initiate()`设置成员.
```shell
rs.initiate()
```
MongoDB使用`replica set`默认配置启动了一个包含当前成员的`replica set`

> 注意:
>
> 这个过程大概需要几分钟的时间, 所以需要耐心的稍等一下.

### Verify the initial replica set configuration.

在`mongo`shell中使用`rs.conf()`输出`replica set`配置:
```shell
rs.conf()
```

输出的`replica set`配置类似于下面的结构
```json
{
   "_id" : "rs0",
   "version" : 1,
   "members" : [
      {
         "_id" : 1,
         "host" : "mongodb0.example.net:27017"
      }
   ]
}
```

### Add the remaining members to the replica set.

在`mongo`shell中使用`rs.add()`方法添加俩个成员:
```shell
rs.add("mongodb1.example.net")
rs.add("mongodb2.example.net")
```

完成这一步之后,你就获得了一个拥有完整功能的`replica set`. 新的`replica set`会选出一个主要的来.

### Check the status of the replica set.

在`mongo`shell中使用`rs.status()`方法查看`replica set`状态.
```shell
rs.status()
```

## Replication Introduction

`Replication` 是用于多台服务器间数据同步的一个进程.
