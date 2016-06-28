package com.company.umeng;



import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
/**
 * Created by umeng on 4/18/16.
 */
public class AESUtils {
    public static String getEncryptedMap(String plaintext,String appSecret) {
        if(null==plaintext){
            throw new RuntimeException("data is null");
        }

        // 加密
        String encrypt = encrypt(plaintext,appSecret);

        return  encrypt;
    }

    /*
     * 对明文加密.
     * @param text 需要加密的明文
     * @return 加密后base64编码的字符串
     */
    private static String encrypt(String plaintext,String appSecret){
        try {
            byte[] plainTextBytes = plaintext.getBytes();
            int length = plaintext.length();
            byte [] fourByteArray  = new byte[]{(byte)(length&0xff)};
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byteArrayOutputStream.write(fourByteArray);
            byteArrayOutputStream.write(plainTextBytes);
            byte[] unencrypted = ByteBuffer.wrap(byteArrayOutputStream.toByteArray()).order(ByteOrder.BIG_ENDIAN).array();
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(appSecret.getBytes(), "AES");
            IvParameterSpec iv = new IvParameterSpec(appSecret.getBytes(), 0, 16);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);
            byte[] encrypted = cipher.doFinal(unencrypted);
            String result = com.company.umeng.Base64.encodeToString(encrypted, com.company.umeng.Base64.NO_WRAP);
            return result;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

}
