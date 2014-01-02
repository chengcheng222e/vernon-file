package com.vernon.file.core;

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

    public Context(){}

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
                "baseName='" + baseName + '\'' +
                ", userId=" + userId +
                ", operatorId=" + operatorId +
                ", path='" + path + '\'' +
                ", channel=" + channel +
                ", params=" + params +
                ", ext='" + ext + '\'' +
                "} " + super.toString();
    }
}
