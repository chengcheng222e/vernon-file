package com.vernon.file.domain;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: chenyuan
 * Date: 1/7/14
 * Time: 8:59
 * To change this template use File | Settings | File Templates.
 */
public class Metadata {

    public enum Product {
        USER("user"), SHARE("share");

        String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        Product(String name) {
            this.name = name;
        }
    }

    private int id; // 主键
    private int userId;// 用户ID
    private String requestId;// 请求ID
    private String contentType;// 文件类型
    private String contentMd5;// MD5加密
    private int contentLength;// 长度
    private String userAgent;//用户代理
    private String accessKey;// 密钥
    private String filename;// 文件名
    private int picWidth;// 图片宽度
    private int picHeight;// 图片高度
    private String fileType;//后缀名
    private String ip;// 访问者IP
    private String method = "post";// 方式 POST/PUT
    private String remark;// 标记
    private Date ctime;// 创建时间
    private Product product;// 属于产品

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public String getContentMd5() {
        return contentMd5;
    }

    public void setContentMd5(String contentMd5) {
        this.contentMd5 = contentMd5;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Date getCtime() {
        return ctime;
    }

    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public int getPicHeight() {
        return picHeight;
    }

    public void setPicHeight(int picHeight) {
        this.picHeight = picHeight;
    }

    public int getPicWidth() {
        return picWidth;
    }

    public void setPicWidth(int picWidth) {
        this.picWidth = picWidth;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Metadata{" +
                "accessKey='" + accessKey + '\'' +
                ", id=" + id +
                ", userId=" + userId +
                ", requestId='" + requestId + '\'' +
                ", contentType='" + contentType + '\'' +
                ", contentMd5='" + contentMd5 + '\'' +
                ", contentLength=" + contentLength +
                ", userAgent='" + userAgent + '\'' +
                ", filename='" + filename + '\'' +
                ", picWidth=" + picWidth +
                ", picHeight=" + picHeight +
                ", fileType='" + fileType + '\'' +
                ", ip='" + ip + '\'' +
                ", method='" + method + '\'' +
                ", remark='" + remark + '\'' +
                ", ctime=" + ctime +
                ", product=" + product +
                "} " + super.toString();
    }
}
