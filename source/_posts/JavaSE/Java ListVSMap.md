category: Java
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


对于`HashMap`它的Javadoc中是如此说的:

`HashMap`是hash table的一个实现。它与`HashTable`不同之处就是它是非同步的而且键值都支持null.对于put和get操作，HashMap的耗时都是固定的，不会因为Map的大小而变化。因为hash函数会将元素分配到不同的bucket里面取. HashMap的迭代操作与它的容量（bucket数量+键值对数量）成正比关系.

一般情况下, load factor的默认值0.75, 这个值在空间和时间上找到较为平衡的查找性能。 如果高于这个值的话，会减少空间占用但是会增加查询的消耗(这点反应在了大多数的hashMap操作中，包括get和put操作).

下来我们首先看一下它的数据成员
```java
// load factor 默认值
static final float DEFAULT_LOAD_FACTOR = 0.75f;

// hash表存储数据的数据结构, 每个Node都是一个散列桶, 每个桶里是一个链表
transient Node<K,V>[] table;

// hash表散列桶的大小阀值, 如果超过这个值就对hash表进行拓容 (大小为: capacity * load factor)
int threshold;

// hash表使用的loadFactor
final float loadFactor;
```

然后我们看一下`put()`方法的实现
```java
final V putVal(int hash, K key, V value, boolean onlyIfAbsent,                    boolean evict) {
        Node<K,V>[] tab;
        Node<K,V> p;
        int n, i;
        // 如果table不存在或者table大小为0, 则重新生成一个table
        if ((tab = table) == null || (n = tab.length) == 0)
            n = (tab = resize()).length;
        // 根据与hash值与操作找到要插入的元素所在的散列桶的位置
        if ((p = tab[i = (n - 1) & hash]) == null)
            // 发现当前位置上的散列桶上没有Node则,重新生成一个Node
            tab[i] = newNode(hash, key, value, null);
        else {
            // 发现当前散列桶已经有元素了
            Node<K,V> e; K k;
            // 判断插入key是否与找到的Node的key是否相等, 如果相等则将p赋值给e, 进行value的赋值操作
            if (p.hash == hash && ((k = p.key) == key || (key != null && key.equals(k))))
                e = p;
            else if (p instanceof TreeNode)
                e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
            else {
                // 插入的key与散列桶链表中的第一个元素不相符, 则遍历整个链表
                for (int binCount = 0; ; ++binCount) {
                    // 在连表中找不到存在的元素, 则生成一个新的Node插入进来
                    if ((e = p.next) == null) {
                        p.next = newNode(hash, key, value, null);
                        if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                            treeifyBin(tab, hash);
                        break;
                    }
                    // 找到了与要插入的key相等的散列表的元素, 则停止继续查找
                    if (e.hash == hash && ((k = e.key) == key || (key != null && key.equals(k))))
                        break;
                    p = e;
                }
            }

            // 在散列表里找到了相同的key的hash值, 就直接插入了, 不再需要进行下面的hash表拓容操作
            if (e != null) { // existing mapping for key
                V oldValue = e.value;
                if (!onlyIfAbsent || oldValue == null)
                    e.value = value;
                afterNodeAccess(e);
                return oldValue;
            }
        }
        ++modCount;
        // 占有了一个新的hash桶, 判断如果超过了Hash表散列桶的阀值,则对hash表进行拓容
        if (++size > threshold)
            resize();
        afterNodeInsertion(evict);
        return null;
    }
```
从上面的逻辑我们可以看出,`HashMap`在插入元素的时候,首先是根据key的hash值找到散列桶的位置, 然后再根据key与散列桶中的散列表的数据进行便利查找.

因此我们应该尽量的调大HashMap的容量, 尽可能的让桶的容量大于元素的个数, 同时尽可能的保证key值hash函数的正确性, 否则如果元素过多但是桶的数量太少, 会将hash 表退化到链表结构, 将O(1)的查找复杂度变成O(N). 说完HashMap的查找复杂度, 我们再来看一下HashMap的内存占有, 每当我们插入一个新的元素的时候都会生成一个`Node`对象
```java
static class Node<K,V> implements Map.Entry<K,V> {
    final int hash;
    final K key;
    V value;
    Node<K,V> next;
}
```
