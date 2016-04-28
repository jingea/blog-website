category: ZooKeeper
date: 2016-04-26
title: ZooKeeper Curator 事件监听
---

```java
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.utils.ZKPaths;

public class PathCacheExample {

	public static void main(String[] args) throws InterruptedException {
		try {
			CuratorFramework client = CuratorFrameworkFactory.newClient("0.0.0.0:2181", new ExponentialBackoffRetry(1000, 3));
			client.start();
			client.delete().deletingChildrenIfNeeded().forPath("/Servers");
			CloseableUtils.closeQuietly(client);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		ZKClient.register("Server1");
		ZKClient.register("Server2");

		TimeUnit.SECONDS.sleep(5);
	}
}

class ZKClient implements Runnable {
	private static final String PATH = "/Servers";

	private CuratorFramework client = null;
	private PathChildrenCache cache = null;
	private String servername = null;

	public static void register(String serverName) throws InterruptedException {
		ZKClient zkClient = new ZKClient(serverName);
		Thread thread = new Thread(zkClient);
		thread.start();
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
		remove(client, servername);

//		listFromCache(cache);
		CloseableUtils.closeQuietly(cache);
		CloseableUtils.closeQuietly(client);
	}

	private void addListener(PathChildrenCache cache) {
		PathChildrenCacheListener listener = (client, event) -> {
			switch (event.getType()) {
				case CHILD_ADDED: {
					System.out.println(servername + " Monitor Node added: " + ZKPaths.getNodeFromPath(event.getData().getPath()));
					break;
				}
				case CHILD_UPDATED: {
					System.out.println(servername + " Monitor Node changed: " + ZKPaths.getNodeFromPath(event.getData().getPath()));
					break;
				}
				case CHILD_REMOVED: {
					System.out.println(servername + " Monitor Node removed: " + ZKPaths.getNodeFromPath(event.getData().getPath()));
					break;
				}
			}
		};
		cache.getListenable().addListener(listener);
	}

	private static void listFromCache(PathChildrenCache cache) {
		if (cache.getCurrentData().size() == 0) {
			System.out.println("* empty *");
		} else {
			for (ChildData data : cache.getCurrentData()) {
				System.out.println(data.getPath() + " = " + new String(data.getData()));
			}
		}
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
			client.create().creatingParentsIfNeeded().forPath(path, data.getBytes());
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