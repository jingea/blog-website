category: 
- security
tag:
- 消息摘要
title: SHACoder
---
```java
public enum SHACoder {
	
	INSTANCE;

	SHACoder() {
		// 加入BouncyCastleProvider支持
		Security.addProvider(new BouncyCastleProvider());
		
		try {
			sha224 = MessageDigest.getInstance("SHA-224");
			sha = MessageDigest.getInstance("SHA");
			sha256 = MessageDigest.getInstance("SHA-256");
			sha384 = MessageDigest.getInstance("SHA-384");
			sha512 = MessageDigest.getInstance("SHA-512");
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	private MessageDigest sha224;
	private MessageDigest sha;
	private MessageDigest sha256;
	private MessageDigest sha384;
	private MessageDigest sha512;
	
	/**
	 * SHA加密
	 * 
	 * @param data 待加密数据
	 * @return byte[] 消息摘要
	 */
	public byte[] encodeSHA1ByCodec(byte[] data)  {
		// 执行消息摘要
		return DigestUtils.sha1(data);
	}

	/**
	 * SHAHex加密
	 * 
	 * @param data 待加密数据
	 * @return byte[] 消息摘要
	 */
	public String encodeSHAHexByCodec(byte[] data)  {

		// 执行消息摘要
		return DigestUtils.sha1Hex(data);
	}

	/**
	 * SHA256加密
	 * 
	 * @param data 待加密数据
	 * @return byte[] 消息摘要
	 */
	public byte[] encodeSHA256ByCodec(byte[] data)  {

		// 执行消息摘要
		return DigestUtils.sha256(data);
	}

	/**
	 * SHA256Hex加密
	 * 
	 * @param data 待加密数据
	 * @return byte[] 消息摘要
	 */
	public String encodeSHA256HexByCodec(byte[] data)  {

		// 执行消息摘要
		return DigestUtils.sha256Hex(data);
	}

	/**
	 * SHA384加密
	 * 
	 * @param data 待加密数据
	 * @return byte[] 消息摘要 
	 */
	public byte[] encodeSHA384ByCodec(byte[] data)  {

		// 执行消息摘要
		return DigestUtils.sha384(data);
	}

	/**
	 * SHA384Hex加密
	 * 
	 * @param data 待加密数据
	 * @return byte[] 消息摘要 
	 */
	public String encodeSHA384HexByCodec(byte[] data)  {

		// 执行消息摘要
		return DigestUtils.sha384Hex(data);
	}

	/**
	 * SHA512Hex加密
	 * 
	 * @param data 待加密数据
	 * @return byte[] 消息摘要
	 * @
	 */
	public byte[] encodeSHA512ByCodec(byte[] data)  {

		// 执行消息摘要
		return DigestUtils.sha512(data);
	}

	/**
	 * SHA512Hex加密
	 * 
	 * @param data 待加密数据
	 * @return byte[] 消息摘要
	 * @
	 */
	public String encodeSHA512HexByCodec(byte[] data)  {

		// 执行消息摘要
		return DigestUtils.sha512Hex(data);
	}
	
	/**
	 * SHA-224加密
	 * 
	 * @param data
	 *            待加密数据
	 * @return byte[] 消息摘要
	 * @throws NoSuchAlgorithmException 
	 * 
	 */
	public byte[] encodeSHA224(byte[] data)  {

		// 执行消息摘要
		return sha224.digest(data);
	}

	/**
	 * SHA-224加密
	 * 
	 * @param data
	 *            待加密数据
	 * @return byte[] 消息摘要
	 * @throws NoSuchAlgorithmException 
	 */
	public String encodeSHA224Hex(byte[] data)  {

		// 执行消息摘要
		byte[] b = encodeSHA224(data);

		// 做十六进制编码处理
		return new String(Hex.encode(b));

	}
	
	/**
	 * SHA-1加密
	 * 使用Codec
	 * @param data
	 *            待加密数据
	 * @return byte[] 消息摘要
	 * @throws NoSuchAlgorithmException 
	 * 
	 */
	@Deprecated
	public byte[] encodeSHA(byte[] data)  {

		// 执行消息摘要
		return sha.digest(data);
	}


	/**
	 * SHA-256加密
	 * 使用Codec
	 * @param data
	 *            待加密数据
	 * @return byte[] 消息摘要
	 * @throws NoSuchAlgorithmException 
	 * 
	 */
	@Deprecated
	public byte[] encodeSHA256(byte[] data)  {

		// 执行消息摘要
		return sha256.digest(data);
	}

	/**
	 * SHA-384加密
	 * 使用Codec
	 * @param data
	 *            待加密数据
	 * @return byte[] 消息摘要
	 * @throws NoSuchAlgorithmException 
	 * 
	 */
	@Deprecated
	public byte[] encodeSHA384(byte[] data)  {
		
		// 执行消息摘要
		return sha384.digest(data);
	}

	/**
	 * SHA-512加密
	 * 使用Codec
	 * @param data
	 *            待加密数据
	 * @return byte[] 消息摘要
	 * @throws NoSuchAlgorithmException 
	 * 
	 */
	@Deprecated
	public byte[] encodeSHA512(byte[] data)  {

		// 执行消息摘要
		return sha512.digest(data);
	}
}
```