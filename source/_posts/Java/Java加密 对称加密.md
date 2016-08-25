category: Java
date: 2014-11-09
title: Java加密 -- 对称加密
---
# 对称加密

## 对称加密算法的由来
目前可知的可通过Java语言实现的对称加密算法大约20多种. java7仅提供部分算法实现,如DES,DESede,AES,Blowfish以及RC2和RC4算法.其他算法通过第三方加密软件包Bouncy Castle实现.在对称加密算法中,DES最具有代表性,堪称典范; DESede是DES算法的变种; AES算法则作为DES算法的替代者;IDEA算法作为一种强加密算法,成为邮件加密软件PGP的核心算法之一.

## 数据加密标准-DES
DES算法和DESede算法统称为DES系列算法. DESede算法是基于DES算法进行三重迭代,增加了算法的安全性.1998年,实用化DES算法破译机的出现彻底宣告DES算法已不具备安全性. 1999年NIST版本新标准,规定

## 分组密码
下面介绍了分组密码的各种工作模式

###  电子密码本模式-ECB
![]()
```java
优点：易于理解且简单易行;便于实现并行操作;没有误差产传递的问题
缺点：不能隐藏明文模式,如果明文重复,则对于的密文也会重复,密文内容很容易被替换,重拍,删除,重放;
对明文主动攻击的可能性较高
用途：适用于加密密钥,随机数等短数据.例如安全地传递DES秘药,ECB是最合适的模式
```

###  密文连接模式-CBC
![](https://raw.githubusercontent.com/wanggnim/blog-website/images/secure/%E5%88%86%E7%BB%84%E6%A8%A1%E5%BC%8FCBC.jpg)
```java
优点：密文连接模式加密后的密文上下文关联,即使在明文中出现重复的信息也不会产生相同的密文;
密文内容如果被替换,重拍,删除,重放或网络传输过程中发生错误,后续密文即被破坏,
无法完成还原;对明文的主动攻击性较低
缺点：不利于并行计算,目前没有已知的并行运算算法;误差传递,如果在加密过程中发生错误,则错误将被无限放大,
导致加密失败;需要初始化向量
用途：可加密任意长度的数据;适用于计算产生检测数据完整性的消息认证码Mac
```

###  密文反馈模式-CFB
![](https://raw.githubusercontent.com/wanggnim/blog-website/images/secure/%E5%88%86%E7%BB%84%E6%A8%A1%E5%BC%8FCFB.jpg)
```java
优点：隐藏了明文的模式,每一个分组的加密结果必受其前面所有分组内容的影响,即使出现许多次相同的明文,
也均产生不同的密文;分组密码转化为流模式,可产生密钥流;可以及时加密传送小于分组的数据
缺点：与CBC相似.不利于并行计算,目前没有已知的并行运算算法;存在误差传递,一个单元损坏影响多个单元;
需要初始化向量.
用途：因错误传播无界,可用于检查发现明文密文的篡改
```

###  输出反馈模式-OFB
![](https://raw.githubusercontent.com/wanggnim/blog-website/images/secure/%E5%88%86%E7%BB%84%E6%A8%A1%E5%BC%8FOFB.jpg)
```java
优点：隐藏了明文的模式;分组密码转化为流模式;无误差传递问题;可以及时加密传送小于分组的数据
缺点：不利于并行计算;对明文的主动攻击是可能的,安全性较CFB差
用途：适用于加密冗余性较大的数据,比如语音和图像数据
```

###  计数器模式-CTR
![](https://raw.githubusercontent.com/wanggnim/blog-website/images/secure/%E5%88%86%E7%BB%84%E6%A8%A1%E5%BC%8FCTR.jpg)
```java
优点：可并行计算;安全性至少与CBC模式一样好;加密与解密仅涉及密码算法的加密
缺点：没有错误传播,因此不易确保数据完整性
用途：适用于各种加密应用
```

## 流密码
![](https://raw.githubusercontent.com/wanggnim/blog-website/images/secure/%E6%B5%81%E6%A8%A1%E5%BC%8F.jpg)
```java
同步流密码
自同步流密码
主要用于军事和外交
常用算法 ： RC4,  SEAL
```

# RCCoder
```java

public enum RCCoder {

	INSTANCE;

	public byte[] encrypt(byte[] data) {

		byte[] encoded = new byte[data.length];
		for(int i = 0; i < data.length; i++) {
			encoded[i] = (byte) ((data[i]) ^ (byte)'a');
		}

		return encoded;
	}

	public byte[] decrypt(byte[] data) {

		byte[] encoded = new byte[data.length];
		for(int i = 0; i < data.length; i++) {
			encoded[i] = (byte) ((data[i]) ^ (byte)'a');
		}

		return encoded;
	}
}
```

# PBECoder
```java

/**
 * PBE安全编码组件 * Java 6 支持以下任意一种算法
 */
public enum PBECoder {

	PBEWithMD5AndDES("PBEWithMD5AndDES"),
	PBEWithMD5AndTripleDES("PBEWithMD5AndTripleDES"),
	PBEWithSHA1AndDESede("PBEWithSHA1AndDESede"),
	PBEWithSHA1AndRC2_40("PBEWithSHA1AndRC2_40");

	PBECoder(String algothrim) {
		ALGORITHM = algothrim;
	}

	public String ALGORITHM = "PBEWithMD5AndTripleDES";

	{
		Security.addProvider(new BouncyCastleProvider());
	}

	/**
	 * 盐初始化<br>
	 * 盐长度必须为8字节
	 *
	 * @return byte[] 盐
	 * @throws Exception
	 */
	public byte[] initSalt() throws Exception {

		SecureRandom random = new SecureRandom();

		return random.generateSeed(8);
	}

	/**
	 * 转换密钥
	 *
	 * @param password
	 *            密码
	 * @return Key 密钥
	 * @throws Exception
	 */
	private Key toKey(String password) throws Exception {

		// 密钥材料转换
		PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray());

		// 实例化
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);

		// 生成密钥
		SecretKey secretKey = keyFactory.generateSecret(keySpec);

		return secretKey;
	}

	/**
	 * 加密
	 *
	 * @param data
	 *            数据
	 * @param password
	 *            密码
	 * @param salt
	 *            盐
	 * @return byte[] 加密数据
	 * @throws Exception
	 */
	public byte[] encrypt(byte[] data, String password, byte[] salt)
			throws Exception {

		// 转换密钥
		Key key = toKey(password);

		// 实例化PBE参数材料
		PBEParameterSpec paramSpec = new PBEParameterSpec(salt, 100);

		// 实例化
		Cipher cipher = Cipher.getInstance(ALGORITHM);

		// 初始化
		cipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);

		// 执行操作
		return cipher.doFinal(data);

	}

	/**
	 * 解密
	 *
	 * @param data
	 *            数据
	 * @param password
	 *            密码
	 * @param salt
	 *            盐
	 * @return byte[] 解密数据
	 * @throws Exception
	 */
	public byte[] decrypt(byte[] data, String password, byte[] salt)
			throws Exception {

		// 转换密钥
		Key key = toKey(password);

		// 实例化PBE参数材料
		PBEParameterSpec paramSpec = new PBEParameterSpec(salt, 100);

		// 实例化
		Cipher cipher = Cipher.getInstance(ALGORITHM);

		// 初始化
		cipher.init(Cipher.DECRYPT_MODE, key, paramSpec);

		// 执行操作
		return cipher.doFinal(data);

	}

}
```

# IDEACoder
```java

/**
 * IDEA安全编码组件
 *
 */
public abstract class IDEACoder {
	/**
	 * 密钥算法
	 */
	public static final String KEY_ALGORITHM = "IDEA";

	/**
	 * 加密/解密算法 / 工作模式 / 填充方式
	 */
	public static final String CIPHER_ALGORITHM = "IDEA/ECB/PKCS5Padding";

	/**
	 * 转换密钥
	 *
	 * @param key
	 *            二进制密钥
	 * @return Key 密钥
	 * @throws Exception
	 */
	private static Key toKey(byte[] key) throws Exception {

		// 生成秘密密钥
		SecretKey secretKey = new SecretKeySpec(key, KEY_ALGORITHM);

		return secretKey;
	}

	{
		Security.addProvider(new BouncyCastleProvider());
	}

	/**
	 * 解密
	 *
	 * @param data
	 *            待解密数据
	 * @param key
	 *            密钥
	 * @return byte[] 解密数据
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] data, byte[] key) throws Exception {

		// 加入BouncyCastleProvider支持
		Security.addProvider(new BouncyCastleProvider());

		// 还原密钥
		Key k = toKey(key);

		// 实例化
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);

		// 初始化，设置为解密模式
		cipher.init(Cipher.DECRYPT_MODE, k);

		// 执行操作
		return cipher.doFinal(data);
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
	public static byte[] encrypt(byte[] data, byte[] key) throws Exception {

		// 加入BouncyCastleProvider支持
		Security.addProvider(new BouncyCastleProvider());

		// 还原密钥
		Key k = toKey(key);

		// 实例化
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);

		// 初始化，设置为加密模式
		cipher.init(Cipher.ENCRYPT_MODE, k);

		// 执行操作
		return cipher.doFinal(data);
	}

	/**
	 * 生成密钥 <br>
	 *
	 * @return byte[] 二进制密钥
	 * @throws Exception
	 */
	public static byte[] initKey() throws Exception {

		// 加入BouncyCastleProvider支持
		Security.addProvider(new BouncyCastleProvider());

		// 实例化
		KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);

		// 初始化
		kg.init(128);

		// 生成秘密密钥
		SecretKey secretKey = kg.generateKey();

		// 获得密钥的二进制编码形式
		return secretKey.getEncoded();
	}

}
```

# DESedeCoder

```java

/**
 * DESede安全编码组件
 * 加密/解密算法 / 工作模式 / 填充方式
 * Java 6支持PKCS5PADDING填充方式
 * Bouncy Castle支持PKCS7Padding填充方式
 */
public enum DESedeCoder {

	ECB_NoPadding(WorkModel.ECB, Padding.NoPadding),
	ECB_PKCS5Padding(WorkModel.ECB, Padding.PKCS5Padding),
	ECB_ISO10126Padding(WorkModel.ECB, Padding.ISO10126Padding),
	CBC_NoPadding(WorkModel.CBC, Padding.NoPadding),
	CBC_PKCS5Padding(WorkModel.CBC, Padding.PKCS5Padding),
	CBC_ISO10126Padding(WorkModel.CBC, Padding.ISO10126Padding),
	PCBC_NoPadding(WorkModel.PCBC, Padding.NoPadding),
	PCBC_PKCS5Padding(WorkModel.PCBC, Padding.PKCS5Padding),
	PCBC_ISO10126Padding(WorkModel.PCBC, Padding.ISO10126Padding),
	CTR_NoPadding(WorkModel.CTR, Padding.NoPadding),
	CTR_PKCS5Padding(WorkModel.CTR, Padding.PKCS5Padding),
	CTR_ISO10126Padding(WorkModel.CTR, Padding.ISO10126Padding),
	CTS_NoPadding(WorkModel.CTS, Padding.NoPadding),
	CTS_PKCS5Padding(WorkModel.CTS, Padding.PKCS5Padding),
	CTS_ISO10126Padding(WorkModel.CTS, Padding.ISO10126Padding),
	CFB_NoPadding(WorkModel.CFB, Padding.NoPadding),
	CFB_PKCS5Padding(WorkModel.CFB, Padding.PKCS5Padding),
	CFB_ISO10126Padding(WorkModel.CFB, Padding.ISO10126Padding),
	OFB_NoPadding(WorkModel.OFB, Padding.NoPadding),
	OFB_PKCS5Padding(WorkModel.OFB, Padding.PKCS5Padding),
	OFB_ISO10126Padding(WorkModel.OFB, Padding.ISO10126Padding),
	;

	DESedeCoder(WorkModel workModel, Padding padding) {
		CIPHER_ALGORITHM = KEY_ALGORITHM + "/" + workModel.name() + "/" + padding.name();
		this.padding = padding;
		this.workModel = workModel;
	}

	{
		Security.addProvider(new BouncyCastleProvider());
	}

	/**
	 * 密钥算法
	 */
	private final String KEY_ALGORITHM = "DESede";

	private String CIPHER_ALGORITHM = "DESede/ECB/PKCS5Padding";

	private Padding padding;
	private WorkModel workModel;

	/**
	 * 转换密钥
	 *
	 * @param key
	 *            二进制密钥
	 * @return Key 密钥
	 * @throws Exception
	 */
	private Key toKey(byte[] key) {

		// 实例化DES密钥材料
		DESedeKeySpec dks = null;
		try {
			dks = new DESedeKeySpec(key);
		} catch (InvalidKeyException e) {
			System.out.println(this + "\t" + e.getMessage());
		}

		// 实例化秘密密钥工厂
		SecretKeyFactory keyFactory = null;
		try {
			keyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			System.out.println(this + "\t" + e.getMessage());
		}

		// 生成秘密密钥
		SecretKey secretKey = null;
		try {
			secretKey = keyFactory.generateSecret(dks);
		} catch (InvalidKeySpecException e) {
			System.out.println(this + "\t" + e.getMessage());
		}

		return secretKey;
	}

	/**
	 * 解密
	 *
	 * @param data
	 *            待解密数据
	 * @param key
	 *            密钥
	 * @return byte[] 解密数据
	 * @throws Exception
	 */
	public byte[] decrypt(byte[] data, byte[] key) {

		// 还原密钥
		Key k = toKey(key);

		/*
		 * 实例化
		 * 使用PKCS7Padding填充方式
		 * Cipher.getInstance(CIPHER_ALGORITHM, "BC");
		 */
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			System.out.println(this + "\t" + e.getMessage());
		} catch (NoSuchPaddingException e) {
			System.out.println(this + "\t" + e.getMessage());
		}

		// 初始化，设置为解密模式
		try {
			cipher.init(Cipher.DECRYPT_MODE, k);
		} catch (InvalidKeyException e) {
			System.out.println(this + "\t" + e.getMessage());
		}

		// 执行操作
		try {
			return cipher.doFinal(data);
		} catch (IllegalBlockSizeException e) {
			System.out.println(this + "\t" + e.getMessage());
		} catch (BadPaddingException e) {
			System.out.println(this + "\t" + e.getMessage());
		} catch (IllegalStateException e) {
			System.out.println(this + "\t" + e.getMessage());
		}

		return null;
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
	public byte[] encrypt(byte[] data, byte[] key) {

		// 还原密钥
		Key k = toKey(key);

		/*
		 * 实例化
		 * 使用PKCS7Padding填充方式
		 * Cipher.getInstance(CIPHER_ALGORITHM, "BC");
		 */
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			System.out.println(this + "\t" + e.getMessage());
		} catch (NoSuchPaddingException e) {
			System.out.println(this + "\t" + e.getMessage());
		}

		// 初始化，设置为加密模式
		try {
			cipher.init(Cipher.ENCRYPT_MODE, k);
		} catch (InvalidKeyException e) {
			System.out.println(this + "\t" + e.getMessage());
		}

		// 执行操作
		try {
			return cipher.doFinal(data);
		} catch (IllegalBlockSizeException e) {
			System.out.println(this + "\t" + e.getMessage());
		} catch (BadPaddingException e) {
			System.out.println(this + "\t" + e.getMessage());
		}

		return null;
	}

	/**
	 * 生成密钥 <br>
	 *
	 * @return byte[] 二进制密钥
	 * @throws Exception
	 */
	public byte[] initEncodedSecretKey() {

		// 实例化
		KeyGenerator kg = null;
		try {
			kg = KeyGenerator.getInstance(KEY_ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			System.out.println(this + "\t" + e.getMessage());
		}

		/*
		 * DESede 要求密钥长度为 112位或168位
		 */
		kg.init(168);

		// 生成秘密密钥
		SecretKey secretKey = kg.generateKey();

		// 获得密钥的二进制编码形式
		return secretKey.getEncoded();
	}

}
```

# DESCoder

```java

/**
 * DES安全编码组件
 * 密钥算法 <br>
 * Java 6 只支持56bit密钥 <br>
 * Bouncy Castle 支持64bit密钥
 */
public enum DESCoder {

	ECB_NoPadding(WorkModel.ECB, Padding.NoPadding),
	ECB_PKCS5Padding(WorkModel.ECB, Padding.PKCS5Padding),
	ECB_ISO10126Padding(WorkModel.ECB, Padding.ISO10126Padding),
	CBC_NoPadding(WorkModel.CBC, Padding.NoPadding),
	CBC_PKCS5Padding(WorkModel.CBC, Padding.PKCS5Padding),
	CBC_ISO10126Padding(WorkModel.CBC, Padding.ISO10126Padding),
	PCBC_NoPadding(WorkModel.PCBC, Padding.NoPadding),
	PCBC_PKCS5Padding(WorkModel.PCBC, Padding.PKCS5Padding),
	PCBC_ISO10126Padding(WorkModel.PCBC, Padding.ISO10126Padding),
	CTR_NoPadding(WorkModel.CTR, Padding.NoPadding),
	CTR_PKCS5Padding(WorkModel.CTR, Padding.PKCS5Padding),
	CTR_ISO10126Padding(WorkModel.CTR, Padding.ISO10126Padding),
	CTS_NoPadding(WorkModel.CTS, Padding.NoPadding),
	CTS_PKCS5Padding(WorkModel.CTS, Padding.PKCS5Padding),
	CTS_ISO10126Padding(WorkModel.CTS, Padding.ISO10126Padding),
	CFB_NoPadding(WorkModel.CFB, Padding.NoPadding),
	CFB_PKCS5Padding(WorkModel.CFB, Padding.PKCS5Padding),
	CFB_ISO10126Padding(WorkModel.CFB, Padding.ISO10126Padding),
	OFB_NoPadding(WorkModel.OFB, Padding.NoPadding),
	OFB_PKCS5Padding(WorkModel.OFB, Padding.PKCS5Padding),
	OFB_ISO10126Padding(WorkModel.OFB, Padding.ISO10126Padding),
	;

	/**
	 * 加密/解密算法 / 工作模式 / 填充方式
	 */
	DESCoder(WorkModel workModel, Padding padding) {
		CIPHER_ALGORITHM = KEY_ALGORITHM + "/" + workModel.name() + "/" + padding.name();
		this.padding = padding;
		this.workModel = workModel;
	}

	{
		Security.addProvider(new BouncyCastleProvider());
	}

	private final String KEY_ALGORITHM = "DES";

	private String CIPHER_ALGORITHM;

	private Padding padding;
	private WorkModel workModel;
	/**
	 * 转换密钥
	 *
	 * @param key
	 *            二进制密钥
	 * @return Key 密钥
	 * @throws Exception
	 */
	private Key toKey(byte[] key) {

		SecretKey secretKey = null;
		try {
			// 实例化DES密钥材料
			DESKeySpec dks = new DESKeySpec(key);
			// 实例化秘密密钥工厂
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
			// 生成秘密密钥
			secretKey = keyFactory.generateSecret(dks);
		} catch (final Exception e) {
			System.out.println("toKey : " + this + "\t" + e.getMessage());
		}

		return secretKey;
	}

	/**
	 * 解密
	 *
	 * @param data
	 *            待解密数据
	 * @param key
	 *            密钥
	 * @return byte[] 解密数据
	 * @throws Exception
	 */
	public byte[] decrypt(byte[] data, byte[] key) {


		byte[] result = null;
		try {
			// 还原密钥
			Key k = toKey(key);
			// 实例化
			Cipher cipher;
			cipher = Cipher.getInstance(CIPHER_ALGORITHM);
			// 初始化，设置为解密模式
			cipher.init(Cipher.DECRYPT_MODE, k);
			// 执行操作
			result = cipher.doFinal(data);

		} catch (final Exception e) {
			System.out.println("decrypt : " + this + "\t" + e.getMessage());
		}

		return result;
	}

	/**
	 * 加密
	 *
	 * 加密数据在下面几种情况下,必须满足长度和倍数关系, 否则会抛出下面异常
	 * CTS_NoPadding	input is too short!
	 * CTS_PKCS5Padding	input is too short!
	 * CBC_NoPadding	Input length not multiple of 8 bytes
	 * PCBC_NoPadding	Input length not multiple of 8 bytes
	 * ECB_NoPadding	Input length not multiple of 8 bytes
	 *
	 * @param data
	 *            待加密数据
	 * @param key
	 *            密钥
	 * @return byte[] 加密数据
	 * @throws Exception
	 */
	public byte[] encrypt(byte[] data, byte[] key) {

		byte[] result = null;
		try {
			// 还原密钥
			Key k = toKey(key);

			// 实例化
			Cipher cipher;
			cipher = Cipher.getInstance(CIPHER_ALGORITHM);
			// 初始化，设置为加密模式
			cipher.init(Cipher.ENCRYPT_MODE, k);

			// 执行操作
			result = cipher.doFinal(data);
		} catch (final Exception e) {
			System.out.println("encrypt : " + this + "\t" + e.getMessage());
		}

		return result;
	}

	/**
	 * 生成密钥 <br>
	 * Java 6 只支持56bit密钥 <br>
	 * Bouncy Castle 支持64bit密钥 <br>
	 *
	 * @return byte[] 二进制密钥
	 * @throws Exception
	 */
	public byte[] initEncodedSecretKey() {

		/*
		 * 实例化密钥生成器
		 *
		 * 若要使用64bit密钥注意替换 将下述代码中的KeyGenerator.getInstance(CIPHER_ALGORITHM);
		 * 替换为KeyGenerator.getInstance(CIPHER_ALGORITHM, "BC");
		 */
		KeyGenerator kg = null;
		try {
			kg = KeyGenerator.getInstance(KEY_ALGORITHM, "BC");
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			System.out.println(this + "\t" + e.getMessage());
		}

		/*
		 * 初始化密钥生成器 若要使用64bit密钥注意替换 将下述代码kg.init(56); 替换为kg.init(64);
		 */
		kg.init(64, new SecureRandom());

		// 生成秘密密钥
		SecretKey secretKey = kg.generateKey();

		// 获得密钥的二进制编码形式
		return secretKey.getEncoded();
	}


}
```

# AESCoder

```java

/**
 * AES安全编码组件
 *
 */
public enum AESCoder {

	ECB_NoPadding(WorkModel.ECB, Padding.NoPadding),
	ECB_PKCS5Padding(WorkModel.ECB, Padding.PKCS5Padding),
	ECB_ISO10126Padding(WorkModel.ECB, Padding.ISO10126Padding),
	CBC_NoPadding(WorkModel.CBC, Padding.NoPadding),
	CBC_PKCS5Padding(WorkModel.CBC, Padding.PKCS5Padding),
	CBC_ISO10126Padding(WorkModel.CBC, Padding.ISO10126Padding),
	PCBC_NoPadding(WorkModel.PCBC, Padding.NoPadding),
	PCBC_PKCS5Padding(WorkModel.PCBC, Padding.PKCS5Padding),
	PCBC_ISO10126Padding(WorkModel.PCBC, Padding.ISO10126Padding),
	CTR_NoPadding(WorkModel.CTR, Padding.NoPadding),
	CTR_PKCS5Padding(WorkModel.CTR, Padding.PKCS5Padding),
	CTR_ISO10126Padding(WorkModel.CTR, Padding.ISO10126Padding),
	CTS_NoPadding(WorkModel.CTS, Padding.NoPadding),
	CTS_PKCS5Padding(WorkModel.CTS, Padding.PKCS5Padding),
	CTS_ISO10126Padding(WorkModel.CTS, Padding.ISO10126Padding),
	CFB_NoPadding(WorkModel.CFB, Padding.NoPadding),
	CFB_PKCS5Padding(WorkModel.CFB, Padding.PKCS5Padding),
	CFB_ISO10126Padding(WorkModel.CFB, Padding.ISO10126Padding),
	;

	/**
	 * 加密/解密算法 / 工作模式 / 填充方式
	 * Java 6支持PKCS5Padding填充方式
	 * Bouncy Castle支持PKCS7Padding填充方式
	 */
	AESCoder(WorkModel workModel, Padding padding) {
		CIPHER_ALGORITHM = KEY_ALGORITHM + "/" + workModel.name() + "/" + padding.name();
		this.padding = padding;
		this.workModel = workModel;
	}

	{
		Security.addProvider(new BouncyCastleProvider());
	}

	private Padding padding;
	private WorkModel workModel;

	/**
	 * 密钥算法
	 */
	public static final String KEY_ALGORITHM = "AES";

	private String CIPHER_ALGORITHM;

	/**
	 * 转换密钥
	 *
	 * @param key 二进制密钥
	 * @return Key 密钥
	 * @throws Exception
	 */
	private Key toKey(byte[] key) {

		// 实例化AES密钥材料
		SecretKey secretKey = new SecretKeySpec(key, KEY_ALGORITHM);

		return secretKey;
	}

	/**
	 * 解密
	 *
	 * @param data 待解密数据
	 * @param key 密钥
	 * @return byte[] 解密数据
	 * @throws Exception
	 */
	public byte[] decrypt(byte[] data, byte[] key) {

		// 还原密钥
		Key k = toKey(key);

		/*
		 * 实例化
		 * 使用PKCS7Padding填充方式，按如下方式实现
		 * Cipher.getInstance(CIPHER_ALGORITHM, "BC");
		 */
		try {
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
			// 初始化，设置为解密模式
			cipher.init(Cipher.DECRYPT_MODE, k);
			// 执行操作
			return cipher.doFinal(data);
		} catch (final Exception e) {
			System.out.println(this + " - decrypt - " + e.getMessage());
		}

		return null;
	}

	/**
	 * 加密
	 *
	 * @param data 待加密数据
	 * @param key 密钥
	 * @return byte[] 加密数据
	 * @throws Exception
	 */
	public byte[] encrypt(byte[] data, byte[] key) {

		// 还原密钥
		Key k = toKey(key);

		/*
		 * 实例化
		 * 使用PKCS7Padding填充方式，按如下方式实现
		 * Cipher.getInstance(CIPHER_ALGORITHM, "BC");
		 */
		byte[] result = null;
		try {
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
			// 初始化，设置为加密模式
			cipher.init(Cipher.ENCRYPT_MODE, k);
			// 执行操作
			result = cipher.doFinal(data);
		} catch (final Exception e) {
			System.out.println(this + " - encrypt - " + e.getMessage());
		}

		return result;
	}

	/**
	 * 生成密钥 <br>
	 *
	 * @return byte[] 二进制密钥
	 * @throws Exception
	 */
	public byte[] initKey() {

		// 实例化
		KeyGenerator kg = null;
		try {
			kg = KeyGenerator.getInstance(KEY_ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			System.out.println(this + "  " + e.getMessage());
		}

		/*
		 * AES 要求密钥长度为 128位、192位或 256位
		 */
		kg.init(256);

		// 生成秘密密钥
		SecretKey secretKey = kg.generateKey();

		// 获得密钥的二进制编码形式
		return secretKey.getEncoded();
	}

}
```
