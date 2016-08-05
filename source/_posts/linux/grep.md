category: linux
date: 2015-10-15
title: grep
---
grep一种强大的文本搜索工具,它能使用正则表达式搜索文本,并把匹 配的行打印出来

用法: `grep [选项]... PATTERN [FILE]...`. 在每个 FILE 或是标准输入中查找 PATTERN.默认的 PATTERN 是一个基本正则表达式(缩写为 BRE).
```shell
grep -i 'hello world' menu.h main.c
```

主要参数：
* `－c`：只输出匹配行的计数.
* `－I`：不区分大小写(只适用于单字符).
* `－h`：查询多文件时不显示文件名.
* `－l`：查询多文件时只输出包含匹配字符的文件名.
* `－n`：显示匹配行及行号.
* `－s`：不显示不存在或无匹配文本的错误信息.
* `－v`：显示不包含匹配文本的所有行.

pattern正则表达式主要参数：
* `\`： 忽略正则表达式中特殊字符的原有含义.
* `^`：匹配正则表达式的开始行.
* `$`: 匹配正则表达式的结束行.
* `\<`：从匹配正则表达 式的行开始.
* `\>`：到匹配正则表达式的行结束.
* `[ ]`：单个字符,如[A]即A符合要求 .
* `[ - ]`：范围,如[A-Z],即A,B,C一直到Z都符合要求 .
* `.`：所有的单个字符.
* `*` ：有字符,长度可以为0.

正则表达式选择与解释:
* `-E`     PATTERN 是一个可扩展的正则表达式(缩写为 ERE)
* `-F`     PATTERN 是一组由断行符分隔的定长字符串.
* `-G`     PATTERN 是一个基本正则表达式(缩写为 BRE)
* `-e`     用 PATTERN 来进行匹配操作
* `-f`     从 FILE 中取得 PATTERN
* `-i`     忽略大小写
* `-w`     强制 PATTERN 仅完全匹配字词
* `-x`     强制 PATTERN 仅完全匹配一行
* `-z`     一个 0 字节的数据行,但不是空行

输出控制:
* `-m` : 只获得前m个匹配结果
* `-b` : 在输出的内容中打印byte offset
* `-n` : 输出行号
* `-H` : 输出文件名
* `-h` : 不输出文件名
* `-o` : 仅仅输出一行中符合 PATTERN模式 匹配要求的部分
* `-a` : 等同于 --binary-files=text
* `-I` : 等同于 --binary-files=without-match
* `-d` : 处理目录方式; `read', `recurse', or `skip'
* `-D` : 处理 devices, FIFOs and sockets的方式: `read' or `skip'
* `-R` : equivalent to --directories=recurse
>    --include=FILE_PATTERN  仅对符合FILE_PATTERN模式的文件进行搜索
>     --exclude=FILE_PATTERN  跳过符合FILE_PATTERN的文件和目录
>     --exclude-from=FILE   跳过符合FILE中file pattern所有的文件
>     --exclude-dir=PATTERN  符合PATTERN模式的目录将被跳过.
* `-L` :  只输出匹配失败的文件名
* `-l` : 只输出匹配成功的文件名
* `-c` : 只输出每个文件匹配成功的行数
* `-Z` : 在文件名后输出0 byte

Context control:
* `-B, --before-context=NUM`  显示匹配字符串前n行的数据
* `-A, --after-context=NUM`   显示匹配字符串后n行的数据
* `-C, --context=NUM`         print NUM lines of output context
* `-NUM`                      和 --context=NUM 一样

当前目录下有多个文件我们想要同时对所有文件进行搜索的话, 我们可以使用
```shell
grep "刷新" *
```
如果我们只想对今天的文件进行搜索的话, 可以使用
```shell
grep "刷新" *2016-02-02*
```
如果我们的目录下有多个目录, 要搜索的文件都在当前的子目录里的话, 可以使用
```shell
grep --directories=recurse "刷新" *
```
递归grep所有目录：
```shell
grep -r "some_text" /path/to/dir
```
排除包含字符的行
```bash
grep -v "22" ./as.txt
```
包含多个条件中的任意一个(或的关系)
```bash
grep -E "22222|11111" ./as.txt
```
输出匹配行的前后俩行
```bash
grep -C 2 'linux'
```