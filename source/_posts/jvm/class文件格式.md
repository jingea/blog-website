category: JVM
date: 2014-10-08
title: class文件格式
---

## ClassFile 结构
```
u4              magic;
u2              minor_version;
u2              major_version;
u2              constant_pool_count;
cp_info         constant_pool[constant_pool_count - 1];
u2              access_flags;
u2              this_class;
u2              super_class;
u2              interfaces_count;
u2              interfaces[interfaces_count];
u2              fields_count;
field_info      fields[fields_count];
u2              methods_count;
method_info     methods[methods_count];
u2              attributes_count;
attribute_info  attributes[attributes_count];
```

### magic
Magic的唯一作用是确定这个文件是否是一个能被虚拟机所接受的Class文件.魔数固定值为`0xCAFEBABY`,不会改变

### 版本号
* minor_version副版本号 
* major_version主版本号
二者共同构成Class文件版本号.假设minor_version为m, major_version为M, 那么class文件的版本号为M.m.在JDK版本在1.k(k>=2)以上时, class文件的版本范围为(45.0 ~ 44+k.0)

### constant_pool_count
此值等于常量池中的成员数加1. 常量池表的索引值只有大于0且小于constant_pool_count 时才会被认为是有效的,对于long和double例外

### constant_pool(常量池)
是一种表结构, 它包含Class文件结构及其子结构中所引用的所有字符串常量, 类, 或接口名, 字段名和其他常量.

常量池主要存放俩大类常量:
* 字面量 : 文本字符串,被声明为final的常量值
* 符号引用. 符号引用则包括了下列三种常量:

1. 类和接口的全限定名.
2. 字段的名称和描述符
3. 方法的名称和描述符

当虚拟机运行时,会从常量池获得对应的符合引用,再在类创建或运行时解析并翻译到具体的内存地址之中.常量池中每一项常量都是一个表,下面列举了这11种表结构

|项目                            |类型|描述                                              |
|--------------------------------|---:|-------------------------------------------------:|
|CONSTANT_Utf8_info              |UTF-8编码的字符串                                      |
|tag                             |u1  |值为1                                             |
|length                          |u2  |UTF-8编码的字符串占用了字节数                     |
|bytes                           |u1  |长度为length的UTF-8的字符串                       |
|CONSTANT_Integer_info           |整型字面量                                             |
|tag                             |u1  |值为3                                             |
|bytes                           |u4  |按照高位在前存储的int值                           |
|CONSTANT_Float_info             |浮点型字面量                                           |
|tag                             |u1  |值为4                                             |
|bytes                           |u4  |按照高位在前存储的值float                         |
|CONSTANT_Long_info              |长整型字面量                                           |
|tag                             |u1  |值为5                                             |
|bytes                           |u8  |按照高位在前存储的float值                         |
|CONSTANT_Double_info            |双精度浮点型字面量                                     |
|tag                             |u1  |值为6                                             |
|bytes                           |u8  |按照高位在前存储的double值                        |
|CONSTANT_Class_info             |类或接口的符号引用                                     |
|tag                             |u1  |值为7                                             |
|bytes                           |u2  |指定全限定名常量项的索引                          |
|CONSTANT_String_info            |字符串型字面量                                         |
|tag                             |u1  |值为8                                             |
|bytes                           |u4  |指向字符串字面量的索引                            |
|CONSTANT_Fieldref_info          |字段的符号引用                                         |
|tag                             |u1  |值为9                                             |
|index                           |u2  |指向声明字段的类或接口描述符CONSTANT_Class_info   |
|index                           |u2  |指向字段描述符CONSTANT_NameAndType的索引项        |
|CONSTANT_Methodref_info         |类中方法的符号引用                                     |
|tag                             |u1  |值为10                                            |
|index                           |u2  |指向声明字段的类或接口描述符CONSTANT_Class_info   |
|index                           |u2  |指向字段描述符CONSTANT_NameAndType的索引项        |
|CONSTANT_InterfaceMethodref_info|接口中方法的引用                                       |
|tag                             |u1  |值为11                                            |
|index                           |u2  |指向声明字段的类或接口描述符CONSTANT_Class_info   |
|index                           |u2  |指向字段描述符CONSTANT_NameAndType的索引项        |
|CONSTANT_NameAndType_info       |字段或方法的部分符号引用                               |
|tag                             |u1  |值为12                                            |
|index                           |u2  |指向该字段或方法名称常量项的索引                  |
|index                           |u2  |指向该字段或方法描述符常量项的索引                |


### access_flags
是一种掩码标志, 用于表示某个类或者接口的访问权限及属性.

access_flags 的取值范围和相应含义表

|标志名         |值     |含义                                                              |
|---------------|------:|-----------------------------------------------------------------:|
|ACC_PUBLIC     |0x0001 |声明为public,可以被包外访问                                       |
|ACC_FINAL      |0x0010 |声明为final,不允许有子类                                          |
|ACC_SUPER      |0x0020 |当用到invokespecial指令时,需要特殊处理的父类方法                  |
|ACC_INTERFACE  |0x0200 |标志定义的是接口而不是类                                          |
|ACC_ABSTRACT   |0x0400 |声明为abstract, 不能被实例化                                      |
|ACC_SYNTHETIC  |0x1000 |声明为synthetic, 标志为非java源码生成的                           |
|ACC_ANNOTATION |0x2000 |标志为注解类型                                                    |
|ACC_ENUM       |0x4000 |标志为枚举类型,意味着它或者它的父类被声明为枚举                   |

当设置上`ACC_INTERFACE`意味着它是接口而不是类, 反之是类而不是接口. 当带有该标志,同时也设置了 `ACC_ABSTRACT`,则不能再设置`ACC_FINAL,ACC_SUPER,ACC_ENUM`.

### this_class

类索引用于确定这个的全限定名, this_class类型为u2, 指向一个类型为CONSTANT_Class_info 的类描述符常量.而CONSTANT_Class_info 类型中的索引值指向了 CONSTANT_Utf8_info 类型的常量中的全限定名字符串.java通过this_class, super_class, interfaces 来确定这个类的继承关系

### super_class

当前类的父类. 由于java是单继承体制, 所以父类索引只有一个.类型为u2, 指向一个类型为CONSTANT_Class_info 的类描述符常量. 而CONSTANT_Class_info 类型中的索引值指向了 CONSTANT_Utf8_info 类型的常量中的全限定名字符串.

### interfaces_count

该类实现了接口的数量.

### interfaces

该类实现的接口列表. 类型为u2, 指向一个类型为CONSTANT_Class_info 的类描述符常量.
而CONSTANT_Class_info 类型中的索引值指向了 CONSTANT_Utf8_info 类型的常量中的全限定名字符串.

### fields_count

用于描述接口或类中声明的字段数量.

### fields 字段表

用于描述接口或类中声明的字段.字段表中包括了类级变量或实例级变量, 但不包括在方法内部声明的变量.每个表中字段中的信息有:字段的作用域(public, private, protected修饰符), 实例变量还是类变量, 可变性(final),并发可见性(volatile), 可否序列化(transient), 字段数据类型, 字段名称.

字段表中不会出现从父类或者父接口中继承而来的字段, 但有可能列出原本java代码中不存在呃字段, 例如在内部类中为了保持对外部类的访问性, 会自动添加指向外部类实例的字段. 另外在java语言中字段是无法重载的, 无论俩个字段的数据类型,修饰符是否相同, 都必须使用不一样的名称, 但是对于字节码来讲, 如果俩个描述符不同, 那字段重名就是合法的.

字段表结构

|类型             |名称               |数量               |
|-----------------|------------------:|------------------:|
|u2               |access_flags       |1                  |
|u2               |name_index         |1                  |
|u2               |descriptor_index   |1                  |
|u2               |attributes_count   |1                  |
|attribute_info   |attributes         |attributes_count   |

### access_flags

|标志名称        |标识符   | 二进制           |    含义                       |
|----------------|--------:|-----------------:|------------------------------:|
|ACC_PUBLIC      |0x0001   |1                 |字段是否是 public              |
|ACC_PRIVATE     |0x0002   |10                |字段是否是private              |
|ACC_PROTECTED   |0x0004   |100               |字段是否是protected            |
|ACC_STATIC      |0x0008   |1000              |字段是否是static               |
|ACC_FINAL       |0x0010   |10000             |字段是否是final                |
|ACC_VOLATILE    |0x0040   |1000000           |字段是否是volatile             |
|ACC_TRANSIENT   |0x0080   |10000000          |字段是否是transient            |
|ACC_SYNTHETIC   |0x1000   |1000000000000     |字段是否是由编译器自动产生的   |
|ACC_ENUM        |0x4000   |100000000000000   |字段是否是enum                 |

通过`access_flags` 我们可以很容易的看出`ACC_PUBLIC, ACC_PRIVATE, ACC_PROTECTED`三个标记中最多只能选择其一.而且`ACC_FINAL` 和 `ACC_VOLATILE` 不能同时选择.


### methods_count

方法表中的方法的数量

### methods

Class文件存储格式中对方法的描述与对字段的描述几乎采用了完全一致的方法.

如果父类方法在子类中没有被重写, 方法表集合中就不会出现来自父类的方法信息. 但同样的,有可能出现由编译器自动添加的方法, 最经典的就是类构造器<clinit>和实例构造器<init>

在java语言中重载一个方法,除了要与愿方法具有相同的简单名称之外, 还要求必须拥有一个与原方法不同的签名特征, 签名特征就是一个方法中各个参数在常量池的字段符号引用的集合, 也就是因为
返回值不会包含在签名特征之中, 因此java语言里无法仅仅靠返回值的不同来对一个方法进行重载.但是在class文件格式之中,签名的范围更大一些,只要描述符不是完全一致的俩个方法也可以共存.
也就是说俩个方法具有相同的名称和特征签名,但返回值不同,那么也是可以合法共存于一个class文件中.

方法表结构

|类型             |名称               |数量               |
|-----------------|------------------:|------------------:|
|u2               |access_flags       |1                  |
|u2               |name_index         |1                  |
|u2               |descriptor_index   |1                  |
|u2               |attributes_count   |1                  |
|attribute_info   |attributes         |attributes_count   |

方法访问标志

|标志名称        |标识符   |二进制表示      |含义                             |
|----------------|--------:|---------------:|--------------------------------:|
|ACC_PUBLIC      |0x0001   |1               |方法是否是public                 |
|ACC_PRIVATE     |0x0002   |10              |方法是否是private                |
|ACC_PROTECTED   |0x0004   |100             |方法是否是protected              |
|ACC_STATIC      |0x0008   |1000            |方法是否是static                 |
|ACC_FINAL       |0x0010   |10000           |方法是否是final                  |
|ACC_SYNCHRONIZED|0x0020   |100000          |方法是否是synchronized           |
|ACC_BRIDGE      |0x0040   |1000000         |方法是否是由编译器产生的桥接方法 |
|ACC_VARARGS     |0x0080   |10000000        |方法是否是接受不确定参数         |
|ACC_NATIVE      |0x0100   |100000000       |方法是否是native                 |
|ACC_ABSTRACT    |0x0400   |10000000000     |方法是否是abstract               |
|ACC_STRICT      |0x0800   |100000000000    |方法是否是strictfp               |
|ACC_SYNTHETIC   |0x1000   |1000000000000   |方法是否是由编译器自动产生的     |

### attributes_count

属性表里的属性数量

### attributes

#### 属性表

|属性名称             |使用位置           |含义                                    |
|---------------------|------------------:|---------------------------------------:|
|Code                 |方法表             |java代码编译成的字节码指令              |
|ConstantValue        |字段表             |final关键字定义的常量值                 |
|Deprecated           |类,方法表,字段表   |被声明为deprecated的方法和字段          |
|Exceptions           |方法表             |方法抛出的异常                          |
|InnerClass           |类文件             |内部类列表                              |
|LineNumberTable      |Code属性           |java源码的行号和字节码指令的对应关系    |
|LocalVariableTable   |Code属性           |方法的局部的变量描述                    |
|SourceFile           |类文件             |原文件名称                              |
|Synthetic            |类,字段表,方法表   |标志方法或字段为编译器自动生成的        |


属性表在Class文件,字段表,方法表中都可以携带自己的属性表集合.

#### Code属性

|类型             |名称                     |数量                  |
|-----------------|------------------------:|---------------------:|
|u2               |attribute_name_index     |1                     |
|u4               |attribute_length         |1                     |
|u2               |max_stack                |1                     |
|u2               |max_locals               |1                     |
|u4               |code_length              |1                     |
|u1               |code                     |code_length           |
|u2               |exception_table_length   |1                     |
|exception_info   |exception_table          |exception_table_length|
|u2               |attributes_count         |1                     |
|attribute_info   |attributes               |attributes_count      |


1. attribute_name_index  是一项指向CONSTANT_Utf8_info型常量. 常量值固定为"Code",它代表了该属性的属性名称.
2. attribute_length  该值代表了属性值的长度, 由于属性名称索引和属性长度一共是6个字节, 所以属性值的长度固定为整个属性表的长度
3. max_stack  该值代表了操作数栈深度的最大值.虚拟机运行时需要根据这个值来分配栈帧中的操作数栈深度.
4. max_locals 该值代表了局部变量所需的存储空间. max_locals的单位是Slot, Slot是虚拟机为局部变量分配空间所使用的最小单位.
5. code_length 代表字节码长度, 虽然该值是一个u4类型的长度值,但是虚拟机规范中限制了一个方法不允许超过65535条字节码指令。如果超过这个指令,javac编译器会拒绝编译.
6. code 用于存储字节码指令的一系列字节流. 每个字节码指令都是一个u1类型的单字节,当虚拟机读取到Code中的一个字节码时,就可以相应的找出这个字节码代表的是什么指令, 并且可以知道这条指令后面是否需要跟随参数,以及参数如何理解.
7. exception_table_length
8. exception_table
9. attributes_count
10. attributes

> 对于max_locals有如下说明：对应byte, char, float, int, short, boolean, refrence, returnAddress 等长度不超过32位的数据类型,每个局部变量占用一个Slot, 而double和long这俩种64位的数据类型则需要2个solt来存放.方法参数,显式异常处理器的参数,方法体中定义的局部变量都需要使用局部变量来存放.需要注意的是,并不是在方法中用到了多少个局部变量,就把这些局部变量所占的Slot之和作为max_locals的值,原因是局部变量表中的Slot可以重用,当代码执行超出一个局部变量的作用域时,这个局部变量所占的Slot就可以被其他的局部变量所使用,编译器会根据变量的作用域来分类Solt并分配给各个变量使用.

#### Exceptions 属性

Exceptions属性是在与方法表中与Code属性平级的一项属性, 这与异常表是不同的. Exceptions属性的作用是列举出方法中可能抛出的受检查异常,也就是方法描述时throws关键字后面列举的异常

Exceptions属性表结构

|类型  |名称                   |数量                   |
|------|----------------------:|----------------------:|
|u2    |attribute_name_index   |1                      |
|u4    |attribute_length       |1                      |
|u2    |number_of_exceptions   |1                      |
|u2    |exception_index_table  |number_of_exceptions   |

> number_of_exceptions 表示方法可能抛出number_of_exceptions种受检查异常, 每一种受检查异常都是要一个xception_index_table表示. exception_index_table指向一个常量池CONSTANT_Class_info类型的常量索引

#### LineNumberTable属性
用于描述java源码行号与字节码之间的对应关系. 它并不是运行时必须的属性. 但默认的会生成到Class文件中,可以使用javac中-g:none或者-g:lines选项来取消它. 取消的后果是在抛出异常时,堆栈中将不会显示错的行号,并且在断点时,无法按照源码设置断点.

#### LocalVariableTable 属性
用于描述栈帧中局部变量表中的变量与java源码中定义的变量之间的关系. 它并不是运行时必须的属性.默认也不会生成到Class文件中, 可以使用javac中-g:none或者-g:vars选项来取消或者生成这项信息. 如果没有生成这项信息,最大的影响是当其他人引用这个方法时,所有的参数名都将丢失,IDE可能使用诸如arg0, arg1之类的占位符来代替原有的参数名

#### LocalVarialTable属性结构

|类型                 |名称                         |数量                          |
|---------------------|----------------------------:|-----------------------------:|
|u2                   |attribute_name_index         |1                             |
|u4                   |attribute_length             |1                             |
|u2                   |local_variable_table_length  |1                             |
|local_variable_info  |local_variable_table         |local_variable_table_length   |

local_variable_info项目结构

|类型  |名称              |数量|
|------|-----------------:|---:|
|u2    |start_pc          |1   |
|u2    |length            |1   |
|u2    |name_index        |1   |
|u2    |descriptor_index  |1   |
|u2    |index             |1   |


1. local_variable_info代表了一个栈帧与源码中的局部变量的联系.
2. start_pc和length属性分别代表了这个局部变量的生命周期开始的字节码偏移量及其作用范围覆盖的长度,俩者结合起来就是这个局部变量在字节码之中的作用域范围.
3. name_index和descriptor指向的是常量池中CONSTANT_Utf8_info型常量的索引. 分别代表了局部变量名称及其描述符
4. index是这个局部变量在栈帧局部变量表中Solt的位置.
5. 在JDK1.5引入泛型之后,引入了一个LocalVarialTypeTable,这个新增的属性结构和LocalVarialTable非常相似,它仅仅是把记录的字段的描述符descriptor_index换成了字段的特征签名,对于非泛型类型来说,描述符和特征签名能描述的信息基本是一致的. 但是引入泛型之后,由于描述符中泛型化的参数被擦除掉了,描述符就不能准确地描述泛型信息了,因此引入了LocalVarialTypeTable


#### SourceFile 属性


该属性用来记录生成这个Class文件的源码文件名称,该属性也是可选的,可以使用javac中-g:none或者-g:vars选项来取消或者生成这项信息.

SourceFile属性结构
|类型                 |名称                         |数量                          |
|---------------------|----------------------------:|-----------------------------:|
|u2                   |attribute_name_index         |1                             |
|u4                   |attribute_length             |1                             |
|u2                   |sourcefile_index             |1                             |

#### ConstantValue

1. 该属性的作用是通知虚拟机自动为静态变量赋值. 只有被static修饰的变量才可以使用这项属性.
2. 对于非static类型变量的赋值是在实例构造器<init>方法中进行的.
3. 对于static类型的变量,有俩种赋值方式选择:
   > A: 在类构造器<clinit>中进行
   > B: 使用ConstantValue属性来赋值
  
前Sun Javac编译器的选择是:如果同时使用final和static来修饰一个变量, 并且这个变量的数据类型是基本类型或者String的话, 就生成ConstantValue属性来初始化, 否则在<clinit>中进行初始化.


ConstantValue属性结构
|类型                 |名称                         |数量                          |
|---------------------|----------------------------:|-----------------------------:|
|u2                   |attribute_name_index         |1                             |
|u4                   |attribute_length             |1                             |
|u2                   |constantvalue_index          |1                             |

ConstantValue属性是一个定长属性,它的attribute_length数据值必须为2. constantvalue_index代表了常量池中一个字面量的音乐,根据字段类型的不同,字面量可以是CONSTANT_Long_info, CONSTANT_Float_info,CONSTANT_Double_info,CONSTANT_integer_info,CONSTANT_String_info常量中的一种.


#### InnerClass

用于记录内部类和宿主类之间的关系.如果一个类中定义了内部类,那么编译器会为它以及包含的内部类生成InnerClass属性.

InnerClass 属性结构
|类型                 |名称                         |数量                          |
|---------------------|----------------------------:|-----------------------------:|
|u2                   |attribute_name_index         |1                             |
|u4                   |attribute_length             |1                             |
|u2                   |number_of_classes            |1                             |
|inner_classes_info   |inner_classes                |number_of_classes             |

inner_classes_info表结构

|类型  |名称                        |数量|
|------|---------------------------:|---:|
|u2    |inner_class_info_index      |1   |
|u2    |outer_class_info_index      |1   |
|u2    |inner_name_index            |1   |
|u2    |inner_class_access_flags    |1   |


1. inner_class_info_index和outer_class_info_index分别指向常量池中CONSTANT_Class_info型常量索引. 分别代表内部类和宿主类的符号引用
2. inner_name_index指向常量池中CONSTANT_Utf8_info型常量索引. 代表这个内部类的名称.如果是匿名内部类则为0
3. inner_class_access_flags是内部类的访问标志,

inner_class_access_flags访问标志

|标志名称        |标识符   |二进制表示      |含义                               |
|----------------|--------:|---------------:|----------------------------------:|
|ACC_PUBLIC      |0x0001   |1               |内部类是否是public                 |
|ACC_PRIVATE     |0x0002   |10              |内部类是否是private                |
|ACC_PROTECTED   |0x0004   |100             |内部类是否是protected              |
|ACC_STATIC      |0x0008   |1000            |内部类是否是static                 |
|ACC_FINAL       |0x0010   |10000           |内部类是否是final                  |
|ACC_INTERFACE   |0x0020   |100000          |内部类是否是synchronized           |
|ACC_ABSTRACT    |0x0400   |10000000000     |内部类是否是abstract               |
|ACC_SYNTHETIC   |0x1000   |1000000000000   |内部类是否是由并非用户代码产生的   |
|ACC_ANNOTATION  |0x2000   |100000000000    |内部类是否是一个注解               |
|ACC_ENUM        |0x4000   |100000000000    |内部类是否是一个枚举               |

#### Deprecated, Synthetic
这俩个属性属于标志型的布尔属性,只有存在不存在的区别.Deprecated 表示某个类或者字段或者方法被作者不再推荐使用,在代码中通过@Deprecated标注Synthetic 代码该字段或者方法并不是由java源码直接产生的,而是由编译器自行添加的.

在JDK1.5以后,标志一个类,字段,方法是编译器自动产生的,也可以设置他们的访问标志中的ACC_SYNTHETIC标志位,最典型的例子就是Bridge Method了. 所有由非用户产生的类,字段,方法都应当至少设置Synthetic属性或者ACC_SYNTHETIC标志位,唯一例外的就是<init>和<clinit>方法.


