category: 杂记
date: 2015-10-15
title: CheckList
---

## FastJSON

### 序列化bug
在项目中运营团队给出了下面这样的一个公告
```java
================================================
官方QQ群： 467027422
官方微信号：gm-xyxmp
微信订阅号：xyxmpsy

亲爱的小伙伴：

大家好，老猪给各位请安了！

首款能交易的3D卡牌游戏，星爷独家正版授权《西游降魔篇3D》
自2015年7月21日正式登陆iOS平台后，大量玩家热情涌入游戏
，老猪我面对此景喜极而泣，为了让更多小伙伴加入到咱们的
大团队中，老猪我决定[FFFF00]10月16日11：00开启新服N23-物华天宝 [-]，
诚邀各位新老玩家的加入！


新服开启，精彩纷呈的活动期待各位的参与，丰厚的大礼拿到手
抽筋，在《西游降魔篇3D》西行的途中，让我们一同感受友情，
感受激情，感受无限快乐！

服务器名称：[FFFF00]N23-物华天宝 [-]
开服时间：[FFFF00]2015年10月16日11：00（周五）[-]


伴随开服的同时，多个活动前来助阵！
[FFFF00]活动一：连续登陆送豪礼[-]
[FFFF00]活动二：冲级领元宝[-]
[FFFF00]活动三：战力大比拼[-]
[FFFF00]活动四：五星神将双倍抽[-]
[FFFF00]活动五：天天有礼送段小姐[-]
[FFFF00]活动六：累积充值送玉帝[-]
[FFFF00]活动七：每日礼包大回馈[-]
[FFFF00]活动八：首充翻倍送豪礼[-]
[FFFF00]活动九：签到送好礼[-]


活动详情请查看游戏内公告！
================================================
                                                            《西游降魔篇3D》运营团队
```
当我将其赋值到一个对象`obj#value`时,然后使用`JSON.toJSONString(obj)`的时候遇到了下面的异常:
```java
Exception in thread "main" java.lang.ArrayIndexOutOfBoundsException: 160
	at com.alibaba.fastjson.serializer.SerializeWriter.writeStringWithDoubleQuote(SerializeWriter.java:868)
	at com.alibaba.fastjson.serializer.SerializeWriter.writeStringWithDoubleQuote(SerializeWriter.java:602)
	at com.alibaba.fastjson.serializer.SerializeWriter.writeString(SerializeWriter.java:1366)
	at com.alibaba.fastjson.serializer.StringCodec.write(StringCodec.java:49)
	at com.alibaba.fastjson.serializer.StringCodec.write(StringCodec.java:34)
	at com.alibaba.fastjson.serializer.JSONSerializer.write(JSONSerializer.java:369)
	at com.alibaba.fastjson.JSON.toJSONString(JSON.java:430)
	at com.alibaba.fastjson.JSON.toJSONString(JSON.java:418)
	at Print1.main(Print1.java:52)
```
出现的原因是:`服务器名称：[FFFF00]N23-物华天宝 [-]`物华天宝后面跟的空格，不是我们常用的ascii码为32的空格，而是一个ascii码为160特殊的空格符,导致JSON序列化时失败.

## 用户名字符集
用户可能输入emoji表情符号,这种符号普遍存在iOS与android系统中,这种表情不处理直接存储到MySQL5.5以下的版本会报错
> 这种符号采用Unicode 6标准4个bytes作为存储单元,MySQL存储这种字符需要修改数据库字符集为utf8mb4,但数据回传给网页或者移动客户端时则需要做兼容处理

## 日志问题
在项目的开发过程中不允许使用debug模式, 一定要使用日志分析问题, 因为在线上时候, 没办法使用debug的方式, 因此在记录的日志的时候, 一定要记录的全(每个数据变化都要记录下来), 简(剩下来的就是钱)


## 数据接口都尽量做成数组形式
这是为了提高拓展性,以防策划将模块提出批处理

## 代码中不要出现中文
当要出现中文的时候,可以根据个英文索引到表里查国际化文字.

服务器向客户端提示的时候发送错误号,由客户端进行国际化

## 客户端打包
在打包的时候，加上时间和svn版本号，方便渠道确定客户端的版本是一致的

## 交易类模块
交易类模块要慎重考虑,玩家可能刷元宝

## 玩家使用模拟器登录
这个要做技术处理

## id
项目里边每个模块的ID要是全区唯一的,保证合服顺利

## GWF的问题
参考[记今天域名和GWF的问题](http://blog.zhukunqian.com/?p=1377)

## 模块次数
每个模块活动都加一个每日次数限制。例如扫荡类的功能，加一个每日最大扫荡次数，避免出现角色刷货币的情况.

## 道具
* 道具信息里添加上获得来源途径信息. 将途径分类, 以区别特殊道具 

## 数据备份
每天备份玩家数据, 以便后期查bug和回档使用