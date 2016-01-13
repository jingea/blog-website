category: 编程语言
tag: Php
date: 2015-12-16
title: PHP 语法初探
---
PHP 脚本以 `<?php 开头，以 ?>` 结尾：
```php
<?php
// 此处是 PHP 代码
?>
```

## 变量
```php
<?php
// 定义一个变量
$x=5;
// 调用echo函数输出变量x的值
echo $x;
?>
```

PHP 有三种不同的变量作用域：
* local : 函数里定义的变量,只能在函数内部访问,函数外部不可访问
* global: 函数外部定义的变量,只能在函数外部访问,函数内部不可访问
* static: 当变量脱离它的作用域之后,并不会被删除掉,而是缓存起来


```php
<?php
$x=5; // 全局作用域

function socpePrint() {
  $y=10; // 局部作用域
  echo $y;
  echo $x;
} 

myTest();

echo $y;
echo $x;
?>
```

刚才我们只是介绍了`global`这个作用域,其实还有`global`关键字,这个关键字是在函数内部定义一个全局变量,让函数外访问函数内部的变量
```php
<?php
function socpePrint() {
  global $x=10;
}

socpePrint();
echo $x;
?>
```

php里有如下的数据类型
* 字符串
* 整数
* 浮点数
* 逻辑
* 数组
* 对象
* NULL
```php
<?php
$stringVar="this is a string";	// 定义一个字符串
$numVar=10;	// 定义一个整数
$floatVar=1.0;	// 定义一个浮点数
$boolVar=true;		// 定义一个布尔值
$arrayVar=array(1,2);	// 定义一个数组
?>
```

数组相关操作
```php
<?php
$arrayVar=array(1,2);	// 定义一个数组
$ele1=$arrayVar[0];	// 访问数组第一个元素
$arrayCount=count($arrayVar);	// 求数组长度
$arrayVar[2]=3;		// 向数组中追加元素
?>
```

## 函数

下面我们定义了一个求和函数, 这个函数定义了俩个参数`x, y`, 其中y是一个默认参数,它有一个默认值, 最后我们还定义了一个函数返回值. 
```php
<?php
function sum($x,$y=5) {
  $z=$x+$y;
  return $z;
}

echo sum(5,10);
?>
```

### 常量函数
在php中,我们如果想要定义一个常量,则必须使用常量函数`define()`
```php
<?php
define("constant", "I am constant", true);
echo CONSTANT;
?>
```
最后一个参数是一个可选参数,如果设置为true,则说明访问常量的时候是不区分大小写的. 访问常量则不需要`$`符号

### 日期函数
我们使用`date(format,timestamp)`函数来获得系统的时间, 第二个参数是可选的,如果不填则是当前时间. 第一个参数则是格式化时间戳的格式, 有如下选项
* `d` - 表示月里的某天（01-31）
* `m` - 表示月（01-12）
* `Y` - 表示年（四位数）
* `1` - 表示周里的某天
* `h` - 带有首位零的 12 小时小时格式
* `i` - 带有首位零的分钟
* `s` - 带有首位零的秒（00 -59）
* `a` - 小写的午前和午后（am 或 pm）
```php

<?php 
$now = date("Y-m-d h:i:sa");
echo $now;
?>
```

## 流程控制

### if else
```php
<?php
define("TEN", 10);

if (TEN<"20") {
    echo "10 < 20";
} else {
	echo "ERROR";
}
?>
```
如果还有其他条件的话我们可以采用
```php
<?php
define("TEN", 10);

if (TEN<20) {
    echo "10 < 20";
} elseif (TEN==20) {
  echo "ERROR";
} else {
	echo "ERROR";
}
?>
```

### switch
```php
<?php
switch ($x)
{
case 1:
  echo "Number 1";
  break;
case 2:
  echo "Number 2";
  break;
default:
  echo "No number between 1 and 3";
}
?>
```

### while
```php
<?php 
$x=1; 
while($x<=5) {
  echo "这个数字是：$x <br>";
  $x++;
} 
?>
```

### for
```php
<?php 
for ($x=0; $x<=10; $x++) {
  echo $x;
} 
?>
```

另外还有一种适用于数组的foreach
```php
<?php 
$colors = array("red","green","blue","yellow"); 

foreach ($colors as $value) {
  echo "$value";
}
?>
```
如果我们遍历数组的时候,数组的元素是一个键值对的话, 我们可以这样处理
<?php 
$colors = array("red:555","green:123","blue:856"); 

foreach ($colors as $colerKey => $colorValue) {
  echo "$colerKey  is $colorValue";
}
?>
```
`=>`就表示一个键值对

## 文件
我们使用`fopen(fileName, openMode)`函数打开文件, 第一个参数是文件名, 第二个参数打开模式
* `r`:	打开文件为只读。文件指针在文件的开头开始。
* `w`:	打开文件为只写。删除文件的内容或创建一个新的文件，如果它不存在。文件指针在文件的开头开始。
* `a`:	打开文件为只写。文件中的现有数据会被保留。文件指针在文件结尾开始。创建新的文件，如果文件不存在。
* `x`:	创建新文件为只写。返回 FALSE 和错误，如果文件已存在。
* `r+`:	打开文件为读/写、文件指针在文件开头开始。
* `w+`:	打开文件为读/写。删除文件内容或创建新文件，如果它不存在。文件指针在文件开头开始。
* `a+`:	打开文件为读/写。文件中已有的数据会被保留。文件指针在文件结尾开始。创建新文件，如果它不存在。
* `x+`:	创建新文件为读/写。返回 FALSE 和错误，如果文件已存在。
```php
<?php
$demofile = fopen("demo.txt", "r") or die("Unable to open file!");
?>
```

读取文件内容, `fread()`函数会读取整个文件内容
```php
<?php 
$demofile = fopen("demo.txt", "r") or die("Unable to open file!");
fread($demofile,filesize("demo.txt"));
?>
```
按行读取
```php
<?php
$demofile = fopen("demo.txt", "r") or die("Unable to open file!");
while(!feof($demofile)) {	// 如果文件没有到达结尾的话,则继续读取
  echo fgets($demofile);	// 读取一行
}
fclose($myfile);		// 关闭文件
?>
```

向文件中写入数据
```php
<?php 
$demofile = fopen("demo.txt", "w") or die("Unable to open file!");
fwrite($demofile, "hello world");
fclose($myfile);
?>
```

## mysql
建立连接
```php
<?php 
// 建立mysql连接
$con = mysql_connect("localhost","root","root");
if (!$con) {
  die('Could not connect: ' . mysql_error());
}

// 选择数据库
mysql_select_db("my_db", $con);

// 执行sql语句
$result = mysql_query("SELECT * FROM Persons");

// 遍历结果
while($row = mysql_fetch_array($result)) {
	// 输出某个列元素
  echo $row['FirstName'];
}

// 关闭连接
mysql_close($con);
?>
```
