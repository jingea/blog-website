category: Java
date: 2014-11-08
title: Java加密 -- 概述
---

## Java安全领域组成部分

Java安全领域总共分为4个部分:
* `JCA`(`Java Cryptography Architecture`,Java加密体系结构). JCA提供基本的加密框架,如证书、数字签名、消息摘要和密钥对产生器.

* `JCE`(`Java Cryptography Extension`,Java加密扩展包).JCE在JCA的基础上作了扩展,提供了各种加密算法、消息摘要算法和密钥管理等功能.我们已经有所了解的DES算法、AES算法、RSA算法、DSA算法等就是通过JCE来提供的.有关JCE的实现主要在javax.crypto包(及其子包)中.

* `JSSE`(`Java Secure Sockets Extension`,Java安全套接字扩展包). JSSE提供了基于SSL(Secure Sockets Layer,安全套接字层)的加密功能.在网络的传输过程中,信息会经过多个主机(很有可能其中一台就被窃听),最终传送给接收者,这是不安全的.这种确保网络通信安全的服务就是由JSSE来提供的.

* `JAAS`(`Java Authentication and Authentication Service`,Java鉴别与安全服务).JAAS提供了在Java平台上进行用户身份鉴别的功能.如何提供一个符合标准安全机制的登录模块,通过可配置的方式集成至各个系统中呢？这是由JAAS来提供的.

JCA和JCE是Java平台提供的用于安全和加密服务的两组API.它们并不执行任何算法,它们只是连接应用和实际算法实现程序的一组接口.软件开发商可以根据JCE接口(又称安全提供者接口)将各种算法实现后,打包成一个Provider(安全提供者),动态地加载到Java运行环境中.

根据美国出口限制规定,JCA可出口(JCA和Sun的一些默认实现包含在Java发行版中),但JCE对部分国家是限制出口的.因此,要实现一个完整的安全结构,就需要一个或多个第三方厂商提供的JCE产品,称为安全提供者.BouncyCastle JCE就是其中的一个安全提供者.

安全提供者是承担特定安全机制实现的第三方.有些提供者是完全免费的,而另一些提供者则需要付费.提供安全提供者的公司有Sun、Bouncy Castle等,Sun提供了如何开发安全提供者的细节.Bouncy Castle提供了可以在J2ME/J2EE/J2SE平台得到支持的API,而且Bouncy Castle的API是免费的.

JDK 1.4版本及其后续版本中包含了上述扩展包,无须进行配置.在此之前,安装JDK后需要对上述扩展包进行相应配置.

##  安全提供者体系结构

Java安全体系结构通过扩展的方式,加入了更多的算法实现及相应的安全机制.我们把这些提供者称为安全提供者(以下简称“提供者”).

以下内容是JDK 1.7所提供的安全提供者的配置信息.
* security.provider.1=sun.security.provider.Sun
* security.provider.2=sun.security.rsa.SunRsaSign
* security.provider.3=sun.security.ec.SunEC
* security.provider.4=com.sun.net.ssl.internal.ssl.Provider
* security.provider.5=com.sun.crypto.provider.SunJCE
* security.provider.6=sun.security.jgss.SunProvider
* security.provider.7=com.sun.security.sasl.Provider
* security.provider.8=org.jcp.xml.dsig.internal.dom.XMLDSigRI
* security.provider.9=sun.security.smartcardio.SunPCSC
* security.provider.10=sun.security.mscapi.SunMSCAPI

上述这些提供者均是`Provider`类(`java.security.Provider`)的子类.其中`sun.security.provider.Sun`是基本安全提供者,`sun.security.rsa.SunRsaSign`是实现RSA算法的提供者.

与上一版本对比,Java 7新增了EC算法安全提供者—`sun.security.ec.SunEC`,暗示在该版本中可能支持相应的算法实现.Java安全体系不仅支持来自Sun官方提供的安全提供者,同时也可配置第三方安全提供者以扩展相应的算法实现等.

安全提供者实现了两个概念的抽象:
* 引擎:	可以理解为操作,如加密、解密等.
* 算法: 定义了操作如何执行,如一个算法可以理解为一个引擎的具体实现.当然,一个算法可以有多种实现方式,这就意味着同一个算法可能与多个引擎的具体实现相对应.

安全提供者接口的目的就是提供一个简单的机制,从而可以很方便地改变或替换算法及其实现.在实际开发中,程序员只需要用引擎类实现特定的操作,而不需要关心实际进行运算的类是哪一个.

`Provider`类和`Security`类(`java.security.Security`)共同构成了安全提供者的概念.

本文全貌
* 主要详解了`java.security`包与`javax.crypto包`,这两个包中包含了Java加密与解密的核心部分.
* 在`java.security.interfaces`包和`javax.crypto.interfaces`包中包含了密钥相关的接口.
* 在`java.security.spec`包和`javax.crypto.spec`包中包含了密钥规范和算法参数规范的类和接口.

## java7支持的算法
### 消息摘要算法

MD系列
* MD2             128位
* MD5             128位

SHA系列
* SHA-1           160位
* SHA-256         256位
* SHA-384         384位
* SHA-512         512位

Hmac系列
* HmacMD5        128位
* HmacSHA1       160位
* HmacSHA256     256位
* HmacSHA384     384位
* HmacSHA512     512位


###  对称加密算法

* DES
```java
56(默认值)
ECB,CBC,PCBC,CTR,CTS,CFB,CFB8至CFB128,OFB,OFB8至OFB128
NoPadding,PKCS5Padding,ISO10126Padding
```
* DESede
```java
112,168(默认值)
ECB,CBC,PCBC,CTR,CTS,CFB,CFB8至CFB128,OFB,OFB8至OFB128
NoPadding,PKCS5Padding,ISO10126Padding
```
* AES
```java
128(默认值),192,256
ECB,CBC,PCBC,CTR,CTS,CFB,CFB8至CFB128,OFB,OFB8至OFB128
NoPadding,PKCS5Padding,ISO10126Padding
```
* Blowfish
```java
32z至448(8的倍数,默认值128)
ECB,CBC,PCBC,CTR,CTS,CFB,CFB8至CFB128,OFB,OFB8至OFB128
NoPadding,PKCS5Padding,ISO10126Padding
```
* RC2
```java
40至1024(8的倍数,默认值128)
ECB
NoPadding
```
* RC4
```java
40至1024(8的倍数,默认值128)
ECB
NoPadding
```
### 对称加密算法-PBE
* PBEWithMD5AndDES
```java
56
CBC
PKCS5Padding
```
* PBEWithMD5AndTripleDES
```java
112,168(默认值)
CBC
PKCS5Padding
```
* PBEWithSHA1AndRC2_40
```java
112,168(默认值)
CBC
PKCS5Padding
```
* PBEWithSHA1AndDESede
```java
40至1024(8的整数倍,默认值128)
CBC
PKCS5Padding
```
## 非对称加密算法
* DH
```java
512-1024(64的整数倍)
```
* RSA
```java
512-65536(64的整数倍)
ECB
```
* ECDH
```java
112-571
```

## Bouncy Castle
在[官网](http://www.bouncycastle.org/latest_releases.html) 下载 `bcprov-jdk15on-151.jar` 和 `bcprov-ext-jdk15on-151.jar`

对于Bouncy Castle 提供的扩充算法支持,有俩种方案可选
* 配置方式,通过配置JRE环境,使其作为提供者提供相应的算法支持,在代码实现层面只需指定要扩展的算法名称
> 1. 修改JDK
    修改java.security配置文件(jdk1.7.0_75\jre\lib\security)
    添加安全提供者 security.provider.11=org.bouncycastle.jce.provider.BouncyCastleProvider
    然后将bcprov-ext-jdk15on-151.jar 文件放入jdk1.7.0_75\jre\lib\ext
  2. 修改JRE
    修改java.security配置文件(jre7\lib\security)
    添加安全提供者 security.provider.11=org.bouncycastle.jce.provider.BouncyCastleProvider
    然后将bcprov-ext-jdk15on-151.jar 文件放入jre7\lib\ext

* 调用方式 : 直接将`bcprov-ext-jdk15on-151.jar` 导入到项目工程文件

JCE工具将其拓展包：仅包括`org.bouncycastle.jce`包. 这是对JCE框架的支持

也可以通过MAVEN引用依赖的方式
```xml
<!-- https://mvnrepository.com/artifact/org.bouncycastle/bcprov-jdk15on -->
<dependency>
    <groupId>org.bouncycastle</groupId>
    <artifactId>bcprov-jdk15on</artifactId>
    <version>1.55</version>
</dependency>

<!-- https://mvnrepository.com/artifact/org.bouncycastle/bcprov-ext-jdk15on -->
<dependency>
    <groupId>org.bouncycastle</groupId>
    <artifactId>bcprov-ext-jdk15on</artifactId>
    <version>1.55</version>
</dependency>
```

## Base64

Base64是一种基于64个字符的编码算法,根据RFC 2045的定义：Base64内容传送编码是一种以任意8位字节序列组合的描述形式, 这种形式不易被人直接识别.经过Base64编码后的数据会比原始数据略长,为原来的4/3,经Base64编码后的字符串的字符数是以4为单位的整数倍

Base64算法有编码和解码操作可充当加密和解密操作,还有一张字符映射表充当了秘钥.由于字符映射表公开且Base64加密强度并不高,因此不能将其看作现代加密算法.但是如果将字符映射表调整,保密,改造后的Base64就具备了加密算法的意义而且Base64常作为密钥, 密文 和证书的一种通用存储编码格式

实现原理
1. 将给定的字符串以字符为单位转换为对应的字符编码(如ASCII码)
2. 将获得的字符编码转换为二进制码
3. 对获得的二进制码做分组转换操作,每3个8位二进制码为1组,转换为每4个6位二进制码为1组(不足6位时低位补0)这是一个分组变化的过程, 3个8位二进制码和4个6位二进制码的长度都是24位
4. 对获得的4个6位二进制码补位,向6位二进制码添加2位 高位0,组成4个8位二进制码
5. 将获得的4个8位二进制码转换为10进制码
6. 将获得的十进制码转换为base64字符表中对应的字符

```java
对A进行Base64编码
字符              A
ASCII码          65
二进制码            01000001
4-6二进制码     010000      010000
4-8二进制码     00010000    00010000
十进制         16          16
字符表映射码      Q           Q           =   =

字符A编码之后就变成了QQ==

base64 映射表
V E           V E           V E           V E
0 A            17 R            34 i            51 z
1 B            18 S            35 j            52 0
2 C            19 T            36 k            53 1
3 D            20 U            37 l            54 2
4 E            21 V            38 m            55 3
5 F            22 W            39 n            56 4
6 G            23 X            40 o            57 5
7 H            24 Y            41 p            58 6
8 I            25 Z            42 q            59 7
9 J            26 a            43 r            60 8
10 K           27 b            44 s            61 9
11 L           28 c            45 t            62 +
12 M           29 d            46 u            63 /
13 N           30 e            47 v
14 O           31 f            48 w         (pad) =
15 P           32 g            49 x
16 Q           33 h            50 y
```


代码举例
```java
public class TestBase64 {

    static final String base64 = "base64编码!@#$%^&*()+_=-{}[];:'<>,./?|";

    @before
    public void before () {
        System.out.println(base64);
    }

    @Test
    public void testUrlBase64() {
        // 不能编码空格
        byte[] encode = UrlBase64.encode(base64.getBytes());
        System.out.println("UrlBase64 : " + new String(encode));

        byte[] decode = UrlBase64.decode(encode);
        Assert.assertEquals(base64, new String(decode));
    }

    @Test
    public void testJavaBase64() {
        System.out.println();
        byte[] encode = java.util.Base64.getEncoder().encode(base64.getBytes());
        System.out.println("JavaBase64 : " + new String(encode));
        byte[] decode = java.util.Base64.getDecoder().decode(encode);
        Assert.assertEquals(base64, new String(decode));
    }

    @Test
    public void testApacheBase64() {
        String encode = org.apache.commons.codec.binary.Base64.encodeBase64String(base64.getBytes());
        System.out.println("apacheBase64 : " + encode);
        byte[] decode = org.apache.commons.codec.binary.Base64.decodeBase64(encode.getBytes());
        Assert.assertEquals(base64, new String(decode));

        String url = org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString(base64.getBytes());
        System.out.println("apacheBase64 url : " + url);
        byte[] decoded = org.apache.commons.codec.binary.Base64.decodeBase64(url);
        Assert.assertEquals(base64, new String(decoded));
    }


    @Test
    public void testBouncycastleBase64() {
        byte[] encode = org.bouncycastle.util.encoders.Base64.encode(base64.getBytes());
        System.out.println("BouncycastleBase64 : " + new String(encode));
        byte[] decode = org.bouncycastle.util.encoders.Base64.decode(encode);
        Assert.assertEquals(base64, new String(decode));
    }
}
```