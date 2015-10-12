category: 
- java
tag:
- java加密解密
title: java.security  API
---
## AlgorithmParameterGenerator
```java
AlgorithmParameterGenerator apg = AlgorithmParameterGenerator.getInstance("DES"));
// 与算法无关的初始化,所有参数生成器都共享大小概念和一个随机源
// 特定于算法的参数生成值默认为某些标准值,除非他们可以从指定的大小派生
apg.init(size, random);

apg.init(56);

//实例化后就可以生成算法参数对象了
AlgorithmParameters ap = apg.generateParameters();

// 获得字节数组
byte[] b = ap.getEncoded();
System.out.println(new BigInteger(b).toString());

// 特定于算法的初始化,使用特定算法的语意初始化参数生成器对象,这些语意由特定于算法的参数生成值集合表示.
apg.init(genParamSpec);
apg.init(genParamSpec, random);
```
引擎类,用于生成某个特定算法中使用的参数集合.{@link AlgorithmParameters} 生成的参数

## AlgorithmParameters
提供密码参数的不透明表示`AlgorithmParameters`.`AlgorithmParameters`是一个引擎类,提供密码参数的不透明表示`AlgorithmParameterGenerator`可以通过该引擎类生成不透明:不可以直接访问各参数域,只能得到与参数集相关联的算法名及该参数集的某类编码透明 :用户可以通过相应规范中定义的某个get方法来分别访问每个值
```java
// 实例化AlgorithmParameters 并指定DES算法
AlgorithmParameters ap = AlgorithmParameters.getInstance("DES"));
// 使用BigInteger生成字节数组参数
ap.init(new BigInteger("19050619766489163472469").toByteArray());
// 获取参数字节数组
byte[] b = ap.getEncoded();
// 打印经过BigInteger处理后的字符串,会得到与19050619766489163472469 相同的字符串
System.out.println("BigInteger Encoded : " + new BigInteger(b).toString());
// 获取基本编码格式的参数
System.out.println("Hex 	   Encoded : " + Hex.toHexString(ap.getEncoded()));

// 完成初始化后 获得参数对象的规范
// ap.getParameterSpec(AlgorithmParameterSpec.class);
// 使用 AlgorithmParameterSpec指定的参数初始化
// ap.init(paramSpec);
// 根据参数的基本解码格式导入指定的参数字节数组并对其解码
// ap.init(new byte[]{});
// 根据指定的解码方案从参数字节数组导入参数并对其解码
// ap.init(params, format);

// 注意的是 AlgorithmParameters对象只能被初始化一次,不能复用
// ap.init(new byte[]{});

// 获取此参数对象的算法的名称
System.out.println("Algorithm : " + ap.getAlgorithm());
// 获取次参数对象的提供者
System.out.println("Provider : " + ap.getProvider());

// 获取指定编码格式的参数
ap.getEncoded("");
```


## CodeSigner
封装了代码签名者的信息,且他是不可变的,称之为代码签名 他和数字时间戳({@link Timestamp}) 紧密相连CodeSigner类是一个终态类,可以通过其构造方法完成实例化对象：
* 构建CodeSigner对象 --> CodeSigner()完成实例化对象后,就可以通过以下方法获得其属性：
* 返回签名者的CertPath对象 --> getSignerCertpath()
* 返回签名时间戳  --> getTimestamp()注意,这里的Timestamp是java.security.Timestamp,是用做数字时间戳的Timestamp！

获得CodeSigner对象后的最重要的操作就是执行比对,CodeSigner覆盖了equals()方法.测试指定对象与此CodeSigner对象是否相等-->equals().如果,传入参数不是CodeSigner类的实现,则直接返回false.如果传入参数是CodeSigner类的实现,则比较其Timestamp和CerPath两个属性
```java
// 构建CertificateFactory对象,并指定证书类型为x.509
CertificateFactory cf = CertificateFactory.getInstance("509");
// 生成Certificate对象
CertPath cp = cf.generateCertPath(new FileInputStream(""));
// 实例化时间戳
Timestamp t = new Timestamp(new Date(), cp);
// 实例化CodeSinger对象
CodeSigner cs = new CodeSigner(cp, t);
// 对比
boolean result = cs.equals(new CodeSigner(cp, t));
```

## DigestInputStream
消息摘要输入流通过指定的读操作完成MessageDigest 的update()方法
```java
MessageDigest md = MessageDigest.getInstance("MD5");
try(FileInputStream in = new FileInputStream(new File(fileName));
DigestInputStream dis = new DigestInputStream(in, md)) {

// 更新MessageDigest对象,将其与dis相关
dis.setMessageDigest(MessageDigest.getInstance("MD2"));

// 读取字节 并更新摘要. read会调用MessageDigest的update()方法完成摘要更新之后通过nmd的digest完成摘要操作
dis.read();
//  获取流中相关的MessageDigest
MessageDigest nmd = dis.getMessageDigest();
nmd.digest();

//关闭DigestInputStream摘要功能,则该流就变成一般的流
dis.on(false);

// 待做消息摘要操作的原始信息
byte[] input = "md5".getBytes();
// 使用MD5算法初始化MessageDigest对象
MessageDigest md = MessageDigest.getInstance("MD5");

try(DigestInputStream dis = new DigestInputStream(
new ByteArrayInputStream(input), md)) {
	
dis.read(input, 0, input.length);
MessageDigest nmd = dis.getMessageDigest();
// 获得摘要信息
byte[] output = nmd.digest();
System.out.println(output);
```

## DigestOutputStream
消息摘要输出流通过指定的写操作完成MessageDigest 的update()方法. 基本上和DigestInputStream类似
```java
// 待做消息摘要操作的原始信息
byte[] input = "md5".getBytes();
// 使用MD5算法初始化MessageDigest对象
MessageDigest md = MessageDigest.getInstance("MD5");

try (DigestOutputStream dis = new DigestOutputStream(
new ByteArrayOutputStream(), md)) {
	// 流输出
	dis.write(input, 0, input.length);
	MessageDigest nmd = dis.getMessageDigest();
	// 获得摘要信息
	byte[] output = nmd.digest();
	System.out.println(output);

	dis.flush();
}
```

## Key
Key接口是所有密钥接口的顶层接口,一切与加密有关的操作都离不开Key接口. 由于密钥必须在不同的实体间传输,因此所有的密钥都是可序列话的.

所有的密钥都具有三个形式
* 算法:密钥算法, 如DES和DSA. getAlgorithm()
* 编码形式:密钥的外部编码形式,密钥根据标准格式(RKC#8)编码,  getEncode()
* 格式:已编码密钥的格式的名称,getFormat()

对称密钥顶层接口 SecretKey}. 通常使用的是{@link SecretKeySpec}DES,AES 等多种对称密码算法均可通过该接口提供,PBE接口提供PBE算法定义并继承了该接口.MAC算法实现过程中,通过SecretKey接口提供秘密秘钥

非对称密钥顶层接口
* PublicKey 公钥接口
* PrivateKey 私钥接口

Dh,RSA,DSA,EC等多种非对称秘钥接口均继承了这俩个接口

## KeyFactory
同{@link KeyPairGenerator} 一样,它也是用来生成密钥(公钥和私钥)的引擎类,称之为密钥工厂.按照指定的编码格式或密钥参数,提供一个用于输入和输出密钥的基础设施从另一方面来说KeyFactory 是通过密钥规范({@link KeySpec}) 还原密钥,与KeyFacory对应的是{@link SecretKeyFactory},用来生成秘密密钥
```java
KeyPairGenerator generator = KeyPairGenerator.getInstance(Algorithm.RSA.name());
generator.initialize(1024);
KeyPair kp = generator.genKeyPair();
// 获得私钥密钥字节数组.使用过程中该密钥以此种形式保存传递给另一方
byte[] privateBytes = kp.getPrivate().getEncoded();
// 由私钥密钥字节数组获得密钥规范
PKCS8EncodedKeySpec pkcs = new PKCS8EncodedKeySpec(privateBytes);

// 实例化密钥工厂,并指定RSA算法
KeyFactory factory = KeyFactory.getInstance(Algorithm.RSA.name());
// 根据提供的密钥规范生成publicKey和privateKey
PrivateKey pk = factory.generatePrivate(pkcs);
PublicKey k = factory.generatePublic(pkcs);

// 将提供者可能未知或不受信任的密钥对象转换成次密钥工厂对应的密钥对象
factory.translateKey(pk);

// 返回给定对象的规范
factory.getKeySpec(pk, PKCS8EncodedKeySpec.class);
```

## KeyPair
对非对称密钥的拓展,是密钥对的载体,称之为密钥对一般是通过KeyPairGenerator#generateKeyPair()获得keyPair只能通过构造方法初始化内部的公钥和私钥,此外不提供设置公钥和私钥的方法

## KeyPairGenerator
引擎类. 负责生成公钥和私钥,称之为密钥对生成器,负责生成公钥和私钥,称之为密钥对生成器,同样是一个引擎类如果要生成私钥可以使用 KeyGenerator
```java
// 生成指定算法的公钥私钥密钥对的KeyPairGenerator对象
KeyPairGenerator kg = KeyPairGenerator.getInstance("DH");

//与算法无关的初始化,所有密钥对生成器都共享大小概念和一个随机源.
// 初始化给定密钥对大小的密钥对生成器,使用默认的参数集合,并使用以最高优先级安装的提供者的SecureRandom作为随机源
// keysize是用来控制密钥长度的,单位为位
kg.initialize(1024); // DH算法要求长度为64的倍数 长度为512-1024

KeyPairGenerator kg = KeyPairGenerator.getInstance("DH");

// 特定算法的初始化,所有密钥对生成器都共享大小概念和一个随机源.
// 初始化给定密钥对大小的密钥对生成器,使用默认的参数集合,并使用以最高优先级安装的提供者的SecureRandom作为随机源
kg.initialize(params);

KeyPairGenerator kg = KeyPairGenerator.getInstance("DH");
kg.initialize(1024); 
// generateKeyPair()由具体的密钥对生成器提供者实现
KeyPair kp = kg.generateKeyPair();

```

## KeyStore
称为密钥库,用于管理密钥和证书的存储

密钥库类型：
* JKS,PKCS12,JCEKS 三种类型(名字不区分大小写)
* JCEKS 受美国出口限制
* PKCS12 这种类型的密钥库管理支持不是很完备,只能读取该类型的证书,却不能对其更改

KeyStore.Entry接口是一个空借口,内部没有定义代码用于类型区分 KeyStore用于管理不同类型的条目,每种类型的条目都实现Entry接口

Entry接口实现:
* PrivateKeyEntry:保存私钥和相应证书链的密钥库项
* SecretKeyEntry:保存私密密钥的密钥库项
* TrustedCertificateEntry:保存可信的证书的密钥库项
```java
KeyStoreException, NoSuchAlgorithmException, CertificateException,
UnrecoverableKeyException {
// 加载密钥文件
try (FileInputStream in = new FileInputStream("")) {
// 实例化KeyStore对象
// 根据Java安全属性文件中指定的默认密钥库类型, 如果不存在此类属性则返回字符串 jks
KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());

// 加载密钥库,使用密码"password"  (从给定输入流中加载和存储密钥库)
ks.load(in, "password".toCharArray());

// 得到密钥对象后,可以获得其别名对应的私钥 ( 获得别名为"alias"所对应的私钥)
PrivateKey pk = (PrivateKey) ks.getKey("alias", "password".toCharArray());

// 获得私钥项
//PrivateKeyEntry pke = (KeyStore.PrivateKeyEntry) ks.getEntry( "alias", "password".toCharArray());
PrivateKeyEntry pke = null;
// 获得私钥
PrivateKey privateKey = pke.getPrivateKey();

// 返回此密钥库的类型
ks.getType();

// 将此密钥存储到给定输出流,冰雨给定密码保护其完整性
 ks.store(new FileOutputStream(""), "password".toCharArray());
 
// 获取密钥库中的条目数
ks.size();

// 在密钥库中,密钥和证书都是通过别名进行组织的
// 通过以下方法获得密钥库的别名列表
ks.aliases();

// 确定给定的别名是否在当前密钥库中
ks.containsAlias("");

// 返回与给定别名关联的密钥,并用给定密码来恢复他
 ks.getKey("alias", "password".toCharArray());

// 返回给定别名关联的证书
Certificate cert = ks.getCertificate("alias");

// 返回给定别名关联的证书链
Certificate[] chain = ks.getCertificateChain("alias");

// 返回证书给定证书匹配的第一个密钥库条目的别名
 ks.getCertificateAlias(cert);

// 返回给定别名表示的条目的创建日期
ks.getCreationDate("alias");

// 删除次密钥库中给定别名标识的条目
ks.deleteEntry("");

// 将给定密钥(受保护的)分配给指定的别名
 ks.setKeyEntry("alias", privateKey.getEncoded(), chain);

// 将给定的密钥分配给给定的别名，并用给定密码保护他
 ks.setKeyEntry("alias", privateKey, "password".toCharArray(), chain);

// 将给定可信证书分配给给定别名
 ks.setCertificateEntry("alias", cert);

// 构造带私钥和相应证书链的私钥项
PrivateKeyEntry pe = new KeyStore.PrivateKeyEntry(privateKey, null);

// 从此死要内部的证书链数组首位中获取证书
pe.getCertificate();

// 获取PrivateKey对象
pe.getPrivateKey();
SecretKeyEntry ske = new KeyStore.SecretKeyEntry(
(SecretKey) privateKey);

// 秘密密钥项的主要作用是保护秘密密钥,通过如下方法获取秘密密钥
ske.getSecretKey();

// 用可信证书构造信任证书项
TrustedCertificateEntry tcf = new KeyStore.TrustedCertificateEntry(
(Certificate) privateKey);

// 从信任证书项获取可信证书
tcf.getTrustedCertificate();
```

## MessageDigest
实现了消息摘要算法: JAVA7 支持 MD2 MD5 SHA-1 SHA-256 SHA-284 SHA-512 六种消息摘要算法

* MessageDigest DigestInputStream DigestOutputStream  Mac 均是消息认证的引擎类.
* MessageDigest : 提供核心的消息摘要实现
* DigestInputStream  DigestOutputStream  ： 为核心的消息摘要流实现
* Mac ： 提供基于秘密密钥的安全消息摘要实现

Mac与MessageDigest没有任何依赖关系
```java
// 通过指定算法获取MessageDigest 实例
MessageDigest.getInstance("MD5");
//MessageDigest.getInstance("MD5", "SunJCE");// 使用SunJCE安全提供者提供的MD5 消息摘要
//MessageDigest.getInstance("MD5", new SunJCE());// 使用SunJCE安全提供者提供的MD5 消息摘要
MessageDigest md = MessageDigest.getInstance("MD5");

System.out.println(md.getAlgorithm());
System.out.println(md.getProvider());
System.out.println(md.getDigestLength());

// 使用指定字节更新摘要
md.update((byte)1);

// 使用指定字节数组更新摘要
md.update(new byte[]{(byte)1});

//使用指定字节数组和偏移量更新摘要
md.update(new byte[]{(byte)1}, 1, 2);

//使用指定字节缓冲模式更新摘要
md.update((byte)1);

//完成摘要更新后 完成摘要计算, 返回摘要数组
byte[] digest = md.digest();
	
// 直接使用字节数组进行摘要更新同时完成摘要计算, 返回摘要数组
md.digest(new byte[]{(byte)1});

//判断俩个摘要是否相等
byte[] d1 = md.digest(new byte[]{(byte)1});
byte[] d2 = md.digest(new byte[]{(byte)2});
boolean isEqual = MessageDigest.isEqual(d1, d2);
Assert.assertEquals(false, isEqual);

d1 = md.digest(new byte[]{(byte)1});
d2 = md.digest(new byte[]{(byte)1});
isEqual = MessageDigest.isEqual(d1, d2);
Assert.assertEquals(true, isEqual);

// 重置摘要以供再次使用,执行该操作等同于创建一个新的MessageDigest实例
md.reset();

md.clone(); // 如果实现是可复制的,则返回一个副本
	
byte[] input = "sha".getBytes();
MessageDigest sha = MessageDigest.getInstance("SHA");
sha.update(input);
System.out.println(sha.digest());
```

## Provider
Provider 可能实现的服务:DSA,RSA,MD5,SHA-1等算法,密钥的生成,转换和管理设置和 Security} 一起构成了安全提供者

JCA和JCE是Java平台用于安全和加密服务的俩组API,它们并不执行任何算法,它们只是链接应用和实际算法实现程序的一组接口

软件开发商可以根据JCE接口将各种算法实现后打包成一个安全提供者.要实现一个完整的安全结构就需要一个或者多个第三方提供的JCE产品(安全提供者们)

安全提供者抽象了俩个概念：
* 引擎：可以理解为操作,如加密,解密
* 算法：可以理解为如何加密,如何解密

Provider 可能实现的服务:
* DSA,RSA,MD5,SHA-1等算法
* 密钥的生成,转换和管理设置
```
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

```java
Provider provider = Security.getProvider("SUN");
System.out.println("Name : " + provider.getName());
System.out.println("Version : " + provider.getVersion());
System.out.println("Info : " + provider.getInfo());
Set<Entry<Object, Object>> keys = provider.entrySet();
for (Entry<Object, Object> entry : keys) {
System.out.println(entry.getKey() + " : " + entry.getValue());
```

## SecureRandom
安全随机数生成器 ,SHA1PRNG是其默认算法
```java
SecureRandom sr = SecureRandom.getInstance("MD5");

// 可使用如下方法多次生成种子,返回给定种子字节数量,该数量可是要此类来将自身设置为种子的种子生成算法来计算
byte[] seeds = sr.generateSeed(256);

SecureRandom sr = SecureRandom.getInstance("MD5");
// SecureRandom覆盖了Random几个方法.
// 生成用户指定的随机字节数,其结果填充到byte[]数组中
byte[] bytes = new byte[1024];
sr.nextBytes(bytes);
```


## Security
管理java程序中所用到的提供者.Security类是一个终态类,除了私有的构造方法外,其余均匀静态方法
```java
// 在提供者数组某个位置增加新的提供者
Security.insertProviderAt(provider, position)
//将带有指定名称的提供者从提供者数组中移除,其后提供者位置可能上移
Security.removeProvider("");
//获取带有指定名称的提供者
Security.getProvider("");

Security.getProviders();
// 返回包含制定的选择标准的所有已安装的提供者的数组(拷贝),如果尚未安装此类提供者,则返回null
Security.getProviders(filter)
// 返回包含制定的选择标准的所有已安装的提供者的数组(拷贝),如果尚未安装此类提供者,则返回null
Security.getProviders("");
// 获取安全属性值
Security.getProperty("");
// 设置安全属性值
Security.setProperty("", "");
// 通过指定加密服务所对应的可用算法活类型的名称
Security.getAlgorithms(""); // TODO
```

## Signature
引擎类. 用来生成和验证数字签名. 用来生成和验证数字签名.

使用Sinature对象签名数据或验证签名包括下面三个阶段
1. 初始化:初始化验证签名的公钥, 初始化签署签名的私钥
2. 更新. 根绝初始化的类型,可更新要签名活验证的字节
3.签署或验证所有更新字节的签名

支持的算法
```
基于:DSA
NONEwithDSA,SHAwithDSA
基于:RSA
MD2whitRSA,MD5whitRSA,SHA1whitRSA,SHA256whitRSA,SHA256whitRSA,SHA384whitRSA,SHA512whitRSA
```
```java
// 实例化对象
Signature s = Signature.getInstance("NONEwithDSA");
// 使用指定的参数集初始化签名引擎
//s.setParameter(params);
// 获取与次签名对象一起使用的参数
s.getParameters();
// 根据私密密钥初始化要进行签名操作的签名对象
//s.initSign(privateKey);
// 根据公共密钥初始化要进行验证操作的签名对象 (验证一般数字签名)
//s.initVerify(publicKey);
// 根据公共密钥初始化要进行验证操作的签名对象 (验证签名证书)
//s.initVerify(certificate);

// 完成初始化操作便可更新Signature对象中的数据了
s.update((byte)1);

// 完成更新后便个做签名操作了,完成签名后返回了存储在缓冲区中的呃签名字节长度
byte[] signed = s.sign();

// 开始验证
boolean vResult = s.verify(signed);
	
// 代做签名的原始信息
byte[] bytes = "Data signture".getBytes();
// 实例化KeyPairGenerator对象
KeyPairGenerator kpg = KeyPairGenerator.getInstance("DSA");
kpg.initialize(1024);
KeyPair kp = kpg.genKeyPair();
// 实例化Signature对象
Signature s = Signature.getInstance(kpg.getAlgorithm());
// 初始化用来签名操作的Signature对象
s.initSign(kp.getPrivate());
// 更新
s.update(bytes);
// 获得签名
byte[] sign = s.sign();
/* 完成签名/


// 用公钥完成验证
s.initVerify(kp.getPublic());
// 更新
s.update(bytes);
// 获得验证结果
boolean status = s.verify(sign);
```

## SignedObject
用来创建实际运行时的对象.在检测不到这些对象的情况下,其完整性不会遭受损害SignedObject包含另一个Serializable对象,即签名的对象及其签名.签名对象是对原始对象的深层复制(以序列化形式),一旦生成了副本对原始对的进一步操作就不再影响该副本

签名对象通过以下构造方法完成实例化对象：通过任何可序列化对象构造Signedobject对象`public Signedobject(Serializable object,privateKey,Signature signingEngine)`. 在完成上述实例化操作后，可通过以下方法获得封装后边的对象和签名：获取已封装的对象 --> getObject() 

在已签名对象上按字节数组的形式获取签名  -->	 getobject()接着，可以通过公钥和Signature进行验证操作：使用指派的验证引擎，通过给定的验证密钥验证Sibnedobject中的签名是否为内部存储对象的有效签名  verify()

此外，SignedObject还提供了以下方法：获取签名算法的名称 getAlgorithm()
```java
// 代做签名的原始信息
byte[] bytes = "Data signture".getBytes();
// 实例化KeyPairGenerator对象
KeyPairGenerator kpg = KeyPairGenerator.getInstance("DSA");
kpg.initialize(1024);
KeyPair kp = kpg.genKeyPair();
// 实例化Signature对象
Signature s = Signature.getInstance(kpg.getAlgorithm());
SignedObject so = new SignedObject(bytes, kp.getPrivate(), s);
// 获得签名
byte[] sign = so.getSignature();
/* 完成签名/

// 用公钥完成验证
// 获得验证结果
boolean status = so.verify(kp.getPublic(), s)
```


## Timestamp
用来封装有关签署时间戳的信息.它包括时间戳的日期和时间,以及有关生成时间戳的Timestamping Authority信息.构建一个数字时间戳需要提供时间和签名证书路径（CertPath）两个参数，方法如下：
构建一个Timestamp对象 --> Timestamp（）

获得数字时间戳后的主要目的在于校验给定对象是否与此数字时间戳一致，方法如下：比较指定的对象和Timestamp对象是否相同 --> equals（）

当然，我们可以通过数字时间戳获得相应的签名证书路径和生成数字时间戳的日期和时间，方法如下：返回Timestamping Authority的证书路径  --> getSignerCertPath()

返回生成数字数字时间戳时的日期和时间  --> getTimestamp()

此外，数字时间戳覆盖了以下两种方法：返回描述此数字时间戳的字符串tostring()
```java
// 构造一个数字时间戳
// 构建CertificateFactory对象,并指定证书类型为x.509
CertificateFactory cf = CertificateFactory.getInstance("509");
// 生成Certificate对象
CertPath cp = cf.generateCertPath(new FileInputStream(""));
// 实例化时间戳
Timestamp t = new Timestamp(new Date(), cp);
	}
}
```
用来封装有关签署时间戳的信息.
