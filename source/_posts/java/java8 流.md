title: java8 流
---
# 流
java8中新添加的流又称为`Streams API`. 它是对集合（Collection）对象功能的增强，它专注于对集合对象进行各种非常便利、高效的聚合操作（aggregate operation），或者大批量数据操作 (bulk data operation).

接下来我们构建一个流:
```java
Stream.of(1, 2, 3)
      .filter(ele -> ele.equals("123"))
      .count();
```
我们通过上述代码构建流一个流,这里有俩个概念要说:
* 惰性求值方法:像`filter`方法,它只是在刻画Stream,它并不会被调用.(在Stream方法中凡事返回Stream对象的都是这种方法)
* 及早求值方法:像`count`方法,它会从Stream中最终产生值.(在Stream方法中凡事返回空或者另一个值都是这种方法)

## 常用的流操作

### collect()
该方法会产生一个列表,是一个及早求值方法.
```java
Stream.of(1, 2, 3)
      .filter(ele -> ele.equals("123"))
      .collect(Collectors.toList());
```
当然我们还可以调用`Collectors.toSet()`等其他方法,构建其他集合

### map
该操作会将一个流中的值转换为一个新的流
```java
Stream.of(1, 2, 3)
      .map(num -> {
          if (num > 1) {
             return 0;
          } else {
             return 1;
          }
       })
       .collect(Collectors.toSet())
       .forEach(ele -> System.out.println(ele));
```

### filter
遍历数据并检查其中的元素是否符合某种条件

这个操作看起来和`map`很像, 但是`map`是根据操作的结果产生新的流,而`filter`是判断流中的数据是否符合条件保留下来
```java
 Stream.of(1, 2, 3)
                .filter(ele -> {
                    if (ele > 1) {
                        return true;
                    } else {
                        return false;
                    }
                })
                .collect(Collectors.toSet())
                .forEach(ele -> System.out.println(ele));
```

### flatMap
用于Stream替换值然后将多个流连接到一起

首先我们看一种情况,流里有俩个列表
```
 Stream.of(Arrays.asList(1, 2, 3),Arrays.asList(7, 8, 9))
                .collect(Collectors.toSet())
                .forEach(ele -> System.out.println(ele));
                
输出的结果是:
[1, 2, 3]
[7, 8, 9]
```
如果我们想将这俩个列表组合到一起呢?
```java
Stream.of(Arrays.asList(1, 2, 3),Arrays.asList(7, 8, 9))
                    .flatMap(list -> {
                        return list.stream();
                    })
                .collect(Collectors.toSet())
                .forEach(ele -> System.out.println(ele));
```
看到了吧,我们首先讲列表转换成流,然后由`flatMap`操作将流组合到一起

### max
查找流中的最大值
```java
Integer max = Stream.of(1, 2, 3)
                .max(Comparator.comparing(ele -> ele))
                .get();
        System.out.println(max);
```
我们需要向`max`操作中传递一个排序的动作. `Comparator.comparing()`这个静态方法是java8新添加的方法,它实现流一个方便的比较器.以前我们需要比较俩个对象的某项属性的值,现在只需要提供一个取值方法就好了.


### min
查找流中的最小值
```java
Integer min = Stream.of(1, 2, 3)
                .min(Comparator.comparing(ele -> ele))
                .get();
        System.out.println(min);
```
和`max`相似


### reduce
从一组值生成一个值.
```java
Integer sum = Stream.of(1, 2, 3)
                .reduce((inSum, element) -> {
                    return inSum + element;
                }).get();
        System.out.println(sum);
```
`reduce`中的`BinaryOperator`类型的lambda表达式第一个参数是上个元素执行`reduce`操作的结果, 第二个参数是流中的每个元素. 

另外Stream中还有其他的`reduce`操作,可以指定开始结束的的位置


# 元素顺序
在一个有序集合中创建一个流时，流中元素就按照出现的顺序进行排列:
```java
Arrays.asList(1, 2, 3).stream().forEach(ele -> System.out.println(ele));
```
上面这个输出顺序总是`1, 2, 3`.

而如果一个集合本身是无序的话，那么生成的流也是无序的，最后由流生成的集合也是无序的


# 使用收集器
`java.util.stream.Collectors`这是java提供的一种通用的，从流生成复杂值结构的收集器.

## 转换成其他集合
Collectors提供了转换成其他集合的方式

* `Collectors.toCollection()`： 接受一个函数作为参数，来创建集合
* `Collectors.toConcurrentMap()`
* `Collectors.toList()`： 不需要指定具体的类型，Stream会自动挑选出合适的类型
* `Collectors.toMap()`
* `Collectors.toSet()`： 不需要指定具体的类型，Stream会自动挑选出合适的类型

## groupingBy
数据分组

lambda表达式类型
```java
public interface Function<T, R> {
    R apply(T t);
}
```
示例：
```java
List<Integer> list = Arrays.asList(1, 10, 2, 3, 2, 9, 4, 5, 11, 6, 7, 8);
		Map<Integer, List<Integer>> group = list.stream().collect(Collectors.groupingBy(ele -> {
			if (ele > 5) {
				return 1;
			} else {
				return 2;
			}
		}));

// 最后结果为
{1:[
		10,
		9,
		11,
		6,
		7,
		8
	],
 2:[
		1,
		2,
		3,
		2,
		4,
		5
	]
}
```
当然分组的key,我们还可以取其他的类型,这完全取决于我们的返回值
```java
Map<String, List<Integer>> group = list.stream().collect(Collectors.groupingBy(ele -> {
			if (ele > 5) {
				return "1";
			} else {
				return "2";
			}
		}));
```

## groupingByConcurrent
并发版本的`group by`实现

lambda表达式类型
```java
public interface Function<T, R> {
    R apply(T t);
｝
```
示例：
```java
List<Integer> list = Arrays.asList(1, 10, 2, 3, 2);
ConcurrentMap<Integer, List<Integer>> result = list.stream().collect(Collectors.groupingByConcurrent(ele -> ele - 3));

// 结果
{-2:[1],
 -1:[
		2,
		2
	],
  0:[3],
  7:[10]
}
```

## partitioningBy
数据分组,key为`True`和`False`

lambda表达式类型
```java
public interface Predicate<T> {
    boolean test(T t);
｝
```
示例：
```java
List<Integer> list = Arrays.asList(1, 10, 2, 3, 2, 9, 4, 5, 11, 6, 7, 8);
Map<Boolean, List<Integer>> result = list.stream().collect(Collectors.partitioningBy(ele -> ele == 1));
System.out.println(JSON.toJSONString(result, true));

// 结果为
{false:[
		10,
		2,
		3,
		2,
		9,
		4,
		5,
		11,
		6,
		7,
		8
	],
true:[1]
}
```

## averagingDouble

lambda表达式类型
```java
public interface ToDoubleFunction<T> {
    double applyAsDouble(T value);
}
```
示例：
```java
List<Integer> list = Arrays.asList(1, 10, 2, 3, 2);
Double result = list.stream().collect(Collectors.averagingDouble(ele -> ele - 3));

// 结果
0.6
```

## averagingInt

lambda表达式类型
```java
public interface ToIntFunction<T> {
    int applyAsInt(T value);
}
```
示例：
```java
List<Integer> list = Arrays.asList(1, 10, 2, 3, 2);
Double result = list.stream().collect(Collectors.averagingInt(ele -> ele - 3));
// 结果
0.6
```

## averagingLong
对流中数据进行进行平均数操作

lambda表达式类型
```java
public interface ToLongFunction<T> {
    long applyAsLong(T value);
}
```
示例：
```java
List<Integer> list = Arrays.asList(1, 10, 2, 3, 2, 9, 4, 5, 11, 6, 7, 8);
Double result = list.stream().collect(Collectors.averagingLong(ele -> ele));
```

## summarizingInt

lambda表达式类型
```java
public interface ToIntFunction<T> {
    int applyAsInt(T value);
}
```
示例：
```java
List<Integer> list = Arrays.asList(1, 10, 2, 3, 2, 9, 4, 5, 11, 6, 7, 8);
IntSummaryStatistics result = list.stream().collect(Collectors.summarizingInt(ele -> ele));

// 结果为
{
	"average":5.666666666666667,
	"count":12,
	"max":11,
	"min":1,
	"sum":68
}
```

## summarizingLong
统计流中数据分布

lambda表达式类型
```java
public interface ToLongFunction<T> {
    long applyAsLong(T value);
}
```
示例：
```java
List<Integer> list = Arrays.asList(1, 10, 2, 3, 2, 9, 4, 5, 11, 6, 7, 8);
LongSummaryStatistics result = list.stream().collect(Collectors.summarizingLong(ele -> ele));

// 结果为
{
	"average":5.666666666666667,
	"count":12,
	"max":11,
	"min":1,
	"sum":68
}
```

## summarizingDouble
统计流中数据分布

lambda表达式类型
```java
public interface ToDoubleFunction<T> {
    double applyAsDouble(T value);
}
```
示例：
```java
List<Integer> list = Arrays.asList(1, 10, 2, 3, 2, 9, 4, 5, 11, 6, 7, 8);
DoubleSummaryStatistics result = list.stream().collect(Collectors.summarizingDouble(ele -> ele));

// 结果
{
	"average":5.666666666666667,
	"count":12,
	"max":11,
	"min":1,
	"sum":68
}
```

## counting
统计流中数据数量

示例：
```java
List<Integer> list = Arrays.asList(1, 10, 2, 3, 2, 9, 4, 5, 11, 6, 7, 8);
Long result = list.stream().collect(Collectors.counting());
```

## maxBy
取出一个列表中的最大值,不过我们要自己定义一个对比规则

lambda表达式类型
```java
public interface Comparator<T> {
    int compare(T o1, T o2);
}
```
示例：
```java
List<Integer> list = Arrays.asList(1, 10, 2, 3, 2, 9, 4, 5, 11, 6, 7, 8);
Optional<Integer> result = list.stream().collect(Collectors.maxBy((ele1, ele2) -> ele1 - ele2));
```

## minBy
取出一个列表中的最大值,不过我们要自己定义一个对比规则

lambda表达式类型
```java
public interface Comparator<T> {
    int compare(T o1, T o2);
}
```
示例：
```java
List<Integer> list = Arrays.asList(1, 10, 2, 3, 2, 9, 4, 5, 11, 6, 7, 8);
Optional<Integer> result = list.stream().collect(Collectors.minBy((ele1, ele2) -> ele1 - ele2));
```

## summingDouble
对列表数据取和操作,结果为`Double`

lambda表达式类型
```java
public interface ToDoubleFunction<T> {
    double applyAsDouble(T value);
}
```
示例：
```java
List<Integer> list = Arrays.asList(1, 10, 2, 3, 2, 9, 4, 5, 11, 6, 7, 8);
Double result = list.stream().collect(Collectors.summingDouble(ele -> ele));
// 结果为68

```

## summingInt
对列表数据取和操作,结果为`Int`

lambda表达式类型
```java
public interface ToIntFunction<T> {
    int applyAsInt(T value);
}
```
示例：
```java
List<Integer> list = Arrays.asList(1, 10, 2, 3, 2, 9, 4, 5, 11, 6, 7, 8);
Integer result = list.stream().collect(Collectors.summingInt(ele -> ele));
```

## summingLong
对列表数据取和操作,结果为`Long`

lambda表达式类型
```java
public interface ToLongFunction<T> {
    long applyAsLong(T value);
}
```
示例：
```java
List<Integer> list = Arrays.asList(1, 10, 2, 3, 2, 9, 4, 5, 11, 6, 7, 8);
Long result = list.stream().collect(Collectors.summingLong(ele -> ele));
```

## joining
将流中的数据拼接成一个字符串. 需要注意的是如果流中的数据不是`String`类型的数据,可以通过`map`操作将流中数据转换成`String`类型

lambda表达式类型
```java
public interface Function<T, R> {
    R apply(T t);
}
```

示例：
```java
List<String> list = Arrays.asList("1", "10", "2", "3", "2");
String result = list.stream().collect(Collectors.joining(",", "(", ")"));
// 结果
"(1,10,2,3,2)"
```

## reducing

lambda表达式类型
```java
R apply(T t, U u);
```
示例：
```java

```


## mapping

lambda表达式类型
```java
public interface Function<T, R> {
    R apply(T t);
}
```
示例：
```java
List<String> list = Arrays.asList("1", "10", "2", "3", "2");
List<String> result = list.stream().collect(Collectors.mapping(ele -> ele + 1, Collectors.toList()));
// 结果
[
	"11",
	"101",
	"21",
	"31",
	"21"
]
```


