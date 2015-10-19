category: 编程语言
date: 2015-10-08
title: Shell
---
每个shell脚本文件第一行都要指定使用哪个shell,我们默认使用`#!/bin/bash`

# 变量
bash变量分为
* 局部变量: 脚本或命令中定义，仅在当前shell实例中有效
* 环境变量: 所有的脚本和shell中都可以访问的变量
* 预定义变量

## 变量类型
运行shell时，会同时存在三种变量：
* 局部变量： 局部变量在脚本或命令中定义，仅在当前shell实例中有效，其他shell启动的程序不能访问局部变量。
* 环境变量：所有的程序，包括shell启动的程序，都能访问环境变量，有些程序需要环境变量来保证其正常运行。必要的时候shell脚本也可以定义环境变量。
* shell变量：shell变量是由shell程序设置的特殊变量。shell变量中有一部分是环境变量，有一部分是局部变量，这些变量保证了shell的正常运行

## 变量声明
```shell
v1=123
```
在上面的声明语法中我们需要注意以下几点
* `=`左右不能用空格
* 变量的默认类型是字符串
* 该变量对当前以及子shell都有效

### declare声明


## 变量引用
我们通过使用`$`或者`${}`符号可以引用一个变量
```shell
v=1
echo $v
echo ${v}
```

## 只读变量
readonly 命令可以将变量定义为只读变量
```shell
readonly myUrl
```

## 删除变量
```shell
unset  myUrl
```

## 特殊变量
* `$0`:	当前脚本的文件名
* `$n`:	传递给脚本或函数的参数。(第一个参数是$1，第二个参数是$2)
* `$#`:	传递给脚本或函数的参数个数。
* `$*`:	传递给脚本或函数的所有参数。
* `$@`:	传递给脚本或函数的所有参数。被双引号(" ")包含时，与 $* 稍有不同
* `$?`:	上个命令的退出状态，或函数的返回值。
* `$$`:	当前Shell进程ID。对于 Shell 脚本，就是这些脚本所在的进程ID。

## 数组

### 数组定义
一对括号表示是数组，数组元素用“空格”符号分割开。
```shell
array=(1 2 3 4 5)
```

### 数组长度
用`${#数组名[@或*]}` 可以得到数组长度
```shell
${#array[@]}
```
### 索引数组成员
用`${数组名[下标]}` 下标是从0开始 (下标是：*或者@ 得到整个数组内容)
```shell
${array[2]}
```

### 数组成员赋值
直接通过 `数组名[下标]` 就可以对其进行引用赋值，如果下标不存在，自动添加新一个数组元素
```shell
array[1]=100
```

### 删除数组
`unset 数组[下标]` 可以清除相应的元素，不带下标，清除整个数据。
```shell
unset array[1]
```

### 数组分片
`${数组名[@或*]:起始位置:长度}` 切片原先数组，返回是字符串，中间用“空格”分开，因此如果加上”()”，将得到切片数组
```shell
 c=(${array[@]:1:4})
```

### 数组替换
`${数组名[@或*]/查找字符/替换字符}` 该操作不会改变原先数组内容
```shell
${array[@]/3/100}
```

# 运算符

## 算术运算符
我们可以使用`expr`, `let`, `(())`, `[]`等四种方式进行算术运算
* `+`:	加法	`(($a + $b))` 
* `-	`:	减法`(($a - $b))` 
* `*`:	乘法	`(($a \* $b))`
* `/`:	除法	`(($b / $a))` 
* `%`:	取余	`(($b % $a))` 
* `=`:	赋值	`a=$b`
* `==`:	相等。用于比较两个数字，相同则返回 true。	`[ $a == $b ]` 返回 false。
* `!=`:	不相等。用于比较两个数字，不相同则返回 true。	`[ $a != $b ]` 返回 true。

## 关系运算符
关系运算符只支持数字，不支持字符串，除非字符串的值是数字。
* `-eq`	检测两个数是否相等，相等返回 true。	`[ $a -eq $b ]` 返回 true。
* `-ne`	检测两个数是否相等，不相等返回 true。	`[ $a -ne $b ]` 返回 true。
* `-gt`	检测左边的数是否大于右边的，如果是，则返回 true。	`[ $a -gt $b ]` 返回 false。
* `-lt`	检测左边的数是否小于右边的，如果是，则返回 true。	`[ $a -lt $b ]` 返回 true。
* `-ge`	检测左边的数是否大等于右边的，如果是，则返回 true。	`[ $a -ge $b ]` 返回 false。
* `-le`	检测左边的数是否小于等于右边的，如果是，则返回 true。	`[ $a -le $b ]` 返回 true。

## 逻辑运算符
* `!`	非运算，表达式为 true 则返回 false，否则返回 true。	[ ! false ] 返回 true。
* `-o`	或运算，有一个表达式为 true 则返回 true。	[ $a -lt 20 -o $b -gt 100 ] 返回 true。
* `-a`	与运算，两个表达式都为 true 才返回 true。	[ $a -lt 20 -a $b -gt 100 ] 返回 false。

## 字符串运算符
* `=`	检测两个字符串是否相等，相等返回 true。	[ $a = $b ] 返回 false。
* `!=`	检测两个字符串是否相等，不相等返回 true。	[ $a != $b ] 返回 true。
* `-z`	检测字符串长度是否为0，为0返回 true。	[ -z $a ] 返回 false。
* `-n`	检测字符串长度是否为0，不为0返回 true。	[ -z $a ] 返回 true。
* `str`	检测字符串是否为空，不为空返回 true。	[ $a ] 返回 true。

## 文件测试运算符
* `-b` 文件是否是块设备文件，如果是，则返回 true。	[ -b $file ] 返回 false。
* `-c` 文件是否是字符设备文件，如果是，则返回 true。	[ -b $file ] 返回 false。
* `-d` 文件是否是目录，如果是，则返回 true。	[ -d $file ] 返回 false。
* `-f` 文件是否是普通文件（既不是目录，也不是设备文件），如果是，则返回 true。	[ -f $file ] 返回 true。
* `-g` 文件是否设置了 SGID 位，如果是，则返回 true。	[ -g $file ] 返回 false。
* `-k` 文件是否设置了粘着位(Sticky Bit)，如果是，则返回 true。	[ -k $file ] 返回 false。
* `-p` 文件是否是具名管道，如果是，则返回 true。	[ -p $file ] 返回 false。
* `-u` 文件是否设置了 SUID 位，如果是，则返回 true。	[ -u $file ] 返回 false。
* `-r` 文件是否可读，如果是，则返回 true。	[ -r $file ] 返回 true。
* `-w` 文件是否可写，如果是，则返回 true。	[ -w $file ] 返回 true。
* `-x` 文件是否可执行，如果是，则返回 true。	[ -x $file ] 返回 true。
* `-s` 文件是否为空（文件大小是否大于0），不为空返回 true。	[ -s $file ] 返回 true。
* `-e` 文件（包括目录）是否存在，如果是，则返回 true。	[ -e $file ] 返回 true。

# 流程控制
shell流程控制包含：
* if
* while
* until
* case
* for

同样的shell也支持`break`和`continue`

##  if else

### if 
语法格式
```shell
if condition
then
    command1 
    command2
    ...
    commandN 
fi
```
示例
```shell
#!/bin/bash
v=123
if [ -b $txt ];   
then
        echo "ok";
fi
```
我们一定要注意if前后的空格

### if else
```
if condition
then
    command1 
    command2
    ...
    commandN
else
    command
fi
```

### if else-if else
```
if condition1
then
    command1
elif condition2
    command2
else
    commandN
fi
```

## case 
 case语句为多选择语句
```shell
case 值 in
模式1)
    command1
    command2
    ...
    commandN
    ;;
模式2）
    command1
    command2
    ...
    commandN
    ;;
esac
```
示例
```shell

#!/bin/bash

for i in 1 2 3 4 5;
do
        case $i in
                1)  echo '你选择了 1'
                ;;
                2)  echo '你选择了 2'
                ;;
                3)  echo '你选择了 3'
                ;;
                4)  echo '你选择了 4'
                ;;
                *)  echo '你没有输入 1 到 4 之间的数字'
                ;;
                esac
done
```

## for 
```shell
for var in item1 item2 ... itemN
do
    command1
    command2
    ...
    commandN
done
```
示例
```shell
#!/bin/bash

for i in 1 2 3 4;
do
        echo $i
done
```

## while 
```shell
while condition
do
    command
done
```
示例
```shell
#!/bin/bash

i=1
while(( $i<=5 ))
do
        echo $i
        ((i++))
done
```

## until 
until循环执行一系列命令直至条件为真时停止。
```shell
until condition
do
    command
done
```
示例
```shell
#!/bin/bash

i=1
until(( $i>3 ))
do
        echo $i
        ((i++))
done
```

# 函数
调用函数不需要加`()`
```shell
vi=123546789
f() {
	echo $vi
}
f
```

## 带参数的函数
参数名是固定的`$n`, 如果参数大于10的话就需要`${10}`
```shell
f() {
echo $1
}
f 789
```

## 函数的返回值
函数返回值在调用该函数后通过 `$?` 来获得
```shell
f() {
echo $1
}
f 789
echo $?
```
如果不写`return`, 则直接返回0
```shell
f() {
echo $1
return 569
}
f 789
echo $?
```
