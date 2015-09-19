category: 
- concurrency
tag:
- 并发集合
title: CopyOnWriteArrayList
---
# CopyOnWriteArrayList

1. CopyOnWriteArrayList（写数组的拷贝）是ArrayList的一个线程安全的变体,CopyOnWriteArrayList和CopyOnWriteSet都是线程安全的集合,其中所有可变操作（add、set等等）都是通过对底层数组进行一次新的复制来实现的。

2. 它绝对不会抛出ConcurrentModificationException的异常。因为该列表（CopyOnWriteArrayList）在遍历时将不会被做任何的修改。

3. CopyOnWriteArrayList适合用在“读多,写少”的“并发”应用中,换句话说,它适合使用在读操作远远大于写操作的场景里,比如缓存。它不存在“扩容”的概念,每次写操作（add or remove）都要copy一个副本,在副本的基础上修改后改变array引用,所以称为“CopyOnWrite”,因此在写操作是加锁,并且对整个list的copy操作时相当耗时的,过多的写操作不推荐使用该存储结构。

4. CopyOnWriteArrayList的功能是是创建一个列表,有三种构造方法：
   >（1）CopyOnWriteArrayList ()创建一个空列表。
   >（2）CopyOnWriteArrayList (Collection<? extendsE> c) 创建一个按 collection的迭代器返回元素的顺序包含指定 collection元素的列表。
   >（3）CopyOnWriteArrayList（E[] toCopyIn） 创建一个保存给定数组的副本的列表。


它是线程安全的无序的集合，可以将它理解成线程安全的HashSet。有意思的是，CopyOnWriteArraySet和HashSet虽然都继承于共同的父类AbstractSet
；但是，HashSet是通过“散列表(HashMap)”实现的，而CopyOnWriteArraySet则是通过“动态数组(CopyOnWriteArrayList)”实现的，并不是散列表。
和CopyOnWriteArrayList类似，CopyOnWriteArraySet具有以下特性： 
1. 它最适合于具有以下特征的应用程序：Set大小通常保持很小，只读操作远多于可变操作，需要在遍历期间防止线程间的冲突。 
2. 它是线程安全的。 
3. 因为通常需要复制整个基础数组，所以可变操作（add()、set() 和 remove() 等等）的开销很大。 
4. 迭代器支持hasNext(),next()等不可变操作，但不支持可变 remove()等 操作。 
5. 使用迭代器进行遍历的速度很快，并且不会与其他线程发生冲突。在构造迭代器时，迭代器依赖于不变的数组快照。

