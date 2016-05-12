category: MySQL
date: 2016-05-10
title: Mysql JSON Data Type
---
[官方文档](http://dev.mysql.com/doc/refman/5.7/en/json.html)

## Creating JSON Values
在Mysql中, JSON是通过字符串进行存储的. 

These contexts include inserting a value into a column that has the JSON data type and passing an argument to a function that expects a JSON value, as the following examples demonstrate:

下面的例子演示了创建JSON类型字段的表, 以及插入一个JSON串和插入一个非法的JSON串
```sql
mysql> CREATE TABLE t1 (jdoc JSON);
Query OK, 0 rows affected (0.20 sec)

mysql> INSERT INTO t1 VALUES('{"key1": "value1", "key2": "value2"}');
Query OK, 1 row affected (0.01 sec)

mysql> INSERT INTO t1 VALUES('[1, 2,');
ERROR 3140 (22032) at line 2: Invalid JSON text: "Invalid value." at position 6 in value (or column) '[1, 2,'.
```

Positions for “at position N” in such error messages are 0-based, but should be considered rough indications of where the problem in a value actually occurs.

`JSON_TYPE()`方法接受一个JSON串, 然后尝试解析它, 最后返回该JSON的数据类型
```sql
mysql> SELECT JSON_TYPE('["a", "b", 1]');
+----------------------------+
| JSON_TYPE('["a", "b", 1]') |
+----------------------------+
| ARRAY                      |
+----------------------------+

mysql> SELECT JSON_TYPE('"hello"');
+----------------------+
| JSON_TYPE('"hello"') |
+----------------------+
| STRING               |
+----------------------+

mysql> SELECT JSON_TYPE('hello');
ERROR 3146 (22032): Invalid data type for JSON data in argument 1
to function json_type; a JSON string or JSON type is required.
```
MySQL 使用`utf8mb4`编码和`utf8mb4_bin`集合处理JSON 字符串内容. 其他的编码会被转换成utf8mb4编码. (ascii 和 utf8 编码并不会进行转换, 因为这俩个字符集是utf8mb4的子集.)

As an alternative to writing JSON values using literal strings, functions exist for composing JSON values from component elements. JSON_ARRAY() takes a (possibly empty) list of values and returns a JSON array containing those values:
在Mysql中可以将不同的数据类型数据写入到JSON字符串中, 例如可以将字面量写入到JSON字符串的函数. 

`JSON_ARRAY()`函数接受一个参数列表(个数大于等于0), 然后返回一个JSON字符串数组.
```sql
mysql> SELECT JSON_ARRAY('a', 1, NOW());
+----------------------------------------+
| JSON_ARRAY('a', 1, NOW())              |
+----------------------------------------+
| ["a", 1, "2015-07-27 09:43:47.000000"] |
+----------------------------------------+
```

`JSON_OBJECT()`接受一个key/value形式的参数列表, 返回一个包含那些元素的JSON对象:
```sql
mysql> SELECT JSON_OBJECT('key1', 1, 'key2', 'abc');
+---------------------------------------+
| JSON_OBJECT('key1', 1, 'key2', 'abc') |
+---------------------------------------+
| {"key1": 1, "key2": "abc"}            |
+---------------------------------------+
```

`JSON_MERGE()` 将多个JSON串组合到一起,然后返回一个总的JSON串:
```sql
mysql> SELECT JSON_MERGE('["a", 1]', '{"key": "value"}');
+--------------------------------------------+
| JSON_MERGE('["a", 1]', '{"key": "value"}') |
+--------------------------------------------+
| ["a", 1, {"key": "value"}]                 |
+--------------------------------------------+
```
> 关于更多的合并规则,参考下面的 Normalization, Merging, and Autowrapping of JSON Values 章节.

也可以将JSON赋给一个用户自定义的变量
```sql
mysql> SET @j = JSON_OBJECT('key', 'value');
mysql> SELECT @j;
+------------------+
| @j               |
+------------------+
| {"key": "value"} |
+------------------+
```
在上例中, 尽管`JSON_OBJECT()`方法会返回一个JSON类型对象, 但是当将其赋给一个变量(`@j`)时, 它就被自动转换成了一个字符串类型.

JSON转换成的字符串, 它的编码是`utf8mb4`, 字符序为`utf8mb4_bin`:
```sql
mysql> SELECT CHARSET(@j), COLLATION(@j);
+-------------+---------------+
| CHARSET(@j) | COLLATION(@j) |
+-------------+---------------+
| utf8mb4     | utf8mb4_bin   |
+-------------+---------------+
```
因为`utf8mb4_bin`是一种二进制的字符序, 因此在对比俩个JSON值是区分大小写的.
```sql
mysql> SELECT JSON_ARRAY('x') = JSON_ARRAY('X');
+-----------------------------------+
| JSON_ARRAY('x') = JSON_ARRAY('X') |
+-----------------------------------+
|                                 0 |
+-----------------------------------+
```
区分大小写同样支持JSON的`null`, `true`, `false`等字面量. 因此在引用他们的时候一定要小写.
```sql
mysql> SELECT JSON_VALID('null'), JSON_VALID('Null'), JSON_VALID('NULL');
+--------------------+--------------------+--------------------+
| JSON_VALID('null') | JSON_VALID('Null') | JSON_VALID('NULL') |
+--------------------+--------------------+--------------------+
|                  1 |                  0 |                  0 |
+--------------------+--------------------+--------------------+

mysql> SELECT CAST('null' AS JSON);
+----------------------+
| CAST('null' AS JSON) |
+----------------------+
| null                 |
+----------------------+
1 row in set (0.00 sec)

mysql> SELECT CAST('NULL' AS JSON);
ERROR 3141 (22032): Invalid JSON text in argument 1 to function cast_as_json:
"Invalid value." at position 0 in 'NULL'.
```
JSON字面量区分大小写与SQL中的不同. 在SQL中`NULL, TRUE, FALSE`等字面量可以写成由任意大小写组成:
```sql
mysql> SELECT ISNULL(null), ISNULL(Null), ISNULL(NULL);
+--------------+--------------+--------------+
| ISNULL(null) | ISNULL(Null) | ISNULL(NULL) |
+--------------+--------------+--------------+
|            1 |            1 |            1 |
+--------------+--------------+--------------+
```

## Normalization, Merging, and Autowrapping of JSON Values
当一个字符串可以解析成一个有效的JSON文档, 它同时也会进行归一化处理. 当JSON中出现重复的Key时, 只会保留最开始的那个Key/Value, 接下来重复出现的都会抛弃掉.
```sql
mysql> SELECT JSON_OBJECT('key1', 1, 'key2', 'abc', 'key1', 'def');
+------------------------------------------------------+
| JSON_OBJECT('key1', 1, 'key2', 'abc', 'key1', 'def') |
+------------------------------------------------------+
| {"key1": 1, "key2": "abc"}                           |
+------------------------------------------------------+
```
Mysql的归一化处理还会对JSON对象的key进行排序处理(以便查找时提供更好的性能). The result of this ordering is subject to change and not guaranteed to be consistent across releases. 另外, key或者value之间的空格会自动的被忽略掉.

同样的, Mysql中创建JSON的方法同样也都做了归一化处理.

当多个数组合并成一个数组时,
In contexts that combine multiple arrays, the arrays are merged into a single array by concatenating arrays named later to the end of the first array. In the following example, JSON_MERGE() merges its arguments into a single array:
```sql
mysql> SELECT JSON_MERGE('[1, 2]', '["a", "b"]', '[true, false]');
+-----------------------------------------------------+
| JSON_MERGE('[1, 2]', '["a", "b"]', '[true, false]') |
+-----------------------------------------------------+
| [1, 2, "a", "b", true, false]                       |
+-----------------------------------------------------+
```

多个对象合并到一个对象中的时候, 如果多个对象中都出现了相同的key, 那么相同的key对应的value值会被放到该key对应的数组中.
```sql
mysql> SELECT JSON_MERGE('{"a": 1, "b": 2}', '{"c": 3, "a": 4}');
+----------------------------------------------------+
| JSON_MERGE('{"a": 1, "b": 2}', '{"c": 3, "a": 4}') |
+----------------------------------------------------+
| {"a": [1, 4], "b": 2, "c": 3}                      |
+----------------------------------------------------+
```
当非数组类型的数据出现在要求数组为参数的上下文中时, 非数组类型的数据会自动被包装成数组类型(会自动在数据俩侧添加`[]`将其括起来). 在下面的例子中, 每一个参数都会被自动包装成([1], [2]), 然后产生一个新的数组.
```sql
mysql> SELECT JSON_MERGE('1', '2');
+----------------------+
| JSON_MERGE('1', '2') |
+----------------------+
| [1, 2]               |
+----------------------+
```
当对象和数组进行合并时, 对象会自动的包装成一个数组, 然后将这俩个数组进行合并
```sql
mysql> SELECT JSON_MERGE('[10, 20]', '{"a": "x", "b": "y"}');
+------------------------------------------------+
| JSON_MERGE('[10, 20]', '{"a": "x", "b": "y"}') |
+------------------------------------------------+
| [10, 20, {"a": "x", "b": "y"}]                 |
+------------------------------------------------+
```

## Searching and Modifying JSON Values
我们可以在JSON文档中通过指定path来搜索出一个值.

在相关方法中使用表达式可以提取数据,或者修改JSON文档 以及进行其他的操作. 例如下面的操作就是从JSON文档中提取key为name的值.
Path expressions are useful with functions that extract parts of or modify a JSON document, to specify where within that document to operate. For example, the following query extracts from a JSON document the value of the member with the name key:
```sql
mysql> SELECT JSON_EXTRACT('{"id": 14, "name": "Aztalan"}', '$.name');
+---------------------------------------------------------+
| JSON_EXTRACT('{"id": 14, "name": "Aztalan"}', '$.name') |
+---------------------------------------------------------+
| "Aztalan"                                               |
+---------------------------------------------------------+
```
Path语法使用`$`符作为开头, 
Path syntax uses a leading $ character to represent the JSON document under consideration, optionally followed by selectors that indicate successively more specific parts of the document:

* A period followed by a key name names the member in an object with the given key. The key name must be specified within double quotation marks if the name without quotes is not legal within path expressions (for example, if it contains a space).
* `[N]` appended to a path that selects an array names the value at position `N` within the array. Array positions are integers beginning with zero.
* Paths 可以包含 `*`或者`**` 通配符.
> `.[*]` evaluates to the values of all members in a JSON object. `[*]` evaluates to the values of all elements in a JSON array. `prefix**suffix` evaluates to all paths that begin with the named prefix and end with the named suffix.
* A path that does not exist in the document (evaluates to nonexistent data) evaluates to NULL.

下面我们创建出三个元素的数组, 然后假设 `$` 指向这个数组:
```json
[3, {"a": [5, 6], "b": 10}, [99, 100]]
```
那么:
* `$[0]` 求值为 3.
* `$[1]` 求值为 {"a": [5, 6], "b": 10}.
* `$[2]` 求值为[99, 100].
* `$[3]` 求值为 NULL (指向一个不存在的元素).

因为 $[1] 和 $[2] 是非标量的值, 因此我们可以进一步的使用path表达式求出它内嵌的值. 例如:
* `$[1].a` 求值为 [5, 6].
* `$[1].a[1]` 求值为 6.
* `$[1].b` 求值为 10.
* `$[2][0]` 求值为 99.

刚才我们也提到了, path表达式的key必须被`""`包含起来, 未被`""`包含起来的key会被视为非法的.
```json
{"a fish": "shark", "a bird": "sparrow"}
```
这俩个key都包含了一个空格, 因此在path表达式中, 必须使用`""`将key包含:

* `$."a fish"` 求值为 shark.
* `$."a bird"` 求值为 to sparrow.

如果在对数组求值时, path中的通配符会求值出多个结果.
Paths that use wildcards evaluate to an array that can contain multiple values:
```sql
mysql> SELECT JSON_EXTRACT('{"a": 1, "b": 2, "c": [3, 4, 5]}', '$.*');
+---------------------------------------------------------+
| JSON_EXTRACT('{"a": 1, "b": 2, "c": [3, 4, 5]}', '$.*') |
+---------------------------------------------------------+
| [1, 2, [3, 4, 5]]                                       |
+---------------------------------------------------------+
mysql> SELECT JSON_EXTRACT('{"a": 1, "b": 2, "c": [3, 4, 5]}', '$.c[*]');
+------------------------------------------------------------+
| JSON_EXTRACT('{"a": 1, "b": 2, "c": [3, 4, 5]}', '$.c[*]') |
+------------------------------------------------------------+
| [3, 4, 5]                                                  |
+------------------------------------------------------------+
```
在下面的例子中, `$**.b`会在多个path($.a.b 和 $.c.b)中进行求值, 然后将求值结果放到一个数组中:
```sql
mysql> SELECT JSON_EXTRACT('{"a": {"b": 1}, "c": {"b": 2}}', '$**.b');
+---------------------------------------------------------+
| JSON_EXTRACT('{"a": {"b": 1}, "c": {"b": 2}}', '$**.b') |
+---------------------------------------------------------+
| [1, 2]                                                  |
+---------------------------------------------------------+
```
在MySQL 5.7.9 和以后的版本中, 你可以使用`column->path`代替方法`JSON_EXTRACT(column, path)`. 
> 更多参考See Section 13.16.3, “Functions That Search JSON Values” 以及 Section 14.1.18.6, “Secondary Indexes and Generated Virtual Columns”.

在一些方法中, 会接受一个JSON文档, 然后对该JSON文档进行一些处理. 例如
* `JSON_SET()`
* `JSON_INSERT()` 
* `JSON_REPLACE()`
这些方法接受一个或者多个KV, 
Some functions take an existing JSON document, modify it in some way, and return the resulting modified document. Path expressions indicate where in the document to make changes. For example, the JSON_SET(), JSON_INSERT(), and JSON_REPLACE() functions each take a JSON document, plus one or more path/value pairs that describe where to modify the document and the values to use. The functions differ in how they handle existing and nonexisting values within the document.

我们生成一个JSON文档, 然后在下面的操作中使用这个文档:
```sql
mysql> SET @j = '["a", {"b": [true, false]}, [10, 20]]';
```
`JSON_SET()`会对已经存在的path替换, 不存在的进行添加:
```sql
mysql> SELECT JSON_SET(@j, '$[1].b[0]', 1, '$[2][2]', 2);
+--------------------------------------------+
| JSON_SET(@j, '$[1].b[0]', 1, '$[2][2]', 2) |
+--------------------------------------------+
| ["a", {"b": [1, false]}, [10, 20, 2]]      |
+--------------------------------------------+
```
在上例中`$[1].b[0]`选择了一个已经存在的value，然后它被替换成了1. 但是`$[2][2]` 并不存在, 所以就在`$[2][2]`插入了值2.

`JSON_INSERT()`向JSON文档中插入新的值, 但是如果path已经存在, 则会归一化处理, 不会覆盖原有的值:
```sql
mysql> SELECT JSON_INSERT(@j, '$[1].b[0]', 1, '$[2][2]', 2);
+-----------------------------------------------+
| JSON_INSERT(@j, '$[1].b[0]', 1, '$[2][2]', 2) |
+-----------------------------------------------+
| ["a", {"b": [true, false]}, [10, 20, 2]]      |
+-----------------------------------------------+
```
`JSON_REPLACE()`执行替换操作, 但是如果path不存在的话, 不会进行插入操作:
```sql
mysql> SELECT JSON_REPLACE(@j, '$[1].b[0]', 1, '$[2][2]', 2);
+------------------------------------------------+
| JSON_REPLACE(@j, '$[1].b[0]', 1, '$[2][2]', 2) |
+------------------------------------------------+
| ["a", {"b": [1, false]}, [10, 20]]             |
+------------------------------------------------+
```
The path/value pairs are evaluated left to right. The document produced by evaluating one pair becomes the new value against which the next pair is evaluated.

`JSON_REMOVE()` 接受一个 JSON 文档以及一个或者多个要删除的path.  The return value is the original document minus the values selected by paths that exist within the document:
```sql
mysql> SELECT JSON_REMOVE(@j, '$[2]', '$[1].b[1]', '$[1].b[1]');
+---------------------------------------------------+
| JSON_REMOVE(@j, '$[2]', '$[1].b[1]', '$[1].b[1]') |
+---------------------------------------------------+
| ["a", {"b": [true]}]                              |
+---------------------------------------------------+
```
这三个path产生了如下的效果
* `$[2]`找到匹配[10, 20]值, 然后将其删除掉.
* 第一个`$[1].b[1]`匹配到了false值, 然后将其删除掉.
* 第二个`$[1].b[1]`没有匹配到任何值, 因此该操作不会有任何结果.

## Comparison and Ordering of JSON Values

JSON文档里面的value可以通过如下操作符进行比较操作
* =, 
* <, 
* <=, 
* >, 
* >=, 
* <>, 
* !=, 
* <=> 

The following comparison operators and functions are not yet supported with JSON values:

* BETWEEN
* IN()
* GREATEST()
* LEAST()

A workaround for the comparison operators and functions just listed is to cast JSON values to a native MySQL numeric or string data type so they have a consistent non-JSON scalar type.

Comparison of JSON values takes place at two levels. The first level of comparison is based on the JSON types of the compared values. If the types differ, the comparison result is determined solely by which type has higher precedence. If the two values have the same JSON type, a second level of comparison occurs using type-specific rules.

The following list shows the precedences of JSON types, from highest precedence to the lowest. (The type names are those returned by the JSON_TYPE() function.) Types shown together on a line have the same precedence. Any value having a JSON type listed earlier in the list compares greater than any value having a JSON type listed later in the list.

* BLOB
* BIT
* OPAQUE
* DATETIME
* TIME
* DATE
* BOOLEAN
* ARRAY
* OBJECT
* STRING
* INTEGER, DOUBLE
* NULL
For JSON values of the same precedence, the comparison rules are type specific:

### BLOB

The first N bytes of the two values are compared, where N is the number of bytes in the shorter value. If the first N bytes of the two values are identical, the shorter value is ordered before the longer value.

### BIT

Same rules as for BLOB.

### OPAQUE

Same rules as for BLOB. OPAQUE values are values that are not classified as one of the other types.

### DATETIME

A value that represents an earlier point in time is ordered before a value that represents a later point in time. If two values originally come from the MySQL DATETIME and TIMESTAMP types, respectively, they are equal if they represent the same point in time.

### TIME

The smaller of two time values is ordered before the larger one.

### DATE

The earlier date is ordered before the more recent date.

### ARRAY

Two JSON arrays are equal if they have the same length and values in corresponding positions in the arrays are equal.

If the arrays are not equal, their order is determined by the elements in the first position where there is a difference. The array with the smaller value in that position is ordered first. If all values of the shorter array are equal to the corresponding values in the longer array, the shorter array is ordered first.

Example:

[] < ["a"] < ["ab"] < ["ab", "cd", "ef"] < ["ab", "ef"]
BOOLEAN

The JSON false literal is less than the JSON true literal.

### OBJECT

Two JSON objects are equal if they have the same set of keys, and each key has the same value in both objects.

Example:

{"a": 1, "b": 2} = {"b": 2, "a": 1}
The order of two objects that are not equal is unspecified but deterministic.

### STRING

Strings are ordered lexically on the first N bytes of the utf8mb4 representation of the two strings being compared, where N is the length of the shorter string. If the first N bytes of the two strings are identical, the shorter string is considered smaller than the longer string.

Example:

"a" < "ab" < "b" < "bc"
This ordering is equivalent to the ordering of SQL strings with collation utf8mb4_bin. Because utf8mb4_bin is a binary collation, comparison of JSON values is case sensitive:

"A" < "a"

### INTEGER, DOUBLE

JSON values can contain exact-value numbers and approximate-value numbers. For a general discussion of these types of numbers, see Section 10.1.2, “Number Literals”.

The rules for comparing native MySQL numeric types are discussed in Section 13.2, “Type Conversion in Expression Evaluation”, but the rules for comparing numbers within JSON values differ somewhat:

In a comparison between two columns that use the native MySQL INT and DOUBLE numeric types, respectively, it is known that all comparisons involve an integer and a double, so the integer is converted to double for all rows. That is, exact-value numbers are converted to approximate-value numbers.

On the other hand, if the query compares two JSON columns containing numbers, it cannot be known in advance whether numbers will be integer or double. To provide the most consistent behavior across all rows, MySQL converts approximate-value numbers to exact-value numbers. The resulting ordering is consistent and does not lose precision for the exact-value numbers. For example, given the scalars 9223372036854775805, 9223372036854775806, 9223372036854775807 and 9.223372036854776e18, the order is such as this:

9223372036854775805 < 9223372036854775806 < 9223372036854775807
< 9.223372036854776e18 = 9223372036854776000 < 9223372036854776001
Were JSON comparisons to use the non-JSON numeric comparison rules, inconsistent ordering could occur. The usual MySQL comparison rules for numbers yield these orderings:

Integer comparison:

9223372036854775805 < 9223372036854775806 < 9223372036854775807
(not defined for 9.223372036854776e18)

Double comparison:

9223372036854775805 = 9223372036854775806 = 9223372036854775807 = 9.223372036854776e18
For comparison of any JSON value to SQL NULL, the result is UNKNOWN.

For comparison of JSON and non-JSON values, the non-JSON value is converted to JSON according to the rules in the following table, then the values compared as described previously.

Converting between JSON and non-JSON values.  The following table provides a summary of the rules that MySQL follows when casting between JSON values and values of other types:

Converting between JSON and non-JSON values.  The following table provides a summary of the rules that MySQL follows when casting between JSON values and values of other types:

Table 12.1 JSON Conversion Rules

other type	CAST(other type AS JSON)	CAST(JSON AS other type)
JSON	No change	No change
utf8 character type (utf8mb4, utf8, ascii)	The string is parsed into a JSON value.	The JSON value is serialized into a utf8mb4 string.
Other character types	Other character encodings are implicitly converted to utf8mb4 and treated as described for utf8 character type.	The JSON value is serialized into a utf8mb4 string, then cast to the other character encoding. The result may not be meaningful.
NULL	Results in a NULL value of type JSON.	Not applicable.
Geometry types	The geometry value is converted into a JSON document by calling ST_AsGeoJSON().	Illegal operation. Workaround: Pass the result of CAST(json_val AS CHAR) to ST_GeomFromGeoJSON().
All other types	Results in a JSON document consisting of a single scalar value.	Succeeds if the JSON document consists of a single scalar value of the target type and that scalar value can be cast to the target type. Otherwise, returns NULL and produces a warning.

ORDER BY and GROUP BY for JSON values works according to these principles:

Ordering of scalar JSON values uses the same rules as in the preceding discussion.

For ascending sorts, SQL NULL orders before all JSON values, including the JSON null literal; for descending sorts, SQL NULL orders after all JSON values, including the JSON null literal.

Sort keys for JSON values are bound by the value of the max_sort_length system variable, so keys that differ only after the first max_sort_length bytes compare as equal.

Sorting of nonscalar values is not currently supported and a warning occurs.

For sorting, it can be beneficial to cast a JSON scalar to some other native MySQL type. For example, if a column named jdoc contains JSON objects having a member consisting of an id key and a nonnegative value, use this expression to sort by id values:

ORDER BY CAST(JSON_EXTRACT(jdoc, '$.id') AS UNSIGNED)
If there happens to be a generated column defined to use the same expression as in the ORDER BY, the MySQL optimizer recognizes that and considers using the index for the query execution plan. See Section 9.3.9, “Optimizer Use of Generated Column Indexes”.