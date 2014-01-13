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

import java.io.IOException;
import java.util.ArrayList;

public class Im4javaUtil {

    public final static Character RESIZE_SPECIAL_STRETCH = '!';
    public final static String EXT_GIF = "gif"; // gif文件后辍名

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
        String jpeg01 = "E:/Picture/psb.jpg";
        String jpegTmp = "E:/Picture/tmp.jpg";
        cutImage(jpeg01, jpegTmp, 0, 0, 100, 100);
//        String png01 = "/Users/xionglie/IdeaProjects/fileServer/src/test/resources/img/png/p1.png";
//        String png02 = "/Users/xionglie/IdeaProjects/fileServer/src/test/resources/img/png/p2.png";
//
//        String gif01 = "/Users/xionglie/IdeaProjects/fileServer/src/test/resources/img/jpeg/big.jpeg";
//
//        zoomImage(jpeg01, "/tmp/test/jpeg.jpeg", "50x50");
//        zoomImage(png01, "/tmp/test/png01.png", "50x50");
//        zoomImage(png02, "/tmp/test/png02.png", "50x50");
//
//        // zoomImageOld(gif01,"/tmp/test/old-jgif.jpeg","50x50");
//        zoomImage(gif01, "/tmp/test/jgif.jpeg", "50x50");
        //resizeAndCrop("/Users/xionglie/Desktop/t02.gif", "/Users/xionglie/Desktop/new.gif", 200, 200);
        //zoomLongImage("/Users/xionglie/Desktop/test001.gif", "/Users/xionglie/Desktop/t001_out.gif", 160, 120);
//        fillWhiteImage("/Users/xionglie/Desktop/test002.jpg", "/Users/xionglie/Desktop/new.jpg", 600, 600);
        //zoomLongImage("/Users/xionglie/Desktop/EBzaeu2.png", "/Users/xionglie/Desktop/EBzaeu2_1.png", 160, 120);
    }

}
