category: 
- security
title: javax_crypto
---
## Cipher
Cipher类是一个引擎类,它需要通过getlnstanceo工厂方法来实例化对象.

我们可以通过指定转换模式的方式获得实例化对象方法如下所示`getlnstance(string transformation)` 返回指定转换的Cipher对象,也可以在制定转换模式的同时制定该转换模式的提供者 getlnstance(string transformation, Providerprovider) getlnstance(stringtransformation, String provider)`

为加密解密提供密码功能,它构成了`Java Cryptographic Extension(JCE)` 框架核心.

在java.security包中 只完成了密钥的处理 并未完成加密与解密的操作. 这些核心 操作需要通过Cipher类来实现.

通过以下方法可借助于证书，获得其公钥来完成加密和解密操作： 
* 用取自给定证书的公钥初始化此Cipher对象 init()
* 用取自给定证书的公钥和随机源初始化此Cipher对象 init() 

如果需要多次更新待加密(解密)的数据可使用如下方法。
最为常用的是通过输入给定位的字节数组完成更新： 
* 继续多部分加密或解密操作(具体取决于此Cipher对象的初始化方式)，
* 以处理其他数据部分update() 或者通过偏移量的方式完成更新，方法如下所示：
* 继续多部分加密或解密操作(具体取决于此Cipher对象的初始化方式)，以处理其他数据部分 update()
* 另外一种方式就是将更新结果输出至参数中，方法如下所示：
* 继续多部分加密或解密操作(具体取决于此Cipher对象的初始化方式)，以处理其他数据部分 update()

当然，我们也可以使用如下缓冲方式： 继续多部分加密或解密操作(具体取决于此Cipher对象的初始化方式)，以处理其他数据部分update() 完成上述数据更新后，直接执行如下方法： 结束多部分加密和解密操作(具体取决于此Cipher对象的初始化方式)dofinal() 如果，加密(解密)操作不需要多次更新数据可以直接执行如下方法： 按单部分操作加密或解密数据，或者结束一个多部分操作
dofinal()

或按以下偏移量的方式完成操作： 按单部分操作加密或解密数据，或者结束一个多部分操作 dofinal()以下方式将操作后的结果存于给定的参数中，与上述方式大同小异： 结束多部分加密和解密操作(具体取决于此Cipher对象的初始化方式) dofinal() 与上述方法不同的是，以下方法可用于多部分操作，并操作结果存于给定参数中：
按单部分操作加密或解密数据，或者结束一个多部分操作 dofinal() 按单部分操作加密或解密数据，或者结束一个多部分操作
dofinal() 以下方法提供了一种基于缓冲的处理方式： 按单部分操作加密或解密数据，或者结束一个多部分操作 dofinal()
除了完成数据的加密与解密，Cipher类还提供了对密钥的包装与解包。 我们先来了解一下与密钥包装有关的常量
用于将Cipher对象初始化为密钥包装模式的常量 init WAR_MODE

这一常量需要在进行Cipher对象初始化使用，给出如下示例代码： init() 初始化 在此之后我们就可以执行包装操作，可使用如下方法：
包装密钥 wrap() 解包操作需要如下常量执行初始化 用于将Cipher初始化为密钥解包模式的常量 init WAR_MODE
这个常量同样需要在初始化中执行，给出如下示例代码： init() 在此之后才能执行解包操作。 我们先看一下解包方法： 解包一个以前的密钥
unwrap() 上述方法中的参数int wrappeKeyType需要使用如下常量： 用于表示要解包的密钥为“私钥”的常量 int
PRIVATE_KEY 用于表示要解包的密钥为“公钥”的常量 PUBLIC_KEY 用于表示要解包的密钥为“私密密钥”的常量
SECRET_KEY 在执行包装操作时使用的是私钥就使用私钥常量，依次对应。
如果读者对第2章中有关分组加密工作模式的内容还有印象，应该记得文中曾提到过初始化向量。我们可以通过如下方法获得：
返回新缓冲区中的初始化向量(IV) getIV() 通常，我们有必要通过如下方法来获悉当前转换模式所支持的密钥长度，方法如下所示：
根据所安装的JCB仲裁策略文件，返回指定转换的最大密钥长度 getMaxAllowedKeyLength()
分组加密中，每一组都有固定的长度，也称为块，以下方法可以获得相应的块大小： 返回块的大小(以字节为单位) getBlockSize()
以下方法获得输出缓冲区字节长度：
根据给定的输入长度inputLen(以字节单位)，返回保存下一个update或doFinal操作结果所需要的输出缓冲区长度(以字节为单位)
getBlockSize() 我们也可以通过如下方法获得该Cipher对象的算法参数相关信息：
根据仲裁策略文件，返回包含最大Cipher参数值的AlgorithmParameterSpec对象
getMaxAllowedKeyLength() 返回此Cipher使用的参数 getParameters() 此外，Cipher
类还提供以下方法： 返回此cipher使用的参数 getParameters() Cipher类作为一个引擎类，同样提供如下方法：
返回cipher对象的提供者 getProvider() 返回此Cipher对象的算法名称 getAlgorithm() 
```java
// 完成密钥的包装和解包操作
// 实例化KeyGenerator对象,并指定DES算法
KeyGenerator kg = KeyGenerator.getInstance("DES");
// 生成SecretKey对象
SecretKey sk = kg.generateKey();
// 实例化Cipher对象 transformation 的格式是 算法/工作模式/填充模式
Cipher c = Cipher.getInstance("DES/CBC/PKCS5Padding");
// 接下来执行包装操作
// 初始化Cipher对象,用于包装
c.init(Cipher.WRAP_MODE, sk);
// 包装私密密钥
byte[] k = c.wrap(sk);// 得到字节数组k后,可以将其传递给需要解包的一方
// 初始化Cipher对象,用于解包
c.init(Cipher.UNWRAP_MODE, sk);
// 解包私密密钥
Key key = c.unwrap(k, "DES", Cipher.SECRET_KEY);// 加密 用密钥初始化加密模式
// 初始化Cipher对象,用于加密操作
c.init(Cipher.ENCRYPT_MODE, sk);
// 加密
byte[] input = c.doFinal("DES DATA".getBytes());// 解密操作与之相对于 用密钥初始化为解密模式
// 初始化Cipher对象,用于解密操作
c.init(Cipher.DECRYPT_MODE, sk);
// 解密
byte[] output = c.doFinal(input);
加入SecureRandom参数来初始化 c.init(Cipher.DECRYPT_MODE, key, random);
```

## CipherInputStream
Ciper的拓展,称为密钥拓展流
```java
// 使用密钥输入流解密文件中数据// 实例化KeyGenerator对象,指定DES算法
KeyGenerator kg = KeyGenerator.getInstance("DES");
// 生成SecretKey对象
SecretKey sk = kg.generateKey();
// 实例化Cipher对象
Cipher c = Cipher.getInstance("DES");// 接着从文件中读入数据,然后进行解密操作
c.init(Cipher.DECRYPT_MODE, sk);
// 实例化CipherInputStream
CipherInputStream cis = new CipherInputStream(new FileInputStream(new File("secret")), c);
// 使用DataInputStream对象包装CipherInputStream对象
DataInputStream dis = new DataInputStream(cis);
// 读出解密后的数据
String output = dis.readUTF();
dis.close();
```

## CipherOutputStream
```java
// 密钥输出流加密操作
// 实例化KeyGenerator对象,指定DES算法
KeyGenerator kg = KeyGenerator.getInstance("DES");
// 生成SecretKey对象
SecretKey sk = kg.generateKey();
// 实例化Cipher对象
Cipher c = Cipher.getInstance("DES");// 初始化Cipher对象,用于加密操作
c.init(Cipher.ENCRYPT_MODE, sk);
// 待加密的原始数据
String input = "1234567890";
// 实例化CipherOutputStream对象
CipherOutputStream cos = new CipherOutputStream(new FileOutputStream(new File("secret")), c);
// 使用DataOutputStream对象包装CipherOutputStream对象
DataOutputStream dos = new DataOutputStream(cos);
// 向输出流写待加密的数据
dos.writeUTF(input);
// 清空流
dos.flush();
dos.close();
```
密钥输出流
## KeyAgreement
KeyAgreement类提供密钥协定协议的功能，它同样是一个引擎类。我们称它为密钥协定，将在DH算法实现中使用到它。此类提供密钥协定

与我们所熟悉的其他引擎类一样,KeyAgreement类需要通过getlnstce)工厂方法获得实例化对象：
* 返回实观指定密钥协定算法的KeyAgreement对象 public static KeyAgreement getlnstance(string algorithat) 
* 返回实现指定密钥协定算法的KeyA9reement对象 public static KeyAgreement getlnstance(String algorithat.Pravider provider)
* 返回实现指定密钥协定算法的KeyAgreement对象 public static KeyAgreement getlnstance(String algorittua.String provider
	 
算法生成器共有两种初始化方式,与算法无关的方式或特定于算法的方式。 获得实例化对象后,需要执行以下初始化方法:
* init(Keykey,Algorithmparameterspec params) 
* 或者，基于上述方式，再加入安全随机数参数，方法如下所示：key,Algorithmparameterspec params , SecureRanRandom random)
	 
除上述方式外,我们也可以仅使用密钥和安全随机数两个参数完成初始化操作，方法如下所示： 用给定密钥和随机源初始化此KeyAgreement用给定密钥执行此KeyAreement的下一个阶段,给定密钥是从此密钥协定所涉及的其他某个参与者那里接收的 public Key dophase(Key key, boolean lastphase) 最后，我们可以获得共享秘密密钥： 生成共亭秘密密钥并在新的缓冲区中返回它 public bytel generategecreto /生成共享秘密密钥，并将其放入缓冲区 sharedsecret，从offer(包括)开始
创建共享秘密密钥并将其作为指定算法的SecretKey对象 public SecretKey generatesecret(Stringalgorithm)

此外 KeyAgreement类还提供了以下常用方法： /返回此密钥协定对象的提供者 public Provider getprovider()返回此密钥协定对象的算法名称 public String getALgorithm()
```java
// 实例化KeyPairGenerator对象,并指定DH算法
KeyPairGenerator kpg = KeyPairGenerator.getInstance("DH");
// 生成KeyPair对象kp1
KeyPair kp1 = kpg.generateKeyPair();
// 生成KeyPair对象kp2
KeyPair kp2 = kpg.generateKeyPair();
// 实例化KeyAgreement对象
KeyAgreement ka = KeyAgreement.getInstance(kpg.getAlgorithm());
// 初始化KeyAgreement对象
ka.init(kp2.getPrivate());
// 执行计划
ka.doPhase(kp1.getPublic(), true);
// 生成SecretKey对象
SecretKey sk = ka.generateSecret("DES");	}
}
```

## KeyGenerator
KeyGenerator类与KeypairGenerato类相似,KeyGenerato类用来生成私密密钥,我们称之为私密密钥生成器.

Java7版本中提供了Blowfish、AES、DES和DESede等多种对称加密算法实现,以及HmacMD5、HmacSHA1和HmacSHA256等多种安全消息摘要算法实现

与KeypairGenerator类相似，KeyGenerator类通过如下方法获得实例化对象：
返回生成指定算法的秘密密钥的KeyGenerator对象public static final KeyGenerator
getlnstance(String algorithm)另一种方式就是指定算法名称的同时指定该算法的提供者，方法如下所示：
返回生成指定算法的秘密密钥的KeyGenerator对象 public static final KeyGenerator
getlnatance(String algorithm, Provider provider)
返回生成指定算法的秘密密钥的KeyGenerator对象public static final reyGenerator
getlnstance(String algorithm, Provider provider)
KeyGenerator对象可重复使用，也就是说，在生成密钥后，可以重复使用同一个KeyGenerator对象来生成更多的密钥。
生成密钥的方式有两种：与算法无关的方式和特定于算法的方式。这一点与KeypairGenemtor类生成密钥方式相类似。两者
之间的唯一不同是对象的初始化：

与算法无关的初始化。所有密钥生成器都具有密钥大小和随机源的概念。KeyGenerator类中有一个init()方法，它带有
这两个通用共享类型的参数，还有一个只带有keysize参数的init()方法，它使用具有最高优先级的已安装提供者的SecureRandom
实现作为随机源(如果已安装的提供者都不提供SecureRandom实现,则使用系统提供的随机源)。KeyGenerator类还提供一个只带随机
源参数的init()的方法。因为调用上述与算法无关的init()方法时未指定其他参数，所以由提供者决定如何处理要与每个密钥关联的特
定于算法的参数(如果有)。
特定于算法的初始化。在已经存在特定于算法的参数集的情况下，有两个带Algorithmparameterspec参数的init()方法。其中一个
方法还有一个SecureRandom参数，而另一个方法将具有高优先级的已安装提供者的SecureRandom实现作为随机源 (如果安装的提供者
都不提供SecureRandom实现，则使用系统提供的随机源)。如果客户端没有显式地初始化KeyGenerator (通过调用init()方法)，
那么每个提供者都必须提供 (并记录) 默认初始化。与算法无关的初始化方法如下： 初始化此KeyGeneratorpublic final void
init(secureRandom random) 初始化此KeyGenerator使其具有确定的密钥大小public final void
init(int keysize) 使用用户提供的随机源初始化此KeyGenerator，使其具有确定的密钥大小public final void
init(int keysize, SecureRandom random)

特定于算法的初始化方法如下： 用指定参敛集初始化此KeyGeneratorpublic final void
init(Algorithmparameterspec params) 用指定参数集和用户提供的随机源初始化此KeyGeneratorpublic
final void init(Algorithmparameterspec params, SecureRandom random)

完成初始化操作后 我们就可以通过以下方法获得秘密密钥： 生成一个SecretKey对象public final SecretKey
generateKey() 与其他引擎类一样,KeyGenerator类提供如下两种方法: 返回此秘密密钥生成器对象的算法名称public
final String getAlgorithm()返回此秘密密钥生成器对象的提供者public final String
getAlgorithm()
```java
// 实例化KeyGenerator对象,并指定HmacMD5算法
KeyGenerator kg = KeyGenerator.getInstance("HmacMD5");
// 生成SecretKey对象
SecretKey sk = kg.generateKey();
```

## Mac
Mac属于消息摘要的一种,但它不同于一般消息摘要(如Message Digest提供的消息摘要实现),仅通过输入数据无法活的吧消息摘要,必须有一个由发送方和接收方共享的秘密密钥才能生成最终的消息摘要——安全消息摘要.安全消息摘要又称消息认证(鉴别)码.

Mac与MessageDigest绝大多数方法相同，我们可以通过以下方法获得它的实例：
返回实现指定摘要算法的Mac对象 
getInstance()
或者，指定算法名称的同时指定该算法的提供者
返回实现指定摘要算法的Mac对象
getInstance()
返回实现指定摘要算法的Mac对象
getInstance()
目前，Mac类支持HmacMD5 HmacSHA1 HmacSHA256 HmacSHA384 HmacSHA512 5种消息摘要算法。
在获得Mac实现化对象后，需要通过给的密钥对Mac对象初始化，方法如下：
用给定的密钥和算法参数初始化此Mac
init()
用给定的密钥和算法参数初始化此Mac
init()
这里的密钥Key指的是秘密密钥，请使用该密钥作为init()方法的参数。
Mac类更新摘要方法与MessageDigest类相同，方法如下：
使用指定的字节更新摘要。
update()
使用指定的字节数组更新摘要。
update()
上述方法传入参数不同，前一个传入的是字节，后一个传入的是字节数组。
我们也可以通过输入偏移量的方式做更新操作，方法如下：
使用指定的字节数组，从指定的偏移量开始更新摘要
update()
当然，我们还可以使用缓冲方式，方法如下：
使用指定的字节缓冲更新摘要
update()
与MessageDigest类相同，更新摘要信息，其参数可以是更新一个字节、一个字节数组甚至是字节数组中的某一段偏移量，也可以是字节缓冲对象。
这些方法的调用顺序不受限制，在向摘要中增加所需数据时可以多次调用。在完成摘要更新后，我们可以通过以下方法完成摘要操作：
完成摘要操作
doFinal()
使用指定的字节数组对摘要进行组后更新，然后完成摘要计算，返回消息摘要字节数组
doFinal()
完成摘要操作，按指定的偏移量将摘要信息保存在字节数组中
doFinal()
 
与MessageDigest类相同，Mac类也有重置方法：
重置摘要以供再次使用
reset()
执行该重置方法等同于创建一个新的Mac实例化对象。
除了上述方法外，还常用到以下几个方法：
返回以字节为单位的摘要长度，如果提供者不支持此操作并且实现是不可复制的，则返回0
返回算法名称，如HmacMD5
getAlgorithm()
返回此信息摘要对象的提供者
getprovider()
```java
// 待做安全消息摘要的原始信息
byte[] input = "MAC".getBytes();
// 初始化KeyGenerator对象,使用HmacMD5算法
KeyGenerator kg = KeyGenerator.getInstance("HmacMD5");
// 构建SecretKey对象
SecretKey sk = kg.generateKey();
// 构建MAC对象
Mac mac = Mac.getInstance(sk.getAlgorithm());
// 初始化Mac对象
mac.init(sk);
// 获得经过安全消息摘要后的信息
byte[] output = mac.doFinal(input);
	}
}
```

## SealedObject
ScaledObject使程序员能够用加密算法创建对象并保护其机密性.

在给定任何Serializable对象的情况下,程序员可以序列化格式(即深层复制)封装原始对象的SealedObject并使用类似于DES的加密算法密封(加密)其序列化的内容,保护其机密性.
加密的内容以后可以解密和烦序列话,生成原始对象已密封的原始对象可以使用以下俩种方式恢复
1. 使用采用Cipher对象的getObject)方法.
此方法需要一个完全初始化的Cipher对象,用相同的用来蜜蜂对象的算法,密钥,填充方案等进行初始化.这样做的好处是解封密封 对象的一方不需要知道解密密钥. 例如一方用所需的解密密钥初始化Cipher对象之后,
它就会将Cipher对象移交给以后要解封密封对象的另一方
2. 使用采用Key对象的getObject()方法.
在此方法中getObject方法创建一个用于适当解密算法的Cipher对象,并用给定的解密密钥和存储在密封对象中的算法参数 (如果有)对其进行初始化.这样做的好处是解封此对象的一方不需要跟踪用来密封该对象的参数 (如IV 、 初始化向量).
```java
// 待加密的字符串对象
String input = "SealedObject";
// 实例化KeyGenerator对象,并使用DES算法
KeyGenerator kg = KeyGenerator.getInstance("DES");
// 创建秘密密钥
SecretKey key = kg.generateKey();
// 实例化用于加密的Cipher对象c1
Cipher c1 = Cipher.getInstance(key.getAlgorithm());
// 初始化
c1.init(Cipher.ENCRYPT_MODE, key);
// 构建SealedObject对象
SealedObject so = new SealedObject(input, c1);
// 实例化用于解密的Cipher对象c2
Cipher c2 = Cipher.getInstance(key.getAlgorithm());
// 初始化
c2.init(Cipher.DECRYPT_MODE, key);
// 获得解密后的字符串对象
String output = (String)so.getObject(c2);
```

## SecretKeyFactory
SecretKe factor类同样属于引擎类,与Kefactory类相对应,它用于产生秘密密钥,我们称之为秘密密钥工厂.
此类表示秘密密钥的工厂信任管理库及构建安全基于HTTPS的加密网络通信实现的知识

既然SecretKeyFactory类也是一 个引擎类,同样需要通过getlnstance()工厂方法来实例化对象。
我们可以通过制定算法名称的方式获得秘密密钥实例化对象，方法如下：
返回转换指定算法的秘密密钥的SecretKeyFactory对象
public final static SecretXeyPactory getxnstance(String algorithm)
或者，指定算法名称的同时制定该算法的提供者，方法如下： 
返回转换指定算法的秘迷密钥的SecretKeyFactory对象
public final static SecretReyfactory getlnstance(String algorlttmt, Provider provider)
返回转换指定算法的秘迷密钥的SecretKeyFactory对象
public final static SecretReyfactory getlnstance(String algorlttmt, sting provider)
算法生成器共有两种初始化方式：与算法无关的方式或特定于算法的方式。
得利SecretKeyFactory实例化对象后，我们就可以通过以下方法来生成秘密密钥：
根据提供的密钥规范(密钥材料) 生成SecretKey对象
public final SecretKey generatesecret(Reyspec keyspec)
SecretKeyfactory类也是一个引擎类同样需要通过getlnstanceo工厂方法来实例化对象
与其他引擎类一 样，SecretKeyFactory类提供以下两种方法
返回此秘密密钥工厂对象的提供者
public final provider getprovider( )
返回此秘密密钥工厂对象的算法名称
 public final String getAlgorithm()
 
```java
// 通过以下代码获得私密迷药的迷药编码字节数组
// 实例化KeyGenerator对象,并指定DES算法
KeyGenerator kg = KeyGenerator.getInstance("DES");
// 生成SecretKey
SecretKey sk = kg.generateKey();
// 获得私密密钥的密钥编码字节数组
byte[] key = sk.getEncoded();// 得到上述密钥编码字节数组后,我们就可以还原其秘密密钥的对象
// 由获得的密钥编码字节数组构建DESKeySpec对象
DESKeySpec dks = new DESKeySpec(key);
// 实例化SecreatKeyFactory对象
SecretKeyFactory kf = SecretKeyFactory.getInstance("DES");
// 生成SecretKey对象
SecretKey sk1 = kf.generateSecret(dks);
```

