package com.babyduncan.rsa;



import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Encryptor {

	/**
	 * 
	 * @param source
	 * @return
	 */
	public static byte[] encode2bytes(String source) {
		byte[] result = null;
		
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(source.getBytes("UTF-8"));
			result = messageDigest.digest();
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param source
	 * @return
	 */
	public static String encode2hex(String source) {
		byte[] data = encode2bytes(source);
		
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < data.length; i++) {  
            String hex = Integer.toHexString(0xff & data[i]);  
              
            if (hex.length() == 1) {  
                hexString.append('0');  
            }  
              
            hexString.append(hex);  
        }  
          
        return hexString.toString(); 
	}
	
	/**
	 * 
	 * @param unknown
	 * @param okHex
	 * @return
	 */
	public static boolean validate(String unknown , String okHex) {  
        return okHex.equals(encode2hex(unknown));  
    }
}
