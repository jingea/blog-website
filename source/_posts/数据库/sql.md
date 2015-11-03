category: 编程语言
date: 2015-10-08
title: 数据库和SQL
---

# Mysql索引

一般我们在创建索引的时候都要指定它的索引名字. 当然这个不是必须的.

## 索引类型

### 普通索引
```sql
CREATE INDEX idxName ON db1.idtable(id);

CREATE TABLE t1 (
  id int NOT NULL,
  INDEX (id)
)
```

### 唯一索引
与普通索引相比值唯一, 可以有空值
```sql
CREATE UNIQUE INDEX id ON db1.idtable(id);

CREATE TABLE t1 (
  id int NOT NULL,
  UNIQUE (id)
)
```

### 主键索引
与普通索引相比值唯一, 不可以有空值
```sql
CREATE TABLE t1 (
  id int NOT NULL,
  PRIMARY KEY (id)
)
```

### 组合索引
下面我们创建了一个唯一组合索引.
```sql
CREATE TABLE `t1` (
  `id` int(11) NOT NULL,
  `name` varchar(16) NOT NULL,
  UNIQUE KEY `idx` (`id`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
```

## 索引注意事项
* 如果索引上出现`NULL`值(包含组合索引),那么这一行就不会被索引
* 我们要尽可能的使用短索引(指定一个前缀长度),例如对varchar类型进行索引,它的长度是16个字符,但是前6个字符是固定的,那么我们指定前缀长度为6就好了
* 尽量不要对索引进行`like`操作
* 不要在索引列上使用`NOT IN`和`<>`操作

# 连接数据库
```
mysql -h 主机地址 -u 用户名 －p 用户密码 （注:u与root可以不用加空格，其它也一样）
```

# 用户操作
创建用户
```
CREATE USER 'username'@'host' IDENTIFIED BY 'password';
```
授权: 
```
GRANT privileges ON databasename.tablename TO 'username'@'host' 
```
> privileges - 用户的操作权限,如SELECT,INSERT,UPDATE等.如果要授予所的权限则使用ALL;如果要授予该用户对所有数据库和表的相应操作权限则可用*表示, 如*.*. 

用以上命令授权的用户不能给其它用户授权,如果想让该用户可以授权,用以下命令: 
```
GRANT privileges ON databasename.tablename TO 'username'@'host' WITH GRANT OPTION; 
```
设置与更改用户密码 
```
SET PASSWORD FOR 'username'@'host' = PASSWORD('newpassword');
```
撤销用户权限 
```
REVOKE privilege ON databasename.tablename FROM 'username'@'host'; 
```
删除用户 
```
DROP USER 'username'@'host';
```

# 数据库操作 
* 显示数据库：`SHOW databases`; 
* 创建库：`CREATE DATABASE 库名`; 
* 删除库：`DROP DATABASE 库名`; 
* 使用库(选中库)：`USE 库名`; 

# 表操作
* 显示数据表：`SHOW tables`
* 显示表结构：`DESC 表名`
* 删除表：`DROP TABLE 表名`; 
* 输入创建表的DDL语句 `SHOW CREATE TABLE 表名;`
* 创建表：
```sql
CREATE TABLE  
    USER  
    (  
        name VARCHAR(30) NOT NULL,  
        id INT DEFAULT '0' NOT NULL,  
        stu_id INT,  
        phone VARCHAR(20),  
        address VARCHAR(30) NOT NULL,  
        age INT(4) NOT NULL,  
        PRIMARY KEY (name),  
        CONSTRAINT stu_id UNIQUE (stu_id)  
    )  
    ENGINE=InnoDB DEFAULT CHARSET=utf8;  
```

## 表数据操作
* 清空表数据`truncate table 表名;`. truncate删除后不记录mysql日志，不可以恢复数据。相当于保留mysql表的结构，重新创建了这个表，所有的状态都相当于新表。
* 清空表数据`delete from 表名`. delete的效果有点像将mysql表中所有记录一条一条删除到删完

## 修改表结构
* 修改列名`alter table 表名称 change 字段名称 字段名称`
* 修改表名`alter table 表名称 rename 表名称`
* 修改某个表的字段类型及指定为空或非空`alter table 表名称 change 字段名称字段名称 字段类型 [null/not null];`
* 修改某个表的字段名称及指定为空或非空`alter table 表名称 change 字段原名称字段新名称 字段类型 [null/not null];`
* 增加一个字段(一列)`alter table table_name add column column_name type default value;` type指该字段的类型,value指该字段的默认值
* 更改一个字段名字(也可以改变类型和默认值)`alter table table_name change sorce_col_name dest_col_name type defaultvalue;` source_col_name指原来的字段名称,dest_col_name指改后的字段名称
* 改变一个字段的默认值`alter table table_name alter column_name set default value;`
* 改变一个字段的数据类型`alter table table_name change column column_name column_name type;`
* 向一个表中增加一个列做为主键`alter table table_name add column column_name type auto_increment PRIMARYKEY;`
* 向一个表中增加一个列做为主键`alter table table_name add column column_name type auto_increment PRIMARYKEY;`
* 删除字段`alter table form1 drop column 列名;`


## 复制表
* 含有主键等信息的完整表结构 `CREATE table 新表名 LIKE book;`
* 只有表结构，没有主键等信息 `create table 新表名 select * from books;
* 将旧表中的数据灌入新表 `INSERT INTO 新表 SELECT * FROM 旧表；` 注：新表必须已经存在

## 导入导出数据库
* 数据库某表的备份,在命令行中输入:`mysqldump -u root -p database_name table_name > bak_file_name`
* 导出数据`select_statment into outfile”dest_file”;`
* 导入数据`load data infile”file_name” into table table_name;`
* 将两个表里的数据拼接后插入到另一个表里`insert into tx select t1.com1,concat(t1.com2,t2.com1) from t1,t2;`


 
## 查询表
mysql查询的五种子句 

### where(条件查询)
```
SELECT * FROM t1 WHERE id > 100;
```
* 数值谓词:`>,=,<,<>,!=,!>,!<,=>,=<`
* 字符串谓词：`=，like`
* 日期谓词：`=` (`SELECT * from t1 WHERE create_time = '2011-04-08'`)


### having（筛选）
```

```

### group by（分组）
```
SELECT id FROM player GROUP BY vip;
```

### order by（排序）
```
SELECT id FROM player ORDER BY id;
```

### limit（限制结果数）
查询前n条记录
```
SELECT id FROM player LIMIT 10;
```
查询后n条记录


# 事务
事务（Transaction）是是一个操作序列，它构成了并发执行的基本单元。事务的提出主要是为了解决并发情况下保持数据一致性。

数据库事务具有ACID特性,即
* 原子性： 原子性体现在对事务的修改,要么全部执行要么都不执行
* 一致性： 保持数据的一致性,例如整数类型的数据大小不能溢出,字符型数据长度不能超过规定范围，保证数据的完整性.
* 隔离性： 如果数据库并发执行A,B俩个事务,那么在A事务执行完之前对B事务是不可见的,也就是说,B事务是看不见A事务的中间状态的.
* 持久性： 事务完成后,它对数据库的影响是永久的,即使数据库出现异常也是如此.


隔离级别
* `Read Uncommitted`: 读取未提交的数据,即其他事务已经提交修改但还未提交的数据(这是最低的隔离级别)
* `Read Committed`: 读取已经提交的数据,但是在一个事务中,对同一个项,前后俩次读取的结果可能不同.
* `Repetable Read`: 可重复读取,在一个事务中,对同一个项,确保前后俩次读取的结果一样
* `Serializable`: 可序列话,即数据库的事务是可串行执行的,就像一个事务执行的时候没有别的事务同时在执行
我们使用下面的语句来改变数据库的隔离级别
```sql
SET [SESSION|GLOBAL] TRANSACTION ISOLATION LEVEL READ UNCOMMITTED | READ COMMITTED | REPEATABLE READ | SERIALIZABLE
```
1. 不带`SESSION、GLOBAL`的SET命令,只对下一个事务有效
2. `SET SESSION` 为当前会话设置隔离模式
3. `SET GLOBAL`为以后新建的所有MYSQL连接设置隔离模式（当前连接不包括在内）

读写异常
* `Lost Update`: 俩个事务并发修改同一个数据,A事务提交成功,B事务提交失败回滚后,A事务的修改都可能会丢失
* `Dirty Reads`: A事务读取了B事务更新却没有提交的数据
* `Non-Repeatable Reads`: 一个事务对同一个数据项的多次读取可能得到不同的结果
* `Second Lost Updates`:俩个事务并发修改同一个数据, B事务可能会覆盖掉A事务的修改
* `Phantom Reads`: A事务进行前后俩次查询,但是在查询过程中出现了B事务向其中插入数据,那么A事务可能读取到未出现的数据

隔离级别与读写异常的关系
```
    LU  DR  NRR  SLU  PR
RU  N   Y   Y    Y    Y
RC  N   N   Y    Y    Y
RR  N   N   N   N     Y
S   N   N   N   N     N
```

## 事务语句
* 开始事物：`BEGIN TRANSACTION`
* 提交事物：`COMMIT TRANSACTION`
* 回滚事务：`ROLLBACK TRANSACTION`

```sql
# 开启一个事务
START TRANSACTION;
INSERT INTO db1.`t1`(id) VALUES(1);
# 提交事务
COMMIT;

# 开启事务
START TRANSACTION;
INSERT INTO db1.`t1`(id) VALUES(2);
# 回滚刚才的事务
ROLLBACK;
```


## 并发控制

### 锁

### 写时复制

### 多版本并发控制

Mysql InnoDB存储引擎,InnoDB对每一行维护了俩个隐含的列,一列用于存储行被修改的时间,另一列存储每一行被删除的时间.
> 这里的时间并不是绝对时间,而是与时间对应的数据库系统的版本号,每当一个事务开始时,InnoDB都会给这个事务分配一个递增的版本号,所以版本号也可以被任务是事务好.对于每一行的查询语句,InnoDB都会把这个查询语句的版本号同这个查询雨具遇到的行的版本号进行对比,然后结合不同的事务隔离级别来决定是否返回改行.

下面以SELECT,DELETE,INSERT,UPDATE为例:
#### SELECT
只有同时满足下面俩个条件的行才能被返回:
1. 行的版本号小于等于该事务的版本号
2. 行的删除版本号要么没有定义,要么大于等于事务的版本号
如果行的修改或者删除版本号大于事务号,说明行是被该食物后面启动的事务修改或者删除的 


#### DELETE
InnoDB直接把该行的删除版本号设置为当前的事务号,相当于标记为删除而不是物理删除

#### INSERT
对于新插入的行,行的修改版本号更新为该事务的事务号

#### UPDATE
更新行的时候,InnoDB会把原来的行复制一份,并把当前的事务号作为改行的修改版本号
