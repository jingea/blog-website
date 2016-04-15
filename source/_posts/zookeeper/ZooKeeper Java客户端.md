category: ZooKeeper
date: 2013-11-20
title: ZooKeeper Java客户端
---
我们使用ZooKeeper官方java客户端进行测试,另外可以使用curator.

我们使用Maven添加相关依赖
```xml
<dependency>
	<groupId>org.apache.zookeeper</groupId>
	<artifactId>zookeeper</artifactId>
	<version>3.4.8</version>
</dependency>
```

## 创建节点
然后我们向zk服务器上添加俩个节点
```java
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CreateNode {
	private static final String ROOT_PATH = "/Servers";
	private static ZooKeeper zooKeeper;

	public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
		// 连接ZooKeeper服务器
		zooKeeper = new ZooKeeper("localhost:2181", 15000, event -> {
			// TODO
		});

		// 创建根节点, 子节点依赖于父节点, 我们不能直接创建出/Servers/server1
		zooKeeper.create(ROOT_PATH, // 节点路径
				"create".getBytes(), // 节点数据
				ZooDefs.Ids.OPEN_ACL_UNSAFE, // 权限控制(不使用节点权限控制)
				CreateMode.PERSISTENT, // 节点类型(临时节点不允许创建子节点,所以我们选择了持久节点)
				(rc, path, ctx, name) -> { // 节点创建成功之后的回调函数
					// TODO
				}, ROOT_PATH);

		// 创建子节点
		zooKeeper.create(ROOT_PATH + "/server1", // 节点路径
				"create".getBytes(), // 节点数据
				ZooDefs.Ids.OPEN_ACL_UNSAFE, // 权限控制
				CreateMode.PERSISTENT, // 节点类型
				(rc, path, ctx, name) -> { // 节点创建成功之后的回调函数
					// TODO
				}, ROOT_PATH);

		// 获取节点下的所有子节点
		List<String> children = zooKeeper.getChildren(ROOT_PATH, event -> {
			// TODO
		});

		children.forEach(child -> System.out.println(child));

		TimeUnit.SECONDS.sleep(1);
	}
}
```

## watch
```java
import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class CreateNode {
	private static final String ROOT_PATH = "/Servers" + new Random().nextInt(100000);
	private static ZooKeeper zooKeeper;

	public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
		// 连接ZooKeeper服务器
		zooKeeper = new ZooKeeper("localhost:2181", 15000, event -> {
			System.out.println("事件变化 : " + event.getPath() + " -> " + event.getType());
		});

		// 创建根节点, 子节点依赖于父节点, 我们不能直接创建出/Servers/server1
		zooKeeper.create(ROOT_PATH, // 节点路径
				"create".getBytes(), // 节点数据
				ZooDefs.Ids.OPEN_ACL_UNSAFE, // 权限控制(不使用节点权限控制)
				CreateMode.PERSISTENT, // 节点类型(临时节点不允许创建子节点,所以我们选择了持久节点)
				(rc, path, ctx, name) -> { // 节点创建成功之后的回调函数
					try {
						System.out.println("Create Node OK!");
						zooKeeper.getChildren(ROOT_PATH, event -> {
							System.out.println("Watch : " + event.getPath() + " -> " + event.getType());
						});
					} catch (final Exception e) {
						e.printStackTrace();
					}
				}, ROOT_PATH);

		// 在创建上个node的时候, 由于注册watch是异步执行的, 因此需要在这里等待一下
		TimeUnit.SECONDS.sleep(3);

		zooKeeper.create(ROOT_PATH + "/server1", // 节点路径
				"create".getBytes(), // 节点数据
				ZooDefs.Ids.OPEN_ACL_UNSAFE, // 权限控制(不使用节点权限控制)
				CreateMode.PERSISTENT, // 节点类型(临时节点不允许创建子节点,所以我们选择了持久节点)
				(rc, path, ctx, name) -> { // 节点创建成功之后的回调函数
					try {
						System.out.println("Create Node OK!");
						zooKeeper.getChildren(ROOT_PATH, event -> {
							System.out.println("Watch : " + event.getPath() + " -> " + event.getType());
						});
					} catch (final Exception e) {
						e.printStackTrace();
					}
				}, ROOT_PATH);

		TimeUnit.SECONDS.sleep(3);
	}
}
```
输出结果为
```xml
事件变化 : null -> None
Create Node OK!
Watch : /Servers53734 -> NodeChildrenChanged
Create Node OK!
```
