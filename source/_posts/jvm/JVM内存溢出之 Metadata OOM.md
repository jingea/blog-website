category: JVM
date: 2014-09-06
title: JVM内存溢出之 Metadata OOM
---

```java
	/**
	 * 借助CGLib使得方法区内存溢出异常
	 * -XX:PermSize10M -XX:MaxPermSize10M
	 * @author mingwang
	 *
	 */
	public class JavaMethodAreaOOM {

		public static void main(String[] args) {
			while(true) {
				Enhancer enhancer = new Enhancer();
				enhancer.setSuperclass(OOMObject.class);
				enhancer.setUseCache(false);
				enhancer.setCallBack(new MethodInterceptor(){
					public Object intercept(Object obj, Method method, Object[] objs,
					MethodProxy proxy) throws Throwable {
						return proxy.invokeSuper(obj, args);
					}
				});
			}
		}

		static class OOMObject {

		}
	}
```
执行代码
```java
	javac JavaMethodAreaOOMRun.java
	java -XX:PermSize10M -XX:MaxPermSize10M JavaMethodAreaOOMRun
pause
```
方法区用于存放Class信息,为了测试这个区域,基本思路是产生大量的类去填充方法区,直到溢出.本例中使用的是CGLib, 还可以使用ASM等框架进行测试.方法区溢出也是一种常见的内存溢出异常.一个类如果被垃圾收集器回收,其条件是非常苛刻的. 在经常动态生成大量Class的应用中,需要特别注意类的回收状况. (基于OSGI的应用即使是同一个类文件被不同的加载器加载也会视为不同的类)


### 本地内存直接溢出
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
