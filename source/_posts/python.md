title: PYTHON2
---

## 字符串
* `capitalize(...)` : `S.capitalize() -> str`

    Return a capitalized version of S, i.e. make the first character
    have upper case and the rest lower case.

* `casefold(...)` : `S.casefold() -> str`

    Return a version of S suitable for caseless comparisons.

* `center(...)` : `S.center(width[, fillchar]) -> str`

    Return S centered in a string of length width. Padding is
    done using the specified fill character (default is a space)

* `count(...)` : `S.count(sub[, start[, end]]) -> int`

    Return the number of non-overlapping occurrences of substring sub in
    string S[start:end].  Optional arguments start and end are
    interpreted as in slice notation.

* `encode(...)` : `S.encode(encoding='utf-8', errors='strict') -> bytes`

    Encode S using the codec registered for encoding. Default encoding
    is 'utf-8'. errors may be given to set a different error
    handling scheme. Default is 'strict' meaning that encoding errors raise
    a UnicodeEncodeError. Other possible values are 'ignore', 'replace' and
    'xmlcharrefreplace' as well as any other name registered with
    codecs.register_error that can handle UnicodeEncodeErrors.

* `endswith(...)` : `S.endswith(suffix[, start[, end]]) -> bool`

    Return True if S ends with the specified suffix, False otherwise.
    With optional start, test S beginning at that position.
    With optional end, stop comparing S at that position.
    suffix can also be a tuple of strings to try.

* `expandtabs(...)` : `S.expandtabs(tabsize=8) -> str`

    Return a copy of S where all tab characters are expanded using spaces.
    If tabsize is not given, a tab size of 8 characters is assumed.

* `find(...)` : `S.find(sub[, start[, end]]) -> int`

    Return the lowest index in S where substring sub is found,
    such that sub is contained within S[start:end].  Optional
    arguments start and end are interpreted as in slice notation.

    Return -1 on failure.

* `format(...)` : `S.format(*args, **kwargs) -> str`

    Return a formatted version of S, using substitutions from args and kwargs.
    The substitutions are identified by braces ('{' and '}').

* `format_map(...)` : `S.format_map(mapping) -> str`

    Return a formatted version of S, using substitutions from mapping.
    The substitutions are identified by braces ('{' and '}').

* `index(...)` : `S.index(sub[, start[, end]]) -> int`

    Like S.find() but raise ValueError when the substring is not found.

* `isalnum(...)` : `S.isalnum() -> bool`

    Return True if all characters in S are alphanumeric
    and there is at least one character in S, False otherwise.

* `isalpha(...)` : `S.isalpha() -> bool`

    Return True if all characters in S are alphabetic
    and there is at least one character in S, False otherwise.

* `isdecimal(...)` : `S.isdecimal() -> bool`

    Return True if there are only decimal characters in S,
    False otherwise.

* `isdigit(...)` : `S.isdigit() -> bool`

    Return True if all characters in S are digits
    and there is at least one character in S, False otherwise.

* `isidentifier(...)` : `S.isidentifier() -> bool`

    Return True if S is a valid identifier according
    to the language definition.

    Use keyword.iskeyword() to test for reserved identifiers
    such as "def" and "class".

* `islower(...)` : `S.islower() -> bool`

    Return True if all cased characters in S are lowercase and there is
    at least one cased character in S, False otherwise.

* `isnumeric(...)` : `S.isnumeric() -> bool`

    Return True if there are only numeric characters in S,
    False otherwise.

* `isprintable(...)` : `S.isprintable() -> bool`

    Return True if all characters in S are considered
    printable in repr() or S is empty, False otherwise.

* `isspace(...)` : `S.isspace() -> bool`

    Return True if all characters in S are whitespace
    and there is at least one character in S, False otherwise.

* `istitle(...)` : `S.istitle() -> bool`

    Return True if S is a titlecased string and there is at least one
    character in S, i.e. upper- and titlecase characters may only
    follow uncased characters and lowercase characters only cased ones.
    Return False otherwise.

* `isupper(...)` : `S.isupper() -> bool`

    Return True if all cased characters in S are uppercase and there is
    at least one cased character in S, False otherwise.

* `join(...)` : `S.join(iterable) -> str`

    Return a string which is the concatenation of the strings in the
    iterable.  The separator between elements is S.

* `ljust(...)` : `S.ljust(width[, fillchar]) -> str`

    Return S left-justified in a Unicode string of length width. Padding is
    done using the specified fill character (default is a space).

* `lower(...)` : `S.lower() -> str`

    Return a copy of the string S converted to lowercase.

* `lstrip(...)` : `S.lstrip([chars]) -> str`

    Return a copy of the string S with leading whitespace removed.
    If chars is given and not None, remove characters in chars instead.

* `partition(...)` : `S.partition(sep) -> (head, sep, tail)`

    Search for the separator sep in S, and return the part before it,
    the separator itself, and the part after it.  If the separator is not
    found, return S and two empty strings.

* `replace(...)` : `S.replace(old, new[, count]) -> str`

    Return a copy of S with all occurrences of substring
    old replaced by new.  If the optional argument count is
    given, only the first count occurrences are replaced.

* `rfind(...)` : `S.rfind(sub[, start[, end]]) -> int`

    Return the highest index in S where substring sub is found,
    such that sub is contained within S[start:end].  Optional
    arguments start and end are interpreted as in slice notation.

    Return -1 on failure.

* `rindex(...)` : `S.rindex(sub[, start[, end]]) -> int`

    Like S.rfind() but raise ValueError when the substring is not found.

* `rjust(...)` : `S.rjust(width[, fillchar]) -> str`

    Return S right-justified in a string of length width. Padding is
    done using the specified fill character (default is a space).

* `rpartition(...)` : `S.rpartition(sep) -> (head, sep, tail)`

    Search for the separator sep in S, starting at the end of S, and return
    the part before it, the separator itself, and the part after it.  If the
    separator is not found, return two empty strings and S.

* `rsplit(...)` : `S.rsplit(sep=None, maxsplit=-1) -> list of strings`

    Return a list of the words in S, using sep as the
    delimiter string, starting at the end of the string and
    working to the front.  If maxsplit is given, at most maxsplit
    splits are done. If sep is not specified, any whitespace string
    is a separator.

* `rstrip(...)` : `S.rstrip([chars]) -> str`

    Return a copy of the string S with trailing whitespace removed.
    If chars is given and not None, remove characters in chars instead.

* `split(...)` : `S.split(sep=None, maxsplit=-1) -> list of strings`

    Return a list of the words in S, using sep as the
    delimiter string.  If maxsplit is given, at most maxsplit
    splits are done. If sep is not specified or is None, any
    whitespace string is a separator and empty strings are
    removed from the result.

* `splitlines(...)` : `S.splitlines([keepends]) -> list of strings`

    Return a list of the lines in S, breaking at line boundaries.
    Line breaks are not included in the resulting list unless keepends
    is given and true.

* `startswith(...)` : `S.startswith(prefix[, start[, end]]) -> bool`

    Return True if S starts with the specified prefix, False otherwise.
    With optional start, test S beginning at that position.
    With optional end, stop comparing S at that position.
    prefix can also be a tuple of strings to try.

* `strip(...)` : `S.strip([chars]) -> str`

    Return a copy of the string S with leading and trailing
    whitespace removed.
    If chars is given and not None, remove characters in chars instead.

* `swapcase(...)` : `S.swapcase() -> str`

    Return a copy of S with uppercase characters converted to lowercase
    and vice versa.

* `title(...)` : `S.title() -> str`

    Return a titlecased version of S, i.e. words start with title case
    characters, all remaining cased characters have lower case.

* `translate(...)` : `S.translate(table) -> str`

    Return a copy of the string S, where all characters have been mapped
    through the given translation table, which must be a mapping of
    Unicode ordinals to Unicode ordinals, strings, or None.
    Unmapped characters are left untouched. Characters mapped to None
    are deleted.

* `upper(...)` : `S.upper() -> str`

    Return a copy of S converted to uppercase.

* `zfill(...)` : `S.zfill(width) -> str`

    Pad a numeric string S with zeros on the left, to fill a field
    of the specified width. The string S is never truncated.

## 内置数据结构
### 列表
* 声明一个列表 `list = [123, "ad"]`
* 索引第一个元素 `list[0]`
* 在尾部添加一个元素 `list.append(2.56)`
* 对第一个元素重新赋值 `list[0] = "0"`
* 获取列表长度 `len(list)`
* 删除第一个元素 `del list[0]`



### 元组
元组和列表十分类似，只不过元组和字符串一样是 不可变的 即你不能修改元组
```python
tumple = (123, "adf")
print(tumple[0])
print(len(tumple))
```

### 字典
```python
map = {
       "key1":"value1",
       "key2":"value2",
       "key3":"value3",
       }
print(map)
print(len(map))
print(map["key1"])
del map["key1"]
print(map)

for key in map:
    print(key + "   " + map[key])
if "key2" in map:
    print("map contains key2")

help(dict)
```

### 序列
序列的两个主要特点是索引操作符和切片操作符
```python
shoplist = ['apple', 'mango', 'carrot', 'banana']

print('Item 0 is', shoplist[0])
print('Item -1 is', shoplist[-1])

# Slicing on a list
print('Item 1 to 3 is', shoplist[1:3])
print('Item 2 to end is', shoplist[2:])
print('Item 1 to -1 is', shoplist[1:-1])
print('Item start to end is', shoplist[:])

# Slicing on a string
name = 'swaroop'
print('characters 1 to 3 is', name[1:3])
print('characters 2 to end is', name[2:])
print('characters 1 to -1 is', name[1:-1])
print('characters start to end is', name[:])
```


## 文件


## 控制流
### if
```python
tmp = 0
if tmp > 0 :
    print(">")
elif tmp < 0 :
    print("<")
else :
    print("=")
```

### while
```python
tmp = 0
while tmp < 3 :
    print(tmp)
    tmp +=1
else :
    print("over")
```

### for
```python
for i in [0,1,2,3,4]:
    print(i)

    if i > 2 :
        break
    else :
        continue

else:
    print('loop over')
```

## 函数

### 定义一个不带参数的函数
```python
# 定义一个不带参数的函数
def printHelloworld():
    print("hello world")

# 调用函数
printHelloworld()
```

### 定义一个带参数的函数
```python
# 定义一个带参数的函数
def printHelloworld(saywhat):
    print(saywhat)

# 调用函数
printHelloworld("hello world")
```

### 函数中的局部变量
```python
# 定义一个带参数的函数
def printHelloworld(saywhat):
    value = saywhat
    print(value)

# 调用函数
printHelloworld("hello world")
```

当在函数内部修改了局部变量之后,并不会影响脚本中的变量
```python
# 定义一个带参数的函数
def printHelloworld(saywhat):
    print(saywhat)
    saywhat = "new value"
    print(saywhat)

# 调用函数
str = "hello world"
printHelloworld(str)
print(str)
```

### 使用global语句
```python
# 定义一个带参数的函数
def printHelloworld():
    global saywhat # 此处不可进行初始化
    saywhat = "new value"
    print(saywhat)

# 调用函数
printHelloworld()
print(saywhat)
```

### 默认参数值
```python
def printHelloworld(str, str1="str1 value", str2="str2 value"):
    print(str + " " + str1 + " " + str2)

# 调用函数
printHelloworld("123", str2="789")
```

### return返回值
```python
def printHelloworld(str, str1="str1 value", str2="str2 value"):
    print(str)
    if str1=="str1 value" :
        return "nil value"
    print(str1)
    print(str2)

# 调用函数
result = printHelloworld("123", str2="789")
print(result)

result = printHelloworld("123", str1="789")
print(result)
```

## 模块
模块是一个包含函数和变量的文件。为了在其他程序中重用模块，模块的文件名必须以.py为扩展名。

### 使用`sys`模块
```python
import sys

for argv in sys.argv :
    print(argv)
```

使用`from..import..`, `import`可以使用`*`
```python
from sys import argv

for argvtmp in argv :
    print(argvtmp)
```

模块的name,下面的语法输出当前模块的name
```python
print(__name__)
```

### 自定义模块
* 建立`mymodule.py`文件
```python
# Filename: mymodule.py

def printModuleName():
    print(__name__)
```
* 建立`test_mymodule.py`文件
```python
import mymodule

mymodule.printModuleName()
```
1. 需要注意的是`mymodule.py`文件的`Filename`必须和文件名相同
2. 如果`module`的name是`__main__`说明这个module是由用户启动的