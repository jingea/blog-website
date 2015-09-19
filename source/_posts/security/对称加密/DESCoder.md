category: 
- security
tag:
- 对称加密
title: DESCoder
---
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
			SecretKeyFactory keyFactory = SecretKeyFactory
					.getInstance(KEY_ALGORITHM);
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