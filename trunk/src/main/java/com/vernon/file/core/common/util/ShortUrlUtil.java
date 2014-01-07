package com.vernon.file.core.common.util;

import com.vernon.file.core.common.encrypt.MD5Encrypt;

/**
 * 短路径util类, 62的6次方中
 *
 * @author xionglie
 */
public class ShortUrlUtil {

    private final static String[] chars = new String[]{
            "a", "b", "c", "d", "e", "f", "g",
            "h", "i", "j", "k", "l", "m", "n",
            "o", "p", "q", "r", "s", "t",
            "u", "v", "w", "x", "y", "z",
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "A", "B", "C", "D", "E", "F", "G",
            "H", "I", "J", "K", "L", "M", "N",
            "O", "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z"
    };

    /**
     * Java版短网址(ShortUrl)的算法
     * ① 将长网址用md5算法生成32位签名串，分为4段,，每段8个字符；<br/>
     * ② 对这4段循环处理，取每段的8个字符, 将他看成16进制字符串与0x3fffffff(30位1)的位与操作，超过30位的忽略处理；<br/>
     * ③ 将每段得到的这30位又分成6段，每5位的数字作为字母表的索引取得特定字符，依次进行获得6位字符串； <br/>
     * ④ 这样一个md5字符串可以获得4个6位串，取里面的任意一个就可作为这个长url的短url地址。<br/>
     * http://www.sunchis.com/html/java/javaweb/2011/0418/309.html
     *
     * @param str
     * @return
     */
    private static String[] shortText(String str) {
        String hex = MD5Encrypt.encoderForString(System.currentTimeMillis() + str);
        int hexLen = hex.length();
        int subHexLen = hexLen / 8;
        String[] shortStr = new String[4];

        for (int i = 0; i < subHexLen; i++) {
            String outChars = "";
            int j = i + 1;
            String subHex = hex.substring(i * 8, j * 8);
            long idx = Long.valueOf("3FFFFFFF", 16) & Long.valueOf(subHex, 16);

            for (int k = 0; k < 6; k++) {
                int index = (int) (Long.valueOf("0000003D", 16) & idx);
                outChars += chars[index];
                idx = idx >> 5;
            }
            shortStr[i] = outChars;
        }
        return shortStr;
    }

    public static String shortUrl(String url) {
        return shortText(url)[0];
    }

    public static void main(String[] args) {
        String str = "http://www.baidu.com";
        String[] strs = shortText(str);
        for (String s : strs) {
            System.out.println("s = " + s);
        }
        str = strs[0];
        System.out.println(str);
    }

}
