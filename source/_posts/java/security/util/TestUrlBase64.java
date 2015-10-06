package wang.gnim.corejava.security.util;


import java.net.URLDecoder;

import org.bouncycastle.util.encoders.UrlBase64;
import org.junit.Assert;
import org.junit.Test;

public class TestUrlBase64 {

	static final String base64 = "base64编码!@#$%^&*()+_=-{}[];:'<>,./?|";
	
	@Test
	public void before () {
		System.out.println();
		System.out.println(base64);
	}
	
	@Test
	public void testUrlBase64() {
		System.out.println();
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
		System.out.println();
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
		System.out.println();
		byte[] encode = org.bouncycastle.util.encoders.Base64.encode(base64.getBytes());
		System.out.println("BouncycastleBase64 : " + new String(encode));
		byte[] decode = org.bouncycastle.util.encoders.Base64.decode(encode);
		Assert.assertEquals(base64, new String(decode));
	}
	
	
	@Test
	public void testSpace() {
		String base64 = "wKOS4FsxiFvE48KGGSuSkRui9Iap1ukgl1+eVqZiGhXQYYiP8KGCV%2FRIeTEyMLsWxE%2FEx6jhuW3DPUt4JYX+cohUOqFVVaQ%2FioGZCAge3ygaCz%2Fe4q8o9XQzOEtcdXPywGZ0e5sgE787ij4dRZy2ILK2cxsVvC8yrlIPGZ3LUg8nOj8oEg5l2AnQnA3i+Sxbgqmwe1OjIXVZqPZWb+Y4SVQL8EpWlmEjXb4HjgmGTgVYzwJ64QO7HUPP1yuQHkS0PLS%2FpbPrgL5vqTF7h%2FPvMw=%3D"; 
		String decoded = URLDecoder.decode(base64);
		System.out.println(decoded);
		byte[] decode = UrlBase64.decode(base64);
		System.out.println(new String(decode));
	}
}
