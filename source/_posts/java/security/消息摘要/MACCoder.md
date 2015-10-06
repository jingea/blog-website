category: 
- security
tag:
- 消息摘要
title: MACCoder
---
```java

/**
 * MAC消息摘要组件
 * 
 */
public enum MACCoder {

	INSTANCE;

	private Mac hmacMD2;
	private Mac hmacMD4;
	private Mac hmacMD5;
	private Mac hmacSHA1;
	private Mac hmacSHA224;
	private Mac hmacSHA256;
	private Mac hmacSHA384;
	private Mac hmacSHA512;

	MACCoder() {
		// 加入BouncyCastleProvider支持
		Security.addProvider(new BouncyCastleProvider());

		hmacMD2 = getMacBy("HmacMD2");
		hmacMD4 = getMacBy("HmacMD4");
		hmacMD5 = getMacBy("HmacMD5");
		hmacSHA1 = getMacBy("HmacSHA1");
		hmacSHA224 = getMacBy("HmacSHA224");
		hmacSHA256 = getMacBy("HmacSHA256");
		hmacSHA384 = getMacBy("HmacSHA384");
		hmacSHA512 = getMacBy("HmacSHA512");
	}

	public Mac getMacBy(String ar) {

		// 初始化KeyGenerator
		KeyGenerator keyGenerator;
		Mac mac = null;
		try {
			keyGenerator = KeyGenerator.getInstance(ar);
			// 产生秘密密钥
			SecretKey secretKey = keyGenerator.generateKey();

			// 获得密钥
			byte[] key = secretKey.getEncoded();

			// 加入BouncyCastleProvider支持
			Security.addProvider(new BouncyCastleProvider());

			// 还原密钥
			SecretKey secretKey1 = new SecretKeySpec(key, ar);

			// 实例化Mac
			mac = Mac.getInstance(secretKey1.getAlgorithm());

			// 初始化Mac
			mac.init(secretKey1);
		} catch (final Exception e) {
			e.printStackTrace();
		}

		return mac;
	}

	/**
	 * HmacMD2消息摘要
	 * 
	 * @param data
	 *            待做消息摘要处理的数据
	 * @param byte[] 密钥
	 * @return byte[] 消息摘要
	 * @throws Exception
	 */
	public byte[] encodeHmacMD2(byte[] data) {

		// 执行消息摘要
		return hmacMD2.doFinal(data);
	}

	/**
	 * HmacMD2Hex消息摘要
	 * 
	 * @param data
	 *            待做消息摘要处理的数据
	 * @param String
	 *            密钥
	 * @return byte[] 消息摘要
	 * @throws Exception
	 */
	public String encodeHmacMD2Hex(byte[] data) {

		// 执行消息摘要
		byte[] b = encodeHmacMD2(data);

		// 做十六进制转换
		return new String(Hex.encode(b));
	}

	/**
	 * HmacMD4消息摘要
	 * 
	 * @param data
	 *            待做消息摘要处理的数据
	 * @param byte[] 密钥
	 * @return byte[] 消息摘要
	 * @throws Exception
	 */
	public byte[] encodeHmacMD4(byte[] data) {

		// 执行消息摘要
		return hmacMD4.doFinal(data);
	}

	/**
	 * HmacMD4Hex消息摘要
	 * 
	 * @param data
	 *            待做消息摘要处理的数据
	 * @param byte[] 密钥
	 * @return String 消息摘要
	 * @throws Exception
	 */
	public String encodeHmacMD4Hex(byte[] data) {

		// 执行消息摘要
		byte[] b = encodeHmacMD4(data);

		// 做十六进制转换
		return new String(Hex.encode(b));
	}

	/**
	 * HmacSHA224消息摘要
	 * 
	 * @param data
	 *            待做消息摘要处理的数据
	 * @param byte[] 密钥
	 * @return byte[] 消息摘要
	 * @throws Exception
	 */
	public byte[] encodeHmacSHA224(byte[] data) {

		// 执行消息摘要
		return hmacSHA224.doFinal(data);
	}

	/**
	 * HmacSHA224Hex消息摘要
	 * 
	 * @param data
	 *            待做消息摘要处理的数据
	 * @param byte[] 密钥
	 * @return String 消息摘要
	 * @throws Exception
	 */
	public String encodeHmacSHA224Hex(byte[] data) {

		// 执行消息摘要
		byte[] b = encodeHmacSHA224(data);

		// 做十六进制转换
		return new String(Hex.encode(b));
	}

	/**
	 * HmacMD5加密
	 * 
	 * @param data
	 *            待加密数据
	 * @param key
	 *            密钥
	 * @return byte[] 消息摘要
	 * 
	 * @throws Exception
	 */
	public byte[] encodeHmacMD5(byte[] data) {

		// // 还原密钥
		// SecretKey secretKey = new SecretKeySpec(key, "HmacMD5");
		//
		// // 实例化Mac "SslMacMD5"
		// Mac mac = Mac.getInstance("SslMacMD5");// secretKey.getAlgorithm());
		//
		// // 初始化Mac
		// mac.init(secretKey);

		// 执行消息摘要
		return hmacMD5.doFinal(data);
	}

	/**
	 * HmacSHA1加密
	 * 
	 * @param data
	 *            待加密数据
	 * @param key
	 *            密钥
	 * @return byte[] 消息摘要
	 * 
	 * @throws Exception
	 */
	public byte[] encodeHmacSHA(byte[] data) {

		// // 还原密钥
		// SecretKey secretKey = new SecretKeySpec(key, "HMacTiger");
		//
		// // 实例化Mac SslMacMD5
		// Mac mac = Mac.getInstance("SslMacMD5");// secretKey.getAlgorithm());
		//
		// // 初始化Mac
		// mac.init(secretKey);

		// 执行消息摘要
		return hmacSHA1.doFinal(data);
	}

	/**
	 * HmacSHA256加密
	 * 
	 * @param data
	 *            待加密数据
	 * @param key
	 *            密钥
	 * @return byte[] 消息摘要
	 * 
	 * @throws Exception
	 */
	public byte[] encodeHmacSHA256(byte[] data) {

		// // 还原密钥
		// SecretKey secretKey = new SecretKeySpec(key, "HmacSHA256");
		//
		// // 实例化Mac
		// Mac mac = Mac.getInstance(secretKey.getAlgorithm());
		//
		// // 初始化Mac
		// mac.init(secretKey);

		// 执行消息摘要
		return hmacSHA256.doFinal(data);
	}

	/**
	 * HmacSHA384加密
	 * 
	 * @param data
	 *            待加密数据
	 * @param key
	 *            密钥
	 * @return byte[] 消息摘要
	 * 
	 * @throws Exception
	 */
	public byte[] encodeHmacSHA384(byte[] data) {

		// // 还原密钥
		// SecretKey secretKey = new SecretKeySpec(key, "HmacSHA384");
		//
		// // 实例化Mac
		// Mac mac = Mac.getInstance(secretKey.getAlgorithm());
		//
		// // 初始化Mac
		// mac.init(secretKey);

		// 执行消息摘要
		return hmacSHA384.doFinal(data);
	}

	/**
	 * HmacSHA512加密
	 * 
	 * @param data
	 *            待加密数据
	 * @param key
	 *            密钥
	 * @return byte[] 消息摘要
	 * 
	 * @throws Exception
	 */
	public byte[] encodeHmacSHA512(byte[] data) {

		// // 还原密钥
		// SecretKey secretKey = new SecretKeySpec(key, "HmacSHA512");
		//
		// // 实例化Mac
		// Mac mac = Mac.getInstance(secretKey.getAlgorithm());
		//
		// // 初始化Mac
		// mac.init(secretKey);

		// 执行消息摘要
		return hmacSHA512.doFinal(data);
	}

	public String encodeHmacMD5Hex(byte[] data) {
		// 执行消息摘要
		byte[] b = encodeHmacMD5(data);

		// 做十六进制转换
		return new String(Hex.encode(b));
	}

	public String encodeHmacSHA1Hex(byte[] data) {
		// 执行消息摘要
		byte[] b = encodeHmacSHA(data);

		// 做十六进制转换
		return new String(Hex.encode(b));
	}

	public String encodeHmacSHA256Hex(byte[] data) {
		// 执行消息摘要
		byte[] b = encodeHmacSHA256(data);

		// 做十六进制转换
		return new String(Hex.encode(b));
	}

	public String encodeHmacSHA384Hex(byte[] data) {
		// 执行消息摘要
		byte[] b = encodeHmacSHA384(data);

		// 做十六进制转换
		return new String(Hex.encode(b));
	}

	public String encodeHmacSHA512Hex(byte[] data) {
		// 执行消息摘要
		byte[] b = encodeHmacSHA512(data);

		// 做十六进制转换
		return new String(Hex.encode(b));
	}
}
```