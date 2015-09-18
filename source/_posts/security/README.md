category: 
- 加密
title: README
# JDK加密
## java.security包详解

#### javax_crypto
###### Cipher
* [Cipher](https://github.com/wanggnim/GnimSecurity/blob/master/java/src/test/wang/gnim/jdk/javax_crypto/testCipher/TestCipher.java)
```
为加密解密提供密码功能,它构成了Java Cryptographic Extension(JCE) 框架核心.
在java.security包中 只完成了密钥的处理 并未完成加密与解密的操作. 这些核心 操作需要通过Cipher类来实现.
```
* [CipherInputStream](https://github.com/wanggnim/GnimSecurity/blob/master/java/src/test/wang/gnim/jdk/javax_crypto/testCipher/TestCipherInputStream.java)
```
Ciper的拓展,称为密钥拓展流
```

* [CipherOutputStream](https://github.com/wanggnim/GnimSecurity/blob/master/java/src/test/wang/gnim/jdk/javax_crypto/testCipher/TestCipherOutputStream.java)

```
密钥输出流
```

* [SealedObject](https://github.com/wanggnim/GnimSecurity/blob/master/java/src/test/wang/gnim/jdk/javax_crypto/testCipher/TestSealedObject.java)

```
ScaledObject使程序员能够用加密算法创建对象并保护其机密性
在给定任何Serializable对象的情况下,程序员可以序列化格式(即深层复制)封装原始对象的SealedObject
并使用类似于DES的加密算法密封(加密)其序列化的内容,保护其机密性

加密的内容以后可以解密和烦序列话,生成原始对象

已密封的原始对象可以使用以下俩种方式恢复
1.使用采用Cipher对象的getObject)方法.
		此方法需要一个完全初始化的Cipher对象,用相同的用来蜜蜂对象的算法,密钥,填充方案等进行初始化.
		这样做的好处是解封密封 对象的一方不需要知道解密密钥. 例如一方用所需的解密密钥初始化Cipher对象之后,
		它就会将Cipher对象移交给以后要解封密封对象的另一方
2.使用采用Key对象的getObject()方法.
		在此方法中getObject方法创建一个用于适当解密算法的Cipher对象
		并用给定的解密密钥和存储在密封对象中的算法参数 (如果有)对其进行初始化.
		这样做的好处是解封此对象的一方不需要跟踪用来密封该对象的参数 (如IV 、 初始化向量).
```

###### MacMessageDigest

* [KeyAgreement](https://github.com/wanggnim/GnimSecurity/blob/master/java/src/test/wang/gnim/jdk/javax_crypto/testMacMessageDigest/TestKeyAgreement.java)

```
KeyAgreement类提供密钥协定协议的功能,它同样是一个引擎类.
我们称它为密钥协定,将在DH算法实现中使用到它.
```

* [KeyGenerator](https://github.com/wanggnim/GnimSecurity/blob/master/java/src/test/wang/gnim/jdk/javax_crypto/testMacMessageDigest/TestKeyGenerator.java)

```
KeyGenerator类与KeypairGenerato类相似,
KeyGenerato类用来生成私密密钥,我们称之为私密密钥生成器.
KeyGenerator类与KeypairGenerato类相似,KeyGenerato类用来生成私密密钥,我们称之为私密密钥生成器.
生成用于对称加密算法的秘密密钥,并提供相关信息 public class KeyGenerator extends Object

Java7版本中提供了Blowfish、AES、DES和DESede等多种对称加密算法实现,以及HmacMD5、
HmacSHA1和HmacSHA256等多种安全消息摘要算法实现
```

* [Mac](https://github.com/wanggnim/GnimSecurity/blob/master/java/src/test/wang/gnim/jdk/javax_crypto/testMacMessageDigest/TestMac.java)

```
Mac属于消息摘要的一种,但它不同于一般消息摘要（如Message Digest提供的消息摘要实现）,
仅通过输入数据无法活的吧消息摘要,必须有一个由发送方和接收方
共享的秘密密钥才能生成最终的消息摘要——安全消息摘要.安全消息摘要又称消息认证（鉴别）码.
```

* [SecretKeyFactory](https://github.com/wanggnim/GnimSecurity/blob/master/java/src/test/wang/gnim/jdk/javax_crypto/testMacMessageDigest/TestSecretKeyFactory.java)

```
SecretKe factor类同样属于引擎类,与Kefactory类相对应,
它用于产生秘密密钥,我们称之为秘密密钥工厂.
SecretKe factor类同样属于引擎类,与Kefactory类相对应,它用于产生秘密密钥,我们称之为秘密密钥工厂.
  此类表示秘密密钥的工厂
```

###### javax_net_ssl 包用于提供安全套接字包的类,该包对构建秘钥库,
信任管理库及构建安全基于HTTPS的加密网络通信实现的知识

* [HttpsURLConnection](https://github.com/wanggnim/GnimSecurity/blob/master/java/src/test/wang/gnim/jdk/javax_net_ssl/TestHttpsURLConnection.java)

```
HttpsURLConnection 拓展了URLConnection, 支持各种特定于Https的功能
```

* [KeyManagerFactory](https://github.com/wanggnim/GnimSecurity/blob/master/java/src/test/wang/gnim/jdk/javax_net_ssl/TestKeyManagerFactory.java)

```
用来管理秘钥,设定秘钥库. 此类充当基于秘钥内容源的秘钥管理器的工厂.每个秘钥管理器管理特定类型的,由套接字所使用的秘钥内容
```

* [SSLContext](https://github.com/wanggnim/GnimSecurity/blob/master/java/src/test/wang/gnim/jdk/javax_net_ssl/TestSSLContext.java)

```
表示安全套接字上下文,安全套接字协议的实现,它充当于安全套接字工厂或者SSLEngine的工厂
用可选的一组秘钥和信任管理器及安全随机字节初始化此类
```

* [SSLServerSocket](https://github.com/wanggnim/GnimSecurity/blob/master/java/src/test/wang/gnim/jdk/javax_net_ssl/TestSSLServerSocket.java)

```
   SSLServerSocket
```

* [SSLServerSocketFactory](https://github.com/wanggnim/GnimSecurity/blob/master/java/src/test/wang/gnim/jdk/javax_net_ssl/TestSSLServerSocketFactory.java)

```
   SSLServerSocketFactory
```

* [SSLSession](https://github.com/wanggnim/GnimSecurity/blob/master/java/src/test/wang/gnim/jdk/javax_net_ssl/TestSSLSession.java)

```
SSLSession 接口用于保持SSL协议网络交互会话状态. 用来描述俩个实体之间的会话关系
在SSL的会话中,可以获得加密套件(CipherSuite),数字证书等

CipherSuite 明确给出了加密参数, 具体包括：协议,秘钥交换算法,加密算法,工作模式和消息摘要算法
如 TLS_RSA_TITH_AES_256_CBC_SHA 就是一个完成加密套件信息, 它表示：
使用TLS协议,迷药交换算法为RSA,对称加密算法为AES(长度256),使用CBC模式,并使用SHA消息摘要算法
```

* [SSLSocket](https://github.com/wanggnim/GnimSecurity/blob/master/java/src/test/wang/gnim/jdk/javax_net_ssl/TestSSLSocket.java)

```
SSLSocket是基于SSL协议的Socket,用于设置加密套件,处理握手结束事件,并管理SSLSession

目前Java环境中支持的协议有：SSLLv2Hello,SSLv3,TLSv1,TlSv1.1, TLSv1.2
通常默认的是SSLv3和TLSv1.1
setEnabledProtocols(protocols);  通过该方法设置SSL链接可用协议


getSupportedCipherSuites()		获得可支持的加密套件
setEnabledCipherSuites(suites)	为当前SSL链接设置可用的加密套件
getEnabledCipherSuites()			获得当前SSL链接可用的加密套件

有时候需要与远程服务器建立基于SSLSocket的链接. 远程服务仅通过SSLSocket传递数字证书.
这时候,就不能通过HttpsURLConnection来获得数字证书了,本方法就是通过SSLSocket来获得SSLSession
并最终获得数字证书
```

* [SSLSocketFactory](https://github.com/wanggnim/GnimSecurity/blob/master/java/src/test/wang/gnim/jdk/javax_net_ssl/TestSSLSocketFactory.java)

```
   SSLSocketFactory
```

* [TrustManagerFactory](https://github.com/wanggnim/GnimSecurity/blob/master/java/src/test/wang/gnim/jdk/javax_net_ssl/TestTrustManagerFactory.java)

```
 管理信任材料的管理工厂,设定信任库

 每个信任管理器管理特定类型的由安全套接字使用的信任材料
```

###### java_security  为安全框架提供类和接口, 该包只能完成消息摘要算法的实现,同时提供数字签名和秘钥接口的定义

* [AlgorithmParameterGenerator](https://github.com/wanggnim/GnimSecurity/blob/master/java/src/test/wang/gnim/jdk/java_security/TestAlgorithmParameterGenerator.java)

```
引擎类,用于生成某个特定算法中使用的参数集合.

{@link AlgorithmParameters} 生成的参数
```

* [AlgorithmParameters](https://github.com/wanggnim/GnimSecurity/blob/master/java/src/test/wang/gnim/jdk/java_security/TestAlgorithmParameters.java)

```
引擎类. 提供密码参数的不透明表示
{@link AlgorithmParameters}
AlgorithmParameters是一个引擎类,提供密码参数的不透明表示

{@link AlgorithmParameterGenerator} 可以通过该引擎类生成

不透明:不可以直接访问各参数域,只能得到与参数集相关联的算法名及该参数集的某类编码
透明 :用户可以通过相应规范中定义的某个get方法来分别访问每个值
```

###### Key

* [Key](https://github.com/wanggnim/GnimSecurity/blob/master/java/src/test/wang/gnim/jdk/java_security/testKey/TestKey.java)

```
Key接口是所有密钥接口的顶层接口,一切与加密有关的操作都离不开Key接口
Key接口是所有密钥接口的顶层接口,一切与加密有关的操作都离不开Key接口
由于密钥必须在不同的实体间传输,因此所有的密钥都是可序列话的

所有的密钥都具有三个形式
算法:密钥算法, 如DES和DSA. getAlgorithm()
编码形式:密钥的外部编码形式,密钥根据标准格式(RKC#8)编码,  getEncode()
格式:已编码密钥的格式的名称,getFormat()

对称密钥顶层接口
{@link SecretKey}
		通常使用的是{@link SecretKeySpec}
DES,AES 等多种对称密码算法均可通过该接口提供,PBE接口提供PBE算法定义并继承了该接口
MAC算法实现过程中,通过SecretKey接口提供秘密秘钥

非对称密钥顶层接口
{@link PublicKey} 公钥接口
{@link PrivateKey} 私钥接口
Dh,RSA,DSA,EC等多种非对称秘钥接口均继承了这俩个接口
```

* [KeyFactory](https://github.com/wanggnim/GnimSecurity/blob/master/java/src/test/wang/gnim/jdk/java_security/testKey/TestKeyFactory.java)

```java
同{@link KeyPairGenerator} 一样,它也是用来生成密钥(公钥和私钥)的引擎类,称之为密钥工厂
按照指定的编码格式或密钥参数,提供一个用于输入和输出密钥的基础设施

从另一方面来说KeyFactory 是通过密钥规范({@link KeySpec}) 还原密钥,
与KeyFacory对应的是{@link SecretKeyFactory},用来生成秘密密钥
```

* [KeyPair](https://github.com/wanggnim/GnimSecurity/blob/master/java/src/test/wang/gnim/jdk/java_security/testKey/TestKeyPair.java)

```java
对非对称密钥的拓展,是密钥对的载体,称之为密钥对

一般是通过KeyPairGenerator#generateKeyPair()获得

keyPair只能通过构造方法初始化内部的公钥和私钥,此外不提供设置公钥和私钥的方法
```

* [KeyPairGenerator](https://github.com/wanggnim/GnimSecurity/blob/master/java/src/test/wang/gnim/jdk/java_security/testKey/TestKeyPairGenerator.java)

```
引擎类. 负责生成公钥和私钥,称之为密钥对生成器,
负责生成公钥和私钥,称之为密钥对生成器,同样是一个引擎类
如果要生成私钥可以使用 {@link KeyGenerator}
```

* [KeyStore](https://github.com/wanggnim/GnimSecurity/blob/master/java/src/test/wang/gnim/jdk/java_security/testKey/TestKeyStore.java)

```java
称为密钥库,用于管理密钥和证书的存储

密钥库类型：
 JKS,PKCS12,JCEKS 三种类型(名字不区分大小写)
 JCEKS 受美国出口限制
 PKCS12 这种类型的密钥库管理支持不是很完备,只能读取该类型的证书,却不能对其更改

KeyStore.Entry接口是一个空借口,内部没有定义代码用于类型区分 KeyStore用于管理不同类型的条目,每种类型的条目都实现Entry接口

Entry接口实现:
PrivateKeyEntry:保存私钥和相应证书链的密钥库项
SecretKeyEntry:保存私密密钥的密钥库项
TrustedCertificateEntry:保存可信的证书的密钥库项
```

###### MessageDigest

* [DigestInputStream](https://github.com/wanggnim/GnimSecurity/blob/master/java/src/test/wang/gnim/jdk/java_security/testMessageDigest/TestDigestInputStream.java)

```
消息摘要输入流通过指定的读操作完成MessageDigest 的update()方法
```

* [DigestOutputStream](https://github.com/wanggnim/GnimSecurity/blob/master/java/src/test/wang/gnim/jdk/java_security/testMessageDigest/TestDigestOutputStream.java)

```
消息摘要输出流通过指定的写操作完成MessageDigest 的update()方法
```

* [MessageDigest](https://github.com/wanggnim/GnimSecurity/blob/master/java/src/test/wang/gnim/jdk/java_security/testMessageDigest/TestMessageDigest.java)

```
实现了消息摘要算法
JAVA7 支持 MD2 MD5 SHA-1 SHA-256 SHA-284 SHA-512 六种消息摘要算法

MessageDigest DigestInputStream DigestOutputStream  Mac 均是消息认证的引擎类.
MessageDigest : 提供核心的消息摘要实现
DigestInputStream  DigestOutputStream  ： 为核心的消息摘要流实现
Mac ： 提供基于秘密密钥的安全消息摘要实现

Mac与MessageDigest没有任何依赖关系
```

* [Provider](https://github.com/wanggnim/GnimSecurity/blob/master/java/src/test/wang/gnim/jdk/java_security/TestProvider.java)

```
Provider 可能实现的服务:DSA,RSA,MD5,SHA-1等算法,密钥的生成,转换和管理设置

和 {@link Security} 一起构成了安全提供者

JCA和JCE是Java平台用于安全和加密服务的俩组API,它们并不执行任何算法,它们只是链接应用和实际算法实现程序的一组接口
软件开发商可以根据JCE接口将各种算法实现后打包成一个安全提供者.
要实现一个完整的安全结构就需要一个或者多个第三方提供的JCE产品(安全提供者们)

安全提供者抽象了俩个概念：
引擎：可以理解为操作,如加密,解密
算法：可以理解为如何加密,如何解密

Provider 可能实现的服务:
  DSA,RSA,MD5,SHA-1等算法
  密钥的生成,转换和管理设置

SUN
SunRsaSign
SunEC
SunJSSE
SunJCE
SunJGSS
SunSASL
XMLDSig
SunPCSC
SunMSCAPI
```

* [SecureRandom](https://github.com/wanggnim/GnimSecurity/blob/master/java/src/test/wang/gnim/jdk/java_security/TestSecureRandom.java)

```
安全随机数生成器 ,SHA1PRNG是其默认算法
```

* [Security](https://github.com/wanggnim/GnimSecurity/blob/master/java/src/test/wang/gnim/jdk/java_security/TestSecurity.java)

```
管理java程序中所用到的提供者
Security类是一个终态类,除了私有的构造方法外,其余均匀静态方法
```

###### Sign

* [CodeSigner](https://github.com/wanggnim/GnimSecurity/blob/master/java/src/test/wang/gnim/jdk/java_security/testSign/TestCodeSigner.java)

```
封装了代码签名者的信息,且他是不可变的,称之为代码签名 他和数字时间戳({@link Timestamp}) 紧密相连

CodeSigner类是一个终态类,可以通过其构造方法完成实例化对象：
构建CodeSigner对象 --> CodeSigner()
完成实例化对象后,就可以通过以下方法获得其属性：
返回签名者的CertPath对象 --> getSignerCertpath()
返回签名时间戳  --> getTimestamp()

注意,这里的Timestamp是java.security.Timestamp,是用做数字时间戳的Timestamp！
获得CodeSigner对象后的最重要的操作就是执行比对,CodeSigner覆盖了equals()方法.

测试指定对象与此CodeSigner对象是否相等 --> equals()
如果,传入参数不是CodeSigner类的实现,则直接返回false.
如果传入参数是CodeSigner类的实现,则比较其Timestamp和CerPath两个属性
```

* [Signature](https://github.com/wanggnim/GnimSecurity/blob/master/java/src/test/wang/gnim/jdk/java_security/testSign/TestSignature.java)

```
引擎类. 用来生成和验证数字签名.
用来生成和验证数字签名.同样是引擎类

使用Sinature对象签名数据或验证签名包括下面三个阶段
1.初始化
		初始化验证签名的公钥
		初始化签署签名的私钥
2.更新
		根绝初始化的类型,可更新要签名活验证的字节
3.签署或验证所有更新字节的签名

支持的算法
	基于:DSA
		NONEwithDSA,SHAwithDSA
	基于:RSA
		MD2whitRSA,MD5whitRSA,SHA1whitRSA,SHA256whitRSA,SHA256whitRSA,SHA384whitRSA,SHA512whitRSA
```

* [SignedObject](https://github.com/wanggnim/GnimSecurity/blob/master/java/src/test/wang/gnim/jdk/java_security/testSign/TestSignedObject.java)

```
用来创建实际运行时的对象.在检测不到这些对象的情况下,其完整性不会遭受损害

SignedObject包含另一个Serializable对象,即签名的对象及其签名.
签名对象是对原始对象的深层复制(以序列化形式),一旦生成了副本对原始对的进一步操作就不再影响该副本
```

* [Timestamp](https://github.com/wanggnim/GnimSecurity/blob/master/java/src/test/wang/gnim/jdk/java_security/testSign/TestTimestamp.java)

```
用来封装有关签署时间戳的信息.
```

###### java_security_cert

* [Certificate](https://github.com/wanggnim/GnimSecurity/blob/master/java/src/test/wang/gnim/jdk/java_security_cert/TestCertificate.java)

```
用于管理证书的抽象类 证书有很多类型,如X.509证书,PGP证书和SDSI证书
并且它们都以不同的方式存储并存储不同的信息,但却都可以通过继承Certificate类来实现
```

* [CertificateFactory](https://github.com/wanggnim/GnimSecurity/blob/master/java/src/test/wang/gnim/jdk/java_security_cert/TestCertificateFactory.java)

```
CertificateFactory是一个引擎类,称之为证书工厂,可以通过它将证书导入程序中.
```

* [CertPath](https://github.com/wanggnim/GnimSecurity/blob/master/java/src/test/wang/gnim/jdk/java_security_cert/TestCertPath.java)

```
定义了常用于所有CertPath的方法 其子类可处理不同类型的证书(x.509 PGP等)
所有的CertPath对象都包含类型,Certificate列表及其支持的一种或多种编码
```

* [CRL](https://github.com/wanggnim/GnimSecurity/blob/master/java/src/test/wang/gnim/jdk/java_security_cert/TestCRL.java)

```
证书可能会由于各种原因失效, 失效后证书将被制为无效,无效的结果就是产生CRL(证书撤销列表),
CA负责发布CRL,CRL中列出了该CA已经撤销的证书
验证证书时,首先需要查询此列表,然后再考虑接受证书的合法性
```

* [X509Certificate](https://github.com/wanggnim/GnimSecurity/blob/master/java/src/test/wang/gnim/jdk/java_security_cert/TestX509Certificate.java)

```
X509Certificate是Certificate的子类
x.509证书的抽象类,此类提供类一种访问x.509证书的所有属性的标准方式
```

* [X509CRL](https://github.com/wanggnim/GnimSecurity/blob/master/java/src/test/wang/gnim/jdk/java_security_cert/TestX509CRL.java)

```
作为CRl的子类,已标明了类型为X.509的CRl, X.509证书撤销列表(CRL)的抽象类.
CRL是标致已撤销证书的时间戳列表.
它由证书颁发机构签署,并可在公共存储库中随意使用
```

* [X509CRLEntry](https://github.com/wanggnim/GnimSecurity/blob/master/java/src/test/wang/gnim/jdk/java_security_cert/TestX509CRLEntry.java)

```
已经撤销的证书类
```

###### java_security_spec

* [AlgorithmParameterSpec](https://github.com/wanggnim/GnimSecurity/blob/master/java/src/test/wang/gnim/jdk/java_security_spec/TestAlgorithmParameterSpec.java)

```
此接口不包含任何方法或常亮.它仅用于将所有参数规范分组,并为其提供类型安全.所有参数规范否必须实现此接口
AlgorithmParameterSpec接口有很多的子接口和实现类,用于特定算法的初始化.
使用起来也很方便,只需要十一指定参数填充构造方法即可获得一个实例化对象
```

* [DESKeySpec](https://github.com/wanggnim/GnimSecurity/blob/master/java/src/test/wang/gnim/jdk/java_security_spec/TestDESKeySpec.java)

```
DESKeySpec和SecretKeySpec都是提供秘密密钥规范的实现类 DESKeySpec：指定类DES算法
SecretKeySpec：兼容所有对称加密算法

DESKeySpec有很多的同胞, DESedeKeySpec提供类三重DES加密算法的密钥规范 PBEKeySpec 提供了PBE算法的密钥规范
```

* [EncodedKeySpec](https://github.com/wanggnim/GnimSecurity/blob/master/java/src/test/wang/gnim/jdk/java_security_spec/TestEncodedKeySpec.java)

```
用编码格式来表示公钥和私钥,称之为编码密钥规范
```

* [KeySpec](https://github.com/wanggnim/GnimSecurity/blob/master/java/src/test/wang/gnim/jdk/java_security_spec/TestKeySpec.java)

```
本接口不包含任何方法或常量,它仅用于将所有密钥规范分组,并为其提供类型安全.所有密钥规范都要继承该接口
KeySpec的抽象实现类(EncodedKeySpec)构建了用于构建公钥规范和私钥规范的俩个实习
X509EncodedKeySpec用于构建公钥
PKCS8EncodedKeySpec用于构建私钥规范

SecretKeySpec接口是KeySpec的实现类,用于构建私密密钥规范
```

* [SecretKeySpec](https://github.com/wanggnim/GnimSecurity/blob/master/java/src/test/wang/gnim/jdk/java_security_spec/TestSecretKeySpec.java)

```
SecretKeySpec类是KeySpec接口的实现类,用于构建秘密密钥规范
此类仅能表示为一个字节数组并且没有任何与之相关联的密钥参数的原始密钥有用,如DES或Triple DES密钥
```

* [ModifyPolicy](https://github.com/wanggnim/GnimSecurity/blob/master/java/src/test/wang/gnim/jdk/TestModifyPolicy.java)

```

```
