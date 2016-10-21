category: mgits
date: 2016-10-21
title: Html 随笔
---

## 引入HTML
最近自己在做个小web项目. 由于很多年没有写过html了, 很多东西都忘记的差不多了. 第一件难道我的事情就是我想整个项目都引用同一个header html页面. 于是乎在网上搜索了几种解决方案.
* 使用iframe `<iframe src="header.html" width="100%" height="100" frameborder="0" scrolling="no">` 这种方式虽然有效, 但是会出现header的弹窗被父页面遮蔽的问题(没有尝试将主页面也引入的情况)
* 使用 html5 的`embed`标签`<embed type="text/html" src="header.html" />`
* 使用jquery + div标签的方式, 这种方式引发的问题是 header.html 内部的 angularjs 失效了
后来在知乎上找解决方案
* 使用 angularjs 的 `<ng-include>` 实现`<div ng-include="'header.html'"></div>`