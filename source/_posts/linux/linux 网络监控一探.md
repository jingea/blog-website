������������ܻ�����, �����ڴ��cpu֮��, ��Ҫ��һ��2500�������˵��������, ��zabixһ��, �Ҳ��� Outgoing network traffic on eth0 ��������Ȼ�Ǵ� 20M~60M֮�両��, �ⲻ��ѧ��. ���Ǻ���һ��������������������������, ���Ǳ����ҵ�һ������Խ��̻����Զ˿�Ϊ��Ԫ�ļ�ع���. 

�ڰٶ���������һ��, ��Ȼ����ô��������ع��� (�ο�[һЩ����ܲ�֪����Linux���繤��](http://www.linuxdiyf.com/linux/12131.html))
* nethogs
* ntopng
* nload
* iftop
* iptraf
* bmon
* slurm
* tcptrack
* cbm
* netwatch
* collectl
* trafshow
* cacti
* etherape
* ipband
* jnettop
* netspeed 
* speedometer
����Ϳ�ʼ���ҵ�̽��֮��

## iftop
�����ҵ�����������, ��˵������ʵ���ҵ�Ҫ��, ���ǿ�ʼ��װ

����ִ������������а�װ
```
yum install flex byacc  libpcap ncurses ncurses-devel libpcap-devel 
```
ok, ������װ���,������`yum install iftop`ʱ��ʾ
```
[root@~]# yum install iftop
Loaded plugins: fastestmirror, refresh-packagekit, security
Loading mirror speeds from cached hostfile
 * base: mirrors.tuna.tsinghua.edu.cn
 * epel: mirrors.neusoft.edu.cn
 * extras: mirrors.tuna.tsinghua.edu.cn
 * updates: mirrors.tuna.tsinghua.edu.cn
Setting up Install Process
Resolving Dependencies
--> Running transaction check
---> Package iftop.x86_64 0:1.0-0.7.pre4.el5 will be installed
--> Processing Dependency: libpcap.so.0.9.4()(64bit) for package: iftop-1.0-0.7.pre4.el5.x86_64
--> Finished Dependency Resolution
Error: Package: iftop-1.0-0.7.pre4.el5.x86_64 (epel)
           Requires: libpcap.so.0.9.4()(64bit)
 You could try using --skip-broken to work around the problem
 You could try running: rpm -Va --nofiles --nodigest
```
��libpcap�����������ǰ�װ�İ汾��һ��, �����ҳ������ذ�װ���ֶ���װ����
```
wget http://www.ex-parrot.com/~pdw/iftop/download/iftop-0.17.tar.gz
cd iftop-0.17
./configure
make && make install
```
��װ���, ������û�а�װ�ɹ�
```
iftop
```
ok�ɹ�����.


����������ʾ�������ƿ̶ȳߵĿ̶ȷ�Χ��Ϊ��ʾ����ͼ�εĳ���������õġ��м��<= =>���������Ҽ�ͷ����ʾ���������ķ���

* TX����������
* RX����������
* TOTAL��������
* Cumm������iftop��Ŀǰʱ���������
* peak��������ֵ
* rates���ֱ��ʾ��ȥ 2s 10s 40s ��ƽ������

�����������ÿ���ͻ������ӵ�������������Ϊ��λ������ʾ��. ��Ȼ�ﲻ��Ҫ�Ա���Ϊ��λ��ʾ�˿ںͽ��̵Ĵ��������, ����Ҳ���������.

> ��������������������,��ҿ��԰ٶ�һ��

## iptraf
�������ҵ���iptraf������, ��һ������ncurses��IP��������������������ɰ���TCP��Ϣ��UDP������ICMP��OSPF��Ϣ����̫��������Ϣ���ڵ�״̬��Ϣ��IPУ��ʹ���ȵ�ͳ�����ݡ�


��������װ�ܼ�, ����Ҫ��װʲô����
```
yum install iptraf
```
�򿪿���һ��, ������������ǻ�������, ��IP���м���. ��Ȼ��Staticstics breakdowns�Ͽ��Կ����˿���Ϣ, ���ǲ�֪��Ϊʲôû�п���Ӧ�ó���Ķ˿�. û�취���Űٶ�, ��������ƪ�������ҵ��˴�[Linux��iptraf�������](http://blog.csdn.net/quiet_girl/article/details/50777210)ԭ����Configure���Additional ports��������Ҫ�����Ķ˿�, Ĭ��ֻ����1000���µ�. 

�������Ժ�, �ٴν���Staticstics breakdowns���Կ�������Ҫ��صĶ˿���, ���Ǽ��ĳ����Ļ�������û�������趨�Ǹ���Χ��Ķ˿ڡ� �ţ����о���һ��,�����ڶ˿��������ǿ���ѡ�����˿ڵ�. ֻҪ���ö���˿ھͿ�����. ���ǻ��и�����, ����ͳ�Ƶ����������ܼƵ�,û��ʵʱ����ƽ��ֵ.

## nethogs
nethogs ����ݽ��������з���(ok,��һ��Ҫ��ﵽ��), ���Ҵ�֮��Ҳ����ʾ�ý��̵�ʵʱ����.
```bash
[root@~]# nethogs -p eth0

NetHogs version 0.8.0

PID USER     PROGRAM                                                                        DEV       SEN         RECEIVED       
23095 root   java                                                                           eth0      421.746     138.566 KB/sec
10867 root     sshd: root@pts/1                                                             eth0      1.825       0.059 KB/sec
?     root     192.168.15.25:10050-192.168.15.12:48691                                      		  0.000       0.014 KB/sec
?     root     192.168.15.25:19001-192.168.10.220:26680                                               0.000       0.000 KB/sec
?     root     192.168.15.25:10050-192.168.15.12:48608                                                0.000       0.000 KB/sec
?     root     192.168.15.25:10050-192.168.15.12:48096                                                0.000       0.000 KB/sec
?     root     192.168.15.25:10050-192.168.15.12:48082                                                0.000       0.000 KB/sec
23182 root     java                                                                        eth0       0.000       0.000 KB/sec
?     root     unknown TCP                                                                            0.000       0.000 KB/sec

TOTAL                                                                                                 423.571     138.639 KB/sec 
```
����, nethogs������������ǵ�����

## netstat


## vnStat
vnStat��һ�����ڿ���̨������������ع��ߣ���ΪLinux��BSD��Ƶġ������Ա���ĳ��������ѡ�������ӿڵ�����������־��Ϊ��������־��vnStatʹ���ں��ṩ����Ϣ�����仰˵����������̽����������ȷ����������ϵͳ��Դ��
```bash
[root@ ~]# vnstat --help
 vnStat 1.6 by Teemu Toivola <tst at iki dot fi>

         -q,  --query          query database
         -h,  --hours          show hours
         -d,  --days           show days
         -m,  --months         show months
         -w,  --weeks          show weeks
         -t,  --top10          show top10
         -s,  --short          use short output
         -u,  --update         update database
         -i,  --iface          select interface (default: eth0)
         -?,  --help           short help
         -v,  --version        show version
         -tr, --traffic        calculate traffic
         -l,  --live           ʵʱ��ʾ��������

See also "--longhelp" for complete options list and "man vnstat".
```
����
```bash
[root@ ~]# vnstat 
Database updated: Tue Aug  2 14:08:15 2016

        eth0

           received:     686.90 MB (22.6%)
        transmitted:       2.30 GB (77.4%)
              total:       2.97 GB

                        rx     |     tx     |  total
        -----------------------+------------+-----------
            today    686.90 MB |    2.30 GB |    2.97 GB
        -----------------------+------------+-----------
        estimated      1.14 GB |    3.91 GB |    5.04 GB
[root@ ~]# vnstat -l -u
Monitoring eth0...    (press CTRL-C to stop)

   rx:     791.08 kB/s 11969 p/s            tx:    2482.06 kB/s 21979 p/s^C


 eth0  /  traffic statistics

                             rx       |       tx
--------------------------------------+----------------------------------------
  bytes                      7.27 MB  |      22.35 MB
--------------------------------------+----------------------------------------
          max            894.73 kB/s  |     2.60 MB/s
      average            620.59 kB/s  |     1.86 MB/s
          min            599.12 kB/s  |     1.82 MB/s
--------------------------------------+----------------------------------------
  packets                     111771  |        200661
--------------------------------------+----------------------------------------
          max              13430 p/s  |     22767 p/s
      average               9314 p/s  |     16721 p/s
          min               9019 p/s  |     16711 p/s
--------------------------------------+----------------------------------------
  time                    12 seconds

[root@ ~]# vnstat 
Database updated: Tue Aug  2 14:08:56 2016

        eth0

           received:     712.66 MB (22.6%)
        transmitted:       2.38 GB (77.4%)
              total:       3.07 GB

                        rx     |     tx     |  total
        -----------------------+------------+-----------
            today    712.66 MB |    2.38 GB |    3.07 GB
        -----------------------+------------+-----------
        estimated      1.18 GB |    4.03 GB |    5.21 GB
[root@ ~]#  
```
���ǿ���ֻ��ʹ����`-u`���������ݸ��µ����ݿ�֮��, �����ٴ�ʹ��vnstatʱ��������ʾ����. ���cnstat������һ����ʷ��¼����

## tcpdump
�������ϵ����ݰ����нػ�İ���������. ��Ȼ�������, ���ǽ�������������, ���ǻ����������¼һ��, ��һ�õõ���

> ���⻹��tcpflow����, ��tcpdump��ͬ������������Ϊ��λ��ʾ�������ݣ���cpdump�԰�Ϊ��λ��ʾ���ݡ�