category: java
tag: 并发编程
title: Peterson
---
算法实现
```
class Peterson {
	private voliate boolean[] flag = new boolean[2];
	private int lock;
	
	public void lock() {
		int tid = ThreadID.get();
		int oid = 1 - tid;
		flag[oid] = true;
		lock = tid;
		
		while(flag[tid] && lock == tid) {}
	}
	
	public void unlock() {
		int tid = ThreadID.get();
		flag[tid] = false;
	}
}
```
同样的我们看看俩个事件互斥执行是什么顺序的(线程A标识符为0,线程B标识符为1)：
```
write_A(flag[1] = true) -> write_A(lock = 0) -> read_A(flag[0] == false) -> read_A(lock == 0) -> CS_A
write_B(flag[0] = true) -> write_B(lock = 1) -> read_B(flag[1] == true) -> read_B(lock == 1) -> CS_B
```
好，首先我们看一下
* `CS_A`先于`CS_B`事件执行的话,那么B线程会进入锁等待. 

`CS_A`和`CS_B`事件并发执行我们分俩种情况分析：
```
write_A(flag[1] = true) -> write_A(lock = 0) -> write_B(flag[0] = true) -> write_B(lock = 1) -> read_A(flag[1] == true) -> read_A(lock == 0) -> read_B(flag[1] == true) -> read_B(lock == 1)
```
同样的A线程事件先于B线程事件,我们看到A线程并没有进入锁等待,而是B线程进入了锁等待
```
write_A(flag[1] = true) -> write_A(lock = 0) -> write_B(flag[0] = true) -> write_B(lock = 1) -> read_B(flag[1] == true) -> read_B(lock == 1) -> read_A(flag[1] == true) -> read_A(lock == 0)
```
我们发现这个锁算法仍然是有问题的.