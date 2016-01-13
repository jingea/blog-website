category: 编程语言
tag: JavaScript
date: 2015-09-08
title: JavaScript函数
---
JavaScript是一门基于原型继承的函数式的面向对象的编程语言,对象可以直接从其他对象继承属性,且JavaScript是无类型的.

JavaScript中函数就是对象。函数对象连接到Function.prototype(该原型连接到Object.prototype).
* 每个函数对象在创建时也随配一个prototype属性,它的值是一个拥有constructor属性且值为该函数的对象（这和连接到Function.prototype全完不同）.
* 因为函数是对象,所以函数可以保存在变量,对象和数组中. 函数可以当做参数传递给其他函数,函数也可以再返回给函数.而且因为函数是对象,函数还可以拥有方法
* 一个函数总会有一个返回值,如果没有指定返回值则返回undefined(如果函数调用前加上了new前缀,且返回的不是一个对象,则返回this)

### 函数字面量
我们定义一个函数字面量
```JavaScript
var add = function(a, b) {
	return a + b;
}
```
上面这个函数字面量是通过将一个匿名函数赋值给一个变量.

函数字面量可以出现在任何允许表达式出现的地方. 函数可以被定义在其他函数中. 可以作为函数参数或者函数返回值出现。函数甚至拥有自己的作用域(就像变量有自己的作用域一样)

每声明一个函数实际是创建了一个Function 实例,上面的函数等价于
```javascript
var Add = new Function("a","b","a + b;");

var add = new Add(1, 2);
```

### 闭包
内部函数可以访问自己内部的参数和变量还可以访问嵌套在父函数的参数和变量。通过函数字面量创建的函数对象包含了一个连接到外部上下文的连接，这杯称为闭包(每个函数在创建时会附加俩个隐藏属性：函数的上下文和实现函数行为的代码).


### 函数调用
当调用函数时除了显示地向其传递的参数,每个函数还会接受俩个附加参数`this`, `arguments`. 当实际参数大于形式参数时,多余的参数会被忽略,而当实际参数小于形式,参数时缺少的参数会被赋值为undefined.

下面会介绍四种调用模式,每种调用模式对`this`参数的初始化是不一样的.

#### 方法调用模式
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

#### 函数调用模式
当一个函数并非一个对象属性时,那么它就被当做是一个函数来调用
```javascript
function add(a, b) {
	return a + b;
}

c = add(1, 2);
```
这种模式下this被绑定到全局对象上

#### 构造器调用模式


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

#### Apply调用模式
JavaScript的函数可以拥有方法,apply方法可以让我们构建一个参数数组传递给调用函数. apply方法接受俩个参数`this`和参数数组.
```javascript
var add = function(a, b) {
	return a + b;
}

var argus = [3, 4];
var sum = add.apply(null, argus);
```

### arguments数组
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

### 异常
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

### 作用域
代码块是包在一对花括号中的一组语句，JavaScript中的代码块不会创建新的作用域.但是函数确实是有其自己的作用域的,但是在函数内部定义的变量在整个函数体的任意位置都是可见的,因此变量应该被定义在函数的头部。

作用域的好处是内部函数可以访问定义在他们的外部函数的参数和变量(除了this和arguments)

### 闭包
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

### 模块
我们可以使用函数和闭包来构造模块. 模块是一个提供接口却隐藏状态与实现的函数或者对象.

模块的一般形式是：一个定义了私有变量和函数的函数,利用闭包创建可以访问私有变量和函数的特权函数,最后返回这个特权函数,或者将他们保存到一个可访问到的地方.



### 柯里化
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



### 普通函数

```JavaScript
function add(a, b) {
	return a + b;
}
```
