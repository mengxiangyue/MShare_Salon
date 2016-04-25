import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;

public class RSAEncryptor {

	 // cat rsa_public_key.pem 
    public static final String DEFAULT_PUBLIC_KEY=   
    				"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA25hJvkrLh27ieyUk2lTZ"
					+"sXEVP3b2jsV9pUz2R+hTOc1JsNM7txCeiCD28cGvoj7fcdzecT84IJMjWi+QW/ox"
					+"IQjfy+zlKffDkyrRzObcot1gpYMMUlUY3092SCKz0XAJVwHHwU0GLKXXkaa+glt+"
					+"NYFKI/xY6CFjcUOayZrlZAMYi2dt9lvi+VMBnW6otT5WJcdr6rr/1XrzebLUfkRn"
					+"83FTwQKW+tk6TSMUQlKndR8j+vAEn5moeC+wW8HLSPeXowiWLD8NMcubmYvxQIO5"
					+"AkbrYSZ1DJ2T1AV4ZFoT/ItM3mmTRb9ESrM/WsKKFEcGzSoo33wxp9zilYWGI/hk"
					+"LwIDAQAB" + "\r";

    // cat pkcs8_private_key.pem
    public static final String DEFAULT_PRIVATE_KEY=
    		 "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDbmEm+SsuHbuJ7"
			+"JSTaVNmxcRU/dvaOxX2lTPZH6FM5zUmw0zu3EJ6IIPbxwa+iPt9x3N5xPzggkyNa"
			+"L5Bb+jEhCN/L7OUp98OTKtHM5tyi3WClgwxSVRjfT3ZIIrPRcAlXAcfBTQYspdeR"
			+"pr6CW341gUoj/FjoIWNxQ5rJmuVkAxiLZ232W+L5UwGdbqi1PlYlx2vquv/VevN5"
			+"stR+RGfzcVPBApb62TpNIxRCUqd1HyP68ASfmah4L7BbwctI95ejCJYsPw0xy5uZ"
			+"i/FAg7kCRuthJnUMnZPUBXhkWhP8i0zeaZNFv0RKsz9awooURwbNKijffDGn3OKV"
			+"hYYj+GQvAgMBAAECggEBAKlmwLSwxHPVAZhYHibjwBAXmXmdDzUyeuwTBAaS3elA"
			+"SbHEMlPV2UZQj9AOR6xU7lDLPzqDt4vBlksTWDzsNAw06VhKhrqqbNBR/wkfq2Nk"
			+"fhMQlmfJR+e1SBz70FYoJLxfHjcrcTDuOu8cf+jWJfWmqIg8OmXQpAMTUQxnlsBg"
			+"CI+pU/AGeKsjV4dMNgZEBCcOGGDFnjNrh60e865mgsGmx0aFfxXzWVErHxg6CNaM"
			+"pCDfDPhAeGE09n515lxorbBXWwU0ipp6P+loQnqIyPS5sQgyb521LBGzqrViHWoj"
			+"QNzqG3aUde7DRihiaCruq2sXoHDKRjlu1LyHad0tdjkCgYEA+6GLofaLnMyaP7c9"
			+"FDkPa1/i5bYapT8XO50bQ+2Vyj5NmvtoZiz36LkeLzAGh9YCfQFYDjlMVOSnLkbf"
			+"05QYVKBqfzCdVlscxher/XpL8ZRt7hBOBWqDtr8PkNzHS1iXvTibffoBwAj1pIKO"
			+"9zZCAg5QA1900alplav43ianRn0CgYEA32hZAFMbingmH1/6vBCU7RObf/+PWbdh"
			+"U+SZdu0pnW2iRC+s8ruB2nmPRCitguFSLlzZZmzL4fdUb4gP7PhcBemvJvnZ/e+3"
			+"1H3Ecog/kcSDROphnWHMr3rUy+wxYavICh/+pCBM1XxdqQtpaX2uN3f99Bj8dTJa"
			+"F9BL6owk2RsCgYB1kfRo94Yh/CMyFA6wzTxcIhAk3mIyxXi4fN6JbY/oUBX4jPEp"
			+"lGc80Im+6ISUoTTGbYNUGgeFPtSyNaFM/Uwk9lLsHK3W9HlMQTE2HRcwx/ZZHjsm"
			+"Iq9qCnleaRmhJcK4QaO9R7vbmxQTCoF8FljfFwrhmI8/ixVvPkvomEzeDQKBgGA0"
			+"kU8getWwU8pjDJh8E4XcUbRXGyYpYBI+eQ/LpYYTtjLizPliYdLxUg6p7b5UxY0N"
			+"ktkWtN3EImo9D46ejRJGxZRlN8iwuFsbJcOkhKuDEnxU6mBZw0k1/fkq50EGNoiV"
			+"qpSHDE9K1RJ8xZIV+zYLyCKbt+vIGR/0JR0F6MBNAoGBALuEflKdypHARYN5Nh9S"
			+"usHh2CMOMnGPcGl7IfbwAi7wja/17WP66in+N9XyvLg1eNMbPf4nEDO/WjNWJP35"
			+"ndfKmR+Uj331UdPriMlGgwLlP8UWIknsyXYZM2xjFJ6B803uXN+v57CEdbxM8GlZ"
			+"rQedkxvaPDgRmIjjL8llsEAx" + "\r";
    
    /** 
     * Private Key 
     */  
    private RSAPrivateKey privateKey;  
  
    /** 
     * Public Key 
     */  
    private RSAPublicKey publicKey;  
      
    /** 
     * Get Private Key 
     */  
    public RSAPrivateKey getPrivateKey() {  
        return privateKey;  
    }  
  
    /** 
     * Get Public Key
     */  
    public RSAPublicKey getPublicKey() {  
        return publicKey;  
    }
    
    public String encryptWithBase64(String string) throws Exception {
        //  http://commons.apache.org/proper/commons-codec/ : org.apache.commons.codec.binary.Base64
        // sun.misc.BASE64Encoder
        byte[] binaryData = encrypt(getPrivateKey(), string.getBytes());
        String base64String = new BASE64Encoder().encodeBuffer(binaryData) /* org.apache.commons.codec.binary.Base64.encodeBase64(binaryData) */;
        return base64String;
    }
    
    /** 
     * Encrypt using Private Key 
     */  
    public byte[] encrypt(RSAPrivateKey privateKey, byte[] plainTextData) throws Exception{  
        if(publicKey== null){  
            throw new Exception("Public Key Should Not Null For Encrypt");  
        }  
        Cipher cipher= null;  
        try {  
            cipher= Cipher.getInstance("RSA");//, new BouncyCastleProvider());  
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);  
            byte[] output= cipher.doFinal(plainTextData);  
            return output;  
        } catch (NoSuchAlgorithmException e) {  
            throw new Exception("No Such Algorithm Exception");  
        } catch (NoSuchPaddingException e) {  
            e.printStackTrace();  
            return null;  
        }catch (InvalidKeyException e) {  
            throw new Exception("Invalid Public Key Exception");  
        } catch (IllegalBlockSizeException e) {  
            throw new Exception("PlainText Illegal Block Size Exception");  
        } catch (BadPaddingException e) {  
            throw new Exception("PlainText Data Corruption or Bad Padding Exception");  
        }  
    }
    
    public String decryptWithBase64(String base64String) throws Exception {
        //  http://commons.apache.org/proper/commons-codec/ : org.apache.commons.codec.binary.Base64
        // sun.misc.BASE64Decoder
        byte[] binaryData = decrypt(getPrivateKey(), new BASE64Decoder().decodeBuffer(base64String) /*org.apache.commons.codec.binary.Base64.decodeBase64(base46String.getBytes())*/);
        String string = new String(binaryData);
        return string;
    }
    
    /** 
     * Decrypt using Private Key 
     */  
    public byte[] decrypt(RSAPrivateKey privateKey, byte[] cipherData) throws Exception{  
        if (privateKey== null){  
            throw new Exception("Private Key Should Not Null For Decrypt");  
        }  
        Cipher cipher= null;  
        try {  
            cipher= Cipher.getInstance("RSA");//, new BouncyCastleProvider());  
            cipher.init(Cipher.DECRYPT_MODE, privateKey);  
            byte[] output= cipher.doFinal(cipherData);  
            return output;  
        } catch (NoSuchAlgorithmException e) {  
            throw new Exception("No Such Algorithm Exception"); 
        } catch (NoSuchPaddingException e) {  
            e.printStackTrace();  
            return null;  
        }catch (InvalidKeyException e) {  
            throw new Exception("Invalid Private Key Exception");   
        } catch (IllegalBlockSizeException e) {  
            throw new Exception("Cipher Data Illegal Block Size Exception");  
        } catch (BadPaddingException e) {  
            throw new Exception("Cipher Data Corruption or Bad Padding Exception");  
        }         
    }
    
    public String decryptWithPublicKey(String base64String) throws Exception {
        //  http://commons.apache.org/proper/commons-codec/ : org.apache.commons.codec.binary.Base64
        // sun.misc.BASE64Decoder
        byte[] binaryData = decryptWithPulicKey(getPublicKey(), new BASE64Decoder().decodeBuffer(base64String) /*org.apache.commons.codec.binary.Base64.decodeBase64(base46String.getBytes())*/);
        String string = new String(binaryData);
        return string;
    }
    
    /** 
     * Decrypt using Public Key 
     */  
    public byte[] decryptWithPulicKey(RSAPublicKey publicKey, byte[] cipherData) throws Exception{  
        if (privateKey== null){  
            throw new Exception("Private Key Should Not Null For Decrypt");  
        }  
        Cipher cipher= null;  
        try {  
            cipher= Cipher.getInstance("RSA");//, new BouncyCastleProvider());  
            cipher.init(Cipher.DECRYPT_MODE, publicKey);  
            byte[] output= cipher.doFinal(cipherData);  
            return output;  
        } catch (NoSuchAlgorithmException e) {  
            throw new Exception("No Such Algorithm Exception"); 
        } catch (NoSuchPaddingException e) {  
            e.printStackTrace();  
            return null;  
        }catch (InvalidKeyException e) {  
            throw new Exception("Invalid Private Key Exception");   
        } catch (IllegalBlockSizeException e) {  
            throw new Exception("Cipher Data Illegal Block Size Exception");  
        } catch (BadPaddingException e) {  
            throw new Exception("Cipher Data Corruption or Bad Padding Exception");  
        }         
    }
	
    /** 
     * Load Public Key From String
     */  
    public void loadPublicKey(String publicKeyStr) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        BASE64Decoder base64Decoder = new BASE64Decoder();
        byte[] buffer = base64Decoder.decodeBuffer(publicKeyStr);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
        this.publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
    } 
    
    /** 
     * Load Private Key From String
     */
    public void loadPrivateKey(String privateKeyStr) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {  
        BASE64Decoder base64Decoder = new BASE64Decoder();
        byte[] buffer = base64Decoder.decodeBuffer(privateKeyStr);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        this.privateKey= (RSAPrivateKey) keyFactory.generatePrivate(keySpec);  
    }
    
	public static void main(String[] args) throws Exception {
		RSAEncryptor rsaEncryptor= new RSAEncryptor();  
      
      //Load The Public Key  
      try {  
          rsaEncryptor.loadPublicKey(RSAEncryptor.DEFAULT_PUBLIC_KEY);  
          System.out.println("Load Public Key Successfully, length："+rsaEncryptor.getPublicKey().toString().length());
      } catch (Exception e) {  
          System.err.println(e.getMessage());  
          System.err.println("Load Public Key Failed");  
      }  
      //Load The Private Key  
      try {  
          rsaEncryptor.loadPrivateKey(RSAEncryptor.DEFAULT_PRIVATE_KEY);  
          System.out.println("Load Private Key Successfully, length："+rsaEncryptor.getPrivateKey().toString().length());
      } catch (Exception e) {  
          System.err.println(e.getMessage());  
          System.err.println("Load Private Key Failed");  
      } 
      
      // java 私钥加密
      String test = "JAVA";
      String testRSAEnWith64 = rsaEncryptor.encryptWithBase64(test);
      System.out.println("\nEncrypt: \n" + testRSAEnWith64);
      
      System.out.println(rsaEncryptor.decryptWithPublicKey(testRSAEnWith64));
      
      // java 私钥解密
      String rsaBase46StringFromIOS =
              "18Baapl4ajANKe1P1vxIfUYVZa6j95XzmuPx9MQIt67sv5l8tGZ/jBGbHX1wqMW8jQolEMWz+/uy/m6ZJARnuu7yHqX6f75IOEoD4ZHpdf6w3OvMPA9ZP1yAiatSu9JuC26NYYCLGJRcfmboOUzZAW9IEwI/2jrrOUhbsZ//jpB6O6zPOmwQ+0F5DJVZ5DrM0+TCrACFWntfcb/7BS6UOktWttBX7yraq+xFeTe6RqY8pKEgheLoXfHr7uHSFGqGsZ7XI4JJD5y+QhBouktp/18j4KXYScxn4QrqDdMKAQDw7+hl4GZ9sl1oOY0OPcSIQeG+SyQsUgBlmI9mmtYJtQ==";
      
      String decryptStringFromIOS = rsaEncryptor.decryptWithBase64(rsaBase46StringFromIOS);
      System.out.println("Decrypt result from ios client: \n" + decryptStringFromIOS);
      
      
	}
}
