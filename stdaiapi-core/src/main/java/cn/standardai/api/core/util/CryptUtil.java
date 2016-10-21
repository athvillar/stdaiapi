package cn.standardai.api.core.util;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class CryptUtil {

	public static final String KEY_SHA = "SHA";

	public static final String KEY_MD5 = "MD5";

	public static final String UTF8 = "UTF-8";

	public static byte[] encryptMD5(byte[] data) {
		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance(KEY_MD5);
			md5.update(data);
			return md5.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static byte[] encryptHMACSHA1(String key, String msg) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
		Mac mac = Mac.getInstance("HmacSHA1");
		mac.init(new SecretKeySpec(key.getBytes(UTF8), "HmacSHA1"));
		return mac.doFinal(msg.getBytes(UTF8));
	}

	public static String encodeBase64(byte[] bytes) throws UnsupportedEncodingException {
        return new String(Base64.encodeBase64(bytes), UTF8);
    }

	public static void main(String[] args) throws Exception {
		String pwd = "bb";
		byte[] b = encryptMD5(pwd.getBytes());
		System.out.println(b.length);
		for (int i = 0; i < b.length; i++) {
			System.out.print(Byte.toString(b[i]));
		}
		// [33, -83, 11, -40, 54, -71, 13, 8, -12, -49, 100, 11, 76, 41, -114, 124]
		System.out.println(new BigInteger(encryptMD5(pwd.getBytes())).toString(16));
	}
}
