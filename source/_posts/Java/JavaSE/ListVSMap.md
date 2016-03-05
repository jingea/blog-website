category: Java
tag: JavaSE
date: 2016-03-05
title: List VS Map
---
在日常的使用中, 使用的最多的结构就是List和Map了. 其中又以ArrayList和HashMap使用的最多. 今天特意找了一些时间来看一下他们各自的实现以及添加索引数据时的性能.

首先看一下ArrayList.
```java
transient Object[] elementData;

public boolean add(E e) {
    ensureCapacityInternal(size + 1);  // Increments modCount!!
    elementData[size++] = e;
    return true;
}

public E get(int index) {
    rangeCheck(index);

    return elementData(index);
}
```
它的内部就是一个Object类型的数组, 在添加数据时首先确保数组不会越界, 如果会产生越界则内部进行数组扩容拷贝操作.



HashMap是hash table的一个实现。它与HashTable不同之处就是它是非同步的而且键值都支持null.对于put和get操作，HashMap的耗时都是固定的，不会因为Map的大小而变化。因为hash函数会将元素分配到不同的bucket里面取. HashMap的迭代操作与它的容量（bucket数量+键值对数量）成正比关系，因此在初始化HashMap大小的时候要设置太高的容量或者较小的load factor。

一般情况下, load factor的默认值0.75在空间和时间上找到了一种合适的性能。 如果高于这个值的话，会减少空间占用但是会增加查询的消耗(这点反应在了大多数的hashMap操作中，包括get和put操作). 