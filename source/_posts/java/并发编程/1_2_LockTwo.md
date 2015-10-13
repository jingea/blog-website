category: java
tag: 并发编程
title: LockTwo
---
算法实现：
```java
class LockTwo {
	private int lock;
	public void lock() {
		int tid = ThreadID.get();
		lock = tid;
		while(lock == tid){}
	}
	
	public void unlock() {
		int tid = ThreadID.get();
		lock = tid;
		
	}
}
```

同样我们假设有俩个事件发生
1. `write_A(lock = 1) -> read_A(lock == 1) -> CS_A`
2. `write_B(lock = 2) -> read_B(lock == 2) -> CS_B`

当CS_A和CS_B顺序执行的时候,任何一个事件都会发生死锁.但是如果事件并发执行:
```
write_A(lock = 1) -> write_B(lock = 2) -> read_A(lock == 2)
```
因此只要这俩个事件并发执行就能完成互斥要求.