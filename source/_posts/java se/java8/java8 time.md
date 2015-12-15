category: JAVA SE
tag: JAVA8
date: 2015-09-08
title: Java8 时间处理
---

# LocalDate
表示日期的不可变类型，不包含时间和时区。
```java
// 获取当天的日期
LocalDate now = LocalDate.now();
// 2015-09-21
System.out.println(now.toString());
// 获取当前日期的月份,注意这个值是一个英文月份：例如,SEPTEMBER
System.out.println(now.getMonth());
// 这个方式用来获取数字月份
System.out.println(now.getMonth().getValue());
// 获取今天是这个月中的第几天
System.out.println(now.getDayOfMonth());
// 获取今天是这周中的第几天
System.out.println(now.getDayOfWeek());
// 获取今天是今年当中第几天
System.out.println(now.getDayOfYear());
// 这个方式用来获取数字月份 等同于 System.out.println(now.getMonth().getValue());
System.out.println(now.getMonthValue());
// 获取今年年份
System.out.println(now.getYear());
// 根据指定日期生成一个LocalDate 对象
LocalDate someDay = LocalDate.of(2015, 8, 16);
System.out.println(someDay.toString());

// 在当前日期行指定一个时间
System.out.println(now.atTime(12, 59, 59));

LocalDate someDay1 = LocalDate.of(2015, 8, 16);
LocalDate someDay2 = LocalDate.of(2015, 8, 17);
// 判断俩天是否是同一天
System.out.println(someDay1.equals(someDay2));

// 我们获取明天的日期
LocalDate tomorrow = now.plusDays(1);
// 判断今天是否在明天之前
System.out.println(now.isBefore(tomorrow));
```

# LocalTime
```java

```

# LocalDateTime

# Period

# DateTimeFormatter