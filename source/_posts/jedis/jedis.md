category: Redis客户端
date: 2016-03-02
title: Jedis 初探
---
## using Jedis in a multithreaded environment
我们不应该在多线程的情况下使用同一个Jedis实例对象, 因为在Jedis本身不是线程安全的, 它可能会引发一些奇奇怪怪的问题. 但是如果我们在每个线程中都创建一个Jedis实例对象的话这也是不可取的, 因为每个Jedis对象的创建都意味着socket和网络连接的创建. 为了避免这种情况,我们应该使用`JedisPool`, 它是一个线程安全的网络连接池. 我们可以通过池子创建多个Jedis实例对象, 当使用完之后再将它返回给池子.

在下面的实例中我们初始化一个池子
```java
JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");
```
因为JedisPool是线程安全的, 因此我们可以将它缓存起来, 供所有线程使用.

`JedisPoolConfig`包含了一个丰富有用的Redis连接配置参数. `JedisPoolConfig`基于[Commons Pool 2](http://commons.apache.org/proper/commons-pool/apidocs/org/apache/commons/pool2/impl/GenericObjectPoolConfig.html)

```java
/// Jedis implements Closable. Hence, the jedis instance will be auto-closed after the last statement.
try (Jedis jedis = pool.getResource()) {
  /// ... do stuff here ... for example
  jedis.set("foo", "bar");
  String foobar = jedis.get("foo");
  jedis.zadd("sose", 0, "car"); jedis.zadd("sose", 0, "bike"); 
  Set<String> sose = jedis.zrange("sose", 0, -1);
}
/// ... when closing your application:
pool.destroy();
```
如果你不喜欢`try-with-resource`这种语法, 那么你可以使用`Jedis.close()`.
```java
Jedis jedis = null;
try {
  jedis = pool.getResource();
  /// ... do stuff here ... for example
  jedis.set("foo", "bar");
  String foobar = jedis.get("foo");
  jedis.zadd("sose", 0, "car"); jedis.zadd("sose", 0, "bike"); 
  Set<String> sose = jedis.zrange("sose", 0, -1);
} finally {
  if (jedis != null) {
    jedis.close();
  }
}
/// ... when closing your application:
pool.destroy();
```

If Jedis was borrowed from pool, it will be returned to pool with proper method since it already determines there was JedisConnectionException occurred. If Jedis wasn't borrowed from pool, it will be disconnected and closed.

下来我们来看一段测试代码
```java
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisTest {

    public static void main(String[] args) {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(10);
        JedisPool pool = new JedisPool(jedisPoolConfig, "10.1.4.110");
        System.out.println("begin : " + pool.getNumActive());
        System.out.println("begin : " + pool.getNumIdle());
        System.out.println("begin : " + pool.getNumWaiters());
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            System.out.println("borrow : " + pool.getNumActive());
            System.out.println("borrow : " + pool.getNumIdle());
            System.out.println("borrow : " + pool.getNumWaiters());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        System.out.println("return : " + pool.getNumActive());
        System.out.println("return : " + pool.getNumIdle());
        System.out.println("return : " + pool.getNumWaiters());
        pool.destroy();
    }
}
```
结果为
```java
begin : 0
begin : 0
begin : 0
borrow : 1
borrow : 0
borrow : 0
return : 0
return : 1
return : 0
```
当调用`jedis.close();`后,池子里空闲的Jedis对象就多了1个, 可是如果我们注释掉这一行后的结果为
```
begin : 0
begin : 0
begin : 0
borrow : 1
borrow : 0
borrow : 0
return : 1
return : 0
return : 0
```
我们发现那个Jedis仍然处于激活状态, 在Jedis 2.8.0版本以前当我们从池子里借用一个Jedis之后还需要将其还回去
```java
public void close(Jedis resource) {
    jedisPool.returnResource(resource);
}
```
但是现在已经不需要了, 而且Jedis已经将这个`returnResource()`的方法舍弃掉了
```java
/** @deprecated */
@Deprecated
public void returnResource(Jedis resource) {
    if(resource != null) {
        try {
            resource.resetState();
            this.returnResourceObject(resource);
        } catch (Exception var3) {
            this.returnBrokenResource(resource);
            throw new JedisException("Could not return the resource to the pool", var3);
        }
    }

}
```

接下来我们看一下多线程情况
```java
public class JedisTest {

    public static void main(String[] args) throws InterruptedException {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(3);
        JedisPool pool = new JedisPool(jedisPoolConfig, "10.1.4.110");
        List<Thread> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Runnable runnable = () -> {
                Jedis jedis = null;
                try {
                    jedis = pool.getResource();
                    int sleep = new Random().nextInt(5);
                    System.out.println("sleep:" + sleep + ". time:" + new Date().toLocaleString() + ". active: " + pool.getNumActive() + ". idle: " + pool.getNumIdle());

                    TimeUnit.SECONDS.sleep(sleep);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    if (jedis != null) {
                        jedis.close();
                    }
                }
            };
            list.add(new Thread(runnable));
        }
        list.forEach(thread -> thread.start());

        Thread.currentThread().join();
        pool.destroy();
    }
}
```
结果为
```
sleep:4. time:2016-3-4 10:04:03. active: 3. idle: 0
sleep:4. time:2016-3-4 10:04:03. active: 3. idle: 0
sleep:3. time:2016-3-4 10:04:03. active: 3. idle: 0
sleep:3. time:2016-3-4 10:04:06. active: 3. idle: 0
sleep:3. time:2016-3-4 10:04:07. active: 2. idle: 1
```