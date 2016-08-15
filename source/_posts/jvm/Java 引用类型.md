category: JVM
date: 2016-05-05
title: Java 引用类型
---

JDK1.2之后,java对引用的概念进行了拓充,将引用分为强引用,软引用,弱引用,虚引用
1. 强引用: 指的是在代码之中普遍存在的,类似`Object obj = new Object()` 这类的引用,只要强引用还存在,垃圾收集器永远不会回收掉被引用的对象
2. 软引用: 用来描述一些还有用,但是并非重要的对象.对于软引用关联着的对象,在系统将要发生内存溢出之前,将会把这些对象列进回收范围之中并进行第二次回收.如果这次回收还是没有足够的内存,才会抛出内存溢出异常.
3. 弱饮用: 当垃圾收集器工作时,无论是否内存足够,都将回收掉只被若饮用关联的对象
4. 虚引用: 一个对象是否是有虚引用的存在,完全不会对其生成时间构成影响,也无法通过虚引用来取得一个对象实例.为一个对象设置虚引用关联的唯一目的是希望在其被收集器回收时收到一个系统通知.

## 强引用
JVM在GC的时候并不会释放强引用的堆实例, 因此当堆内GC后仍然不能获得足够的空间, 就会发生OOM
```java
String str = new String("Hi");
```
上面的例子中, 在栈中分配的`str`指向了堆中分配的String实例, 那么`str`引用就是这个实例的强引用.
> 保存在数组和集合中以及Map中的引用都都算是强引用.

## 软引用
JVM在GC时不一定会释放软引用所引用的对象实例, 那什么时候会进行释放呢? 只有当JVM发现堆内存不足时, 才会在GC时将软引用的堆内存释放掉
```java
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;

public class TestSoft {

    public static void main(String[] args) throws InterruptedException {
        ReferenceQueue<User> referenceQueue = new ReferenceQueue<>();
        User user = new User();
        SoftReference<User> softReference = new SoftReference<>(user, referenceQueue);
        user = null;

        Thread t = new Thread(() -> {
            while (true) {
                Reference<? extends User> ref = referenceQueue.poll();
                if (ref != null) {
                    System.out.println("Changed : " + ref);
                    break;
                }
            }
        });

        t.setDaemon(true);
        t.start();

        System.out.println("Before GC : " + " " + softReference.get());

        System.gc();
        System.out.println("After GC : " + softReference.get());

        byte[] array = new byte[1024 * 920 * 7];
        System.out.println("Alocate : " + softReference.get());
    }
}

class User {
    public String name;
}
```
我们指定虚拟机参数`-Xmx10M -Xms10M -XX:PrintGC`, 运行一下这个程序的结果为:
```bash
[GC (Allocation Failure)  2048K->836K(9728K), 0.0023890 secs]
Before GC :  testRef.User@404b9385
[GC (System.gc())  1145K->844K(9728K), 0.0013400 secs]
[Full GC (System.gc())  844K->750K(9728K), 0.0085260 secs]
After GC : testRef.User@404b9385
[GC (Allocation Failure)  788K->782K(9728K), 0.0003760 secs]
[GC (Allocation Failure)  782K->782K(9728K), 0.0002590 secs]
[Full GC (Allocation Failure)  782K->750K(9728K), 0.0043290 secs]
[GC (Allocation Failure)  750K->750K(9728K), 0.0004580 secs]
[Full GC (Allocation Failure)  750K->692K(9728K), 0.0079430 secs]
Changed : java.lang.ref.SoftReference@19366529
Alocate : null
```
我们在构建`SoftReference`实例对象时, 除了添加一个测试对象外, 还添加里一个`ReferenceQueue`实例对象, 当对象的可达状态发生改变时, `SoftReference`就会移动到`ReferenceQueue`队列里. 从最后的Poll  这个输出里我们可以看到, 已经看不到这个对象了.

## 弱引用
弱引用是一种比软饮用更加弱的引用, JVM在GC时只要发现弱引用, 都会对其引用的实例进行回收
```java
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

public class TestSoft {

    public static void main(String[] args) throws InterruptedException {
        ReferenceQueue<User> referenceQueue = new ReferenceQueue<>();
        User user = new User();
        WeakReference<User> softReference = new WeakReference<>(user, referenceQueue);
		// 确定没有强引用
        user = null;

        Thread t = new Thread(() -> {
            while (true) {
                Reference<? extends User> ref = referenceQueue.poll();
                if (ref != null) {
                    System.out.println("Changed : " + ref);
                    break;
                }
            }
        });

        t.setDaemon(true);
        t.start();

        System.out.println("Before GC : " + " " + softReference.get());

        System.gc();
        System.out.println("After GC : " + softReference.get());

        byte[] array = new byte[1024 * 920 * 7];
        System.out.println("Alocate : " + softReference.get());

    }
}

class User {}
```
我们指定虚拟机参数`-Xmx10M -Xms10M -XX:+PrintGC`, 运行一下这个程序的结果为:
```bash
[GC (Allocation Failure)  2048K->800K(9728K), 0.0031060 secs]
Before GC :  null
Changed : java.lang.ref.WeakReference@175fdc70[GC (System.gc())  1084K->824K(9728K), 0.0011480 secs]
[Full GC (System.gc())  824K->748K(9728K), 0.0088060 secs]

After GC : null
[GC (Allocation Failure)  807K->812K(9728K), 0.0010100 secs]
[GC (Allocation Failure)  812K->844K(9728K), 0.0004150 secs]
[Full GC (Allocation Failure)  844K->748K(9728K), 0.0090930 secs]
[GC (Allocation Failure)  748K->748K(9728K), 0.0003230 secs]
[Full GC (Allocation Failure)  748K->690K(9728K), 0.0082600 secs]
Alocate : null
```

如果``是保存在一个对象实例里面是什么情况呢？
```java
import java.lang.ref.WeakReference;

public class TestSoft {
	public static void main(String[] args) throws InterruptedException {
		WeakReferenceCache weakReferenceCache = new WeakReferenceCache();
		weakReferenceCache.cache = new WeakReference<>(new User());
		System.out.println("Before GC : " + weakReferenceCache.cache.get());
		System.gc();
		System.out.println("After GC : " + weakReferenceCache.cache.get());
		byte[] array = new byte[1024 * 920 * 7];
		System.out.println("Alocate GC : " + weakReferenceCache.cache.get());
	}
}

class WeakReferenceCache {
	public WeakReference<User> cache;

}
class User {
}
```
同样我们运行一下看一下结果
```bash
Before GC : User@41629346
After GC : null
Alocate GC : null
```
确实是, 每次GC都将其回收掉了

我们再实验一下, 如果将其存进一个列表里
```java
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class TestSoft {
	public static void main(String[] args) throws InterruptedException {
		WeakReferenceCache weakReferenceCache = new WeakReferenceCache();
		for (int i = 0; i < 10; i++) {
			WeakReference<User> softReference = new WeakReference<>(new User());
			weakReferenceCache.cache.add(softReference);
		}

		System.out.println("Before GC : ");
		weakReferenceCache.cache.forEach(cache -> {
			System.out.println(cache.get());
		});
		System.gc();
		System.out.println("After GC : ");
		weakReferenceCache.cache.forEach(cache -> {
			System.out.println(cache.get());
		});
		byte[] array = new byte[1024 * 920 * 7];
		System.out.println("Alocate GC : ");
		weakReferenceCache.cache.forEach(cache -> {
			System.out.println(cache.get());
		});
	}
}

class WeakReferenceCache {
	public List<WeakReference<User>> cache = new ArrayList<>();

}
class User {
}
```
结果为
```bash
Before GC : 
User@6433a2
User@5910e440
User@6267c3bb
User@533ddba
User@246b179d
User@7a07c5b4
User@26a1ab54
User@3d646c37
User@41cf53f9
User@5a10411
After GC : 
null
null
null
null
null
null
null
null
null
null
Alocate GC : 
null
null
null
null
null
null
null
null
null
null
```
即使是存储在数组里也一样被回收掉了

## 虚引用

虚引用是所有引用类型中最弱的一个, 一个被虚引用持有的对象跟没有被持有的效果基本上是一样的. 当我们从虚引用中get时, 总会获得一个空, 那既然如此还为什么要设计出一个这样的引用呢? 因为虚引用必须跟一个引用队列, 我们可以将一些资源性的东西放到虚引用中执行和记录.
```java
import java.lang.ref.*;

public class TestSoft {

    public static void main(String[] args) throws InterruptedException {
        ReferenceQueue<User> referenceQueue = new ReferenceQueue<>();
        User user = new User();
        PhantomReference<User> softReference = new PhantomReference<>(user, referenceQueue);
        user = null;

        Thread t = new Thread(() -> {
            while (true) {
                Reference<? extends User> ref = referenceQueue.poll();
                if (ref != null) {
                    System.out.println("Changed : " + System.currentTimeMillis());
                    break;
                }
            }
        });

        t.setDaemon(true);
        t.start();

        System.out.println("Before GC : " + System.currentTimeMillis() + " " + softReference.get());

        System.gc();
        System.out.println("After GC : " + softReference.get());

        byte[] array = new byte[1024 * 920 * 7];
        System.out.println("Alocate : " + softReference.get());

    }
}

class User {}
```
我们指定虚拟机参数`-Xmx30M -Xms30M -XX:+PrintGC`, 运行一下这个程序的结果为:
```bash
Before GC : 1462461362835 null
[GC (System.gc())  2806K->904K(29696K), 0.0033390 secs]
[Full GC (System.gc())  904K->779K(29696K), 0.0095950 secs]
Changed : 1462461362850
After GC : null
Alocate : null
```
