category: 
- security
tag:
- 消息摘要
title: MessageDigestCoder
---
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