package com.seatus.Utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by rohail on 23-Mar-17.
 */

public class EncryptionHelper {

    private static final String KEY = "57238004e784498bbc2f8bf984565090";

    public static String encryptAES(String clearText) {
        byte[] encryptedText = null;
        try {
            byte[] keyData = hexStringToByteArray(KEY);
            IvParameterSpec ivspec;
            ivspec = new IvParameterSpec(keyData);
            SecretKeySpec ks = new SecretKeySpec(keyData, "AES");
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(Cipher.ENCRYPT_MODE, ks,ivspec);


            encryptedText = c.doFinal(clearText.getBytes("UTF-8"));


            return byteArrayToHexString(encryptedText);
        } catch (Exception e) {
            return null;
        }
    }

    public static String decryptAES(String encryptedStr) {
        byte[] clearText = null;
        byte[] encryptedText = hexStringToByteArray(encryptedStr);
        try {
            byte[] keyData = hexStringToByteArray(KEY);
            IvParameterSpec ivspec;
            ivspec = new IvParameterSpec(keyData);
            SecretKey ks = new SecretKeySpec(keyData, "AES");
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(Cipher.DECRYPT_MODE, ks,ivspec);
            clearText = c.doFinal(encryptedText);
            return new String(clearText, "UTF-8");
        } catch (Exception e) {
            return null;
        }
    }

    public static byte[] hexStringToByteArray(String s) {
        byte[] b = new byte[s.length() / 2];
        for (int i = 0; i < b.length; i++) {
            int index = i * 2;
            int v = Integer.parseInt(s.substring(index, index + 2), 16);
            b[i] = (byte) v;
        }
        return b;
    }

    public static String byteArrayToHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            int v = b[i] & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase();
    }
}
