category: Groovy
date: 2014-04-08
title: groovy集合
---
> 本文是对Groovy部分官方文档进行了翻译

Groovy 语言层面上就支持多种集合类型,包括list, map, range. 大多数类型集合都是基于java的集合框架,而且Groovy development kit对这些集合内置很多快捷方法.

### Lists

Groovy使用了一种被`[]`括起来,值通过`,`分割的语法 定义list. Groovy list 采用的是 JDK里`java.util.List`的实现, 因为它自身并没有定义自己的集合类.
Groovy list 的默认实现是`java.util.ArrayList`, 在后面我们可以看到其他形式的list

```groovy
def numbers = [1, 2, 3]         (1)

assert numbers instanceof List  (2)
assert numbers.size() == 3      (3)
```

1. 我们定义了一个Number类型的List,然后将这个list分配给一个变量
2. 判断list是 Java’s `java.util.List` interface 的实例
3. list的大小可以通过size()来进行查询, 例子中也给我们展示了这个list确实包含3个元素

在上面的list中,我们使用的是同类元素的list, 但其实Groovy list中的数据类型还可以不一样：
```groovy
def heterogeneous = [1, "a", true]  (1)
```
1. 我们定义了一个包含有number,string,boolean 三个类型的list

在上面我们提到过, list实际上是`java.util.ArrayList`实例, 但其实list还可以是其他不同类型的实例, 下面我们通过操作符或者显式类型声明来强制指定 list使用不同的List实现
```groovy
def arrayList = [1, 2, 3]
assert arrayList instanceof java.util.ArrayList

def linkedList = [2, 3, 4] as LinkedList    (1)
assert linkedList instanceof java.util.LinkedList

LinkedList otherLinked = [3, 4, 5]          (2)
assert otherLinked instanceof java.util.LinkedList
```
1. 我们使用操作符强制将类型显式地声明为`java.util.LinkedList`
2. 我们使用显式声明方式, 将list声明为`java.util.LinkedList`

我们可以通过`[]`下标操作符来访问list中的元素(读写都可以). 下标既如果是正数的话,那就从左到右访问元素, 如果下标是负数那就从右到左访问元素. 我们好可以使用`<<`操作符向list里追加元素
```groovy
def letters = ['a', 'b', 'c', 'd']

assert letters[0] == 'a'     (1)
assert letters[1] == 'b'

assert letters[-1] == 'd'    (2)
assert letters[-2] == 'c'

letters[2] = 'C'             (3)
assert letters[2] == 'C'

letters << 'e'               (4)
assert letters[ 4] == 'e'
assert letters[-1] == 'e'

assert letters[1, 3] == ['b', 'd']         (5)
assert letters[2..4] == ['C', 'd', 'e']    (6)
```

1. 访问第一个元素(从这可以看出,list的下标是从0开始的)
2. 通过-1 下标访问list中的最后一个元素.
3. 使用下标对list中第三个元素重新赋值
4. 使用`<<`向list尾部添加一个元素
5. 一次性访问list中俩个元素,这个操作的结果是返回一个包含俩个元素的新的list
6. 使用值域符来访问list中一定范围内的值.

由于list支持多种不同类型的元素, 那么list中也可以包含list,这样就可以制造出多维list
```groovy
def multi = [[0, 1], [2, 3]]     (1)
assert multi[1][0] == 2          (2)
```

1. 定义了一个包含Number类型list的list
2. 访问外层的第二个元素(第二个list), 然后访问内部list的第一个元素(第二个list的第一个元素)

#### List literals

你可以像下面这样创建集合, 注意`[]`是空集合表达式.
```groovy
def list = [5, 6, 7, 8]
assert list.get(2) == 7
assert list[2] == 7
assert list instanceof java.util.List

def emptyList = []
assert emptyList.size() == 0
emptyList.add(5)
assert emptyList.size() == 1
```

每一个list表达式都是实现自`java.util.List`

当然list也可以指定其具体的实现类型
```groovy
def list1 = ['a', 'b', 'c']
//construct a new list, seeded with the same items as in list1
def list2 = new ArrayList<String>(list1)

assert list2 == list1 // == checks that each corresponding element is the same

// clone() can also be called
def list3 = list1.clone()
assert list3 == list1
```

list本质上是一个有序的对象集合.
```groovy
def list = [5, 6, 7, 8]
assert list.size() == 4
assert list.getClass() == ArrayList     // the specific kind of list being used

assert list[2] == 7                     // indexing starts at 0
assert list.getAt(2) == 7               // equivalent method to subscript operator []
assert list.get(2) == 7                 // alternative method

list[2] = 9
assert list == [5, 6, 9, 8,]           // trailing comma OK

list.putAt(2, 10)                       // equivalent method to [] when value being changed
assert list == [5, 6, 10, 8]
assert list.set(2, 11) == 10            // alternative method that returns old value
assert list == [5, 6, 11, 8]

assert ['a', 1, 'a', 'a', 2.5, 2.5f, 2.5d, 'hello', 7g, null, 9 as byte]
//objects can be of different types; duplicates allowed

assert [1, 2, 3, 4, 5][-1] == 5             // use negative indices to count from the end
assert [1, 2, 3, 4, 5][-2] == 4
assert [1, 2, 3, 4, 5].getAt(-2) == 4       // getAt() available with negative index...
try {
    [1, 2, 3, 4, 5].get(-2)                 // but negative index not allowed with get()
    assert false
} catch (e) {
    assert e instanceof ArrayIndexOutOfBoundsException
}
```

#### List as a boolean expression

list还可以计算出boolean表达式.
```groovy
assert ![]             // an empty list evaluates as false

//all other lists, irrespective of contents, evaluate as true
assert [1] && ['a'] && [0] && [0.0] && [false] && [null]
```

#### Iterating on a list

可以通过`each`, `eachWithIndex`遍历整个集合.
```groovy
[1, 2, 3].each {
    println "Item: $it" // `it` is an implicit parameter corresponding to the current element
}
['a', 'b', 'c'].eachWithIndex { it, i -> // `it` is the current element, while `i` is the index
    println "$i: $it"
}
```

在遍历的时候,我们经常需要将遍历出来的值经过某些运算,然后再重新放进一个新的list中. 这种操作经常称为映射(mapping), 这种操作通过`collect`方法实现.
```groovy
assert [1, 2, 3].collect { it * 2 } == [2, 4, 6]

// shortcut syntax instead of collect
assert [1, 2, 3]*.multiply(2) == [1, 2, 3].collect { it.multiply(2) }

def list = [0]
// it is possible to give `collect` the list which collects the elements
assert [1, 2, 3].collect(list) { it * 2 } == [0, 2, 4, 6]
assert list == [0, 2, 4, 6]
```

#### Manipulating lists

##### Filtering and searching

[Groovy development kit](http://www.groovy-lang.org/gdk.html)提供了许多强大有趣的方法用来强化标准集合:

```groovy
assert [1, 2, 3].find { it > 1 } == 2           // find 1st element matching criteria
assert [1, 2, 3].findAll { it > 1 } == [2, 3]   // find all elements matching critieria
assert ['a', 'b', 'c', 'd', 'e'].findIndexOf {      // find index of 1st element matching criteria
    it in ['c', 'e', 'g']
} == 2

assert ['a', 'b', 'c', 'd', 'c'].indexOf('c') == 2  // index returned
assert ['a', 'b', 'c', 'd', 'c'].indexOf('z') == -1 // index -1 means value not in list
assert ['a', 'b', 'c', 'd', 'c'].lastIndexOf('c') == 4

assert [1, 2, 3].every { it < 5 }               // returns true if all elements match the predicate
assert ![1, 2, 3].every { it < 3 }
assert [1, 2, 3].any { it > 2 }                 // returns true if any element matches the predicate
assert ![1, 2, 3].any { it > 3 }

assert [1, 2, 3, 4, 5, 6].sum() == 21                // sum anything with a plus() method
assert ['a', 'b', 'c', 'd', 'e'].sum {
    it == 'a' ? 1 : it == 'b' ? 2 : it == 'c' ? 3 : it == 'd' ? 4 : it == 'e' ? 5 : 0
    // custom value to use in sum
} == 15
assert ['a', 'b', 'c', 'd', 'e'].sum { ((char) it) - ((char) 'a') } == 10
assert ['a', 'b', 'c', 'd', 'e'].sum() == 'abcde'
assert [['a', 'b'], ['c', 'd']].sum() == ['a', 'b', 'c', 'd']

// an initial value can be provided
assert [].sum(1000) == 1000
assert [1, 2, 3].sum(1000) == 1006

assert [1, 2, 3].join('-') == '1-2-3'           // String joining
assert [1, 2, 3].inject('counting: ') {
    str, item -> str + item                     // reduce operation
} == 'counting: 123'
assert [1, 2, 3].inject(0) { count, item ->
    count + item
} == 6
```

下面这段代码是由Groovy语言支撑的在集合中找到最大和最小数的例子:
```groovy
def list = [9, 4, 2, 10, 5]
assert list.max() == 10
assert list.min() == 2

// we can also compare single characters, as anything comparable
assert ['x', 'y', 'a', 'z'].min() == 'a'

// we can use a closure to specify the sorting behaviour
def list2 = ['abc', 'z', 'xyzuvw', 'Hello', '321']
assert list2.max { it.size() } == 'xyzuvw'
assert list2.min { it.size() } == 'z'
```

在闭包里,你还可以自定义一个比较规则.
```groovy
Comparator mc = { a, b -> a == b ? 0 : (a < b ? -1 : 1) }

def list = [7, 4, 9, -6, -1, 11, 2, 3, -9, 5, -13]
assert list.max(mc) == 11
assert list.min(mc) == -13

Comparator mc2 = { a, b -> a == b ? 0 : (Math.abs(a) < Math.abs(b)) ? -1 : 1 }


assert list.max(mc2) == -13
assert list.min(mc2) == -1

assert list.max { a, b -> a.equals(b) ? 0 : Math.abs(a) < Math.abs(b) ? -1 : 1 } == -13
assert list.min { a, b -> a.equals(b) ? 0 : Math.abs(a) < Math.abs(b) ? -1 : 1 } == -1
```

##### Adding or removing elements

我们可以使用`[]`去声明一个新的空list, 然后使用`<<`向list追加元素
```groovy
def list = []
assert list.empty

list << 5
assert list.size() == 1

list << 7 << 'i' << 11
assert list == [5, 7, 'i', 11]

list << ['m', 'o']
assert list == [5, 7, 'i', 11, ['m', 'o']]

//first item in chain of << is target list
assert ([1, 2] << 3 << [4, 5] << 6) == [1, 2, 3, [4, 5], 6]

//using leftShift is equivalent to using <<
assert ([1, 2, 3] << 4) == ([1, 2, 3].leftShift(4))
```groovy
We can add to a list in many ways:
```groovy
assert [1, 2] + 3 + [4, 5] + 6 == [1, 2, 3, 4, 5, 6]
// equivalent to calling the `plus` method
assert [1, 2].plus(3).plus([4, 5]).plus(6) == [1, 2, 3, 4, 5, 6]

def a = [1, 2, 3]
a += 4      // creates a new list and assigns it to `a`
a += [5, 6]
assert a == [1, 2, 3, 4, 5, 6]

assert [1, *[222, 333], 456] == [1, 222, 333, 456]
assert [*[1, 2, 3]] == [1, 2, 3]
assert [1, [2, 3, [4, 5], 6], 7, [8, 9]].flatten() == [1, 2, 3, 4, 5, 6, 7, 8, 9]

def list = [1, 2]
list.add(3)
list.addAll([5, 4])
assert list == [1, 2, 3, 5, 4]

list = [1, 2]
list.add(1, 3) // add 3 just before index 1
assert list == [1, 3, 2]

list.addAll(2, [5, 4]) //add [5,4] just before index 2
assert list == [1, 3, 5, 4, 2]

list = ['a', 'b', 'z', 'e', 'u', 'v', 'g']
list[8] = 'x' // the [] operator is growing the list as needed
// nulls inserted if required
assert list == ['a', 'b', 'z', 'e', 'u', 'v', 'g', null, 'x']
```

在list中`+`的语义并没有发生变化,这是何等的重要啊~~~ 与`<<`相比, `+`会创建一个新的list,  但是这个创建的list很可能不是你所预期的, 而且这种方式也可能会导致一些性能问题.

`Groovy development kit`同样提供了很多便捷的方式从list里删除元素:
```groovy
assert ['a','b','c','b','b'] - 'c' == ['a','b','b','b']
assert ['a','b','c','b','b'] - 'b' == ['a','c']
assert ['a','b','c','b','b'] - ['b','c'] == ['a']

def list = [1,2,3,4,3,2,1]
list -= 3           // creates a new list by removing `3` from the original one
assert list == [1,2,4,2,1]
assert ( list -= [2,4] ) == [1,1]
```
同样,你也能通过索引的方式从list里删除元素.
```groovy
def list = [1,2,3,4,5,6,2,2,1]
assert list.remove(2) == 3          // remove the third element, and return it
assert list == [1,2,4,5,6,2,2,1]
```
假设,你如果从list中删除多个相同元素中的第一个, 那你可以调用`remove`方法.
```groovy
def list= ['a','b','c','b','b']
assert list.remove('c')             // remove 'c', and return true because element removed
assert list.remove('b')             // remove first 'b', and return true because element removed

assert ! list.remove('z')           // return false because no elements removed
assert list == ['a','b','b']
```
如果你想要将list清空的话,只需要调用`clear`方法即可
```groovy
def list= ['a',2,'c',4]
list.clear()
assert list == []
```

##### Set operations

`Groovy development kit`还包含很多逻辑运算的方法
```groovy
assert 'a' in ['a','b','c']             // returns true if an element belongs to the list
assert ['a','b','c'].contains('a')      // equivalent to the `contains` method in Java
assert [1,3,4].containsAll([1,4])       // `containsAll` will check that all elements are found

assert [1,2,3,3,3,3,4,5].count(3) == 4  // count the number of elements which have some value
assert [1,2,3,3,3,3,4,5].count {
    it%2==0                             // count the number of elements which match the predicate
} == 2

assert [1,2,4,6,8,10,12].intersect([1,3,6,9,12]) == [1,6,12]

assert [1,2,3].disjoint( [4,6,9] )
assert ![1,2,3].disjoint( [2,4,6] )
```

##### Sorting

Groovy还提供了很多使用闭包比较器的排序操作
```groovy
assert [6, 3, 9, 2, 7, 1, 5].sort() == [1, 2, 3, 5, 6, 7, 9]

def list = ['abc', 'z', 'xyzuvw', 'Hello', '321']
assert list.sort {
    it.size()
} == ['z', 'abc', '321', 'Hello', 'xyzuvw']

def list2 = [7, 4, -6, -1, 11, 2, 3, -9, 5, -13]
assert list2.sort { a, b -> a == b ? 0 : Math.abs(a) < Math.abs(b) ? -1 : 1 } ==
        [-1, 2, 3, 4, 5, -6, 7, -9, 11, -13]

Comparator mc = { a, b -> a == b ? 0 : Math.abs(a) < Math.abs(b) ? -1 : 1 }

// JDK 8+ only
// list2.sort(mc)
// assert list2 == [-1, 2, 3, 4, 5, -6, 7, -9, 11, -13]

def list3 = [6, -3, 9, 2, -7, 1, 5]

Collections.sort(list3)
assert list3 == [-7, -3, 1, 2, 5, 6, 9]

Collections.sort(list3, mc)
assert list3 == [1, 2, -3, 5, 6, -7, 9]
```

##### Duplicating elements

`roovy development kit`还通过重载操作符的方式, 内部提供了一些方法进行list元素复制.
```groovy
assert [1, 2, 3] * 3 == [1, 2, 3, 1, 2, 3, 1, 2, 3]
assert [1, 2, 3].multiply(2) == [1, 2, 3, 1, 2, 3]
assert Collections.nCopies(3, 'b') == ['b', 'b', 'b']

// nCopies from the JDK has different semantics than multiply for lists
assert Collections.nCopies(2, [1, 2]) == [[1, 2], [1, 2]] //not [1,2,1,2]
```

### Arrays

Groovy 数组重用了list符号, 但是如果想要创建数组, 那么就必须强制地显式定义数组类型
```groovy
String[] arrStr = ['Ananas', 'Banana', 'Kiwi']  (1)

assert arrStr instanceof String[]    (2)
assert !(arrStr instanceof List)

def numArr = [1, 2, 3] as int[]      (3)

assert numArr instanceof int[]       (4)
assert numArr.size() == 3
```

1. 使用显式变量类型定义了一个字符串数组
2. 断言刚才创建的数组是否是string类型
3. 使用操作符定义一个int数组
4. 断言刚才创建的数组是否是int类型

我们也可以创建出一个多维数组
```groovy
def matrix3 = new Integer[3][3]         (1)
assert matrix3.size() == 3

Integer[][] matrix2                     (2)
matrix2 = [[1, 2], [3, 4]]
assert matrix2 instanceof Integer[][]
```
1. 我们指定了新数组的边界
2. 当然我们也可以不指定它的边界

访问数组元素和访问list元素的方式相同
```groovy
String[] names = ['Cédric', 'Guillaume', 'Jochen', 'Paul']
assert names[0] == 'Cédric'     (1)

names[2] = 'Blackdrag'          (2)
assert names[2] == 'Blackdrag'
```
1	Retrieve the first element of the array
2	Set the value of the third element of the array to a new value
1. 检索数组中第一个元素
2. 对数组中第三个元素重新赋值

Groovy不支持Java数组初始化语法, 因为Java数组中的花括号可能被会Groovy无解成闭包

### Maps
有时候我们在其他语言中称map为 字典或者关联数组. Map将key和value关联起来, 在Groovy中map被`[]`括起来, 通过`,`分割键值对, 键值通过`:`分割
```groovy
def colors = [red: '#FF0000', green: '#00FF00', blue: '#0000FF']   (1)

assert colors['red'] == '#FF0000'    (2)
assert colors.green  == '#00FF00'    (3)

colors['pink'] = '#FF00FF'           (4)
colors.yellow  = '#FFFF00'           (5)

assert colors.pink == '#FF00FF'
assert colors['yellow'] == '#FFFF00'

assert colors instanceof java.util.LinkedHashMap
```

1. 我们定义了一个string类型的代表颜色名字的数组,
2. 然后使用下标来检索map中是否包含red这个key
3. 我们还可以直接使用`.`来索引到某个key
4. 我们可以使用下标向map中添加一个新的键值对
5. 我们也可以使用`.`添加一个新的键值对

Groovy创建的map类型默认的是`java.util.LinkedHashMap`

当你想要访问一个不存在的key时：
```groovy
assert colors.unknown == null
```
你将检索出一个null的结果

在上面的例子中我们使用的是以string作为key, 但是你还可以使用其他类型作为map的key：

```groovy
def numbers = [1: 'one', 2: 'two']

assert numbers[1] == 'one'
```

我们使用了number作为了map新的key类型, number类型就会直接被解释为number类型, 因此Groovy不会像先前那样创建一个string类型的key. 但是假设你想要传递一个变量作为key,是变量的值作为key：

```groovy
def key = 'name'
def person = [key: 'Guillaume']      (1)

assert !person.containsKey('name')   (2)
assert person.containsKey('key')     (3)
```
1. 与`\'Guillaume'` 关联的key实际上是`"key"`这个字符串, 而不是这个key的引用值`'name'`
2. map中不包含`'name'`key
3. 取而代之的是map中包含一个`"key"`的字符串

你可以向map中传递一个引号字符串作为key,例如`["name": "Guillaume"]`.

```groovy
person = [(key): 'Guillaume']        (1)

assert person.containsKey('name')    (2)
assert !person.containsKey('key')    (3)
```
1	This time, we surround the key variable with parentheses, to instruct the parser we are passing a variable rather than defining a string key
2	The map does contain the name key
3	But the map doesn’t contain the key key as before
1.
2.
3.

#### Map literals

在Groovy中可以使用`[:]` 创建一个map.
```groovy
def map = [name: 'Gromit', likes: 'cheese', id: 1234]
assert map.get('name') == 'Gromit'
assert map.get('id') == 1234
assert map['name'] == 'Gromit'
assert map['id'] == 1234
assert map instanceof java.util.Map

def emptyMap = [:]
assert emptyMap.size() == 0
emptyMap.put("foo", 5)
assert emptyMap.size() == 1
assert emptyMap.get("foo") == 5
```

Map的key默认是`string`, 例如`[a:1]`等同于`['a':1]`. 比较荣誉造成疑惑的就是,如果你创建了一个变量a(值为b), 但是你将变量a`put`进map后, map的key会是a,而不是b. 如果你遇到了这个情况的话,那么你必须对使用`()`key进行转义了.
```groovy
def a = 'Bob'
def ages = [a: 43]
assert ages['Bob'] == null // `Bob` is not found
assert ages['a'] == 43     // because `a` is a literal!

ages = [(a): 43]            // now we escape `a` by using parenthesis
assert ages['Bob'] == 43   // and the value is found!
```

通过下面的方式你可以轻松克隆一个map
```groovy
def map = [
        simple : 123,
        complex: [a: 1, b: 2]
]
def map2 = map.clone()
assert map2.get('simple') == map.get('simple')
assert map2.get('complex') == map.get('complex')
map2.get('complex').put('c', 3)
assert map.get('complex').get('c') == 3
```

#### Map property notation

Maps和beans也是非常相像的, 所以你可以对map使用`get/set`操作元素,当然这也有个前提,那就是map中的key必须是符合Groovy标识符的key.

```groovy
def map = [name: 'Gromit', likes: 'cheese', id: 1234]
assert map.name == 'Gromit'     // can be used instead of map.get('Gromit')
assert map.id == 1234

def emptyMap = [:]
assert emptyMap.size() == 0
emptyMap.foo = 5
assert emptyMap.size() == 1
assert emptyMap.foo == 5
```

注意:`map.foo`总是会在map中查找key`foo`. 这意味着,
```groovy
def map = [name: 'Gromit', likes: 'cheese', id: 1234]
assert map.class == null
assert map.get('class') == null
assert map.getClass() == LinkedHashMap // this is probably what you want

map = [1      : 'a',
       (true) : 'p',
       (false): 'q',
       (null) : 'x',
       'null' : 'z']
assert map.containsKey(1) // 1 is not an identifier so used as is
assert map.true == null
assert map.false == null
assert map.get(true) == 'p'
assert map.get(false) == 'q'
assert map.null == 'z'
assert map.get(null) == 'x'
```

#### Iterating on maps

`Groovy development kit`还提供了`eachWithIndex`方法遍历map.值得注意的是,map会保留put元素的顺序,也就是说,当你遍历一个map的时候,无论进行多少次,你获得的元素的顺序是一定的.
```groovy
def map = [
        Bob  : 42,
        Alice: 54,
        Max  : 33
]

// `entry` is a map entry
map.each { entry ->
    println "Name: $entry.key Age: $entry.value"
}

// `entry` is a map entry, `i` the index in the map
map.eachWithIndex { entry, i ->
    println "$i - Name: $entry.key Age: $entry.value"
}

// Alternatively you can use key and value directly
map.each { key, value ->
    println "Name: $key Age: $value"
}

// Key, value and i as the index in the map
map.eachWithIndex { key, value, i ->
    println "$i - Name: $key Age: $value"
}
```

#### Manipulating maps

##### Adding or removing elements

向map中添加元素你可以使用`put`方法, `下标`, `putAll`方法.
```groovy
def defaults = [1: 'a', 2: 'b', 3: 'c', 4: 'd']
def overrides = [2: 'z', 5: 'x', 13: 'x']

def result = new LinkedHashMap(defaults)
result.put(15, 't')
result[17] = 'u'
result.putAll(overrides)
assert result == [1: 'a', 2: 'z', 3: 'c', 4: 'd', 5: 'x', 13: 'x', 15: 't', 17: 'u']
```

如果想要删除map中全部的元素,可以使用`clear`方法.
```groovy
def m = [1:'a', 2:'b']
assert m.get(1) == 'a'
m.clear()
assert m == [:]
```

通过map字面量标记创建的map会使用`object`的`equals`方法和`hashcode`方法.

还要注意的是,不要使用GString作为map的key, 因为GString的hashcode方法和String的hashcode方法不一样.
```groovy
def key = 'some key'
def map = [:]
def gstringKey = "${key.toUpperCase()}"
map.put(gstringKey,'value')
assert map.get('SOME KEY') == null
```

##### Keys, values and entries

我们可以在视图中inspect`keys, values, and entries`
```groovy
def map = [1:'a', 2:'b', 3:'c']

def entries = map.entrySet()
entries.each { entry ->
  assert entry.key in [1,2,3]
  assert entry.value in ['a','b','c']
}

def keys = map.keySet()
assert keys == [1,2,3] as Set
```

Mutating values returned by the view (be it a map entry, a key or a value) is highly discouraged because success of the operation directly depends on the type of the map being manipulated. In particular, Groovy relies on collections from the JDK that in general make no guarantee that a collection can safely be manipulated through keySet, entrySet, or values.


##### Filtering and searching

The Groovy development kit contains filtering, searching and collecting methods similar to those found for lists:

```groovy
def people = [
    1: [name:'Bob', age: 32, gender: 'M'],
    2: [name:'Johnny', age: 36, gender: 'M'],
    3: [name:'Claire', age: 21, gender: 'F'],
    4: [name:'Amy', age: 54, gender:'F']
]

def bob = people.find { it.value.name == 'Bob' } // find a single entry
def females = people.findAll { it.value.gender == 'F' }

// both return entries, but you can use collect to retrieve the ages for example
def ageOfBob = bob.value.age
def agesOfFemales = females.collect {
    it.value.age
}

assert ageOfBob == 32
assert agesOfFemales == [21,54]

// but you could also use a key/pair value as the parameters of the closures
def agesOfMales = people.findAll { id, person ->
    person.gender == 'M'
}.collect { id, person ->
    person.age
}
assert agesOfMales == [32, 36]

// `every` returns true if all entries match the predicate
assert people.every { id, person ->
    person.age > 18
}

// `any` returns true if any entry matches the predicate

assert people.any { id, person ->
    person.age == 54
}
```

##### Grouping

We can group a list into a map using some criteria:

```groovy
assert ['a', 7, 'b', [2, 3]].groupBy {
    it.class
} == [(String)   : ['a', 'b'],
      (Integer)  : [7],
      (ArrayList): [[2, 3]]
]

assert [
        [name: 'Clark', city: 'London'], [name: 'Sharma', city: 'London'],
        [name: 'Maradona', city: 'LA'], [name: 'Zhang', city: 'HK'],
        [name: 'Ali', city: 'HK'], [name: 'Liu', city: 'HK'],
].groupBy { it.city } == [
        London: [[name: 'Clark', city: 'London'],
                 [name: 'Sharma', city: 'London']],
        LA    : [[name: 'Maradona', city: 'LA']],
        HK    : [[name: 'Zhang', city: 'HK'],
                 [name: 'Ali', city: 'HK'],
                 [name: 'Liu', city: 'HK']],
]
```

### Ranges

Ranges allow you to create a list of sequential values. These can be used as List since Range extends java.util.List.

Ranges defined with the .. notation are inclusive (that is the list contains the from and to value).

Ranges defined with the ..< notation are half-open, they include the first value but not the last value.

```groovy
// an inclusive range
def range = 5..8
assert range.size() == 4
assert range.get(2) == 7
assert range[2] == 7
assert range instanceof java.util.List
assert range.contains(5)
assert range.contains(8)

// lets use a half-open range
range = 5..<8
assert range.size() == 3
assert range.get(2) == 7
assert range[2] == 7
assert range instanceof java.util.List
assert range.contains(5)
assert !range.contains(8)

//get the end points of the range without using indexes
range = 1..10
assert range.from == 1
assert range.to == 10
```

Note that int ranges are implemented efficiently, creating a lightweight Java object containing a from and to value.

Ranges can be used for any Java object which implements java.lang.Comparable for comparison and also have methods next() and previous() to return the next / previous item in the range. For example, you can create a range of String elements:
