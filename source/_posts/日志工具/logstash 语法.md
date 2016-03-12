category: 日志工具
date: 2015-08-08
title: logstash 语法
---
一般我们将logstash的配置写到一个配置文件中. 然后在启动logstash时,加载这些配置.

然后我们就可以在配置文件中写自己的插件配置. 常用的插件有`input`, `codec`, `filter`, `output`, 一般我们最少也要写一个`input`插件, 否则启动的时候会报错.

插件的形式如下
```json
input {
    stdin {}
    syslog {}
}
```
`{}`称为区域, 我们可以在区域里定义新的插件. 我们还可以在区域内定义字段, 然后在其他的插件中引用这个字段, 例如
```json
input {
    stdin {
		type => "std"
	}
}
```
