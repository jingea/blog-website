category: 数据库
date: 2016-05-10
title: Mysql Secondary Indexes and Generated Virtual Columns
---
[官方文档](http://dev.mysql.com/doc/refman/5.7/en/create-table-secondary-indexes-virtual-columns.html)

## Generated Columns
generated column是由普通column生成的列.
```sql
CREATE TABLE sum (
  num1 int,
  num2 int,
  sum int AS (num1 + num2)
);
INSERT INTO sum (num1, num2) VALUES(1,1),(3,4);
mysql> SELECT * FROM triangle;
+-------+-------+--------------------+
| num1  | num2  | sum                |
+-------+-------+--------------------+
|     1 |     1 |                  2 |
|     3 |     4 |                  7 |
+-------+-------+--------------------+
```
语法为
```sql
col_name data_type [GENERATED ALWAYS] AS (expression)
  [VIRTUAL | STORED] [UNIQUE [KEY]] [COMMENT comment]
  [[NOT] NULL] [[PRIMARY] KEY]
```
* VIRTUAL: 不存储值到磁盘上
* STORED : 将值存储到磁盘上

## Secondary Indexes
从MySQL5.7.8开始, InnoDB引擎基于自生成(generated virtual columns)的虚拟列支持辅助索引索引(secondary indexes, 并不支持其他索引, 例如簇索引等).

secondary indexe可以基于一个, 多个, 组合virtual columns或者非generated virtual columns生成。当基于virtual column的secondary index可以由`UNIQUE`进行定义.

当基于generated virtual column创建的secondary index, generated column的值就体现在了这个secondary index的记录上. 如果这个索引是一个covering index, generated column值是从索引中已经生成的值进行索引, 而不是再自己计算一遍.
> covering index 查询时检索所有的column.

在执行`INSERT`和`UPDATE`这样的写操作时, 如果用到了基于virtual column的辅助索引时, 那么生成virtual column时会产生额外的性能消耗. 甚至当使用STORED generated columns时, 写操作会带来更多的性能消耗, 还有更多的内存和磁盘消耗. 如果secondary index不是基于virtual column, 当产生读操作时会带来更多的性能消耗, 这是因为virtual column的值每当该column的列被检查时都会计算一次.

当发生回滚或者清除操作时, 被索引过的virtual column已经经过里MVCC-logged, 如此一来就可以以避免再计算一次. logged值的长度是由索引长度限制的, `COMPACT`和`REDUNDANT`是767字节, `DYNAMIC`和`COMPRESSED`是3072个字节.

在virtual column 上增加或者删除secondary index是一个内置的操作.

virtual column 上的secondary index不能成为外键的索引. 同样secondary index的virtual column也不能指向外键, 而且也不能使用如下的语句定义
* ON DELETE CASCADE
* ON DELETE SET NULL
* ON UPDATE CASCADE
* ON UPDATE SET NULL.

## Generated Virtual Column索引JSON Column上
JSON columns是不能被直接索引的. 但是我们可以通过创建一个generated column来间接为JSON columns生成一个索引, 如下例:
```sql
mysql> CREATE TABLE jemp (
    ->     c JSON,
    ->     g INT GENERATED ALWAYS AS (JSON_EXTRACT(c, '$.id')),
    ->     INDEX i (g)
    -> );
Query OK, 0 rows affected (0.28 sec)

mysql> INSERT INTO jemp (c) VALUES
     >   ('{"id": "1", "name": "Fred"}'), ('{"id": "2", "name": "Wilma"}'),
     >   ('{"id": "3", "name": "Barney"}'), ('{"id": "4", "name": "Betty"}');
Query OK, 4 rows affected (0.04 sec)
Records: 4  Duplicates: 0  Warnings: 0

mysql> SELECT JSON_UNQUOTE(JSON_EXTRACT(c, '$.name')) AS name
     >     FROM jemp WHERE g > 2;
+--------+
| name   |
+--------+
| Barney |
| Betty  |
+--------+
2 rows in set (0.00 sec)

mysql> EXPLAIN SELECT JSON_UNQUOTE(JSON_EXTRACT(c, '$.name')) AS name
     >    FROM jemp WHERE g > 2\G
*************************** 1. row ***************************
           id: 1
  select_type: SIMPLE
        table: jemp
   partitions: NULL
         type: range
possible_keys: i
          key: i
      key_len: 5
          ref: NULL
         rows: 2
     filtered: 100.00
        Extra: Using where
1 row in set, 1 warning (0.00 sec)

mysql> SHOW WARNINGS\G
*************************** 1. row ***************************
  Level: Note
   Code: 1003
Message: /* select#1 */ select json_unquote(json_extract(`test`.`jemp`.`c`,'$.name'))
AS `name` from `test`.`jemp` where (`test`.`jemp`.`g` > 2)
1 row in set (0.00 sec)
```
> 关于上例中创建表的更多信息参考 [Section 9.3.9, “Optimizer Use of Generated Column Indexes”]()

在Mysql 5.7.9以后, 你可以使用`->`替代`JSON_EXTRACT()`作为path访问JSON列.

当你使用`EXPLAIN`的语句中包含了一个或者多个`->`操作符时, 它们会被`JSON_EXTRACT()`进行替换.
```sql
mysql> EXPLAIN SELECT c->"$.name"
     > FROM jemp WHERE g > 2\G ORDER BY c->"$.name"
*************************** 1. row ***************************
           id: 1
  select_type: SIMPLE
        table: jemp
   partitions: NULL
         type: range
possible_keys: i
          key: i
      key_len: 5
          ref: NULL
         rows: 2
     filtered: 100.00
        Extra: Using where; Using filesort
1 row in set, 1 warning (0.00 sec)

mysql> SHOW WARNINGS\G
*************************** 1. row ***************************
  Level: Note
   Code: 1003
Message: /* select#1 */ select json_extract(`test`.`jemp`.`c`,'$.name') AS
`c->"$.name"` from `test`.`jemp` where (`test`.`jemp`.`g` > 2) order by
json_extract(`test`.`jemp`.`c`,'$.name')  
1 row in set (0.00 sec)
```
