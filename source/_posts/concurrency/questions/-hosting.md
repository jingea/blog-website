# 提升-hosting

```java
public class FixBy {

	private static boolean isStop ;

	public static void main(String[] args) throws InterruptedException {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				int i = 0;
				while(!isStop) {
					i++;
					System.out.println(i);
				}
			}
		});

		t.start();

		TimeUnit.SECONDS.sleep(1);
		isStop = true;
	}
}
```

```java
public class FixBySynchronezid {

	private static boolean isStop ;

	private static synchronized void stop() {
		isStop = true;
	}

	private static synchronized boolean isStop() {
		return isStop;
	}

//	private static void stop() {
//		isStop = true;
//	}
//
//	private static synchronized boolean isStop() {
//		return isStop;
//	}

	public static void main(String[] args) throws InterruptedException {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				int i = 0;

				while(!isStop())
					i++;
				System.out.println(i);
			}
		});

		t.start();

		TimeUnit.SECONDS.sleep(1);
		stop();
	}

}
```

```java
public class FixByVolatile {

	private static volatile boolean isStop ;

	public static void main(String[] args) throws InterruptedException {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				long i = 0;	// 使用int会溢出

				while(!isStop) {
					i++;
				}
				System.out.println(i);
			}
		});

		t.start();

		TimeUnit.SECONDS.sleep(1);
		isStop = true;
	}
}
```


```java
public class Wrong {

	private static boolean isStop ;

	public static void main(String[] args) throws InterruptedException {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				int i = 0;
				/*
				 * while(!isStop) 会被编译器优化成
				 * if(!isStop)
				 *   while(true)
				 *
				 * 因此永远不会输出i的值
				 */
				while(!isStop)
					i++;
				System.out.println(i);
			}
		});

		t.start();

		TimeUnit.SECONDS.sleep(1);
		isStop = true;
	}
}
```
