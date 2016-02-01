category: 数据库 
tag: mycat
date: 2015-10-08
title: Mycat分片
---
[Mycat权威指南V1](https://item.taobao.com/item.htm?spm=a230r.1.14.8.eRsdoe&id=44263828402&ns=1&abbucket=17#detail)学习总结

由于分片规则主要定义在function里,因此下面的讲解中主要是针对function的讲解

## 分片枚举
```xml
<function name="hash-int" class="org.opencloudb.route.function.PartitionByFileMap">
	<property name="mapFile">partition-hash-int.txt</property>
	<property name="type">0</property>
	<property name="defaultNode">0</property>
</function>
```
* mapFile: 配置文件名称
* type: 0表示Integer，非零表示String
* defaultNode: 枚举分片时，如果碰到不识别的枚举值，就让它路由到默认节点

在partition-hash-int.txt文件中,采用`x=y`的形式, x是columns值. y是节点值. 所有的节点都是从0开始的.
```
a=0
b=1
c=2
```
上面这个例子中,columns值为1的sql会路由到1的这个节点上. 节点在schema.xml中配置. 具体0对应哪个节点还需要探寻.

## 范围约定
```xml
<function name="rang-long" class="org.opencloudb.route.function.AutoPartitionByLong">
  <property name="mapFile">autopartition-long.txt</property>
  <property name="defaultNode">0</property>
</function>
```
这个规则是约定好,哪个范围的sql对应哪个节点,我们可以这样配置autopartition-long.txt
```
0-500=0
500-1000=1
1000-1500=2
```
这种范围的就只能适应,id分片的了

## 求模
```xml
<function name="mod-long" class="org.opencloudb.route.function.PartitionByMod">
 <!-- how many data nodes  -->
  <property name="count">2</property>
</function>
```
这个是根据column的十进制值进行求模分片. 


## 固定分片hash算法
该算法是对columns的二进制的低十位进行取模运算.
```xml
<function name="partitionByLong" class="org.opencloudb.route.function.PartitionByLong">
  <property name="partitionCount">8, 1</property>
  <property name="partitionLength">128, 256</property>
</function>
```
* partitionCount 分片个数列表
* partitionLength 分片范围列表
这来个参数的长度必须相等. 上面的例子的意思是, 在对低十位取模运算时, 128以内的sql要平均分配到8个分片上执行. 128 ~ 256的sql要到1个分片上执行
