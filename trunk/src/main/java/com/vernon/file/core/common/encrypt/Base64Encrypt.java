package com.vernon.file.core.common.encrypt;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: chenyuan
 * Date: 1/2/14
 * Time: 11:32
 * To change this template use File | Settings | File Templates.
 */
public class Base64Encrypt {

    /**
     * base64编码的解码器
     *
     * @param source
     * @return
     */
    public static String decoder(String source) {
        if (source == null) {
            return null;
        }
        try {
            byte[] buff = new BASE64Decoder().decodeBuffer(source);
            return new String(buff);
        } catch (IOException ignored) {
        }
        return null;
    }

    /**
     * base64编码的解码器
     *
     * @param source
     * @return
     */
    public static byte[] decoder(byte[] source) {
        if (source == null) {
            return null;
        }
        try {
            return new BASE64Decoder()
                    .decodeBuffer(new java.io.ByteArrayInputStream(source));
        } catch (IOException ignored) {
        }
        return null;
    }

    /**
     * base64编码的编码器
     *
     * @param source
     * @return
     */
    public static String encoder(String source) {
        if (source == null) {
            return null;
        }
        return new BASE64Encoder().encode(source.getBytes())
                .replaceAll("\n", "").replaceAll("\r", "");
    }

    /**
     * base64编码的编码器
     *
     * @param source
     * @return
     */
    public static byte[] encoder(byte[] source) {
        if (source == null) {
            return null;
        }
        return new BASE64Encoder().encode(source).getBytes();
    }

    public static void main(String[] args) {
        String str = "陈袁1111111111111";
        str = encoder(str);
        System.out.println("str = " + str);
        str = decoder(str);
        System.out.println("str = " + str);
    }
}
