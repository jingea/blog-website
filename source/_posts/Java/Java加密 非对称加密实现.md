category: Java
date: 2014-11-010
title: Java加密 -- 非对称加密
---
# 非对称密码

非对称密码与对称密码体制相对,他们的主要区别在于：非对称密码体制的加密密钥和解密密钥不相同,分为俩个密钥,一个公开(公钥),一个保密(密钥).

![非对称密码体制的保密通信模型](https://raw.githubusercontent.com/ming15/blog-website/images/secure/%E9%9D%9E%E5%AF%B9%E7%A7%B0%E5%AF%86%E7%A0%81%E4%BD%93%E5%88%B6%E7%9A%84%E4%BF%9D%E5%AF%86%E9%80%9A%E4%BF%A1%E6%A8%A1%E5%9E%8B.png)

在非对称密码体制中,公玥与私钥均可用于加密与解密操作,但它与对称密码体制有极大的不同. 公玥与私钥分属通信双方,一份消息的加密与解密需要公玥和私钥共同参与. 公玥加密需要私钥解密, 反之, 私钥加密需要公玥解密.

![公玥加密-私钥解密的保密通信模型]()

非对称密码的体制的主要优点是可以适应于开放性的使用环境, 秘钥管理相对简单, 可以方便安全地实现数字签名和验证. 


非对称密码算法的安全性完全依赖于基于计算机复杂度上的难题,通常来自于数论.例如：
* RSA来源于整数因子分解问题.
* DSA-数字签名算法源于离散对数问题.
* ECC-椭圆曲线加密算法源于离散对数问题.
由于这些数学难题的实现多涉及底层模数乘法和指数运算,相比分组密码需要更多的计算机资源, 为了尼补这一缺陷, 非对称密码系统通常是复合式的:用高效率的对称密码算法进行加密解密处理; 用非对称密钥加密对称密码系统所使用的密钥, 通过这种复合方式增进效率.

# RSA

RSA是非对称密码体制的典范,它不仅仅可以完成一般的数据加密操作,同时也支持数字签名和验证. 除了数字签名非对称密码体制还支持数字信封等技术.
```java
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;

import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Random;

public class RSACoder {

	// 非对称加密密钥算法
	private static final String KEY_ALGORITHM = "RSA";
	private RSAPublicKey publicKey;
	private RSAPrivateKey privateKey;

	public RSACoder() {
		Security.addProvider(new BouncyCastleProvider());
		try {
			// 实例化密钥对生成器
			KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
			// 初始化密钥对生成器. RSA密钥长度(默认1024位)， 密钥长度必须是64的倍数，范围在512至65536位之间。
			keyPairGen.initialize(512);
			// 生成密钥对
			KeyPair keyPair = keyPairGen.generateKeyPair();
			// 公钥
			publicKey = (RSAPublicKey) keyPair.getPublic();
			// 私钥
			privateKey = (RSAPrivateKey) keyPair.getPrivate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 私钥解密
	 */
	public byte[] decryptByPrivateKey(byte[] data) {
		try {
			// 取得私钥
			PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
			// 生成私钥
			PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
			// 对数据解密
			Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			return cipher.doFinal(data);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 公钥解密
	 */
	public byte[] decryptByPublicKey(byte[] data) {
		try {
			// 取得公钥
			X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKey.getEncoded());
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
			// 生成公钥
			PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);
			// 对数据解密
			Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
			cipher.init(Cipher.DECRYPT_MODE, publicKey);
			return cipher.doFinal(data);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 公钥加密
	 */
	public byte[] encryptByPublicKey(byte[] data) {
		try {
			// 取得公钥
			X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKey.getEncoded());
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
			PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);
			// 对数据加密
			Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			return cipher.doFinal(data);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 私钥加密
	 */
	public byte[] encryptByPrivateKey(byte[] data) {
		try {
			// 取得私钥
			PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
			// 生成私钥
			PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
			// 对数据加密
			Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
			cipher.init(Cipher.ENCRYPT_MODE, privateKey);
			return cipher.doFinal(data);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// RSA 算法一次加密的数据不能超过53个字节
	private static final byte[] datas = new byte[53];

	static {
		for (int i = 0; i < 53; i++) {
			datas[i] = (byte)new Random().nextInt(125);
		}
	}

	public static void main(String[] args) {
		RSACoder rsaCoder = new RSACoder();
		byte[] encryptData = rsaCoder.encryptByPrivateKey(datas);
		byte[] decryptData = rsaCoder.decryptByPublicKey(encryptData);
		Assert.assertArrayEquals(datas, decryptData);

		encryptData = rsaCoder.encryptByPublicKey(datas);
		decryptData = rsaCoder.decryptByPrivateKey(encryptData);
		Assert.assertArrayEquals(datas, decryptData);
	}
}
```

# ElGamal

```java
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;

import javax.crypto.Cipher;
import javax.crypto.spec.DHParameterSpec;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Random;

public class ElGamalCoder {
	// 非对称加密密钥算法
	private static final String KEY_ALGORITHM = "ElGamal";

	private PublicKey publicKey;
	private PrivateKey privateKey;

	public ElGamalCoder() {
		// 加入BouncyCastleProvider支持
		Security.addProvider(new BouncyCastleProvider());
		try {
			// 实例化算法参数生成器
			AlgorithmParameterGenerator apg = AlgorithmParameterGenerator.getInstance(KEY_ALGORITHM);
			// 初始化算法参数生成器. 密钥长度. ElGamal算法默认密钥长度为1024. 密钥长度范围在160位至16,384位不等。
//			apg.init(KEY_SIZE);
			// 生成算法参数
			AlgorithmParameters params = apg.generateParameters();
			// 构建参数材料
			DHParameterSpec elParams = params.getParameterSpec(DHParameterSpec.class);
			// 实例化密钥对儿生成器
			KeyPairGenerator kpg = KeyPairGenerator.getInstance(KEY_ALGORITHM);
			// 初始化密钥对儿生成器
			kpg.initialize(elParams, new SecureRandom());
			// 生成密钥对儿
			KeyPair keys = kpg.genKeyPair();
			// 取得密钥
			publicKey = keys.getPublic();
			privateKey = keys.getPrivate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 用私钥解密
	 */
	public byte[] decryptByPrivateKey(byte[] data) {
		try {
			// 私钥材料转换
			PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
			// 实例化密钥工厂
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
			// 生成私钥
			Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
			// 对数据解密
			Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			return cipher.doFinal(data);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 用公钥加密
	 */
	public byte[] encryptByPublicKey(byte[] data) {
		try {
			// 公钥材料转换
			X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKey.getEncoded());
			// 实例化密钥工厂
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
			// 生成公钥
			Key publicKey = keyFactory.generatePublic(x509KeySpec);
			// 对数据加密
			Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			return cipher.doFinal(data);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// RSA 算法一次加密的数据不能超过53个字节
	private static final byte[] datas = new byte[53];

	static {
		for (int i = 0; i < 53; i++) {
			datas[i] = (byte)new Random().nextInt(125);
		}
	}

	public static void main(String[] args) {
		ElGamalCoder rsaCoder = new ElGamalCoder();

		byte[] encryptData = rsaCoder.encryptByPublicKey(datas);
		byte[] decryptData = rsaCoder.decryptByPrivateKey(encryptData);
		Assert.assertArrayEquals(datas, decryptData);
	}
}
```

# DH

```java
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Random;

public class DHCoder {
	// 非对称加密密钥算法
	private static final String KEY_ALGORITHM = "DH";

	// 本地密钥算法，即对称加密密钥算法，可选DES、DESede和AES算法
	private static final String SECRET_KEY_ALGORITHM = "AES";

	 // 默认密钥长度. DH算法默认密钥长度为1024 密钥长度必须是64的倍数，其范围在512到1024位之间。
	private static final int KEY_SIZE = 512;

	// 甲方公钥
	private DHPublicKey publicKey1;
	// 甲方私钥
	private DHPrivateKey privateKey1;
	// 乙方公钥
	DHPublicKey publicKey2;
	// 乙方私钥
	DHPrivateKey privateKey2;

	public DHCoder() {
		Security.addProvider(new BouncyCastleProvider());
		try {
			initKey();
			initKey2();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 初始化甲方密钥
	 */
	public void initKey() throws Exception {
		// 实例化密钥对生成器
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
		// 初始化密钥对生成器
		keyPairGenerator.initialize(KEY_SIZE);
		// 生成密钥对
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		// 甲方公钥
		publicKey1 = (DHPublicKey) keyPair.getPublic();
		// 甲方私钥
		privateKey1 = (DHPrivateKey) keyPair.getPrivate();
	}

	/**
	 * 初始化乙方密钥
	 */
	public void initKey2() throws Exception {
		// 解析甲方公钥. 转换公钥材料
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKey1.getEncoded());
		// 实例化密钥工厂
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		// 产生公钥
		PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);
		// 由甲方公钥构建乙方密钥
		DHParameterSpec dhParamSpec = ((DHPublicKey) pubKey).getParams();
		// 实例化密钥对生成器
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(keyFactory.getAlgorithm());
		// 初始化密钥对生成器
		keyPairGenerator.initialize(dhParamSpec);
		// 产生密钥对
		KeyPair keyPair = keyPairGenerator.genKeyPair();
		// 乙方公钥
		publicKey2 = (DHPublicKey) keyPair.getPublic();
		// 乙方私钥
		privateKey2 = (DHPrivateKey) keyPair.getPrivate();
	}

	/**
	 * 加密
	 */
	public byte[] encrypt(byte[] data) throws Exception {
		// 生成本地密钥
		SecretKey secretKey = new SecretKeySpec(publicKey1.getEncoded(), SECRET_KEY_ALGORITHM);
		// 数据加密
		Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		return cipher.doFinal(data);
	}

	/**
	 * 解密
	 */
	public byte[] decrypt(byte[] data) throws Exception {
		// 生成本地密钥
		SecretKey secretKey = new SecretKeySpec(privateKey2.getEncoded(), SECRET_KEY_ALGORITHM);
		// 数据解密
		Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		return cipher.doFinal(data);
	}

	/**
	 * 构建密钥
	 *
	 * @param publicKey
	 *            公钥
	 * @param privateKey
	 *            私钥
	 * @return byte[] 本地密钥
	 * @throws Exception
	 */
	public byte[] getSecretKey(byte[] publicKey, byte[] privateKey) throws Exception {
		// 实例化密钥工厂
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		// 初始化公钥
		// 密钥材料转换
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKey);
		// 产生公钥
		PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);
		// 初始化私钥
		// 密钥材料转换
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKey);
		// 产生私钥
		PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);
		// 实例化
		KeyAgreement keyAgree = KeyAgreement.getInstance(keyFactory.getAlgorithm());
		// 初始化
		keyAgree.init(priKey);
		keyAgree.doPhase(pubKey, true);
		// 生成本地密钥
		SecretKey secretKey = keyAgree.generateSecret(SECRET_KEY_ALGORITHM);
		return secretKey.getEncoded();
	}

	// RSA 算法一次加密的数据不能超过53个字节
	private static final byte[] datas = new byte[53];

	static {
		for (int i = 0; i < 53; i++) {
			datas[i] = (byte)new Random().nextInt(125);
		}
	}

	public static void main(String[] args) throws Exception {
		DHCoder rsaCoder = new DHCoder();

		byte[] encryptData = rsaCoder.encrypt(datas);
		byte[] decryptData = rsaCoder.decrypt(encryptData);
		Assert.assertArrayEquals(datas, decryptData);
	}
}
```