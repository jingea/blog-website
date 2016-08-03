category: 数据库
date: 2016-03-16
title: Data too long for column 引起的设置 sql_mod
---
今天游戏上线, 在角色登陆日志里突然发生了
```bash
Data too long for column 'name' at row 1
```
当时在我们测试环境测试了,没问题, 这是为啥嘞? 当时没有纠结这个问题, 现在出现问题的这个varchar长度调大, 问题解决.

但是为啥生产环境出现问题, 而测试环境就没问题了, 于是开启了我的探索之旅...

我在自己的机器上创建了一张这样的表
```sql
CREATE TABLE `testvarchar` (
  `name` varchar(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8
```
执行以下语句
```sql
INSERT INTO testvarchar(NAME) VALUES("0123456789");
INSERT INTO testvarchar(NAME) VALUES("0123456789123");
INSERT INTO testvarchar(NAME) VALUES("中文测试中文测试中文测试");
INSERT INTO testvarchar(NAME) VALUES("abcdeabcdeabcde");
INSERT INTO testvarchar(NAME) VALUES("a        ee  ee");
```
查询结果为
```bash
0123456789
0123456789
中文测试中文测试中文
abcdeabcde
a        e
```
很奇怪数据都存进去了,但是却把我的数据给截取到指定的长度10个字符. 而且不管是中文还是数字都是10字符长度. 这会不会和刚才那个问题有关系呢? 鉴于刚才那个问题没有头绪, 于是我看下这个截取的是为啥？
于是我找到了这篇文章[mysql中varchar字段长度超过限制长度自动截取的问题](http://www.th7.cn/db/mysql/201512/146503.shtml)

我在本地种使用
```sql
SET SESSION sql_mode='STRICT_TRANS_TABLES';
SELECT @@sql_mode;
```
再次执行上面的插入,果真产生了`Data too long for column`, 看来就是生产环境里设置了这个值引起的.

在本地环境里也要设置成和生产环境一样的, 肯定不能用`SESSION`，用`GLOBLE`也不靠谱,我再Windows上对my.ini进行修改
```bash
# Set the SQL mode to strict
sql-mode="STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION"
```
这样一来,哪怕MySQL重启了,我们的设置仍然有效.

最后贴一下其他的sql_mode的值
* `ONLY_FULL_GROUP_BY`：对于GROUP BY聚合操作，如果在SELECT中的列，没有在GROUP BY中出现，那么这个SQL是不合法的，因为列不在GROUP BY从句中
* `NO_AUTO_VALUE_ON_ZERO`：该值影响自增长列的插入。默认设置下，插入0或NULL代表生成下一个自增长值。如果用户 希望插入的值为0，而该列又是自增长的，那么这个选项就有用了。
* `STRICT_TRANS_TABLES`：在该模式下，如果一个值不能插入到一个事务表中，则中断当前的操作，对非事务表不做限制
* `NO_ZERO_IN_DATE`：在严格模式下，不允许日期和月份为零
* `NO_ZERO_DATE`：设置该值，mysql数据库不允许插入零日期，插入零日期会抛出错误而不是警告。
* `ERROR_FOR_DIVISION_BY_ZERO`：在INSERT或UPDATE过程中，如果数据被零除，则产生错误而非警告。如 果未给出该模式，那么数据被零除时MySQL返回NULL
* `NO_AUTO_CREATE_USER`：禁止GRANT创建密码为空的用户
* `NO_ENGINE_SUBSTITUTION`：如果需要的存储引擎被禁用或未编译，那么抛出错误。不设置此值时，用默认的存储引擎替代，并抛出一个异常
* `PIPES_AS_CONCAT`：将"||"视为字符串的连接操作符而非或运算符，这和Oracle数据库是一样的，也和字符串的拼接函数Concat相类似
* `ANSI_QUOTES`：启用ANSI_QUOTES后，不能用双引号来引用字符串，因为它被解释为识别符


