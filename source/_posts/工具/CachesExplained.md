category: 工具
date: 2013-09-13
title: CachesExplained
---

## Example
```java
LoadingCache<Key, Graph> graphs = CacheBuilder.newBuilder()  
       .maximumSize(1000)  
       .expireAfterWrite(10, TimeUnit.MINUTES)  
       .removalListener(MY_LISTENER)  
       .build(  
           new CacheLoader<Key, Graph>() {  
             public Graph load(Key key) throws AnyException {  
               return createExpensiveGraph(key);  
             }  
           });  
```

## 适用范围
缓存的使用范围是十分广泛的。每当计算或者通过一些方式生成一个值的时候，会造成资源严重浪费的时候我们可以考虑用缓存技术来存储该值。

缓存和`CurrentMap`十分相似(键值对形式),但是他们之间还是仍有诸多不同.他们之间最大的不同之处是`ConcurrentMap`里的元素在被明确地删除之前会一直被存储在`Map`里，但是对于cache来说，为了维护cache的内存占用，cache被设计成会自动删除其中的数据。在一些应用场合中，使用`LoadingCache也`是非常有用的，即使它不被允许自动删除其entries(由于它的自动内存加载机制，他不允许这么做)。
    
一般来说，Guava的缓存技术一般适用于以下场合
1. 想要消耗掉一些内存来换取速度的提升
2. key(map中也有key)会在一段时间内被频繁的访问。
3. 在cache存储的数据容量不会大于其RAM中存储的。

你可以按照上文中的例子(CacheBuilder的builder pattern)来创建一个Cache，但是定制属于自己应用程序的Cache才是最激动人心的事。

>注：如果你的应用程序中不会用到上文提到的Cache的特性，那么你可以考虑ConcurrentHashMap，它在内存方面也许更有优势。但是ConcurrentHashMap是非常困难，甚至不可能的来模拟出Cache那样的强大功能。
至于如何选择，就要看你的应用程序需求了,仔细看看下面提到的特性----例如元素的存活期，元素的大小等等，这些特点都是在ConcurrentMap里所不存在的。

## 总体
你应该先问自己第一个问题：你是否有特定的明确的通过某些keys的作参数生成Value的方法？如果你的回答是肯定的话，那么`CacheLoader`是适合你的。如果你不需要通过某些key来生成value或者你想要重载默认的方法或者想要使用`get-if-absent-compute`方式,你可以参考[From A Callable]()。一般我们可以通过`Cache.put`直接将元素插入cache中，但是我们应该首先考虑它的自动缓存加载，因为它会考虑到所有缓存内容的一致性。

> From A CacheLoader : LoadingCache通过一个附着的CacheLoader来创建。创建一个CacheLoader也是非常简单的，只要实现一个V load(K key) throws exception的方法就可以了.下面的例子展示出如何创建一个LoadingCache
```java
LoadingCache<Key, Graph> graphs = CacheBuilder.newBuilder()  
       .maximumSize(1000)  
       .build(  
           new CacheLoader<Key, Graph>() {  
             public Graph load(Key key) throws AnyException {  
               return createExpensiveGraph(key);  
             }  
           });  
  
...  
try {  
  return graphs.get(key);  
} catch (ExecutionException e) {  
  throw new OtherException(e.getCause());  
}  
```
上面的例子也展示除了我们可以通过`get(K)`的方式对`LoadingCache`进行查询获取值。我们如果可以从cache中查找到该key，那么将会直接返回该key对应的value，否则会通过cache的`CacheLoader`自动加载一个新的键值对，然后返回该值。因为`CacheLoader`可能会抛出异常，所以get(K)可能会抛出`Execution`。如果在`CacheLoader`中定义了一个非异常检查的`load`方法，那么在查询取值时可以使用`getUnchecked(Key)`;但是如果你声明了throws，则一定不要调用`getUnchecked(Key)`. 下面是一个例子：
```java
LoadingCache<Key, Graph> graphs = CacheBuilder.newBuilder()  
       .expireAfterAccess(10, TimeUnit.MINUTES)  
       .build(  
           new CacheLoader<Key, Graph>() {  
             public Graph load(Key key) { // no checked exception  
               return createExpensiveGraph(key);  
             }  
           });  
  
...  
return graphs.getUnchecked(key);  
```

当我们想要获取N多值的时候，在查询时可以使用方法`getAll(Iterable<? extends K>)`.在getAll中，对每一个不存在于cache里的key都会执行一个单独的对`CacheLoader.load`的方法调用来加载该值。看，guava提供了如此优秀的方法当进行一次getAll比多次get更有优势时，我们就应该重载`CacheLoader.loadAll`来实现这个功能。

可以通过实现`CacheLoader.loadAll`这个方法来加载那些不被包含的显示请求的值。

如果想要设定cache有一定的大小可以通过`CacheBuilder.maximumSize(long)`来设定。如此设定会使得cache在达到限定值时删除那些没有被使用过或者不经常使用的entries.

> From a Callable: 所有的Guava caches，不管是否是loading模式的，都支持get(K, Callable<V>)方法。这个方法会从cache中返回与该key相关联的value，或者从Callable中计算该值并把它放进cache中。这个方法使用了一个非常简单的模式"if cached, return; otherwise create, cache and return"

```java
Cache<Key, Value> cache = CacheBuilder.newBuilder()  
    .maximumSize(1000)  
    .build(); // look Ma, no CacheLoader  
...  
try {  
  // If the key wasn't in the "easy to compute" group, we need to  
  // do things the hard way.  
  cache.get(key, new Callable<Value>() {  
    @Override  
    public Value call() throws AnyException {  
      return doThingsTheHardWay(key);  
    }  
  });  
} catch (ExecutionException e) {  
  throw new OtherException(e.getCause());  
}  
```
Inserted Directly : Values也可以通过cache.put(key,value)直接将值插入cache中。该方法将重写先前与key匹配的entry。

## Eviction
一个不能避免的问题：由于内存原因，我们不能将所有的东西都加载进cache中。那么你必须下决定：一个cache entry应该何时被抛弃。Guava提供了三种entry释放策略：size-basd evicton，time-based eviction 和reference-based eviction

### Size-based Eviction
如果你的cache不允许扩容,即不允许超过设定的最大值，那么使用CacheBuilder.maxmuSize(long)即可。在这种条件下，cache会自己释放掉那些最近没有或者不经常使用的entries内存。注意：cache并不是在超过限定时才会删除掉那些entries，而是在即将达到这个限定值时，那么你就要小心考虑这种情况了，因为很明显即使没有达到这个限定值，cache仍然会进行删除操作。

还有一种情况：cache里不同的entries可能会有不同的weight。例如：如果你的cache values有着截然不同的内存占用----你可以使用CacheBuilder.weigher(Weigher)设定weigh和使用CacheBuilder.maximumWeight(long)设定一个最大值。
下面代码展示了对weight的使用
```java
LoadingCache<Key, Graph> graphs = CacheBuilder.newBuilder()  
       .maximumWeight(100000)  
       .weigher(new Weigher<Key, Graph>() {  
          public int weigh(Key k, Graph g) {  
            return g.vertices().size();  
          }  
        })  
       .build(  
           new CacheLoader<Key, Graph>() {  
             public Graph load(Key key) { // no checked exception  
               return createExpensiveGraph(key);  
             }  
           });  
```

### Timed Eviction
CacheBuilder 提供了俩种方式来实现这一模式
expireAfterAccess(long, TimeUnit) 
从最后一次访问(读或者写)开始计时，过了这段指定的时间就会释放掉该entries。注意：那些被删掉的entries的顺序时和size-based eviction是十分相似的。
expireAfterWrite(long,TimeUnit)
它是从entries被创建或者最后一次被修改值的点来计时的，如果从这个点开始超过了那段指定的时间，entries就会被删除掉。这点设计的很精明，因为数据会随着时间变得越来越陈旧。
如果想要测试Timed Eviction，使用Ticker interface和CacheBuilder.ticker(Ticker)方法对你的cache设定一个时间即可，那么你就不需要去等待系统时间了。

### Reference-based Eviction
Guava为你准备了entries的垃圾回收器，对于keys或者values可以使用`weak reference` ，对于values可以使用 `soft reference`.

`CacheBuilder.weakKeys()`通过weak reference存储keys。在这种情况下，如果keys没有被strong或者soft引用，那么entries会被垃圾回收。这种条件下的垃圾回收器是建立在标识符(引用)之上的，那么这会造成整个cache是使用==来比较俩个key的，而不是equals();

`CacheBuilder.weakValues()`  通过weak referene 存储values.在这种情况下，如果valves没有被strong或者soft引用，那么entries会被垃圾回收。这种条件下的垃圾回收器是建立在标识符(引用)之上的，那么这会造成整个cache是使用==来比较俩个values的，而不是equals();
CacheBuilder.softValues() 

### Explicit Removals
也许在某年某月某天你不想再等cache释放entries，而是自己能手动的去释放掉这些entries，下面三个方法会帮助你
* 单个释放：Cache.invalidate(key)
* 多个释放：Cache.invalidateAll(keys)
* 全部释放：Cache.invalidateAll()

### Removal Listeners
cache允许你指定一个removal listener监听entry的移除操作(例如`CacheBuilder.removalListener(RemovalListener)`).通过R`emovaNotification`获得的`RemovalListener`制定了RemovalCause,key和value`。

>注意RemovalListener抛出的任何异常都会被Logger记录然后被丢弃

```java
CacheLoader<Key, DatabaseConnection> loader = new CacheLoader<Key, DatabaseConnection> () {  
  public DatabaseConnection load(Key key) throws Exception {  
    return openConnection(key);  
  }  
};  
RemovalListener<Key, DatabaseConnection> removalListener = new RemovalListener<Key, DatabaseConnection>() {  
  public void onRemoval(RemovalNotification<Key, DatabaseConnection> removal) {  
    DatabaseConnection conn = removal.getValue();  
    conn.close(); // tear down properly  
  }  
};  
  
return CacheBuilder.newBuilder()  
  .expireAfterWrite(2, TimeUnit.MINUTES)  
  .removalListener(removalListener)  
  .build(loader);  
```
警告：removal listeners是被默认同步执行的，而且cache的维护是在其普通操作中维护的，那么“昂贵的”removal listener会降低cache操作(某些方法)的效率。如果你在使用一个"昂贵的"removal listener，你可以使用RemovalListener.asynchronous(RemovalListener,Executor),将其布置成异步操作.
