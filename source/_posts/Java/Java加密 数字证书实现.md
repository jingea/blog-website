﻿category: Java
date: 2014-11-12
title: Java加密 -- 数字证书 
---
# 散列函数

散列函数又称为哈希函数,消息摘要函数,单向函数或者杂凑函数. 与上述密码体制不同的是, 散列函数的主要作用不是完成数据加密解密操作, 它主要是用来验证数据的完整性. 散列值是一个短的随机字母和数字组成的字符串.

![消息认证流程]()

在上述认证流程中,信息收发双发在通信前已经商定了具体的散列算法,并且该算法是公开的.
散列函数具有以下特性:
* 消息的长度不受限制.
* 对于给定的消息,其散列值的计算是很容易的.
* 如果两个散列值不相同,则这两个散列值的原始输入消息也不相同,这个特性使得散列函数具有确定性的结果.
* 散列函数的运算过程是不可逆的,这个特性称为函数的单向性.这也是单向函数命名的由来.
* 对于一个已知的消息及其散列值,要找到另一个消息使其获得相同的散列值是不可能的,这个特性称为抗弱碰撞性.这被用来防止伪造.
* 任意两个不同的消息的散列值一定不同,这个特性称为抗强碰撞性.


# 数字签名

通过散列函数可以确保数据内容的完整性,但这还远远不够. 此外,还需要确保数据来源的可认证性和数据发送行为的不可否任性. 完整性,可认证性和不可否认性是数字签名的主要特征. 数字签名针对以数字形式存储的消息进行处理, 产生一种带有操作者身份信息的编码.执行数字签名的实体称为签名者,签名过程中所使用的算法称为签名算法, 签名过程中生成的编码称为签名者对该消息的数字签名. 发送者通过网络连同数字签名一齐发送给接受者. 接受者在得到该消息及数字签名后,可以通过一个算法来验证签名的真伪以及识别相应的签名者. 这一过程称为验证过程, 其过程使用的算法称为验证算法. 数字签名离不开非对称密码体制, 签名算法受私钥控制,且由签名者保密. 验证算法受公玥控制,且对外公开.
RSA算法既是最为常用的非对称加密算法,又是最为常用的签名算法.DSA算法是典型的数字签名算法,其本身属于非对称加密算法不具备数据加密与解密的功能.
数字签名满足以下三个基本要求
* 签名者任何时候都无法否认自己曾经签发的数字签名.
* 信息接受者能够验证和确认收到的数字签名,但任何人无法伪造信息发送者的数字签名.
* 当收发双发对数字签名的真伪产生争议时,可通过仲裁机构进行仲裁.

![数字签名认证流程]()

暂定甲方拥有私钥并且奖罚将公玥发布给乙方, 当甲方作为消息的发送方时, 甲方使用私钥对消息做签名处理,然后将加密的消息连同数字签名发送给乙方.乙方使用已获得的公玥对接收到的加密消息做解密处理,然后使用公玥及数字签名对原始消息做验证处理.

当然我们可以对消息先加密,然后对加密后的消息做签名处理,这样乙方获得消息后,先做验证处理,如果验证通过则对消息解密.反之,验证消息失败则抛弃消息.这样做显然可以提高系统的处理速度,但即便如此,仍建议大家先对消息做签名,再做加密处理.加密与签名都应该只针对原始消息做处理.加密是为了确保消息在传送过程中避免被破解,签名是为了确保消息的有效性.消息本身就可能是一个可执行文件,消息的接收方通过对消息的验证判断该文件是由有权执行,而这个文件本身是不需要加密的.

由于签名的不可伪造,甲方不能否认自己已经发送的消息,而乙方可验证消息的来源以及消息是否完整.数字签名可提供OSI参考模型5种安全服务中的3种：认证服务,抗否认性服务,数据完整性服务. 正因为如此,数字签名称为公玥基础设施以及许多网络安全机制的基础.

当乙方作为发送方,通过公玥将消息加密后发送给甲方时,由于算法,公玥公开,任何一个已获得公玥的窃密者都可以截获乙方发送的消息,替换成自己的消息发送给甲方,而甲方无法辨别消息来源是否是乙方.也就是说,上述的认证方式是单向的,属于单向认证. 如果拥有俩套公私玥,甲乙双方都对数据做签名及验证就可以避免这一问题. 没错这种认证方式是双向认证.以网银交易事宜的都是单向认证方式,无法验证使用者的身份. 而要求较高的网银交易都是双向认证方式,交易双方身份都可以得到验证.

# 公玥基础设施

公钥基础设施（Public Key Infrastructure,PKI）是一个基于X.509的、用于创建、分配和撤回证书的模型.PKI能够为所有网络应用提供加密和数字签名等密码服务及所必需的密钥和证书管理体系.换言之,PKI利用公钥密码技术构建基础设施,为网上电子商务、电子政务等应用提供安全服务.PKI技术是信息安全技术的核心,也是电子商务的关键和基础技术.如今大家所熟悉的网银交易系统就是PKI技术的具体体现.

PKI由公钥密码技术、数字证书、证书认证中心和关于公钥的安全策略等基本成分共同组成,对密钥和证书进行管理.因此,PKI技术涉及对称加密算法、非对称加密算法、消息摘要算法和数字签名等密码学算法.

我们目前所使用到的电子商务平台大部分都是基于PKI技术实现的.

## 2.9.1 PKI的标准

RSA公司定义了PKCS（Public Key Cryptography Standards,公钥加密标准）,并定义了许多PKI基础组件,如数字签名和证书请求格式；IETF（Internet Engineering Task Force,互联网工程任务组）和PKIWG（Public Key Infrastructure Working Group,PKI工作组）定义了一组具有可操作性的公钥基础设施协议PKIX（Public Key Infrastructure Using X.509,公钥基础设施X.509）.

## PKCS共有15项标准:

1. PKCS#1：RSA公钥算法加密和签名机制
2. PKCS#3：DH密钥交换协议
3. PKCS#5：PBE加密标准
4. PKCS#6：公钥证书（X.509证书的扩展格式）标准语法
5. PKCS#7：加密消息语法标准
6. PKCS#8：私钥信息格式
7. PKCS#9：选择属性格式
8. PKCS#10：证书请求语法
9. PKCS#11：密码装置标准接口
10. PKCS#12：个人信息交换语法标准
11. PKCS#13：椭圆曲线密码体制标准
12. PKCS#14：伪随机数生成标准
13. PKCS#15：密码令牌信息格式标准

其中,PKCS#2和PKCS#4标准已被撤销,合并至PKCS#1中；较为常用的是PKCS#7、PKCS#10和PKCS#12.

上述标准主要用于用户实体通过注册机构（RA）进行证书申请、用户证书更新等过程.当证书作废时,注册机构通过认证中心向目录服务器发布证书撤销列表.上述标准还用于扩展证书内容、数字签名、数字签名验证和定义数字信封格式等情况.在构建密钥填充方式时,考虑到不同的安全等级,也会选择不同PKCS标准.

PKIX作为操作性标准涉及证书管理协议(Certificate Management Protocol,CMP)、安全多用途邮件扩展（S/MIME）和在线证书状态协议（Online Certificate Status Protocol,OCSP）等.

### PKI系统的组成

PKI系统由认证中心（Certificate Authority,CA）、数字证书库（Certificate Repository,CR）、密钥备份及恢复系统、证书作废系统,以及应用程序接口（Application Programming Interface,API）五部分组成.其中,认证中心CA和数字证书库是PKI技术的核心.

1. 认证中心

CA是PKI的核心之一,是数字证书的申请及签发机构,且机构必须具有权威性,以确保公钥管理公开透明.

### 认证中心的主要功能如下：
* 证书发放
* 证书更新
* 证书撤销
* 证书验证

认证中心主要由注册服务器、注册机构（Registry Authority,RA）,和认证中心服务器三部分组成.

2. 数字证书库. 数字证书库用于存储已签发的数字证书及公钥,包括LDAP（Light Direct Access Protocol,轻量级目录访问协议）目录服务器和普通数据库.用户可通过数字证书库进行证书查询,并可获得其他用户的证书及公钥.

3. 密钥备份及恢复系统. 若用户丢失密钥则无法对数据解密,这将造成数据的丢失.为避免此类情况,PKI技术提供密钥备份及恢复功能.密钥的备份与恢复需要可信的权威机构来完成,这也是认证机构存在的必要条件.

4. 证书作废系统. 为了确保证书的有效性,证书具有使用时效性,以确保证书所属环境的安全性.从另一个角度来讲,如果证书持有机构存在一定的安全性问题,即便证书未超过有效期,亦需要作废.PKI技术通过将证书列入作废证书列表（Certificate Revocation List,CRL）来完成证书作废操作.用户可以通过查询CRL来验证证书的有效性.

5. 应用程序接口API. PKI技术必须提供良好的应用程序接口,使得各式各样的应用,不同的系统架构都能以安全、一致、可信的方式与PKI进行交互,且能快速完成交互过程,以确保安全网络环境的完整性和易用性.

### 数字证书

数字证书是网络用户的身份标表,包含ID、公钥和颁发机构的数字签名等内容.其形式主要有X.509公钥证书、SPKI（Simple Public Key Infrastructure,简单PKI）证书、PGP（Pretty Good Privacy,译为“很好的私密”）证书和属性（Attribute）证书.其中,X.509证书最为常见.我们俗称的数字证书,通常指的是X.509公钥证书.

目前,我们所使用的X.509证书通常由VeriSign、GeoTrust和Thawte三大国际权威认证机构签发.VeriSign由RSA控股,借助RSA成熟的安全技术提供了较为广泛的PKI产品,其产品活跃在电子商务平台中.当我们在淘宝或者亚马逊上购物时,总能看到熟悉的VeriSign字样.

由于证书存在时效性,证书持有机构需要定期向认证机构申请证书签发.根据证书持有机构的证书使用范畴,认证机构会对不同的证书签发收取不同的费用.由此,证书持有机构需要每年向认证机构缴纳高额的年费.为了加强系统安全性,证书的密钥长度也会随着其费用递增.其中,价格最高的是商业网站的证书认证费用.上述的费用是认证机构得以生存的经济来源,同时也是电子商务平台等机构构建系统架构必须支付的安全成本之一.


# RSACoder
```java

/**
 * RSA安全编码组件
 * 
 */
public abstract class RSACoder {
	
	/**
	 * 数字签名
	 * 密钥算法
	 */
	public static final String KEY_ALGORITHM = "RSA";

	/**
	 * 数字签名
	 * 签名/验证算法
	 */
	public static final String SIGNATURE_ALGORITHM = "SHA1withRSA";

	/**
	 * 公钥
	 */
	private static final String PUBLIC_KEY = "RSAPublicKey";

	/**
	 * 私钥
	 */
	private static final String PRIVATE_KEY = "RSAPrivateKey";

	/**
	 * RSA密钥长度 默认1024位，
	 *  密钥长度必须是64的倍数， 
	 *  范围在512至65536位之间。
	 */
	private static final int KEY_SIZE = 512;

	/**
	 * 签名
	 * 
	 * @param data
	 *            待签名数据
	 * @param privateKey
	 *            私钥
	 * @return byte[] 数字签名
	 * @throws Exception
	 */
	public static byte[] sign(byte[] data, byte[] privateKey) throws Exception {

		// 转换私钥材料
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKey);

		// 实例化密钥工厂
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

		// 取私钥匙对象
		PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);

		// 实例化Signature
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);

		// 初始化Signature
		signature.initSign(priKey);

		// 更新
		signature.update(data);

		// 签名
		return signature.sign();
	}

	/**
	 * 校验
	 * 
	 * @param data
	 *            待校验数据
	 * @param publicKey
	 *            公钥
	 * @param sign
	 *            数字签名
	 * 
	 * @return boolean 校验成功返回true 失败返回false
	 * @throws Exception
	 * 
	 */
	public static boolean verify(byte[] data, byte[] publicKey, byte[] sign)
			throws Exception {

		// 转换公钥材料
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);

		// 实例化密钥工厂
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

		// 生成公钥
		PublicKey pubKey = keyFactory.generatePublic(keySpec);

		// 实例化Signature
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);

		// 初始化Signature
		signature.initVerify(pubKey);

		// 更新
		signature.update(data);

		// 验证
		return signature.verify(sign);
	}

	/**
	 * 取得私钥
	 * 
	 * @param keyMap
	 * @return
	 * @throws Exception
	 */
	public static byte[] getPrivateKey(Map<String, Object> keyMap)
			throws Exception {

		Key key = (Key) keyMap.get(PRIVATE_KEY);

		return key.getEncoded();
	}

	/**
	 * 取得公钥
	 * 
	 * @param keyMap
	 * @return
	 * @throws Exception
	 */
	public static byte[] getPublicKey(Map<String, Object> keyMap)
			throws Exception {

		Key key = (Key) keyMap.get(PUBLIC_KEY);

		return key.getEncoded();
	}

	/**
	 * 初始化密钥
	 * 
	 * @return Map 密钥对儿 Map
	 * @throws Exception
	 */
	public static Map<String, Object> initKey() throws Exception {

		// 实例化密钥对儿生成器
		KeyPairGenerator keyPairGen = KeyPairGenerator
				.getInstance(KEY_ALGORITHM);

		// 初始化密钥对儿生成器
		keyPairGen.initialize(KEY_SIZE);

		// 生成密钥对儿
		KeyPair keyPair = keyPairGen.generateKeyPair();

		// 公钥
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

		// 私钥
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

		// 封装密钥
		Map<String, Object> keyMap = new HashMap<String, Object>(2);

		keyMap.put(PUBLIC_KEY, publicKey);
		keyMap.put(PRIVATE_KEY, privateKey);

		return keyMap;
	}
}
```

# ECDSACoder
---
```java

/**
 * ECDSA安全编码组件
 * 
 */
public enum ECDSACoder {

	NONEwithECDSA("NONEwithECDSA"), 
	RIPEMD160withECDSA("RIPEMD160withECDSA"), 
	SHA1withECDSA("SHA1withECDSA"),   
	SHA224withECDSA("SHA224withECDSA"),  
	SHA256withECDSA("SHA256withECDSA"),
	SHA384withECDSA("SHA384withECDSA"), 
	SHA512withECDSA("SHA512withECDSA");
	
	/**
	 * 数字签名 密钥算法
	 */
	private final String KEY_ALGORITHM = "ECDSA";

	ECDSACoder(String algo) {
		this.SIGNATURE_ALGORITHM = algo;
	}
	
	private String SIGNATURE_ALGORITHM ;

	{
		Security.addProvider(new BouncyCastleProvider());
	}
	
	/**
	 * 公钥
	 */
	private final String PUBLIC_KEY = "ECDSAPublicKey";

	/**
	 * 私钥
	 */
	private final String PRIVATE_KEY = "ECDSAPrivateKey";

	/**
	 * 初始化密钥
	 * 
	 * @return Map 密钥Map
	 * @throws Exception
	 */
	public Map<String, Object> initKey() throws Exception {

		BigInteger p = new BigInteger(
				"883423532389192164791648750360308885314476597252960362792450860609699839");
 
		ECFieldFp ecFieldFp = new ECFieldFp(p);

		BigInteger a = new BigInteger(
				"7fffffffffffffffffffffff7fffffffffff8000000000007ffffffffffc",
				16);
 
		BigInteger b = new BigInteger(
				"6b016c3bdcf18941d0d654921475ca71a9db2fb27d1d37796185c2942c0a",
				16);
 
		EllipticCurve ellipticCurve = new EllipticCurve(ecFieldFp, a, b);

		BigInteger x = new BigInteger(
				"110282003749548856476348533541186204577905061504881242240149511594420911");
 
		BigInteger y = new BigInteger(
				"869078407435509378747351873793058868500210384946040694651368759217025454");
 
		ECPoint g = new ECPoint(x, y);

		BigInteger n = new BigInteger(
				"883423532389192164791648750360308884807550341691627752275345424702807307");

		ECParameterSpec ecParameterSpec = new ECParameterSpec(ellipticCurve, g,
				n, 1);

		// 实例化密钥对儿生成器
		KeyPairGenerator kpg = KeyPairGenerator.getInstance(KEY_ALGORITHM);

		// 初始化密钥对儿生成器
		kpg.initialize(ecParameterSpec, new SecureRandom());

		// 生成密钥对儿
		KeyPair keypair = kpg.generateKeyPair();

		ECPublicKey publicKey = (ECPublicKey) keypair.getPublic();

		ECPrivateKey privateKey = (ECPrivateKey) keypair.getPrivate();

		// 封装密钥
		Map<String, Object> map = new HashMap<String, Object>(2);

		map.put(PUBLIC_KEY, publicKey);
		map.put(PRIVATE_KEY, privateKey);

		return map;
	}

	/**
	 * 取得私钥
	 * 
	 * @param keyMap
	 *            密钥Map
	 * @return byte[] 私钥
	 * @throws Exception
	 */
	public byte[] getPrivateKey(Map<String, Object> keyMap)
			throws Exception {

		Key key = (Key) keyMap.get(PRIVATE_KEY);

		return key.getEncoded();
	}

	/**
	 * 取得公钥
	 * 
	 * @param keyMap
	 *            密钥Map
	 * @return byte[] 公钥
	 * @throws Exception
	 */
	public byte[] getPublicKey(Map<String, Object> keyMap)
			throws Exception {

		Key key = (Key) keyMap.get(PUBLIC_KEY);

		return key.getEncoded();
	}

	/**
	 * 签名
	 * 
	 * @param data
	 *            待签名数据
	 * @param privateKey
	 *            私钥
	 * @return byte[] 数字签名
	 * @throws Exception
	 */
	public byte[] sign(byte[] data, byte[] privateKey) throws Exception {

		// 转换私钥材料
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKey);

		// 实例化密钥工厂
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

		// 取私钥匙对象
		PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);

		// 实例化Signature
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);

		// 初始化Signature
		signature.initSign(priKey);

		// 更新
		signature.update(data);

		// 签名
		return signature.sign();
	}

	/**
	 * 校验
	 * 
	 * @param data
	 *            待校验数据
	 * @param publicKey
	 *            公钥
	 * @param sign
	 *            数字签名
	 * @return boolean 校验成功返回true 失败返回false
	 * @throws Exception
	 * 
	 */
	public boolean verify(byte[] data, byte[] publicKey, byte[] sign)
			throws Exception {

		// 转换公钥材料
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);

		// 实例化密钥工厂
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

		// 生成公钥
		PublicKey pubKey = keyFactory.generatePublic(keySpec);

		// 实例化Signature
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);

		// 初始化Signature
		signature.initVerify(pubKey);

		// 更新
		signature.update(data);

		// 验证
		return signature.verify(sign);
	}
}
```

# DSACoder
---
```java
/**
 * DSA安全编码组件
 * 
 */
public abstract class DSACoder {

	/**
	 * 数字签名密钥算法
	 */
	public static final String ALGORITHM = "DSA";

	/**
	 * 数字签名
	 * 签名/验证算法
	 */
	public static final String SIGNATURE_ALGORITHM = "SHA1withDSA";
	
	/**
	 * 公钥
	 */
	private static final String PUBLIC_KEY = "DSAPublicKey";

	/**
	 * 私钥
	 */
	private static final String PRIVATE_KEY = "DSAPrivateKey";
	
	/**
	 * DSA密钥长度 
	 * 默认1024位， 
	 * 密钥长度必须是64的倍数， 
	 * 范围在512至1024位之间（含）
	 */
	private static final int KEY_SIZE = 1024;
	
	/**
	 * 签名
	 * 
	 * @param data
	 *            待签名数据
	 * @param privateKey
	 *            私钥
	 * @return byte[] 数字签名
	 * @throws Exception
	 */
	public static byte[] sign(byte[] data, byte[] privateKey) throws Exception {

		// 还原私钥
		// 转换私钥材料
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKey);

		// 实例化密钥工厂
		KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);

		// 生成私钥对象
		PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);

		// 实例化Signature
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);

		// 初始化Signature
		signature.initSign(priKey);

		// 更新
		signature.update(data);

		// 签名
		return signature.sign();
	}

	/**
	 * 校验
	 * 
	 * @param data
	 *            待校验数据
	 * @param publicKey
	 *            公钥
	 * @param sign
	 *            数字签名
	 * 
	 * @return boolean 校验成功返回true 失败返回false
	 * @throws Exception
	 * 
	 */
	public static boolean verify(byte[] data, byte[] publicKey, byte[] sign)
			throws Exception {

		// 还原公钥
		// 转换公钥材料
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);

		// 实例化密钥工厂
		KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);

		// 取公钥匙对象
		PublicKey pubKey = keyFactory.generatePublic(keySpec);

		// 实例话Signature
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);

		// 初始化Signature
		signature.initVerify(pubKey);

		// 更新
		signature.update(data);

		// 验证
		return signature.verify(sign);
	}

	/**
	 * 生成密钥
	 * 
	 * @return 密钥对象
	 * @throws Exception
	 */
	public static Map<String, Object> initKey() throws Exception {

		// 初始化密钥对儿生成器
		KeyPairGenerator keygen = KeyPairGenerator.getInstance(ALGORITHM);

		// 实例化密钥对儿生成器
		keygen.initialize(KEY_SIZE, new SecureRandom());

		// 实例化密钥对儿
		KeyPair keys = keygen.genKeyPair();

		DSAPublicKey publicKey = (DSAPublicKey) keys.getPublic();

		DSAPrivateKey privateKey = (DSAPrivateKey) keys.getPrivate();

		// 封装密钥
		Map<String, Object> map = new HashMap<String, Object>(2);

		map.put(PUBLIC_KEY, publicKey);
		map.put(PRIVATE_KEY, privateKey);

		return map;
	}

	/**
	 * 取得私钥
	 * 
	 * @param keyMap
	 *            密钥Map
	 * @return byte[] 私钥
	 * @throws Exception
	 */
	public static byte[] getPrivateKey(Map<String, Object> keyMap)
			throws Exception {

		Key key = (Key) keyMap.get(PRIVATE_KEY);

		return key.getEncoded();
	}

	/**
	 * 取得公钥
	 * 
	 * @param keyMap
	 *            密钥Map
	 * @return byte[] 公钥
	 * @throws Exception
	 */
	public static byte[] getPublicKey(Map<String, Object> keyMap)
			throws Exception {

		Key key = (Key) keyMap.get(PUBLIC_KEY);

		return key.getEncoded();
	}
}
```