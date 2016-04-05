category: ��������
date: 2015-06-08
title: mavenmaven�汾����
---

### �Զ����汾����
�Զ����汾����������ȷ�İ汾��. һ�����ǵİ汾�Ź���Ϊ`��Ҫ�汾��.��Ҫ�汾��.�����汾��-��̱��汾��`. ������pom�ļ��в�����ã�
```xml
<plugin>
	<groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-release-plugin</artifactId>
    <version>2.5.2</version>
    <configuration>
		<!-- �����Ŀ�ṹ���ǲ��ñ�׼��SVN����(ƽ�е�trunk/tags/branches),����Ҫ������������ -->
		<tagBase>https://svn.mycompany.com/repos/myapplication/tags</tagBase>
		<branchBase>https://svn.mycompany.com/repos/myapplication/branchs</branchBase>
    </configuration>
</plugin>
```
����������������ִ�����
```java
mvn release:clean
```
��ִ���������,Ȼ��ִ����������׼���汾������
```java
mvn release:prepare
```
������������в�����
* �����Ŀ�Ƿ���δ�ύ����
* �����Ŀ�Ƿ��п��հ汾����
* �����û������뽫���հ汾����Ϊ������
* ��POM�е�SCM��Ϣ����ΪTAG��ַ
* �����޸ĺ��POMִ��MAVEN����
* �ύPOM���
* �����û������뽫�����TAG
* ������ӷ���������Ϊ�µĿ��հ�
* �ύPOM���

��ǰ������ok֮��,�������ʾ�û������Ҫ�����İ汾��,TAG���ƺ��µĿ��հ汾��

���ǻ�����ִ�лع�:
```java
mvn release:rollback
```
�ع�`release:prepare`��ִ�еĲ���. ������Ҫע�������`release:prepare`�����д����TAG�����ᱻɾ��,��Ҫ�ֶ�ɾ��.

�������Ϳ���ִ�а汾�����ˣ�
```java
mvn release:perform
```
������`release:prepare`���ɵ�TAGԴ��,���ڴ˻�����ִ��`mvn deploy`,��������𵽲ֿ�.


```java
mvn release:stage
```

����һ�������Ĺ��ܣ����֧
```java
mvn release:branch
```
ͨ��maven���֧,ִ�����в���
* �����Ŀ�Ƿ���δ�ύ����
* Ϊ��֧����POM�İ汾,�����`1.1.00SNAPSHOT`�ı�Ϊ`1.1.1-SNAPSHOT`
* ��POM�е�SCM��Ϣ����Ϊ��֧��ַ
* �ύ���ϸ���
* �����ɴ������Ϊ��֧����
* �޸ı��ش���ʹ֮���˵�֮ǰ�İ汾(�û�����ָ���µİ汾)
* �ύ���ظ���


```java
mvn release:update-versions
```


## maven����
### ��������
* `${basedir}`: ��ʾ��Ŀ��Ŀ¼,������`pom.xml`�ļ���Ŀ¼
* `${version}`:��Ŀ�汾

### POM����
������������POM�ļ��ж�Ӧ��Ԫ��ֵ,���磺
* `${project.artifactId}`: ����`<project><artifactId>`ֵ
* `${project.build.sourceDirectory}`: ��Ŀ����Դ��Ŀ¼
* `${project.build.directory}`: ��Ŀ���������Ŀ¼

### �Զ�������
��`<Properties><property>`�ﶨ�������

### setting����
��POM����ͬ��,������`settings`��ͷ. ����������õ���`setting.xml`�ļ���XMLԪ�ص�ֵ.

### Javaϵͳ����
����JAVAϵͳ�е����Զ�����ʹ��Maven��������,ʹ��`mvn help:system`�鿴����Javaϵͳ����

### ������������
���л��������������Զ�����ʹ��`env`��ͷ����������,����`${env.JAVA_HOME}`


## Maven���

### �Զ���Manifest
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-jar-plugin</artifactId>
    <configuration>
        <archive>
            <manifestFile>
                src/main/resources/META-INF/MANIFEST.MF
            </manifestFile>
            <manifest>
                <addClasspath>true</addClasspath>
                <classpathPrefix>lib/</classpathPrefix>
                <mainClass>
                    wang.ming15.instrument.core.App
                </mainClass>
            </manifest>
        </archive>
    </configuration>
</plugin>
```
