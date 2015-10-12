category: 
- java
tag:
- java加密解密
title: java.security.spec  API
---
## AlgorithmParameterSpec
此接口不包含任何方法或常亮.它仅用于将所有参数规范分组,并为其提供类型安全.所有参数规范否必须实现此接口.

AlgorithmParameterSpec接口有很多的子接口和实现类,用于特定算法的初始化.使用起来也很方便,只需要十一指定参数填充构造方法即可获得一个实例化对象
```java
// 以DSAParameterSpec为例
DSAParameterSpec dsa = new DSAParameterSpec(new BigInteger("1"), new BigInteger("1"), new BigInteger("1"));
dsa.getG(); // 返回基数G
dsa.getP(); // 返回素数P
dsa.getQ();	// 返回子素数Q
```

## DESKeySpec
DESKeySpec和SecretKeySpec都是提供秘密密钥规范的实现类 DESKeySpec：指定类DES算法.

SecretKeySpec：兼容所有对称加密算法.

DESKeySpec有很多的同胞, DESedeKeySpec提供类三重DES加密算法的密钥规范 PBEKeySpec 提供了PBE算法的密钥规范
```java
// 实例化KeyGenerator对象,并指定DES对象
KeyGenerator kg = KeyGenerator.getInstance("DES");
// 生成SecretKey对象
SecretKey sk = kg.generateKey();
// 获得密钥编码字节数组
byte[] key = sk.getEncoded();// 在得到密钥编码字节数组后,我们将通过如下方法还原秘密密钥对象
// 指定DES算法,还原SecretKey对象
SecretKeySpec sk1 = new SecretKeySpec(key, "DES");// 实例化DESKeySpec对象,获得DES算法
DESKeySpec dks = new DESKeySpec(key);
// 实例化SecretKeyFactory对象,并指定DES算法
SecretKeyFactory skf = SecretKeyFactory.getInstance("DESede");
// 获得SecretKey对象
SecretKey kf = skf.generateSecret(dks);
	
// 如果是三重DES算法,除了将原来指明为DES算法的位置替换为DESede外
// 还需要把DESKeySpec类换为DESedeKeySpec
// 实例化，并指定DESede算法// 实例化KeyGenerator对象,并指定DESede对象
KeyGenerator kg = KeyGenerator.getInstance("DESede");
// 生成SecretKey对象
SecretKey sk = kg.generateKey();
// 获得密钥编码字节数组
byte[] key = sk.getEncoded();// 在得到密钥编码字节数组后,我们将通过如下方法还原秘密密钥对象
// 实例化DESedeKeySpec对象,获得DESede秘密密钥规范
DESKeySpec dks = new DESKeySpec(key);
// 实例化SecretKeyFactory对象,并指定DESede算法
SecretKeyFactory skf = SecretKeyFactory.getInstance("DESede");
// 获得SecretKey对象
SecretKey kf = skf.generateSecret(dks);
```

## EncodedKeySpec
用编码格式来表示公钥和私钥,称之为编码密钥规范
```java
// 转换公钥编码密钥,以编码格式表示公钥
// 指定DSA实例化KeyPairGenerator
KeyPairGenerator kg = KeyPairGenerator.getInstance("DSA");
// 初始化
kg.initialize(1024);
// 生成KeyPair对象
KeyPair kp = kg.genKeyPair();
// 获得公钥的密钥字节数组
byte[] pkb = kp.getPublic().getEncoded();
// 将公钥密钥字节数组再转换为公钥对象
// 实例化X509EncodedKeySpec
X509EncodedKeySpec xekp = new X509EncodedKeySpec(pkb);
// 实例化KeyFactory
KeyFactory kf = KeyFactory.getInstance("DSA");
// 获得PublicKey
PublicKey pk = kf.generatePublic(xekp);
// 获得私钥的密钥字节数组
byte[] p1kb = kp.getPrivate().getEncoded();
// 将私钥密钥字节数组再转换为私钥对象
// 实例化X509EncodedKeySpec
X509EncodedKeySpec x1ekp = new X509EncodedKeySpec(p1kb);
// 实例化KeyFactory
KeyFactory kf1 = KeyFactory.getInstance("DSA");
// 获得PublicKey
PrivateKey p1k = kf.generatePrivate(x1ekp);
x1ekp.getEncoded();
x1ekp.getFormat();// X.509
// 转换私钥编码格式密钥,以编码格式表示私钥 (以PKCS#8标准作为密钥规范的编码格式)
// 根据给定的编码密钥创建一个新的// 转换公钥编码密钥,以编码格式表示公钥
// 指定DSA实例化KeyPairGenerator
KeyPairGenerator kg = KeyPairGenerator.getInstance("DSA");
// 初始化
kg.initialize(1024);
// 生成KeyPair对象
KeyPair kp = kg.genKeyPair();// 根据获得的密钥对获得私钥密钥字节数组
byte[] ks = kp.getPrivate().getEncoded();
// 
PKCS8EncodedKeySpec x1ekp = new PKCS8EncodedKeySpec(ks);
// 指定DSA算法实例化KeyFactory
KeyFactory kf = KeyFactory.getInstance("DSA");
// 获得PrivateKey
PrivateKey pk = kf.generatePrivate(x1ekp);
x1ekp.getEncoded();
x1ekp.getFormat(); // PKCS#8
```

## KeySpec
本接口不包含任何方法或常量,它仅用于将所有密钥规范分组,并为其提供类型安全.所有密钥规范都要继承该接口.

KeySpec的抽象实现类(EncodedKeySpec)构建了用于构建公钥规范和私钥规范的俩个实习

* X509EncodedKeySpec用于构建公钥
* PKCS8EncodedKeySpec用于构建私钥规范
* SecretKeySpec接口是KeySpec的实现类,用于构建私密密钥规范

## ModifyPolicy
```java
public static void main(String[] args) {
KeyGenerator kg = KeyGenerator.getInstance("AES");
kg.init(256);
SecretKey skey = kg.generateKey();
byte[] key = skey.getEncoded();
// 如果没有抛出java.security.InvalidKeyException 则证明修改成功
```

## SecretKeySpec
SecretKeySpec类是KeySpec接口的实现类,用于构建秘密密钥规范.

此类仅能表示为一个字节数组并且没有任何与之相关联的密钥参数的原始密钥有用,如DES或Triple DES密钥
```java// 先获得RC2算法的密钥字节数组
// 实例化KeyGenerator对象,并指定RC2对象
KeyGenerator kg = KeyGenerator.getInstance("RC2");
// 生成SecretKey对象
SecretKey sk = kg.generateKey();
// 获得密钥编码字节数组
byte[] key = sk.getEncoded();// 在得到密钥编码字节数组后,我们将通过如下方法还原秘密密钥对象
// 实例化SecretKey对象
SecretKeySpec sk1 = new SecretKeySpec(key, "RC2");
```

