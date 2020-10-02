package com.busicomjp.sapp.common.security;

import java.util.Base64;

import javax.crypto.spec.SecretKeySpec;

public class Decrypt {
	
	private static final String cryptType = "AES";
	private static final String encode = "UTF-8";
	private static final String secretkey = "BUSICOM-JP.COM";
	
	public static String decrypt(String encryptedText) {
		try {
			byte[] decBytes = decrypt(encryptedText.getBytes(encode));
			return new String(decBytes, encode);
		} catch (Exception e) {
			return null;
		}
	}
	
	private static byte[] decrypt(byte[] data) throws Exception {
		byte[] bytes = new byte[128 / 8];
		byte[] keys = secretkey.getBytes(encode);
		for (int i = 0; i < secretkey.length(); i++) {
			if (i >= bytes.length)
				break;
			bytes[i] = keys[i];
		}
		javax.crypto.spec.SecretKeySpec sksSpec = new SecretKeySpec(bytes, cryptType);
		javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance(cryptType);
		cipher.init(javax.crypto.Cipher.DECRYPT_MODE, sksSpec);
		byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(data));
		return decrypted;
	}
}
