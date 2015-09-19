category: 
- security
tag:
- 对称加密
title: AESCoder
---
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