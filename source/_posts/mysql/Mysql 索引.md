category: MySQL
date: 2015-10-08
title: Mysql 索引
---
一般我们在创建索引的时候都要指定它的索引名字. 当然这个不是必须的.

### 普通索引
```sql
CREATE INDEX idxName ON db1.idtable(id);

CREATE TABLE t1 (
  id int NOT NULL,
  INDEX (id)
)
```

### 唯一索引
与普通索引相比值唯一, 可以有空值
```sql
CREATE UNIQUE INDEX id ON db1.idtable(id);

CREATE TABLE t1 (
  id int NOT NULL,
  UNIQUE (id)
)
```

### 主键索引
与普通索引相比值唯一, 不可以有空值
```sql
CREATE TABLE t1 (
  id int NOT NULL,
  PRIMARY KEY (id)
)
```

### 组合索引
下面我们创建了一个唯一组合索引.
```sql
CREATE TABLE `t1` (
  `id` int(11) NOT NULL,
  `name` varchar(16) NOT NULL,
  UNIQUE KEY `idx` (`id`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
```

### 索引注意事项
* 如果索引上出现`NULL`值(包含组合索引),那么这一行就不会被索引
* 我们要尽可能的使用短索引(指定一个前缀长度),例如对varchar类型进行索引,它的长度是16个字符,但是前6个字符是固定的,那么我们指定前缀长度为6就好了
* 尽量不要对索引进行`like`操作
* 不要在索引列上使用`NOT IN`和`<>`操作