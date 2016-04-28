category: ZooKeeper
date: 2016-04-26
title: ZooKeeper Curator 事件监听
---
## 监测时检查出已有节点
```java
package zk;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.CreateMode;

public class PathCacheExample {

	public static void main(String[] args) throws InterruptedException {

		ZKClient zkClient1 = register("Server1");
		TimeUnit.SECONDS.sleep(10);

		ZKClient zkClient2 = register("Server2");
		ZKClient zkClient3 = register("Server3");

		TimeUnit.SECONDS.sleep(5);
		zkClient1.closeAllService();
		zkClient2.closeAllService();
		zkClient3.closeAllService();
	}

	public static ZKClient register(String serverName) throws InterruptedException {
		ZKClient zkClient = new ZKClient(serverName);
		Thread thread = new Thread(zkClient);
		thread.start();
		return zkClient;
	}
}

class ZKClient implements Runnable {
	private static final String PATH = "/ServersCache1";

	private CuratorFramework client = null;
	private PathChildrenCache cache = null;
	private String servername = null;

	public void closeAllService() {
		closeCuratorFramework();
		closePathChildrenCache();
	}

	public void closeCuratorFramework() {
		CloseableUtils.closeQuietly(cache);
	}

	public void closePathChildrenCache() {
		CloseableUtils.closeQuietly(client);
	}

	public ZKClient(String serverName) {
		this.servername = serverName;
		try {
			client = CuratorFrameworkFactory.newClient("0.0.0.0:2181", new ExponentialBackoffRetry(1000, 3));
			client.start();
			cache = new PathChildrenCache(client, PATH, true);
			cache.start();
			addListener(cache);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		create(client, servername, servername);
		setValue(client, servername, servername);
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void addListener(PathChildrenCache cache) {
		PathChildrenCacheListener listener = (client, event) -> {
			switch (event.getType()) {
				case CHILD_ADDED: {
					printNodeStateChange("added", event.getData().getPath());
					break;
				}
				case CHILD_UPDATED: {
					printNodeStateChange("changed", event.getData().getPath());
					break;
				}
				case CHILD_REMOVED: {
					printNodeStateChange("removed", event.getData().getPath());
					break;
				}
			}
		};
		cache.getListenable().addListener(listener);
	}

	private void printNodeStateChange(String type, String path) {
		System.out.println(servername + " Monitor Node " + type + ": " + path + ". " + new Date().toLocaleString());
	}

	private static void remove(CuratorFramework client, String pathName) {
		String path = ZKPaths.makePath(PATH, pathName);
		try {
			client.delete().deletingChildrenIfNeeded().forPath(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void create(CuratorFramework client, String pathName, String data)  {
		String path = ZKPaths.makePath(PATH, pathName);
		try {
			client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path, data.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void setValue(CuratorFramework client, String pathName, String data) {
		String path = ZKPaths.makePath(PATH, pathName);
		try {
			client.setData().forPath(path, data.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
```
输出结果为
```bash
Server1 Monitor Node added: /ServersCache1/Server1. 2016-4-28 18:41:54
Server1 Monitor Node added: /ServersCache1/Server2. 2016-4-28 18:42:04
Server1 Monitor Node added: /ServersCache1/Server3. 2016-4-28 18:42:04
Server3 Monitor Node added: /ServersCache1/Server1. 2016-4-28 18:42:04
Server3 Monitor Node added: /ServersCache1/Server2. 2016-4-28 18:42:04
Server3 Monitor Node added: /ServersCache1/Server3. 2016-4-28 18:42:04
Server2 Monitor Node added: /ServersCache1/Server1. 2016-4-28 18:42:04
Server2 Monitor Node added: /ServersCache1/Server2. 2016-4-28 18:42:04
Server2 Monitor Node added: /ServersCache1/Server3. 2016-4-28 18:42:04
Server3 Monitor Node removed: /ServersCache1/Server1. 2016-4-28 18:42:09
Server2 Monitor Node removed: /ServersCache1/Server1. 2016-4-28 18:42:09
Server3 Monitor Node removed: /ServersCache1/Server2. 2016-4-28 18:42:09
```

## 删除节点监测
```java
public class PathCacheExample {

	public static void main(String[] args) throws InterruptedException {

		ZKClient zkClient1 = register("Server1");

		TimeUnit.SECONDS.sleep(3);
		ZKClient zkClient2 = register("Server2");
		ZKClient zkClient3 = register("Server3");

		TimeUnit.SECONDS.sleep(3);
	}

	public static ZKClient register(String serverName) throws InterruptedException {
		ZKClient zkClient = new ZKClient(serverName);
		Thread thread = new Thread(zkClient);
		thread.start();
		return zkClient;
	}
}

class ZKClient implements Runnable {
	private static final String PATH = "/ServersCache5";

	private CuratorFramework client = null;
	private PathChildrenCache cache = null;
	private String servername = null;

	public void closeAllService() {
		closeCuratorFramework();
		closePathChildrenCache();
	}

	public void closeCuratorFramework() {
		CloseableUtils.closeQuietly(client);
	}

	public void closePathChildrenCache() {
		CloseableUtils.closeQuietly(cache);
	}

	public ZKClient(String serverName) {
		this.servername = serverName;
		try {
			client = CuratorFrameworkFactory.newClient("0.0.0.0:2181", new ExponentialBackoffRetry(1000, 3));
			client.start();
			cache = new PathChildrenCache(client, PATH, true);
			cache.start();
			addListener(cache);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		create(client, servername, servername);
		setValue(client, servername, servername);
		try {
			TimeUnit.SECONDS.sleep(3);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		remove(client, servername);
	}

	private void addListener(PathChildrenCache cache) {
		PathChildrenCacheListener listener = (client, event) -> {
			switch (event.getType()) {
				case CHILD_ADDED: {
					printNodeStateChange("added", event.getData().getPath());
					break;
				}
				case CHILD_UPDATED: {
					printNodeStateChange("changed", event.getData().getPath());
					break;
				}
				case CHILD_REMOVED: {
					printNodeStateChange("removed", event.getData().getPath());
					break;
				}
			}
		};
		cache.getListenable().addListener(listener);
	}

	private void printNodeStateChange(String type, String path) {
		System.out.println(servername + " Monitor Node " + type + ": " + path + ". " + new Date().toLocaleString());
	}

	private static void remove(CuratorFramework client, String pathName) {
		String path = ZKPaths.makePath(PATH, pathName);
		try {
			client.delete().forPath(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void create(CuratorFramework client, String pathName, String data)  {
		String path = ZKPaths.makePath(PATH, pathName);
		try {
			client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path, data.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void setValue(CuratorFramework client, String pathName, String data) {
		String path = ZKPaths.makePath(PATH, pathName);
		try {
			client.setData().forPath(path, data.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
```
输出结果为
```bash
Server1 Monitor Node added: /ServersCache5/Server1. 2016-4-28 19:33:13
Server1 Monitor Node added: /ServersCache5/Server2. 2016-4-28 19:33:16
Server2 Monitor Node added: /ServersCache5/Server1. 2016-4-28 19:33:16
Server1 Monitor Node added: /ServersCache5/Server3. 2016-4-28 19:33:16
Server2 Monitor Node added: /ServersCache5/Server2. 2016-4-28 19:33:16
Server2 Monitor Node added: /ServersCache5/Server3. 2016-4-28 19:33:16
Server3 Monitor Node added: /ServersCache5/Server1. 2016-4-28 19:33:16
Server3 Monitor Node added: /ServersCache5/Server2. 2016-4-28 19:33:16
Server3 Monitor Node added: /ServersCache5/Server3. 2016-4-28 19:33:16
Server2 Monitor Node removed: /ServersCache5/Server1. 2016-4-28 19:33:16
Server1 Monitor Node removed: /ServersCache5/Server1. 2016-4-28 19:33:16
Server3 Monitor Node removed: /ServersCache5/Server1. 2016-4-28 19:33:16
Server1 Monitor Node removed: /ServersCache5/Server2. 2016-4-28 19:33:19
Server3 Monitor Node removed: /ServersCache5/Server2. 2016-4-28 19:33:19
Server2 Monitor Node removed: /ServersCache5/Server2. 2016-4-28 19:33:19
Server3 Monitor Node removed: /ServersCache5/Server3. 2016-4-28 19:33:19
Server1 Monitor Node removed: /ServersCache5/Server3. 2016-4-28 19:33:19
Server2 Monitor Node removed: /ServersCache5/Server3. 2016-4-28 19:33:19
```

## 客户端网络断开监测
```java
public class PathCacheExample {

	public static void main(String[] args) throws InterruptedException {

		ZKClient zkClient1 = register("Server1");

		TimeUnit.SECONDS.sleep(3);
		ZKClient zkClient2 = register("Server2");
		ZKClient zkClient3 = register("Server3");

		TimeUnit.SECONDS.sleep(3);
		zkClient1.closeCuratorFramework();
		TimeUnit.SECONDS.sleep(1);
		zkClient2.closeCuratorFramework();
		TimeUnit.SECONDS.sleep(1);
		zkClient3.closeCuratorFramework();

		TimeUnit.SECONDS.sleep(3);
		zkClient1.closePathChildrenCache();
		zkClient2.closePathChildrenCache();
		zkClient3.closePathChildrenCache();
	}

	public static ZKClient register(String serverName) throws InterruptedException {
		ZKClient zkClient = new ZKClient(serverName);
		Thread thread = new Thread(zkClient);
		thread.start();
		return zkClient;
	}
}

class ZKClient implements Runnable {
	private static final String PATH = "/ServersCache4";

	private CuratorFramework client = null;
	private PathChildrenCache cache = null;
	private String servername = null;

	public void closeAllService() {
		closeCuratorFramework();
		closePathChildrenCache();
	}

	public void closeCuratorFramework() {
		CloseableUtils.closeQuietly(client);
	}

	public void closePathChildrenCache() {
		CloseableUtils.closeQuietly(cache);
	}

	public ZKClient(String serverName) {
		this.servername = serverName;
		try {
			client = CuratorFrameworkFactory.newClient("0.0.0.0:2181", new ExponentialBackoffRetry(1000, 3));
			client.start();
			cache = new PathChildrenCache(client, PATH, true);
			cache.start();
			addListener(cache);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		create(client, servername, servername);
//		remove(client, servername);
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void addListener(PathChildrenCache cache) {
		PathChildrenCacheListener listener = (client, event) -> {
			switch (event.getType()) {
				case CHILD_ADDED: {
					printNodeStateChange("added", event.getData().getPath());
					break;
				}
				case CHILD_UPDATED: {
					printNodeStateChange("changed", event.getData().getPath());
					break;
				}
				case CHILD_REMOVED: {
					printNodeStateChange("removed", event.getData().getPath());
					break;
				}
			}
		};
		cache.getListenable().addListener(listener);
	}

	private void printNodeStateChange(String type, String path) {
		System.out.println(servername + " Monitor Node " + type + ": " + path + ". " + new Date().toLocaleString());
	}

	private static void remove(CuratorFramework client, String pathName) {
		String path = ZKPaths.makePath(PATH, pathName);
		try {
			client.delete().deletingChildrenIfNeeded().forPath(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void create(CuratorFramework client, String pathName, String data)  {
		String path = ZKPaths.makePath(PATH, pathName);
		try {
			client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path, data.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void setValue(CuratorFramework client, String pathName, String data) {
		String path = ZKPaths.makePath(PATH, pathName);
		try {
			client.setData().forPath(path, data.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
```
结果输出为
```bash
Server1 Monitor Node added: /ServersCache4/Server1. 2016-4-28 19:27:32
Server1 Monitor Node added: /ServersCache4/Server2. 2016-4-28 19:27:35
Server3 Monitor Node added: /ServersCache4/Server1. 2016-4-28 19:27:35
Server1 Monitor Node added: /ServersCache4/Server3. 2016-4-28 19:27:35
Server3 Monitor Node added: /ServersCache4/Server2. 2016-4-28 19:27:35
Server3 Monitor Node added: /ServersCache4/Server3. 2016-4-28 19:27:35
Server2 Monitor Node added: /ServersCache4/Server1. 2016-4-28 19:27:35
Server2 Monitor Node added: /ServersCache4/Server2. 2016-4-28 19:27:35
Server2 Monitor Node added: /ServersCache4/Server3. 2016-4-28 19:27:35
Server2 Monitor Node removed: /ServersCache4/Server1. 2016-4-28 19:27:38
Server1 Monitor Node removed: /ServersCache4/Server1. 2016-4-28 19:27:38
Server3 Monitor Node removed: /ServersCache4/Server1. 2016-4-28 19:27:38
Server2 Monitor Node removed: /ServersCache4/Server2. 2016-4-28 19:27:39
Server3 Monitor Node removed: /ServersCache4/Server2. 2016-4-28 19:27:39
Server3 Monitor Node removed: /ServersCache4/Server3. 2016-4-28 19:27:40
```