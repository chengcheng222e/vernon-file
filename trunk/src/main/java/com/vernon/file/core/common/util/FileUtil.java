package com.vernon.file.core.common.util;

import com.google.common.base.Splitter;
import com.vernon.file.core.Config;
import com.vernon.file.core.Constant;
import com.vernon.file.core.FileType;
import com.vernon.file.domain.ImageParam;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

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

    private static Set<String> imageAllowSet = null;
    private static Set<String> voiceAllowSet = null;
    private static Set<String> otherAllowSet = null;

    static {
        String imageAllowTypeStr = Config.INSTANCE.get("imageAllowTypes", "jpg,jpeg,gif,png,bmp");
        imageAllowSet = getAllowSet(imageAllowTypeStr);

        String voiceAllowTypeStr = Config.INSTANCE.get("voiceAllowTypes", "mp3");
        voiceAllowSet = getAllowSet(voiceAllowTypeStr);

        String otherAllowTypeStr = Config.INSTANCE.get("otherAllowTypes", "pdf");
        otherAllowSet = getAllowSet(otherAllowTypeStr);
    }

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
        sBuffer.append(baseName.substring(0, 4));//前4位作为文件夹名字
        sBuffer.append(SEPARATOR);
        sBuffer.append(baseName);
        sBuffer.append("." + ext);
        return sBuffer.toString();
    }

    /**
     * 获取文件
     *
     * @param basename
     * @param ext
     * @return
     */
    public static File getSrcFile(String basename, String ext) {
        String srcFileName = Constant.BASEPATH + FileUtil.getRelativePath(basename, ext);
        File file = new File(srcFileName);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        return file;
    }


    /**
     * 生成文件名（不包括文件后辍）
     *
     * @param contentMd5
     * @param ext
     * @return
     */
    public static String buildFilename(String contentMd5, String ext) {
        String key = contentMd5;
        String basename = ShortUrlUtil.shortUrl(key);
        while (getSrcFile(basename, ext).exists()) {
            basename = ShortUrlUtil.shortUrl(key);
        }
        return basename;
    }

    /**
     * 根据URI获取文件
     *
     * @param URI
     * @return
     */
    public static File getSrcFile(String URI) {
        int index = URI.lastIndexOf("/");
        String filename = URI.substring(index + 1);
        int dotIndex = filename.lastIndexOf(".");
        if (filename.length() < 4) { // 因为文件夹名字是取的basename前4位
            return null;
        }
        String basename = filename.substring(0, dotIndex);
        String ext = filename.substring(dotIndex + 1);
        return FileUtil.getSrcFile(basename, ext);
    }

    public static Map<String, Object> getFileInfo(String imgPath) {
        if (StringUtils.isBlank(imgPath)) {
            return null;
        }
        String ext = FilenameUtils.getExtension(imgPath).toLowerCase();
        Map<String, Object> infoMap = new HashMap<String, Object>();
        infoMap.put("fileExt", ext.toLowerCase());
        if (imageAllowSet.contains(ext)) {
            try {
                infoMap.put("fileType", "image");
                String size = Im4javaImgUtil.getWidthAndHeight(imgPath);
                ImageParam.Size imgSize = ImageParam.getSize(size, true, false);
                if (imgSize != null) {
                    infoMap.put("width", imgSize.getWidth());
                    infoMap.put("height", imgSize.getHeight());
                } else {
                    infoMap.put("width", 0);
                    infoMap.put("height", 0);
                }
            } catch (Exception e) {
                infoMap.put("width", 0);
                infoMap.put("height", 0);
                LOGGER.error("获取图片信息异常file=" + imgPath, e);
            }
        } else if (voiceAllowSet.contains(ext)) {
            infoMap.put("fileType", "voice");
        } else {
            infoMap.put("fileType", "other");
        }
        return infoMap;
    }

    private static Set<String> getAllowSet(String allowTypeStr) {
        HashSet<String> allowSet = new HashSet<String>();
        Iterable<String> imageIt = Splitter.on(",").omitEmptyStrings().trimResults().split(allowTypeStr);
        Iterator<String> iterator = imageIt.iterator();
        while (iterator.hasNext()) {
            String type = iterator.next();
            allowSet.add(type);
        }
        return allowSet;
    }
}

