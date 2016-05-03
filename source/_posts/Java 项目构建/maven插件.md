category: Java 项目构建
date: 2015-06-08
title: Maven 插件
---
插件里会包含多个目标,每个目标都对应着特定的功能,也就是说插件里的功能是通过目标来实现了. 例如`maven-compiler-plugin`的`compile`目标的写法为`compiler:compile`.

## 插件绑定
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

## maven-jar-plugin
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

## maven-dependency-plugin
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

## maven-shade-plugin
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

## maven-resources-plugin
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