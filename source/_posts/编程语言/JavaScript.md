category: 编程语言
title: JavaScript
---
JavaScript是一门基于原型继承的函数式的面向对象的编程语言,对象可以直接从其他对象继承属性,且JavaScript是无类型的.

# 数字
JavaScript只有一个数字类型,它在内部被表示为64位的浮点数. 它没有分离出整数类型,因此1和1.0的值是相同的

NaN是一个数值，它表示一个不能产生正常结果的运算结果。NaN不等于任何值，包括它自己。可以使用函数isNaN来检测NaN。

# 字符串
字符串可以由一对单引号或者一对双引号构成，可以包含0到多个字符。`\`是转义字符。JavaScript采用Unicode16作为字符集，因此所有的字符都是16位的。

字符串有一个length属性，可以获得字符串长度。

字符串同样也是不可变的。一旦字符串被创建出来就无法改变它。我们同+链接其他字符串创建一个新的字符串。俩个包含着相同字符且字符顺序也相同的字符被认为是同一个字符串。`===`进行字符串判断。

# 变量
我们通过 `var`关键字来声明一个变量

## 函数私有变量
JavaScript通过函数管理作用域。在函数内部声明的变量只在这个函数内部可用，而在函数外面不可用。

## 全局变量
每个JavaScript环境有一个全局对象，当你在任意的函数外面使用this的时候可以访问到。你创建的每一个全局变量都成了这个全局对象的属性。

# 控制流程

我们可以通过条件语句(if和switch),循环语句（while，for和do）强制跳转语句（break,return，throw）和函数调用来改变执行序列。

## if
进行if判断时，下列值被作为假：
* false
* null
* 空字符串 `''`
* 数字0
* 数字NaN

## switch
其表达式的值和case条件进行匹配。表达式可以是字符串或者数字。case表达式不一定必须是常量
```javascript
switch (num)
{
	case 1:
	  x="输入正确";
	  break;
	default:
	  x="输入错误";
}
```

## while
```javascript
while (i<5)
{
	sum += i;
}
```

## for
```javascript
for (var i = 0; i<5; i++)
{
	sum += i;
}
```
for...in
```javascript
var array = [1, 2]
for (i in array)
{
	sum += i;
}
```

# 对象
JavaScript的简单数据类型包括数字，字符串，布尔值，null值和undefined值，其他值都是对象(数组，函数都是对象)。 数字，字符串和布尔值虽然拥有方法，但他们是不可变的，因此不能称他们为对象。JavaScript中的对象是可变的键控集合。

对象是属性的容器，每个属性都有名字和值。
* 属性名：包括空串在内的任意字符串。如果字符串不是JavaScript保留的关键字切是合法的标识符则可以不必使用双引号。
* 属性值：包括undefined值之外的任何值

但是JavaScript里的对象是无类型的。

JavaScript包含一种原型链特性,允许对象继承另一个对象的属性.正确的使用它能减少对象初始化时小号的时间和内存.

## 对象字面量
包围在一对花括号中的0~N个键值对即为对象字面量。
```javascript
var empoty = {};

var xiaoming = {
	name : "小明",
	age  : 18
}
```
对象字面量还可以嵌套使用

```javascript
var xiaoming = {
	name : "小明",
	age  : 18
	chengji: {
		yuwen: 99,
		shuxu: 100
	}
}
```

## 检索对象里的值
我们可以使用`[]`括住一个字符串表达式来索引某个值
```javascript
xiaoming["name"]
```
如果该字符串是一个合法的标识符且不是保留关键字，那么也可以使用`.`进行索引
```javascript
xiaoming.name
```
如果我们索引`hair`这个属性的话,会得到一个`undefined`值,因此我们也可以使用`||`指定一个默认值
```javascript
xiaoming.hair || "red"
```
这样我们获得的结果就是`red`这个字符串,可是如果我们向一个`undefined`值继续索引的话会得到一个`TypeError`异常,我们可以使用`&&`来避免
```javascript
xiaoming.cars && xiaoming.cars.changcheng
```
我们通过这种方式获得小明拥有的汽车中长城汽车的属性值.

## 更新对象的值
我们可以通过`=`对对象进行赋值.
```javascript
xiaoming.name = "zhangxiaoming"

// 或者赋值某个新的对象
xiaoming.chengji: {
		yuwen: 99,
		shuxu: 100
	}
```

## 引用对象
对象通过引用来传递

```javascript
var chengji = xiaoming.chengji
```

## 原型链
每个对象都会连接到一个原型对象,并且从中继承属性. 对象字面量会连接到`Object.prototype`

需要指出的是原型连接在对象更新时是不起作用的(如果我们对某个对象做出改变是不会触及该对象的原型).原型连接只有在索引值的时候才会被用到.

### 委托
如果我们尝试去索引某个对象A的值,但该对象没有此属性名,那么JavaScript会试着从A的原型B中进行查找,如果B中也没有的话,会继续向B的原型C中查找,一直找到`Object.prototype`,如果都没有找到那么值就是`undefined`.

### 指定对象的原型
```javascript

```

## 反射
我们可以使用typeof来观察我们的对象中是否包含某个属性
```javascript
typeof xiaoming.name  // 值为string
typeof xiaoming.address  // 值为undefined
```
这样我们可以通过`undefined`来判断某个对象中是否包含某个值,但是有一点需要说明的是`typeof`也会在原型链进行索引判断。

那么我们可以使用`hasOwnProperty`方法进行判断，它不对原型链进行检查同时它的返回值只有布尔值
```javascript
xiaoming.hasOwnProperty("address")  // 值为false
```

## 删除
delete运算符可以用来删除对象的属性.它不会触及原型链中的任何对象. 如果我们自定义的对象属性覆盖了原型中的属性,我们可以通过删除对象的属性而让原型中的属性显露出来


# 函数

JavaScript中函数就是对象。函数对象连接到Function.prototype(该原型连接到Object.prototype).
* 每个函数对象在创建时也随配一个prototype属性,它的值是一个拥有constructor属性且值为该函数的对象（这和连接到Function.prototype全完不同）.
* 因为函数是对象,所以函数可以保存在变量,对象和数组中. 函数可以当做参数传递给其他函数,函数也可以再返回给函数.而且因为函数是对象,函数还可以拥有方法
* 一个函数总会有一个返回值,如果没有指定返回值则返回undefined(如果函数调用前加上了new前缀,且返回的不是一个对象,则返回this)

## 函数字面量
我们定义一个函数字面量
```JavaScript
var add = function(a, b) {
	return a + b;
}
```
上面这个函数字面量是通过将一个匿名函数赋值给一个变量.

函数字面量可以出现在任何允许表达式出现的地方. 函数可以被定义在其他函数中. 内部函数可以访问自己内部的参数和变量还可以访问嵌套在父函数的参数和变量。通过函数字面量创建的函数对象包含了一个连接到外部上下文的连接，这杯称为闭包(每个函数在创建时会附加俩个隐藏属性：函数的上下文和实现函数行为的代码).


## 函数调用
当调用函数时除了显示地向其传递的参数,每个函数还会接受俩个附加参数`this`, `arguments`. 当实际参数大于形式参数时,多余的参数会被忽略,而当实际参数小于形式,参数时缺少的参数会被赋值为undefined.

下面会介绍四种调用模式,每种调用模式对`this`参数的初始化是不一样的.

### 方法调用模式
当一个函数作为一个对象的属性时,我们称其为方法.方法里的this参数被被绑定到该对象上.
```javascript
var obj = {
	value : 1;
	addOne : function() {
		this.value += 1;
	}
};

obj.addOne();
```

### 函数调用模式
当一个函数并非一个对象属性时,那么它就被当做是一个函数来调用
```javascript
function add(a, b) {
	return a + b;
}

c = add(1, 2);
```
这种模式下this被绑定到全局对象上

### 构造器调用模式


一个函数如果创建的目的就是希望集合new关键字来使用,那么它就是构造器函数

如果在一个函数前面带上一个new来调用,实际上会创建一个连接到该函数prototype成员的新对象,同时this也会绑定到那个新对象上.
```javascript
var Quo = function(string) {
	this.status = string;
}

Quo.prototype.get_status = function() {
	return this.status;
}

var myQuo = new Quo("hello");
```

### Apply调用模式
JavaScript的函数可以拥有方法,apply方法可以让我们构建一个参数数组传递给调用函数. apply方法接受俩个参数`this`和参数数组.
```javascript
var add = function(a, b) {
	return a + b;
}

var argus = [3, 4];
var sum = add.apply(null, argus);
```

## arguments数组
在函数内部我们可以通过arguments数组变量访问所有的实际参数
```javascript
var add = function () {
	var sum = 0;
	for(i = 0; i < arguments.length; i += 1) {
		sum += argements[i];
	}
};

var sum = add(1, 2, 3, 4, 5);
```

## 异常
throw语句中断函数的执行,它应该抛出一个exception对象,这个对象包含一个`name`和`message`属性(你也可以添加额外的属性).

我们使用try catch来捕获异常 
```javascript
var add = function(a, b) {
	if(arguments.length < 2) {
		throw {
				name: "arguments error",
				message: "need more arguments"
			}
	}
}

var tryAdd = function(a, b) {
	try {
		return add(a, b);
	} catch(e) {

	}
}

tryAdd(1, 2, 3);
```

## 作用域
代码块是包在一对花括号中的一组语句，JavaScript中的代码块不会创建新的作用域.但是函数确实是有其自己的作用域的,但是在函数内部定义的变量在整个函数体的任意位置都是可见的,因此变量应该被定义在函数的头部。

作用域的好处是内部函数可以访问定义在他们的外部函数的参数和变量(除了this和arguments)

## 闭包
当我们在函数A中定义了函数B,函数B引用了函数A中的变量I,当函数A执行完毕,函数B作为返回值继续被执行时,函数A的变量I是仍然可以被访问的,这就是闭包.
```javascript
var f1 = function() {
	var id = 1132;
	return {
		add: function() {
			return id += 1;
		}

	}
}

var if1 = fi();
var id = if1.add(); // 结果是1133
```
简而言之呢,闭包的特性是保存了创建它时的上下文信息.

## 模块
我们可以使用函数和闭包来构造模块. 模块是一个提供接口却隐藏状态与实现的函数或者对象.

模块的一般形式是：一个定义了私有变量和函数的函数,利用闭包创建可以访问私有变量和函数的特权函数,最后返回这个特权函数,或者将他们保存到一个可访问到的地方.



## 柯里化
函数也是值,我们可以像往常操作值那样去操作函数, 将函数作为一个变量.
```javascript
var f = function(a, b, c) {

}

var f1 = function() {
	return p(1);
} 

var c = f1();
```
上面就实现了函数的柯里化



## 普通函数

```JavaScript
function add(a, b) {
	return a + b;
}
```










