category: 算法
date: 2016-10-24
title: 一致性hash 
---
[原理](http://www.cnblogs.com/haippy/archive/2011/12/10/2282943.html)
```java
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestConsistentHash {

	public static void main(String[] args) {
		Cache.INSTANCE.init();
		Random random = new Random(999999999);
		for (int i = 0; i < 10000; i++) {
			long number = random.nextLong();
			Cache.INSTANCE.insert(number + "", number + "");
		}
		System.out.println(Cache.INSTANCE);
	}

	public static class Cache {

		public static final Cache INSTANCE = new Cache();

		private Integer virtualNodeSize = 50;
		private Integer serverNodeSize = 10;
		private static final Integer MIN_HASH = 0;
		private static final long MAX_HASH = (long)(Math.pow(2,32) - 1);

		private Integer serverNodeIndex = 0;

		private static final ExecutorService exec = Executors.newSingleThreadExecutor();

		private Map<Long, VirtualNode> virtualNodes = new TreeMap<>();
		private Map<Integer, ServerNode> serverNodes = new TreeMap<>();

		private Cache() {
		}

		private void init() {
			for (int i = 0; i < serverNodeSize; i++) {
				serverNodes.put(i, new ServerNode());
			}

			System.out.println("ServerNode Number : " + serverNodes.size());

			long step = MAX_HASH / virtualNodeSize;
			long hashCode = 0;
			for (int i = 0; i < virtualNodeSize; i++) {
				VirtualNode virtualNode = new VirtualNode();
				virtualNode.setServerNode(serverNodes.get(serverNodeIndex++));

				hashCode += step;
				virtualNodes.put(hashCode, virtualNode);
				System.out.println("Add VirtualNode : " + hashCode);
			}
			System.out.println("VirtualNode Number : " + virtualNodes.size());
		}

		public void addVirtualNode() {
			exec.submit(() -> {
				virtualNodes.clear();
				++virtualNodeSize;
				init();
			});
		}

		public void removeVirtualNode() {
			exec.submit(() -> {
				virtualNodes.clear();
				--virtualNodeSize;
				init();
			});
		}

		public void addServerNode() {
			exec.submit(() -> {
				virtualNodes.clear();
				serverNodes.clear();
				serverNodeIndex++;
				init();
			});
		}

		public void removeServerNode() {
			exec.submit(() -> {
				virtualNodes.clear();
				serverNodes.clear();
				serverNodeIndex--;
				init();
			});
		}

		public void insert(String key, String value) {
			exec.submit(() -> {
				int hashCode = key.hashCode();
				for (Map.Entry<Long, VirtualNode> entry : virtualNodes.entrySet()) {
					long hashcode = entry.getKey();
					VirtualNode node = entry.getValue();
					if (hashCode >= hashcode ) {
						node.insert(key, value);
					}
				}
			});
		}

		public String toString() {
			StringBuffer stringBuffer = new StringBuffer();
			for (Map.Entry<Integer, ServerNode> entry : serverNodes.entrySet()) {
				int key1 = entry.getKey();
				ServerNode node = entry.getValue();
				stringBuffer.append(key1).append(" : ").append(node.map.keySet().size()).append("\n");
			}
			return stringBuffer.toString();
		}
	}

	/**
	 * 虚拟节点
	 */
	private static class VirtualNode {
		private ServerNode serverNode;

		public void insert(String key, String value) {
			serverNode.insert(key, value);
		}

		public String get(String key) {
			return serverNode.get(key);
		}

		public void setServerNode(ServerNode serverNode) {
			this.serverNode = serverNode;
		}
	}

	/**
	 * 代表真实服务器
	 */
	private static class ServerNode {
		private final Map<String, String> map = new ConcurrentHashMap<>();

		public void insert(String key, String value) {
			map.put(key, value);
		}

		public String get(String key) {
			return map.get(key);
		}
	}
}
```
