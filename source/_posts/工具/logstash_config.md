category: 工具
date: 2015-08-08
title: logstash配置文件
---
### input插件
```json
input {
    # 用来收集系统性能和提供各种存储方式来存储不同值的机制
	collectd {	
		port => 25826 ## 端口号与发送端对应
		type => collectd
	}
	# collectd的替换配置
	udp {
		port 		=> 25826
		buffer_size => 1452
		workers 	=> 3       # Default is 2
		queue_size 	=> 30000   # Default is 2000
		codec 		=> collectd { }
		type 		=> "collectd"
	}
	# 只支持文件的绝对路径，而且会不自动递归目录. /path/to/**/*.log，用 ** 来缩写表示递归全部子目录。
	file {
        path 				=> ["D:/logs/*.log"]
        type 				=> "system"
        start_position 		=> "beginning"
		discover_interval 	=> # logstash 每隔多久去检查一次被监听的 path 下是否有新文件。默认值是 15 秒。
		exclude 			=> # 不想被监听的文件可以排除出去，这里跟 path 一样支持 glob 展开。
		stat_interval  		=> # logstash 每隔多久检查一次被监听文件状态（是否有更新），默认是 1 秒。
		start_position   	=> # logstash 从什么位置开始读取文件数据，默认是结束位置，也就是说 logstash 进程会以类似 tail -F 的形式运行
		
		# codec配置
		codec 				=> "json"
    }
	stdin {
        add_field => {"key" => "value"}
        codec => "plain"
        tags => ["add"]
        type => "std"
		
		codec => multiline {
            pattern => "^\["
            negate => true
            what => "previous"
        }
    }
	syslog {
		port => "514"
	}
	input {
		tcp {
			port => 8888
			mode => "server"
			ssl_enable => false
		}
	}
}
```

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

### output插件
```json
output {
    file {
        path => "D:\logs\a.log"
        message_format => "%{message}"
        gzip => false
    }
	elasticsearch {
        host => "192.168.0.2"
        protocol => "http"
        index => "logstash-%{type}-%{+YYYY.MM.dd}"
        index_type => "%{type}"
        workers => 5
        template_overwrite => true
    }
}
```