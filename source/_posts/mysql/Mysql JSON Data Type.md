category: MySQL
date: 2016-05-10
title: Mysql JSON Data Type
---
[官方文档](http://dev.mysql.com/doc/refman/5.7/en/json.html)

## Creating JSON Values

In MySQL, JSON values are written as strings. MySQL parses any string used in a context that requires a JSON value, and produces an error if it is not valid as JSON. These contexts include inserting a value into a column that has the JSON data type and passing an argument to a function that expects a JSON value, as the following examples demonstrate:

Attempting to insert a value into a JSON column succeeds if the value is a valid JSON value, but fails if it is not:
```sql
mysql> CREATE TABLE t1 (jdoc JSON);
Query OK, 0 rows affected (0.20 sec)

mysql> INSERT INTO t1 VALUES('{"key1": "value1", "key2": "value2"}');
Query OK, 1 row affected (0.01 sec)

mysql> INSERT INTO t1 VALUES('[1, 2,');
ERROR 3140 (22032) at line 2: Invalid JSON text: "Invalid value." at position 6 in value (or column) '[1, 2,'.
```

Positions for “at position N” in such error messages are 0-based, but should be considered rough indications of where the problem in a value actually occurs.

The JSON_TYPE() function expects a JSON argument and attempts to parse it into a JSON value. It returns the value's JSON type if it is valid and produces an error otherwise:
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
```
to function json_type; a JSON string or JSON type is required.
MySQL handles strings used in JSON context using the utf8mb4 character set and utf8mb4_bin collation. Strings in other character set are converted to utf8mb4 as necessary. (For strings in the ascii or utf8 character sets, no conversion is needed because ascii and utf8 are subsets of utf8mb4.)

As an alternative to writing JSON values using literal strings, functions exist for composing JSON values from component elements. JSON_ARRAY() takes a (possibly empty) list of values and returns a JSON array containing those values:
```sql
mysql> SELECT JSON_ARRAY('a', 1, NOW());
+----------------------------------------+
| JSON_ARRAY('a', 1, NOW())              |
+----------------------------------------+
| ["a", 1, "2015-07-27 09:43:47.000000"] |
+----------------------------------------+
```
JSON_OBJECT() takes a (possibly empty) list of key/value pairs and returns a JSON object containing those pairs:
```sql
mysql> SELECT JSON_OBJECT('key1', 1, 'key2', 'abc');
+---------------------------------------+
| JSON_OBJECT('key1', 1, 'key2', 'abc') |
+---------------------------------------+
| {"key1": 1, "key2": "abc"}            |
+---------------------------------------+
```
JSON_MERGE() takes two or more JSON documents and returns the combined result:
```sql
mysql> SELECT JSON_MERGE('["a", 1]', '{"key": "value"}');
+--------------------------------------------+
| JSON_MERGE('["a", 1]', '{"key": "value"}') |
+--------------------------------------------+
| ["a", 1, {"key": "value"}]                 |
+--------------------------------------------+
```
For information about the merging rules, see Normalization, Merging, and Autowrapping of JSON Values.

JSON values can be assigned to user-defined variables:
```sql
mysql> SET @j = JSON_OBJECT('key', 'value');
mysql> SELECT @j;
+------------------+
| @j               |
+------------------+
| {"key": "value"} |
+------------------+
```
However, user-defined variables cannot be of JSON data type, so although @j in the preceding example looks like a JSON value and has the same character set and collation as a JSON value, it does not have the JSON data type. Instead, the result from JSON_OBJECT() is converted to a string when assigned to the variable.

Strings produced by converting JSON values have a character set of utf8mb4 and a collation of utf8mb4_bin:
```sql
mysql> SELECT CHARSET(@j), COLLATION(@j);
+-------------+---------------+
| CHARSET(@j) | COLLATION(@j) |
+-------------+---------------+
| utf8mb4     | utf8mb4_bin   |
+-------------+---------------+
```
Because utf8mb4_bin is a binary collation, comparison of JSON values is case sensitive.
```sql
mysql> SELECT JSON_ARRAY('x') = JSON_ARRAY('X');
+-----------------------------------+
| JSON_ARRAY('x') = JSON_ARRAY('X') |
+-----------------------------------+
|                                 0 |
+-----------------------------------+
```
Case sensitivity also applies to the JSON null, true, and false literals, which always must be written in lowercase:
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
Case sensitivity of the JSON literals differs from that of the SQL NULL, TRUE, and FALSE literals, which can be written in any lettercase:
```sql
mysql> SELECT ISNULL(null), ISNULL(Null), ISNULL(NULL);
+--------------+--------------+--------------+
| ISNULL(null) | ISNULL(Null) | ISNULL(NULL) |
+--------------+--------------+--------------+
|            1 |            1 |            1 |
+--------------+--------------+--------------+
```

## Normalization, Merging, and Autowrapping of JSON Values

When a string is parsed and found to be a valid JSON document, it is also normalized: Members with keys that duplicate a key found earlier in the document are discarded (even if the values differ). The object value produced by the following JSON_OBJECT() call does not include the second key1 element because that key name occurs earlier in the value:
```sql
mysql> SELECT JSON_OBJECT('key1', 1, 'key2', 'abc', 'key1', 'def');
+------------------------------------------------------+
| JSON_OBJECT('key1', 1, 'key2', 'abc', 'key1', 'def') |
+------------------------------------------------------+
| {"key1": 1, "key2": "abc"}                           |
+------------------------------------------------------+
```
The normalization performed by MySQL also sorts the keys of a JSON object (for the purpose of making lookups more efficient). The result of this ordering is subject to change and not guaranteed to be consistent across releases. In addition, extra whitespace between keys, values, or elements in the original document is discarded.

MySQL functions that produce JSON values (see Section 13.16.2, “Functions That Create JSON Values”) always return normalized values.

In contexts that combine multiple arrays, the arrays are merged into a single array by concatenating arrays named later to the end of the first array. In the following example, JSON_MERGE() merges its arguments into a single array:
```sql
mysql> SELECT JSON_MERGE('[1, 2]', '["a", "b"]', '[true, false]');
+-----------------------------------------------------+
| JSON_MERGE('[1, 2]', '["a", "b"]', '[true, false]') |
+-----------------------------------------------------+
| [1, 2, "a", "b", true, false]                       |
+-----------------------------------------------------+
```
Multiple objects when merged produce a single object. If multiple objects have the same key, the value for that key in the resulting merged object is an array containing the key values:
```sql
mysql> SELECT JSON_MERGE('{"a": 1, "b": 2}', '{"c": 3, "a": 4}');
+----------------------------------------------------+
| JSON_MERGE('{"a": 1, "b": 2}', '{"c": 3, "a": 4}') |
+----------------------------------------------------+
| {"a": [1, 4], "b": 2, "c": 3}                      |
+----------------------------------------------------+
```
Nonarray values used in a context that requires an array value are autowrapped: The value is surrounded by [ and ] characters to convert it to an array. In the following statement, each argument is autowrapped as an array ([1], [2]). These are then merged to produce a single result array:
```sql
mysql> SELECT JSON_MERGE('1', '2');
+----------------------+
| JSON_MERGE('1', '2') |
+----------------------+
| [1, 2]               |
+----------------------+
```
Array and object values are merged by autowrapping the object as an array and merging the two arrays:
```sql
mysql> SELECT JSON_MERGE('[10, 20]', '{"a": "x", "b": "y"}');
+------------------------------------------------+
| JSON_MERGE('[10, 20]', '{"a": "x", "b": "y"}') |
+------------------------------------------------+
| [10, 20, {"a": "x", "b": "y"}]                 |
+------------------------------------------------+
```

## Searching and Modifying JSON Values

A JSON path expression selects a value within a JSON document.

Path expressions are useful with functions that extract parts of or modify a JSON document, to specify where within that document to operate. For example, the following query extracts from a JSON document the value of the member with the name key:
```sql
mysql> SELECT JSON_EXTRACT('{"id": 14, "name": "Aztalan"}', '$.name');
+---------------------------------------------------------+
| JSON_EXTRACT('{"id": 14, "name": "Aztalan"}', '$.name') |
+---------------------------------------------------------+
| "Aztalan"                                               |
+---------------------------------------------------------+
```
Path syntax uses a leading $ character to represent the JSON document under consideration, optionally followed by selectors that indicate successively more specific parts of the document:

A period followed by a key name names the member in an object with the given key. The key name must be specified within double quotation marks if the name without quotes is not legal within path expressions (for example, if it contains a space).

[N] appended to a path that selects an array names the value at position N within the array. Array positions are integers beginning with zero.

Paths can contain * or ** wildcards:

.[*] evaluates to the values of all members in a JSON object.

[*] evaluates to the values of all elements in a JSON array.

prefix**suffix evaluates to all paths that begin with the named prefix and end with the named suffix.

A path that does not exist in the document (evaluates to nonexistent data) evaluates to NULL.

Let $ refer to this JSON array with three elements:

[3, {"a": [5, 6], "b": 10}, [99, 100]]
Then:

$[0] evaluates to 3.

$[1] evaluates to {"a": [5, 6], "b": 10}.

$[2] evaluates to [99, 100].

$[3] evaluates to NULL (it refers to the fourth array element, which does not exist).

Because $[1] and $[2] evaluate to nonscalar values, they can be used as the basis for more-specific path expressions that select nested values. Examples:

$[1].a evaluates to [5, 6].

$[1].a[1] evaluates to 6.

$[1].b evaluates to 10.

$[2][0] evaluates to 99.

As mentioned previously, path components that name keys must be quoted if the unquoted key name is not legal in path expressions. Let $ refer to this value:

{"a fish": "shark", "a bird": "sparrow"}
The keys both contain a space and must be quoted:

$."a fish" evaluates to shark.

$."a bird" evaluates to sparrow.

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
In the following example, the path $**.b evaluates to multiple paths ($.a.b and $.c.b) and produces an array of the matching path values:
```sql
mysql> SELECT JSON_EXTRACT('{"a": {"b": 1}, "c": {"b": 2}}', '$**.b');
+---------------------------------------------------------+
| JSON_EXTRACT('{"a": {"b": 1}, "c": {"b": 2}}', '$**.b') |
+---------------------------------------------------------+
| [1, 2]                                                  |
+---------------------------------------------------------+
```
In MySQL 5.7.9 and later, you can use column->path with a JSON column identifier and JSON path expression as a synonym for JSON_EXTRACT(column, path). See Section 13.16.3, “Functions That Search JSON Values”, for more information. See also Section 14.1.18.6, “Secondary Indexes and Generated Virtual Columns”.

Some functions take an existing JSON document, modify it in some way, and return the resulting modified document. Path expressions indicate where in the document to make changes. For example, the JSON_SET(), JSON_INSERT(), and JSON_REPLACE() functions each take a JSON document, plus one or more path/value pairs that describe where to modify the document and the values to use. The functions differ in how they handle existing and nonexisting values within the document.

Consider this document:
```sql
mysql> SET @j = '["a", {"b": [true, false]}, [10, 20]]';
JSON_SET() replaces values for paths that exist and adds values for paths that do not exist:.

mysql> SELECT JSON_SET(@j, '$[1].b[0]', 1, '$[2][2]', 2);
+--------------------------------------------+
| JSON_SET(@j, '$[1].b[0]', 1, '$[2][2]', 2) |
+--------------------------------------------+
| ["a", {"b": [1, false]}, [10, 20, 2]]      |
+--------------------------------------------+
```
In this case, the path $[1].b[0] selects an existing value (true), which is replaced with the value following the path argument (1). The path $[2][2] does not exist, so the corresponding value (2) is added to the value selected by $[2].

JSON_INSERT() adds new values but does not replace existing values:
```sql
mysql> SELECT JSON_INSERT(@j, '$[1].b[0]', 1, '$[2][2]', 2);
+-----------------------------------------------+
| JSON_INSERT(@j, '$[1].b[0]', 1, '$[2][2]', 2) |
+-----------------------------------------------+
| ["a", {"b": [true, false]}, [10, 20, 2]]      |
+-----------------------------------------------+
```
JSON_REPLACE() replaces existing values and ignores new values:
```sql
mysql> SELECT JSON_REPLACE(@j, '$[1].b[0]', 1, '$[2][2]', 2);
+------------------------------------------------+
| JSON_REPLACE(@j, '$[1].b[0]', 1, '$[2][2]', 2) |
+------------------------------------------------+
| ["a", {"b": [1, false]}, [10, 20]]             |
+------------------------------------------------+
```
The path/value pairs are evaluated left to right. The document produced by evaluating one pair becomes the new value against which the next pair is evaluated.

JSON_REMOVE() takes a JSON document and one or more paths that specify values to be removed from the document. The return value is the original document minus the values selected by paths that exist within the document:
```sql
mysql> SELECT JSON_REMOVE(@j, '$[2]', '$[1].b[1]', '$[1].b[1]');
+---------------------------------------------------+
| JSON_REMOVE(@j, '$[2]', '$[1].b[1]', '$[1].b[1]') |
+---------------------------------------------------+
| ["a", {"b": [true]}]                              |
+---------------------------------------------------+
```
The paths have these effects:

$[2] matches [10, 20] and removes it.

The first instance of $[1].b[1] matches false in the b element and removes it.

The second instance of $[1].b[1] matches nothing: That element has already been removed, the path no longer exists, and has no effect.

## Comparison and Ordering of JSON Values

JSON values can be compared using the =, <, <=, >, >=, <>, !=, and <=> operators.

The following comparison operators and functions are not yet supported with JSON values:

BETWEEN

IN()

GREATEST()

LEAST()

A workaround for the comparison operators and functions just listed is to cast JSON values to a native MySQL numeric or string data type so they have a consistent non-JSON scalar type.

Comparison of JSON values takes place at two levels. The first level of comparison is based on the JSON types of the compared values. If the types differ, the comparison result is determined solely by which type has higher precedence. If the two values have the same JSON type, a second level of comparison occurs using type-specific rules.

The following list shows the precedences of JSON types, from highest precedence to the lowest. (The type names are those returned by the JSON_TYPE() function.) Types shown together on a line have the same precedence. Any value having a JSON type listed earlier in the list compares greater than any value having a JSON type listed later in the list.

BLOB
BIT
OPAQUE
DATETIME
TIME
DATE
BOOLEAN
ARRAY
OBJECT
STRING
INTEGER, DOUBLE
NULL
For JSON values of the same precedence, the comparison rules are type specific:

BLOB

The first N bytes of the two values are compared, where N is the number of bytes in the shorter value. If the first N bytes of the two values are identical, the shorter value is ordered before the longer value.

BIT

Same rules as for BLOB.

OPAQUE

Same rules as for BLOB. OPAQUE values are values that are not classified as one of the other types.

DATETIME

A value that represents an earlier point in time is ordered before a value that represents a later point in time. If two values originally come from the MySQL DATETIME and TIMESTAMP types, respectively, they are equal if they represent the same point in time.

TIME

The smaller of two time values is ordered before the larger one.

DATE

The earlier date is ordered before the more recent date.

ARRAY

Two JSON arrays are equal if they have the same length and values in corresponding positions in the arrays are equal.

If the arrays are not equal, their order is determined by the elements in the first position where there is a difference. The array with the smaller value in that position is ordered first. If all values of the shorter array are equal to the corresponding values in the longer array, the shorter array is ordered first.

Example:

[] < ["a"] < ["ab"] < ["ab", "cd", "ef"] < ["ab", "ef"]
BOOLEAN

The JSON false literal is less than the JSON true literal.

OBJECT

Two JSON objects are equal if they have the same set of keys, and each key has the same value in both objects.

Example:

{"a": 1, "b": 2} = {"b": 2, "a": 1}
The order of two objects that are not equal is unspecified but deterministic.

STRING

Strings are ordered lexically on the first N bytes of the utf8mb4 representation of the two strings being compared, where N is the length of the shorter string. If the first N bytes of the two strings are identical, the shorter string is considered smaller than the longer string.

Example:

"a" < "ab" < "b" < "bc"
This ordering is equivalent to the ordering of SQL strings with collation utf8mb4_bin. Because utf8mb4_bin is a binary collation, comparison of JSON values is case sensitive:

"A" < "a"
INTEGER, DOUBLE

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