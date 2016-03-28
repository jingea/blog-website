category: JavaSE 并发
date: 2016-03-28
title: JavaSE 锁实现
---
# TicketSpinLock

Ticket Lock 是为了解决上面的公平性问题，类似于现实中银行柜台的排队叫号：
锁拥有一个服务号，表示正在服务的线程，还有一个排队号；每个线程尝试获取锁之前先拿一个排队号，
然后不断轮询锁的当前服务号是否是自己的排队号，如果是，则表示自己拥有了锁，不是则继续轮询。

当线程释放锁时，将服务号加1，这样下一个线程看到这个变化，就退出自旋。
Ticket Lock 虽然解决了公平性的问题，但是多处理器系统上，每个进程/线程占用的处理器都在读写同一个变量serviceNum ，
每次读写操作都必须在多个处理器缓存之间进行缓存同步，这会导致繁重的系统总线和内存的流量，大大降低系统整体的性能。

```java
public class TicketSpinLock {
   private AtomicInteger serviceNum = new AtomicInteger(); // 服务号
   private AtomicInteger ticketNum = new AtomicInteger(); // 排队号

   public int lock() {
         // 首先原子性地获得一个排队号
         int myTicketNum = ticketNum.getAndIncrement();

              // 只要当前服务号不是自己的就不断轮询
       while (serviceNum.get() != myTicketNum) {
       }

       return myTicketNum;
    }

    public void unlock(int myTicket) {
        // 只有当前线程拥有者才能释放锁
        int next = myTicket + 1;
        serviceNum.compareAndSet(myTicket, next);
    }
}
```
# CLHSpinLock
CLH锁也是一种基于链表的可扩展、高性能、公平的自旋锁，申请线程只在本地变量上自旋，
它不断轮询前驱的状态，如果发现前驱释放了锁就结束自旋。

差异：
从代码实现来看，CLH比MCS要简单得多。从自旋的条件来看，CLH是在本地变量上自旋，MCS是自旋在其他对象的属性。从链表队列来看，CLH的队列是隐式的，CLHNode并不实际持有下一个节点；MCS的队列是物理存在的。CLH锁释放时只需要改变自己的属性，MCS锁释放则需要改变后继节点的属性。注意：这里实现的锁都是独占的，且不能重入的。

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
# MCSSpinLock
MCS Spinlock 是一种基于链表的可扩展、高性能、公平的自旋锁，申请线程只在本地变量上自旋，直接前驱负责通知其结束自旋，从而极大地减少了不必要的处理器缓存同步的次数，降低了总线和内存的开销。

```java
public class MCSSpinLock {
	public static class MCSNode {
		volatile MCSNode next;
		volatile boolean isLocked = true; // 默认是在等待锁
	}

	volatile MCSNode queue;// 指向最后一个申请锁的MCSNode
	private static final AtomicReferenceFieldUpdater<MCSSpinLock, MCSNode> UPDATER = AtomicReferenceFieldUpdater
			.newUpdater(MCSSpinLock.class, MCSNode.class, "queue");

	public void lock(MCSNode currentThread) {
		MCSNode predecessor = UPDATER.getAndSet(this, currentThread);// step 1
		if (predecessor != null) {
			predecessor.next = currentThread;// step 2

			while (currentThread.isLocked) {// step 3
			}
		}
	}

	public void unlock(MCSNode currentThread) {
		if (UPDATER.get(this) == currentThread) {// 锁拥有者进行释放锁才有意义
			if (currentThread.next == null) {// 检查是否有人排在自己后面
				if (UPDATER.compareAndSet(this, currentThread, null)) {// step 4
					// compareAndSet返回true表示确实没有人排在自己后面
					return;
				} else {
					// 突然有人排在自己后面了，可能还不知道是谁，下面是等待后续者
					// 这里之所以要忙等是因为：step 1执行完后，step 2可能还没执行完
					while (currentThread.next == null) { // step 5
					}
				}
			}

			currentThread.next.isLocked = false;
			currentThread.next = null;// for GC
		}
	}
}

```
# ReadWriteLock
读写锁只有一个实现ReentrantReadWriteLock(可重入读写锁)

读写锁有俩个锁
* 读操作锁,使用读操作锁的时候可以允许多个线程同时访问
* 写操作锁,只允许一个线程运行,即其他线程既不能执行读操作也不能执行写操作


# ReentrantLock

Java提供了同步代码块的另一种机制,它比synchronized关键字更加强大也更加灵活. 这种机制基于Lock接口以及其实现类.
Lock相比Synchronized：
1.更灵活的同步代码块结构.使用synchronized关键字只能在同一个synchronized块结构中获取和释放控制.
Lock接口允许实现更复杂的临界区结构.(例如可以在不同的方法中加锁和释放锁)
2.Lock接口提供了tryLock()方法.这个方法试图获取锁,如果锁已被其他线程获取,它将返回false并继续往下执行代码.
但在使用synchronized关键字时,如果A想要执行一个同步块,但是B恰好在使用她,A就会一直被堵塞到B执行完该同步块
3.Lock接口允许分离读操作和写操作,允许多个读线程和一个写线程
4.而且Lock接口拥有更好的性能

lock(): A尝试获取锁的时候,如果B在执行同步块,A将被锁住,直到B执行完 tryLock():

在本例中我们使用ReentrantLock锁(重入锁)(synchronized本身是支持重入的)

ReentrantLock的实现不仅可以替代隐式的synchronized关键字,而且能够提供超过关键字本身的多种功能。
这里提到一个锁获取的公平性问题,如果在绝对时间上,先对锁进行获取的请求一定被先满足,
那么这个锁是公平的,反之,是不公平的,也就是说等待时间最长的线程最有机会获取锁,也可以说锁的获取是有序的。

ReentrantLock这个锁提供了一个构造函数,能够控制这个锁是否是公平的。
而锁的名字也是说明了这个锁具备了重复进入的可能,也就是说能够让当前线程多次的进行对锁的获取操作,
这样的最大次数限制是Integer.MAX_VALUE,约21亿次左右。

事实上公平的锁机制往往没有非公平的效率高,因为公平的获取锁没有考虑到操作系统对线程的调度因素,
这样造成JVM对于等待中的线程调度次序和操作系统对线程的调度之间的不匹配

对于锁的快速且重复的获取过程中,连续获取的概率是非常高的,而公平锁会压制这种情况,
虽然公平性得以保障,但是响应比却下降了,但是并不是任何场景都是以TPS作为唯一指标的
,因为公平锁能够减少“饥饿”发生的概率,等待越久的请求越是能够得到优先满足。

```java
public class Main {

	public static void main(String args[]) {
		final Lock queueLock = new ReentrantLock();
		List<Integer> list = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			Job job = new Job(queueLock, list);
			job.start();
		}

		while(list.size() < 100){
			System.out.println(list.size());
		}
		System.out.println(list.size());
	}
}

class Job extends Thread {

	Job(Lock queueLock, List<Integer> count) {
		this.count = count;
		this.queueLock = queueLock;
	}
	List<Integer> count;
	Lock queueLock;

	@Override
	public void run() {
		// 使用锁实现一个临界区,并且保证同一时间只有一个执行线程访问这个临界区.
		queueLock.lock();
		try {
			int random = ThreadLocalRandom.current().nextInt(2);
			TimeUnit.SECONDS.sleep(random);
			count.add(random);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally { // 在执行完的最后必须释放锁,否则将
			queueLock.unlock();
		}
	}
}


```
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

修改锁的公平性
---
ReentrantLock和ReentrantReadWriteLock 类的构造器都含有一个布尔参数fair

* fair --> true : 公平模式,当有很多线程在等待锁的时候,锁将选择它们中的一个来访问临界区,
                    选择的是等待时间最长的
* fair --> false: 非公平模式,当有很多线程在等待锁的时候,锁将选择它们中的一个来访问临界区,
                    该选择是没有任何约束的

这俩种模式只适用于lock(),unlock()方法.

Lock接口的tryLock()方法并没有将线程置为睡眠,所以fair属性并不影响这个方法

```java
public class Main {

	public static void main(String args[]) {
		final PrintQueue printQueue = new PrintQueue();

		for (int i = 0; i < 10; i++) {
			Runnable r = new Runnable() {

				@Override
				public void run() {
					System.out.printf("%s:begin print\n", Thread
							.currentThread().getName());
					printQueue.printJob(new Object());
					System.out.printf("%s:print over\n", Thread.currentThread()
							.getName());
				}
			};
			Thread t = new Thread(r);
			t.start();

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}

class PrintQueue {

	private final Lock queueLock = new ReentrantLock(false);

	public void printJob(Object document) {
		queueLock.lock();

		try {
			Long duration = (long) (Math.random() * 10000);
			System.out.printf("%s:Printing a Job during %d seconds\n", Thread
					.currentThread().getName(), (duration / 1000));
			Thread.sleep(duration);

		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			queueLock.unlock();
		}
	}
}
```
