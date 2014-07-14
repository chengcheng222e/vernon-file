package com.vernon.file.service;

import com.vernon.file.core.Context;
import com.vernon.file.core.FileType;
import com.vernon.file.core.common.util.FileUtil;
import com.vernon.file.core.web.action.Action;
import com.vernon.file.core.web.action.ActionFactory;
import org.apache.commons.io.FilenameUtils;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

public class ConnectionService extends SimpleChannelHandler {
    static Charset UTF8 = Charset.forName("UTF-8");

    private static Logger logger = LoggerFactory.getLogger(ConnectionService.class);

    /**
     * 当端口被监听之后,所有访问(eg:80)的都会进入该方法
     */
    @Override
    public void messageReceived(ChannelHandlerContext ctx, final MessageEvent me)
            throws Exception {
        logger.debug(" ---------messageReceived start---------");
        HttpRequest httpRequest = (HttpRequest) me.getMessage();
        logger.debug("###1. hr.getUri() :" + httpRequest.getUri());
        Channel channel = me.getChannel();
        // 获取查询条件
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(httpRequest.getUri(), UTF8);
        String tmpPath = queryStringDecoder.getPath();
        String path = URLDecoder.decode(tmpPath, "UTF-8");
        logger.debug("###2.URLDecoder path={} ", path);
        // get 请求参数
        Map<String, List<String>> params = queryStringDecoder.getParameters();
        String baseName = FilenameUtils.getBaseName(path);
        String ext = FilenameUtils.getExtension(path);
        FileType fileType = FileUtil.getFileType(ext);
        Context context = new Context(channel, params, baseName, ext, path);
        Action cmd = ActionFactory.getAction(fileType);
        cmd.handle(context);
        logger.debug(" ---------messageReceived end---------");

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
            throws Exception {
        e.getChannel().close();
    }

}
