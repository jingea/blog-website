category: Java
date: 2014-11-12
title: Java加密 -- 数字签名
---
# 数字签名

通过[散列函数](http://www.yu66.wang/2014/11/11/Java/Java%E5%8A%A0%E5%AF%86%20%E6%B6%88%E6%81%AF%E6%91%98%E8%A6%81%E5%AE%9E%E7%8E%B0/)可以确保数据内容的完整性,但这还远远不够. 此外,还需要确保数据来源的可认证性和数据发送行为的不可否任性. 完整性,可认证性和不可否认性是数字签名的主要特征. 

消息发送方通过签名算法将消息计算出一个签名, 然后将消息连同签名一起发送给消息接收方. 然后接收方通过通过验证算法将消息和签名进行验证.

数字签名离不开非对称密码体制, 签名算法受私钥控制,且由签名者保密. 验证算法受公玥控制,且对外公开.

数字签名满足以下三个基本要求
* 签名者任何时候都无法否认自己曾经签发的数字签名.
* 信息接受者能够验证和确认收到的数字签名,但任何人无法伪造信息发送者的数字签名.
* 当收发双发对数字签名的真伪产生争议时,可通过仲裁机构进行仲裁.

暂定甲方(消息发送方)拥有私钥并且奖罚将公玥发布给乙方(消息接收方), 当甲方作为消息的发送方时, 甲方使用私钥对消息做签名处理,然后将加密的消息连同数字签名发送给乙方.乙方使用已获得的公玥对接收到的加密消息做解密处理,然后使用公玥及数字签名对原始消息做验证处理.

当乙方作为发送方,通过公玥将消息加密后发送给甲方时,由于算法,公玥公开,任何一个已获得公玥的窃密者都可以截获乙方发送的消息,替换成自己的消息发送给甲方,而甲方无法辨别消息来源是否是乙方.也就是说,上述的认证方式是单向的,属于单向认证. 如果拥有俩套公私玥,甲乙双方都对数据做签名及验证就可以避免这一问题. 没错这种认证方式是双向认证.以网银交易事宜的都是单向认证方式,无法验证使用者的身份. 而要求较高的网银交易都是双向认证方式,交易双方身份都可以得到验证.



# AbstractSign
定义一个签名和验证的抽象类
```java
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public abstract class AbstractSign {
	// 数字签名密钥算法
	private String signKeyAlgorithm;

	// 数字签名  签名/验证算法
	private String signAlgorithm;

	private PublicKey publicKey;
	private PrivateKey privateKey;

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	protected AbstractSign(String signKeyAlgorithm, String signAlgorithm) throws NoSuchAlgorithmException {
		this.signKeyAlgorithm = signKeyAlgorithm;
		this.signAlgorithm = signAlgorithm;
		// 初始化密钥对儿生成器
		KeyPairGenerator keygen = KeyPairGenerator.getInstance(signKeyAlgorithm);
		// 实例化密钥对儿生成器, 调用keygen的initialize()方法
		initKeyPairGenerator(keygen);
		// 实例化密钥对儿
		KeyPair keys = keygen.genKeyPair();
		// (DSAPublicKey, DSAPrivateKey), (RSAPublicKey, RSAPrivateKey), (ECPublicKey, ECPrivateKey)
		publicKey = keys.getPublic();
		privateKey = keys.getPrivate();
	}

	protected abstract void initKeyPairGenerator(KeyPairGenerator keygen);

	// 签名
	public byte[] sign(byte[] data) throws Exception {
		// 还原私钥 转换私钥材料
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
		// 实例化密钥工厂
		KeyFactory keyFactory = KeyFactory.getInstance(signKeyAlgorithm);
		// 生成私钥对象
		PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);
		// 实例化Signature
		Signature signature = Signature.getInstance(signAlgorithm);
		// 初始化Signature
		signature.initSign(priKey);
		// 更新
		signature.update(data);
		// 签名
		return signature.sign();
	}

	//校验
	public boolean verify(byte[] data, byte[] sign) throws Exception {
		// 还原公钥 转换公钥材料
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey.getEncoded());
		// 实例化密钥工厂
		KeyFactory keyFactory = KeyFactory.getInstance(signKeyAlgorithm);
		// 取公钥匙对象
		PublicKey pubKey = keyFactory.generatePublic(keySpec);
		// 实例话Signature
		Signature signature = Signature.getInstance(signAlgorithm);
		// 初始化Signature
		signature.initVerify(pubKey);
		// 更新
		signature.update(data);
		// 验证
		return signature.verify(sign);
	}
}
```
在上面的代码中我们见到了PKCS8和X509这样的字样, 下面就对这俩个概念做一定的讲解

RSA公司定义了PKCS（Public Key Cryptography Standards,公钥加密标准）,并定义了许多PKI基础组件,如数字签名和证书请求格式；
* IETF（Internet Engineering Task Force,互联网工程任务组）
* PKIWG（Public Key Infrastructure Working Group,PKI工作组）定义了一组具有可操作性的公钥基础设施协议PKIX（Public Key Infrastructure Using X.509,公钥基础设施X.509）.

PKCS共有15项标准:
1. PKCS#1：RSA公钥算法加密和签名机制 (PKCS#2和PKCS#4标准已被撤销,合并至PKCS#1中)
2. PKCS#3：DH密钥交换协议
3. PKCS#5：PBE加密标准
4. PKCS#6：公钥证书（X.509证书的扩展格式）标准语法
5. PKCS#7：加密消息语法标准
6. PKCS#8：私钥信息格式
7. PKCS#9：选择属性格式 (较为常用)
8. PKCS#10：证书请求语法
9. PKCS#11：密码装置标准接口
10. PKCS#12：个人信息交换语法标准(较为常用)
11. PKCS#13：椭圆曲线密码体制标准
12. PKCS#14：伪随机数生成标准(较为常用)
13. PKCS#15：密码令牌信息格式标准

上述标准主要用于用户实体通过注册机构（RA）进行证书申请、用户证书更新等过程.当证书作废时,注册机构通过认证中心向目录服务器发布证书撤销列表.上述标准还用于扩展证书内容、数字签名、数字签名验证和定义数字信封格式等情况.在构建密钥填充方式时,考虑到不同的安全等级,也会选择不同PKCS标准.

PKI称为公钥基础设施(Public Key Infrastructure), 是一个基于X.509的、用于创建、分配和撤回证书的模型.
PKI能够为所有网络应用提供加密和数字签名等密码服务及所必需的密钥和证书管理体系.换言之,PKI利用公钥密码技术构建基础设施,为网上电子商务、电子政务等应用提供安全服务.PKI技术是信息安全技术的核心,也是电子商务的关键和基础技术.如今大家所熟悉的网银交易系统就是PKI技术的具体体现.

PKI由公钥密码技术、数字证书、证书认证中心和关于公钥的安全策略等基本成分共同组成,对密钥和证书进行管理.因此,PKI技术涉及对称加密算法、非对称加密算法、消息摘要算法和数字签名等密码学算法.

PKI系统由以下五部分组成:
* 认证中心(Certificate Authority,CA PKI技术核心) : 数字证书的申请及签发机构, 确保公钥管理公开透明,进行 证书发放,, 证书更新, 证书撤销, 证书验证 操作. 由注册服务器、注册机构(Registry Authority,RA)和认证中心服务器三部分组成.
* 数字证书库(Certificate Repository,CR PKI技术核心) : 存储已签发的数字证书及公钥
* 密钥备份及恢复系统 : 若用户丢失密钥则无法对数据解密,这将造成数据的丢失.为避免此类情况,PKI技术提供密钥备份及恢复功能.
* 证书作废系统 : 证书作废系统. 为了确保证书的有效性,证书具有使用时效性. 而且如果证书持有机构存在安全性问题,也需要作废. PKI技术通过将证书列入作废证书列表(Certificate Revocation List,CRL)来完成证书作废操作.用户可以通过查询CRL来验证证书的有效性.
* 应用程序接口（Application Programming Interface,API） : PKI技术必须提供良好的应用程序接口,使得各式各样的应用,不同的系统架构都能以安全、一致、可信的方式与PKI进行交互,且能快速完成交互过程,以确保安全网络环境的完整性和易用性.


数字证书是网络用户的身份标表,包含ID、公钥和颁发机构的数字签名等内容.其形式主要有X.509公钥证书、SPKI（Simple Public Key Infrastructure,简单PKI）证书、PGP（Pretty Good Privacy）证书和属性（Attribute）证书.其中,X.509证书最为常见.我们俗称的数字证书,通常指的是X.509公钥证书.

目前,我们所使用的X.509证书通常由VeriSign、GeoTrust和Thawte三大国际权威认证机构签发.VeriSign由RSA控股,借助RSA成熟的安全技术提供了较为广泛的PKI产品,其产品活跃在电子商务平台中.当我们在淘宝或者亚马逊上购物时,总能看到熟悉的VeriSign字样.

由于证书存在时效性,证书持有机构需要定期向认证机构申请证书签发.根据证书持有机构的证书使用范畴,认证机构会对不同的证书签发收取不同的费用.由此,证书持有机构需要每年向认证机构缴纳高额的年费.为了加强系统安全性,证书的密钥长度也会随着其费用递增.其中,价格最高的是商业网站的证书认证费用.上述的费用是认证机构得以生存的经济来源,同时也是电子商务平台等机构构建系统架构必须支付的安全成本之一.



# RSA

RSA算法既是最为常用的非对称加密算法,又是最为常用的签名算法
```java
import org.junit.Assert;

import java.security.*;
import java.util.Random;

public class RSASign extends AbstractSign{
	public RSASign() throws Exception {
		super("RSA", "SHA1withRSA");
	}

	protected void initKeyPairGenerator(KeyPairGenerator keygen) {
		// RSA密钥长度 默认1024位，密钥长度必须是64的倍数，范围在512至65536位之间。
		keygen.initialize(512);
	}

	private static final byte[] datas = new byte[1024 * 1024];
	static {
		for (int i = 0; i < 1024 * 1024; i++) {
			datas[i] = (byte)new Random().nextInt(125);
		}
	}

	public static void main(String[] args) throws Exception {
		RSASign rsaSign = new RSASign();
		byte[] signd = rsaSign.sign(datas);
		boolean result = rsaSign.verify(datas, signd);
		Assert.assertEquals(true, result);
	}
}
```

# ECDSA
```java
import org.junit.Assert;

import java.math.BigInteger;
import java.security.*;
import java.security.spec.*;
import java.util.Random;

public class ECDSASign extends AbstractSign {

	public ECDSASign(String algo) throws Exception {
		super("ECDSA", algo);
	}

	@Override
	protected void initKeyPairGenerator(KeyPairGenerator keygen) {
		BigInteger p = new BigInteger("883423532389192164791648750360308885314476597252960362792450860609699839");
		BigInteger a = new BigInteger("7fffffffffffffffffffffff7fffffffffff8000000000007ffffffffffc", 16);
		BigInteger b = new BigInteger( "6b016c3bdcf18941d0d654921475ca71a9db2fb27d1d37796185c2942c0a", 16);
		ECFieldFp ecFieldFp = new ECFieldFp(p);

		EllipticCurve ellipticCurve = new EllipticCurve(ecFieldFp, a, b);

		BigInteger x = new BigInteger("110282003749548856476348533541186204577905061504881242240149511594420911");
		BigInteger y = new BigInteger("869078407435509378747351873793058868500210384946040694651368759217025454");
		BigInteger n = new BigInteger("883423532389192164791648750360308884807550341691627752275345424702807307");

		ECPoint g = new ECPoint(x, y);
		ECParameterSpec ecParameterSpec = new ECParameterSpec(ellipticCurve, g, n, 1);
		// 初始化密钥对儿生成器
		try {
			keygen.initialize(ecParameterSpec, new SecureRandom());
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
	}

	private static final byte[] datas = new byte[1024 * 1024];

	static {
		for (int i = 0; i < 1024 * 1024; i++) {
			datas[i] = (byte)new Random().nextInt(125);
		}
	}

	public static void main(String[] args) throws Exception {
		signed("NONEwithECDSA");
		signed("RIPEMD160withECDSA");
		signed("SHA1withECDSA");
		signed("SHA224withECDSA");
		signed("SHA256withECDSA");
		signed("SHA384withECDSA");
		signed("SHA512withECDSA");
	}

	private static void signed(String s) throws Exception {
		ECDSASign ecdsaCoder = new ECDSASign(s);
		byte[] signed = ecdsaCoder.sign(datas);
		Assert.assertEquals(true, ecdsaCoder.verify(datas, signed));
	}
}
```

# DSA
DSA算法是典型的数字签名算法,其本身属于非对称加密算法不具备数据加密与解密的功能.
```java
import org.junit.Assert;

import java.security.*;
import java.util.Random;

public class DSACoder extends AbstractSign {

	public DSACoder() throws Exception {
		super("DSA", "SHA1withDSA");
	}

	@Override
	protected void initKeyPairGenerator(KeyPairGenerator keygen) {
		// DSA密钥长度, 默认1024位，密钥长度必须是64的倍数，范围在512至1024位之间（含）
		keygen.initialize(1024, new SecureRandom());
	}

	private static final byte[] datas = new byte[1024 * 1024];

	static {
		for (int i = 0; i < 1024 * 1024; i++) {
			datas[i] = (byte)new Random().nextInt(125);
		}
	}

	public static void main(String[] args) throws Exception {
		DSACoder dsaCoder = new DSACoder();
		byte[] signd = dsaCoder.sign(datas);
		boolean result = dsaCoder.verify(datas, signd);
		Assert.assertEquals(true, result);
	}
}
```