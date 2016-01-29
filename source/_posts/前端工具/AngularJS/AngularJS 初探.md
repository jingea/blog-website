category: 前端工具
tag: AngularJS
date: 2015-01-21
title: AngularJS 初探
---
[AngularJS 教程](http://www.runoob.com/angularjs/angularjs-tutorial.html) 学习心得

AngularJS 是一个 JavaScript 框架。 它可通过 `<script>` 标签添加到 HTML 页面。 AngularJS 通过 `指令` 扩展了 HTML，且通过 `表达式` 绑定数据到 HTML。 这也就是说我们通过指令和表达式完成了Html标签和数据的双向绑定.

看如下例子
```html
<div ng-app="firstApp" ng-init="firstName='John'">

 	<p>在输入框中尝试输入：</p>
 	<p>姓名：<input type="text" ng-model="firstName"></p>
 	<p>你输入的为： {{ firstName }}</p>

</div>
```
我们来讲解一下这段代码. 

* 首先我们使用了`ng-app`这个属性, 这个属性是告诉AngularJS, 当前`div`下的所有`ng`属性都是绑定到`firstApp`这个AngularJS应用程序上
* 然后我们使用`ng-init`进行数据初始化, 于是`firstApp`这个AngularJS应用程序这个应用程序里就有了一个变量`firstName`, 它的初始值为`John`
* 然后我们又使用到了`ng-model`这个属性, 这个属性的作用是将变量`firstName`绑定到了`<input>`这个标签上, 标签值的变化会直接反应到`firstName`这个变量上
* 最后我们在`{ { } }` 表达式中引用了`firstName`这个变量

