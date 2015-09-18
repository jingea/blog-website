category: jvm7
title: OQL
---
# OQL

## SELECT
`select`用于确定查询语句从堆转储快照中选择什么内容,如果需要显示堆转储快照中的对象,并且浏览这些对象的引用关系,可以使用`*`:

```sql
SELECT * FEOM java.lang.String
```

####选择特定的显示列
查询也可以选择特定需要显示的字段:
```sql
SELECT toString(s), s.count, s.value From java.lang.String s
```
查询可以十一`@`符号来适应java对象的内存属性访问器
```sql
SELECT toString(s), s.@useHeapSize, s.retainedHeapSize From java.lang.String s
```

####使用列别名

```sql

```



####拼合成为一个对象列表选择项目

```sql

```



####排除重复对象

```sql

```



##FROM

####FROM子句指定需要查询的类

```sql

```



####包含子类

```sql

```



####禁止查询类实例


```sql

```


## WHERE



```sql

```

####>=,<=,>,<[NOT]LIKE,[NOT]IN


```sql

```


####=,!=


```sql

```


####AND


```sql

```


####OR


```sql

```


####文字表达式


```sql

```


##属性访问器
####访问堆存储快照中对象的字段


```sql

```


####访问java bean属性


```sql

```


####钓鱼OQL java语法


```sql

```


####OQL内建函数



```sql

```

##OQL语言的BNF范式


```sql

```

