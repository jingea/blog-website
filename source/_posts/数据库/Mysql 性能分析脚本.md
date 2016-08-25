category: 数据库
date: 2016-08-24
title: Mysql 性能分析脚本
---
参考[mysql 性能优化方向](http://www.cnblogs.com/AloneSword/p/3207697.html)写了一个性能分析脚本

```python
#!/usr/bin/python
# -*- coding: utf-8 -*-

import sys
import mysql.connector

mysql_host = "192.168.15.25"
mysql_database = "test"
mysql_user = "root"
mysql_passwrold = "xpec@2015"

reload(sys)
sys.setdefaultencoding('utf-8')


def exec_sqls(exec_sql):
    cnx = mysql.connector.connect(user=mysql_user, password=mysql_passwrold, host=mysql_host, database=mysql_database)
    cursor = cnx.cursor()
    exec_sql(cursor)
    cnx.commit()
    cursor.close()
    cnx.close()


def print_2_blank(output):
    print "     " + str(output)


def print_3_blank(output):
    print "         " + str(output)


def exec_sql_query(cursor, sql):
    cursor.execute(sql)
    result = cursor.fetchall()
    print_2_blank(result)
    return result


def query_max_connections(cursor):
    print "连接数信息"

    max_connections = exec_sql_query(cursor, "show variables like 'max_connections'")
    max_connections_num = float(max_connections[0][1])
    print_2_blank("max_connections : " + str(max_connections_num))
    print ""

    max_used_connections = exec_sql_query(cursor, "show global status like 'max_used_connections'")
    max_used_connections_num = float(max_used_connections[0][1])
    print_2_blank("Max_used_connections : " + str(max_used_connections_num))
    print_2_blank("理想设置值(<=85) max_used_connections / max_connections = " + str(max_used_connections_num / max_connections_num))


def query_thread(cursor):
    print "\n线程信息查询"

    thread_cache_size = exec_sql_query(cursor, "show variables like 'thread_cache_size'")
    print_2_blank("配置的最大缓存线程数 : " + str(thread_cache_size[0][1]))
    print ""

    thread = exec_sql_query(cursor, "show global status like 'thread%'")
    print_2_blank("Threads_cached(缓存线程数) : "        + str(thread[0][1]))
    print_2_blank("Threads_connected(连接过的线程数) : "    + str(thread[1][1]))
    print_2_blank("Threads_created(创建过的线程数) : "    + str(thread[2][1]))
    print_2_blank("Threads_running(运行的线程数) : "      + str(thread[3][1]))
    print "\n"


def query_files_cache(cursor):
    print "文件打开数"
    open_files = exec_sql_query(cursor, "show global status like 'open_files'")

    open_files_num = float(open_files[0][1])
    print_2_blank("open_files : " + str(open_files_num))
    print ""

    open_files_limit = exec_sql_query(cursor, "show variables like 'open_files_limit'")
    open_files_limit_num = float(open_files_limit[0][1])
    print_2_blank("open_files_limit : " + str(open_files_limit_num))

    print ""
    batter_value = str(int((open_files_num / open_files_limit_num)))
    print_2_blank("理想设置值(<=75) open_files / open_files_limit = " + batter_value)
    print "\n"

def query_open_tables(cursor):
    print "数据库表信息"

    print_2_blank("表打开信息")
    open_tables = exec_sql_query(cursor, "show global status like 'open%tables%'")
    open_tables_num = float(open_tables[0][1])
    opened_tables_num = float(open_tables[1][1])
    print_2_blank("open_tables(打开表的数量) : " + str(open_tables_num))
    print_2_blank("opened_tables(打开过的表数量) : " + str(opened_tables_num))
    batter_value = str(int((open_tables_num / opened_tables_num) * 100))
    print_2_blank("比较合适的值(>=85) open_tables / opened_tables = " + batter_value)
    print ""

    if int(batter_value) < 85:
        table_cache = exec_sql_query(cursor, "show variables like 'table_open_cache'")
        table_cache_num = float(table_cache[0][1])
        print_2_blank("table_open_cache : " + str(table_cache_num))
        batter_value = str(int((open_tables_num / table_cache_num) * 100))
        print_2_blank("比较合适的值(<=95) open_tables / table_open_cache = " + batter_value)

    print_2_blank("\n     临时表信息")
    tmp_table_size = exec_sql_query(cursor, "show variables where variable_name in ('tmp_table_size', 'max_heap_table_size')")
    max_heap_table_size = float(tmp_table_size[0][1])
    tmp_table_size = float(tmp_table_size[1][1])
    print_2_blank("max_heap_table_size : " + str(max_heap_table_size / 1024 / 1024) + "Mb")
    print_2_blank("tmp_table_size : " + str(tmp_table_size / 1024 / 1024) + "Mb")
    print_3_blank("只有 256mb 以下的临时表才能全部放内存,超过的就会用到硬盘临时表")

    print_2_blank("\n      内存临时表信息")
    created_tmp = exec_sql_query(cursor, "show global status like 'created_tmp%'")
    Created_tmp_disk_tables = float(created_tmp[0][1])
    Created_tmp_files = float(created_tmp[1][1])
    Created_tmp_tables = float(created_tmp[2][1])
    print_2_blank("Created_tmp_disk_tables : " + str(Created_tmp_disk_tables))
    print_2_blank("Created_tmp_files : " + str(Created_tmp_files))
    print_2_blank("Created_tmp_tables : " + str(Created_tmp_tables))
    print_2_blank("比较合适的值(<=25) created_tmp_disk_tables / created_tmp_tables = " + str(int(Created_tmp_disk_tables / Created_tmp_tables)))


    print_2_blank("\n      表锁情况")
    created_tmp = exec_sql_query(cursor, "show global status like 'table_locks%'")
    table_locks_immediate = float(created_tmp[0][1])
    table_locks_waited = float(created_tmp[1][1])
    print_2_blank("table_locks_immediate (立即释放表锁数): " + str(table_locks_immediate))
    print_2_blank("table_locks_waited (需要等待的表锁数) : " + str(table_locks_waited))
    batter_value = int(table_locks_immediate / table_locks_waited)
    if batter_value >= 5000:
        print_2_blank("推荐采用innodb引擎,因为innodb是行锁而myisam是表锁,对于高并发写入的应用innodb效果会好些. " + str(batter_value))
    if batter_value <= 235:
        print_2_blank("table_locks_immediate / table_locks_waited ＝ 235,myisam就足够了 = " + str(batter_value))

    print_2_blank("\n      表扫描情况")
    handler_read = exec_sql_query(cursor, "show global status like 'handler_read%'")
    handler_read_first = float(handler_read[0][1])
    handler_read_key = float(handler_read[0][1])
    handler_read_next = float(handler_read[0][1])
    handler_read_prev = float(handler_read[0][1])
    handler_read_rnd = float(handler_read[0][1])
    handler_read_rnd_next= float(handler_read[1][1])
    print_2_blank("handler_read_first : " + str(handler_read_first))
    print_2_blank("handler_read_key : " + str(handler_read_key))
    print_2_blank("handler_read_next : " + str(handler_read_next))
    print_2_blank("handler_read_prev : " + str(handler_read_prev))
    print_2_blank("handler_read_rnd : " + str(handler_read_rnd))
    print_2_blank("handler_read_rnd_next : " + str(handler_read_rnd_next))

    print_2_blank("\n      服务器完成的查询请求次数")
    com_select = exec_sql_query(cursor, "show global status like 'com_select'")
    com_select_num = float(com_select[0][1])
    print_2_blank("服务器完成的查询请求次数 " + str(com_select_num))
    batter_value = int(handler_read_rnd_next / com_select_num)
    print_2_blank("表扫描率 handler_read_rnd_next / com_select = " + str(batter_value))
    if batter_value > 4000:
        print_2_blank("表扫描率超过 4000,说明进行了太多表扫描,很有可能索引没有建好,增加 read_buffer_size 值会有一些好处,但最好不要超过8mb")


def query_key_buffer_size(cursor):
    print "\nkey_buffer_size"

    key_buffer_size = exec_sql_query(cursor, "show variables like 'key_buffer_size'")
    key_buffer_size_num = float(key_buffer_size[0][1])
    print_2_blank("key_buffer_size : " + str(key_buffer_size_num))

    key_read = exec_sql_query(cursor, "show global status like 'key_read%'")
    key_read_requests = float(key_read[0][1])
    key_reads = float(key_read[1][1])
    print_2_blank("key_read_requests(索引读取请求次数) : " + str(key_read_requests))
    print_2_blank("key_reads(索引从内存查询失败在硬盘读取次数) : " + str(key_reads))
    print_2_blank("索引缓存未命中概率 key_reads / key_read_requests = " + str(key_reads / key_read_requests))

    # 比如上面的数据,key_cache_miss_rate为0.0244%,4000个索引读取请求才有一个直接读硬盘,已经很bt了,
    # key_cache_miss_rate在0.1%以下都很好(每1000个请求有一个直接读硬盘),如果key_cache_miss_rate在0.01%以下的话,key_buffer_size分配的过多,可以适当减少.
    #【注意】key_read_buffer 默认值为 8M .在专有的数据库服务器上,该值可设置为 RAM * 1/4
    print ""
    key_blocks_u = exec_sql_query(cursor, "show global status like 'key_blocks_u%'")
    key_blocks_unused = float(key_blocks_u[0][1])
    key_blocks_used = float(key_blocks_u[1][1])
    print_2_blank("未使用的缓存簇(blocks)数 : " + str(key_blocks_unused))
    print_2_blank("曾经用到的最大的blocks数 : " + str(key_blocks_used))
    batter_value = int(key_blocks_used / (key_blocks_unused + key_blocks_used))
    # 比如这台服务器,所有的缓存都用到了,要么增加 key_buffer_size,要么就是过渡索引了,把缓存占满了
    print_2_blank("比较理想的设置(80)：key_blocks_used / (key_blocks_unused + key_blocks_used) ->" + str(batter_value))
    print "\n"


def query_sort(cursor):
    print "排序使用情况"
    sort = exec_sql_query(cursor, "show global status like 'sort%'")
    sort_merge_passes = float(sort[0][1])
    sort_range = float(sort[1][1])
    sort_rows = float(sort[2][1])
    sort_scan = float(sort[3][1])
    print_2_blank("sort_merge_passes : " + str(sort_merge_passes))
    print_2_blank("sort_range : " + str(sort_range))
    print_2_blank("sort_rows : " + str(sort_rows))
    print_2_blank("sort_scan : " + str(sort_scan))


def query_cache(cursor):
    print "\n缓存查询"

    qcache = exec_sql_query(cursor, "show global status like 'qcache%'")
    qcache_free_blocks =    float(qcache[0][1])
    qcache_free_memory =    float(qcache[1][1])
    qcache_hits =           float(qcache[2][1])
    qcache_inserts =        float(qcache[3][1])
    qcache_lowmem_prunes =  float(qcache[4][1])
    qcache_not_cached =     float(qcache[5][1])
    qcache_queries_in_cache = float(qcache[6][1])
    qcache_total_blocks =   float(qcache[7][1])
    # 数目大说明可能有碎片.flush query cache会对缓存中的碎片进行整理,从而得到一个空闲块
    print_2_blank("qcache_free_blocks(缓存中相邻内存块的个数) : " + str(qcache_free_blocks))
    print_2_blank("qcache_free_memory(缓存中的空闲内存) : " + str(qcache_free_memory))
    print_2_blank("qcache_hits(每次查询在缓存中命中时就增大) : " + str(qcache_hits))
    # 每次插入一个查询时就增大.命中次数除以插入次数就是命中比率
    print_2_blank("qcache_inserts(命中次数) : " + str(qcache_inserts))
    # 缓存出现内存不足并且必须要进行清理以便为更多查询提供空间的次数.这个数字最好长时间来看；如果这个数字在不断增长,就表示可能碎片非常严重,或者内存很少.(上面的 free_blocks和free_memory可以告诉您属于哪种情况)
    print_2_blank("qcache_lowmem_prunes : " + str(qcache_lowmem_prunes))
    print_2_blank("qcache_not_cached(不适合进行缓存的查询的数量) : " + str(qcache_not_cached))
    print_2_blank("qcache_queries_in_cache(当前缓存的查询(和响应)的数量) : " + str(qcache_queries_in_cache))
    print_2_blank("qcache_total_blocks(缓存中块的数量) : " + str(qcache_total_blocks))

    print ""
    query_cache = exec_sql_query(cursor, "show variables like 'query_cache%'")
    query_cache_limit =         float(query_cache[0][1])
    query_cache_min_res_unit =  float(query_cache[1][1])
    query_cache_size =          float(query_cache[2][1])
    query_cache_type =          str(query_cache[3][1])
    query_cache_wlock_invalidate = str(query_cache[4][1])
    print_2_blank("query_cache_limit(超过此大小的查询将不缓存) : " + str(query_cache_limit))
    print_2_blank("query_cache_min_res_unit(缓存块的最小大小) : " + str(query_cache_min_res_unit))
    print_2_blank("query_cache_size(查询缓存大小) : " + str(query_cache_size))
    print_2_blank("query_cache_type(缓存类型,决定缓存什么样的查询) : " + str(query_cache_type))
    print_2_blank("query_cache_wlock_invalidate : " + str(query_cache_wlock_invalidate))
    print_2_blank("query_cache_wlock_invalidate：当有其他客户端正在对myisam表进行写操作时,如果查询在query cache中,是否返回cache结果还是等写操作完成再读表获取结果.")
    batter_value = int(qcache_free_blocks / qcache_total_blocks)
    print_2_blank("查询缓存碎片率 qcache_free_blocks / qcache_total_blocks = " + str(batter_value))
    if batter_value > 20:
        print_2_blank("查询缓存碎片率超过20%,可以用flush query cache整理缓存碎片,或者试试减小query_cache_min_res_unit,如果你的查询都是小数据量的话. ")

    batter_value = int((query_cache_size - qcache_free_memory) / query_cache_size)
    print_2_blank("查询缓存利用率 (query_cache_size - qcache_free_memory) / query_cache_size = " + str(batter_value))
    if batter_value < 25:
        print_2_blank("查询缓存利用率在25%以下, query_cache_size设置的过大,可适当减小；查询缓存利用率在80％以上而且qcache_lowmem_prunes > 50的话说明query_cache_size可能有点小,要不就是碎片太多.")
    if qcache_hits > 0:
        batter_value = int((qcache_hits - qcache_inserts) / qcache_hits)
        print_2_blank("查询缓存命中率 (qcache_hits - qcache_inserts) / qcache_hits = " + str(batter_value))
    print_2_blank("例如 查询缓存碎片率 ＝ 20.46％,查询缓存利用率 ＝ 62.26％,查询缓存命中率 ＝ 1.94％,命中率很差,可能写操作比较频繁吧,而且可能有些碎片.")

def query_read_buffer_size():
    pass


def query_slow(cursor):
    print "\n慢查询"
    variables_slow = exec_sql_query(cursor, "show variables like '%slow%'")
    log_slow_admin_statements = str(variables_slow[0][1])
    log_slow_slave_statements = str(variables_slow[1][1])
    slow_launch_time = float(variables_slow[2][1])
    slow_query_log = str(variables_slow[3][1])
    slow_query_log_file = str(variables_slow[4][1])
    print_2_blank("log_slow_admin_statements : " + str(log_slow_admin_statements))
    print_2_blank("log_slow_slave_statements : " + str(log_slow_slave_statements))
    print_2_blank("slow_launch_time : " + str(slow_launch_time))
    print_2_blank("slow_query_log : " + str(slow_query_log))
    print_2_blank("slow_query_log_file : " + str(slow_query_log_file))

    slow = exec_sql_query(cursor, "show global status like '%slow%'")
    slow_launch_threads = float(slow[0][1])
    slow_queries = float(slow[1][1])
    print_2_blank("slow_launch_threads : " + str(slow_launch_threads))
    print_2_blank("slow_queries : " + str(slow_queries))


exec_sqls(query_max_connections)
exec_sqls(query_thread)
exec_sqls(query_files_cache)
exec_sqls(query_open_tables)
exec_sqls(query_key_buffer_size)
exec_sqls(query_sort)
exec_sqls(query_cache)
exec_sqls(query_slow)


```