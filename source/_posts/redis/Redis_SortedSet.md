category: Reids
date: 2015-11-16
title: Redis SortedSet
---

> 在命令行中使用Redis客户端连接Redis服务器： `redis-cli -h 127.0.0.1 -p 7000`

## 增加成员

### ZADD
语法：`ZADD key score member [[score member] [score member] ...]`.   
* `ZADD` redis命令
* `key` 有序集合名
* `score`  值 (可以是整数值或双精度浮点数)
* `member` 键

这个命令也就是将键值对(member score)插入到有序集合key中. 如果集合不存在就创建一个集合,如果键已经存在就代替原来的值.

示例
```
redis 127.0.0.1:7006> ZADD test1 10 a
(integer) 1
```

## 修改成员

### ZINCRBY
语法`ZINCRBY key increment member`
* `ZINCRBY` redis命令
* `key` 有序集合名
* `increment`  score值的增量
* `member`  针对哪个成员进行改变

这个命令就是对某个成员进行增加或者减少(通过负数实现). (member 成员的新 score 值,以字符串形式表示)

示例
```
redis 127.0.0.1:7006> zadd test1 23 t
(integer) 1
redis 127.0.0.1:7006> ZINCRBY test1 10 t
"33"
redis 127.0.0.1:7006> zincrby test1 -20 t
"13"
```

## 删除成员

### ZREM
语法`ZREM key member [member ...]`
* `ZREM` redis命令
* `key` 有序集合名
* `member` 成员名 

移除有序集 key 中的一个或多个成员,不存在的成员将被忽略.

示例
```
redis 127.0.0.1:7006> zcard test1
(integer) 7
redis 127.0.0.1:7006> zrem test1 a
(integer) 1
redis 127.0.0.1:7006> zcard test1
(integer) 6
```

### ZREMRANGEBYRANK
语法`ZREMRANGEBYRANK key start stop`
* `ZREMRANGEBYRANK` redis命令
* `key` 有序集合名
* `start` 开始索引从0开始(默认闭区间,使用`(`表示开区间)
* `stop`  结束索引从0开始(默认闭区间,使用`(`表示开区间)

移除有序集 key 中,指定排名(rank)区间内的所有成员.

示例
```
redis 127.0.0.1:7006> ZREMRANGEBYRANK test1 1 2 # 将第二名和第三名移除
(integer) 2
```

### ZREMRANGEBYSCORE

语法`ZREMRANGEBYSCORE key min max`
* `ZREMRANGEBYSCORE` redis命令
* `key` 有序集合名
* `min`最小值(默认闭区间,使用`(`表示开区间)
* `max` 最大值(默认闭区间,使用`(`表示开区间)

将集合key里的score值区间为[min,max]的成员删除
> `+`和`-`在 min 参数以及 max 参数中表示正无限和负无限.

示例
```
redis 127.0.0.1:7006> ZREMRANGEBYSCORE test1 10 20
(integer) 1
```

## 合并集合

### ZUNIONSTORE
语法`ZUNIONSTORE destination numkeys key [key ...] [WEIGHTS weight [weight ...]] [AGGREGATE SUM|MIN|MAX]`
* `ZUNIONSTORE` redis命令
* `destination` 有序集合名
* `numkeys`  需要合并的集合数量
* `key`  需要合并的集合
* `WEIGHTS`  指定该值,则在合并的时候,对每个score值都乘以该元素
* `AGGREGATE` 指定并集的结果集的聚合方式

对多个集合采取并集
> AGGREGATE有三种值：A. SUM,将相同的成员的score相加. MIN,取相同成员的最小score值. MAX,取相同成员的最大score值

示例
```
redis 127.0.0.1:7006> zadd a 10 a1 20 a2 30 a3 40 a4 50 a5
(integer) 5
redis 127.0.0.1:7006> zadd b 11 b1 12 b2 13 b3 14 b4 15 b5
(integer) 5
redis 127.0.0.1:7006> ZUNIONSTORE c 2 a b
(integer) 10
redis 127.0.0.1:7006> ZRANGE c 0 -1 WITHSCORES
 1) "a1"
 2) "10"
 3) "b1"
 4) "11"
 5) "b2"
 6) "12"
 7) "b3"
 8) "13"
 9) "b4"
10) "14"
11) "b5"
12) "15"
13) "a2"
14) "20"
15) "a3"
16) "30"
17) "a4"
18) "40"
19) "a5"
20) "50"
redis 127.0.0.1:7006> ZUNIONSTORE e 2 c a
(integer) 10
redis 127.0.0.1:7006> ZRANGE e  0 -1 WITHSCORES
 1) "b1"
 2) "11"
 3) "b2"
 4) "12"
 5) "b3"
 6) "13"
 7) "b4"
 8) "14"
 9) "b5"
10) "15"
11) "a1"
12) "20"
13) "a2"
14) "40"
15) "a3"
16) "60"
17) "a4"
18) "80"
19) "a5"
20) "100"
```

### ZINTERSTORE
语法`ZINTERSTORE destination numkeys key [key ...] [WEIGHTS weight [weight ...]] [AGGREGATE SUM|MIN|MAX]`
* `ZINTERSTORE` redis命令
* `destination` 有序集合名
* `numkeys`  需要合并的集合数量
* `key`  需要合并的集合
* `WEIGHTS`  指定该值,则在合并的时候,对每个score值都乘以该元素
* `AGGREGATE` 指定并集的结果集的聚合方式

对多个集合采取交集
> AGGREGATE有三种值：A. SUM,将相同的成员的score相加. MIN,取相同成员的最小score值. MAX,取相同成员的最大score值


示例
```
redis 127.0.0.1:7006> zadd h 1 a1 2 a2
(integer) 2
redis 127.0.0.1:7006> ZINTERSTORE j 2 e h
(integer) 2
redis 127.0.0.1:7006> zrange j 0 -1 WITHSCORES
1) "a1"
2) "21"
3) "a2"
4) "42"
redis 127.0.0.1:7006> 
```

## 获取集合数量

### ZCARD
语法`ZCARD key`
* `ZCARD` redis命令
* `key` 有序集合名

获得集合大小

示例
```
redis 127.0.0.1:7006> ZCARD test1 
(integer) 2
```

### ZCOUNT
语法`ZCOUNT key min max`
* `ZCOUNT` redis命令
* `key` 有序集合名
* `min`最小值(默认闭区间,使用`(`表示开区间)
* `max` 最大值(默认闭区间,使用`(`表示开区间)
这个命令就是统计score 值在 min 和 max 之间(默认包括 score 值等于 min 或 max )的成员的数量
> `+`和`-`在 min 参数以及 max 参数中表示正无限和负无限.

示例
```
redis 127.0.0.1:7006> ZCOUNT test 10 50
(integer) 6
```

## 获取集合列表

### ZRANGE
语法`ZRANGE key start stop [WITHSCORES]`
* `ZRANGE` redis命令
* `key` 有序集合名
* `start` 开始索引从0开始(默认闭区间,使用`(`表示开区间)
* `stop`  结束索引从0开始(默认闭区间,使用`(`表示开区间)
* `WITHSCORES`  同时也返回成员对应的值

返回有序集key中指定区间内的成员,得到的成员是递增(从小到大)排序的.
> 索引从0开始, 如果索引为负数则代表从倒序,即-1代表最后一个,-2代表倒数第二个. ( `ZRANGE test1 0 -1 WITHSCORES`显示整个有序集成员)

示例
```
redis 127.0.0.1:7006> zrange test1 0 3
1) "a"
2) "c"
3) "t"
4) "b"
redis 127.0.0.1:7006> zrange test1 0 3 WITHSCORES
1) "a"
2) "10"
3) "c"
4) "12"
5) "t"
6) "13"
7) "b"
8) "20"
```

### ZREVRANGE
语法`ZREVRANGE key start stop [WITHSCORES]`
* `ZREVRANGE` redis命令
* `key` 有序集合名
* `start` 开始索引从0开始(默认闭区间,使用`(`表示开区间)
* `stop`  结束索引从0开始(默认闭区间,使用`(`表示开区间)
* `WITHSCORES` 输出score值

和`ZRANGE`命令不同的是它是从按 score 值递减(从大到小)来排列,其他和ZRANGE命令一样

示例
```
redis 127.0.0.1:7006> ZREVRANGE test1 1 100
1) "d"
2) "f"
```

### ZRANGEBYSCORE
语法`ZRANGEBYSCORE key min max [WITHSCORES] [LIMIT offset count]`
* `ZRANGEBYSCORE` redis命令
* `key` 有序集合名
* `min`最小值(默认闭区间,使用`(`表示开区间)
* `max` 最大值(默认闭区间,使用`(`表示开区间)
* `WITHSCORES` 输出score值
* `LIMIT offset count` 

返回有序集key中,score值介于 [min, max]之间(闭区间)的成员,按 score 值递增(从小到大)次序排列
> `+`和`-`在 min 参数以及 max 参数中表示正无限和负无限.

示例
```
redis 127.0.0.1:7006> ZRANGEBYSCORE test1 10 56 WITHSCORES	# 闭区间
 1) "a"
 2) "10"
 3) "c"
 4) "12"
 5) "t"
 6) "13"
 7) "b"
 8) "20"
 9) "f"
10) "42"
11) "d"
12) "56"
redis 127.0.0.1:7006> ZRANGEBYSCORE test1 (10 (56 WITHSCORES # 开区间
1) "c"
2) "12"
3) "t"
4) "13"
5) "b"
6) "20"
7) "f"
8) "42"
redis 127.0.0.1:7006> ZRANGEBYSCORE test1 10 56 WITHSCORES LIMIT 0 3 # 从第一个成员开始选择三个成员
1) "a"
2) "10"
3) "c"
4) "12"
5) "t"
6) "13"
```

### ZREVRANGEBYSCORE
语法`ZREVRANGEBYSCORE key max min [WITHSCORES] [LIMIT offset count]`
* `ZREVRANGEBYSCORE` redis命令
* `key` 有序集合名
* `min`最小值(默认闭区间,使用`(`表示开区间)
* `max` 最大值(默认闭区间,使用`(`表示开区间)
* `WITHSCORES` 输出score值
* `LIMIT offset count`  

除了成员按 score 值递减的次序排列这一点外, ZREVRANGEBYSCORE 命令的其他方面和 ZRANGEBYSCORE 命令一样. 
> `+`和`-`在 min 参数以及 max 参数中表示正无限和负无限.

示例
```
redis 127.0.0.1:7006> ZREVRANGE test1 1 100 WITHSCORES
 1) "f"
 2) "42"
 3) "d"
 4) "40"
 5) "c"
 6) "30"
 7) "b"
 8) "20"
 9) "a"
10) "10"
```

### ZRANGEBYLEX
语法`ZRANGEBYLEX key min max [LIMIT offset count]`
* `ZRANGEBYLEX` redis命令
* `key` 有序集合名
* `min`最小值(默认闭区间,使用`(`表示开区间)
* `max` 最大值(默认闭区间,使用`(`表示开区间)

根据成员进行排序而不是根据score值排序,然后返回[min, max]区间内的成员
> `+`和`-`在 min 参数以及 max 参数中表示正无限和负无限.

示例
```
redis 127.0.0.1:7006> ZRANGEBYLEX test1 10 30
```

## 查询某个成员

### ZRANK
语法`ZRANK key member`
* `ZRANK` redis命令
* `key` 有序集合名
* `member`  成员值

返回有序集 key 中成员 member 的排名.其中有序集成员按 score 值递增(从小到大)顺序排列.（排名从0开始）

示例
```
redis 127.0.0.1:7006> ZRANK test1 d
(integer) 5
```

### ZREVRANK
语法`ZREVRANK key member`
* `ZREVRANK` redis命令
* `key` 有序集合名
* `member`  成员值
除了成员按 score 值递减的次序排列这一点外, ZREVRANK 命令的其他方面和 ZRANK 命令一样.

示例
```
redis 127.0.0.1:7006> ZREVRANK test1 c
(integer) 3
```

### ZSCORE
语法`ZSCORE key member`
* `ZSCORE` redis命令
* `key` 有序集合名
* `member` 成员 

返回有序集 key 中,成员 member 的 score 值.

示例
```
redis 127.0.0.1:7006> ZSCORE test1 a
"10"
```






