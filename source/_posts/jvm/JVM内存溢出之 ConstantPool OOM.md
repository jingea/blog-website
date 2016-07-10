category: JVM
date: 2014-09-06
title: JVM内存溢出之 ConstantPool OOM
---

溢出代码
```java
/**
 * 运行时常量溢出
 * VM Args: -XX:PermSize=10M -XX:MaxPermSize=10M
 * @author mingwang
 *
 */
public class RuntimeConstantPoolOOM {

	public static void main(String[] args) {
		List<String> list = new ArrayList<>();
		int i = 0;
		while(true) {
			list.add(String.valueOf(i++).intern());
		}
	}
}
```
如果想运行时常量池添加内容最简单的方式就是String.intern()这个native方法.该方法的作用是:如果池中已经包含一个等于此String对象的字符串,则返回池中这个字符串的String对象.否则将次String对象包含的字符串添加到常量池中,并返回次String对象音乐.
