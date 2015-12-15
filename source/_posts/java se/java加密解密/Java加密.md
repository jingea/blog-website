category: JAVA SE
tag: JAVA加密解密
date: 2014-11-08
title: Java加密
---
# Java加密

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

### 以下内容是JDK 1.7所提供的安全提供者的配置信息.
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

> 上述这些提供者均是`Provider`类(`java.security.Provider`)的子类.其中`sun.security.provider.Sun`是基本安全提供者,`sun.security.rsa.SunRsaSign`是实现RSA算法的提供者.
>
> 与上一版本对比,Java 7新增了EC算法安全提供者—`sun.security.ec.SunEC`,暗示在该版本中可能支持相应的算法实现.
>
> Java安全体系不仅支持来自Sun官方提供的安全提供者,同时也可配置第三方安全提供者以扩展相应的算法实现等.

### 安全提供者实现了两个概念的抽象:
* 引擎:	可以理解为操作,如加密、解密等.
* 算法: 定义了操作如何执行,如一个算法可以理解为一个引擎的具体实现.当然,一个算法可以有多种实现方式,这就意味着同一个算法可能与多个引擎的具体实现相对应.

> 安全提供者接口的目的就是提供一个简单的机制,从而可以很方便地改变或替换算法及其实现.在实际开发中,程序员只需要用引擎类实现特定的操作,而不需要关心实际进行运算的类是哪一个.
>
> `Provider`类和`Security`类(`java.security.Security`)共同构成了安全提供者的概念.

### 本文全貌

* 主要详解了`java.security`包与`javax.crypto包`,这两个包中包含了Java加密与解密的核心部分.
* 在`java.security.interfaces`包和`javax.crypto.interfaces`包中包含了密钥相关的接口.
* 在`java.security.spec`包和`javax.crypto.spec`包中包含了密钥规范和算法参数规范的类和接口.

#java7支持的算法
## 消息摘要算法

## MD系列
* MD2             128位
* MD5             128位

## SHA系列
* SHA-1           160位
* SHA-256         256位
* SHA-384         384位
* SHA-512         512位

## Hmac系列
* HmacMD5        128位
* HmacSHA1       160位
* HmacSHA256     256位
* HmacSHA384     384位
* HmacSHA512     512位


##  对称加密算法

* DES
```
56(默认值)
ECB,CBC,PCBC,CTR,CTS,CFB,CFB8至CFB128,OFB,OFB8至OFB128
NoPadding,PKCS5Padding,ISO10126Padding
```
* DESede
```
112,168(默认值)
ECB,CBC,PCBC,CTR,CTS,CFB,CFB8至CFB128,OFB,OFB8至OFB128
NoPadding,PKCS5Padding,ISO10126Padding
```
* AES
```
128(默认值),192,256
ECB,CBC,PCBC,CTR,CTS,CFB,CFB8至CFB128,OFB,OFB8至OFB128
NoPadding,PKCS5Padding,ISO10126Padding
```
* Blowfish
```
32z至448(8的倍数,默认值128)
ECB,CBC,PCBC,CTR,CTS,CFB,CFB8至CFB128,OFB,OFB8至OFB128
NoPadding,PKCS5Padding,ISO10126Padding
```
* RC2
```
40至1024(8的倍数,默认值128)
ECB
NoPadding
```
* RC4
```
40至1024(8的倍数,默认值128)
ECB
NoPadding
```
## 对称加密算法-PBE
* PBEWithMD5AndDES
```
56
CBC
PKCS5Padding
```
* PBEWithMD5AndTripleDES
```
112,168(默认值)
CBC
PKCS5Padding
```
* PBEWithSHA1AndRC2_40
```
112,168(默认值)
CBC
PKCS5Padding
```
* PBEWithSHA1AndDESede
```
40至1024(8的整数倍,默认值128)
CBC
PKCS5Padding
```
## 非对称加密算法
* DH
```
512-1024(64的整数倍)
```
* RSA
```
512-65536(64的整数倍)
ECB
```
* ECDH
```
112-571
```

