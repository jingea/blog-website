category: linux
date: 2015-10-15
title: Linux常用命令
---
## gzip
将文件或者文件夹压缩成后缀为`.gz`的文件

* `-a` 　使用ASCII文字模式.
* `-c` 　把压缩后的文件输出到标准输出设备,不去更动原始文件.
* `-d` 　解开压缩文件.
* `-f` 　强行压缩文件.不理会文件名称或硬连接是否存在以及该文件是否为符号连接.
* `-l` 　列出压缩文件的相关信息.
* `-L` 　显示版本与版权信息.
* `-n` 　压缩文件时,不保存原来的文件名称及时间戳记.
* `-N` 　压缩文件时,保存原来的文件名称及时间戳记.
* `-q` 　不显示警告信息.
* `-r` 　递归处理,将指定目录下的所有文件及子目录一并处理.
* `-S` 　更改压缩字尾字符串.
* `-t` 　测试压缩文件是否正确无误.
* `-v` 　显示指令执行过程.
* `-num` 用指定的数字num调整压缩的速度,-1或--fast表示最快压缩方法（低压缩比）,-9或--best表示最慢压缩方法（高压缩比）.系统缺省值为6.

常用命令
* `gzip *`  把test6目录下的每个文件压缩成.gz文件
* `gzip -dv *` 把例1中每个压缩的文件解压,并列出详细的信息
* `gzip -l *` 详细显示例1中每个压缩的文件的信息,并不解压
* `gzip -r log.tar` 压缩一个tar备份文件,此时压缩文件的扩展名为.tar.gz
* `gzip -rv test6` 递归的压缩目录
* `gzip -dr test6` 递归地解压目录

## tar
tar可用于建立,还原,查看,管理文件,也可方 便的追加新文件到备份文件中,或仅更新部分的备份文件,以及解压,删除指定的文件

将tar.gz提取到新目录里：
```shell
tar zxvf package.tar.gz -C new_dir
```

## sort
sort将文件的每一行作为一个单位,相互比较,比较原则是从首字符向后,依次按ASCII码值进行比较,最后将他们按升序输出.

命令参数
* `-u` 在输出行中去除重复行.
* `-r` sort默认的排序方式是升序,加-r改成降序
* `-o` 将排序结果输出到原文件,而使用重定向是不可以的.
* `-n` sort命令默认是按照字符排序的,如果遇到数字的字符, 可能会出现1大于10的情况, 所以我们可以设定`-n`选项, 将其按照数字排序
* `-t` 设定间隔符. 设置间隔符,就会将每一行分割成一个数组,我们可以使用-k参数指定按照某列排序
* `-k` -t指定了间隔符之后,可以用-k指定基于某列排序.
* `-f` 会将小写字母都转换为大写字母来进行比较,亦即忽略大小写
* `-c` 会检查文件是否已排好序,如果乱序,则输出第一个乱序的行的相关信息,最后返回1
* `-C` 会检查文件是否已排好序,如果乱序,不输出内容,仅返回1
* `-M` 会以月份来排序,比如JAN小于FEB等等
* `-b` 会忽略每一行前面的所有空白部分,从第一个可见字符开始比较.

## wc
统计指定文件中的字节数,字数,行数,并将统计结果显示输出.该命令统计指定文件中的字节数,字数,行数.如果没有给出文件名,则从标准输入读取.wc同时也给出所指定文件的总统计数.

* `-c` 统计字节数.
* `-l` 统计行数.
* `-m` 统计字符数.这个标志不能与 -c 标志一起使用.
* `-w` 统计字数.一个字被定义为由空白,跳格或换行字符分隔的字符串.
* `-L` 打印最长行的长度.

## find

`find pathname -options [-print -exec -ok ...]`

命令参数；
`pathname`: 要查找的路径.
`-print`： find命令将匹配的文件输出到标准输出.
`-exec`： 匹配到合适文件后执行的shell命令.相应命令的形式为`'command' { } ;`(注意{ }和；之间的空格).
`-ok`： 和-exec的作用相同,但是在执行命令之前,都会给出提示确认是否真的执行.

options选项
* `-name` 要查找的文件名(可以使用正则).
* `-perm` 按照文件权限查找文件.
* `-prune` 使用这一选项可以使find命令不在当前指定的目录中查找,如果同时使用-depth选项,那么-prune将被find命令忽略.
* `-user` 按照文件属主来查找文件.
* `-group` 按照文件所属的组来查找文件.
* `-mtime -n +n` 按照文件的更改时间来查找文件, - n表示文件更改时间距现在n天以内,+ n表示文件更改时间距现在n天以前.
* `-nogroup` 查找无有效所属组的文件,即该文件所属的组在/etc/groups中不存在.
* `-nouser` 查找无有效属主的文件,即该文件的属主在/etc/passwd中不存在.
* `-newer file1 ! file2` 查找更改时间比文件file1新但比文件file2旧的文件.
* `-type` 查找某一类型的文件(例如: `-b`块设备文件. `-d`目录. `-c`字符设备文件. `-p`管道文件. `-l`符号链接文件. `-f`普通文件.)
* `-size n [c]`：查找文件长度为n块的文件,带有c时表示文件长度以字节计.
* `-depth`：在查找文件时,首先查找当前目录中的文件,然后再在其子目录中查找.
* `-mount`：在查找文件时不跨越文件系统mount点.
* `-follow`：如果find命令遇到符号链接文件,就跟踪至链接所指向的文件.

另外,下面三个的区别:
* `-amin n`  查找系统中最后N分钟访问的文件
* `-atime n` 查找系统中最后n*24小时访问的文件
* `-cmin n`  查找系统中最后N分钟被改变文件状态的文件
* `-ctime n` 查找系统中最后n*24小时被改变文件状态的文件
* `-mmin n`  查找系统中最后N分钟被改变文件数据的文件
* `-mtime n` 查找系统中最后n*24小时被改变文件数据的文件
找出/home/user下所有空子目录：
```shell
find /home/user -maxdepth 1 -type d -empty
```
获取test.txt文件中第50-60行内容：
```shell
< test.txt sed -n '50,60p'
```
将所有文件名中含有”txt”的文件移入/home/user目录：
```shell
find -iname "*txt*" -exec mv -v {} /home/user \;
```

## grep
一种强大的文本搜索工具,它能使用正则表达式搜索文本,并把匹 配的行打印出来

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
* `  ` : 每一行都输出, 不再行上进行缓存
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
* -B, --before-context=NUM  显示匹配字符串前n行的数据
* -A, --after-context=NUM   显示匹配字符串后n行的数据
* -C, --context=NUM         print NUM lines of output context
* -NUM                      和 --context=NUM 一样

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

## cat,more,less
* cat是一次性显示整个文件的内容,还可以将多个文件连接起来显示,它常与重定向符号配合使用,适用于文件内容少的情况；
* more比cat强大,提供分页显示的功能
* less比more更强大,提供翻页,跳转,查找等命令.

## source
1. 当shell脚本具有可执行权限时,用sh filename与./filename执行脚本是没有区别得../filename是因为当前目录没有在PATH中,所有"."是用来表示当前目录的.
2. sh filename 重新建立一个子shell,在子shell中执行脚本里面的语句,该子shell继承父shell的环境变量,但子shell新建的,改变的变量不会被带回父shell,除非使用export.
3. source filename：这个命令其实只是简单地读取脚本里面的语句依次在当前shell里面执行,没有建立新的子shell.那么脚本里面所有新建,改变变量的语句都会保存在当前shell里面.

## wget
用wget抓取完整的网站目录结构,存放到本地目录中：
```shell
wget -r --no-parent --reject "index.html*" http://hostname/ -P /home/user/dirs
```
用wget命令执行ftp下载：
```shell
wget -m ftp:
//username:password@hostname
```

## mkdir
一次创建多个目录：
```shell
mkdir -p /home/user/{test,test1,test2}
```

## ps
列出包括子进程的进程树：
```shell
ps axwef
```

## dd
测试硬盘写入速度：
```shell
dd if=/dev/zero of=/tmp/output.img bs=8k count=256k; rm -rf /tmp/output.img
```

测试硬盘读取速度：
```shell
hdparm -Tt /dev/sda
```

## xmllint
检查xml格式：
```shell
xmllint --noout file.xml
```

## curl
使用curl获取HTTP头信息：
```shell
curl -I http://www.example.com
```

## touch
修改文件或目录的时间戳(YYMMDDhhmm)：
```shell
touch -t 0712250000 file
```

## cp
快速备份一个文件：
```shell
cp some_file_name{,.bkp}
```

## watch
重复运行文件,显示其输出（缺省是2秒一次）：
```shell
watch ps -ef
```

## lsof
列出前10个最大的文件：
```shell
lsof / | awk '{ if($7 > 1048576) print $7/1048576 "MB "$9 }' | sort -n -u | tail
```

## free
显示剩余内存(MB)：
```shell
free -m | grep cache | awk '/[0-9]/{ print $4" MB" }'
```

## paste
将文件按行并列显示：
```shell
paste test.txt test1.txt
```

## pv
shell里的进度条：
```shell
pv data.log
```

## scp
文件复制：本机->远程服务器：
```xml
scp /home/shaoxiaohu/test1.txt shaoxiaohu@172.16.18.1:/home/test2.txt  
```
> 其中，test1为源文件，test2为目标文件，shaoxiaohu@172.16.18.1为远程服务器的用户名和ip地址。

文件复制：远程服务器->本机
```xml
scp shaoxiaohu@172.16.18.2:/home/test2.txt /home/shaoxiaohu/test1.txt  
```
> 其中，haoxiaohu@172.16.18.2为远程服务器的用户名和ip地址， test2为源文件，test1为目标路径

文件夹复制, 在scp命令后加`-r`参数即可。

端口号, 如果端口号有更改，需在scp 后输入：`-P` 端口号 （注意是大写，ssh的命令中 `-p`是小写）。
