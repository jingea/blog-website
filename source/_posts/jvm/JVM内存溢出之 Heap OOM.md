category: JVM
date: 2014-09-06
title: JVM�ڴ����֮ Heap OOM
---
�������ȿ�һ���ڴ�����Ĵ���
```java
public class TestHeapOOM {

	public static void main(String[] args) {
		for(int i = 0; i < 10; i ++) {
			System.out.println("Allocate : " + i);
			byte[] bytes = new byte[1324 * 1124 * i * 2];
		} 
	}
}
```

��������������һ��������Ǹ�����
```java
D:\testOOM>java -XX:+HeapDumpOnOutOfMemoryError -XX:+PrintHeapAtGC -Xms10M -Xmx10M -Xmn4M TestHeapOOM
Allocate : 1
Allocate : 2
Allocate : 3
{Heap before GC invocations=1 (full 0):
 PSYoungGen      total 3584K, used 3002K [0x00000000ffc00000, 0x0000000100000000, 0x0000000100000000)
  eden space 3072K, 97% used [0x00000000ffc00000,0x00000000ffeee8c0,0x00000000fff00000)
  from space 512K, 0% used [0x00000000fff80000,0x00000000fff80000,0x0000000100000000)
  to   space 512K, 0% used [0x00000000fff00000,0x00000000fff00000,0x00000000fff80000)
 ParOldGen       total 6144K, used 4096K [0x00000000ff600000, 0x00000000ffc00000, 0x00000000ffc00000)
  object space 6144K, 66% used [0x00000000ff600000,0x00000000ffa00010,0x00000000ffc00000)
 Metaspace       used 2580K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 287K, capacity 386K, committed 512K, reserved 1048576K
Heap after GC invocations=1 (full 0):
 PSYoungGen      total 3584K, used 488K [0x00000000ffc00000, 0x0000000100000000, 0x0000000100000000)
  eden space 3072K, 0% used [0x00000000ffc00000,0x00000000ffc00000,0x00000000fff00000)
  from space 512K, 95% used [0x00000000fff00000,0x00000000fff7a020,0x00000000fff80000)
  to   space 512K, 0% used [0x00000000fff80000,0x00000000fff80000,0x0000000100000000)
 ParOldGen       total 6144K, used 4312K [0x00000000ff600000, 0x00000000ffc00000, 0x00000000ffc00000)
  object space 6144K, 70% used [0x00000000ff600000,0x00000000ffa36020,0x00000000ffc00000)
 Metaspace       used 2580K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 287K, capacity 386K, committed 512K, reserved 1048576K
}
{Heap before GC invocations=2 (full 0):
 PSYoungGen      total 3584K, used 488K [0x00000000ffc00000, 0x0000000100000000, 0x0000000100000000)
  eden space 3072K, 0% used [0x00000000ffc00000,0x00000000ffc00000,0x00000000fff00000)
  from space 512K, 95% used [0x00000000fff00000,0x00000000fff7a020,0x00000000fff80000)
  to   space 512K, 0% used [0x00000000fff80000,0x00000000fff80000,0x0000000100000000)
 ParOldGen       total 6144K, used 4312K [0x00000000ff600000, 0x00000000ffc00000, 0x00000000ffc00000)
  object space 6144K, 70% used [0x00000000ff600000,0x00000000ffa36020,0x00000000ffc00000)
 Metaspace       used 2580K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 287K, capacity 386K, committed 512K, reserved 1048576K
Heap after GC invocations=2 (full 0):
 PSYoungGen      total 3584K, used 488K [0x00000000ffc00000, 0x0000000100000000, 0x0000000100000000)
  eden space 3072K, 0% used [0x00000000ffc00000,0x00000000ffc00000,0x00000000fff00000)
  from space 512K, 95% used [0x00000000fff80000,0x00000000ffffa020,0x0000000100000000)
  to   space 512K, 0% used [0x00000000fff00000,0x00000000fff00000,0x00000000fff80000)
 ParOldGen       total 6144K, used 4312K [0x00000000ff600000, 0x00000000ffc00000, 0x00000000ffc00000)
  object space 6144K, 70% used [0x00000000ff600000,0x00000000ffa36020,0x00000000ffc00000)
 Metaspace       used 2580K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 287K, capacity 386K, committed 512K, reserved 1048576K
}
{Heap before GC invocations=3 (full 1):
 PSYoungGen      total 3584K, used 488K [0x00000000ffc00000, 0x0000000100000000, 0x0000000100000000)
  eden space 3072K, 0% used [0x00000000ffc00000,0x00000000ffc00000,0x00000000fff00000)
  from space 512K, 95% used [0x00000000fff80000,0x00000000ffffa020,0x0000000100000000)
  to   space 512K, 0% used [0x00000000fff00000,0x00000000fff00000,0x00000000fff80000)
 ParOldGen       total 6144K, used 4312K [0x00000000ff600000, 0x00000000ffc00000, 0x00000000ffc00000)
  object space 6144K, 70% used [0x00000000ff600000,0x00000000ffa36020,0x00000000ffc00000)
 Metaspace       used 2580K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 287K, capacity 386K, committed 512K, reserved 1048576K
Heap after GC invocations=3 (full 1):
 PSYoungGen      total 3584K, used 0K [0x00000000ffc00000, 0x0000000100000000, 0x0000000100000000)
  eden space 3072K, 0% used [0x00000000ffc00000,0x00000000ffc00000,0x00000000fff00000)
  from space 512K, 0% used [0x00000000fff80000,0x00000000fff80000,0x0000000100000000)
  to   space 512K, 0% used [0x00000000fff00000,0x00000000fff00000,0x00000000fff80000)
 ParOldGen       total 6144K, used 642K [0x00000000ff600000, 0x00000000ffc00000, 0x00000000ffc00000)
  object space 6144K, 10% used [0x00000000ff600000,0x00000000ff6a08a0,0x00000000ffc00000)
 Metaspace       used 2580K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 287K, capacity 386K, committed 512K, reserved 1048576K
}
{Heap before GC invocations=4 (full 1):
 PSYoungGen      total 3584K, used 0K [0x00000000ffc00000, 0x0000000100000000, 0x0000000100000000)
  eden space 3072K, 0% used [0x00000000ffc00000,0x00000000ffc00000,0x00000000fff00000)
  from space 512K, 0% used [0x00000000fff80000,0x00000000fff80000,0x0000000100000000)
  to   space 512K, 0% used [0x00000000fff00000,0x00000000fff00000,0x00000000fff80000)
 ParOldGen       total 6144K, used 642K [0x00000000ff600000, 0x00000000ffc00000, 0x00000000ffc00000)
  object space 6144K, 10% used [0x00000000ff600000,0x00000000ff6a08a0,0x00000000ffc00000)
 Metaspace       used 2580K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 287K, capacity 386K, committed 512K, reserved 1048576K
Heap after GC invocations=4 (full 1):
 PSYoungGen      total 3584K, used 0K [0x00000000ffc00000, 0x0000000100000000, 0x0000000100000000)
  eden space 3072K, 0% used [0x00000000ffc00000,0x00000000ffc00000,0x00000000fff00000)
  from space 512K, 0% used [0x00000000fff00000,0x00000000fff00000,0x00000000fff80000)
  to   space 512K, 0% used [0x00000000fff80000,0x00000000fff80000,0x0000000100000000)
 ParOldGen       total 6144K, used 642K [0x00000000ff600000, 0x00000000ffc00000, 0x00000000ffc00000)
  object space 6144K, 10% used [0x00000000ff600000,0x00000000ff6a08a0,0x00000000ffc00000)
 Metaspace       used 2580K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 287K, capacity 386K, committed 512K, reserved 1048576K
}
{Heap before GC invocations=5 (full 2):
 PSYoungGen      total 3584K, used 0K [0x00000000ffc00000, 0x0000000100000000, 0x0000000100000000)
  eden space 3072K, 0% used [0x00000000ffc00000,0x00000000ffc00000,0x00000000fff00000)
  from space 512K, 0% used [0x00000000fff00000,0x00000000fff00000,0x00000000fff80000)
  to   space 512K, 0% used [0x00000000fff80000,0x00000000fff80000,0x0000000100000000)
 ParOldGen       total 6144K, used 642K [0x00000000ff600000, 0x00000000ffc00000, 0x00000000ffc00000)
  object space 6144K, 10% used [0x00000000ff600000,0x00000000ff6a08a0,0x00000000ffc00000)
 Metaspace       used 2580K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 287K, capacity 386K, committed 512K, reserved 1048576K
Heap after GC invocations=5 (full 2):
 PSYoungGen      total 3584K, used 0K [0x00000000ffc00000, 0x0000000100000000, 0x0000000100000000)
  eden space 3072K, 0% used [0x00000000ffc00000,0x00000000ffc00000,0x00000000fff00000)
  from space 512K, 0% used [0x00000000fff00000,0x00000000fff00000,0x00000000fff80000)
  to   space 512K, 0% used [0x00000000fff80000,0x00000000fff80000,0x0000000100000000)
 ParOldGen       total 6144K, used 630K [0x00000000ff600000, 0x00000000ffc00000, 0x00000000ffc00000)
  object space 6144K, 10% used [0x00000000ff600000,0x00000000ff69d8d0,0x00000000ffc00000)
 Metaspace       used 2580K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 287K, capacity 386K, committed 512K, reserved 1048576K
}
java.lang.OutOfMemoryError: Java heap space
Dumping heap to java_pid17676.hprof ...
Heap dump file created [1336934 bytes in 0.006 secs]
Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
	at TestHeapOOM.main(TestHeapOOM.java:6)

D:\testOOM>
```
����������һ��, ���ǹ̶����ڴ��СΪ10M, ������Ϊ4M. ���ǿ���PSYoungGen�ܹ�Ϊ3584K, �ֱ�Ϊeden��3072K, from survivor��Ϊ512K, Ȼ�����to survivor ����512K, �ܹ�Ϊ4096K. ������ܹ�Ϊ6114K

��������һ�µ�һ��GC֮ǰ���ڴ�ֲ����: ��ʱ�����Ѿ������������ڴ����, �ڵ����ε�(����6M 6114K��byte����)ʱ�򴥷���GC. ��ʱ ������Ϊ3002K, �����Ϊ4096K, ���ǿ����ƶϳ�����һ��2M��byte����Ӧ���Ƿ�������������, ���ڶ��ε�4M byte����ֱ�ӷ������������.

������һ��GC֮��, ������ʹ����488K, �������������4312K. ����һ��GC֮��������������`3002 - 488 -(4312 - 4096) = 2298`, ���ǿ����ƶϳ���һ�η������2M��byte���鱻���յ���.

�������ֽ�����һ��yong GC�����ڴ沢û�з���ʲô�仯,���Ǿͷ�����һ��full GC.

��һ��full GC(Ҳ����invocations=3��ʱ��)֮��, ���ǿ����������������, �����Ҳֻʣ����642K���ڴ汻ʹ����, �����ƶ�Ӧ�����Ǹ�4M��byte���鱻���յ���. ���Ǵ�ʱҪ����һ��6M��byte����,��Ȼ������ǲ�����. ���������Full GC��ʱ���ֽ�����һ��GC����, �����ڴ���Ȼ����, �����ֲ�����һ��Full GC, Ҳ����full=2���Ǵ�. ���Ǻܱ���, �ڴ���Ȼ�ǲ����õ�, ���ǾͿ�����java.lang.OutOfMemoryError: Java heap space, ͬʱ������һ��java_pid17676.hprof ���ļ�.

����*.hprof�ļ������ǿ���ͨ�����й��߷�����
* Eclipse Memory Analyzer
* JProfiler
* jvisualvm
* jhat

�������Ǵ������GC��־�з������������ڴ������ԭ��, Ҳ�Ͳ���ʹ�����еĹ��߷���*.hprof�ļ���,���Ƕ��ڸ��ӵ�Ӧ�ó�����˵,��������˶��ڴ�����Ļ�, ʹ�����й��߷����Ļ�,���Ƿǳ��б�Ҫ��.

�ڷ�������ļ���ʱ��,�����ص�ȷ���ڴ��еĶ����Ƿ��Ǳ�Ҫ��,Ҳ����Ū������������ڴ�й©�����ڴ����.
1. ������ڴ�й©��ͨ�����߲鿴й©����GC Roots��������.���Ǿ����ҵ�й©������ͨ��������·����GC Toots�����,�����������ռ����޷��Զ��������ǵ�. ������й©�����������Ϣ,�Լ�GC Roots��������Ϣ,�Ϳ��ԱȽ�׼ȷ�ض�λ��й©�����λ��.
2. ���������й©, ���仰˵�����ڴ��еĶ���ȷʵ������������, �Ǿ�Ӧ�����������ĶѲ���,��������ڴ�ԱȲ鿴�Ƿ񻹿��Ե���,�Ӵ����ϼ���Ƿ����ĳЩ�������ڹ���,����״̬ʱ����������,���Լ��ٳ����������ڵ��ڴ�����.

