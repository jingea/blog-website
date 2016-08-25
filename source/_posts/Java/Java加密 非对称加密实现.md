﻿category: Java
date: 2014-11-010
title: Java加密 -- 非对称加密
---
# 非对称密码

非对称密码与对称密码体制相对,他们的主要区别在于：非对称密码体制的加密密钥和解密密钥不相同,分为俩个密钥,一个公开(公钥),一个保密(密钥).

![非对称密码体制的保密通信模型]()

在非对称密码体制中,公玥与私钥均可用于加密与解密操作,但它与对称密码体制有极大的不同. 公玥与私钥分属通信双方,一份消息的加密与解密需要公玥和私钥共同参与. 公玥加密需要私钥解密, 反之, 私钥加密需要公玥解密.

![公玥加密-私钥解密的保密通信模型]()

非对称密码的体制的主要优点是可以适应于开放性的使用环境, 秘钥管理相对简单, 可以方便安全地实现数字签名和验证. RSA是非对称密码体制的典范,它不仅仅可以完成一般的数据加密操作,同时也支持数字签名和验证. 除了数字签名非对称密码体制还支持数字信封等技术.

非对称密码算法的安全性完全依赖于基于计算机复杂度上的难题,通常来自于数论.例如：
* RSA来源于整数因子分解问题.
* DSA-数字签名算法源于离散对数问题.
* ECC-椭圆曲线加密算法源于离散对数问题.
由于这些数学难题的实现多涉及底层模数乘法和指数运算,相比分组密码需要更多的计算机资源, 为了尼补这一缺陷, 非对称密码系统通常是复合式的:用高效率的对称密码算法进行加密解密处理; 用非对称密钥加密对称密码系统所使用的密钥, 通过这种复合方式增进效率.

# RSACoder

```java
/**
 * RSA安全编码组件
 * 
 */
public enum RSACoder {

	INSTANCE;
	
	{
		Security.addProvider(new BouncyCastleProvider());
	}
	
	/**
	 * 非对称加密密钥算法
	 */
	private static final String KEY_ALGORITHM = "RSA";

	/**
	 * 公钥
	 */
	private static final String PUBLIC_KEY = "RSAPublicKey";

	/**
	 * 私钥
	 */
	private static final String PRIVATE_KEY = "RSAPrivateKey";

	/**
	 * RSA密钥长度 
	 * 默认1024位，
	 * 密钥长度必须是64的倍数， 
	 * 范围在512至65536位之间。
	 */
	private static final int KEY_SIZE = 512;

	/**
	 * 私钥解密
	 * 
	 * @param data
	 *            待解密数据
	 * @param key
	 *            私钥
	 * @return byte[] 解密数据
	 * @throws Exception
	 */
	public byte[] decryptByPrivateKey(byte[] data, byte[] key)
			throws Exception {

		// 取得私钥
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(key);

		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

		// 生成私钥
		PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

		// 对数据解密
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());

		cipher.init(Cipher.DECRYPT_MODE, privateKey);

		return cipher.doFinal(data);
	}

	/**
	 * 公钥解密
	 * 
	 * @param data
	 *            待解密数据
	 * @param key
	 *            公钥
	 * @return byte[] 解密数据
	 * @throws Exception
	 */
	public byte[] decryptByPublicKey(byte[] data, byte[] key)
			throws Exception {

		// 取得公钥
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);

		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

		// 生成公钥
		PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);

		// 对数据解密
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());

		cipher.init(Cipher.DECRYPT_MODE, publicKey);

		return cipher.doFinal(data);
	}

	/**
	 * 公钥加密
	 * 
	 * @param data
	 *            待加密数据
	 * @param key
	 *            公钥
	 * @return byte[] 加密数据
	 * @throws Exception
	 */
	public byte[] encryptByPublicKey(byte[] data, byte[] key)
			throws Exception {

		// 取得公钥
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);

		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

		PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);

		// 对数据加密
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());

		cipher.init(Cipher.ENCRYPT_MODE, publicKey);

		return cipher.doFinal(data);
	}

	/**
	 * 私钥加密
	 * 
	 * @param data
	 *            待加密数据
	 * @param key
	 *            私钥
	 * @return byte[] 加密数据
	 * @throws Exception
	 */
	public byte[] encryptByPrivateKey(byte[] data, byte[] key)
			throws Exception {

		// 取得私钥
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(key);

		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

		// 生成私钥
		PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

		// 对数据加密
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());

		cipher.init(Cipher.ENCRYPT_MODE, privateKey);

		return cipher.doFinal(data);
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
	 * 初始化密钥
	 * 
	 * @return Map 密钥Map
	 * @throws Exception
	 */
	public Map<String, Object> initKey() throws Exception {

		// 实例化密钥对生成器
		KeyPairGenerator keyPairGen = KeyPairGenerator
				.getInstance(KEY_ALGORITHM);

		// 初始化密钥对生成器
		keyPairGen.initialize(KEY_SIZE);

		// 生成密钥对
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

# ElGamalCoder

```java

/**
 * ElGamal安全编码组件
 * 
 */
public enum ElGamalCoder {

	INSTANCE;
	
	{
		Security.addProvider(new BouncyCastleProvider());
	}
	
	/**
	 * 非对称加密密钥算法
	 */
	private static final String KEY_ALGORITHM = "ElGamal";

	/**
	 * 密钥长度
	 * 
	 * ElGamal算法默认密钥长度为1024 
	 * 密钥长度范围在160位至16,384位不等。
	 */
	private static final int KEY_SIZE = 256;

	/**
	 * 公钥
	 */
	private static final String PUBLIC_KEY = "ElGamalPublicKey";

	/**
	 * 私钥
	 */
	private static final String PRIVATE_KEY = "ElGamalPrivateKey";

	/**
	 * 用私钥解密
	 * 
	 * @param data
	 *            待解密数据
	 * @param key
	 *            私钥
	 * @return byte[] 解密数据
	 * @throws Exception
	 */
	public byte[] decryptByPrivateKey(byte[] data, byte[] key)
			throws Exception {

		// 加入BouncyCastleProvider支持
		Security.addProvider(new BouncyCastleProvider());

		// 私钥材料转换
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(key);

		// 实例化密钥工厂
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

		// 生成私钥
		Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

		// 对数据解密
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());

		cipher.init(Cipher.DECRYPT_MODE, privateKey);

		return cipher.doFinal(data);
	}

	/**
	 * 用公钥加密
	 * 
	 * @param data
	 *            待加密数据
	 * @param key
	 *            公钥
	 * @return byte[] 加密数据
	 * @throws Exception
	 */
	public byte[] encryptByPublicKey(byte[] data, byte[] key)
			throws Exception {

		// 加入BouncyCastleProvider支持
		Security.addProvider(new BouncyCastleProvider());

		// 公钥材料转换
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);

		// 实例化密钥工厂
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

		// 生成公钥
		Key publicKey = keyFactory.generatePublic(x509KeySpec);

		// 对数据加密
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());

		cipher.init(Cipher.ENCRYPT_MODE, publicKey);

		return cipher.doFinal(data);
	}

	/**
	 * 生成密钥
	 * 
	 * @return Map 密钥Map
	 * @throws Exception
	 */
	public Map<String, Object> initKey() throws Exception {

		// 加入BouncyCastleProvider支持
		Security.addProvider(new BouncyCastleProvider());

		// 实例化算法参数生成器
		AlgorithmParameterGenerator apg = AlgorithmParameterGenerator
				.getInstance(KEY_ALGORITHM);

		// 初始化算法参数生成器
		apg.init(KEY_SIZE);

		// 生成算法参数
		AlgorithmParameters params = apg.generateParameters();

		// 构建参数材料
		DHParameterSpec elParams = (DHParameterSpec) params.getParameterSpec(DHParameterSpec.class);

		// 实例化密钥对儿生成器
		KeyPairGenerator kpg = KeyPairGenerator.getInstance(KEY_ALGORITHM);

		// 初始化密钥对儿生成器
		kpg.initialize(elParams, new SecureRandom());

		// 生成密钥对儿
		KeyPair keys = kpg.genKeyPair();

		// 取得密钥
		PublicKey publicKey = keys.getPublic();

		PrivateKey privateKey = keys.getPrivate();

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
	 * @return
	 * @throws Exception
	 */
	public byte[] getPublicKey(Map<String, Object> keyMap)
			throws Exception {

		Key key = (Key) keyMap.get(PUBLIC_KEY);

		return key.getEncoded();
	}
}
```

# DHCoder

```java

/**
 * DH安全编码组件
 * 
 */
public enum DHCoder {

	INSTANCE;
	
	{
		Security.addProvider(new BouncyCastleProvider());
	}
	
	/**
	 * 非对称加密密钥算法
	 */
	private static final String KEY_ALGORITHM = "DH";

	/**
	 * 本地密钥算法，即对称加密密钥算法，可选DES、DESede和AES算法
	 */
	private static final String SECRET_KEY_ALGORITHM = "AES";

	/**
	 * 默认密钥长度
	 * 
	 * DH算法默认密钥长度为1024 密钥长度必须是64的倍数，其范围在512到1024位之间。
	 */
	private static final int KEY_SIZE = 512;

	/**
	 * 公钥
	 */
	private static final String PUBLIC_KEY = "DHPublicKey";

	/**
	 * 私钥
	 */
	private static final String PRIVATE_KEY = "DHPrivateKey";

	/**
	 * 初始化甲方密钥
	 * 
	 * @return Map 甲方密钥Map
	 * @throws Exception
	 */
	public Map<String, Object> initKey() throws Exception {

		// 实例化密钥对生成器
		KeyPairGenerator keyPairGenerator = KeyPairGenerator
				.getInstance(KEY_ALGORITHM);

		// 初始化密钥对生成器
		keyPairGenerator.initialize(KEY_SIZE);

		// 生成密钥对
		KeyPair keyPair = keyPairGenerator.generateKeyPair();

		// 甲方公钥
		DHPublicKey publicKey = (DHPublicKey) keyPair.getPublic();

		// 甲方私钥
		DHPrivateKey privateKey = (DHPrivateKey) keyPair.getPrivate();

		// 将密钥对存储在Map中
		Map<String, Object> keyMap = new HashMap<String, Object>(2);

		keyMap.put(PUBLIC_KEY, publicKey);
		keyMap.put(PRIVATE_KEY, privateKey);

		return keyMap;
	}

	/**
	 * 初始化乙方密钥
	 * 
	 * @param key
	 *            甲方公钥
	 * @return Map 乙方密钥Map
	 * @throws Exception
	 */
	public Map<String, Object> initKey(byte[] key) throws Exception {

		// 解析甲方公钥
		// 转换公钥材料
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);

		// 实例化密钥工厂
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

		// 产生公钥
		PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);

		// 由甲方公钥构建乙方密钥
		DHParameterSpec dhParamSpec = ((DHPublicKey) pubKey).getParams();

		// 实例化密钥对生成器
		KeyPairGenerator keyPairGenerator = KeyPairGenerator
				.getInstance(keyFactory.getAlgorithm());

		// 初始化密钥对生成器
		keyPairGenerator.initialize(dhParamSpec);

		// 产生密钥对
		KeyPair keyPair = keyPairGenerator.genKeyPair();

		// 乙方公钥
		DHPublicKey publicKey = (DHPublicKey) keyPair.getPublic();

		// 乙方私钥
		DHPrivateKey privateKey = (DHPrivateKey) keyPair.getPrivate();

		// 将密钥对存储在Map中
		Map<String, Object> keyMap = new HashMap<String, Object>(2);

		keyMap.put(PUBLIC_KEY, publicKey);
		keyMap.put(PRIVATE_KEY, privateKey);

		return keyMap;
	}

	/**
	 * 加密
	 * 
	 * @param data
	 *            待加密数据
	 * @param key
	 *            密钥
	 * @return byte[] 加密数据
	 * @throws Exception
	 */
	public byte[] encrypt(byte[] data, byte[] key) throws Exception {

		// 生成本地密钥
		SecretKey secretKey = new SecretKeySpec(key, SECRET_KEY_ALGORITHM);

		// 数据加密
		Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm());

		cipher.init(Cipher.ENCRYPT_MODE, secretKey);

		return cipher.doFinal(data);
	}

	/**
	 * 解密<br>
	 * 
	 * @param data
	 *            待解密数据
	 * @param key
	 *            密钥
	 * @return byte[] 解密数据
	 * @throws Exception
	 */
	public byte[] decrypt(byte[] data, byte[] key) throws Exception {

		// 生成本地密钥
		SecretKey secretKey = new SecretKeySpec(key, SECRET_KEY_ALGORITHM);

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
	public byte[] getSecretKey(byte[] publicKey, byte[] privateKey)
			throws Exception {

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
		KeyAgreement keyAgree = KeyAgreement.getInstance(keyFactory
				.getAlgorithm());

		// 初始化
		keyAgree.init(priKey);

		keyAgree.doPhase(pubKey, true);

		// 生成本地密钥
		SecretKey secretKey = keyAgree.generateSecret(SECRET_KEY_ALGORITHM);

		return secretKey.getEncoded();
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
}
```