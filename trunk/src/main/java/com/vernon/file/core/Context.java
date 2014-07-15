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
    private Channel channel;
    private Map<String, List<String>> params;
    private String baseName;
    private String ext;
    private String path;

    public Context(Channel channel, Map<String, List<String>> params,
                   String baseName, String ext, String path) {
        super();
        this.channel = channel;
        this.params = params;
        this.baseName = baseName;
        this.ext = ext;
        this.path = path;
    }

    public Channel getChannel() {
        return channel;
    }

    public Map<String, List<String>> getParams() {
        return params;
    }

    public String getBaseName() {
        return baseName;
    }

    public String getExt() {
        return ext;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return "Context [channel=" + channel + ", params=" + params
                + ", baseName=" + baseName + ", ext=" + ext + ", path=" + path
                + "]";
    }
}
