package com.vernon.file.core;

import com.vernon.file.core.common.http.HttpHeaders;
import com.vernon.file.core.common.http.HttpHelper;
import com.vernon.file.core.common.http.HttpParams;
import org.jboss.netty.channel.Channel;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: chenyuan
 * Date: 1/2/14
 * Time: 9:45
 * To change this template use File | Settings | File Templates.
 */
public class Context {

    // ----------------------------------- field names ----------------------------------------
    private Integer userId;
    private Integer operatorId;
    private String path;
    private Channel channel;
    private Map<String, List<String>> params;
    private String baseName;
    private String ext;

    private String contentMD5;
    private String contentType;
    private String date;
    private String auth;
    private String lid;
    private String objectType;
    private String objectId;

    public Context() {
    }

    public Context(int userId,
                   int operatorId,
                   String path,
                   Map<String, List<String>> params,
                   String baseName,
                   String ext,
                   Channel channel) {
        this.userId = userId;
        this.operatorId = operatorId;
        this.path = path;
        this.params = params;
        this.baseName = baseName;
        this.ext = ext;
        this.channel = channel;
    }

    public Context(Channel channel, Map<String, List<String>> params,
                   String baseName, String ext, String path) {
        super();
        this.channel = channel;
        this.params = params;
        this.baseName = baseName;
        this.ext = ext;
        this.path = path;
    }

    // ------------------------------ setter / getter methods ----------------------------------

    public Integer getOperatorId() {
        return operatorId;
    }

    public String getPath() {
        return path;
    }

    public Integer getUserId() {
        return userId;
    }

    public Channel getChannel() {
        return channel;
    }

    public String getBaseName() {
        return baseName;
    }

    public void setBaseName(String baseName) {
        this.baseName = baseName;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public Map<String, List<String>> getParams() {
        return params;
    }

    public void setParams(Map<String, List<String>> params) {
        this.params = params;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getContentMD5() {
        return contentMD5;
    }

    public void setContentMD5(String contentMD5) {
        this.contentMD5 = contentMD5;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getLid() {
        return lid;
    }

    public void setLid(String lid) {
        this.lid = lid;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    // -------------------------------------- other methods -----------------------------------

    /**
     * 获取 channelId
     *
     * @return
     */
    public int getChannelId() {
        if (channel != null) {
            return channel.getId();
        }
        return 0;
    }


    @Override
    public String toString() {
        return "Context{" +
                "userId=" + userId +
                ", operatorId=" + operatorId +
                ", path='" + path + '\'' +
                ", channel=" + channel +
                ", params=" + params +
                ", baseName='" + baseName + '\'' +
                ", ext='" + ext + '\'' +
                ", contentMD5='" + contentMD5 + '\'' +
                ", contentType='" + contentType + '\'' +
                ", date='" + date + '\'' +
                ", auth='" + auth + '\'' +
                ", lid='" + lid + '\'' +
                ", objectType='" + objectType + '\'' +
                ", objectId='" + objectId + '\'' +
                '}';
    }
}
