category: java
tag: 并发编程
title: LockOne
---
双线程LockOne锁算法
```java
class LockOne {
	private volatile boolean[] flags = new boolean[2];
	public void lock() {
		int i = ThreadID.get();
		int j = 1- i;
		flag[i] = true;
		while(flag[j]) {}		

	}

	public void unlock() {
		int i = ThreadID.get();
		flag[i] = false;
	}
}
```

双线程算法遵循以下俩点约定:
1. 线程标志为`0`或者`1`. 若当前线程调用者的标志为i,则另一方的调用者为1 - i
2. 通过ThreadId.get()获取自己的标志

假设线程A对应flag[0]标志,线程B对应flag[1]标志,那么我们得出下面这一个流程:
1. `write_A(flag[0] = true) -> read_A(flag[1] == false) -> CS_A`这段话的意思是线程A将`flag[0]`的值置为true然后读取`flag[1]`的值,这个过程称为`CS_A`事件
2. `write_B(flag[1] = true) -> read_B(flag[0] == false) -> CS_B`这段话的意思是线程B将`flag[1]`的值置为true然后读取`flag[0]`的值,这个过程称为`CS_B`事件

如果CS_A与CS_B事件是互斥的,也就是一个事件先于另一个事件发生的话,那么这个算法是ok的.

但是如果`write_A(flag[0] = true)`和`write_B(flag[1] = true)`先于`read_A(flag[1] == false)`和`read_B(flag[0] == false)`的话,那么`flag[0]`和`flag[1]`就都成为true,也就是线程A和线程B进入了死锁.


至于说为什么要使用`volatile`关键字,这是为了保证`flags`变量的内存可见性,因为Java会将这段代码
```java
while(flag[j]) {}	
```
编译成
```java
if(flag[j]) {
	while(true) {

	}
}
```
编译后的代码进行了重排序,加上`volatile`关键字,就是告诉编译器,不要重排序我的代码.