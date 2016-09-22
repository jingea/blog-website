category: Java
date: 2014-11-09
title: Java加密 -- 对称加密
---
## 概述

现在Java语言实现的对称加密算法大约20多种. java7仅提供部分算法实现(其他算法通过第三方加密软件包Bouncy Castle实现.)
* DES : 最具有代表性的对称加密算法,堪称典范(1998年,实用化DES算法破译机的出现彻底宣告DES算法已不具备安全性)
* DESede ： DESede是DES算法的变种。 基于DES算法进行三重迭代,增加了算法的安全性
* AES ： AES算法则作为DES算法的替代者
* Blowfish
* RC2
* RC4
* IDEA ：在DES算法的基础上发展出来的，类似于三重DES。发展IDEA也是因为感到DES具有密钥太短等缺点，已经过时，DEA的密钥为128位

> DES算法和DESede算法统称为DES系列算法. 

## 分组密码
下面介绍了分组密码的各种工作模式

###  电子密码本模式-ECB
![]()

* 优点：易于理解且简单易行;便于实现并行操作;没有误差产传递的问题
* 缺点：不能隐藏明文模式,如果明文重复,则对于的密文也会重复,密文内容很容易被替换,重拍,删除,重放; 对明文主动攻击的可能性较高
* 用途：适用于加密密钥,随机数等短数据.例如安全地传递DES秘药,ECB是最合适的模式

###  密文连接模式-CBC
![](https://raw.githubusercontent.com/wanggnim/blog-website/images/secure/%E5%88%86%E7%BB%84%E6%A8%A1%E5%BC%8FCBC.jpg)

* 优点：密文连接模式加密后的密文上下文关联,即使在明文中出现重复的信息也不会产生相同的密文;密文内容如果被替换,重拍,删除,重放或网络传输过程中发生错误,后续密文即被破坏,无法完成还原;对明文的主动攻击性较低
* 缺点：不利于并行计算,目前没有已知的并行运算算法;误差传递,如果在加密过程中发生错误,则错误将被无限放大,导致加密失败;需要初始化向量
* 用途：可加密任意长度的数据;适用于计算产生检测数据完整性的消息认证码Mac


###  密文反馈模式-CFB
![](https://raw.githubusercontent.com/wanggnim/blog-website/images/secure/%E5%88%86%E7%BB%84%E6%A8%A1%E5%BC%8FCFB.jpg)

* 优点：隐藏了明文的模式,每一个分组的加密结果必受其前面所有分组内容的影响,即使出现许多次相同的明文,也均产生不同的密文;分组密码转化为流模式,可产生密钥流;可以及时加密传送小于分组的数据
* 缺点：与CBC相似.不利于并行计算,目前没有已知的并行运算算法;存在误差传递,一个单元损坏影响多个单元;需要初始化向量.
* 用途：因错误传播无界,可用于检查发现明文密文的篡改


###  输出反馈模式-OFB
![](https://raw.githubusercontent.com/wanggnim/blog-website/images/secure/%E5%88%86%E7%BB%84%E6%A8%A1%E5%BC%8FOFB.jpg)

* 优点：隐藏了明文的模式;分组密码转化为流模式;无误差传递问题;可以及时加密传送小于分组的数据
* 缺点：不利于并行计算;对明文的主动攻击是可能的,安全性较CFB差
* 用途：适用于加密冗余性较大的数据,比如语音和图像数据

###  计数器模式-CTR
![](https://raw.githubusercontent.com/wanggnim/blog-website/images/secure/%E5%88%86%E7%BB%84%E6%A8%A1%E5%BC%8FCTR.jpg)

* 优点：可并行计算;安全性至少与CBC模式一样好;加密与解密仅涉及密码算法的加密
* 缺点：没有错误传播,因此不易确保数据完整性
* 用途：适用于各种加密应用


## 流密码
![](https://raw.githubusercontent.com/wanggnim/blog-website/images/secure/%E6%B5%81%E6%A8%A1%E5%BC%8F.jpg)

* 同步流密码
* 自同步流密码
* 主要用于军事和外交
* 常用算法 ： RC4,  SEAL

## 算法实现

### DESCoder
DES安全编码组件  密钥算法 Java 6 只支持56bit密钥  Bouncy Castle 支持64bit密钥
```java
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;
import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.*;
import java.util.Random;

public class DESCoder {

	private static final byte[] datas = new byte[1024 * 1024];

	static {
		Security.addProvider(new BouncyCastleProvider());
		for (int i = 0; i < 1024 * 1024; i++) {
			datas[i] = (byte)new Random().nextInt(125);
		}
	}

	@Test
	public void test() throws NoSuchAlgorithmException {
		// 加密/解密算法 / 工作模式 / 填充方式
		print("DES", "ECB", "NoPadding");
	}

	private void print(String algo, String workModel, String padding) {
		byte[] key = initKey(algo);
		byte[] encryptData = encrypt(algo, workModel, padding, datas, key);
		byte[] decryptData = decrypt(algo, workModel, padding, encryptData, key);
		Assert.assertArrayEquals(datas, decryptData);
	}

	private Key toKey(String name,byte[] key) {

		SecretKey secretKey = null;
		try {
			// 实例化DES密钥材料
			DESKeySpec dks = new DESKeySpec(key);
			// 实例化秘密密钥工厂
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(name);
			// 生成秘密密钥
			secretKey = keyFactory.generateSecret(dks);
		} catch (final Exception e) {
			System.out.println("toKey : " + this + "\t" + e.getMessage());
		}

		return secretKey;
	}

	public byte[] decrypt(String algo, String workModel, String padding, byte[] data, byte[] key) {
		byte[] result = null;
		try {
			// 还原密钥
			Key k = toKey(algo, key);
			// 实例化
			Cipher cipher;
			cipher = Cipher.getInstance(algo + "/" + workModel + "/" + padding);
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
	 * 加密数据在下面几种情况下,必须满足长度和倍数关系, 否则会抛出下面异常
	 */
	public byte[] encrypt(String algo, String workModel, String padding, byte[] data, byte[] key) {

		byte[] result = null;
		try {
			// 还原密钥
			Key k = toKey(algo, key);

			// 实例化
			Cipher cipher;
			cipher = Cipher.getInstance(algo + "/" + workModel + "/" + padding);
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
	public byte[] initKey(String algo) {

		/*
		 * 实例化密钥生成器
		 *
		 * 若要使用64bit密钥注意替换 将下述代码中的KeyGenerator.getInstance(CIPHER_ALGORITHM);
		 * 替换为KeyGenerator.getInstance(CIPHER_ALGORITHM, "BC");
		 */
		KeyGenerator kg = null;
		try {
			kg = KeyGenerator.getInstance(algo, "BC");
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			System.out.println(this + "\t" + e.getMessage());
		}

		// 初始化密钥生成器 若要使用64bit密钥注意替换 将下述代码kg.init(56); 替换为kg.init(64);
		kg.init(64, new SecureRandom());

		// 生成秘密密钥
		SecretKey secretKey = kg.generateKey();

		// 获得密钥的二进制编码形式
		return secretKey.getEncoded();
	}
}
```

### DESedeCoder

```java
/**
 * DESede安全编码组件
 * 加密/解密算法 / 工作模式 / 填充方式
 * Java 6支持PKCS5PADDING填充方式
 * Bouncy Castle支持PKCS7Padding填充方式
 */
public class DESedeCoder {

	private static final byte[] datas = new byte[1024 * 1024];

	static {
		Security.addProvider(new BouncyCastleProvider());
		for (int i = 0; i < 1024 * 1024; i++) {
			datas[i] = (byte)new Random().nextInt(125);
		}
	}

	@Test
	public void test() throws NoSuchAlgorithmException {
		// 加密/解密算法 / 工作模式 / 填充方式
		print("DESede", "ECB", "NoPadding");
	}

	private void print(String algo, String workModel, String padding) {
		byte[] key = initKey(algo);
		byte[] encryptData = encrypt(algo, workModel, padding, datas, key);
		byte[] decryptData = decrypt(algo, workModel, padding, encryptData, key);
		Assert.assertArrayEquals(datas, decryptData);
	}

	private Key toKey(String name, byte[] key) {
		// 实例化DES密钥材料
		DESedeKeySpec dks = null;
		// 生成秘密密钥
		SecretKey secretKey = null;
		// 实例化秘密密钥工厂
		SecretKeyFactory keyFactory = null;
		try {
			dks = new DESedeKeySpec(key);
			keyFactory = SecretKeyFactory.getInstance(name);
			secretKey = keyFactory.generateSecret(dks);
		} catch (final  Exception e) {
			System.out.println(this + "\t" + e.getMessage());
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
	public byte[] decrypt(String algo, String workModel, String padding, byte[] data, byte[] key) {
		// 还原密钥
		Key k = toKey(algo, key);
		/*
		 * 实例化
		 * 使用PKCS7Padding填充方式
		 * Cipher.getInstance(CIPHER_ALGORITHM, "BC");
		 */
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(algo + "/" + workModel + "/" + padding);
			// 初始化，设置为解密模式
			cipher.init(Cipher.DECRYPT_MODE, k);
			// 执行操作
			return cipher.doFinal(data);
		} catch (Exception e) {
			System.out.println(this + "\t" + e.getMessage());
		}
		return null;
	}

	/**
	 * 加密
	 *
	 * @param data
	 *            待加密数据
	 * @param key
	 *            密钥
	 * @return byte[] 加密数据
	 * @throws Exception
	 */
	public byte[] encrypt(String algo, String workModel, String padding, byte[] data, byte[] key) {

		// 还原密钥
		Key k = toKey(algo, key);

		/*
		 * 实例化
		 * 使用PKCS7Padding填充方式
		 * Cipher.getInstance(CIPHER_ALGORITHM, "BC");
		 */
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(algo + "/" + workModel + "/" + padding);
			// 初始化，设置为加密模式
			cipher.init(Cipher.ENCRYPT_MODE, k);
			// 执行操作
			return cipher.doFinal(data);
		} catch (final Exception e) {
			System.out.println(this + "\t" + e.getMessage());
		}
		return null;
	}

	public byte[] initKey(String name) {
		// 实例化
		KeyGenerator kg = null;
		try {
			kg = KeyGenerator.getInstance(name);
		} catch (NoSuchAlgorithmException e) {
			System.out.println(this + "\t" + e.getMessage());
		}
		 //DESede 要求密钥长度为 112位或168位
		kg.init(168);
		// 生成秘密密钥
		SecretKey secretKey = kg.generateKey();
		// 获得密钥的二进制编码形式
		return secretKey.getEncoded();
	}

}
```

### AESCoder

```java
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;
import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Random;

/**
 * 加密/解密算法 / 工作模式 / 填充方式
 * Java 6支持PKCS5Padding填充方式
 * Bouncy Castle支持PKCS7Padding填充方式
 */
public class AESCoder {

	private static final byte[] datas = new byte[1024 * 1024];

	static {
		Security.addProvider(new BouncyCastleProvider());
		for (int i = 0; i < 1024 * 1024; i++) {
			datas[i] = (byte)new Random().nextInt(125);
		}
	}

	@Test
	public void test() throws NoSuchAlgorithmException {
		// 加密/解密算法 / 工作模式 / 填充方式
		print("AES", "ECB", "NoPadding");
	}

	private void print(String algo, String workModel, String padding) {
		byte[] key = initKey(algo);
		byte[] encryptData = encrypt(algo, workModel, padding, datas, key);
		byte[] decryptData = decrypt(algo, workModel, padding, encryptData, key);
		Assert.assertArrayEquals(datas, decryptData);
	}

	private Key toKey(String name, byte[] key) {
		// 实例化AES密钥材料
		SecretKey secretKey = new SecretKeySpec(key, name);
		return secretKey;
	}

	public byte[] decrypt(String algo, String workModel, String padding, byte[] data, byte[] key) {
		// 还原密钥
		Key k = toKey(algo, key);
		/*
		 * 实例化
		 * 使用PKCS7Padding填充方式，按如下方式实现 Cipher.getInstance(CIPHER_ALGORITHM, "BC");
		 */
		try {
			Cipher cipher = Cipher.getInstance(algo + "/" + workModel + "/" + padding);
			// 初始化，设置为解密模式
			cipher.init(Cipher.DECRYPT_MODE, k);
			// 执行操作
			return cipher.doFinal(data);
		} catch (final Exception e) {
			System.out.println(this + " - decrypt - " + e.getMessage());
		}
		return null;
	}

	public byte[] encrypt(String algo, String workModel, String padding, byte[] data, byte[] key) {

		// 还原密钥
		Key k = toKey(algo, key);

		/*
		 * 实例化
		 * 使用PKCS7Padding填充方式，按如下方式实现 Cipher.getInstance(CIPHER_ALGORITHM, "BC");
		 */
		byte[] result = null;
		try {
			Cipher cipher = Cipher.getInstance(algo + "/" + workModel + "/" + padding);
			// 初始化，设置为加密模式
			cipher.init(Cipher.ENCRYPT_MODE, k);
			// 执行操作
			result = cipher.doFinal(data);
		} catch (final Exception e) {
			System.out.println(this + " - encrypt - " + e.getMessage());
		}

		return result;
	}

	public byte[] initKey(String name) {

		// 实例化
		KeyGenerator kg = null;
		try {
			kg = KeyGenerator.getInstance(name);
		} catch (NoSuchAlgorithmException e) {
			System.out.println(this + "  " + e.getMessage());
		}

		 // AES 要求密钥长度为 128位、192位或 256位
		kg.init(256);
		// 生成秘密密钥
		SecretKey secretKey = kg.generateKey();
		// 获得密钥的二进制编码形式
		return secretKey.getEncoded();
	}

}
```

### RC算法实现
```java
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

public class RCCoder {

	private static final byte[] datas = new byte[1024 * 1024];

	static {
		for (int i = 0; i < 1024 * 1024; i++) {
			datas[i] = (byte)new Random().nextInt(125);
		}
	}

	@Test
	public void test() {
		byte[] encryptDatas = encrypt(datas);
		byte[] decryptDatas = decrypt(encryptDatas);
		Assert.assertArrayEquals(datas, decryptDatas);
	}

	public static byte[] encrypt(byte[] data) {
		byte[] encoded = new byte[data.length];
		for(int i = 0; i < data.length; i++) {
			encoded[i] = (byte) ((data[i]) ^ (byte)'a');
		}
		return encoded;
	}

	public static byte[] decrypt(byte[] data) {
		byte[] encoded = new byte[data.length];
		for(int i = 0; i < data.length; i++) {
			encoded[i] = (byte) ((data[i]) ^ (byte)'a');
		}
		return encoded;
	}
}
```

### PBE算法
PBE算法(Password Base Encryption，基于口令加密), 它并不是一种新的加密算法, 而是采用现有的一些加密算法(DES, RC2等等)通过口令(替代秘钥)和加"盐"的方式来进行加密. 如果单单使用口令的话,很容易被穷举破解出来,但是在口令上加盐的话, 会进一步加大破解难度.
```java
算法							密钥长度(默认值)	工作模式	填充方式													备注
PBEWithMD5AndDES				56(56)				CBC			PKCS5Padding												java6实现
PBEWithMD5AndTripeDES			112、168(168)		CBC			PKCS6Padding												java7实现
PBEWithSHA1AndDESede			112、168(168)		CBC			PKCS7Padding												java8实现
PBEWithSHA1AndRC2_40			40至1024(128)		CBC			PKCS8Padding												java9实现
PBEWithMD5AndDES				64(64)				CBC			PKCS5Padding/PKCS7Padding/ISO10126Padding/ZeroBytePadding	BouncyCastle实现
PBEWithMD5AndRC2				128(128)			CBC			PKCS5Padding/PKCS7Padding/ISO10127Padding/ZeroBytePadding	BouncyCastle实现
PBEWithSHA1AndDES				64(64)				CBC			PKCS5Padding/PKCS7Padding/ISO10128Padding/ZeroBytePadding	BouncyCastle实现
PBEWithSHA1AndRC2				128(128)			CBC			PKCS5Padding/PKCS7Padding/ISO10129Padding/ZeroBytePadding	BouncyCastle实现
PBEWithSHAAndIDEA-CBC			128(128)			CBC			PKCS5Padding/PKCS7Padding/ISO10130Padding/ZeroBytePadding	BouncyCastle实现
PBEWithSHAAnd2-KeyTripleDES-CBC	128(128)			CBC			PKCS5Padding/PKCS7Padding/ISO10131Padding/ZeroBytePadding	BouncyCastle实现
PBEWithSHAAnd3-KeyTripleDES-CBC	192(192)			CBC			PKCS5Padding/PKCS7Padding/ISO10132Padding/ZeroBytePadding	BouncyCastle实现
PBEWithSHAAnd128BitRC2-CBC		128(128)			CBC			PKCS5Padding/PKCS7Padding/ISO10133Padding/ZeroBytePadding	BouncyCastle实现
PBEWithSHAAnd40BitRC2-CBC		40(40)				CBC			PKCS5Padding/PKCS7Padding/ISO10134Padding/ZeroBytePadding	BouncyCastle实现
PBEWithSHAAnd128BitRC4			128(128)			CBC			PKCS5Padding/PKCS7Padding/ISO10135Padding/ZeroBytePadding	BouncyCastle实现
PBEWithSHAAnd40BitRC4			40(40)				CBC			PKCS5Padding/PKCS7Padding/ISO10136Padding/ZeroBytePadding	BouncyCastle实现
```

```java
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;
import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Random;

public class PBECoder {

	private static final byte[] datas = new byte[1024 * 1024];
	private static final String PASSWORD = "adfgv5d12s45d1f54we";

	static {
		Security.addProvider(new BouncyCastleProvider());
		for (int i = 0; i < 1024 * 1024; i++) {
			datas[i] = (byte)new Random().nextInt(125);
		}
	}

	@Test
	public void test() throws NoSuchAlgorithmException {
		print("PBEWithMD5AndDES");
	}

	private void print(String name) {
		byte[] solt = initSalt();
		byte[] encryptData = encrypt(name, datas, solt);
		byte[] decryptData = decrypt(name, encryptData, solt);
		Assert.assertArrayEquals(datas, decryptData);
	}

	public byte[] encrypt(String algorith, byte[] data, byte[] salt) {
		try {
			// 转换密钥
			Key key = toKey(algorith);
			// 实例化PBE参数材料
			PBEParameterSpec paramSpec = new PBEParameterSpec(salt, 100);
			// 实例化
			Cipher cipher = Cipher.getInstance(algorith);
			// 初始化
			cipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
			// 执行操作
			return cipher.doFinal(data);
		} catch (final Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public byte[] decrypt(String algorith, byte[] data, byte[] salt) {
		try {
			// 转换密钥
			Key key = toKey(algorith);
			// 实例化PBE参数材料
			PBEParameterSpec paramSpec = new PBEParameterSpec(salt, 100);
			// 实例化
			Cipher cipher = Cipher.getInstance(algorith);
			// 初始化
			cipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
			// 执行操作
			return cipher.doFinal(data);
		} catch (final Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private Key toKey(String algorith) throws Exception {
		PBEKeySpec keySpec = new PBEKeySpec(PASSWORD.toCharArray());
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algorith);
		SecretKey secretKey = keyFactory.generateSecret(keySpec);
		return secretKey;
	}

	public byte[] initSalt() {
		SecureRandom random = new SecureRandom();
		// 长度必须为8字节
		return random.generateSeed(8);
	}
}
```

### IDEA算法
```java
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;
import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Random;

public class IDEACoder {
	/**
	 * 密钥算法
	 */
	public static final String KEY_ALGORITHM = "IDEA";

	private static final byte[] datas = new byte[1024 * 1024];

	static {
		Security.addProvider(new BouncyCastleProvider());
		for (int i = 0; i < 1024 * 1024; i++) {
			datas[i] = (byte)new Random().nextInt(125);
		}
	}

	@Test
	public void test() throws NoSuchAlgorithmException {
		// 加密/解密算法 / 工作模式 / 填充方式
		print("IDEA/ECB/PKCS5Padding");
	}

	private void print(String name) {
		byte[] key = initKey();
		byte[] encryptData = encrypt(name, datas, key);
		byte[] decryptData = decrypt(name, encryptData, key);
		Assert.assertArrayEquals(datas, decryptData);
	}

	public static byte[] decrypt(String name, byte[] data, byte[] key) {
		try {
			// 还原密钥
			Key k = toKey(key);
			// 实例化
			Cipher cipher = Cipher.getInstance(name);
			// 初始化，设置为解密模式
			cipher.init(Cipher.DECRYPT_MODE, k);
			// 执行操作
			return cipher.doFinal(data);
		}catch (final Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static byte[] encrypt(String name, byte[] data, byte[] key) {
		try {
			// 还原密钥
			Key k = toKey(key);
			// 实例化
			Cipher cipher = Cipher.getInstance(name);
			// 初始化，设置为加密模式
			cipher.init(Cipher.ENCRYPT_MODE, k);
			// 执行操作
			return cipher.doFinal(data);
		}catch (final Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static Key toKey(byte[] key) throws Exception {
		// 生成秘密密钥
		return new SecretKeySpec(key, KEY_ALGORITHM);
	}

	public static byte[] initKey() {
		try {
			KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);
			// 初始化
			kg.init(128);
			// 生成秘密密钥
			SecretKey secretKey = kg.generateKey();
			// 获得密钥的二进制编码形式
			return secretKey.getEncoded();
		}catch (final Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
```



