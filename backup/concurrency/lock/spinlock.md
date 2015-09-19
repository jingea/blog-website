category: 
- lock
title: 自旋锁spinlock
---
# 自旋锁spinlock
自旋锁（Spin lock）
自旋锁是指当一个线程尝试获取某个锁时，如果该锁已被其他线程占用，就一直循环检测锁是否被释放，而不是进入线程挂起或睡眠状态。
自旋锁适用于锁保护的临界区很小的情况，临界区很小的话，锁占用的时间就很短。
SimpleSpinLock里有一个owner属性持有锁当前拥有者的线程的引用，如果该引用为null，则表示锁未被占用，不为null则被占用。
这里用AtomicReference是为了使用它的原子性的compareAndSet方法（CAS操作），
解决了多线程并发操作导致数据不一致的问题，确保其他线程可以看到锁的真实状态。
缺点
CAS操作需要硬件的配合；
保证各个CPU的缓存（L1、L2、L3、跨CPU Socket、主存）的数据一致性，通讯开销很大，在多处理器系统上更严重；
没法保证公平性，不保证等待进程/线程按照FIFO顺序获得锁。

```java
public class Spinlock {
	private AtomicReference<Thread> owner = new AtomicReference<Thread>();

	public void lock() {
		Thread currentThread = Thread.currentThread();

		// 如果锁未被占用，则设置当前线程为锁的拥有者
		while (owner.compareAndSet(null, currentThread)) {
		}
	}

	public void unlock() {
		Thread currentThread = Thread.currentThread();

		// 只有锁的拥有者才能释放锁
		owner.compareAndSet(currentThread, null);
	}
}
```
