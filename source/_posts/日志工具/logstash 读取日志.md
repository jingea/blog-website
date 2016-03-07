category: 日志工具
date: 2015-08-08
title: logstash读取日志
---
启动logstash时,我们需要指定配置文件, 以便让logstash按照我们配置的方式进行工作, 例如`logstash -f ./logstash.conf`

## 接受标准日志
```
stdin {
        add_field => {"key" => "value"}
        tags => ["add"]
        type => "std"
 }
```

## 从文件中读取
```json
input {
	# 只支持文件的绝对路径,而且会不自动递归目录. /path/to/**/*.log,用 ** 来缩写表示递归全部子目录.
	file {
        path 				=> ["D:/logs/*.log"]
        type 				=> "system"
        start_position 		=> "beginning"
		discover_interval 	=> # logstash 每隔多久去检查一次被监听的 path 下是否有新文件.默认值是 15 秒.
		exclude 			=> # 不想被监听的文件可以排除出去,这里跟 path 一样支持 glob 展开.
		stat_interval  		=> # logstash 每隔多久检查一次被监听文件状态（是否有更新）,默认是 1 秒.
		start_position   	=> # logstash 从什么位置开始读取文件数据,默认是结束位置,也就是说 logstash 进程会以类似 tail -F 的形式运行
    }
}
```

## 接受rsysylog日志
```
input {
	syslog {
		port => "514"
	}
}
```

## 接受TCP日志
```
input {
	tcp {
		port => 8888
		mode => "server"
		ssl_enable => false
	}
}
```
logstash可以直接接受tcp日志,但是由于logstash内部采用SizedQueue实现日志队列机制,它只能缓存20个日志事件,因此这个功能更多的是在测试时候使用,正式环境中应该采用其他更加高效地消息队列.


```java
Socket socket = new Socket();
socket.connect(new InetSocketAddress("localhost", 8888));
BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
for (int i = 0; i < 10000; i++) {
	writer.write("hello world");
}
writer.close();
socket.close();
```
