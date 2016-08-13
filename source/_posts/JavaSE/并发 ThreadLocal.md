category: JavaSE
date: 2016-06-07
title: ThreadLocal
---
## 应用
项目中使用了`java.text.SimpleDateFormat`, 但是却将其声明为`static`. 在Oracle的Java API文档中是这样说明的
```bash
Synchronization

Date formats are not synchronized. It is recommended to create separate format instances for each thread. If multiple threads access a format concurrently, it must be synchronized externally.
```
`Date`对象的format操作并不是同步进行的. 我们应该为每个线程都创建一个`SimpleDateFormat`对象, 或者为format操作进行加锁处理.

那么`ThreadLocal`就可以成为这种场景下的替代方案. 我们看一下替换后的代码
```java
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Test {

	private static ThreadLocal<byte[]> simpleDateFormatThreadLocal = new ThreadLocal<>();
	private static AtomicInteger count = new AtomicInteger();

    public static void main(String[] args) throws InterruptedException {
		for (int j = 0; j < 5; j++) {
			for (int i = 0; i < 50; i++) {
				Thread thread = new Thread(() -> {
					byte[] bytes = simpleDateFormatThreadLocal.get();
					if (bytes == null) {
						bytes = new byte[1024 * 1024 * 3];
						simpleDateFormatThreadLocal.set(bytes);
						count.incrementAndGet();
					}
				});
				thread.start();
				thread.join();
			}
			System.out.println("Active Thread Count : " + Thread.activeCount());
	        TimeUnit.MILLISECONDS.sleep(50);
		}
		System.out.println("set count ; " + count);
	}
}
```
我们看到了ThreadLocal的使用很简单, 首先是分配一个ThreadLocal对象, 然后接下来就通关get, set进行操作就ok了

## 原理
下来看一下ThreadLocal的实现(我们不详细剖析源码, 只是大概看一下流程)
```java
public T get() {
        Thread t = Thread.currentThread();
        ThreadLocalMap map = getMap(t);
        if (map != null) {
            ThreadLocalMap.Entry e = map.getEntry(this);
            if (e != null) {
                @SuppressWarnings("unchecked")
                T result = (T)e.value;
                return result;
            }
        }
        return setInitialValue();
    }
	
ThreadLocalMap getMap(Thread t) {
        return t.threadLocals;
    }
	
private Entry getEntry(ThreadLocal<?> key) {
            int i = key.threadLocalHashCode & (table.length - 1);
            Entry e = table[i];
            if (e != null && e.get() == key)
                return e;
            else
                return getEntryAfterMiss(key, i, e);
        }
```
通过上面的源码我们可以看出, 每个和线程相关的数据最终都是保存到了各自的线程对象里, 然后使用`ThreadLocal`作为key存储. 


## 内存溢出
原理我们就简单地说道这里, [网上有人说](http://www.codeceo.com/article/about-threadlocal-memory-leak.html), `ThreadLocal`可能会引起内存泄漏, 于是我使用`-Xmx10M -Xms10M -XX:+PrintGC`这几个JVM参数运行上面程序, 结果为
```bash
[GC (Allocation Failure)  2048K->905K(9728K), 0.0061875 secs]
[GC (Allocation Failure)  7854K->7113K(9728K), 0.0011924 secs]
[GC (Allocation Failure)  7113K->7129K(9728K), 0.0031599 secs]
[Full GC (Allocation Failure)  7129K->873K(9728K), 0.0409086 secs]
[GC (Allocation Failure)  7140K->7081K(9728K), 0.0311850 secs]
[Full GC (Ergonomics)  7081K->1024K(9728K), 0.0355878 secs]
[GC (Allocation Failure)  4150K->4128K(9728K), 0.0130218 secs]
[GC (Allocation Failure)  4128K->4128K(8704K), 0.0009429 secs]
[Full GC (Allocation Failure)  4128K->872K(8704K), 0.0158858 secs]
[GC (Allocation Failure)  7057K->7112K(9216K), 0.0064885 secs]
[Full GC (Ergonomics)  7112K->872K(9216K), 0.0088626 secs]
[GC (Allocation Failure)  7057K->7112K(9216K), 0.0037829 secs]
[Full GC (Ergonomics)  7112K->872K(9216K), 0.0174345 secs]
[GC (Allocation Failure)  7057K->7048K(9216K), 0.0180387 secs]
[Full GC (Ergonomics)  7048K->872K(9216K), 0.0508169 secs]
[GC (Allocation Failure)  7057K->7080K(9216K), 0.0088642 secs]
[Full GC (Ergonomics)  7080K->872K(9216K), 0.0328227 secs]
Active Thread Count : 2

......

[Full GC (Ergonomics)  7050K->874K(9728K), 0.0055843 secs]
[GC (Allocation Failure)  7100K->7050K(9728K), 0.0002894 secs]
[Full GC (Ergonomics)  7050K->874K(9728K), 0.0209747 secs]
[GC (Allocation Failure)  7100K->7114K(9728K), 0.0002878 secs]
[Full GC (Ergonomics)  7114K->874K(9728K), 0.0034925 secs]
[GC (Allocation Failure)  7100K->7082K(9728K), 0.0003256 secs]
[Full GC (Ergonomics)  7082K->874K(9728K), 0.0057511 secs]
[GC (Allocation Failure)  7100K->7082K(9728K), 0.0003378 secs]
[Full GC (Ergonomics)  7082K->874K(9728K), 0.0037080 secs]
[GC (Allocation Failure)  7100K->7050K(9728K), 0.0001761 secs]
[Full GC (Ergonomics)  7050K->874K(9728K), 0.0040176 secs]
[GC (Allocation Failure)  7120K->7050K(9728K), 0.0005024 secs]
[Full GC (Ergonomics)  7050K->874K(9728K), 0.0051379 secs]
[GC (Allocation Failure)  7100K->7082K(9728K), 0.0021276 secs]
[Full GC (Ergonomics)  7082K->874K(9728K), 0.0060047 secs]
Active Thread Count : 2
set count ; 250
```
由于篇幅的原因, 我并没有将全部的日志输出, 但是通过上面的日志我们还是可以看出, 随着线程运行的结束, 分配到线程里的对象也被GC掉了, 因此对`ThreadLocal`的一个简单应用, 只要我们写的线程代码没有问题, 我们并不需要对内存泄漏担心太多.