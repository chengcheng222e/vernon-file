package com.vernon.file.core.common.encrypt;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: chenyuan
 * Date: 1/2/14
 * Time: 11:37
 * To change this template use File | Settings | File Templates.
 */
public class NumberEncrypt {

    // ------------------------------ FIELDS ------------------------------

    private static final Map<String, String> NUMBER_LETTER = new HashMap<String, String>();

    // -------------------------- STATIC METHODS --------------------------

    /**
     * 转换
     *
     * @param src 来源
     * @return String
     */
    public static String covert(String src) {
        if (StringUtils.isBlank(src)) {
            return null;
        }
        char[] chars = src.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char c : chars) {
            String str = NUMBER_LETTER.get(String.valueOf(c));
            sb.append(str);
        }
        return sb.toString();
    }

    static {
        NUMBER_LETTER.put("d", "0");
        NUMBER_LETTER.put("i", "1");
        NUMBER_LETTER.put("r", "2");
        NUMBER_LETTER.put("b", "3");
        NUMBER_LETTER.put("v", "4");
        NUMBER_LETTER.put("t", "5");
        NUMBER_LETTER.put("y", "6");
        NUMBER_LETTER.put("e", "7");
        NUMBER_LETTER.put("j", "8");
        NUMBER_LETTER.put("f", "9");

        NUMBER_LETTER.put("0", "d");
        NUMBER_LETTER.put("1", "i");
        NUMBER_LETTER.put("2", "r");
        NUMBER_LETTER.put("3", "b");
        NUMBER_LETTER.put("4", "v");
        NUMBER_LETTER.put("5", "t");
        NUMBER_LETTER.put("6", "y");
        NUMBER_LETTER.put("7", "e");
        NUMBER_LETTER.put("8", "j");
        NUMBER_LETTER.put("9", "f");
    }

    public static void main(String[] args) {
        String num = "12451";
        num = covert(num);
        System.out.println("num begin: " + num);

        num = covert(num);
        System.out.println("num after: " + num);
    }
}
