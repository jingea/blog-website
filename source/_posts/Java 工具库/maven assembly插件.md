category: Java工具
date: 2016-04-13
title: Maven Assembly 插件
---
学习自[assembly插件官方教程](http://maven.apache.org/plugins/maven-assembly-plugin/)
## 目标
目前assembly插件只有`assembly:single`可用, 其他的都已经被废弃了.
```xml
mvn assembly:single
```

## jar包
下面我们将assembly插件绑定到package阶段, 打出一个可运行jar包,同时该jar包里包含了所有的依赖类.
```xml
<build>
    <pluginManagement>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-assembly-plugin</artifactId>
                <version>2.6</version>
            </plugin>
        </plugins>
    </pluginManagement>
    <plugins>
        <!-- 引用maven-assembly-plugin插件 -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-assembly-plugin</artifactId>
            <version>2.6</version>
            <configuration>
                <!-- 引用jar-with-dependencies, 在打包时将依赖包也打包进jar包里 -->
                <descriptorRefs>
                    <descriptorRef>jar-with-dependencies</descriptorRef>
                </descriptorRefs>
                <!-- 创建一个可执行jar包, 我们在此处指定main class-->
                <archive>
                    <manifest>
                        <mainClass>org.sample.App</mainClass>
                    </manifest>
                </archive>
            </configuration>
            <executions>
                <!-- 我们将assembly:single目标绑定到package阶段, 那么当我们运行mvn package时, 就会执行该目标 -->
                <execution>
                    <id>make-assembly</id> <!-- this is used for inheritance merges -->
                    <phase>package</phase> <!-- bind to the packaging phase -->
                    <goals>
                        <goal>single</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

## 过滤文件
我们可以自己实现一个descriptor来实现文件过滤功能
```xml
<assembly xmlns="http://maven.apache.org/plugins/maven-assembly-plugin/assembly/1.1.3"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/plugins/maven-assembly-plugin/assembly/1.1.3 http://maven.apache.org/xsd/assembly-1.1.3.xsd">
  <id>distribution</id>
  <formats>
	<!-- 设置打包的最终文件格式 -->
    <format>jar</format>
  </formats>
  <fileSets>
    <fileSet>
      <directory>${basedir}</directory>
      <includes>
        <include>*.txt</include>
      </includes>
      <excludes>
        <exclude>README.txt</exclude>
        <exclude>NOTICE.txt</exclude>
      </excludes>
    </fileSet>
  </fileSets>
  <files>
    <file>
      <source>README.txt</source>
      <outputDirectory>/</outputDirectory>
      <filtered>true</filtered>
    </file>
    <file>
      <source>NOTICE.txt</source>
      <outputDirectory>/</outputDirectory>
      <filtered>true</filtered>
    </file>
  </files>
</assembly>
```
* fileSets用于过滤文件夹中的文件
* files用于过滤单个文件
然后我们在pom文件中使用
```xml
<plugin>
  <artifactId>maven-assembly-plugin</artifactId>
  <version>2.6</version>
  <configuration>
    <filters>
      <filter>src/assembly/filter.properties</filter>
    </filters>
    <descriptors>
      <descriptor>src/assembly/distribution.xml</descriptor>
    </descriptors>
  </configuration>
</plugin>
```
filter.properties文件内容如下
```xml
# lines beginning with the # sign are comments

variable1=value1
variable2=value2
```

## 过滤依赖
在pom文件中我们可能会有很多依赖, 但是打出的包, 我们可能并不需要这些依赖, 那么我们 可以在descriptor文件中将其过滤掉
```
<dependencySets>
  <dependencySet>
    <excludes>
      <exclude>commons-lang:commons-lang</exclude>
      <exclude>log4j:log4j</exclude>
    </excludes>
  </dependencySet>
</dependencySets>
```

## 打包子模块
有的时候我们的工程会有一些子工程, 那么在打包的时候, 我们也希望将子工程也打包进来. 例如我们有一个这样的工程pom文件
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>org.test</groupId>
  <artifactId>parent</artifactId>
  <version>1.0</version>

  <packaging>pom</packaging>

  <name>Parent</name>

  <modules>
    <module>child1</module>
    <module>child2</module>
    <module>child3</module>
  </modules>

  <build>
    <plugins>
      <plugin>
        <artifactId>maven-assembly-plugin</artifactId>
        <version>2.6</version>
        <configuration>
          <descriptors>
            <descriptor>src/assembly/src.xml</descriptor>
          </descriptors>
        </configuration>
      </plugin>
    </plugins>
  </build>
</project>
```
src.xml如下
```xml
<assembly xmlns="http://maven.apache.org/plugins/maven-assembly-plugin/assembly/1.1.3"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/plugins/maven-assembly-plugin/assembly/1.1.3 http://maven.apache.org/xsd/assembly-1.1.3.xsd">
  <id>src</id>
  <formats>
    <format>dir</format>
  </formats>
  <includeBaseDirectory>false</includeBaseDirectory>
  <moduleSets>
    <moduleSet>
      <includes>
        <include>org.test:child1</include>
      </includes>
      <sources>
        <outputDirectory>sources/${artifactId}</outputDirectory>
      </sources>
    </moduleSet>
  </moduleSets>
</assembly>
```
执行命令`mvn clean assembly:directory`打包的结果为
```xml
target/parent-1.0-src/
`-- sources
    `-- child1
        |-- pom.xml
        `-- src
            |-- main
            |   `-- java
            |       `-- org
            |           `-- test
            |               `-- App.java
            `-- test
                `-- java
                    `-- org
                        `-- test
                            `-- AppTest.java
```
