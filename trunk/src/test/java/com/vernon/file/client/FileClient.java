package com.vernon.file.client;

import com.vernon.file.core.common.util.JsonUtil;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created with IntelliJ IDEA.
 * User: chenyuan
 * Date: 1/7/14
 * Time: 8:53
 * To change this template use File | Settings | File Templates.
 */
public class FileClient {

    // ------------------------------- field names -------------------------------
    private Logger logger = LoggerFactory.getLogger(FileClient.class);
    private static final String UTF8 = "UTF-8";
    private static final char EXTENSION_SEPARATOR = '.';
    private static final char UNIX_SEPARATOR = '/';
    private static final char WINDOWS_SEPARATOR = '\\';
    private final String VERSION = "0.1";
    private final String SEPARATOR = "/";
    private final String METHOD_PUT = "PUT";
    private final String METHOD_POST = "POST";
    public boolean debug = false;
    private int timeout = 30 * 1000;
    private String apiDomain = "localhost:8867";// 默认为自动识别接入点

    private String contentMD5 = "";
    private String contentType = "";
    private String accessKeyId = null;
    private String secretAccessKey = null;
    private String objectType = null; // enum('user','share')
    private String objectId = null;
    private String userId = "";
    private String lid = "";
    private String filename = null;
    // 图片信息的参数
    private int picWidth = 0;
    private int picHeight = 0;
    private String fileExt = null;
    private String responseText = null;


    // -------------------------- constructor methods ----------------------------

    public FileClient(String accessKeyId, String secretAccessKey) {
        this.accessKeyId = accessKeyId;
        this.secretAccessKey = secretAccessKey;
    }

    public FileClient(String apiDomain, String accessKeyId, String secretAccessKey, String userId,
                      String lid, String contentMD5, String objectType, String objectId) {
        this.apiDomain = apiDomain;
        this.contentMD5 = contentMD5;
        this.accessKeyId = accessKeyId;
        this.secretAccessKey = secretAccessKey;
        this.objectType = objectType;
        this.objectId = objectId;
        this.userId = userId;
        this.lid = lid;
    }

    // -------------------------- other methods ----------------------------

    /**
     * 上传文件
     *
     * @param filename
     * @param file
     * @return
     * @throws IOException
     */
    public boolean writeFile(String filename, File file) throws IOException {
        return writeFile(filename, file, null);
    }

    /**
     * 上传文件
     *
     * @param file
     * @return
     * @throws IOException
     */
    public boolean writeFile(File file) throws IOException {
        return writeFile(file.getName(), file, null);
    }

    /**
     * 上传文件
     *
     * @param file
     * @param params
     * @return
     * @throws IOException
     */
    public boolean writeFile(File file, Map<String, String> params) throws IOException {
        return writeFile(file.getName(), file, params);
    }

    /**
     * 上传文件
     *
     * @param filename
     * @param file
     * @param params
     * @return
     * @throws IOException
     */
    public boolean writeFile(String filename, File file, Map<String, String> params) throws IOException {
        if (isEmpty(filename)) {
            filename = file.getName();
        }
        filename = System.currentTimeMillis() + "." + getExt(filename);
        filename = formatPath(filename);

        InputStream is = null;
        OutputStream os = null;
        HttpURLConnection conn = null;
        try {
            is = new FileInputStream(file);
            URL url = new URL("http://" + apiDomain + filename);
            conn = (HttpURLConnection) url.openConnection();

            // 设置必要参数
            conn.setConnectTimeout(timeout);
            conn.setRequestMethod(METHOD_PUT);
            conn.setUseCaches(false);
            conn.setDoOutput(true);
            conn.setRequestProperty(HttpHeaders.Names.DATE, getGMTDate());
            conn.setRequestProperty(HttpHeaders.Names.AUTHORIZATION, sign(conn, filename));

            if (!isEmpty(contentMD5)) {
                conn.setRequestProperty(HttpHeaders.Names.CONTENT_MD5, contentMD5);
                contentMD5 = null;
            }
            if (!isEmpty(userId)) {
                conn.setRequestProperty(HttpParams.X_DZQ_UID, userId);
            }
            if (!isEmpty(lid)) {
                conn.setRequestProperty(HttpParams.X_DZQ_LID, lid);
            }
            if (!isEmpty(objectType)) {
                conn.setRequestProperty(HttpParams.X_DZQ_OBJTYPE, objectType);
            }
            if (!isEmpty(objectId)) {
                conn.setRequestProperty(HttpParams.X_DZQ_OBJID, objectId);
            }
            // 设置额外的参数，如图片缩略图等
            if (params != null && !params.isEmpty()) {
                for (Map.Entry<String, String> param : params.entrySet()) {
                    conn.setRequestProperty(param.getKey(), param.getValue());
                }
            }
            conn.connect();

            os = conn.getOutputStream();
            byte[] data = new byte[1024];
            int temp = -1;
            while ((temp = is.read(data)) != -1) {
                os.write(data, 0, temp);
            }
            os.flush();
            return parseText(conn);
        } catch (IOException e) {
            if (debug) {
                e.printStackTrace();
            }
            return false;
        } finally {
            if (os != null) {
                os.close();
                os = null;
            }
            if (is != null) {
                is.close();
                is = null;
            }
            if (conn != null) {
                conn.disconnect();
                conn = null;
            }
        }
    }

    /**
     * 获取扩展名
     *
     * @param fileName 文件名
     * @return String
     */
    public static String getExt(String fileName) {
        if (fileName == null) {
            return null;
        }
        if (fileName.lastIndexOf(".") < 0) {
            return null;
        }
        fileName = fileName.substring(fileName.lastIndexOf(".") + 1);
        if (fileName.indexOf("?") > 0) {
            fileName = fileName.substring(0, fileName.indexOf("?"));
        }
        return fileName.toLowerCase();
    }

    /**
     * 确保以/开始
     *
     * @param path
     * @return
     */
    private String formatPath(String path) {
        if (!isEmpty(path)) {
            path = path.trim();
            if (!path.startsWith(SEPARATOR)) {
                return SEPARATOR + path;
            }
        }
        return SEPARATOR + path;
    }

    /**
     * 判断字符串是否为空
     *
     * @param str
     * @return 是否为空
     */
    private boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * 获取 GMT 格式时间戳
     *
     * @return GMT 格式时间戳
     */
    private String getGMTDate() {
        SimpleDateFormat formater = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        formater.setTimeZone(TimeZone.getTimeZone("GMT"));
        return formater.format(new Date());
    }

    /**
     * 计算签名
     *
     * @param conn 连接
     * @param uri  请求地址
     * @return 签名字符串
     */
    private String sign(HttpURLConnection conn, String uri) {
        StringBuilder sign = new StringBuilder();
        sign.append(conn.getRequestMethod());
        sign.append("&");
        sign.append(uri);
        sign.append("&");
        sign.append(conn.getRequestProperty(HttpHeaders.Names.DATE));
        sign.append("&");
        sign.append(contentType);
        sign.append("&");
        sign.append(contentMD5);
        sign.append("&");
        sign.append(userId);
        sign.append("&");
        sign.append(secretAccessKey);
        if (debug) {
            System.out.println("sign = " + sign.toString());
        }
        return "yrxf " + accessKeyId + ":" + MD5Encrypt.encoderForString(sign.toString());
    }

    /**
     * 获得连接请求的返回数据
     *
     * @param conn
     * @return 字符串
     */
    private boolean parseText(HttpURLConnection conn) throws IOException {
        int code = conn.getResponseCode();
        if (HttpResponseStatus.OK.getCode() == code) {
            InputStream in = conn.getInputStream();
            byte[] buffer = new byte[512];
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            int count = -1;
            while ((count = in.read(buffer)) != -1) {
                outStream.write(buffer, 0, count);
            }
            this.responseText = new String(outStream.toByteArray(), "UTF-8");

            ResponseResult response = (ResponseResult) JsonUtil.json2Object(this.responseText, ResponseResult.class);
            if (response != null) {
                this.filename = response.getFilename();
                this.fileExt = response.getFileExt();
                this.picWidth = response.getWidth();
                this.picHeight = response.getHeight();
            }
            return true;
        }
        logger.info("code = {}, message={}", code, conn.getResponseMessage());
        return false;
    }

    // -------------------------- setter / getter methods ----------------------------


    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getApiDomain() {
        return apiDomain;
    }

    public void setApiDomain(String apiDomain) {
        this.apiDomain = apiDomain;
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

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public String getFileExt() {
        return fileExt;
    }

    public void setFileExt(String fileExt) {
        this.fileExt = fileExt;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getLid() {
        return lid;
    }

    public void setLid(String lid) {
        this.lid = lid;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
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

    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    public String getSecretAccessKey() {
        return secretAccessKey;
    }

    public void setSecretAccessKey(String secretAccessKey) {
        this.secretAccessKey = secretAccessKey;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
