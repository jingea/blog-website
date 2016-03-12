category: JVM
date: 2014-11-28
title: Xss参数
---
这个参数用来分配线程最大的栈空间, 这个参数决定了方法调用的最大的次数. 如果我们不使用这个参数,在mac平台上大概能进行16000个方法调用. 这个参数可设置的最小值为160k. 当我们设置上160k的时候, 程序能调用818次的方法调用

```java
public class Test {
	private static long count = 0;
	public static void main(String[] main) {
		try {
			invokeSelf();
		} catch (Throwable e) {
			System.out.println("invoke:" + count);
			e.printStackTrace();
		}
	}

	public static void invokeSelf() {
		count++;
		invokeSelf();
	}
}
```


我们知道在java的方法调用就是在栈帧不断地在java栈中出栈入栈, 因此栈帧的大小也影响着方法的调用次数.  栈帧是由局部变量表,操作数栈,和帧数据几个部分组成.


下面我们测试一下将上例的递归函数加入一个参数,看看调用次数


```java

public class Test {
	private static long count = 0;
	public static void main(String[] main) {
		try {
			invokeSelf(count);
		} catch (Throwable e) {
			System.out.println("invoke:" + count);
			e.printStackTrace();
		}
	}

	public static void invokeSelf(long times) {
		count++;
		invokeSelf(times);
	}
}
```

这次的调用次数成了682次,说明局部变量表确实是在影响方法调用的次数.

```java
public class Test {
	private static long count = 0;
	public static void main(String[] main) {
		try {
			invokeSelf(count);
		} catch (Throwable e) {
			System.out.println("invoke:" + count);
			e.printStackTrace();
		}
	}

	public static void invokeSelf(long times) {
		count++;
		int newCount = (int)count;
		invokeSelf(times);
	}
}
```
这次我添加了一个中间过程,调用次数变成了629


本想这个次数是针对一个线程的设置还是个共享值, 但是在多线程的情况下,明显的出现了难以预料的结果,他的调用次数有了明显的提升
```java
public class Test {

    private static long count = 0;
    public static void main(String[] main) {

        Runnable runnable = () -> {
            try {
                invokeSelf(count);
            } catch (Throwable e) {
                System.out.println("invoke : " + count);
    //            e.printStackTrace();
            }

        };

        Runnable runnable2 = () -> {
            try {
                invokeSelf2(count);
            } catch (Throwable e) {
                System.out.println("invoke : " + count);
                //            e.printStackTrace();
            }

        };

        Thread t = new Thread(runnable);
        Thread t2 = new Thread(runnable2);
        t.start();
        t2.start();
    }

    public static void invokeSelf(long times) {
        count++;
        int newCount = (int)count;
        invokeSelf(times);
    }

    public static void invokeSelf2(long times) {
        count++;
        int newCount = (int)count;
        invokeSelf2(times);
    }

}
```
这个很奇怪,
结果为
```java
invoke : 1083
invoke : 1083
```
或者
```java
invoke : 650
invoke : 1300
```
要不然次数是一样的,要不然次数是个倍数的关系
