category: 
- collection
title: java2.0的同步方式
---
# synchronize2.0

##### 2.0
同步的集合包装器 synchronizedMap 和 synchronizedList ,有时也被称作 有条件地线程安全――所有单个的操作都是线程安全的,但是多个操作组成的操作序列却可能导致数据争用,
因为在操作序列中控制流取决于前面操作的结果.例如对于这样的功能put-if-absent语句块――如果一个条目不在Map 中,那么添加这个条目.该功能通过containsKey() 方法和 put() 方法组合起来.
要保证原子性操作,就需要对该语句块加上同步锁.
而且同步容器是对每个操作都进行同步,当大数据量或者多个线程下,会造成严重的并发性能下降
通过将基本的功能从线程安全性中分离开来,Collections.synchronizedMap 允许需要同步的用户可以拥有同步,而不需要同步的用户则不必为同步付出代价.

###### 第一组方法主要返回集合的各种数据：
* 检查要添加的元素的类型并返回结果.任何尝试添加非法类型的变量都会抛出一个ClassCastException异常.这个功能可以防止在运行的时候出错.
```
Collections.checkedCollection
Collections.checkedList
Collections.checkedMap
Collections.checkedSet
Collections.checkedSortedMap
Collections.checkedSortedSet：
```
* 返回一个固定的空集合,不能添加任何元素.
```
Collections.emptyList
Collections.emptyMap
Collections.emptySet ：
```
* 返回一个只有一个入口的 set/list/map 集合.
```
Collections.singleton
Collections.singletonList
Collections.singletonMap：
```
* 获得集合的线程安全版本(多线程操作时开销低但不高效,而且不支持类似put或update这样的复合操作)
```
Collections.synchronizedCollection
Collections.synchronizedList
Collections.synchronizedMap
Collections.synchronizedSet
Collections.synchronizedSortedMap
Collections.synchronizedSortedSet：
```
* 返回一个不可变的集合.当一个不可变对象中包含集合的时候,可以使用此方法.
```
Collections.unmodifiableCollection
Collections.unmodifiableList
Collections.unmodifiableMap
Collections.unmodifiableSet
Collections.unmodifiableSortedMap
Collections.unmodifiableSortedSet：
```

###### 第二组方法中,其中有一些方法因为某些原因没有加入到集合中：
* Collections.addAll：添加一些元素或者一个数组的内容到集合中.
* Collections.binarySearch：和数组的Arrays.binarySearch功能相同.
* Collections.disjoint：检查两个集合是不是没有相同的元素.
* Collections.fill：用一个指定的值代替集合中的所有元素.
* Collections.frequency：集合中有多少元素是和给定元素相同的.
* Collections.indexOfSubList / lastIndexOfSubList：和String.indexOf(String) / lastIndexOf(String)方法类似——找出给定的List中第一个出现或者最后一个出现的子表.
* Collections.max / min：找出基于自然顺序或者比较器排序的集合中,最大的或者最小的元素.
* Collections.replaceAll：将集合中的某一元素替换成另一个元素.
* Collections.reverse：颠倒排列元素在集合中的顺序.如果你要在排序之后使用这个方法的话,在列表排序时,最好使用Collections.reverseOrder比较器.
* Collections.rotate：根据给定的距离旋转元素.
* Collections.shuffle：随机排放List集合中的节点,可以给定你自己的生成器——例如 java.util.Random / java.util.ThreadLocalRandom or java.security.SecureRandom.
* Collections.sort：将集合按照自然顺序或者给定的顺序排序.
* Collections.swap：交换集合中两个元素的位置(多数开发者都是自己实现这个操作的).