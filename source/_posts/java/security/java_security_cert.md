category: 
- java
tag:
- java加密解密
title: java.security.cert  API
---
## Certificate
用于管理证书的抽象类 证书有很多类型,如X.509证书,PGP证书和SDSI证书,并且它们都以不同的方式存储并存储不同的信息,但却都可以通过继承Certificate类来实现

## CertificateFactory
CertificateFactory是一个引擎类,称之为证书工厂,可以通过它将证书导入程序中.
```java
// 实例化,并指明证书类型为 x.509
CertificateFactory cf = CertificateFactory.getInstance("x.509");
// 获得证书输出流
FileInputStream in = new FileInputStream("D:\\x.keystore");
// 生成一个证书对象,并使用从输出流in中读取的数据对它进行初始化
Certificate c = cf.generateCertificate(in);
// 返回从给定输入流in中读取的证书的集合视图
cf.generateCertificates(in);// 生成一个CertPath,并使用从InputStream中读取的数据对它进行初始化
cf.generateCertPath(in);
// 还可使用List或者指定编码对其进行初始化// 生成一个证书撤销列表(CRL),并使用从输入流in中读取的数据对它进行初始化
cf.generateCRL(in);// 返回CertificateFactory 支持的CertPath编码的迭代器,默认编码方式优先
cf.getCertPathEncodings();// 返回CertificateFactory相关联的证书类型
cf.getType();// 关闭流
in.close();
	}
}
```

## CertPath
定义了常用于所有CertPath的方法 其子类可处理不同类型的证书(x.509 PGP等)所有的CertPath对象都包含类型,Certificate列表及其支持的一种或多种编码
```java
// 实例化,并指明证书类型为X.509
CertificateFactory cf = CertificateFactory.getInstance("x.509");
// 获得证书输入流
FileInputStream in = new FileInputStream("");
// 获得CertPath对象
CertPath cp = cf.generateCertPath(in);in.close();// 获得证书路径中的证书列表
cp.getCertificates();
// 获得证书路径的编码形式,使用默认编码（还可指定编码） 
cp.getEncoded();// 获得证书路径中的Certificate类型 如x。509
cp.getType();	}
}
```

## CRL
证书可能会由于各种原因失效, 失效后证书将被制为无效,无效的结果就是产生CRL(证书撤销列表),CA负责发布CRL,CRL中列出了该CA已经撤销的证书验证证书时,首先需要查询此列表,然后再考虑接受证书的合法性
```java
// 实例化,并致命证书类型为 X509
CertificateFactory cf = CertificateFactory.getInstance("X.509");
// 获得证书输入流
FileInputStream in = new FileInputStream("");
// 获得证书撤销列表
CRL crl = cf.generateCRL(in);
// 关闭流
in.close();
// 获取CRL类型
crl.getType();
// 检查给定的证书是否在此CRl中
//	crl.isRevoked(cert)
```

## X509Certificate
X509Certificate是Certificate的子类. x.509证书的抽象类,此类提供类一种访问x.509证书的所有属性的标准方式
```java
// 加载密钥库文件
FileInputStream fin = new FileInputStream(new File(""));
// 实例化KeyStore对象
KeyStore ks = KeyStore.getInstance("JKS");
// 加载密钥库
ks.load(fin, "password".toCharArray());
// 关闭文件输入流
fin.close();
// 获得x.509类型证书
X509Certificate xc = (X509Certificate)ks.getCertificate("alias");
// 通过证书标明的签名算法构建Signature对象
Signature s = Signature.getInstance(xc.getSigAlgName());// 获取验证证书的有效期
xc.getNotAfter(); 
xc.getNotBefore();// 检查给定的日期是否处于证书的有效期内
xc.checkValidity(new Date());// 获取证书的相应属性
// 获取证书的版本
xc.getVersion();
// 获取证书的SerialNumber
xc.getSerialNumber();
// 从关键BasicConstraints扩展(OID = 2.5.29.15)获取证书的限制路径长度
xc.getBasicConstraints();// 获取一个表示KeyUsage扩展（OID＝2.5.29.15）的各个位的boolean数组
xc.getKeyUsage();
//获取一个不可修改的String列表，表示已扩展的密钥使用扩展（OID=2.5.29.37）中ExtKeyUsageSyntax字段的对象标识符（OBJECT IDENTIFIER）
xc.getExtendedKeyUsage();// 获得证书的发布者的相关信息
//从IssuerAltName扩展（OID＝2.5.29.18）中获取一个发布方替换名称的不可変集合
xc.getIssuerAlternativeNames();
//获取证书的issuerUniqueID值
xc.getIssuerUniqueID();
//以x500Principal的形式返回证书的发布方（发布方标识名）值
xc.getIssuerX500Principal();// 以下方法可以获得证书主题的一些相关信息
//从SubjectAltName扩展（OID=2.5.29.17）中获取一个主体替换名称的不可变集合
xc.getSubjectAlternativeNames();
// 获取证书的SubjectUniqueID
xc.getSubjectUniqueID();
// 以行X500Principal的形式返回证书的主体(主题标志名)值
xc.getSubjectX500Principal();// 获得证书的DER编码的二进制信息
// 从次证书中获取以DER编码的二进制信息
// 从此证书中获取以DER编码的证书信息
xc.getTBSCertificate();
// 获取证书签名算法的签名算法名
xc.getSigAlgName();
// 获取证书的签名算法OID字符串
xc.getSigAlgOID();
// 从此证书的签名算法中获取DER编码形式的签名算法参数
xc.getSigAlgParams();// 获取签名值
xc.getSignature();	}
}
```

## X509CRL
作为CRl的子类,已标明了类型为X.509的CRl, X.509证书撤销列表(CRL)的抽象类.CRL是标致已撤销证书的时间戳列表.它由证书颁发机构签署,并可在公共存储库中随意使用
```java
// 实例化,并指明证书类型为X.509
CertificateFactory cf = CertificateFactory.getInstance("x.509");
// 获得证书输入流
FileInputStream in = new FileInputStream("");
// 获得证书
X509Certificate c = (X509Certificate)cf.generateCertificate(in);
// 获得证书撤销列表
X509CRL xc = (X509CRL)cf.generateCRL(in);
// 获得证书撤销列表实体
X509CRLEntry xce = xc.getRevokedCertificate(c);
in.close();
// 获得版本号
xc.getVersion();// 获得DER编码的二进制信息
// 返回CRL的ASN.1 DER的编码形式
xc.getEncoded();
// 从CRL中获取以DER编码的CRL信息
xc.getTBSCertList();// 获取crl的thisUpdate信息
xc.getThisUpdate();
// 获取crl的nextupdate信息
xc.getNextUpdate();// 获取CRL签名算法的签名算法名
xc.getSigAlgName();
// 获取CRL签名算法的OID字符串
xc.getSigAlgOID();// 获得数字签名值(原始签名位)
xc.getSignature();// 获取具有给定证书serialNumber的CRL项
//xc.getRevokedCertificate(serialNumber)// 验证是否已使用给定公钥相应的私钥签署了此CRL
//xc.verify(key);// 以X500Principal的形式返回CRL的发布方
xc.getIssuerX500Principal();	}
}
```

## X509CRLEntry
已经撤销的证书类
