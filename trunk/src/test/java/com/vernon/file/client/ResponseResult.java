package com.vernon.file.client;

/**
 * Created with IntelliJ IDEA.
 * User: Vernon.Chen
 * Date: 14-1-8
 * Time: 下午4:12
 * To change this template use File | Settings | File Templates.
 */
public class ResponseResult {

    // ------------------------------------------- FIELD NAMES -----------------------------------------

    private String filename = null;
    private int width = 0;
    private int height = 0;
    private String fileExt = null;
    private String fileType;

    // --------------------------------------  SETTE / GETTER METHODS ----------------------------------


    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getFileExt() {
        return fileExt;
    }

    public void setFileExt(String fileExt) {
        this.fileExt = fileExt;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    @Override
    public String toString() {
        return "ResponseResult{" +
                "filename='" + filename + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", fileExt='" + fileExt + '\'' +
                ", fileType='" + fileType + '\'' +
                '}';
    }
}
