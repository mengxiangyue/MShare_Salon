package com.mxy.rsaservice;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;

public class RSAEncryptor {
	/**
	 * // https://github.com/jslim89/RSA-objc 
	 * // 下面这两个key是oc使用的 
	 * openssl genrsa -out private_key.pem 2048 
	 * openssl rsa -in private_key.pem -pubout -out public_key.pem
	 * 
	 * // http://isaacselement.github.io/2014/06/18/rsa-encrypt-and-decrypt-in-ios-and-java/ 
	 * // 下面这两个key是java使用的 
	 * openssl rsa -in private_key.pem -out rsa_public_key.pem -pubout 
	 * openssl pkcs8 -topk8 -in private_key.pem -out pkcs8_private_key.pem -nocrypt
	 */

	public static RSAEncryptor sharedInstance = null;

	public static void setSharedInstance(RSAEncryptor rsaEncryptor) {
		sharedInstance = rsaEncryptor;
	}

	private RSAPrivateKey privateKey;

	private RSAPublicKey publicKey;

	public RSAEncryptor(String publicKeyFilePath, String privateKeyFilePath) throws Exception {
		String public_key = getKeyFromFile(publicKeyFilePath);
		String private_key = getKeyFromFile(privateKeyFilePath);
		loadPublicKey(public_key);
		loadPrivateKey(private_key);
	}

	public String getKeyFromFile(String filePath) throws Exception {
		BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));

		String line = null;
		List<String> list = new ArrayList<String>();
		while ((line = bufferedReader.readLine()) != null) {
			list.add(line);
		}

		// remove the firt line and last line
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 1; i < list.size() - 1; i++) {
			stringBuilder.append(list.get(i)).append("\r");
		}

		String key = stringBuilder.toString();
		return key;
	}

	public String decryptedByPublicKey(String base64String) throws Exception {
		byte[] binaryData = decrypt(getPublicKey(), new BASE64Decoder().decodeBuffer(
				base64String));
		String string = new String(binaryData);
		return string;
	}

	public String decryptedByPrivateKey(String base64String) throws Exception {
		byte[] binaryData = decrypt(getPrivateKey(), new BASE64Decoder().decodeBuffer(
				base64String));
		String string = new String(binaryData);
		return string;
	}

	public String encryptedByPublicKey(String string) throws Exception {
		byte[] binaryData = encrypt(getPublicKey(), string.getBytes());
		String base64String = new BASE64Encoder()
				.encodeBuffer(binaryData);
		return base64String;
	}

	public String encryptedByPrivateKey(String string) throws Exception {
		byte[] binaryData = encrypt(getPrivateKey(), string.getBytes());
		String base64String = new BASE64Encoder()
				.encodeBuffer(binaryData);
		return base64String;
	}

	public RSAPrivateKey getPrivateKey() {
		return privateKey;
	}

	public RSAPublicKey getPublicKey() {
		return publicKey;
	}

	/**
	 * Generate Key Pair Randomly by JDK
	 */
	public void genKeyPair() {
		KeyPairGenerator keyPairGen = null;
		try {
			keyPairGen = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		keyPairGen.initialize(1024, new SecureRandom());
		KeyPair keyPair = keyPairGen.generateKeyPair();
		this.privateKey = (RSAPrivateKey) keyPair.getPrivate();
		this.publicKey = (RSAPublicKey) keyPair.getPublic();
	}

	/**
	 * Load Public Key From InputStream
	 */
	public void loadPublicKey(InputStream in) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String readLine = null;
		StringBuilder sb = new StringBuilder();
		while ((readLine = br.readLine()) != null) {
			if (readLine.charAt(0) == '-') {
				continue;
			} else {
				sb.append(readLine);
				sb.append('\r');
			}
		}
		loadPublicKey(sb.toString());
	}

	/**
	 * Load Public Key From String
	 */
	public void loadPublicKey(String publicKeyStr)
			throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		BASE64Decoder base64Decoder = new BASE64Decoder();
		byte[] buffer = base64Decoder.decodeBuffer(publicKeyStr);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
		this.publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
	}

	/**
	 * Load Private Key From InputStream
	 */
	public void loadPrivateKey(InputStream in) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String readLine = null;
		StringBuilder sb = new StringBuilder();
		while ((readLine = br.readLine()) != null) {
			if (readLine.charAt(0) == '-') {
				continue;
			} else {
				sb.append(readLine);
				sb.append('\r');
			}
		}
		loadPrivateKey(sb.toString());
	}

	/**
	 * Load Private Key From String
	 */
	public void loadPrivateKey(String privateKeyStr)
			throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		BASE64Decoder base64Decoder = new BASE64Decoder();
		byte[] buffer = base64Decoder.decodeBuffer(privateKeyStr);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		this.privateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
	}

	/**
	 * Encrypt using Public Key
	 */
	public byte[] encrypt(RSAPublicKey publicKey, byte[] plainTextData) throws Exception {
		if (publicKey == null) {
			throw new Exception("Public Key Should Not Null For Encrypt");
		}
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance("RSA");// , new BouncyCastleProvider());
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			byte[] output = cipher.doFinal(plainTextData);
			return output;
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("No Such Algorithm Exception");
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidKeyException e) {
			throw new Exception("Invalid Public Key Exception");
		} catch (IllegalBlockSizeException e) {
			throw new Exception("PlainText Illegal Block Size Exception");
		} catch (BadPaddingException e) {
			throw new Exception("PlainText Data Corruption or Bad Padding Exception");
		}
	}

	public byte[] encrypt(RSAPrivateKey privateKey, byte[] plainTextData) throws Exception {
		if (publicKey == null) {
			throw new Exception("Public Key Should Not Null For Encrypt");
		}
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance("RSA");// , new BouncyCastleProvider());
			cipher.init(Cipher.ENCRYPT_MODE, privateKey);
			byte[] output = cipher.doFinal(plainTextData);
			return output;
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("No Such Algorithm Exception");
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidKeyException e) {
			throw new Exception("Invalid Public Key Exception");
		} catch (IllegalBlockSizeException e) {
			throw new Exception("PlainText Illegal Block Size Exception");
		} catch (BadPaddingException e) {
			throw new Exception("PlainText Data Corruption or Bad Padding Exception");
		}
	}

	/**
	 * Decrypt using Private Key
	 */
	public byte[] decrypt(RSAPrivateKey privateKey, byte[] cipherData) throws Exception {
		if (privateKey == null) {
			throw new Exception("Private Key Should Not Null For Decrypt");
		}
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance("RSA");// , new BouncyCastleProvider());
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] output = cipher.doFinal(cipherData);
			return output;
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("No Such Algorithm Exception");
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidKeyException e) {
			throw new Exception("Invalid Private Key Exception");
		} catch (IllegalBlockSizeException e) {
			throw new Exception("Cipher Data Illegal Block Size Exception");
		} catch (BadPaddingException e) {
			throw new Exception("Cipher Data Corruption or Bad Padding Exception");
		}
	}

	public byte[] decrypt(RSAPublicKey publicKey, byte[] cipherData) throws Exception {
		if (privateKey == null) {
			throw new Exception("Private Key Should Not Null For Decrypt");
		}
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance("RSA");// , new BouncyCastleProvider());
			cipher.init(Cipher.DECRYPT_MODE, publicKey);
			byte[] output = cipher.doFinal(cipherData);
			return output;
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("No Such Algorithm Exception");
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidKeyException e) {
			throw new Exception("Invalid Private Key Exception");
		} catch (IllegalBlockSizeException e) {
			throw new Exception("Cipher Data Illegal Block Size Exception");
		} catch (BadPaddingException e) {
			throw new Exception("Cipher Data Corruption or Bad Padding Exception");
		}
	}
}
