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

# 用户操作


# 数据库操作 
一、用户创建、权限、删除

1、连接MySql操作
连接：mysql -h 主机地址 -u 用户名 －p 用户密码 （注:u与root可以不用加空格，其它也一样）
断开：exit （回车） 
打开cmd,输入
mysql -h 127.0.0.1 -u root -p 然后输入密码。就可以连接到本地的MySql数据库了。

2、 创建用户: 

命令:CREATE USER 'username'@'host' IDENTIFIED BY 'password'; 

说明:
username - 你将创建的用户名, 
host - 指定该用户在哪个主机上可以登陆,如果是本地用户可用localhost, 如果想让该用户可以从任意远程主机登陆,可以使用通配符%. 
password - 该用户的登陆密码,密码可以为空,如果为空则该用户可以不需要密码登陆服务器. 

例子: 
CREATE USER 'lin'@'localhost' IDENTIFIED BY '123456'; 
CREATE USER 'pig'@'192.168.1.101_' IDENDIFIED BY '123456'; 
CREATE USER 'pig'@'%' IDENTIFIED BY '123456'; 
CREATE USER 'pig'@'%' IDENTIFIED BY ''; 
CREATE USER 'pig'@'%'; 

登陆时,先把当前exit,再输入以下
           mysql -h 127.0.0.1 -u linlin -p 密码
            mysql -h 127.0.0.1 -u pig -p 密码
                                               
3、授权: 

命令:GRANT privileges ON databasename.tablename TO 'username'@'host' 

说明:
privileges - 用户的操作权限,如SELECT , INSERT , UPDATE 等(详细列表见该文最后面).如果要授予所的权限则使用ALL.;databasename - 数据库名,tablename-表名,如果要授予该用户对所有数据库和表的相应操作权限则可用*表示, 如*.*. 

例子: 
          GRANT SELECT, INSERT ON school.*   TO  'lin' @'%'; 
          GRANT ALL ON *.* TO 'pig'@'%'; 

注意:用以上命令授权的用户不能给其它用户授权,如果想让该用户可以授权,用以下命令: 
GRANT privileges ON databasename.tablename TO 'username'@'host' WITH GRANT OPTION; 


4、设置与更改用户密码 

命令:SET PASSWORD FOR 'username'@'host' = PASSWORD('newpassword');如果是当前登陆用户用SET PASSWORD = PASSWORD("newpassword"); 

例子: SET PASSWORD FOR 'lin'@'%' = PASSWORD("123456"); 

                                                                              
5、撤销用户权限 

命令: REVOKE privilege ON databasename.tablename FROM 'username'@'host'; 

说明: privilege, databasename, tablename - 同授权部分. 

例子: REVOKE SELECT ON *.* FROM 'pig'@'%'; 

注意: 假如你在给用户'pig'@'%'授权的时候是这样的(或类似的):GRANT SELECT ON test.user TO 'pig'@'%', 则在使用REVOKE SELECT ON *.* FROM 'pig'@'%';命令并不能撤销该用户对test数据库中user表的SELECT 操作.相反,如果授权使用的是GRANT SELECT ON *.* TO 'pig'@'%';则REVOKE SELECT ON test.user FROM 'pig'@'%';命令也不能撤销该用户对test数据库中user表的Select 权限. 

具体信息可以用命令SHOW GRANTS FOR 'pig'@'%'; 查看. 

6、删除用户 

命令: DROP USER 'username'@'host';

二、数据库与表显示、创建、删除


1、数据库显示、创建、删除

显示数据库：show databases; 
创建库：create database 库名; 
删除库：drop database 库名; 
使用库(选中库)：use 库名; 


2、表显示、创建、删除
显示数据表：show tables; （要先用use 数据库名选定数据库）
                                                                               
显示表结构：describe 表名;或者desc  表名
                                                             
创建表：create table 表名 (字段设定列表); 
[sql] view plaincopy
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
删除表：drop table 表名; 
句法：DROP DATABASE [IF EXISTS] db_name
功能：DROP DATABASE删除数据库中的所有表和数据库。要小心地使用这个命令！
DROP DATABASE返回从数据库目录被删除的文件的数目。通常，这3倍于表的数量，因为每张表对应于一个“.MYD”文件、一个“.MYI”文件和一个“.frm”文件。
在MySQL 3.22或以后版本中，你可以使用关键词IF EXISTS阻止一个错误的发生，如果数据库不存在。 

三、表复制及备份还原

假设现在有表books：

1.复制表结构
  1.1 含有主键等信息的完整表结构
  CREATE table 新表名 LIKE book;

  1.2 只有表结构，没有主键等信息
  create table 新表名 select * from books;
  或
  create table  新表名 as(select * from book);
  或
  create table 新表名 select * from books where1=2;

2.将旧表中的数据灌入新表
INSERT INTO 新表 SELECT * FROM 旧表；
注：新表必须已经存在

3.输入创建表的DDL语句
show create table 表名;

4.清空表数据
truncate table 表名;

5.备份数据库
比如备份library数据库
进去Mysql的bin目录
E:\mysql-5.6.23-win32\bin
利用“mysqldump  -u 用户名 -p 数据库名>备份名字”导出数据库到文件
C:\Program Files\MySQL\MySQL Server 5.5\bin>mysqldump -u root -p test >test.sql
Enter password: ***
即可.

E:\mysql-5.6.23-win32\bin目录下

6.还原数据库
还原test数据库为例
先create database test1
然后 下mysql>下
输入source 路径
即可。
要注意test.sql所在的路径！



 
四、数据库表中数据操作

1、清除mysql表中数据
delete from 表名;
truncate table 表名;
不带where参数的delete语句可以删除mysql表中所有内容，使用truncate table也可以清空mysql表中所有内容。
效率上truncate比delete快，但truncate删除后不记录mysql日志，不可以恢复数据。
delete的效果有点像将mysql表中所有记录一条一条删除到删完，
而truncate相当于保留mysql表的结构，重新创建了这个表，所有的状态都相当于新表。
2、删除表中的某些数据
delete from命令格式：delete from 表名 where 表达式
例如，删除表 MyClass中编号为1 的记录：
代码如下:
mysql> delete from MyClass where id=1;
 
五、修改表的列与表名

 
1、给列更名
>alter table 表名称 change 字段名称 字段名称
例如：
alter table pet change weight wei;
2、给表更名
>alter table 表名称 rename 表名称
例如：
alter table tbl_name rename new_tbl
3、修改某个表的字段类型及指定为空或非空
>alter table 表名称 change 字段名称字段名称 字段类型 [是否允许非空];
>alter table 表名称 modify 字段名称字段类型 [是否允许非空];
4、修改某个表的字段名称及指定为空或非空
>alter table 表名称 change 字段原名称字段新名称 字段类型 [是否允许非空];
例如:
修改表expert_info中的字段birth,允许其为空
代码如下:
>alter table expert_info change birth birth varchar(20) null;
 
 
六、修改表中的数据

1.增加一个字段(一列)
alter table table_name add column column_name type default value; type指该字段的类型,value指该字段的默认值
例如:
代码如下:
alter table mybook add column publish_house varchar(10) default ”;
2.更改一个字段名字(也可以改变类型和默认值)
alter table table_name change sorce_col_name dest_col_name type defaultvalue; source_col_name指原来的字段名称,dest_col_name
指改后的字段名称
例如:
代码如下:
alter table Board_Info change IsMobile IsTelphone int(3) unsigned default1;
3.改变一个字段的默认值
alter table table_name alter column_name set default value;
例如:
代码如下:
alter table book alter flag set default '0′;
4.改变一个字段的数据类型
alter table table_name change column column_name column_name type;
例如:
代码如下:
alter table userinfo change column username username varchar(20);
5.向一个表中增加一个列做为主键
alter table table_name add column column_name type auto_increment PRIMARYKEY;
例如:
代码如下:
alter table book add column id int(10) auto_increment PRIMARY KEY;
6.数据库某表的备份,在命令行中输入:
mysqldump -u root -p database_name table_name > bak_file_name
例如:
代码如下:
mysqldump -u root -p f_info user_info > user_info.dat
7.导出数据
select_statment into outfile”dest_file”;
例如:
代码如下:
select cooperatecode,createtime from publish limit 10 intooutfile”/home/mzc/temp/tempbad.txt”;
8.导入数据
load data infile”file_name” into table table_name;
例如:
代码如下:
load data infile”/home/mzc/temp/tempbad.txt” into table pad;
9.将两个表里的数据拼接后插入到另一个表里。下面的例子说明将t1表中的com2和t2表中的com1字段的值拼接后插入到tx表对应的
字段里。
例如:
代码如下:
insert into tx select t1.com1,concat(t1.com2,t2.com1) from t1,t2;
10,删除字段
alter table form1 drop column 列名;
 
七、查询表
mysql查询的五种子句
        where(条件查询)、having（筛选）、group by（分组）、order by（排序）、limit（限制结果数）
 
1、查询数值型数据:
 SELECT * FROM tb_name WHERE sum > 100;
 查询谓词:>,=,<,<>,!=,!>,!<,=>,=<
 
2、查询字符串
 SELECT * FROM tb_stu  WHERE sname  =  '小刘'
 SELECT * FROM tb_stu  WHERE sname like '刘%'
 SELECT * FROM tb_stu  WHERE sname like '%程序员'
 SELECT * FROM tb_stu  WHERE sname like '%PHP%'
 
3、查询日期型数据
 SELECT * FROM tb_stu WHERE date = '2011-04-08'
 注:不同数据库对日期型数据存在差异: ：
 (1)MySQL:SELECT * from tb_name WHERE birthday = '2011-04-08'
 (2)SQL Server:SELECT * from tb_name WHERE birthday = '2011-04-08'
 (3)Access:SELECT * from tb_name WHERE birthday = #2011-04-08#
 
4、查询逻辑型数据
 SELECT * FROM tb_name WHERE type = 'T'
 SELECT * FROM tb_name WHERE type = 'F'
 逻辑运算符:and or not
 
6、查询非空数据
 SELECT * FROM tb_name WHERE address <>'' order by addtime desc
 注:<>相当于PHP中的!=
 
6、利用变量查询数值型数据
 SELECT * FROM tb_name WHERE id = '$_POST[text]' 
注:利用变量查询数据时，传入SQL的变量不必用引号括起来，因为PHP中的字符串与数值型数据进行连接时，程序会自动将数值型数据转变成字符串，然后与要连接的字符串进行连接
 
7、利用变量查询字符串数据 
SELECT * FROM tb_name WHERE name LIKE '%$_POST[name]%' 
完全匹配的方法"%%"表示可以出现在任何位置
 
8、查询前n条记录
 SELECT * FROM tb_name LIMIT 0,$N;
 limit语句与其他语句，如order by等语句联合使用，会使用SQL语句千变万化，使程序非常灵活
 
9、查询后n条记录
 SELECT * FROM tb_stu ORDER BY id ASC LIMIT $n
 
10、查询从指定位置开始的n条记录
 SELECT * FROM tb_stu ORDER BY id ASC LIMIT $_POST[begin],$n
 注意:数据的id是从0开始的
 
11、查询统计结果中的前n条记录
 SELECT * ,(yw+sx+wy) AS total FROM tb_score ORDER BY (yw+sx+wy) DESC LIMIT 0,$num
 
12、查询指定时间段的数据
 SELECT  要查找的字段 FROM 表名 WHERE 字段名 BETWEEN 初始值 AND 终止值
 SELECT * FROM tb_stu WHERE age BETWEEN 0 AND 18
 
13、按月查询统计数据
 SELECT * FROM tb_stu WHERE month(date) = '$_POST[date]' ORDER BY date ;
 注：SQL语言中提供了如下函数，利用这些函数可以很方便地实现按年、月、日进行查询
 year(data):返回data表达式中的公元年分所对应的数值
 month(data):返回data表达式中的月分所对应的数值
 day(data):返回data表达式中的日期所对应的数值
 
14、查询大于指定条件的记录
 SELECT * FROM tb_stu WHERE age>$_POST[age] ORDER BY age;
 
15、查询结果不显示重复记录
 SELECT DISTINCT 字段名 FROM 表名 WHERE 查询条件 
注:SQL语句中的DISTINCT必须与WHERE子句联合使用，否则输出的信息不会有变化 ,且字段不能用*代替
 
16、NOT与谓词进行组合条件的查询
 (1)NOT BERWEEN … AND … 对介于起始值和终止值间的数据时行查询 可改成 <起始值 AND >终止值
 (2)IS NOT NULL 对非空值进行查询 
 (3)IS NULL 对空值进行查询
 (4)NOT IN 该式根据使用的关键字是包含在列表内还是排除在列表外，指定表达式的搜索，搜索表达式可以是常量或列名，而列名可以是一组常量，但更多情况下是子查询
 
17、显示数据表中重复的记录和记录条数
 SELECT  name,age,count(*) ,age FROM tb_stu WHERE age = '19' group by date
 
18、对数据进行降序/升序查询
 SELECT 字段名 FROM tb_stu WHERE 条件 ORDER BY 字段 DESC 降序
 SELECT 字段名 FROM tb_stu WHERE 条件 ORDER BY 字段 ASC  升序
 注:对字段进行排序时若不指定排序方式，则默认为ASC升序
 
19、对数据进行多条件查询
 SELECT 字段名 FROM tb_stu WHERE 条件 ORDER BY 字段1 ASC 字段2 DESC  …
 注意:对查询信息进行多条件排序是为了共同限制记录的输出，一般情况下，由于不是单一条件限制，所以在输出效果上有一些差别。
 
20、对统计结果进行排序
 函数SUM([ALL]字段名) 或 SUM([DISTINCT]字段名),可实现对字段的求和，函数中为ALL时为所有该字段所有记录求和,若为DISTINCT则为该字段所有不重复记录的字段求和
 如：SELECT name,SUM(price) AS sumprice  FROM tb_price GROUP BY name
 
SELECT * FROM tb_name ORDER BY mount DESC,price ASC
 
21、单列数据分组统计
 SELECT id,name,SUM(price) AS title,date FROM tb_price GROUP BY pid ORDER BY title DESC
 注:当分组语句group by排序语句order by同时出现在SQL语句中时，要将分组语句书写在排序语句的前面，否则会出现错误
 
22、多列数据分组统计
 多列数据分组统计与单列数据分组统计类似 
SELECT *，SUM(字段1*字段2) AS (新字段1) FROM 表名 GROUP BY 字段 ORDER BY 新字段1 DESC
 SELECT id,name,SUM(price*num) AS sumprice  FROM tb_price GROUP BY pid ORDER BY sumprice DESC
 注：group by语句后面一般为不是聚合函数的数列，即不是要分组的列
 
23、多表分组统计
 SELECT a.name,AVG(a.price),b.name,AVG(b.price) FROM tb_demo058 AS a,tb_demo058_1 AS b WHERE a.id=b.id GROUP BY b.type;