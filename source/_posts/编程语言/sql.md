category: 编程语言
date: 2015-10-08
title: SQL
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

## ACID
事务规范了数据操作的语义,每个事务使得数据库从一个状态原子地转移到另外一个状态.

数据库事务具有
* 原子性
* 一致性
* 隔离性
* 持久性

### 原子性
原子性体现在对事务的修改,要么全部执行要么都不执行

### 一致性
保持数据的一致性,例如整数类型的数据大小不能溢出,字符型数据长度不能超过规定范围.

### 隔离性
如果数据库并发执行A,B俩个事务,那么在A事务执行完之前对B事务是不可见的,也就是说,B事务是看不见A事务的中间状态的.

### 持久性
事务完成后,它对数据库的影响是永久的,即使数据库出现异常也是如此.

## 隔离级别
* Read Uncommitted: 读取未提交的数据,即其他事务已经提交修改但还未提交的数据(这是最低的隔离级别)
* Read Committed:读取已经提交的数据,但是在一个事务中,对同一个项,前后俩次读取的结果可能不同.
* Repetable Read: 可重复读取,在一个事务中,对同一个项,确保前后俩次读取的结果一样
* Serializable: 可序列话,即数据库的事务是可串行执行的,就像一个事务执行的时候没有别的事务同时在执行
* 

## 读写异常
* Lost Update: 俩个事务并发修改同一个数据,A事务提交成功,B事务提交失败回滚后,A事务的修改都可能会丢失
* Dirty Reads: A事务读取了B事务更新却没有提交的数据
* Non-Repeatable Reads: 一个事务对同一个数据项的多次读取可能得到不同的结果
* Second Lost Updates:俩个事务并发修改同一个数据, B事务可能会覆盖掉A事务的修改
* Phantom Reads: A事务进行前后俩次查询,但是在查询过程中出现了B事务向其中插入数据,那么A事务可能读取到未出现的数据

## 隔离级别与读写异常的关系
```
    LU  DR  NRR  SLU  PR
RU  N   Y   Y    Y    Y
RC  N   N   Y    Y    Y
RR  N   N   N   N     Y
S   N   N   N   N     N
```
