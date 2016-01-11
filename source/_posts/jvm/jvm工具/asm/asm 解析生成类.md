category: jvm
tags: 
	- asm
	- jvm工具
date: 2016-01-11
title: ASM 解析生成类
---
[asm4-guide](http://download.forge.objectweb.org/asm/asm4-guide.pdf)学习心得

## 解析class
下来我们给出一个示例, 这个例子仅仅用来打印class信息
```java
class ClassPrinter extends ClassVisitor {
	public ClassPrinter() {
		super(Opcodes.ASM4);
	}
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		System.out.println(name + " extends " + superName + " {");
	}
	public void visitSource(String source, String debug) {
	}
	public void visitOuterClass(String owner, String name, String desc) {
	}
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		return null;
	}
	public void visitAttribute(Attribute attr) {
	}
	public void visitInnerClass(String name, String outerName, String innerName, int access) {
	}
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		System.out.println(" " + desc + " " + name);
		return null;
	}
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		System.out.println(" " + name + desc);
		return null;
	}
	public void visitEnd() {
		System.out.println("}");
	}
}
```
然后我们写一段运行代码
```java
public class Test {
	public static void main(String[] args) throws IOException {
		ClassReader cr = new ClassReader("Test");
		ClassPrinter cp = new ClassPrinter();
		cr.accept(cp, 0);
	}
}
```
结果为
```
Test extends java/lang/Object {
 <init>()V
 main([Ljava/lang/String;)V
 lambda$main$22(Ljava/lang/Integer;)V
 lambda$main$21(Ljava/lang/Integer;Ljava/lang/Integer;)I
}
```
我们首先创建了一个`ClassReader`实例用于读取`Test`字节码. 然后由`accept()`方法依次调用`ClassPrinter`的方法


## 生成class
我们仅仅使用`ClassWriter`就可以生成一个类, 例如我们要生成一个如下的接口
```java
package pkg;
public interface Comparable extends Mesurable {
	int LESS = -1;
	int EQUAL = 0;
	int GREATER = 1;
	int compareTo(Object o);
}
```
我们仅仅需要调用`ClassVisitor`的六个方法
```java
public class Test {
	public static void main(String[] args) throws IOException {
		ClassWriter cw = new ClassWriter(0);
		cw.visit(V1_8,											// 指定class文件版本号, 我们将其设置为java8
				ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE,	// 设置接口的修饰符, 需要指出的是由于interface是不可实例化的,
																// 因此我们将其设置为ACC_ABSTRACT的
				"pkg/Comparable",								// 我们设置classname, 需要在这里指定全限定名
				null,											// 设置泛型信息, 因为我们的接口是非泛化的, 因此我们将其设置为null
				"java/lang/Object",							// 设置父类, 同时需要设定全限定名
				new String[] { "pkg/Mesurable" });			// 设置接口, 同样需要设置全限定名

		cw.visitField(
				ACC_PUBLIC + ACC_FINAL + ACC_STATIC,	// 设置字段的修饰符
				"LESS",										// 设置字段名
				"I",										// 设置字段类型
				null,										// 设置泛型信息
				new Integer(-1))							// 设置字面量值. (如果这个字段是常量值的话,例如 final static,
																					// 那么我们就必须设置这个值)
				.visitEnd();

		cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT,		// 设置字段的修饰符
				"compareTo",								// 设置方法名
				"(Ljava/lang/Object;)I",					// 设置返回值类型
				null,										// 设置泛型信息
				null)										// 设置异常信息
				.visitEnd();

		cw.visitEnd();
		byte[] b = cw.toByteArray();
	}
}
```

## 使用生成的类
记下来我们自定义一个`ClassLoader`来加载生成的字节码
```java
class MyClassLoader extends ClassLoader {
	public Class defineClass(String name, byte[] b) {
		return defineClass(name, b, 0, b.length);
	}
}
```
然后使用它
```java
byte[] bytes = genComparableInterface();
MyClassLoader myClassLoader = new MyClassLoader();
Class c = myClassLoader.defineClass("pkg.Comparable", bytes);
```
我们直接使用`defineClass`函数来加载这个类.

另外我们还可以重写`findClass`这个函数来动态的生成我们所需要的类
```java
class StubClassLoader extends ClassLoader {
	@Override
	protected Class findClass(String name) throws ClassNotFoundException {
		if (name.endsWith("_Stub")) {
			ClassWriter cw = new ClassWriter(0);
			...
			byte[] b = cw.toByteArray();
			return defineClass(name, b, 0, b.length);
		}
		return super.findClass(name);
	}
}
```

