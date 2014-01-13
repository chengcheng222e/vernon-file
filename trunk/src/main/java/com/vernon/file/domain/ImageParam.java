package com.vernon.file.domain;

import org.apache.commons.lang.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: Vernon.Chen
 * Date: 14-1-10
 * Time: 上午9:45
 * To change this template use File | Settings | File Templates.
 */
public class ImageParam {

    // ---------------------------------------------- main methods -----------------------------------------------------

    /**
     * 获取页面容器的尺寸，如sizeStr输入为300x300,
     *
     * @param sizeStr
     * @param requireWeight
     * @param requireHeight
     * @return Size
     * @throws Exception
     */
    public static Size getSize(String sizeStr, boolean requireWeight, boolean requireHeight) throws Exception {
        if (StringUtils.isBlank(sizeStr)) {
            return null;
        }
        String regEx = null;
        if (requireWeight && requireHeight) {
            regEx = "^\\d{1,5}x\\d{1,5}$";
        } else if (requireWeight && !requireHeight) {
            regEx = "^\\d{1,5}x\\d{0,5}$";
        } else if (!requireWeight && requireHeight) {
            regEx = "^\\d{0,5}x\\d{1,5}$";
        }
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(sizeStr);
        if (m.find()) {
            String[] sizes = sizeStr.split("x");
            int width = 0;
            if (sizes.length >= 1 && StringUtils.isNotBlank(sizes[0])) {
                width = Integer.valueOf(sizes[0]);
            }
            int height = 0;
            if (sizes.length >= 2 && StringUtils.isNotBlank(sizes[1])) {
                height = Integer.valueOf(sizes[1]);
            }
            return new Size(width, height);
        }
        throw new Exception("size格式错误," + sizeStr);
    }

    /**
     *
     * @param inParam
     * @param pageSize
     * @param picWidth
     * @param picHeight
     * @return
     */
    public static CropParam countCropParam(CropParam inParam, Size pageSize, int picWidth, int picHeight) {
        int pageWidth = pageSize.getWidth();
        int pageHeight = pageSize.getHeight();

        if (pageHeight == 0) {
            float radio = (float) picHeight / picWidth;
            pageHeight = Math.round(pageWidth * radio);
        }

        float radioWidth = (float) picWidth / pageWidth;
        float radioHeight = (float) picHeight / pageHeight;

        int width = Math.round(inParam.getWidth() * radioWidth);
        int height = Math.round(inParam.getHeight() * radioHeight);

        int x = Math.round(inParam.getX() * radioWidth);
        int y = Math.round(inParam.getY() * radioHeight);

        return new CropParam(x, y, width, height);
    }

    // ---------------------------------- inner class ------------------------------------------

    public static class CropParam {
        int x;
        int y;
        int width;
        int height;

        public CropParam(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        @Override
        public String toString() {
            return "CropParam{" +
                    "x=" + x +
                    ", y=" + y +
                    ", width=" + width +
                    ", height=" + height +
                    '}';
        }
    }

    public static class Size {
        int width = 0;
        int height = 0;

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public Size(int width, int height) {
            this.width = width;
            this.height = height;
        }

        @Override
        public String toString() {
            return "Size{" +
                    "width=" + width +
                    ", height=" + height +
                    '}';
        }
    }
}
