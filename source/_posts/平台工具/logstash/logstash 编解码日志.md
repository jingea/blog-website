category: 平台工具
tag: logstash
date: 2015-12-15
title: logstash编解码日志
---
我们可以在`input`或者`ouput`插件里使用`codec`插件, 正如logstash流程那样`input -> decode -> fliter -> encode -> output`

## json
当我们向文件输出日志的时候, 如果不指定编码的话
```
output {
    file {
        path => "D:\log\generate\%{+yyyy-MM-dd hh-MM}.log"
    }
}
```
会将
```
moon3  2015-12-15 00:00:00,008 [ThreadPoolTaskExecutor-1] PlayerManagerImpl(506) ERROR ERROR update player error 2:1362104689008
```
输出为
```
{"message":"moon3  2015-12-15 00:00:00,008 [ThreadPoolTaskExecutor-1] PlayerManagerImpl(506) ERROR ERROR update player error 2:1362104689008","@version":"1","@timestamp":"2015-12-16T03:43:14.112Z","host":"wangming-PC","path":"D:/log/logs/moon-alert-2015-12-15.log","type":"system"}
```

而我们指定编码为json的话
