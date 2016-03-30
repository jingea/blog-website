category: ICE
date: 2016-03-30
title: ZeroC ICE Hello Demo
---
接触ICE有段时间了, 今天就找个时间将ZEROC ICE官网的hello Demo讲解一下.

当我们打开hello demo的时候,我们会看到如下几个文件
×　Client.java : 启动客户端进程
×　config.client : 客户端配置文件
×　config.server : 服务器配置文件
×　Hello.ice : 
×　HelloI.java : 服务器逻辑接入文件
×　Server.java : 启动服务器进程

我们首先看一下Hello.ice这个文件
```java
#pragma once

// module相当于package
module Demo
{

// 定义了一个Hello的服务
interface Hello
{
    idempotent void sayHello(int delay);
    void shutdown();
};

};
```
然后我们看一下HelloI.java这个文件, 这里面是服务器用来处理客户端请求的
```java
import Demo.*;

public class HelloI extends _HelloDisp {
    public void sayHello(int delay, Ice.Current current) {
        if(delay > 0) {
            try {
                Thread.currentThread().sleep(delay);
            }
            catch(InterruptedException ex1){
            }
        }
        System.out.println("Hello World!");
    }

    public void shutdown(Ice.Current current) {
        System.out.println("Shutting down...");
        current.adapter.getCommunicator().shutdown();
    }
}
```
至于_HelloDisp我们会在下面讲解.

然后我们现在启动服务器, 在看启动服务器进程的我们首先看一下, 服务器端的配置(由于配置较多,我只将最基本的配置挑选出来了)
```java
Hello.Endpoints=tcp -h localhost -p 10000:udp -h localhost -p 10000
```
然后看服务器进程启动过程
```java
import Demo.*;

public class Server extends Ice.Application
{
    public int run(String[] args) {
		// 创建Hello适配器(也就是config.server文件中的Hello.Endpoints), 创建网络监听
        Ice.ObjectAdapter adapter = communicator().createObjectAdapter("Hello");
		// 然后在Hello适配器中监听hello调用, 当监听到hello调用时由HelloI进行处理
        adapter.add(new HelloI(), communicator().stringToIdentity("hello"));
        adapter.activate();
        communicator().waitForShutdown();
        return 0;
    }

    public static void main(String[] args) {
        Server app = new Server();
        int status = app.main("Server", args, "config.server");
        System.exit(status);
    }
}
```

现在我们启动客户端进程, 同样在启动之前看一下客户端的配置
```java
Hello.Proxy=hello:tcp -h localhost -p 10000:udp -h localhost -p 10000
```
在下面的客户端示例中我们会拿到`Hello.Proxy`代理, 然后ice内部会发送hello调用从而实现RPC业务逻辑调用

然后我们看一下启动过程
```java
import Demo.*;

public class Client extends Ice.Application {
    class ShutdownHook extends Thread {
        public void run() {
            try {
                communicator().destroy();
            }
            catch(Ice.LocalException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void menu() {
        System.out.println(
            "usage:\n" +
            "t: send greeting as twoway\n" +
            "o: send greeting as oneway\n" +
            "O: send greeting as batch oneway\n" +
            "d: send greeting as datagram\n" +
            "D: send greeting as batch datagram\n" +
            "f: flush all batch requests\n" +
            "T: set a timeout\n" +
            "P: set a server delay\n" +
            "S: switch secure mode on/off\n" +
            "s: shutdown server\n" +
            "x: exit\n" +
            "?: help\n");
    }

    public int run(String[] args) {
        setInterruptHook(new ShutdownHook());

		// 根据配置中的Hello.Proxy创建代理, 稍后根据代理进行网络连接
        HelloPrx twoway = HelloPrxHelper.checkedCast(
            communicator().propertyToProxy("Hello.Proxy").ice_twoway().ice_timeout(-1).ice_secure(false));
        if(twoway == null) {
            System.err.println("invalid proxy");
            return 1;
        }
        HelloPrx oneway = (HelloPrx)twoway.ice_oneway();
        HelloPrx batchOneway = (HelloPrx)twoway.ice_batchOneway();
        HelloPrx datagram = (HelloPrx)twoway.ice_datagram();
        HelloPrx batchDatagram = (HelloPrx)twoway.ice_batchDatagram();

        boolean secure = false;
        int timeout = -1;
        int delay = 0;

        menu();

        java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));

        String line = null;
        do {
            try {
                System.out.print("==> ");
                System.out.flush();
                line = in.readLine();
                if(line == null) {
                    break;
                }
                if(line.equals("t")) {
                    twoway.sayHello(delay);
                }
                else if(line.equals("o")) {
                    oneway.sayHello(delay);
                }
                else if(line.equals("O")) {
                    batchOneway.sayHello(delay);
                }
                else if(line.equals("d")) {
                    if(secure) {
                        System.out.println("secure datagrams are not supported");
                    }
                    else {
                        datagram.sayHello(delay);
                    }
                }
                else if(line.equals("D")) {
                    if(secure) {
                        System.out.println("secure datagrams are not supported");
                    }
                    else {
                        batchDatagram.sayHello(delay);
                    }
                }
                else if(line.equals("f")) {
                    communicator().flushBatchRequests();
                }
                else if(line.equals("T")) {
                    if(timeout == -1) {
                        timeout = 2000;
                    }
                    else {
                        timeout = -1;
                    }

                    twoway = (HelloPrx)twoway.ice_timeout(timeout);
                    oneway = (HelloPrx)oneway.ice_timeout(timeout);
                    batchOneway = (HelloPrx)batchOneway.ice_timeout(timeout);

                    if(timeout == -1) {
                        System.out.println("timeout is now switched off");
                    }
                    else {
                        System.out.println("timeout is now set to 2000ms");
                    }
                }
                else if(line.equals("P")) {
                    if(delay == 0) {
                        delay = 2500;
                    }
                    else {
                        delay = 0;
                    }

                    if(delay == 0) {
                        System.out.println("server delay is now deactivated");
                    }
                    else {
                        System.out.println("server delay is now set to 2500ms");
                    }
                }
                else if(line.equals("S")) {
                    secure = !secure;

                    twoway = (HelloPrx)twoway.ice_secure(secure);
                    oneway = (HelloPrx)oneway.ice_secure(secure);
                    batchOneway = (HelloPrx)batchOneway.ice_secure(secure);
                    datagram = (HelloPrx)datagram.ice_secure(secure);
                    batchDatagram = (HelloPrx)batchDatagram.ice_secure(secure);

                    if(secure) {
                        System.out.println("secure mode is now on");
                    }
                    else {
                        System.out.println("secure mode is now off");
                    }
                }
                else if(line.equals("s")) {
                    twoway.shutdown();
                }
                else if(line.equals("x")) {
                    // Nothing to do
                }
                else if(line.equals("?")) {
                    menu();
                }
                else {
                    System.out.println("unknown command `" + line + "'");
                    menu();
                }
            }
            catch(java.io.IOException ex) {
                ex.printStackTrace();
            }
            catch(Ice.LocalException ex) {
                ex.printStackTrace();
            }
        }
        while(!line.equals("x"));

        return 0;
    }

    public static void main(String[] args)
    {
        Client app = new Client();
        int status = app.main("Client", args, "config.client");
        System.exit(status);
    }
}
```
这里我们要详细讲一下客户端示例中的几种调用
* oneway 
* batchOneway
* datagram
* batchDatagram