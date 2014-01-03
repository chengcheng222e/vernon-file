package com.vernon.file.core.common.util;

import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: chenyuan
 * Date: 1/3/14
 * Time: 10:05
 * To change this template use File | Settings | File Templates.
 */
public class UUIDUtil {

    /**
     * 获取UUID
     *
     * @return
     */
    public static final String getUUID() {
        String source = UUID.randomUUID().toString();
        String[] splits = source.split("-");
        StringBuffer result = new StringBuffer();
        for (String split : splits) {
            result.append(split);
        }
        return result.toString();
    }

    /**
     * 获得指定数目的UUID
     *
     * @param length 需要获得的UUID数量
     * @return String[] UUID数组
     */
    public static final String[] getUUID(int length) {
        if (length < 1) {
            return null;
        }
        String[] result = new String[length];
        for (int i = 0; i < length; i++) {
            result[i] = getUUID();
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(getUUID());
        System.out.println(getUUID(2));
    }
}
