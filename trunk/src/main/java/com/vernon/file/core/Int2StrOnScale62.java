package com.vernon.file.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 整数与字符串以62进制进行互转
 * User: xionglie
 * Date: 2014-04-15
 */
public class Int2StrOnScale62 {
    private static final char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
    private final static int SCALE = chars.length;

    /**
     * 图片的宽高转为62进制字符串(长度为6)形式
     *
     * @param width  图片的宽
     * @param height 图片的高
     * @return  如果是非合理范围内的数字，返回空字符串，否则返回6位长度的字符串(前3代表宽,后3代表高)
     */
    public static String toStr(int width, int height) {
        int max = (int) Math.pow(SCALE, 3);
        if (width <= 0 || height <= 0 || width >= max || height >= max) {
            return "";
        }
        return toStr(width) + toStr(height);
    }

    /**
     * 62进制字符串(长度为3)转化为数字
     *
     * @param str 62进制字符串,长度为3
     * @return 非法字符串返回-1,其它返回正确结果
     */
    public static int toInt(String str) {
        if (!isScale62Str(str)) {
            return -1;
        }
        char c2 = str.charAt(0);
        char c1 = str.charAt(1);
        char c0 = str.charAt(2);
        return (int) Math.pow(SCALE, 2) * toInt(c2) + SCALE * toInt(c1) + toInt(c0);
    }

    private static String toStr(int v) {
        int[] pos = new int[3];
        for (int i = 0; i < 3; i++) {
            pos[i] = v % SCALE;
            v = v / SCALE;
        }
        return "" + chars[pos[2]] + chars[pos[1]] + chars[pos[0]];
    }

    private static boolean isScale62Str(String v) {
        String regEx = "^[0-9A-Za-z]{3}$";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(v);
        return m.find();
    }

    private static int toInt(char c) {
        for (int i = 0; i < SCALE; i++) {
            if (chars[i] == c) {
                return i;
            }
        }
        return 0;
    }

    public static void main(String[] args) {
        for (int v = 0; v < 200; v++) {
            String str = toStr(v);
            int i = toInt(str);
            System.out.println("v=" + v + ",\tstr=" + str + ",\tint=" + i);
        }
        System.out.println(toInt("AE3"));  //303
        System.out.println(toInt("AHu"));  //480
        System.out.println(toInt("F99"));
    }

}
