category: 数据库
date: 2016-05-10
title: Mysql Secondary Indexes and Generated Virtual Columns
---
[官方文档](http://dev.mysql.com/doc/refman/5.7/en/create-table-secondary-indexes-virtual-columns.html)

As of MySQL 5.7.8, InnoDB supports secondary indexes on generated virtual columns. Other index types are not supported.

A secondary index may be created on one or more virtual columns or on a combination of virtual columns and non-generated virtual columns. Secondary indexes on virtual columns may be defined as UNIQUE.

When a secondary index is created on a generated virtual column, generated column values are materialized in the records of the index. If the index is a covering index (one that includes all the columns retrieved by a query), generated column values are retrieved from materialized values in the index structure instead of computed “on the fly”.

There are additional write costs to consider when using a secondary index on a virtual column due to computation performed when materializing virtual column values in secondary index records during INSERT and UPDATE operations. Even with additional write costs, secondary indexes on virtual columns may be preferable to STORED generated columns, which are materialized in the clustered index, resulting in larger tables that require more disk space and memory. If a secondary index is not defined on a virtual column, there are additional costs for reads, as virtual column values must be computed each time the column's row is examined.

Values of an indexed virtual column are MVCC-logged to avoid unnecessary recomputation of generated column values during rollback or during a purge operation. The data length of logged values is limited by the index key limit of 767 bytes for COMPACT and REDUNDANT row formats, and 3072 bytes for DYNAMIC and COMPRESSED row formats.

Adding or dropping a secondary index on a virtual column is an in-place operation.

A secondary index on a virtual column cannot be used as the index for a foreign key.

Secondary indexes are not supported on virtual columns that have a base column that is referenced in a foreign key constraint and uses ON DELETE CASCADE, ON DELETE SET NULL, ON UPDATE CASCADE, or ON UPDATE SET NULL.

Using a Generated Virtual Column Index to Indirectly Index a JSON Column

As noted elsewhere, JSON columns cannot be indexed directly. To create an index that references such a column indirectly, you can define a generated column that extracts the information that should be indexed, then create an index on the generated column, as shown in this example:
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
(We have wrapped the output from the last statement in this example to fit the viewing area. See Section 9.3.9, “Optimizer Use of Generated Column Indexes”, for the statements used to create and populate the table just shown.)

In MySQL 5.7.9 and later, you can use -> as shorthand for JSON_EXTRACT() to access a value by path from a JSON column value. See Searching and Modifying JSON Values, for information about the JSON path syntax supported by MySQL.

When you use EXPLAIN on a statement containing one or more expressions that use the -> operator, they are translated into the equivalent expressions using JSON_EXTRACT() instead, as shown here in the output from SHOW WARNINGS immediately following this EXPLAIN statement:
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
See the descriptions for the -> operator and JSON_EXTRACT() function (Section 13.16.3, “Functions That Search JSON Values”) for additional information and examples.

This technique also can be used to provide indexes that indirectly reference columns of other types that cannot be indexed directly, such as GEOMETRY columns.
