category: 
- security
tag:
- 消息摘要
title: CRCCoder
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