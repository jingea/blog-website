package wang.gnim.corejava.security.util;

import org.apache.commons.codec.binary.Base64;

public class TestBase64 {

	public static void main(String[] args) {
		String base64 = "base64编码";
		String encode = Base64.encodeBase64String(base64.getBytes());
		System.out.println(new String(encode));

		byte[] decode = Base64.decodeBase64(encode.getBytes());
		System.out.println(new String(decode));
	}
}
