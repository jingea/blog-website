category: asm
date: 2016-01-11
title: ASM Core(2) 操作
---
[asm4-guide](http://download.forge.objectweb.org/asm/asm4-guide.pdf)学习心得

## 解析class
下来的示例中我们通过重写`ClassVisitor`相关函数然后依次打印出类型信息, 字段信息和函数信息.
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
		// 读取解析二进制字节流
		ClassReader cr = new ClassReader("Test");
		ClassPrinter cp = new ClassPrinter();
		// 开始处理字节流信息
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
在测试代码中我们首先创建了一个`ClassReader`实例用于读取`Test`字节码. 然后由`accept()`方法依次调用`ClassPrinter`的方法


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

## 修改
在上篇文章中我们只是单独的使用了`ClassReader`和`ClassWriter`,但是更多的应用其实应该是将其组合到一起使用
```java
byte[] b1 = ...;
ClassWriter cw = new ClassWriter(0);
ClassReader cr = new ClassReader(b1);
cr.accept(cw, 0);
byte[] b2 = cw.toByteArray(); // b2 represents the same class as b1
```
这个例子中我们什么都没有做, 只不过完成了一个copy字节码的功能, 接下来我们在这俩个过程中加入`ClassVisitor`
```java
byte[] b1 = ...;
ClassWriter cw = new ClassWriter(0);
// cv forwards all events to cw
ClassVisitor cv = new ClassVisitor(ASM4, cw) { };
ClassReader cr = new ClassReader(b1);
cr.accept(cv, 0);
byte[] b2 = cw.toByteArray(); // b2 represents the same class as b1
```
这段代码的处理流程如下图![](https://raw.githubusercontent.com/ming15/blog-website/images/asm/transformation%20chain.jpg)
> 方框代表我们的核心组件, 箭头代表我们的数据流.

下面我们给出一个`ClassVisitor`小例子
```java
class ChangeVersionAdapter extends ClassVisitor {
	public ChangeVersionAdapter(ClassVisitor cv) {
		// ASM4为ASM的版本号
		super(ASM4, cv);
	}
	@Override
	public void visit(int version, int access, String name,
					  String signature, String superName, String[] interfaces) {
		// 修改class信息
		cv.visit(V1_5,			// 改变class的版本号
				access,			// 改变class的标识符
				name,			// 改变类名
				signature,		// 泛型信息
				superName,		// 父类信息
				interfaces);	// 接口信息
	}
}
```
在上面的实现中,除了调用`visit`函数(修改类本身函数, 将class版本号转化为1.5), 其他的方法都没有重写,因此他们什么改变都不会做. 下来我们给出这个类执行的时序图
![](https://raw.githubusercontent.com/ming15/blog-website/images/asm/Sequence%20diagram%20for%20the%20ChangeVersionAdapter.jpg)
从这个时序图中我们可以看出, 用户调用了`accept`方法之后, 有ASM自动调用`ClassReader`的`visti(version)`方法, 接着调用`ChangeVersionAdapter`的`visti(1.5)`方法, 最后调用`ClassWriter`的相关方法. 从这个模式中我们可以看出, ASM的调用模式是链式调用的, 先调用visit, 然后调用责任链中所有的`ClassVisitor`的vist最后调用`ClassWriter`的完结方法. 当`visit`调用完之后再调用`visitSource`责任链流程, 依次类推下去.

## 优化
在上述的代码中, 其实代码的运行效率并不是高效进行的. 这是因为当`b1`字节码被`ClassReader`读取并通过`ClassVisitor`将其执行转换的时候, 我们可能只改变了class的版本号, 其他部分并没有转换, 但是在实际的执行中其他的部分也都被执行了一边, 那这就浪费了cpu计算和内存空间的占用, 其实只需要将不需要改变的字节从`b1`直接拷贝到`b2`就好了.  

好在ASM为我们内部构建了这种优化过程.
*


## 删除成员
如果我们想将class中的某个成员删除掉, 那么只需在执行asm责任链调用时, 中断调用过程(不调用super或者直接return)就可以了.

例如我们下面的例子我们将类中的内部类和外部类以及编译成该class的源文件信息删除掉
```java
class RemoveDebugAdapter extends ClassVisitor {
	public RemoveDebugAdapter(ClassVisitor cv) {
		super(ASM4, cv);
	}
	@Override
	public void visitSource(String source, String debug) {
	}
	@Override
	public void visitOuterClass(String owner, String name, String desc) {
	}
	@Override
	public void visitInnerClass(String name, String outerName, String innerName, int access) {
	}
}
```
看,就是如此简单, 我们在这三个方法内部什么都不做(不进行super调用)就轻松地完成了我们需要的功能, 但是这种做法却并不适合
字段和方法的删除, 因为在字段和方法的删除中除了不进行super调用之外还需要return null, 如下:
```java
class RemoveMethodAdapter extends ClassVisitor {
	private String mName;
	private String mDesc;
	public RemoveMethodAdapter(
			ClassVisitor cv, String mName, String mDesc) {
		super(ASM4, cv);
		this.mName = mName;
		this.mDesc = mDesc;
	}
	@Override
	public MethodVisitor visitMethod(int access, String name,
									 String desc, String signature, String[] exceptions) {
		if (name.equals(mName) && desc.equals(mDesc)) {
			// do not delegate to next visitor -> this removes the method
			return null;
		}
		return cv.visitMethod(access, name, desc, signature, exceptions);
	}
}
```

## 添加成员
当我们中断方法调用的时候,会删除成员. 但是当我们在责任链中的原生方法调用(`visitXxx`方法)中新增加一些方法调用的话, 会增加成员.

例如如果你想要增加一个字段, 那么你必须在`visitXxx`方法中增加一个`visitField`方法调用. 需要注意的是`visitXxx`方法只包含`visitInnerClass,visitField, visitMethod,visitEnd`这四个方法, 这是因为`visit,visitSource,visitOuterClass,visitAnnotation,visitAttribute` 这些方法正如我们在第一篇文章中给出那些顺序一样, `visitField`方法只能在这些方法之后调用.

> 需要注意的是,由于`visitInnerClass,visitField, visitMethod`这些方法会进行多次调用, 因此有可能会添加N个相同的成员, 因此我们建议在`visitEnd`的时候进行成员添加, 这是因为这个方法总会有且只有一次调用.

如下例
```java
class AddFieldAdapter extends ClassVisitor {
	private int fAcc;
	private String fName;
	private String fDesc;
	private boolean isFieldPresent;
	public AddFieldAdapter(ClassVisitor cv, int fAcc, String fName, String fDesc) {
		super(ASM4, cv);
		this.fAcc = fAcc;
		this.fName = fName;
		this.fDesc = fDesc;
	}
	@Override
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		if (name.equals(fName)) {
			isFieldPresent = true;
		}
		return cv.visitField(access, name, desc, signature, value);
	}
	@Override
	public void visitEnd() {
		if (!isFieldPresent) {
			FieldVisitor fv = cv.visitField(fAcc, fName, fDesc, null, null);
			if (fv != null) {
				fv.visitEnd();
			}
		}
		cv.visitEnd();
	}
}
```
