/*
 * Copyright ROCO 2011. All rights reserved.
 */
package org.funsoft.remoteagent.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.eclipse.jetty.util.security.Password;
import org.painlessgridbag.util.Assert;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;


/**
 * @author hun
 *
 */
public final class CryptoUtil {
	private static final byte KEY_SIZE = 16; // 128bit
	private static final String SALT = "ddACDSjw%DA1209A"; // this also ensure the encryption key >= 16
	
	public static String obfucatePasswordWithJettyPasswordObfuscator(String pw) {
		String obfuscatedPw = Password.obfuscate(pw);
		String obf = obfuscatedPw.substring(0, Password.__OBFUSCATE.length());
		if (!obf.equals(Password.__OBFUSCATE)) {
			throw new RuntimeException("jetty password obfuscator now no longer generates prefix: "
					+ Password.__OBFUSCATE);
		}
		return obfuscatedPw.substring(Password.__OBFUSCATE.length());
	}

	public static String encrypt(String text, String encryptionKey) {
		if (StringUtils.isBlank(text)) {
			return null;
		}
		
		try {
			return encrypt(text.getBytes("UTF-8"), encryptionKey);
		} catch (Exception e) {
			// Decryption may fail, BUT Encryption must be success
			throw new RuntimeException(e);
		}
	}
	
	private static String encrypt(byte[] data, String encryptionKey) {
		Validate.isTrue(StringUtils.isNotBlank(encryptionKey), "encryption key is not provided");
		
		try {
			SecretKeySpec secretKeySpec = normalizeKey(encryptionKey);
			
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
			
			byte[] encoded = cipher.doFinal(data);
			
			// inspired from com.iplanet.services.util.Crypt.encode()
			String str = new String(new Base64().encode(embedIV(cipher.getIV(), encoded))).trim();
			BufferedReader bufReader = new BufferedReader(new StringReader(str));
			StringBuffer strClean = new StringBuffer(str.length());
			String strTemp = null;
			while ((strTemp = bufReader.readLine()) != null) {
				strClean.append(strTemp);
			}
			
			return strClean.toString();
		} catch (Exception e) {
			// Decryption may fail, BUT Encryption must be success
			throw new RuntimeException(e);
		}
	}
	
	private static SecretKeySpec normalizeKey(String encryptionKey)
			throws UnsupportedEncodingException, NoSuchAlgorithmException {
		byte[] key = (encryptionKey + SALT).getBytes("UTF-8");
		MessageDigest sha = MessageDigest.getInstance("SHA-1");
		key = sha.digest(key);
		key = Arrays.copyOf(key, KEY_SIZE); // use only first 128 bit
		return new SecretKeySpec(key, "AES");
	}
	
	private static byte[] embedIV(byte iv[], byte share[]) {
		Assert.isTrue(iv.length == KEY_SIZE, "Expect Initial Vector length 128 bit in AES-128");
		byte data[] = new byte[share.length + KEY_SIZE];
		for (int i = 0; i < iv.length; i++) {
			data[i] = iv[i];
		}
		for (int i = 0; i < share.length; i++) {
			data[iv.length + i] = share[i];
		}
		return data;
	}
	
	private static byte[] decryptToBytes(String encryptedText, String encryptionKey) {
		Validate.isTrue(StringUtils.isNotBlank(encryptionKey), "encryption key is not provided");
		try {
			byte[] encData = new Base64().decode(encryptedText);
			IvParameterSpec ivspec = new IvParameterSpec(ArrayUtils.subarray(encData, 0, KEY_SIZE));
			encData = ArrayUtils.subarray(encData, KEY_SIZE, encData.length);
			
			SecretKeySpec secretKeySpec = normalizeKey(encryptionKey);
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivspec);
			return cipher.doFinal(encData);
		} catch (Exception e) {
			// This may result in case user provide dump "text"
			return null;
		}
	}
	
	public static String decrypt(String text, String encryptionKey) {
		if (StringUtils.isBlank(text)) {
			return null;
		}
		return new String(decryptToBytes(text, encryptionKey));
	}
}