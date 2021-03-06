package com.vernon.file.core.common.http;

import com.vernon.file.core.AppUtil;
import com.vernon.file.core.common.util.JsonUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: chenyuan
 * Date: 1/2/14
 * Time: 14:14
 * To change this template use File | Settings | File Templates.
 */
public class HttpHelper {

    private static Logger LOGGER = LoggerFactory.getLogger(HttpHelper.class);
    private static final Charset UTF8 = Charset.forName("UTF-8");
    private static final String FORMAT_GMT_DATE = "EEE, d MMM yyyy HH:mm:ss 'GMT'";
    private static final Map<String, String> contentTypes = new HashMap<String, String>();

    static {
        contentTypes.put("png", "image/png");
        contentTypes.put("jpg", "image/jpeg");
        contentTypes.put("jpeg", "image/jpeg");
        contentTypes.put("gif", "image/gif");
        contentTypes.put("bmp", "application/x-bmp");
    }

    /**
     * 发送
     *
     * @param channel
     * @param message
     */
    public static void sendOK(Channel channel, String message) {
        DefaultHttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK);
        response.setHeader(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8");
        response.setHeader(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.CLOSE);
        response.setHeader(HttpHeaders.Names.DATE, getGMTDate());
        ChannelBuffer buffer = ChannelBuffers.wrappedBuffer(message.getBytes(UTF8));
        response.setContent(buffer);
        response.setHeader(HttpHeaders.Names.CONTENT_LENGTH, buffer.readableBytes());
        channel.write(response).addListener(ChannelFutureListener.CLOSE);
        LOGGER.info("Response code = " + HttpResponseStatus.OK.getCode());
    }

    /**
     *
     * 没有发现文件
     *
     * @param channel
     * @param httpResponseStatus
     */
    public static void sendError(Channel channel, HttpResponseStatus httpResponseStatus) {
        DefaultHttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, httpResponseStatus);
        response.setHeader(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8");
        response.setHeader(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.CLOSE);
        response.setHeader(HttpHeaders.Names.DATE, getGMTDate());
        ChannelBuffer buffer = ChannelBuffers.wrappedBuffer("file not found.".getBytes(UTF8));
        response.setContent(buffer);
        response.setHeader(HttpHeaders.Names.CONTENT_LENGTH, buffer.readableBytes());
        channel.write(response).addListener(ChannelFutureListener.CLOSE);
        LOGGER.info("Response code = " + httpResponseStatus.getCode());
    }

    /**
     * 服务端不可用
     *
     * @param channel
     * @param message
     */
    public static void sendError(Channel channel, String message) {
        DefaultHttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.SERVICE_UNAVAILABLE);
        response.setHeader(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8");
        response.setHeader(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.CLOSE);
        response.setHeader(HttpHeaders.Names.DATE, getGMTDate());
        ChannelBuffer buffer = ChannelBuffers.wrappedBuffer(message.getBytes(UTF8));
        response.setContent(buffer);
        response.setHeader(HttpHeaders.Names.CONTENT_LENGTH, buffer.readableBytes());
        channel.write(response).addListener(ChannelFutureListener.CLOSE);
        LOGGER.info("Response code = " + HttpResponseStatus.SERVICE_UNAVAILABLE.getCode());
    }

    /**
     * 获取时间
     *
     * @return
     */
    private static String getGMTDate() {
        SimpleDateFormat formater = new SimpleDateFormat(FORMAT_GMT_DATE, Locale.US);
        formater.setTimeZone(TimeZone.getTimeZone("GMT"));
        return formater.format(new Date());
    }

    /**
     * 获取请求的ContentType
     *
     * @param file
     * @return
     */
    private static String getContentType(File file) {
        String ext = FilenameUtils.getExtension(file.getName()).toLowerCase();
        if (contentTypes.containsKey(ext)) {
            return contentTypes.get(ext);
        }
        return "application/octet-stream";
    }

    /**
     * 请求头信息转换成JSON格式
     *
     * @param request
     * @return
     */
    public static String getHeaderJsonString(HttpServletRequest request) {
        Enumeration<String> entry = request.getHeaderNames();
        Map<String, String> m = new HashMap<String, String>();
        while (entry.hasMoreElements()) {
            String key = entry.nextElement();
            m.put(key, request.getHeader(key));
        }
        m.put("fromIp", AppUtil.getIpAddr(request));
        return JsonUtil.toJson(m);
    }

    /**
     * 根据key获取头信息的值
     *
     * @param request
     * @param name
     * @param defaultValue
     * @return
     */
    public static String getHeader(HttpServletRequest request, String name, String defaultValue) {
        String value = request.getHeader(name);
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        return value;
    }

    /**
     * 根据key获取头信息的值
     *
     * @param request
     * @param name
     * @return
     */
    public static String getHeader(HttpServletRequest request, String name) {
        return getHeader(request, name, "");
    }

    /**
     * 获取请求Body
     *
     * @param request
     * @return
     * @deprecated
     */
    public static String getBodyString(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        InputStream inputStream = null;
        BufferedInputStream bufferedInput = null;
        try {
            inputStream = request.getInputStream();
            bufferedInput = new BufferedInputStream(inputStream);
            byte[] buffer = new byte[1024];
            int bytesRead = -1;
            while ((bytesRead = bufferedInput.read(buffer)) != -1) {
                String chunk = new String(buffer, 0, bytesRead);
                sb.append(chunk);

            }
            // 因为只能读一次,读了就没有了,为了后面的还能用,我们要调用reset();
            //bufferedInput.reset();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedInput != null) {
                try {
                    bufferedInput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    public static void serveStatic(Channel ch, File file) {
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "r"); // 只读
            DefaultHttpResponse resp = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            resp.setHeader(HttpHeaders.Names.CONTENT_TYPE, getContentType(file));
            long fileLength = raf.length();
            resp.setHeader(HttpHeaders.Names.CONTENT_LENGTH, fileLength);
            ch.write(resp);
            final FileRegion region = new DefaultFileRegion(raf.getChannel(),
                    0, fileLength);
            ch.write(region).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future)
                        throws Exception {
                    region.releaseExternalResources();
                    future.getChannel().close();
                }
            });
        } catch (Exception e) {
            LOGGER.error("serveStatic", e);
            if (ch.isConnected()) {
                sendError(ch, HttpResponseStatus.NOT_FOUND);
            }
        }
    }

}
