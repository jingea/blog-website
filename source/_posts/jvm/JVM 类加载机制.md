category: JVM
date: 2014-09-09
title: 类加载机制
---
### 类的生命周期
类从被加载进虚拟机内存开始到卸载出内存的生命周期:

1. 加载
2. 验证
3. 准备
4. 解析
5. 初始化
6. 使用
7. 卸载

> 特殊说明
> 2.验证, 3.准备, 4.解析 又称为连接阶段
> 1.加载, 2.验证, 3.准备, 4.解析, 5. 初始化 被称为类加载


### 加载
加载的过程就是JVM读取class文件, 然后将其转换成方法区的数据结构存储在方法区中, 接着在JVM堆中对其实例化一个`java.lang.Class`对象, 然后应用程序就可以通过该Class对象访问方法区中的数据了。

整个类的加载过程如下
1. 通过类的全限定名来获取此类的二进制流.
2. 将这个字节流所代表的静态存储结构转化为方法区的运行时结构
3. 在java堆中生成一个代表这个类的`java.class.Class`对象.

在上面的三个过程中, 唯一开发人员可控的就是如何实现加载过程, 也就是从哪里获取以及如何获取类的字节码. 一般情况而言我们都是使用系统提供的类加载器, 或者自己实现一个加载器. 实现参考[使用Classloader加载类](http://www.ming15.wang/2016/01/29/jvm/使用Classloader加载类/)

> 我们还可以使用[Class.forName(..)]() 为我们加载类

### 验证
这个阶段主要是用来检测java文件编译后的class文件是符合虚拟机规范的.

如果我们在java源码中将一个对象强转为一个没有被定义的类, 那么javac在编译的时候, 会给我们提示编译错误. 但是我们可以通过ASM这类的工具修改一个class字节流达到刚才我们描述的那个非法的功能. 这样子一来如果不对加载进虚拟机的字节码流做校验的话, 很可能会对虚拟机产生很大的危害, 因此验证阶段就是做这个事情用的.下面我们看一下校验过程:

首先是class文件格式验证, 保证输入的字节流能正确地解析并存储于方法区之内.
1. 是否以魔术0xCAFEBABY 开头
2. 主次版本号是否在当前虚拟机处理范围内.
3. 常量池中是否有不被支持的常量类型(检查常量tag标志)
4. ... 还有很多其他校验

然后是基于方法区的数据结构进行元数据验证, 基本上就是在检验数据类型
1. 这个类是否是父类.
2. 这个类是否继承了不允许继承的类(被final修饰的类)
3. 如果这个类不是抽象类,是否实现了其父类或接口中所要求实现的所有方法
4. ... 还有很多其他校验

紧接着同样基于方法区的数据结构对方法体进行验证.(主要是针对数据流和控制流进行分析.)
1. 保证任意时刻操作数栈的数据类型与指令代码序列都能配合工作.例如操作数栈放置一个int类型的数据,不会按照long类型加载到本地变量表.
2. 保证跳转指令不会跳转到方法体以外的字节码指令上
3. ...  还有很多其他校验

最后是符号引用验证.符号校验可以看作是对类自身以外(常量池中的各种符号引用)的信息进行匹配性的校验
1. 符号引用通过字符串描述的全限定名是否能找到对应的类
2. 在指定类中是否存在符号方法的字段描述及简单名称所描述的方法和字段
3. ... 还有很多其他的校验
> 符号引用的校验是确保解析动作能正常执行.最后一个阶段校验发生在虚拟机将符号引用转化为直接引用的时候,这个转化动作将在连接的第三阶段-解析阶段中发生

### 准备
准备阶段为类变量在方法区中进行内存分配以及初始值设置. 我们看下面的一个类变量value
```java
public static int value = 123;
```
value在准备阶段初始值为0而不是123.因为把value赋值为123的putstatic指令是在类构造器`<clinit>()`方法中执行的,而类构造器只有在初始化阶段才会执行.
> 但是在一些特殊情况下,如果类字段的字段属性表中存在ConstantValue属性,那么在准备阶段value值就会被初始化为ConstantValue指定的属性值.

### 解析

解析阶段是虚拟机将常量池符号引用替换为直接引用的过程(符号引用以CONSTANT_Class_info,CONSTANT_Field_info等类型常量)

1. 符号引用: 以一组符号来描述所引用的目标,符号可以是任何形式的字面量,只要使用时能无歧义地定位到目标即可.符号引用与内存实现的布局无关,引用的目标不一定已经加载到内存中.
2. 直接引用:可以是直接指向目标的指针,相对偏移量或是一个能间接定位到目标的句柄.直接引用是与虚拟机实现的内存布局相关的,同一个符号引用在不同的虚拟机实例上翻译出来的直接引用一般不会相同.如果有了直接引用,那引用的目标一定已经在内存中存在.

虚拟机只规定了在`anewarray,checkcast,getfield,getstatic,instanceof,invokeinterface,invokespecial,invokestatic,invokevirtual,mutianewarray,new,putfield,putstatic`这13个用于操作符号引用的字节码指令之前,先对他们所使用的符号引用进行解析. 因此有可能是在类被加载时进行解析，也有可能是该符号被使用的时候才会被解析，这个要看具体的虚拟机实现。但是不管是采用哪种方式，都可能发生多次解析的现象，因此虚拟机对此有一个缓存策略，只要一次解析成功，那么后续再接受到解析请求时就会从缓存里面拿，如果第一次解析失败，则以后的解析请求也是失败的

####　CONSTANT_Class_info结构体解析
假设类D引用了一个符号引用N, 第一次将符号引用N解析为类或接口C的直接引用时需要以下步骤
1. 如果C不是一个数组类型,D的类加载器会根据N的全限定名将类C加载进虚拟机. 加载过程中由于上述验证步骤的需要,又可能触发加载C类的父类或实现的接口.一旦这个加载过程出现了任何异常,解析过程将失败.
2. 如果C是一个数组类型,并且数组的元素类型为对象,也就是N的描述符会是类似"[Ljava.lang.Integer"的形式.那将会按照第一点的规则加载数组元素类型,如果N的描述符如前面所假设的形式,需要加载的元素类型就是"java.lang.Integer",接着由虚拟机生成一个代表此数组维度和元素的数组对象
3. 如果上述步骤没有出现任何异常,那么C在虚拟机中实际已经称为一个有效的类或接口了,但在解析完成之前还要进行符号引用验证,确认C是否具备对D的访问权限,如果不具备访问权限,抛出"java.lang.IllegalAccessError"异常

#### CONSTANT_Fieldref_info结构体解析 要解析一个从未被解析过的字段符号引用,首先会对字段表内class_index项中索引的CONSTANT_Class_info符号引用进行解析,也就是字段所属的类或接口的符号引用.如果在解析这个类或接口符号引用的过程中出现了任何异常,都会导致字段解析失败,如果解析成功,那将这个字段所属的类或接口用C表示.

1. 如果C本身就包含了简单名称和字段描述符都与目标相匹配的字段,则返回了这个字段的直接引用,查找结束
2. 否则,如果在C中实现了接口,将会按照继承关系从上往下递归搜索各个接口和它的父接口,如果接口中包含了简单名称和字段描述符都与目标相匹配的字段,则返回这个字段的直接引用,查找结束.
3. 否则,如果C不是java.lang.Object的话,将会按照继承关系从上往下递归搜索其父类,如果父类中不包含了简单名称和字段描述符都与目标相匹配的字段,则返回这个字段的直接引用,查找结束.
4. 否则,查找失败,抛出java.lang.NoSuchFieldError异常

如果查找过程成功返回了引用,将会对这个字段进行权限验证,如果发现不具备对其字段的访问权限,则抛出"java.lang.IllegalAccessError"异常.尝试在父类和子类中都出现相同的字段,看看编译器是否会编译

#### CONSTANT_Methodref_info结构体解析
类方法解析的第一个步骤与字段解析一样,也是需要解析类方法表的class_index项中索引的方法所属的类或接口的符号引用,如果解析成功,依然使用C表示这个类.

1. 类方法和接口方法符号引用的常量类型定义是分开的,如果在类方法表中发现class_index中索引的C是个接口,那就直接抛出java,lang.IncompatibleClassChangeError.
2. 通过第一步,在类C中查找是否有简单名称和描述符都与目标相匹配的方法,如果有则直接返回这个方法的引用,查找结束.
3. 否则在类C的父类中递归查找是否有简单名称和描述符都与目标相匹配的方法,如果有则返回这个方法的直接引用,查找结束
4. 否则在类C实现的接口列表及它们的父接口之中递归查找是否有简单名称和描述符都与目标相匹配的方法,如果存在匹配的方法.说明类C是一个抽象类,这时候查找结束,抛出java.lang.AbstractMethodError异常
5. 否则,宣告查找失败,抛出java.lang.NoSuchMethodError.

最后如果查找过程中成功返回了直接引用,将会对这个方法进行权限验证:如果发现不具备对此方法的权限访问,将抛出java.lang.IllegalAccessError

#### CONSTANT_InterfaceMethodref_info结构体解析
接口方法也需要先解析出接口方法表的class_index项中索引的方法所属的类或接口的符号引用,如果解析成功,依然用C表示这个接口:

1. 与类方法解析相反,如果在接口方法表中发现class_index中的索引C是个类而不是接口,就将直接抛出java.lang.IncompatibleClassChangeError异常.
2. 否则在接口C中查找是否有简单名称和描述符都与目标相匹配的方法,如果有则返回这个方法的直接引用,查找结束.
3. 否则在接口C的父接口中递归查找,知道java.lang.Object类为止,看是否有简单名称和描述符都与目标相匹配的方法,如果有则返回这个方法的直接引用,查找结束.
4. 否则,宣告方法查找失败,抛出java.lang.NoSuchMethodError异常

由于接口中的所有方法都默认是public的,所以不存在访问权限的问题,因为接口方法的符号引用解析都应当不会抛出"java.lang.IllegalAccessError"异常

### 类的初始化
类初始化阶段是类加载过程中最后一步,开始执行类构造器<clinit>方法. `<clinit>`方法执行过程可能会影响程序运行行为的一些特点和细节

1. `<clinit>`方法由编译器将类变量的赋值动作和静态语句块(static{}块)中的语句合并产生的.编译器收集的顺序是由语句在源文件中出现的顺序决定的,静态语句块只能访问到定义在静态语句块之前的变量,定义在它之后的变量,在前面的静态语句块中可以赋值但是不能访问.
2. `<clinit>()`方法和实例的构造函数(<init>)不同,他不需要显式地调用父类构造器,虚拟机会保证在子类的<clinit>()方法执行之前,父类的`<clinit>`方法已经执行完毕,因此虚拟机中第一个被执行的`<clinit>()`方法的类肯定是`java.lang.Object`
3. 由于父类的`<clinit>()`方法先执行,也就意味着父类中定义的静态语句块要优先于子类的变量赋值操作
4. `<clinit>()`方法对于对类或者接口来说并不是必须的,如果一个类中没有静态语句块,也没有对变量的赋值操作,那么编译器可以不为这个类生成`<clinit>()`方法.
5. 接口中不能使用静态语句块,但仍然有变量初始化的赋值操作,因此接口与类一样会生成<clinit>()方法.但接口与类不同的是,执行接口`<clinit>()`不需要先执行父接口`<clinit>()`.只有当父接口中定义的变量被使用时,父接口才会被初始化.另外,接口的实现类在初始化时也一样不会执行接口的<clinit>()方法.
6. 虚拟机会保证一个类的`<clinit>()`方法在多线程环境中被正确地加锁和同步,如果多个线程同时去初始化一个类,那么只会有一个线程去执行这个类的`<clinit>()`方法,其他线程都需要阻塞等待,直到活动线程执行`<clinit>()`方法完毕. 如果,在一个类的<clinit>()方法中有耗时很长的操作,那就很可能造成多个进程阻塞.

`<clinit>()`方法执行顺序
```java
    public class NewClass {

    static class Parent {
        public static int A = 1;
        static {
            A = 2;
        }
    }

    static class Sub extends Parent {
        public static int B = A;
    }

    public static void main(String[] args) {
        System.out.println(Sub.B);
    }
}
```

字段解析
```java
public class DeadLoopClass {

    static {
        if(true) {
            System.out.println(Thread.currentThread() + " init DeadLoopClass ");
            while(true){}
        }
    }

    public static void main(String[] args) {
        Runnable script = new Runnable() {

            @Override
            public void run() {
                System.out.println(Thread.currentThread() + " start");
                DeadLoopClass dlc = new DeadLoopClass();
                System.out.println(Thread.currentThread() + " run over");
            }

        };

        Thread t1 = new Thread(script);
        Thread t2 = new Thread(script);
        t1.start();
        t2.start();
    }
}
```

类初始化的四种情况
1. 遇到new, getstatic, putstatic, invokestatic, 这四条字节码指令时, 如果类没有进行过初始化,则必须先触发初始化
2. 使用java.lang.reflect包的方法进行反射调用的时候,如果类没有进行过初始化,则需要先触发其初始化
3. 当初始化一个类的时候,如果发现其父类还没有进行过初始化,则需要先触发其父类的初始化.
4. 当虚拟机启动的时候,用户需要指定一个要执行的主类,虚拟机会先初始化这个主类.

通过子类引用父类的静态字段,不会导致子类的类初始化
```java
class SuperClass {

    static {
        System.out.println("SuperClass init");
    }

    public static int value = 123;


}

class SubClass extends SuperClass {
    static {
        System.out.println("SubClass init");
    }
}

public class NotInitialization {
    public static void main(String[] args) {
        System.out.println(SubClass.value);
    }
}
```

通过数组定义来引用类,不会触发此类的初始化
```java
class SuperClass {

    static {
        System.out.println("SuperClass init");
    }

    public static int value = 123;
}

public class NotInitialization {
    public static void main(String[] args) {
        SuperClass[] sca = new SuperClass[10];
    }
}
```

常量在编译阶段会存入调用类的常量池中,本质上没有直接引用到定义常量的类,因此不会触发定义常量的类的初始化
```java
class ConstClass {
    static {
        System.out.println("ConstClass init");
    }
    public static final String HELLOWORLD = "hello world";
}

public class NotInitialization {
    public static void main(String[] args) {
        System.out.println(ConstClass.HELLOWORLD);
    }
}
```
