category: Bug库
date: 2015-10-15
title: 在Java项目中遇到的Bug
---

## FastJSON

### 序列化bug
在项目中运营团队给出了下面这样的一个公告
```
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
```
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
原因还待查,总之以后再有非程序生成的Json字符时一定要先进行序列化检查,否则会造成线上重大问题.