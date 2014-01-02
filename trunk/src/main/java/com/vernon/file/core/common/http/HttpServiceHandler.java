package com.vernon.file.core.common.http;


import com.vernon.file.core.AppUtil;
import com.vernon.file.core.Context;
import com.vernon.file.core.FileType;
import com.vernon.file.core.common.util.FileUtil;
import com.vernon.file.core.web.action.Action;
import com.vernon.file.core.web.action.ActionFactory;
import org.apache.commons.io.FilenameUtils;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: chenyuan
 * Date: 13-11-21
 * Time: AM11:56
 * To change this template use File | Settings | File Templates.
 */
public class HttpServiceHandler extends SimpleChannelHandler {

    private static Logger LOGGER = LoggerFactory.getLogger(HttpServiceHandler.class);
    static Charset UTF8 = Charset.forName("UTF-8");

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {

        final int operatorId = AppUtil.operatorIndex.incrementAndGet();
        LOGGER.info("{} ---------- messageReceived start ------------", operatorId);

        // ------------------------ 分析 request ------------------------
        HttpRequest httpRequest = (HttpRequest) e.getMessage();
        Channel channel = e.getChannel();

        LOGGER.info("1. {}, channelId={}, httpRequest.getUri()={}", operatorId, channel.getId(), httpRequest.getUri());
        // 获取查询条件
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(httpRequest.getUri(), UTF8);
        String tmpPath = queryStringDecoder.getPath();
        String path = URLDecoder.decode(tmpPath);
        LOGGER.debug("2. URLDecoder path={} ", path);
        // get 请求参数
        Map<String, List<String>> params = queryStringDecoder.getParameters();
        String baseName = FilenameUtils.getBaseName(path);
        String ext = FilenameUtils.getExtension(path);
        FileType fileType = FileUtil.getFileType(ext);
        Context context = new Context();
        Action cmd = ActionFactory.getAction(fileType);
        cmd.handle(context);
        LOGGER.info("ß", operatorId);
    }

    private Map<String, String> formatReqParam(Map<String, List<String>> reqParams) {
        Map<String, String> bodyParams = new HashMap<String, String>();
        if (reqParams != null) {
            Set<String> keySet = reqParams.keySet();
            for (String k : keySet) {
                List<String> v = reqParams.get(k);
                if (v != null && v.size() > 0) {
                    bodyParams.put(k, v.get(0));
                }
            }
        }
        return bodyParams;
    }

}
