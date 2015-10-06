category: 
- security
tag:
- 消息摘要
title: SHACoder
---
```java
/**
 * RipeMD系列消息摘要组件<br>
 * 包含RipeMD128、RipeMD160、RipeMD256和RipeMD320共4种RipeMD系列算法，<br>
 * 
 */
public enum RipeMDCoder {

	INSTANCE;
	
	RipeMDCoder() {
		// 加入BouncyCastleProvider支持
		Security.addProvider(new BouncyCastleProvider());
		// 初始化MessageDigest
		try {
			ripeMD160 = MessageDigest.getInstance("RipeMD160");
			ripeMD256 = MessageDigest.getInstance("RipeMD256");
			ripeMD320 = MessageDigest.getInstance("RipeMD320");
			ripeMD128 = MessageDigest.getInstance("RipeMD128");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	private MessageDigest ripeMD128;
	private MessageDigest ripeMD160;
	private MessageDigest ripeMD256;
	private MessageDigest ripeMD320;
	
	/**
	 * RipeMD128消息摘要
	 * 
	 * @param data 待做消息摘要处理的数据
	 * @return byte[] 消息摘要 
	 * 
	 */
	public byte[] encodeRipeMD128(byte[] data) {

		// 执行消息摘要
		return ripeMD128.digest(data);
	}

	/**
	 * RipeMD128Hex消息摘要
	 * 
	 * @param data 待做消息摘要处理的数据
	 * @return byte[] 消息摘要 
	 * 
	 */
	public String encodeRipeMD128Hex(byte[] data) {

		// 执行消息摘要
		byte[] b = encodeRipeMD128(data);

		// 做十六进制编码处理
		return new String(Hex.encode(b));
	}

	/**
	 * RipeMD160消息摘要
	 * 
	 * @param data 待做消息摘要处理的数据
	 * @return byte[] 消息摘要 
	 * 
	 */
	public byte[] encodeRipeMD160(byte[] data) {

		// 执行消息摘要
		return ripeMD160.digest(data);
	}

	/**
	 * RipeMD160Hex消息摘要
	 * 
	 * @param data 待做消息摘要处理的数据
	 * @return byte[] 消息摘要 
	 * 
	 */
	public String encodeRipeMD160Hex(byte[] data) {

		// 执行消息摘要
		byte[] b = encodeRipeMD160(data);

		// 做十六进制编码处理
		return new String(Hex.encode(b));
	}

	/**
	 * RipeMD256消息摘要
	 * 
	 * @param data 待做消息摘要处理的数据
	 * @return byte[] 消息摘要 
	 * 
	 */
	public byte[] encodeRipeMD256(byte[] data) {

		// 执行消息摘要
		return ripeMD256.digest(data);
	}

	/**
	 * RipeMD256Hex消息摘要
	 * 
	 * @param data 待做消息摘要处理的数据
	 * @return byte[] 消息摘要 
	 * 
	 */
	public String encodeRipeMD256Hex(byte[] data) {

		// 执行消息摘要
		byte[] b = encodeRipeMD256(data);

		// 做十六进制编码处理
		return new String(Hex.encode(b));
	}

	/**
	 * RipeMD320消息摘要
	 * 
	 * @param data 待做消息摘要处理的数据
	 * @return byte[] 消息摘要 
	 * 
	 */
	public byte[] encodeRipeMD320(byte[] data) {

		// 执行消息摘要
		return ripeMD320.digest(data);
	}

	/**
	 * RipeMD320Hex消息摘要
	 * 
	 * @param data 待做消息摘要处理的数据
	 * @return byte[] 消息摘要 
	 * 
	 */
	public String encodeRipeMD320Hex(byte[] data) {

		// 执行消息摘要
		byte[] b = encodeRipeMD320(data);

		// 做十六进制编码处理
		return new String(Hex.encode(b));
	}

}
```