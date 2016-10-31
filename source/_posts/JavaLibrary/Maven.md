category: Java Library
date: 2015-06-07
title: Maven 随笔
---
## 版本管理
自动化版本发布基于正确的版本号. 一般我们的版本号构成为`主要版本号.次要版本号.增量版本号-里程碑版本号`. 下面是pom文件中插件设置：
```xml
<plugin>
	<groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-release-plugin</artifactId>
    <version>2.5.2</version>
    <configuration>
		<!-- 如果项目结构不是采用标准的SVN布局(平行的trunk/tags/branches),则需要配置下面俩项 -->
		<tagBase>https://svn.mycompany.com/repos/myapplication/tags</tagBase>
		<branchBase>https://svn.mycompany.com/repos/myapplication/branchs</branchBase>
    </configuration>
</plugin>
```
下来我们在命令行执行命令：
```java
mvn release:clean
```
先执行清除操作,然后执行下列命令准备版本发布：
```java
mvn release:prepare
```
该命令包含下列操作：
* 检查项目是否有未提交代码
* 检查项目是否有快照版本依赖
* 根据用户的输入将快照版本升级为发布版
* 将POM中的SCM信息更新为TAG地址
* 基于修改后的POM执行MAVEN构建
* 提交POM变更
* 基于用户的输入将代码打TAG
* 将代码从发布版升级为新的快照版
* 提交POM变更

当前俩项检查ok之后,插件会提示用户输出想要发布的版本号,TAG名称和新的快照版本号

我们还可以执行回滚:
```java
mvn release:rollback
```
回滚`release:prepare`所执行的操作. 但是需要注意的是在`release:prepare`步骤中打出的TAG并不会被删除,需要手动删除.

接下来就可以执行版本发布了：
```java
mvn release:perform
```
它会检出`release:prepare`生成的TAG源码,并在此基础上执行`mvn deploy`,打包并部署到仓库.


```java
mvn release:stage
```

还有一个更棒的功能：打分支
```java
mvn release:branch
```
通过maven打分支,执行下列操作
* 检查项目是否有未提交代码
* 为分支更改POM的版本,例如从`1.1.00SNAPSHOT`改变为`1.1.1-SNAPSHOT`
* 将POM中的SCM信息更新为分支地址
* 提交以上更改
* 将主干代码更新为分支代码
* 修改本地代码使之回退到之前的版本(用户可以指定新的版本)
* 提交本地更改


```java
mvn release:update-versions
```

## 生命周期
Maven的生命周期就是对所有的构建过程进行抽象和统一.Maven的生命周期是抽象的,因此生命周期本身是并不做任何实际工作的,实际的任务交给插件来完成.

Maven拥有如下三套相互独立的生命周期,每个生命周期都包含一些阶段(phase),阶段是按照顺序执行的,而且当PhaseA在PhaseB之前,那么当执行PhaseB时会先执行PhaseA. 但是这三套生命周期是完全独立的.

### clean生命周期
清理项目,下列是该生命周期的阶段
* `pre-clean`
* `clean`
* `post-clean`
该生命周期包含的Maven命令：
```java
mvn clean
```

### default生命周期
构建项目,下列是该生命周期的阶段
* `validate`
* `initialize`
* `generate-sources`
* `process-sources`
* `generate-resources`
* `process-resources`
* `compile`
* `process-class`
* `generate-test-sources`
* `process-test-sourcs`
* `generate-test-resources`
* `process-test-resources`
* `test-compile`
* `process-test-classes`
* `test`
* `generate-package`
* `package`
* `pre-interation-test`
* `interation-test`
* `post-interatopm-test`
* `verify`
* `install`
* `deploy`
该生命周期包含的Maven命令：
```java
mvn validate
mvn compile
mvn test
mvn package
mvn verify
mvn install
mvn deploy
```

安装jar包到本地库
```java
mvn install:install-file -DgroupId=demo -DartifactId=test -Dversion=1.0 -Dpackaging=jar -Dfile=E:\XingeApp.jar
```
安装到自己搭建的中央仓库
```java
mvn deploy:deploy-file -DgroupId=demo -DartifactId=demo -Dversion=1.0 -Dpackaging=jar -Dfile=E:\XingeApp.jar -Durl=[url] -DrepositoryId=[id]
```

maven依赖本地tools.jar
```xml
<dependency>
    <groupId>com.sun</groupId>
    <artifactId>tools</artifactId>
    <version>1.8</version>
    <scope>system</scope>
    <systemPath>D:/Program Files/Java/jdk1.8.0_77/lib/tools.jar</systemPath>
</dependency>
```

### site生命周期
建立项目站点,下列是该生命周期的阶段
* `pre-site`
* `site`
* `post-site`
* `site-deploy`
该生命周期包含的Maven命令：
```java
mvn site
```

## 插件
插件里会包含多个目标,每个目标都对应着特定的功能,也就是说插件里的功能是通过目标来实现了. 例如`maven-compiler-plugin`的`compile`目标的写法为`compiler:compile`.

### 插件绑定
我们可以将插件的目标与生命周期的阶段相绑定.

default生命周期与内置插件绑定关系及具体任务:

生命周期阶段                  | 插件目标                              |执行任务
-----------------------------|--------------------------------------|--------------
process-resources            |maven-resources-plugin:resources      |复制主资源文件至主输出目录
compile                      |maven-compile-plugin:compile	        |编译主代码至主输出目录
process-test-resources       |maven-resources-plugin:testRresources |复制测试资源文件至测试输出目录
test-compile                 |maven-compiler-plugin:testCompile     |编译测试代码至测试输出目录
test	                     |maven-surefire-plugin:test            |执行测试用例
package	                     |maven-jar-plugin:jar                  |创建项目jar包
install	                     |maven-install-plugin:install          |将项目输出构件安装到本地仓库
deploy                       |maven-deploy-plugin:deploy            |将项目输出构件部署到远程仓库


我们来自定义绑定：
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
我们在`install`阶段绑定了`exec-maven-plugin`插件的`java`目标.

### maven-jar-plugin
当我们定义了`<packaging>jar</packaging>`后, 在packaging阶段就会自动调用`maven-jar-plugin`插件。如果是`<packaging>war</packaging>`则会调用`maven-war-plugin`插件
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>wang.ming15</groupId>
    <artifactId>testMavenPlugin</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <dependencies>
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
            <version>1.2.8</version>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <configuration>
                    <archive>
                        <manifestFile>
                            src/main/resources/META-INF/MANIFEST.MF
                        </manifestFile>
                    </archive>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```
在上面的例子中我们指定了MANIFEST文件, 其实还有另外一种写法
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-jar-plugin</artifactId>
    <configuration>
        <archive>
            <manifest>
                <addClasspath>true</addClasspath>
                <classpathPrefix>lib/</classpathPrefix>
                <mainClass>
                    App
                </mainClass>
            </manifest>
        </archive>
    </configuration>
</plugin>
```
除了`archive`还有一些其他的配置选项
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-jar-plugin</artifactId>
    <configuration>
        <!-- 指定排除的文件 -->
        <excludes>
            <exclude>**/service/*</exclude>
        </excludes>
        <!-- 指定包含的文件 -->
        <includes>
            <include>**/</include>
        </includes>
        <!-- 最终的jar包名, 会替换jarName -->
        <finalName>finalName</finalName>
        <!-- 如果没有指定finalName, 则会使用这个名字-->
        <jarName>jarName</jarName>
        <!-- 重新指定输出路径, 替换target-->
        <outputDirectory>./newoutput</outputDirectory>
    </configuration>
</plugin>
```

### maven-dependency-plugin
maven-dependency-plugin是处理与依赖相关的插件. 我们一般使用它的copy依赖功能, 下面的例子就是将依赖copy到target/lib目录下
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>wang.ming15</groupId>
    <artifactId>testMavenPlugin</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <dependencies>
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
            <version>1.2.8</version>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-dependency-plugin</artifactId>
                <executions>
                    <execution>
                        <id>copy-dependencies</id>
                        <phase>package</phase>
                        <goals>
                            <goal>copy-dependencies</goal>
                        </goals>
                        <configuration>
                            <outputDirectory>${project.build.directory}/lib</outputDirectory>
                            <overWriteReleases>false</overWriteReleases>
                            <overWriteSnapshots>false</overWriteSnapshots>
                            <overWriteIfNewer>true</overWriteIfNewer>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

### maven-shade-plugin
引用或者排除引用指定的依赖
```xml
<build>
  <plugins>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-shade-plugin</artifactId>
      <version>2.4.3</version>
      <executions>
        <execution>
          <phase>package</phase>
          <goals>
            <goal>shade</goal>
          </goals>
          <configuration>
            <filters>
              <filter>
                <artifact>junit:junit</artifact>
                <includes>
                  <include>junit/framework/**</include>
                  <include>org/junit/**</include>
                </includes>
                <excludes>
                  <exclude>org/junit/experimental/**</exclude>
                  <exclude>org/junit/runners/**</exclude>
                </excludes>
              </filter>
              <filter>
                <artifact>*:*</artifact>
                <excludes>
                  <exclude>META-INF/*.SF</exclude>
                  <exclude>META-INF/*.DSA</exclude>
                  <exclude>META-INF/*.RSA</exclude>
                </excludes>
              </filter>
            </filters>
          </configuration>
        </execution>
      </executions>
    </plugin>
  </plugins>
</build>
```

### maven-resources-plugin
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>wang.ming15</groupId>
    <artifactId>testMavenPlugin</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <dependencies>
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
            <version>1.2.8</version>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-resources-plugin</artifactId>
                <version>2.7</version>
                <configuration>
                    <!-- 因为拷贝文件涉及到了文件的读写, 在此指定读写文件时的编码格式-->
                    <!--<encoding>UTF-8</encoding>-->
                </configuration>
                <executions>
                    <execution>
                        <id>copy-resources</id>
                        <phase>package</phase>
                        <goals>
                            <goal>copy-resources</goal>
                        </goals>
                        <configuration>
                            <outputDirectory>${basedir}/target/package</outputDirectory>
                            <resources>
                                <resource>
                                    <directory>d://copy</directory>
                                    <filtering>false</filtering>
                                    <includes>
                                        <include>**/*.jar</include>
                                    </includes>
                                </resource>
                            </resources>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>

</project>
```
上面的例子中只拷贝了jar后缀的文件.

> 因为每个execution只能指定一个输出目录, 因此我们要差异化拷贝的话, 可以多写几个execution来实现

### maven-compiler-plugin
[maven-compiler-plugin](http://maven.apache.org/components/plugins/maven-compiler-plugin/compile-mojo.html)
```xml
<build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.5.1</version>
                <configuration>
                    <!-- Compiling Sources Using A Different JDK -->
                    <verbose>true</verbose>
                    <fork>true</fork>
                    <executable><${JAVA_1_4_HOME}/bin/javac</executable>
                    <!-- 指定maven-compiler-plugin插件使用的javac的版本,使用这个参数的时必须将fork设置为true-->
                    <compilerVersion>1.8</compilerVersion>

                    <!-- 指定javac的-source 和 -target 参数。胆识-target参数要小心使用,有坑-->
                    <source>1.8</source>
                    <target>1。8</target>

                    <!-- 设置编译时使用的内存大小 -->
                    <fork>true</fork>
                    <meminitial>128m</meminitial>
                    <maxmem>512m</maxmem>

                    <!-- 在编译时传递jvm参数-->
                    <compilerArgs>
                        <arg>-verbose</arg>
                        <arg>-Xlint:all,-options,-path</arg>
                    </compilerArgs>
                </configuration>

            </plugin>
        </plugins>
</build>
```


### Assembly
学习自[assembly插件官方教程](http://maven.apache.org/plugins/maven-assembly-plugin/)

目前assembly插件只有`assembly:single`可用, 其他的都已经被废弃了.
```xml
mvn assembly:single
```

#### jar包
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

#### 过滤文件
我们可以自己实现一个descriptor来实现文件过滤功能

> excludes 还可以过滤文件夹
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

#### 过滤依赖
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

#### 打包子模块
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

## maven属性
内置属性
* `${basedir}`: 表示项目根目录,即包含`pom.xml`文件的目录
* `${version}`:项目版本

POM属性
该类属性引用POM文件中对应的元素值,例如：
* `${project.artifactId}`: 引用`<project><artifactId>`值
* `${project.build.sourceDirectory}`: 项目的主源码目录
* `${project.build.directory}`: 项目构建的输出目录

自定义属性, 在`<Properties><property>`里定义的属性

setting属性, 与POM属性同理,但是以`settings`开头. 这个属性引用的是`setting.xml`文件中XML元素的值.

Java系统属性, 所有JAVA系统中的属性都可以使用Maven属性引用,使用`mvn help:system`查看所有Java系统属性

环境变量属性, 所有环境变量属性属性都可以使用`env`开头的属性引用,例如`${env.JAVA_HOME}`

