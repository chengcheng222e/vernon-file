package com.vernon.file.client;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created with IntelliJ IDEA.
 * User: chenyuan
 * Date: 1/2/14
 * Time: 11:28
 * To change this template use File | Settings | File Templates.
 */
public class MD5Encrypt {

    /**
     * 加密为MD5
     *
     * @param source
     * @return
     */
    public static String encoderForString(String source) {
        byte[] md5Bytes = MD5Encrypt.encoderForBytes(source);
        if (md5Bytes != null) {
            StringBuilder hexValue = new StringBuilder();
            for (int i = 0; i < md5Bytes.length; i++) {
                int val = ((int) md5Bytes[i]) & 0xff;
                if (val < 16) {
                    hexValue.append("0");
                }
                hexValue.append(Integer.toHexString(val));
            }
            return hexValue.toString().toUpperCase();
        }
        return null;
    }

    /**
     * 加密为MD5
     *
     * @param source
     * @return
     */
    public static byte[] encoderForBytes(String source) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            char[] charArray = source.toCharArray();
            byte[] byteArray = new byte[charArray.length];
            for (int i = 0; i < charArray.length; i++) {
                byteArray[i] = (byte) charArray[i];
            }
            return digest.digest(byteArray);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 加密为MD5
     *
     * @param source
     * @return
     */
    public static byte[] encoderForBytes(byte[] source) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            return digest.digest(source);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 加密为MD5
     *
     * @param source
     * @return
     */
    public static String encoderForString(byte[] source) {
        byte[] md5Bytes = MD5Encrypt.encoderForBytes(source);
        if (md5Bytes != null) {
            StringBuilder hexValue = new StringBuilder();
            for (int i = 0; i < md5Bytes.length; i++) {
                int val = ((int) md5Bytes[i]) & 0xff;
                if (val < 16) {
                    hexValue.append("0");
                }
                hexValue.append(Integer.toHexString(val));
            }
            return hexValue.toString().toUpperCase();
        }
        return null;
    }

    public static void main(String[] args) {
        String str = "123456";
        str = MD5Encrypt.encoderForString(str);
        System.out.println("str = " + str);
    }
}
