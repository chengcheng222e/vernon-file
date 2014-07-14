package com.vernon.file.core;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: chenyuan
 * Date: 1/3/14
 * Time: 8:54
 * To change this template use File | Settings | File Templates.
 */
public class Constant {

    public final static Map<String, String> accessKeyMap = new HashMap<String, String>();

    static {
        accessKeyMap.put("lodawd98232", "2313sd32131231321");
        accessKeyMap.put("and13101", "cml53f5c6");
        accessKeyMap.put("ios02901", "2ef0p17fa");
        accessKeyMap.put("web30101", "b67179c2d");
        accessKeyMap.put("dzq32301", "0c1gdcca5");
    }

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
}
