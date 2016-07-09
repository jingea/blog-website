category: JVM
date: 2014-09-06
title: JVM�ڴ����֮ Heap OOM
---
�������
```java
public class TestStackSOF {

	private static int stackLength = 1;
	public static void stackLeak() {
		stackLength ++;
		stackLeak();
	}

	public static void main(String[] args) {
		try {
			stackLeak();
		} catch(Throwable e) {
			System.out.println("stack length:" + stackLength + ". " + e.getMessage());
		}
	}
}
```
��������ĳ���
```bash
D:\testOOM>java -XX:+HeapDumpOnOutOfMemoryError -Xss1M TestStackSOF
stack length:22427. null
```
1M��ջ�ռ�����ִ�������Ǹ��򵥷�����22427��. ��������������ڱ����ھ;�����,����������ʱ���ݾ�����ڴ�ʹ��������仯��. 
���ǻ�ע�⵽ʹ��`-XX:+HeapDumpOnOutOfMemoryError`�����ܲ������ڴ��������, Ҳû�в���������java_pid19212.hprof�ļ����ļ�.

����Ĳ�û�в���
```java
public class JavaVMStackOOM {
	private void dontStop() {
		while(true) {

		}
	}

	public void stackLeakByThread() {
		while(true) {
			Thread t = new Thread(new Runnable(){
				@Override
				public void run() {
					dontStop();
				}
			});
		}
	}

	public static void main(String[] args) {
		JavaVMStackOM om = new JavaVMStackOM();
		om.stackLeakByThread();
	}
}
```
��������ʵ�ֶ����޷������������OutOfMemoryError�쳣,ֻ�ܲ���StackOverflowError.ʵ��������: �����߳���,��������ջ̫֡�������������̫С,���ڴ��޷�����ʱ,������׳��Ķ���StackOverflowError.�������ʱ�������ڵ��߳�,ͨ�����Ͻ������̵߳ķ�ʽ���ǿ��Բ����ڴ�����쳣. ���������������ڴ�����쳣��ջ�ռ��Ƿ��㹻�󲢲������κ���ϵ,����׼ȷ˵,�����������,��ÿ���̵߳�ջ������ڴ�Խ��,����Խ���ײ����ڴ�����쳣.

���������߳�Ӧ��ʱӦ���ر�ע�����,����StackOverflowError�쳣ʱ�д����ջ�����Ķ�,�����˵�Ƚ������ҵ�����.���ʹ�������Ĭ�ϲ���,ջ����ڴ��������´ﵽ1000-2000��ȫû������,���������ķ�������(�����ݹ�),������Ӧ�ù�����,�����������������̵߳��µ��ڴ����,�ڲ��ܼ����߳������߸���64λ������������,��ֻ��ͨ���������Ѻͼ���ջ��������ȡ������߳�.
