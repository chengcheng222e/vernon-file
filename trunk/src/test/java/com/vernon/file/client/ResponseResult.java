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
    private int picWidth = 0;
    private int picHeight = 0;
    private String fileExt = null;

    // --------------------------------------  SETTE / GETTER METHODS ----------------------------------

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getPicWidth() {
        return picWidth;
    }

    public void setPicWidth(int picWidth) {
        this.picWidth = picWidth;
    }

    public int getPicHeight() {
        return picHeight;
    }

    public void setPicHeight(int picHeight) {
        this.picHeight = picHeight;
    }

    public String getFileExt() {
        return fileExt;
    }

    public void setFileExt(String fileExt) {
        this.fileExt = fileExt;
    }

    @Override
    public String toString() {
        return "ResponseResult{" +
                "filename='" + filename + '\'' +
                ", picWidth=" + picWidth +
                ", picHeight=" + picHeight +
                ", fileExt='" + fileExt + '\'' +
                '}';
    }
}
