category: ��������
date: 2015-06-08
title: maven���Ŀ��
---

������������Ŀ��,ÿ��Ŀ�궼��Ӧ���ض��Ĺ���,Ҳ����˵�����Ĺ�����ͨ��Ŀ����ʵ����.

����`maven-compiler-plugin`��`compile`Ŀ���д��Ϊ`compiler:compile`.

### �����
���ǿ��Խ������Ŀ�����������ڵĽ׶����.

default�������������ò���󶨹�ϵ����������:

�������ڽ׶�                  | ���Ŀ��                              |ִ������
-----------------------------|--------------------------------------|--------------
process-resources            |maven-resources-plugin:resources      |��������Դ�ļ��������Ŀ¼
compile                      |maven-compile-plugin:compile	        |�����������������Ŀ¼
process-test-resources       |maven-resources-plugin:testRresources |���Ʋ�����Դ�ļ����������Ŀ¼
test-compile                 |maven-compiler-plugin:testCompile     |������Դ������������Ŀ¼
test	                     |maven-surefire-plugin:test            |ִ�в�������
package	                     |maven-jar-plugin:jar                  |������Ŀjar��
install	                     |maven-install-plugin:install          |����Ŀ���������װ�����زֿ�
deploy                       |maven-deploy-plugin:deploy            |����Ŀ�����������Զ�ֿ̲�


�������Զ���󶨣�
```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.codehaus.mojo</groupId>
            <artifactId>exec-maven-plugin</artifactId>
            <version>1.1.1</version>
            <executions>
                <execution>
                    <phase>install</phase>
                    <goals>
                        <goal>java</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```
������`install`�׶ΰ���`exec-maven-plugin`�����`java`Ŀ��.

## ��������������
��������Ĳ��, ������ʱ�����������Ҳ�������targetĿ¼��
```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-dependency-plugin</artifactId>
            <executions>
                <execution>
                    <id>copy</id>
                    <phase>package</phase>
                    <goals>
                        <goal>copy-dependencies</goal>
                    </goals>
                    <configuration>
                        <outputDirectory>
                            ${project.build.directory}
                        </outputDirectory>
                    </configuration>
                </execution>
            </executions>
        </plugin>

    </plugins>
</build>
```

## �������jar��
