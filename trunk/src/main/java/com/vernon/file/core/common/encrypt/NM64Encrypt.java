package com.vernon.file.core.common.encrypt;

/**
 * Created with IntelliJ IDEA.
 * User: chenyuan
 * Date: 1/2/14
 * Time: 11:36
 * To change this template use File | Settings | File Templates.
 */
public class NM64Encrypt {

    public static final String CODE = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ()";

    /**
     * 转换16进制数字为byte数组
     *
     * @param source
     * @return
     */
    public static byte[] fromHex(String source) {
        int length = source.length() / 2;
        if (source.length() % 2 != 0) {
            length += 1;
        }
        byte[] buff = new byte[length];
        for (int i = 0; i < length; i++) {
            buff[i] = (byte) (Integer.parseInt(
                    source.substring(i * 2, i * 2 + 2), 16) & 0x000000FF);
        }
        return buff;
    }

    /**
     * 转换为16进制
     *
     * @param buff
     * @return
     */
    public static String toHex(byte[] buff) {
        StringBuilder hexValue = new StringBuilder();
        for (int i = 0; i < buff.length; i++) {
            int val = ((int) getByte(buff, i)) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString().toUpperCase();
    }

    /**
     * 获取数组
     *
     * @param buff
     * @param idx
     * @return
     */
    private static byte getByte(byte[] buff, int idx) {
        if (idx < buff.length) {
            return buff[idx];
        }
        return 0;
    }

    private static String toString(byte[] buff) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < buff.length; i++) {
            sb.append("{").append(i).append("\t").append(buff[i]).append("\t").append(Integer.toHexString(buff[i]))
                    .append("\t").append(Integer.toBinaryString(buff[i])).append("}\n");
        }
        return sb.toString();
    }

    /**
     * 解码
     *
     * @param source
     * @return
     */
    public static String decode(String source) {
        int length = source.length() * 6 / 8;
        if (source.length() * 6 % 8 != 0) {
            length += 1;
        }
        byte[] buff = new byte[length];
        for (int i = 0; i < source.length(); i++) {
            String v = source.substring(i, i + 1);
            int idx = CODE.indexOf(v);
            int mod = i % 4;
            int div = i / 4;
            if (mod == 0) {
                buff[(div * 3)] |= (byte) (idx);
            } else if (mod == 1) {
                buff[(div * 3)] |= (byte) (idx << 6);
                buff[(div * 3 + 1)] |= (byte) (idx >> 2);
            } else if (mod == 2) {
                buff[(div * 3 + 1)] |= (byte) (idx << 4);
                buff[(div * 3 + 2)] |= (byte) (idx >> 4);
            } else if (mod == 3) {
                buff[(div * 3) + 2] |= (byte) (idx << 2);
            }

        }
        return toHex(buff);
    }

    /**
     * 编码
     *
     * @param source
     * @return
     */
    public static String encode(String source) {
        byte[] buff = fromHex(source);

        int length = ((buff.length * 8) / 6);
        if ((buff.length * 8) % 6 != 0) {
            length += 1;
        }

        char[] chars = new char[length];
        for (int i = 0; i < length; i++) {
            int mod = i % 4;
            int div = i / 4;
            int idx = 0;
            if (mod == 0) {
                idx = getByte(buff, div * 3) & 0x3F;
            } else if (mod == 1) {
                byte up = (byte) (getByte(buff, (div * 3)) >> 6);
                byte down = (byte) (getByte(buff, div * 3 + 1) << 2);
                idx = ((up & 0x03) | (down & 0x3F)) & 0x3F;
            } else if (mod == 2) {
                //System.out.println("i:" + i + " " + (div * 3 + 1) + "||||"
                //		+ (div * 3 + 2));
                byte up = (byte) (getByte(buff, div * 3 + 1) >> 4);
                byte down = (byte) (getByte(buff, div * 3 + 2) << 4);
                idx = ((up & 0x0F) | (down & 0x30)) & 0x3F;
            } else if (mod == 3) {
                idx = (getByte(buff, div * 3 + 2) >> 2) & 0x3F;
            }
            chars[i] = CODE.charAt(idx);
            System.out.println("idx:" + idx + " i:" + i + " mod:" + mod
                    + " div:" + div);

        }
        System.out.println();

        return new String(chars);
    }

    public static void main(String[] args) {
        // String md5 = MD5Encrypt.encoderForString("123456");
        // String nm64 = NM64Encrypt.encode(md5, 16);
        // System.out.println(nm64);
        // System.out.println(NM64Encrypt.encode2(md5, 16));
        String md5 = MD5Encrypt.encoderForString("1");
        System.out.println("md5:" + md5);
        byte[] buff = NM64Encrypt.fromHex(md5);
        System.out.println(NM64Encrypt.toString(buff));
        System.out.println("hex:" + NM64Encrypt.toHex(buff));

        String nm64 = NM64Encrypt.encode(md5);
        System.out.println("NM64:" + nm64);

        String encode = NM64Encrypt.decode(nm64);
        System.out.println("MD5:" + encode);
        encode = encode.substring(0, 32);
        System.out.println("MD5:" + encode);
        System.out.println(encode.equals(md5));
    }
}
