package com.vernon.file.core.common.util;

import com.vernon.file.core.Config;
import com.vernon.file.core.Constant;
import com.vernon.file.domain.ImageParam;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.im4java.core.IdentifyCmd;
import org.im4java.process.ArrayListOutputConsumer;
import org.im4java.process.ProcessStarter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;

public class Im4javaImgUtil {

    public final static Character RESIZE_SPECIAL_STRETCH = '!';
    public final static String EXT_GIF = "gif"; // gif文件后辍名
    private static Logger logger = LoggerFactory.getLogger(Im4javaImgUtil.class);

    static {
        ProcessStarter.setGlobalSearchPath(Constant.IM4JAVATOOPATH);
    }

    /**
     * 根据坐标裁剪图片
     *
     * @param srcPath 要裁剪图片的路径
     * @param newPath 裁剪图片后的路径
     * @param x       起始横坐标
     * @param y       起始纵坐标
     * @param width   裁剪后的图片宽度
     * @param height  裁剪后的图片高度
     * @throws org.im4java.core.IM4JavaException
     *
     * @throws InterruptedException
     * @throws java.io.IOException
     */
    public static void cutImage(String srcPath, String newPath, int x, int y, int width, int height)
            throws IOException, InterruptedException, IM4JavaException {
        if (isGifImg(newPath)) {
            cutGifImage(srcPath, newPath, x, y, width, height);
        } else {
            IMOperation op = new IMOperation();
            op.addImage(srcPath);
            op.crop(width, height, x, y);
            op.addImage(newPath);
            ConvertCmd convert = new ConvertCmd();
            convert.run(op);
        }
    }

    /**
     * 是否是gif文件
     *
     * @param path
     * @return
     */
    private static boolean isGifImg(String path) {
        String ext = FilenameUtils.getExtension(path);
        if (StringUtils.isNotBlank(ext) && ext.toLowerCase().equals(EXT_GIF)) {
            return true;
        }
        return false;
    }

    /**
     * 根据坐标裁剪gif图片
     *
     * @param srcPath 要裁剪图片的路径
     * @param newPath 裁剪图片后的路径
     * @param x       起始横坐标
     * @param y       起始纵坐标
     * @param width   裁剪后的图片宽度
     * @param height  裁剪后的图片高度
     * @throws org.im4java.core.IM4JavaException
     *
     * @throws InterruptedException
     * @throws java.io.IOException
     */
    private static void cutGifImage(String srcPath, String newPath, int x, int y, int width, int height)
            throws IOException, InterruptedException, IM4JavaException {
        // convert image1.gif -coalesce -crop 75x75+0+0 +repage image2.gif
        IMOperation op = new IMOperation();
        op.addImage(srcPath);
        op.coalesce();
        op.crop(width, height, x, y);
        op.p_repage();
        op.layers("optimize");
        op.addImage(newPath);
        ConvertCmd convert = new ConvertCmd();
        convert.run(op);
    }

    /**
     * 根据尺寸缩放图片
     *
     * @param width   缩放后的图片宽度,不能为0
     * @param height  缩放后的图片高度
     * @param srcPath 源图片路径
     * @param newPath 缩放后图片的路径
     */
    public static void zoomImage(Integer width, Integer height, String srcPath, String newPath)
            throws Exception {
        zoomImage(width, height, srcPath, newPath, null);
    }

    /**
     * 根据尺寸缩放图片
     *
     * @param width   缩放后的图片宽度,不能为0
     * @param height  缩放后的图片高度
     * @param srcPath 源图片路径
     * @param newPath 缩放后图片的路径
     * @param special
     */
    public static void zoomImage(Integer width, Integer height, String srcPath, String newPath, Character special)
            throws Exception {
        if (isGifImg(newPath)) {
            zoomGifImage(width, height, srcPath, newPath, special);
            return;
        }

        IMOperation op = new IMOperation();
        op.addImage(srcPath);
        if (height == null || height == 0) {
            op.resize(width);
        } else {
            if (special != null) {
                op.resize(width, height, special);
            } else {
                op.resize(width, height);
            }
        }
        op.addImage(newPath);
        ConvertCmd convert = new ConvertCmd();
        convert.run(op);
    }

    /**
     * 根据尺寸缩放gif图片
     *
     * @param width   缩放后的图片宽度,不能为0
     * @param height  缩放后的图片高度
     * @param srcPath 源图片路径
     * @param newPath 缩放后图片的路径
     */
    private static void zoomGifImage(Integer width, Integer height, String srcPath, String newPath, Character special)
            throws Exception {
        // convert old.gif -coalesce -thumbnail 50x50 -layers optimize new.gif
        IMOperation op = new IMOperation();
        op.addImage(srcPath);
        op.coalesce();
        if (height == null || height == 0) {
            op.resize(width);
        } else {
            if (special != null) {
                op.resize(width, height, special);
            } else {
                op.resize(width, height);
            }
        }
        op.layers("optimize");
        op.addImage(newPath);
        ConvertCmd convert = new ConvertCmd();
        convert.run(op);
    }

    /**
     * 根据resize参数缩放图片
     *
     * @param srcPath   源图片路径
     * @param newPath   缩放后图片的路径
     * @param resizeArg -resize的参数，如50%，50x50,50x等， <br/>
     *                  具体请参看http://www.imagemagick.org/Usage/resize/
     * @throws Exception
     */
    public static void zoomImage(String srcPath, String newPath, String resizeArg)
            throws Exception {
        // convert old.gif -coalesce -resize 50x50 -layers optimize new.gif
        boolean isGif = isGifImg(newPath);
        IMOperation op = new IMOperation();
        if (!isGif) {  //防止非gif后辍的gif文件生成多张
            srcPath = srcPath + "[0]";
        }
        op.addImage(srcPath);
        if (isGif) {
            op.coalesce();
        }
        op.resize();
        op.addRawArgs(resizeArg);
        if (isGif) {
            op.layers("optimize");
        }
        op.addImage(newPath);
        ConvertCmd convert = new ConvertCmd();
        convert.run(op);
    }

    /**
     * 图片信息
     *
     * @param imagePath
     * @return
     * @throws org.im4java.core.IM4JavaException
     *
     * @throws InterruptedException
     * @throws java.io.IOException
     */
    public static String showImageInfo(String imagePath)
            throws IOException, InterruptedException, IM4JavaException {
        IMOperation op = new IMOperation();
        op.format("width:%w,height:%h,path:%d%f,size:%b%[EXIF:DateTimeOriginal]");
        op.addImage(1);
        IdentifyCmd identifyCmd = new IdentifyCmd();
        ArrayListOutputConsumer output = new ArrayListOutputConsumer();
        identifyCmd.setOutputConsumer(output);
        identifyCmd.run(op, imagePath);
        ArrayList<String> cmdOutput = output.getOutput();
        return cmdOutput.get(0);
    }

    /**
     * 获得图片的宽高
     *
     * @param imagePath 文件路径
     * @return 宽高width x height
     * @throws org.im4java.core.IM4JavaException
     *
     * @throws InterruptedException
     * @throws java.io.IOException
     */
    public static String getWidthAndHeight(String imagePath)
            throws IOException, InterruptedException, IM4JavaException {
        IMOperation op = new IMOperation();
        op.format("%wx%h\n"); // 设置获取宽度参数
        op.addImage(imagePath);
        IdentifyCmd identifyCmd = new IdentifyCmd();
        ArrayListOutputConsumer output = new ArrayListOutputConsumer();
        identifyCmd.setOutputConsumer(output);
        identifyCmd.run(op);
        ArrayList<String> cmdOutput = output.getOutput();
        return cmdOutput.get(0);
    }

    /**
     * 获得图片的宽度
     *
     * @param imagePath 文件路径
     * @return 图片宽度
     * @throws org.im4java.core.IM4JavaException
     *
     * @throws InterruptedException
     * @throws java.io.IOException
     */
    public static int getWidth(String imagePath) throws IOException,
            InterruptedException, IM4JavaException {
        IMOperation op = new IMOperation();
        op.format("%w\n"); // 设置获取宽度参数
        op.addImage(imagePath);
        IdentifyCmd identifyCmd = new IdentifyCmd();
        ArrayListOutputConsumer output = new ArrayListOutputConsumer();
        identifyCmd.setOutputConsumer(output);
        identifyCmd.run(op);
        ArrayList<String> cmdOutput = output.getOutput();
        return Integer.parseInt(cmdOutput.get(0));
    }

    /**
     * 获得图片的高度
     *
     * @param imagePath 文件路径
     * @return 图片高度
     * @throws org.im4java.core.IM4JavaException
     *
     * @throws InterruptedException
     * @throws java.io.IOException
     */
    public static int getHeight(String imagePath)
            throws IOException, InterruptedException, IM4JavaException {
        IMOperation op = new IMOperation();
        op.format("%h\n"); // 设置获取高度参数
        op.addImage(imagePath);
        IdentifyCmd identifyCmd = new IdentifyCmd();
        ArrayListOutputConsumer output = new ArrayListOutputConsumer();
        identifyCmd.setOutputConsumer(output);
        identifyCmd.run(op);
        ArrayList<String> cmdOutput = output.getOutput();
        return Integer.parseInt(cmdOutput.get(0));
    }

    /**
     * 做缩略图后再裁剪,目前参数要求width==height
     * 原图大小300x400,需要裁剪的大小200x200=>原图转成200x400*(2/3)大小的图再进行裁剪。
     * 原图大小100x200,需要裁剪的大小200x200=>原图转成100x200大小的图再进行裁剪=>这里就相当于返回原图
     *
     * @param srcPath
     * @param newPath
     * @param width   crop后的图片宽度
     * @param height  crop后的图片高度
     * @throws Exception
     */
    public static void resizeAndCrop(String srcPath, String newPath, int width, int height)
            throws Exception {
        //convert old.gif -coalesce -thumbnail 250x250\>^ -layers optimize -coalesce -crop 250x250+0+0 +repage  new.gif
        boolean isGif = isGifImg(newPath);
        IMOperation op = new IMOperation();
        if (!isGif) {  //防止非gif后辍的gif文件生成多张
            srcPath = srcPath + "[0]";
        }
        op.addImage(srcPath);
        op.coalesce();
        op.resize();
        op.addRawArgs(width + "x" + height + ">^");
        op.layers("optimize");
        op.coalesce();
        op.crop(width, height, 0, 0);
        op.p_repage();
        op.addImage(newPath);
        ConvertCmd convert = new ConvertCmd();
        convert.run(op);
    }

    public static void zoomLongImage(String srcPath, String newPath, int width, int height)
            throws Exception {
        String size = getWidthAndHeight(srcPath);
        ImageParam.Size imgSize = ImageParam.getSize(size, true, true);
        if (imgSize == null) {
            return;
        }
        int imgWidth = imgSize.getWidth();
        int imgHeight = imgSize.getHeight();
        int min_height_of_long_img = Config.INSTANCE.getInt("min_height_of_long_img_", 1280);
        int min_radio_height_div_weight = Config.INSTANCE.getInt("min_radio_height_div_weight", 3);
        boolean longImg = (imgHeight > min_height_of_long_img) && (imgHeight / imgWidth > min_radio_height_div_weight);
        if (longImg) {
            resizeAndCrop(srcPath, newPath, width, height);
        } else {
            zoomImage(width, height, srcPath, newPath, '>');
        }
    }

    /**
     * 当要求的尺寸大于图片的(或拉伸后的)大小时，图片居中补白
     *
     * @throws Exception
     */
    public static void fillWhiteImage(String srcPath, String newPath, Integer width, Integer height)
            throws Exception {
        //convert t02.gif -coalesce -thumbnail 250x250\>
        // -background white -gravity center -extent 250x250 -layers optimize -coalesce +repage  new.gif
        boolean isGif = isGifImg(newPath);
        IMOperation op = new IMOperation();
        if (!isGif) {  //防止非gif后辍的gif文件生成多张
            srcPath = srcPath + "[0]";
        }

        op.addImage(srcPath);
        if (isGif) {
            op.coalesce();
        }

        op.resize(width, height, '>');
        op.background("white");
        op.gravity("center");
        op.extent(width, height);

        op.layers("optimize");
        op.addImage(newPath);
        ConvertCmd convert = new ConvertCmd();
        convert.run(op);
    }


    public static void main(String[] args) throws Exception {

        String parentPath = "/Users/chenyuan/Pictures/";
        String toParentPath = parentPath + "ImageMagickTest/";

        logger.info("=========== im4javaimg start !!! ===========");

        // 等比例
        String jpeg001 = parentPath + "100x100.jpg";
        // 宽>高
        String jpeg002 = parentPath + "98D36001B9F080914C298CEA541A0076.jpg";
        // 高>宽
        String jpeg003 = parentPath + "6007BEDC746ACB8D011C7D24F5659E6B.jpg";

        // 长图
        String longpic = parentPath + "longpic.jpg";

        String jpeg001Temp = toParentPath + "temp.jpg";
        // 裁剪静态图片
        // cutImage(jpeg001, jpeg001Temp, 0, 0, 500, 500);

        // 裁剪动态图片
        String gif001 = parentPath + "GIF/fatboy.gif";
        String gif001Temp = toParentPath + "giftemp.gif";
        // cutImage(gif001, gif001Temp, 0, 0, 50, 50);

        // 缩放图片
        // zoomImage(jpeg001, toParentPath + "zoomjpeg1.jpg", "50x50"); // 等比例
        // zoomImage(jpeg002, toParentPath + "zoomjpeg2.jpg", "200x200");// 宽>高, 以宽来算
        // zoomImage(jpeg003, toParentPath + "zoomjpeg3.jpg", "50x50"); // 高>宽, 以高来算

        //resizeAndCrop(jpeg002, toParentPath + "resize-andcrop.jpg", 200, 200);

        // 现在处理是裁剪掉
        //zoomLongImage(longpic, toParentPath + "longpic_out.gif", 160, 120);

        // 填白
        //fillWhiteImage(jpeg001, toParentPath + "white.jpg", 600, 600);

        logger.info("=========== im4javaimg end !!! ===========");
    }

}
