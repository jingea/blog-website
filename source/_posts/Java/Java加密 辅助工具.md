category: Java
date: 2014-11-13
title: Java加密 -- 辅助工具
---
## Bouncy Castle
在[官网](http://www.bouncycastle.org/latest_releases.html) 下载 `bcprov-jdk15on-151.jar` 和 `bcprov-ext-jdk15on-151.jar`

对于Bouncy Castle 提供的扩充算法支持,有俩种方案可选
* 配置方式,通过配置JRE环境,使其作为提供者提供相应的算法支持,在代码实现层面只需指定要扩展的算法名称
> 1. 修改JDK
	修改java.security配置文件(jdk1.7.0_75\jre\lib\security)
	添加安全提供者 security.provider.11=org.bouncycastle.jce.provider.BouncyCastleProvider
	然后将bcprov-ext-jdk15on-151.jar 文件放入jdk1.7.0_75\jre\lib\ext
  2. 修改JRE
	修改java.security配置文件(jre7\lib\security)
	添加安全提供者 security.provider.11=org.bouncycastle.jce.provider.BouncyCastleProvider
	然后将bcprov-ext-jdk15on-151.jar 文件放入jre7\lib\ext

* 调用方式 : 直接将`bcprov-ext-jdk15on-151.jar` 导入到项目工程文件

JCE工具将其拓展包：仅包括`org.bouncycastle.jce`包. 这是对JCE框架的支持


## Base64

Base64是一种基于64个字符的编码算法,根据RFC 2045的定义：Base64内容传送编码是一种以任意8位字节序列组合的描述形式, 这种形式不易被人直接识别.经过Base64编码后的数据会比原始数据略长,为原来的4/3,经Base64编码后的字符串的字符数是以4为单位的整数倍

Base64算法有编码和解码操作可充当加密和解密操作,还有一张字符映射表充当了秘钥.由于字符映射表公开且Base64加密强度并不高,因此不能将其看作现代加密算法.但是如果将字符映射表调整,保密,改造后的Base64就具备了加密算法的意义而且Base64常作为密钥, 密文 和证书的一种通用存储编码格式

###实现原理

1. 将给定的字符串以字符为单位转换为对应的字符编码(如ASCII码)
2. 将获得的字符编码转换为二进制码
3. 对获得的二进制码做分组转换操作,每3个8位二进制码为1组,转换为每4个6位二进制码为1组(不足6位时低位补0)这是一个分组变化的过程, 3个8位二进制码和4个6位二进制码的长度都是24位
4. 对获得的4个6位二进制码补位,向6位二进制码添加2位 高位0,组成4个8位二进制码
5. 将获得的4个8位二进制码转换为10进制码
6. 将获得的十进制码转换为base64字符表中对应的字符

```java
对A进行Base64编码
字符				A
ASCII码			65
二进制码			01000001
4-6二进制码		010000		010000
4-8二进制码		00010000	00010000
十进制			16			16
字符表映射码		Q			Q			=	=

字符A编码之后就变成了QQ==

base64 映射表
V E			  V E			V E			  V E
0 A            17 R            34 i            51 z
1 B            18 S            35 j            52 0
2 C            19 T            36 k            53 1
3 D            20 U            37 l            54 2
4 E            21 V            38 m            55 3
5 F            22 W            39 n            56 4
6 G            23 X            40 o            57 5
7 H            24 Y            41 p            58 6
8 I            25 Z            42 q            59 7
9 J            26 a            43 r            60 8
10 K           27 b            44 s            61 9
11 L           28 c            45 t            62 +
12 M           29 d            46 u            63 /
13 N           30 e            47 v
14 O           31 f            48 w         (pad) =
15 P           32 g            49 x
16 Q           33 h            50 y
```


### 代码举例

```java
public class TestBase64 {

	static final String base64 = "base64编码!@#$%^&*()+_=-{}[];:'<>,./?|";

	@before
	public void before () {
		System.out.println(base64);
	}

	@Test
	public void testUrlBase64() {
		// 不能编码空格
		byte[] encode = UrlBase64.encode(base64.getBytes());
		System.out.println("UrlBase64 : " + new String(encode));

		byte[] decode = UrlBase64.decode(encode);
		Assert.assertEquals(base64, new String(decode));
	}

	@Test
	public void testJavaBase64() {
		System.out.println();
		byte[] encode = java.util.Base64.getEncoder().encode(base64.getBytes());
		System.out.println("JavaBase64 : " + new String(encode));
		byte[] decode = java.util.Base64.getDecoder().decode(encode);
		Assert.assertEquals(base64, new String(decode));
	}

	@Test
	public void testApacheBase64() {
		String encode = org.apache.commons.codec.binary.Base64.encodeBase64String(base64.getBytes());
		System.out.println("apacheBase64 : " + encode);
		byte[] decode = org.apache.commons.codec.binary.Base64.decodeBase64(encode.getBytes());
		Assert.assertEquals(base64, new String(decode));

		String url = org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString(base64.getBytes());
		System.out.println("apacheBase64 url : " + url);
		byte[] decoded = org.apache.commons.codec.binary.Base64.decodeBase64(url);
		Assert.assertEquals(base64, new String(decoded));
	}


	@Test
	public void testBouncycastleBase64() {
		byte[] encode = org.bouncycastle.util.encoders.Base64.encode(base64.getBytes());
		System.out.println("BouncycastleBase64 : " + new String(encode));
		byte[] decode = org.bouncycastle.util.encoders.Base64.decode(encode);
		Assert.assertEquals(base64, new String(decode));
	}

	// 解码由x-www-form-url-encoded格式编码的字符串
	@Test
	public void testSpace() {
		String base64 = "wKOS4FsxiFvE48KGGSuSkRui9Iap1ukgl1+eVqZiGhXQYYiP8KGCV%2FRIeTEyMLsWxE%2FEx6jhuW3DPUt4JYX+cohUOqFVVaQ%2FioGZCAge3ygaCz%2Fe4q8o9XQzOEtcdXPywGZ0e5sgE787ij4dRZy2ILK2cxsVvC8yrlIPGZ3LUg8nOj8oEg5l2AnQnA3i+Sxbgqmwe1OjIXVZqPZWb+Y4SVQL8EpWlmEjXb4HjgmGTgVYzwJ64QO7HUPP1yuQHkS0PLS%2FpbPrgL5vqTF7h%2FPvMw=%3D";
		String decoded = null;
		try {
			decoded = URLDecoder.decode(base64);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// 如果url包含一个%而后却没有2个16进制数字 抛出该异常
			e.printStackTrace();
		}
		System.out.println(decoded);
		byte[] decode = UrlBase64.decode(base64);
		System.out.println(new String(decode));
	}
}

```
> URL使用的字符必须来自ASCII的子集(大写字母`A-Z`,小写字母`a-z`,数字`0-9`, 标点字符 `- _ . ! ~ * ' ,`) 需要注意的是`/ & ? @ # ; $ + = %` 也可以使用,但是必须转换为字节(每个字节为%后跟俩个16进制数字)(空格编码为+) 所以URL组成的内容是ASCII的子集 + 经过转换后的字节 但是URL不会自动地进行编码和解码因此我们需要URLEncoder来进行编码
```java
// 需要注意额是= 和 & URLEncoder会进行盲目地编码 因此在使用URLEncoder编码时避免将整个url字串都编码
print("  : " + URLEncoder.encode(" ", "UTF-8"));
print("= : " + URLEncoder.encode("=", "UTF-8"));
print("& : " + URLEncoder.encode("&", "UTF-8"));
print("* : " + URLEncoder.encode("*", "UTF-8"));
print("% : " + URLEncoder.encode("%", "UTF-8"));
print("+ : " + URLEncoder.encode("+", "UTF-8"));
print("/ : " + URLEncoder.encode("/", "UTF-8"));
print(". : " + URLEncoder.encode(".", "UTF-8"));
print(": : " + URLEncoder.encode(":", "UTF-8"));
print("~ : " + URLEncoder.encode("~", "UTF-8"));
print("\" : " + URLEncoder.encode("\"", "UTF-8"));
print("() : " + URLEncoder.encode("(url)", "UTF-8"));
```
