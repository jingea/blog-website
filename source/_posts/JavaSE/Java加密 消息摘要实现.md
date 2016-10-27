category: Java
date: 2014-11-11
title: Java加密 -- 消息摘要
---

# 散列函数
散列函数又称为哈希函数,消息摘要函数,单向函数或者杂凑函数. 与密码体制不同的是, 散列函数的主要作用不是完成数据加密解密操作, 它主要是用来验证数据的完整性. 散列值是一个短的随机字母和数字组成的字符串.

![消息认证流程]()

在上述认证流程中,信息收发双发在通信前已经商定了具体的散列算法,并且该算法是公开的.
散列函数具有以下特性:
* 消息的长度不受限制.
* 对于给定的消息,其散列值的计算是很容易的.
* 如果两个散列值不相同,则这两个散列值的原始输入消息也不相同,这个特性使得散列函数具有确定性的结果.
* 散列函数的运算过程是不可逆的,这个特性称为函数的单向性.这也是单向函数命名的由来.
* 对于一个已知的消息及其散列值,要找到另一个消息使其获得相同的散列值是不可能的,这个特性称为抗弱碰撞性.这被用来防止伪造.
* 任意两个不同的消息的散列值一定不同,这个特性称为抗强碰撞性.

消息摘要算法三大类：
* MessageDigest
* SHA
* MAC

# MD
MessageDigest 消息摘要算法
* MD2 (1989)
* MD4 (1990)
* MD5 (1991)

MD系 实现选择
* Sun：Sun提供的算法较为底层, 支持MD2和MD5俩种算法. 但缺少了缺少了相应的进制转换实现,不能讲字节数组形式的摘要信息转换为十六进制字符串
* Bouncy Castle：提供了对MD4算法的支持. 支持多种形式的参数, 支持16进制字符串形式的摘要信息
* Commons Codec：如果仅仅需要MD5,使用它则是一个不错的选择

```java
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Random;

public class MessageDigestCoder {

	private static final byte[] datas = new byte[1024 * 1024];

	static {
		for (int i = 0; i < 1024 * 1024; i++) {
			datas[i] = (byte)new Random().nextInt(125);
		}
	}

	public static void main(String[] args) {
		// 加入BouncyCastleProvider支持
		Security.addProvider(new BouncyCastleProvider());

		print("MD2");
		print("MD4");
		print("MD5");
	}

	private static void print(String name) {
		try {
			MessageDigest md2 = MessageDigest.getInstance(name);
			byte[] bc = md2.digest(datas);
			System.out.print(name + "[" + bc.length + "] : ");
			for (int i = 0; i < bc.length; i++) {
				System.out.print(bc[i]);
			}
			System.out.println();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

	}
}
```


# SHA
SHA 安全散列算法 (基于MD4算法改进而来)
* SHA-1 (名字简称为SHA, 长度为160)
* SHA-2(包含SHA-224,SHA-256,SHA-384,SHA-512)
SHA与MD不同之处在于SHA算法的摘要更长,安全性更高. 通常作为MD5算法的继任者

Bouncy Castle实现SHA摘要计算
```java
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Random;

public class SHACoder {

	@Test
	public void testSHA() throws NoSuchAlgorithmException {
		// 加入BouncyCastleProvider支持
		Security.addProvider(new BouncyCastleProvider());
		MessageDigest sha = MessageDigest.getInstance("SHA1");

		byte[] datas = byteData();
		// 采用BouncyCastle进行SHA摘要计算
		byte[] bc = sha.digest(datas);

		for (int i = 0; i < bc.length; i++) {
			System.out.print(bc[i]);
		}
	}

	private byte[] byteData() {
		byte[] data = new byte[1024 * 1024];
		Random random = new Random();
		for (int i = 0; i < 1024 * 1024; i++) {
			data[i] = (byte)random.nextInt(125);
		}
		return data;
	}
}
```
如果要使用Bouncy Castle实现`SHA-256`等算法, 只需要修改算法名称`MessageDigest.getInstance("SHA");`就可以, 但是Apache codec却在编译阶段就为我们指定好了。

利用Apache codec实现SHA摘要计算
```java
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.function.Function;

public class SHACoder {

	private static final byte[] datas ;

	static {
		datas = new byte[1024 * 1024];
		Random random = new Random();
		for (int i = 0; i < 1024 * 1024; i++) {
			datas[i] = (byte)random.nextInt(125);
		}
	}

	@Test
	public void testSHA() throws NoSuchAlgorithmException {
		print("sha1", datas -> DigestUtils.sha1(datas));
		print("sha256", datas -> DigestUtils.sha256(datas));
		print("sha384", datas -> DigestUtils.sha384(datas));
		print("sha512", datas -> DigestUtils.sha512(datas));
	}

	private void print(String name, Function<byte[], byte[]> function) {
		byte[] bc = function.apply(datas);
		System.out.print(name + "[" + bc.length + "] : ");
		for (int i = 0; i < bc.length; i++) {
			System.out.print(bc[i]);
		}
		System.out.println();
	}
}
```
输出结果为
```bash
sha1[20] : -122-1-34365850-53-86-114-371209785-79106513-1118-85
sha256[32] : -72-43-98-56820-49-3744106-1-117-100-743335-74-1289755-9011359-1610-80-7461121-3-21106
sha384[48] : -18-3-82-8495-99-63-103113-84-3288-44-71-66-913-117-1163139-112239936-72-20-33-23-64150-934793-34-8-2811969-6-2-41-73-8690
sha512[64] : 105-73-98-1191265189-54-27-7242127-78-10627787919-106-723751-1927-45310-119-77-3660-5-12229-95-71-79551995-55-809-53-53-88-3149-50-75-5163092484223-724612097-12636-9
```
刚看到这个结果我也有点奇怪, sha1明明摘要长度有160位, 结果怎么会这么短, 这是因为20位是20个byte,而每个byte是从`[-127, 127]`的,而160位是160个bit. 


# MAC 
是含有密钥散列函数算法,兼容MD和SHA算法的特性,并在此基础上加入了密钥. 因为MAC算法融合了密钥散列函数(keyed-hash), 所以通常也把MAC称为HMAC

```java
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Security;
import java.util.Random;

public class MACCoder {

	private static final byte[] datas = new byte[1024 * 1024];

	static {
		for (int i = 0; i < 1024 * 1024; i++) {
			datas[i] = (byte)new Random().nextInt(125);
		}
	}

	public static void main(String[] args) {
		// 加入BouncyCastleProvider支持
		Security.addProvider(new BouncyCastleProvider());

		print("HmacMD2");
		print("HmacMD4");
		print("HmacMD5");
		print("HmacSHA1");
		print("HmacSHA224");
		print("HmacSHA256");
		print("HmacSHA384");
		print("HmacSHA512");
	}

	private static void print(String name) {
		Mac mac = getMac(name);
		byte[] bc = mac.doFinal(datas);
		System.out.print(name + "[" + bc.length + "] : ");
		for (int i = 0; i < bc.length; i++) {
			System.out.print(bc[i]);
		}
		System.out.println();

	}

	private static Mac getMac(String ar) {
		// 初始化KeyGenerator
		KeyGenerator keyGenerator;
		Mac mac = null;
		try {
			keyGenerator = KeyGenerator.getInstance(ar);
			// 产生秘密密钥
			SecretKey secretKey = keyGenerator.generateKey();
			// 获得密钥
			byte[] key = secretKey.getEncoded();
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
}
```

# RipeMDCoder
RipeMD系列消息摘要组件. 包含RipeMD128、RipeMD160、RipeMD256和RipeMD320共4种RipeMD系列算法

RipeMD(1996)	对MD4和MD5缺陷的基础上提出的算法   (Bouncy Castle)
* RipeMD128
* RipeMD160
* RipeMD256
* RipeMD320

```java
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Random;

public class RipeMDCoder {

	private static final byte[] datas = new byte[1024 * 1024];

	static {
		for (int i = 0; i < 1024 * 1024; i++) {
			datas[i] = (byte)new Random().nextInt(125);
		}
	}

	public static void main(String[] args) {
		// 加入BouncyCastleProvider支持
		Security.addProvider(new BouncyCastleProvider());

		print("RipeMD128");
		print("RipeMD160");
		print("RipeMD256");
		print("RipeMD320");
	}

	private static void print(String name) {
		try {
			MessageDigest md2 = MessageDigest.getInstance(name);
			byte[] bc = md2.digest(datas);
			System.out.print(name + "[" + bc.length + "] : ");
			for (int i = 0; i < bc.length; i++) {
				System.out.print(bc[i]);
			}
			System.out.println();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

	}
}

```

# HmacRipeMDCoder
HmacRipeMD系列加密组件. HmacRipeMD128、HmacRipeMD160共2种算法

MAC+RipeMD	(Bouncy Castle)
* HmacRipeMD128
* HmacRipeMD160

```java
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Security;
import java.util.Random;

public class HmacRipeMDCoder {

	private static final byte[] datas = new byte[1024 * 1024];

	static {
		for (int i = 0; i < 1024 * 1024; i++) {
			datas[i] = (byte)new Random().nextInt(125);
		}
	}

	public static void main(String[] args) {
		// 加入BouncyCastleProvider支持
		Security.addProvider(new BouncyCastleProvider());

		print("HmacRipeMD128");
		print("HmacRipeMD160");
	}

	private static void print(String name) {
		Mac mac = getMac(name);
		byte[] bc = mac.doFinal(datas);
		System.out.print(name + "[" + bc.length + "] : ");
		for (int i = 0; i < bc.length; i++) {
			System.out.print(bc[i]);
		}
		System.out.println();

	}

	/**
	 * 初始化HmacRipeMD128密钥
	 * @return byte[] 密钥
	 */
	private static Mac getMac(String key1) {

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
}

```


# Tiger	
号称最快的Hash算法,专门为64位机器做了优化,其消息长度为192

```java
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Random;

public class MessageDigestCoder {

	private static final byte[] datas = new byte[1024 * 1024];

	static {
		for (int i = 0; i < 1024 * 1024; i++) {
			datas[i] = (byte)new Random().nextInt(125);
		}
	}

	public static void main(String[] args) {
		// 加入BouncyCastleProvider支持
		Security.addProvider(new BouncyCastleProvider());
		print("Tiger");
	}

	private static void print(String name) {
		try {
			MessageDigest md2 = MessageDigest.getInstance(name);
			byte[] bc = md2.digest(datas);
			System.out.print(name + "[" + bc.length + "] : ");
			for (int i = 0; i < bc.length; i++) {
				System.out.print(bc[i]);
			}
			System.out.println();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

	}
}
```

# GOST3411	
被列入IDO标准,由于使用了和AES算法相同的转化技术,被称为最安全的摘要算法
```java
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Random;

public class MessageDigestCoder {

	private static final byte[] datas = new byte[1024 * 1024];

	static {
		for (int i = 0; i < 1024 * 1024; i++) {
			datas[i] = (byte)new Random().nextInt(125);
		}
	}

	public static void main(String[] args) {
		// 加入BouncyCastleProvider支持
		Security.addProvider(new BouncyCastleProvider());
		print("GOST3411");
	}

	private static void print(String name) {
		try {
			MessageDigest md2 = MessageDigest.getInstance(name);
			byte[] bc = md2.digest(datas);
			System.out.print(name + "[" + bc.length + "] : ");
			for (int i = 0; i < bc.length; i++) {
				System.out.print(bc[i]);
			}
			System.out.println();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

	}
}
```

# Whirlpool	
摘要长度为256位
```java
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Random;

public class MessageDigestCoder {

	private static final byte[] datas = new byte[1024 * 1024];

	static {
		for (int i = 0; i < 1024 * 1024; i++) {
			datas[i] = (byte)new Random().nextInt(125);
		}
	}

	public static void main(String[] args) {
		// 加入BouncyCastleProvider支持
		Security.addProvider(new BouncyCastleProvider());
		print("Whirlpool");
	}

	private static void print(String name) {
		try {
			MessageDigest md2 = MessageDigest.getInstance(name);
			byte[] bc = md2.digest(datas);
			System.out.print(name + "[" + bc.length + "] : ");
			for (int i = 0; i < bc.length; i++) {
				System.out.print(bc[i]);
			}
			System.out.println();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

	}
}
```

# CRCCoder
CRC 循环冗余校验算法. 

CRC是可以根据数据产生剪短固定位数的一种散列函数 ,主要用来检测或校验数据传输/保存后出现的错误. 生成的散列值在传输或储存之前计算出来并且附加到数据后面.在使用数据之前对数据的完整性做校验.一般来说,循环荣誉校验的值都是32位的2进制数,以8位16进制字符形式表示.它是一类重要的线性分组码.

消息摘要算法和CRC算法同属散列函数,并且CRC算法很可能就是消息摘要算法的前身
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