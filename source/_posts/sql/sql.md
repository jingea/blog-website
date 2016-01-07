category: sql
date: 2015-10-08
title: 数据库和SQL
---
## 连接数据库
```
mysql -h 主机地址 -u 用户名 －p 用户密码 （注:u与root可以不用加空格，其它也一样）
```

## 用户操作
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

## 数据库操作 
* 显示数据库：`SHOW databases`; 
* 创建库：`CREATE DATABASE 库名`; 
* 删除库：`DROP DATABASE 库名`; 
* 使用库(选中库)：`USE 库名`; 

## 表操作
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

### 表数据操作
* 清空表数据`truncate table 表名;`. truncate删除后不记录mysql日志，不可以恢复数据。相当于保留mysql表的结构，重新创建了这个表，所有的状态都相当于新表。
* 清空表数据`delete from 表名`. delete的效果有点像将mysql表中所有记录一条一条删除到删完

### 修改表结构
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


### 复制表
* 含有主键等信息的完整表结构 `CREATE table 新表名 LIKE book;`
* 只有表结构，没有主键等信息 `create table 新表名 select * from books;
* 将旧表中的数据灌入新表 `INSERT INTO 新表 SELECT * FROM 旧表；` 注：新表必须已经存在

### 导入导出数据库
* 数据库某表的备份,在命令行中输入:`mysqldump -u root -p database_name table_name > bak_file_name`
* 导出数据`select_statment into outfile”dest_file”;`
* 导入数据`load data infile”file_name” into table table_name;`
* 将两个表里的数据拼接后插入到另一个表里`insert into tx select t1.com1,concat(t1.com2,t2.com1) from t1,t2;`

### 查询表
mysql查询的五种子句 

#### where(条件查询)
```sql
SELECT * FROM t1 WHERE id > 100;
```
* 数值谓词:`>,=,<,<>,!=,!>,!<,=>,=<`
* 字符串谓词：`=，like`
* 日期谓词：`=` (`SELECT * from t1 WHERE create_time = '2011-04-08'`)


#### having（筛选）
```sql

```

#### group by（分组）
```sql
SELECT id FROM player GROUP BY vip;
```

#### order by（排序）
```sql
SELECT id FROM player ORDER BY id;
```

#### limit（限制结果数）
查询前n条记录(默认从第0个开始)
```sql
SELECT id FROM player LIMIT 10;
```
从结果集中第1个开始查询, 查询10个
```sql
SELECT id FROM player LIMIT 1, 10;
```

## 执行顺序
sql语句的执行顺序
```sql
(7)     SELECT 
(8)     DISTINCT <select_list>
(1)     FROM <left_table>
(3)     <join_type> JOIN <right_table>
(2)     ON <join_condition>
(4)     WHERE <where_condition>
(5)     GROUP BY <group_by_list>
(6)     HAVING <having_condition>
(9)     ORDER BY <order_by_condition>
(10)    LIMIT <limit_number>
```
也就是
```
FROM->ON->JOIN->WHERE->GROUP BY->HAVING->SELECT->DISTINCT->ORDER BY->LIMIT
```