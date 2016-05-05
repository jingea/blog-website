category: JVM
date: 2016-05-05
title: Java 引用类型
---
在java中引用分为四个级别
* 强引用 : 不会被回收
* 软引用 : 内存不足时回收
* 弱引用 : 发现就回收
* 虚引用 : 用于跟踪对象回收


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
