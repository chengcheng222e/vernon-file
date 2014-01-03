package com.vernon.file.action;

import com.vernon.file.core.Constant;
import com.vernon.file.core.common.util.FileUtil;
import com.vernon.file.core.web.action.Action;
import com.vernon.file.core.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: chenyuan
 * Date: 1/2/14
 * Time: 9:52
 * To change this template use File | Settings | File Templates.
 */
public class ImageAction implements Action{

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageAction.class);

    private ExecutorService executor = Executors.newFixedThreadPool(4);
    private static Map<String, String> IMG_SIZE_MAP = new HashMap<String, String>();

    static {
        IMG_SIZE_MAP.put("xl", "150x150");
        IMG_SIZE_MAP.put("l", "100x100");
        IMG_SIZE_MAP.put("m", "50x50");
        IMG_SIZE_MAP.put("s", "25x25");
    }

    @Override
    public void handle(Context context) {

    }


    /**
     * 获取原图路径
     *
     * @param suffix
     * @param ext
     * @return
     */
    private String getSrcImgPath(String suffix, String ext) {
        String relativePath = FileUtil.getRelativePath(suffix, ext);
        String srcPath = Constant.BASEPATH + relativePath;
        File srcPathFile = new File(srcPath);
        if (srcPathFile.exists()) {
            return srcPath;
        }
        return null;
    }
}
