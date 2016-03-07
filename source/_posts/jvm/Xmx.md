category: JVM
date: 2014-11-28
title: Xmx参数
---
这个参数设置可用的最大堆内存, 如果我们不使用这个参数,在Mac平台上,默认分配的堆内存为1431M.


测试程序为

```java
public class Test {
	public static void main(String[] main) {
		long maxMemory = Runtime.getRuntime().maxMemory();

		System.out.println(maxMemory / 1000 / 1000 + "M");
	}
}
```


当我们使用上`-Xmx1m`的时候,我们手动地将堆内存设置为1m, 通过刚才的程序输出,确实是最大的内存为1m.  在这个例子中我们还要注意到, 在JVM分配内存时候, 1M = 1000KBM, 而不是1024KBM