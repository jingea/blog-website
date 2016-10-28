category: 数据库
date: 2015-10-08
title: Mycat全局序列号
---
[Mycat权威指南V1](https://item.taobao.com/item.htm?spm=a230r.1.14.8.eRsdoe&id=44263828402&ns=1&abbucket=17#detail)学习总结

Mycat的全局序列号分为俩部分
* 本地文件方式
* 数据库方式

因为本地文件方式当Mycat重启之后，里面记录的数据会被重置，所以我就只记录一下数据库方式.

在`server.xml`文件中进行如下配置
```xml
<mycat:server xmlns:mycat="http://org.opencloudb/">
	<system>
		<property name="sequnceHandlerType">1</property>
	</system>
</mycat:server>
```
接下来我们创建一张表,用于存放ID
```sql
DROP TABLE IF EXISTS MYCAT_SEQUENCE;
CREATE TABLE db1.MYCAT_SEQUENCE
(
  name VARCHAR(10),
  currentValue BIGINT DEFAULT 1 NOT NULL ,
  increment INT DEFAULT 1 NOT NULL,
  PRIMARY KEY(name)
) ENGINE=InnoDB CHAR SET=utf8;
```
获取当前sequence的值
```sql
DROP FUNCTION IF EXISTS mycat_seq_currval;
DELIMITER $$
CREATE FUNCTION mycat_seq_currval(seq_name VARCHAR(50)) RETURNS VARCHAR(64) CHARSET utf8
DETERMINISTIC
BEGIN
	DECLARE retval VARCHAR(64);
	SET retval="-999999999,null";
	SELECT CONCAT(CAST(current_value AS CHAR),",",CAST(increment AS CHAR))
	 INTO retval
	 FROM MYCAT_SEQUENCE
	 WHERE NAME = seq_name;
	 RETURN retval;
END
$$
DELIMITER ;
```
> 这里需要说明一点的是, 由于mysql默认分隔符为`;`,因此我们要先使用`DELIMITER $$`将分隔符置为`&&`,不然的话在函数里也使用到了`;`会影响到函数,最后再将分隔符置为`;`

设置sequence值
```sql
DROP FUNCTION IF EXISTS mycat_seq_setval;
DELIMITER $$
CREATE FUNCTION mycat_seq_setval(seq_name VARCHAR(50),VALUE INTEGER) RETURNS VARCHAR(64) CHARSET utf8
DETERMINISTIC
BEGIN
UPDATE MYCAT_SEQUENCE
SET current_value = VALUE
WHERE NAME = seq_name;
RETURN mycat_seq_currval(seq_name);
END
$$
DELIMITER ;
```

获取下一个sequence值
```sql
DROP FUNCTION IF EXISTS mycat_seq_nextval;
DELIMITER $$
CREATE FUNCTION mycat_seq_nextval(seq_name VARCHAR(50)) RETURNS VARCHAR(64) CHARSET utf8
DETERMINISTIC
BEGIN
UPDATE MYCAT_SEQUENCE
SET current_value = current_value + increment WHERE NAME = seq_name;
RETURN mycat_seq_currval(seq_name);
END
$$
DELIMITER ;
```
然后修改`sequence_db_conf.properties`这个配置文件
```shell
#sequence stored in datanode
GLOBAL=dn1
COMPANY=dn1
CUSTOMER=dn1
ORDERS=dn1
```
* `GLOBAL`
* `COMPANY`
* `CUSTOMER`
* `ORDERS`
