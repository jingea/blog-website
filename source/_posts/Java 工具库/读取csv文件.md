category: Java工具
date: 2015-06-08
title: 读取csv文件
---
利用Apache Commons CSV读取csv文件, 首先我们添加如下依赖
```xml
<dependency>
	<groupId>org.apache.commons</groupId>
	<artifactId>commons-csv</artifactId>
	<version>1.2</version>
</dependency>
```
读取csv文件
```java
Reader in = new FileReader("path/to/file.csv");
Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
for (CSVRecord record : records) {
    String lastName = record.get("Last Name");
    String firstName = record.get("First Name");
}
```


