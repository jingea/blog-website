title: java集合
---

这一部分介绍的是不支持多线程的集合.这些集合都在java.util包里.其中一些在Java 1.0的时候就有了(现在已经弃用),其中大多数在Java 1.4中重新发布.枚举集合在Java 1.5中重新发布,并且从这个版本之后所有的集合都支持泛型.PriorityQueue也在Java 1.5中加入.非线程安全的集合架构的最后一个版本是ArrayDeque ,也在Java 1.6中重新发布了.

# List

* `LinkedList`：`Deque`实现：每一个节点都保存着上一个节点和下一个节点的指针.这就意味着数据的存取和更新具有线性复杂度(这也是一个最佳化的实现,每次操作都不会遍历数组一半以上,操作成本最高的元素就是数组中间的那个).如果想写出高效的LinkedList代码可以使用 ListIterators .如果你想用一个Queue/Deque实现的话(你只需读取第一个和最后一个元素就行了)——考虑用ArrayDeque代替.

* `Vector`：一个带有线程同步方法的ArrayList版本.现在直接用ArrayList代替了.

* `ArrayList`： 最有用的List集合实现.由一个整形数字或数组存储了集合的大小(数组中第一个没有使用的元素).像所有的List集合一样,ArrayList可以在必要的时候扩展它的大小.ArrayList访问元素的时间开销固定.在尾部添加元素成本低(为常数复杂度),而在头部添加元素成本很高(线性复杂度).

这是由ArrayList的实现原理——所有的元素的从角标为0开始一个接着一个排列造成的.也就是说,从要插入的元素位置往后,每个元素都要向后移动一个位置.CPU缓存友好的集合是基于数组的.(其实也不是很友好,因为有时数组会包含对象,这样存储的只是指向实际对象的指针).


# Maps
* `HashMap`：最常用的Map实现.只是将一个键和值相对应,并没有其他的功能.对于复杂的hashCode method,get/put方法有固定的复杂度.就是一张hash表,键和值都没有排序.Hashtable的后继者HashMap是作为JDK1.2中的集合框架的一部分出现的,它通过提供一个不同步的基类和一个同步的包装器Collections.synchronizedMap ,解决了线程安全性问题.

* `EnumMap`：枚举作为键值的Map.因为键的数量相对固定,所以在内部用一个数组储存对应值.通常来说,效率要高于HashMap.

* `HashTable`：旧HashMap的同步版本,新的代码中也使用了HashMap.是同步的(而HashMap是不同步的).所以如果在线程安全的环境下应该多使用HashMap,而不是Hashtable,因为Hashtable对同步有额外的开销.提供了一种易于使用的、线程安全的、关联的map功能.然而,线程安全性付出代价是――Hashtable 的所有方法都是同步的.

* `IdentityHashMap`：这是一个特殊的Map版本,它违背了一般Map的规则`：它使用 “==” 来比较引用而不是调用Object.equals来判断相等.这个特性使得此集合在遍历图表的算法中非常实用——可以方便地在IdentityHashMap中存储处理过的节点以及相关的数据.

* `LinkedHashMap `：保存了插入时的顺序.HashMap和LinkedList的结合,所有元素的插入顺序存储在LinkedList中.这就是为什么迭代LinkedHashMap的条目(entry)、键和值的时候总是遵循插入的顺序.在JDK中,这是每元素消耗内存最大的集合.

* `TreeMap`：以红-黑树结构为基础,键值按顺序排列.一种基于已排序且带导向信息Map的红黑树.每次插入都会按照自然顺序或者给定的比较器排序.
这个Map需要实现equals方法和Comparable/Comparator.compareTo需要前后一致.这个类实现了一个NavigableMap接口`：可以带有与键数量不同的入口,可以得到键的上一个或者下一个入口,可以得到另一Map某一范围的键(大致和SQL的BETWEEN运算符相同),以及其他的一些方法.

* `WeakHashMap`：这种Map通常用在数据缓存中.它将键存储在WeakReference中,就是说,如果没有强引用指向键对象的话,这些键就可以被垃圾回收线程回收.值被保存在强引用中.因此,你要确保没有引用从值指向键或者将值也保存在弱引用中m.put(key, new WeakReference(value)).


# Sets
* `HashSet`：一个基于HashMap的Set实现.其中,所有的值为“假值”(同一个Object对象具备和HashMap同样的性能.基于这个特性,这个数据结构会消耗更多不必要的内存.

* `EnumSet`：值为枚举类型的Set.Java的每一个enum都映射成一个不同的int.这就允许使用BitSet——一个类似的集合结构,其中每一比特都映射成不同的enum.EnumSet有两种实现,RegularEnumSet——由一个单独的long存储(能够存储64个枚举值,99.9%的情况下是够用的),JumboEnumSet——由long[]存储.

* `BitSet`：一个比特Set.需要时常考虑用BitSet处理一组密集的整数Set(比如从一个预先知道的数字开始的id集合).这个类用 long[]来存储bit.

* `LinkedHashMap`：与HashSet一样,这个类基于LinkedHashMap实现.这是唯一一个保持了插入顺序的Set.

* `TreeSet`：与HashSet类似.这个类是基于一个TreeMap实例的.这是在单线程部分唯一一个排序的Set.
