category: 编程语言
date: 2015-09-08
title: JavaScript面向对象和原型链
---
JavaScript是一门基于原型继承的函数式的面向对象的编程语言,对象可以直接从其他对象继承属性,且JavaScript是无类型的.

## 对象
JavaScript的简单数据类型包括数字，字符串，布尔值，null值和undefined值，其他值都是对象(数组，函数都是对象)。 数字，字符串和布尔值虽然拥有方法，但他们是不可变的，因此不能称他们为对象。JavaScript中的对象是可变的键控集合。

对象是属性的容器，每个属性都有名字和值。
* 属性名：包括空串在内的任意字符串。如果字符串不是JavaScript保留的关键字切是合法的标识符则可以不必使用双引号。
* 属性值：包括undefined值之外的任何值

但是JavaScript里的对象是无类型的。

JavaScript包含一种原型链特性,允许对象继承另一个对象的属性.正确的使用它能减少对象初始化时小号的时间和内存.

### 对象字面量
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

### 检索对象里的值
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

### 更新对象的值
我们可以通过`=`对对象进行赋值.
```javascript
xiaoming.name = "zhangxiaoming"

// 或者赋值某个新的对象
xiaoming.chengji: {
		yuwen: 99,
		shuxu: 100
	}
```

### 引用对象
对象通过引用来传递

```javascript
var chengji = xiaoming.chengji
```

### 创建对象

#### Object 模式
```javascript
var o1 = {};//字面量的表现形式
var o2 = new Object;
var o3 = new Object();
var o4 = new Object(null);
var o5 = new Object(undefined);
var o6 = Object.create(Object.prototype);//等价于 var o = {};//即以 Object.prototype 对象为一个原型模板,新建一个以这个原型模板为原型的对象
// 区别
var o7 = Object.create(null);//创建一个原型为 null 的对象
```

#### 构造器模式
```javascript
function Car(sColor){
    this.color = sColor;      
}

var car = new Car("red");
```


### 原型链
每个对象都会连接到一个原型对象,并且从中继承属性. 对象字面量会连接到`Object.prototype`

需要指出的是原型连接在对象更新时是不起作用的(如果我们对某个对象做出改变是不会触及该对象的原型).原型连接只有在索引值的时候才会被用到.

#### 委托
如果我们尝试去索引某个对象A的值,但该对象没有此属性名,那么JavaScript会试着从A的原型B中进行查找,如果B中也没有的话,会继续向B的原型C中查找,一直找到`Object.prototype`,如果都没有找到那么值就是`undefined`.

#### 指定对象的原型
```javascript

```

### 反射
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

### 删除
delete运算符可以用来删除对象的属性.它不会触及原型链中的任何对象. 如果我们自定义的对象属性覆盖了原型中的属性,我们可以通过删除对象的属性而让原型中的属性显露出来

