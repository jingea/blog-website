category: ELK
tag: logstash
date: 2015-12-15
title: logstash输出日志
---

## 标准输出
```
output {
    stdout {
        codec => rubydebug
        workers => 2
    }
}
```
使用`stdout`我们将能将接受到的日志输出到控制台里

## 输出到文件
```
output {
    file {
        path => "D:\log\generate\%{+yyyy-MM-dd}.log"
    }
}
```
采用这个插件日志就会输出到`D:\log\generate`目录下的2015-12-15.log文件中. 

在file插件里,除了path字段之外,我们还有俩个字段`message_format`和`gzip`
* `message_format` ：选择要输出的内容,默认是输出整个event的JSON形式字符串, 我们可以将其设置为`%{message}`, 这样我们就只会输出日志内容了
* `gzip` : 启用这个参数是采用gzip压缩.

## 输出到elasticsearch
将接受到的日志输出到 elasticsearch中
```
output {
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