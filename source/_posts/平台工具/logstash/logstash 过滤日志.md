category: 平台工具
tag: logstash
date: 2015-12-15
title: logstash过滤日志
---
### filter插件
```json
filter {
	#命名正则表达式，在稍后(grok参数或者其他正则表达式里)引用它	
    grok {
        match => ["message",  "%{COMBINEDAPACHELOG}"]
    }
	date {
        match => ["logdate", "dd/MMM/yyyy:HH:mm:ss Z"]
    }
	json {
        source => "message"
        target => "jsoncontent"
    }
	mutate {
		#类型转换
        convert => ["request_time", "float"]
		#字符串处理
		gsub 	=> ["urlparams", "[\\?#]", "_"]
		split 	=> ["message", "|"]
		join 	=> ["message", ","]
		merge 	=> ["message", "message"]
		#字段处理
		rename => ["syslog_host", "host"]
		update => ["syslog_host", "host"]
		replace => ["syslog_host", "host"]
    }
	ruby {
        
    }
	split {
        field => "message"
        terminator => "#"
    }
	
}
```