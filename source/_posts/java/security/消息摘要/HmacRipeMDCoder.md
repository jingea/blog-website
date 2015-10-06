category: 
- security
tag:
- 消息摘要
title: HmacRipeMDCoder
---
```java

/**
 * HmacRipeMD系列加密组件<br>
 * HmacRipeMD128、HmacRipeMD160共2种算法。<br>
 * 
 */
public enum HmacRipeMDCoder {

	INSTANCE;

	private Mac hmacRipeMD128;
	private Mac hmacRipeMD160;

	HmacRipeMDCoder() {
		// 加入BouncyCastleProvider支持
		Security.addProvider(new BouncyCastleProvider());
		hmacRipeMD128 = getMac("HmacRipeMD128");
		hmacRipeMD160 = getMac("HmacRipeMD160");
	}

	/**
	 * 初始化HmacRipeMD128密钥
	 * 
	 * @return byte[] 密钥
	 * 
	 */
	public Mac getMac(String key1) {

		// 初始化KeyGenerator
		Mac mac = null;
		try {
			KeyGenerator keyGenerator;
			keyGenerator = KeyGenerator.getInstance(key1);
			// 产生秘密密钥
			SecretKey secretKey = keyGenerator.generateKey();

			// 获得密钥
			byte[] key = secretKey.getEncoded();

			// 还原密钥
			SecretKey secretKey1 = new SecretKeySpec(key, key1);

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
	 * HmacRipeMD128消息摘要
	 * 
	 * @param data
	 *            待做消息摘要处理的数据
	 * @param byte[] 密钥
	 * @return byte[] 消息摘要
	 * 
	 */
	public byte[] encodeHmacRipeMD128(byte[] data) {

		// 执行消息摘要
		return hmacRipeMD128.doFinal(data);
	}

	/**
	 * HmacRipeMD128Hex消息摘要
	 * 
	 * @param data
	 *            待做消息摘要处理的数据
	 * @param String
	 *            密钥
	 * @return byte[] 消息摘要
	 * 
	 */
	public String encodeHmacRipeMD128Hex(byte[] data) {

		// 执行消息摘要
		byte[] b = encodeHmacRipeMD128(data);

		// 做十六进制转换
		return new String(Hex.encode(b));
	}

	/**
	 * HmacRipeMD160消息摘要
	 * 
	 * @param data
	 *            待做消息摘要处理的数据
	 * @param byte[] 密钥
	 * @return byte[] 消息摘要
	 * 
	 */
	public byte[] encodeHmacRipeMD160(byte[] data) {

		// 执行消息摘要
		return hmacRipeMD160.doFinal(data);
	}

	/**
	 * HmacRipeMD160Hex消息摘要
	 * 
	 * @param data
	 *            待做消息摘要处理的数据
	 * @param byte[] 密钥
	 * @return String 消息摘要
	 * 
	 */
	public String encodeHmacRipeMD160Hex(byte[] data) {

		// 执行消息摘要
		byte[] b = encodeHmacRipeMD160(data);

		// 做十六进制转换
		return new String(Hex.encode(b));
	}
}
```