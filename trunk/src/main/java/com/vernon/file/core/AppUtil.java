package com.vernon.file.core;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.vernon.file.core.common.encrypt.MD5Encrypt;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 * User: chenyuan
 * Date: 13-11-21
 * Time: PM12:01
 * To change this template use File | Settings | File Templates.
 */
public class AppUtil {
    private static Logger logger = LoggerFactory.getLogger(AppUtil.class);
    public static final AtomicInteger operatorIndex = new AtomicInteger(); // 标号
    private final static String key = "!~@#*&^%$frxf520!";

    /**
     * 获取ip地址
     *
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 校验请求是否合法
     *
     * @param userId
     * @param lid
     * @return
     */
    public static boolean isCanRequest(Integer userId, String lid) {
        if (StringUtils.isBlank(lid)) {
            return false;
        }
        String[] lidArray = lid.split("-");
        if (lidArray.length != 2) {
            return false;
        }
        if (lidArray[1].equals(encoder(userId, lidArray[0]))) {
            return true;
        }
        return false;
    }

    /**
     * 校验请求是否合法
     *
     * @param userId
     * @param lid
     * @return
     */
    public static boolean isCanRequest(String userId, String lid) {
        return isCanRequest(Integer.parseInt(userId), lid);
    }

    /**
     * 加密
     *
     * @param userId
     * @param bootstrapStr
     * @return
     */
    public static String encoder(Integer userId, String bootstrapStr) {
        return MD5Encrypt.encoderForString(userId + bootstrapStr + key);
    }

    public static String getSignature(String method, String date,
                                      String accessKeyId, String uri, String contentType,
                                      String contentMD5, String uid) {
        StringBuffer sBuffer = new StringBuffer();
        final String split = "&";
        sBuffer.append(method + split);
        sBuffer.append(uri + split);
        sBuffer.append(date + split);
        sBuffer.append(contentType + split);
        sBuffer.append(contentMD5 + split);
        sBuffer.append(uid + split);
        sBuffer.append(AppUtil.getSecretAccessKey(accessKeyId));
        logger.debug("sign={}", sBuffer.toString());
        return MD5Encrypt.encoderForString(sBuffer.toString());
    }

    public static String getSecretAccessKey(String accessKeyId) {
        return Constant.accessKeyMap.get(accessKeyId);
    }

    public static String md5(File file) throws IOException {
        return Files.hash(file, Hashing.md5()).toString();
    }

}
