category: 
- security
tag:
- 对称加密
title: RCCoder
---
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