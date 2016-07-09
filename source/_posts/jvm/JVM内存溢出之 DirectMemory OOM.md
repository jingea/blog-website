category: JVM
date: 2014-09-06
title: JVM内存溢出之 DirectMemory OOM
---
溢出代码
```java
	/**
	 * VM Args: -Xmx20M -XX:MaxDirectMemorySize=10M
	 */
	public class DirectMemoryOOM {
	    private static final int _1MB = 1024 * 1024;

	    public static void main(String[] args) throws Exception {
	        Field unsafeField = Unsafe.class.getDeclaredFields()[0];
	        unsafeField.setAccessible(true);
	        Unsafe unsafe = (Unsafe)unsafeField.get(null);
	        while(true)
	            unsafe.allocateMemory(_1MB);
	    }
}
```
直接通过反射获取Unsafe实例并进行内存分配,Unsafe类的getUnsafe()方法限制了只有引导类加载器才会返回实例,也就是设计者希望只有rt.jar中的类才能使用unsafe的功能. 因为虽然使用DirectbyeBuffer分配内存也会抛出内存异常,但抛出异常时并没有真正向操作系统申请分配内存,而是通过计算得知内存无法分配,于是手动抛出异常,真正申请分配内存的方法是:unsafe.allocateMemory(_1MB);
