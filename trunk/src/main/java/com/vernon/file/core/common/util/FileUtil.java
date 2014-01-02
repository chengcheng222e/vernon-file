package com.vernon.file.core.common.util;

import com.vernon.file.core.Config;
import com.vernon.file.core.FileType;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: chenyuan
 * Date: 1/2/14
 * Time: 10:15
 * To change this template use File | Settings | File Templates.
 */
public class FileUtil {
    private static Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

    private final static Map<String, String> imageAllowTypes = new HashMap<String, String>();
    private final static Map<String, String> voiceAllowTypes = new HashMap<String, String>();
    private final static Map<String, String> otherAllowTypes = new HashMap<String, String>();
    public final static String SEPARATOR = "/";

    static {
        String imageAllowTypeStr = Config.INSTANCE.get("imageAllowTypes", "bmp,jpg,x-png,gif,jpeg,png");
        String[] imgTypes = imageAllowTypeStr.split(",");
        for (String type : imgTypes) {
            imageAllowTypes.put(type, type);
        }

        String voiceAllowTypeStr = Config.INSTANCE.get("voiceAllowTypes", "mp3");
        String[] voiceTypes = voiceAllowTypeStr.split(",");
        for (String type : voiceTypes) {
            voiceAllowTypes.put(type, type);
        }

        String otherAllowTypeStr = Config.INSTANCE.get("otherAllowTypes", "txt,rar,jar,zip,doc,docx,xls,xlsx,ppt,pptx,chm,pdf,gz,cr");
        String[] otherTypes = otherAllowTypeStr.split(",");
        for (String type : otherTypes) {
            otherAllowTypes.put(type, type);
        }

    }

    /**
     * 判断文件的格式是否合法格式
     *
     * @param ext
     * @return
     */
    public static boolean isArrowFileType(String ext) {
        ext = ext.toLowerCase();
        return imageAllowTypes.containsKey(ext)
                || voiceAllowTypes.containsKey(ext)
                || otherAllowTypes.containsKey(ext);
    }

    /**
     * 是否是合法图片格式
     *
     * @param ext
     * @return
     */
    public static boolean isImageArrowFileType(String ext) {
        ext = ext.toLowerCase();
        return imageAllowTypes.containsKey(ext);
    }


    /**
     * 获取文件类型
     *
     * @param ext
     * @return
     */
    public static FileType getFileType(String ext) {
        ext = ext.toLowerCase();
        if (imageAllowTypes.containsKey(ext)) {
            return FileType.IMAGE;
        } else if (voiceAllowTypes.containsKey(ext)) {
            return FileType.AUDIO;
        } else if (otherAllowTypes.containsKey(ext)) {
            return FileType.OTHER;
        } else {
            return FileType.NOTALLOW;
        }
    }

    /**
     * 获取文件后缀名
     *
     * @param uri
     * @return
     */
    public static String getExtension(String uri) {
        return FilenameUtils.getExtension(uri);
    }

    /**
     * 相对路径
     *
     * @param baseName
     * @param ext
     * @return
     */
    public static String getRelativePath(String baseName, String ext) {
        StringBuffer sBuffer = new StringBuffer();
        sBuffer.append(SEPARATOR);
        sBuffer.append(baseName.substring(0, 4));
        sBuffer.append(SEPARATOR);
        sBuffer.append(baseName);
        sBuffer.append("." + ext);
        return sBuffer.toString();
    }
}

