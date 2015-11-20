category: ZooKeeper
date: 2013-11-20
title: ZooKeeper Java客户端
---
我们使用ZooKeeper官方java客户端进行测试,另外可以使用curator.
```
// 连接ZooKeeper服务器
ZooKeeper zooKeeper = new ZooKeeper("localhost:2181", 15000, event -> {
	System.out.println(event.toString());
});

// 创建持久节点(在这个版本中临时节点不允许创建子节点,所以我们选择了持久节点), 不使用节点权限控制
String nodePath = "/n" + new Random().nextInt(Integer.MAX_VALUE);

// 创建节点
zooKeeper.create(nodePath, // 节点路径
		"create".getBytes(), // 节点数据
		ZooDefs.Ids.OPEN_ACL_UNSAFE, // 权限控制
		CreateMode.PERSISTENT, // 节点类型
		(rc, path, ctx, name) -> { // 回调函数

			System.out.println(rc + " : " + path + " : " + ctx + " : " + name);

}, "create - " + nodePath);

zooKeeper.getChildren(nodePath, event -> {
	System.out.println(event.toString());
});


zooKeeper.create(nodePath + "/c", "create".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT, (rc, path, ctx, name) -> {
	System.out.println(path);
}, "");

TimeUnit.SECONDS.sleep(1);
```