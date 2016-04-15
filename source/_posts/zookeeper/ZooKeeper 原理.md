category: ZooKeeper
date: 2013-09-13
title: ZooKeeper 原理
---

## 数据结构
Zookeeper 会维护一个类似于标准的文件系统的数据结构
![]()


## 节点类型
* PERSISTENT：持久化目录节点，这个目录节点存储的数据不会丢失；
* PERSISTENT_SEQUENTIAL：顺序自动编号的目录节点，这种目录节点会根据当前已近存在的节点数自动加 1，然后返回给客户端已经成功创建的目录节点名；
* EPHEMERAL：临时目录节点，一旦创建这个节点的客户端与服务器端口也就是 session 超时，这种节点会被自动删除；
* EPHEMERAL_SEQUENTIAL：临时自动编号节点

## 权限控制


## 选举