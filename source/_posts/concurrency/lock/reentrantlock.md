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
