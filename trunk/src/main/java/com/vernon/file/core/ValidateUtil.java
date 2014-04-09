package com.vernon.file.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: chenyuan
 * Date: 4/8/14
 * Time: 21:46
 * To change this template use File | Settings | File Templates.
 */
public class ValidateUtil {

    /**
     * 验证裁剪参数
     *
     * @param cropParam
     * @return
     */
    public static boolean isCropParams(String cropParam) {
        String regEx = "^\\d{1,5},\\d{1,5},[1-9]\\d{0,5},[1-9]\\d{0,5}$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher m = pattern.matcher(cropParam);
        return m.find();
    }
}
