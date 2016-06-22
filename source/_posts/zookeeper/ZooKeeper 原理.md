category: ZooKeeper
date: 2015-07-13
title: ZooKeeper 原理
---

## 数据结构
Zookeeper 会维护一个类似于标准的文件系统的数据结构
![](https://raw.githubusercontent.com/ming15/blog-website/images/zookeeper/data-node-tree.jpg)

## 节点类型
* PERSISTENT：持久化目录节点，这个目录节点存储的数据不会丢失；
* PERSISTENT_SEQUENTIAL：顺序自动编号的目录节点，这种目录节点会根据当前已近存在的节点数自动加 1，然后返回给客户端已经成功创建的目录节点名；
* EPHEMERAL：临时目录节点，一旦创建这个节点的客户端与服务器端口也就是 session 超时，这种节点会被自动删除；
* EPHEMERAL_SEQUENTIAL：临时自动编号节点

## 角色
ZK中有如下三种角色
* Leader：领导者，负责投票的发起和决议，以及更新系统状态
* Follower：接受客户端的请求并返回结果给客户端，并参与投票
* Observer：接受客户端的请求，将写的请求转发给leader，不参与投票。Observer目的是扩展系统，提高读的速度。

## 选举
![](https://raw.githubusercontent.com/ming15/blog-website/images/zookeeper/40280796_2.jpg)