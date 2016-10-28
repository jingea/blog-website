category: Java
date: 2016-10-27
title: LinkedHashMap
---
`LinkedHashMap` 继承自`HashMap`, 它维护着一个运行于所有条目的双重链接列表。
此链接列表定义了迭代顺序，该迭代顺序通常就是将键插入到映射中的顺序（插入顺序）。

我们从`HashMap`的`putVal()`方法开始看
```java
final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
               boolean evict) {
    Node<K,V>[] tab; Node<K,V> p; int n, i;
    if ((tab = table) == null || (n = tab.length) == 0)
        n = (tab = resize()).length;
    if ((p = tab[i = (n - 1) & hash]) == null)
        tab[i] = newNode(hash, key, value, null);
    else {
        Node<K,V> e; K k;
        if (p.hash == hash &&
            ((k = p.key) == key || (key != null && key.equals(k))))
            e = p;
        else if (p instanceof TreeNode)
            e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
        else {
            for (int binCount = 0; ; ++binCount) {
                if ((e = p.next) == null) {
                    p.next = newNode(hash, key, value, null);
                    if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                        treeifyBin(tab, hash);
                    break;
                }
                if (e.hash == hash &&
                    ((k = e.key) == key || (key != null && key.equals(k))))
                    break;
                p = e;
            }
        }
        if (e != null) { // existing mapping for key
            V oldValue = e.value;
            if (!onlyIfAbsent || oldValue == null)
                e.value = value;
            afterNodeAccess(e);
            return oldValue;
        }
    }
    ++modCount;
    if (++size > threshold)
        resize();
    afterNodeInsertion(evict);
    return null;
}
```
在`newNode(hash, key, value, null);`时调用了`LinkedHashMap`的`linkNodeLast`
```java
    private void linkNodeLast(LinkedHashMap.Entry<K,V> p) {
        LinkedHashMap.Entry<K,V> last = tail;
        tail = p;
        if (last == null)
            head = p;
        else {
            p.before = last;
            last.after = p;
        }
    }
```
这个操作就是将新的Node插入到`LinkedHashMap`的尾节点.

我们看完插入操作, 再看一下`afterNodeAccess()`, 这个方法在`HashMap`的`putVal()`方法中, 重新插入一个数据时会调用到(还有一些其他的方法也会调用这个方法). 当重新插入数据时, 会尝试`afterNodeAccess()`调用, 然后改变`LinkedHashMap`的双向链表, 从而改变整个链表表达的插入顺序.
```java
void afterNodeAccess(Node<K,V> e) { // move node to last
    LinkedHashMap.Entry<K,V> last;
	// 如果被访问的元素是最后一个元素的话, 就不处理, 否则尝试将被访问的元素移动到最后的位置
    if (accessOrder && (last = tail) != e) {
        LinkedHashMap.Entry<K,V> p = (LinkedHashMap.Entry<K,V>)e, 
								 b = p.before, 
								 a = p.after;
		
		// 将被访问元素p的后一个node置为null, 因为要将p放到最后面去, 因此它的后面不能再有数据
        p.after = null;
		
        
		// 下面俩个if 开始进行对p前后俩个数据进行双向绑定
		
		if (b == null)
			// 如果p前面没有数据的话, 就将p后面的数据放到首位去, 然后将p放到最后
            head = a;
        else
			// 如果p前面有数据, 就将p后面的数据和前面的数据连接起来
            b.after = a;
			
		
        if (a != null)
			// 将p前面的数据绑定到p的后面的数据上.
            a.before = b;
        else
			// p后面没有数据, 因为在刚开始的if里已经判断, p不可能是最后一个元素, 但是当只有一个元素时, 可能会出现这种情况
            last = b;
        
		if (last == null)
            head = p;
        else {
			// 进行置换, 将p放到队列尾
            p.before = last;
            last.after = p;
        }
        
		tail = p;
        ++modCount;
    }
}
```
从上面方法的if中我们可以看到,如果accessOrder为true(默认false), 当重新访问数据时, 插入顺序呢是会改变的. 也就是说在调用下列方法时
* put
* replace
* computeIfAbsent
* compute
* merge
* get
被调用的数据会放到队列尾, 因此如果我们将accessOrder置为true, `LinkedHashMap`可以当做LRU Cache使用

> 当继承`LinkedHashMap`, 实现一个LRU Cache的时候, 我们可以重载一下`removeEldestEntry(Map.Entry<K,V> eldest)`这个方法, 当插入新的数据的时候, 可以灵活地处理过期的数据

写个测试程序测试一下
```java
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class TestLinkedHashMap {

	@Test
	public void testHashMap() {
		Map<String, String> map = new HashMap<>();
		for (int i = 0; i < 1000; i++) {
			map.put(i + "", "");
		}
		testStep1("testHashMap", map);
	}

	private Map<String, String> falseAccessOrderMap() {
		LinkedHashMap<String, String> map = new LinkedHashMap<>();
		for (int i = 1; i < 1000; i++) {
			map.put(i + "", "");
		}
		return map;
	}

	private void testStep1(String module, Map<String, String> map) {
		int older = 0;
		for (String string : map.keySet()) {
			int num = Integer.parseInt(string);
			if ((older + 1) != num) {
				System.err.println(module + " : " + (older + 1) + " -> " + num);
				break;
			}
			older = num;
		}
	}

	// 测试 迭代顺序就是将键插入到映射中的顺序（插入顺序）
	@Test
	public void falseAccessOrderLinkedHashMap_SaveOrder() {
		Map<String, String> map = falseAccessOrderMap();
		testStep1("falseAccessOrderLinkedHashMap_SaveOrder", map);
	}

	// 测试 如果在映射中重新插入 键，则插入顺序不受影响
	@Test
	public void falseAccessOrderLinkedHashMap_RePutSaveOrder() {
		Map<String, String> map = falseAccessOrderMap();
		map.put("123", "");
		testStep1("falseAccessOrderLinkedHashMap_RePutSaveOrder", map);
	}

	@Test
	public void falseAccessOrderLinkedHashMap_GetSaveOrder() {
		Map<String, String> map = falseAccessOrderMap();
		map.put("123", "");
		map.put("123", "");
		map.put("123", "");
		testStep1("falseAccessOrderLinkedHashMap_GetSaveOrder", map);
	}

	private Map<String, String> trueAccessOrderMap() {
		LinkedHashMap<String, String> map = new LinkedHashMap<>(10, 0.75f, true);
		for (int i = 1; i < 1000; i++) {
			map.put(i + "", "");
		}
		return map;
	}

	@Test
	public void trueAccessOrderLinkedHashMap_SaveOrder() {
		Map<String, String> map = trueAccessOrderMap();
		testStep1("trueAccessOrderLinkedHashMap_SaveOrder", map);
	}

	@Test
	public void trueAccessOrderLinkedHashMap_RePutSaveOrder() {
		Map<String, String> map = trueAccessOrderMap();
		map.put("123", "");
		testStep1("trueAccessOrderLinkedHashMap_RePutSaveOrder", map);
	}

	@Test
	public void trueAccessOrderLinkedHashMap_GetSaveOrder() {
		Map<String, String> map = trueAccessOrderMap();
		map.put("123", "");
		map.put("123", "");
		map.put("123", "");
		testStep1("trueAccessOrderLinkedHashMap_GetSaveOrder", map);
	}
}
```
运行一下, 结果果真如此.







































