category: jvm
tags: 
	- asm
	- jvm工具
date: 2016-01-12
title: ASM Transforimg
---
[asm4-guide](http://download.forge.objectweb.org/asm/asm4-guide.pdf)学习心得

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


