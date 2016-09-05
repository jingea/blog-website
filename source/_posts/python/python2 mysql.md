category: python2
date: 2016-09-05
title: PYTHON2 Mysql
---
```python
#!/usr/bin/python
# -*- coding: utf-8 -*-

import sys
import mysql.connector

mysql_host = "localhost"
mysql_database = "test"
mysql_user = "root"
mysql_passwrold = "root"

reload(sys)
sys.setdefaultencoding('utf-8')


def exec_sqls(exec_sql):
    cnx = mysql.connector.connect(user=mysql_user, password=mysql_passwrold, host=mysql_host, database=mysql_database)
    cursor = cnx.cursor()
    exec_sql(cursor)
    cnx.commit()
    cursor.close()
    cnx.close()

def query_max_connections(cursor):
    print "连接数信息"

    max_connections = exec_sql_query(cursor, "show variables like 'max_connections'")
    max_connections_num = float(max_connections[0][1])
    print("max_connections : " + str(max_connections_num))
	
exec_sqls(query_max_connections)
```
