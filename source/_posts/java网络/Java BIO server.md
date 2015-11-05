

一个Java阻塞服务器实现
```java
try(ServerSocket server =  new ServerSocket(9090)) {
	while (true) {
		try(final Socket finalSocket = server.accept();) {
			Runnable runnable = () -> {

				try(BufferedReader in = new BufferedReader(new InputStreamReader(finalSocket.getInputStream()));
					PrintWriter out = new PrintWriter(finalSocket.getOutputStream(), true);) {

					String body = null;
					while ((body = in.readLine()) != null) {
						System.out.println("Server Revice： " + body);
						out.println("Server -> client");
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			};
			new Thread(runnable).start();
		}
	}
}
```

客户端测试代码
```java
try(Socket socket = new Socket("127.0.0.1", 9090);
	BufferedReader in = new BufferedReader(new InputStreamReader(
			socket.getInputStream()));
	PrintWriter out = new PrintWriter(socket.getOutputStream(), true);) {
	System.out.println("Server Revice： " + in.readLine());
	out.println("client -> Server");

} catch (Exception e) {
	e.printStackTrace();
}

```