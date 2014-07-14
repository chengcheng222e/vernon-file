package com.vernon.file.web.action;

import com.vernon.file.core.Constant;
import com.vernon.file.core.common.http.HttpHelper;
import com.vernon.file.core.common.util.FileUtil;
import com.vernon.file.core.common.util.Im4javaImgUtil;
import com.vernon.file.core.web.action.Action;
import com.vernon.file.core.Context;
import com.vernon.file.domain.ImageParam;
import org.apache.commons.lang.StringUtils;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: chenyuan
 * Date: 1/2/14
 * Time: 9:52
 * To change this template use File | Settings | File Templates.
 */
public class ImageAction implements Action {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageAction.class);

    private ExecutorService executor = Executors.newFixedThreadPool(4);
    private static Map<String, String> IMG_SIZE_MAP = new HashMap<String, String>();

    static {
        IMG_SIZE_MAP.put("l", "100x100");
        IMG_SIZE_MAP.put("m", "50x50");
        IMG_SIZE_MAP.put("s", "25x25");
    }

    @Override
    public void handle(Context context) {
        String baseName = context.getBaseName();
        final Channel channel = context.getChannel();
        String ext = context.getExt();
        String path = context.getPath();

        // 路径太短. 直接返回404
        if (path.length() < 4) {
            HttpHelper.sendError(channel, HttpResponseStatus.NOT_FOUND);
            return;
        }

        // 如果存在此图片就直接返回
        File file = new File(Constant.BASEPATH + path);
        if (file.exists()) {
            HttpHelper.serveStatic(channel, file);
            return;
        }


        int pos = baseName.indexOf("_");
        if (pos == -1) {
            String filePath = FileUtil.getRelativePath(baseName, ext);
            File f = new File(Constant.BASEPATH + filePath);
            if (f.exists()) {
                HttpHelper.serveStatic(channel, f);
                return;
            }
            HttpHelper.sendError(channel, HttpResponseStatus.NOT_FOUND);
        } else {
            // 图片缩略 file:m_6qIe6u.png
            // => baseName m_6qIe6u
            // => prefix = m
            // => suffix = 6qIe6u
            String prefix = baseName.substring(0, pos);
            String suffix = baseName.substring(pos + 1);
            if (IMG_SIZE_MAP.containsKey(prefix)) {
                prefix = IMG_SIZE_MAP.get(prefix);
            }
            if (isThumbnailRequest(prefix)) { // 是缩略图请求
                doThumbnailRequest(channel, ext, prefix, suffix);
            } else {
                HttpHelper.sendError(channel, HttpResponseStatus.NOT_FOUND);
            }
        }
    }

    /**
     * 执行缩略图请求
     *
     * @param channel
     * @param ext     文件扩展名
     * @param prefix
     * @param suffix
     */
    private void doThumbnailRequest(final Channel channel, String ext, String prefix, String suffix) {
        String relativePath = FileUtil.getRelativePath(suffix, ext);
        final String thumbnailPath = Constant.THUMBNAILPATH + FileUtil.SEPARATOR + prefix + relativePath;
        final File thumbnailPathFile = new File(thumbnailPath);
        if (thumbnailPathFile.exists()) {
            HttpHelper.serveStatic(channel, thumbnailPathFile);
            return;
        } else {// 生成缩略图
            thumbnailPathFile.getParentFile().mkdirs();
            final String srcPath = getSrcImgPath(suffix, ext);
            if (!StringUtils.isBlank(srcPath)) {
                buildThumbnail(channel, prefix, thumbnailPath, srcPath);
                return;
            } else { // 原图不存在
                HttpHelper.sendError(channel, HttpResponseStatus.NOT_FOUND);
                return;
            }
        }
    }

    private void buildThumbnail(final Channel channel, String prefix, final String thumbnailPath, final String srcPath) {
        final String tmpPrefix = prefix;
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (isResizeAndCropRequest(tmpPrefix)) {  //v
                        //200x200v
                        String[] v = tmpPrefix.split("[xv]");
                        ImageParam.Size imgSize = ImageParam.getSize(v[0] + "x" + v[1], true, true);
                        Im4javaImgUtil.resizeAndCrop(srcPath, thumbnailPath, imgSize.getWidth(), imgSize.getHeight());
                    } else if (isLongImgRequest(tmpPrefix)) {
                        //200x200vl
                        String[] v = tmpPrefix.split("[xv]");  //vl
                        ImageParam.Size imgSize = ImageParam.getSize(v[0] + "x" + v[1], true, true);
                        Im4javaImgUtil.zoomLongImage(srcPath, thumbnailPath, imgSize.getWidth(), imgSize.getHeight());
                    } else if (isFillRequest(tmpPrefix)) {
                        //200x200f
                        String[] v = tmpPrefix.split("[xf]");   //f
                        ImageParam.Size imgSize = ImageParam.getSize(v[0] + "x" + v[1], true, true);
                        Im4javaImgUtil.fillWhiteImage(srcPath, thumbnailPath, imgSize.getWidth(), imgSize.getHeight());
                    } else {
                        Im4javaImgUtil.zoomImage(srcPath, thumbnailPath, tmpPrefix);
                    }
                    HttpHelper.serveStatic(channel, new File(thumbnailPath));
                } catch (Exception e) {
                    LOGGER.error("生成缩略图失败", e);
                    HttpHelper.sendError(channel, HttpResponseStatus.NOT_FOUND);
                }
            }
        });
    }

    /**
     * 目前此功能主要是在原图进行缩略处理后，裁剪出一个正方形的图片
     *
     * @param prefix
     * @return
     */
    private static boolean isResizeAndCropRequest(String prefix) {
        String regEx = "^[1-9]\\d{1,4}x[1-9]\\d{1,4}v$";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(prefix);
        return m.find();
    }

    /**
     * 长图片的视图模式[vl]-view of long image
     * 例如请求200x100vl的图片，如果原图高度超过1280及高/宽之比大于3，则按视图模式200x100v处理,否则按200×100>模式处理。
     *
     * @param prefix
     * @return
     */
    private static boolean isLongImgRequest(String prefix) {
        String regEx = "^[1-9]\\d{1,4}x[1-9]\\d{1,4}vl$";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(prefix);
        return m.find();
    }

    /**
     * 图片缩略并填白
     *
     * @param prefix
     * @return
     */
    private static boolean isFillRequest(String prefix) {
        String regEx = "^[1-9]\\d{1,4}x[1-9]\\d{1,4}f$";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(prefix);
        return m.find();
    }

    /**
     * 是否是请求缩略图
     *
     * @param prefix 如m,300x200,300x200!,200x200v,x200!
     * @return
     */
    private static boolean isThumbnailRequest(String prefix) {
        String regEx = "^([1-9]\\d{1,3})(x\\d{0,4})?[!><^]?$|^x([1-9]\\d{1,3})[!><^]?$|^[1-9]\\d{1,3}x[1-9]\\d{1,3}(vl?|f)$";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(prefix);
        return m.find();
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
