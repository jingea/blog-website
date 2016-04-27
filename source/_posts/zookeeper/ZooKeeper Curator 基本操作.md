category: ZooKeeper
date: 2016-04-27
title: ZooKeeper Curator 基本操作
---
我们根据下面的例子看一下Curator的增删改查操作
```java
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.CreateMode;

public class TestCurator {

	public static void main(String[] args) throws Exception {
		CuratorFramework client = null;
		try {
			client = CuratorFrameworkFactory.newClient("0.0.0.0:2181", new ExponentialBackoffRetry(1000, 3));
			client.start();

			// 创建一个临时节点, 如果父节点不存在, 则将父节点也创建出来
			client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/Servers/LoginServer");

			// 查看根节点下的所有节点, 但是不会对子节点进行递归查询
			client.getChildren().forPath("/").forEach(path -> System.out.println("Exist : " + path));

			// 设置数据
			client.setData().forPath("/Servers/LoginServer", "192.168.15.15".getBytes());

			// 获取节点数据
			System.out.println("Data : " + client.getData().forPath("/Servers/LoginServer"));

			// 将子节点和父节点一起删除
			client.delete().deletingChildrenIfNeeded().forPath("/Servers");

			// 验证最后还有哪些节点存在
			client.getChildren().forPath("/").forEach(path -> System.out.println("Exist : " + path));
		} finally {
			CloseableUtils.closeQuietly(client);
		}
	}
}
```
结果为
```bash
Exist : Servers
Exist : zookeeper
Data : [B@63753b6d
```

> 如果要异步执行的话, 只需要调用相关逻辑的background方法就好了

我们使用的是Curator2.x版本
```xml
<dependency>
    <groupId>org.apache.curator</groupId>
    <artifactId>curator-framework</artifactId>
    <version>2.10.0</version>
</dependency>
<dependency>
    <groupId>org.apache.curator</groupId>
    <artifactId>curator-recipes</artifactId>
    <version>2.10.0</version>
</dependency>
```
我们应该使用curator-framework, 而不是使用curator-client
```xml
<dependency>
    <groupId>org.apache.curator</groupId>
    <artifactId>curator-client</artifactId>
    <version>2.10.0</version>
</dependency>
```
curator-client是对Zookepper官方API的一个简单封装, 如果我们使用curator-client, 还需要自己处理一些底层问题, 例如失败重连等问题
```java
RetryLoop retryLoop = client.newRetryLoop();
while ( retryLoop.shouldContinue() )
{
   try
   {
       // perform your work
       ...
       // it's important to re\-get the ZK instance as there may have been an error and the instance was re\-created
       ZooKeeper      zk = client.getZookeeper();
       retryLoop.markComplete();
   }
   catch ( Exception e )
   {
       retryLoop.takeException(e);
   }
}
```
或者
```java
RetryLoop.callWithRetry(client, new Callable<Void>()
{
      @Override
      public Void call() throws Exception
      {
          // do your work here - it will get retried if needed
          return null;
      }
});
```