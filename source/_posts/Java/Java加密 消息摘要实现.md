category: Java
date: 2014-11-11
title: Java加密 -- 消息摘要
---

# 消息摘要算法三大类：
## MessageDigest 消息摘要算法
* MD2 (1989)
* MD4 (1990)
* MD5 (1991)
## SHA 安全散列算法 (基于MD4算法改进而来)
* SHA-1 (名字简称为SHA, 长度为160)
* SHA-2(包含SHA-224,SHA-256,SHA-384,SHA-512)
## MAC 消息认证码算法
* HmacMD2	(Bouncy Castle)
* HmacMD3
* HmacMD4 (Bouncy Castle)
* HmacMD5 (Sun)
* HmacSHA1 (Sun)
* HmacSHA224 (Bouncy Castle)
* HmacSHA256 (Sun)
* HmacSHA384 (Sun)
* HmacSHA512 (Sun)
## RipeMD(1996)	对MD4和MD5缺陷的基础上提出的算法   (Bouncy Castle)
* RipeMD128
* RipeMD160
* RipeMD256
* RipeMD320
## MAC+RipeMD	(Bouncy Castle)
* HmacRipeMD128
* HmacRipeMD160
## Tiger	
号称最快的Hash算法,专门为64位机器做了优化,其消息长度为192
## GOST3411	
被列入IDO标准,由于使用了和AES算法相同的转化技术,被称为最安全的摘要算法
## Whirlpool	
摘要长度为256位
##CRC	循环冗余校验算法
CRC是可以根据数据产生剪短固定位数的一种散列函数 ,主要用来检测或校验数据传输/保存后出现的错误.

生成的散列值在传输或储存之前计算出来并且附加到数据后面.在使用数据之前对数据的完整性做校验.
一般来说,循环荣誉校验的值都是32位的2进制数,以8位16进制字符形式表示.它是一类重要的线性分组码.

消息摘要算法和CRC算法同属散列函数,并且CRC算法很可能就是消息摘要算法的前身


## MD系 实现选择
* Sun：Sun提供的算法较为底层, 支持MD2和MD5俩种算法. 但缺少了缺少了相应的进制转换实现,不能讲字节数组形式的摘要信息转换为十六进制字符串
* Bouncy Castle：提供了对MD4算法的支持. 支持多种形式的参数, 支持16进制字符串形式的摘要信息
* Commons Codec：如果仅仅需要MD5,使用它则是一个不错的选择

SHA与MD不同之处在于SHA算法的摘要更长,安全性更高. 通常作为MD5算法的继任者

## MAC 
是含有密钥散列函数算法,兼容MD和SHA算法的特性,并在此基础上加入了密钥. 因为MAC算法融合了密钥散列函数(keyed-hash), 所以通常也把MAC称为HMAC


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

# SHACoder
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
# MessageDigestCoder
```java

public enum MessageDigestCoder {

	INSTANCE;
	
	private MessageDigestCoder() {
		// 加入BouncyCastleProvider支持
		Security.addProvider(new BouncyCastleProvider());
		
		try {
			md2 = MessageDigest.getInstance("MD2");
			md4 = MessageDigest.getInstance("MD4");
			md5 = MessageDigest.getInstance("MD5");
			tiger = MessageDigest.getInstance("Tiger");
			gost3411 = MessageDigest.getInstance("GOST3411");
			whirlpool = MessageDigest.getInstance("Whirlpool");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	private  MessageDigest md2;
	private  MessageDigest md4;
	private  MessageDigest md5;
	private  MessageDigest tiger;
	private  MessageDigest gost3411;
	private  MessageDigest whirlpool;
	
	public byte[] encodeMD2(byte[] input) {
		return md2.digest(input);
	}
	
	public byte[] encodeMD4(byte[] input) {
		return md4.digest(input);
	}

	public byte[] encodeMD5(byte[] input) {
		return md5.digest(input);
	}

	public byte[] encodeTIGER(byte[] input) {
		return tiger.digest(input);
	}

	public byte[] encodeGOST3411(byte[] input) {
		return gost3411.digest(input);
	}

	public byte[] encodeWHIRLPOOL(byte[] input) {
		return whirlpool.digest(input);
	}

	public String encodeMD2Hex(byte[] input) {
		return Hex.toHexString(md2.digest(input));
	}

	public String encodeMD4Hex(byte[] input) {
		return Hex.toHexString(md4.digest(input));
	}

	public String encodeMD5Hex(byte[] input) {
		return Hex.toHexString(md5.digest(input));
	}

	public String encodeTigerHex(byte[] input) {
		return Hex.toHexString(tiger.digest(input));
	}

	public String encodeGOST3411Hex(byte[] input) {
		return Hex.toHexString(gost3411.digest(input));
	}

	public String encodeWhirlpoolHex(byte[] input) {
		return Hex.toHexString(whirlpool.digest(input));
	}
}
```

# MACCoder
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

# HmacRipeMDCoder
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

# CRCCoder
---
```java

public enum CRCCoder {

	INSTANCE;
	
	private final CRC32 crc32 = new CRC32();
	
	public synchronized long encodeByCRC32(byte[] input) {
		crc32.update(input);
		
		final long value = crc32.getValue();
		crc32.reset();
		
		return value;
	}
	
	public String encodeByCRC32Hex(byte[] input) {
		long value = encodeByCRC32(input);
		
		return Long.toHexString(value);
	}
}
```