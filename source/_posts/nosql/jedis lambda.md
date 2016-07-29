category: NoSql
date: 2016-07-29
title: 当Jedis 遇上 Lambda
---
一直苦恼于使用Jedis时要时时牢记关闭资源这个操作, 当Lambda出现后, 这种苦恼终于可以远离而去了
```java
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class Medis {

	private final JedisPool jedisPool;

	public Medis() {
		JedisPoolConfig config = new JedisPoolConfig();
		jedisPool = new JedisPool(config, "192.168.15.20", 6379, 10000, "xpec@2015");
	}

	public void consumer(NonResultOperator consumer) {
		try(Jedis jedis = jedisPool.getResource()) {
			consumer.accept(jedis);
		}
	}

	public <U> U accept(ResultOperator consumer) {
		try(Jedis jedis = jedisPool.getResource()) {
			return (U) consumer.accept(jedis);
		}
	}
}

@FunctionalInterface
interface ResultOperator {
	Object accept(redis.clients.jedis.Jedis t);
}

@FunctionalInterface
interface NonResultOperator {
	void accept(redis.clients.jedis.Jedis t);
}
```

写一个测试类
```java
public class MedisTest {

	@Test
	public void test() {
		Medis medis = new Medis();
		medis.consumer(jedis -> jedis.set("123", "456"));

		String result = medis.accept(jedis -> jedis.get("123"));
		System.out.println(result);
	}
}
```
结果为
```bash
456
```