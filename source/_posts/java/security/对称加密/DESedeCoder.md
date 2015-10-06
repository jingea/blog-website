category: 
- security
tag:
- 对称加密
title: DESedeCoder
---
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
			keyFactory = SecretKeyFactory
					.getInstance(KEY_ALGORITHM);
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