# CLHSpinLock

CLH锁也是一种基于链表的可扩展、高性能、公平的自旋锁，申请线程只在本地变量上自旋，
它不断轮询前驱的状态，如果发现前驱释放了锁就结束自旋。
 *
差异：
 *
 从代码实现来看，CLH比MCS要简单得多。
 从自旋的条件来看，CLH是在本地变量上自旋，MCS是自旋在其他对象的属性。
 从链表队列来看，CLH的队列是隐式的，CLHNode并不实际持有下一个节点；MCS的队列是物理存在的。
 CLH锁释放时只需要改变自己的属性，MCS锁释放则需要改变后继节点的属性。
 注意：这里实现的锁都是独占的，且不能重入的。

 ```java
 public class CLHSpinLock {
	public static class CLHNode {
		private boolean isLocked = true; // 默认是在等待锁
	}

	@SuppressWarnings("unused")
	private volatile CLHNode tail;
	private static final AtomicReferenceFieldUpdater<CLHSpinLock, CLHNode> UPDATER = AtomicReferenceFieldUpdater
			.newUpdater(CLHSpinLock.class, CLHNode.class, "tail");

	public void lock(CLHNode currentThread) {
		CLHNode preNode = UPDATER.getAndSet(this, currentThread);
		if (preNode != null) {// 已有线程占用了锁，进入自旋
			while (preNode.isLocked) {
			}
		}
	}

	public void unlock(CLHNode currentThread) {
		// 如果队列里只有当前线程，则释放对当前线程的引用（for GC）。
		if (!UPDATER.compareAndSet(this, currentThread, null)) {
			// 还有后续线程
			currentThread.isLocked = false;// 改变状态，让后续线程结束自旋
		}
	}
}
```
