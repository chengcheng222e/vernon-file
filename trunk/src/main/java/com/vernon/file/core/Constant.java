package com.vernon.file.core;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created with IntelliJ IDEA.
 * User: chenyuan
 * Date: 1/3/14
 * Time: 8:54
 * To change this template use File | Settings | File Templates.
 */
public class Constant {

    public final static String BASEPATH = Config.INSTANCE.get("basePath");
    public final static String THUMBNAILPATH = Config.INSTANCE.get("thumbnailPath");
    public final static String TMPDIR = Config.INSTANCE.get("tmpDir");

    public static String IM4JAVATOOPATH = "";

    static {
        IM4JAVATOOPATH = Config.INSTANCE.get("IM4JAVA_TOOLPATH");
        try {
            IM4JAVATOOPATH = URLDecoder.decode(IM4JAVATOOPATH, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("IM4JAVATOOPATH: " + IM4JAVATOOPATH);
    }
}
