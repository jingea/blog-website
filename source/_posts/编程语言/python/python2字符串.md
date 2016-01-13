category: 编程语言
tag: python2
date: 2015-08-08
title: PYTHON2 字符串函数
---

* `capitalize(...)` : Python capitalize()将字符串的第一个字母变成大写,其他字母变小写。`print "abc".capitalize()`

* `center(...)` : 返回一个原字符串居中,并使用空格填充至长度 width 的新字符串。默认填充字符为空格。`print "abc".center(10, "1")`结果为`111abc1111`
··
* `count(...)` : 用于统计字符串里某个字符出现的次数。可选参数为在字符串搜索的开始与结束位置。`"abcdefg".count("c", 2, 4)`结果为`1`

* `encode(...)` : encoding 指定的编码格式编码字符串。errors参数可以指定不同的错误处理方案。`S.encode(encoding='utf-8', errors='strict') -> bytes` 

* `endswith(...)` : 用于判断字符串是否以指定后缀结尾，如果以指定后缀结尾返回True，否则返回False。可选参数"start"与"end"为检索字符串的开始与结束位置。`"abcdefg".endswith("d", 2, 4)`结果为true

* `expandtabs(...)` : `S.expandtabs(tabsize=8) -> str` 把字符串中的 tab 符号('\t')转为空格，默认的空格数 tabsize 是 8。

* `find(...)` : `S.find(sub[, start[, end]]) -> int` 检测字符串中是否包含子字符串 str ，如果指定 beg（开始） 和 end（结束） 范围，则检查是否包含在指定范围内，如果包含子字符串返回开始的索引值，否则返回-1。

* `rfind(...)` : `S.rfind(sub[, start[, end]]) -> int` 返回字符串最后一次出现的位置，如果没有匹配项则返回-1。

* `format(...)` : `"abcd{0}fg".format("sdf")` {0}被替换成sdf

* `index(...)` : `S.index(sub[, start[, end]]) -> int`检测字符串中是否包含子字符串 str ，如果指定 beg（开始） 和 end（结束） 范围，则检查是否包含在指定范围内，该方法与 python find()方法一样，只不过如果str不在 string中会报一个异常。

* `rindex(...)` : `S.rindex(sub[, start[, end]]) -> int` 返回子字符串 str 在字符串中最后出现的位置，如果没有匹配的字符串会报异常，你可以指定可选参数[beg:end]设置查找的区间。

* `isalnum(...)` : `S.isalnum() -> bool` 判断字符串是否包含字母数字。

* `isalpha(...)` : `S.isalpha() -> bool` 检测字符串是否只由字母组成。

* `isdecimal(...)` : `S.isdecimal() -> bool` 检查字符串是否只包含十进制字符。这种方法只存在于unicode对象。

* `isdigit(...)` : `S.isdigit() -> bool` 检测字符串是否只由数字组成。

* `islower(...)` : `S.islower() -> bool` 检测字符串是否由小写字母组成。

* `isnumeric(...)` : `S.isnumeric() -> bool` 检查是否只有数字字符组成的字符串。这种方法目前只对unicode对象。

* `isspace(...)` : `S.isspace() -> bool` 是否只由空格组成。

* `istitle(...)` : `S.istitle() -> bool` 检测字符串中所有的单词拼写首字母是否为大写，且其他字母为小写。

* `isupper(...)` : `S.isupper() -> bool` 检测字符串中所有的字母是否都为大写。

* `join(...)` : `S.join(iterable) -> str` 用于将序列中的元素以指定的字符连接生成一个新的字符串。

* `ljust(...)` : `S.ljust(width[, fillchar]) -> str` 返回一个原字符串左对齐,并使用空格填充至指定长度的新字符串。如果指定的长度小于原字符串的长度则返回原字符串。

* `rjust(...)` : `S.rjust(width[, fillchar]) -> str` 返回一个原字符串右对齐,并使用空格填充至长度 width 的新字符串。如果指定的长度小于字符串的长度则返回原字符串。

* `lower(...)` : `S.lower() -> str` 转换字符串中所有大写字符为小写。

* `lstrip(...)` : `S.lstrip([chars]) -> str` 用于截掉字符串左边的空格或指定字符。

* `partition(...)` : `S.partition(sep) -> (head, sep, tail)` Search for the separator sep in S, and return the part before it,the separator itself, and the part after it.  If the separator is not found, return S and two empty strings.

* `rpartition(...)` : `S.rpartition(sep) -> (head, sep, tail)` 类似于 partition()函数,不过是从右边开始查找.

* `replace(...)` : `S.replace(old, new[, count]) -> str` 把字符串中的 old（旧字符串） 替换成 new(新字符串)，如果指定第三个参数max，则替换不超过 max 次。

* `split(...)` : `S.split(sep=None, maxsplit=-1) -> list of strings` 通过指定分隔符对字符串进行切片，如果参数num 有指定值，则仅分隔 num 个子字符串. 另外参考`rsplit(...)`

* `splitlines(...)` : `S.splitlines([keepends]) -> list of strings` 返回一个字符串的所有行，可选包括换行符列表(如果num提供，则为true)

* `startswith(...)` : `S.startswith(prefix[, start[, end]]) -> bool` 用于检查字符串是否是以指定子字符串开头，如果是则返回 True，否则返回 False。如果参数 beg 和 end 指定值，则在指定范围内检查。

* `strip(...)` : `S.strip([chars]) -> str` 用于移除字符串头尾指定的字符（默认为空格）。

* `rstrip(...)` : `S.rstrip([chars]) -> str` 删除 string 字符串末尾的指定字符（默认为空格）.

* `swapcase(...)` : `S.swapcase() -> str` 用于对字符串的大小写字母进行转换。

* `title(...)` : `S.title() -> str` 返回"标题化"的字符串,就是说所有单词都是以大写开始，其余字母均为小写(见 istitle())。

* `translate(...)` : `S.translate(table) -> str` 根据参数table给出的表(包含 256 个字符)转换字符串的字符, 要过滤掉的字符放到 del 参数中。

* `upper(...)` : `S.upper() -> str` 将字符串中的小写字母转为大写字母。

* `zfill(...)` : `S.zfill(width) -> str` 返回指定长度的字符串，原字符串右对齐，前面填充0。

