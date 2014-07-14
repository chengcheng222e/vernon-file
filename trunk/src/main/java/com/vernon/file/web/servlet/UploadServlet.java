package com.vernon.file.web.servlet;

import com.google.common.io.ByteSink;
import com.google.common.io.Files;
import com.vernon.file.core.AppUtil;
import com.vernon.file.core.Constant;
import com.vernon.file.core.Int2StrOnScale62;
import com.vernon.file.core.ValidateUtil;
import com.vernon.file.core.common.http.HttpHeaders;
import com.vernon.file.core.common.http.HttpHelper;
import com.vernon.file.core.common.http.HttpParams;
import com.vernon.file.core.common.util.FileUtil;
import com.vernon.file.core.common.util.Im4javaImgUtil;
import com.vernon.file.core.common.util.UUIDUtil;
import com.vernon.file.domain.Metadata;
import net.sf.json.JSONObject;
import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.lang.StringUtils;
import org.im4java.core.IM4JavaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: chenyuan
 * Date: 1/3/14
 * Time: 9:38
 * To change this template use File | Settings | File Templates.
 */
public class UploadServlet extends HttpServlet {

    private static Logger LOGGER = LoggerFactory.getLogger(UploadServlet.class);
    private static Logger UPLOADLOGGER = LoggerFactory.getLogger("uploadLogger");
    private static final long MAX_SIZE = 1024 * 1024 * 100;//设置最大文件尺寸，这里是100MB
    private static final int SIZETHRESHOLD = 1024 * 4;//设置缓冲区大小，这里是4kb

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Not Found");
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String requestId = UUIDUtil.getUUID();

        String headerJson = HttpHelper.getHeaderJsonString(request);
        LOGGER.info("UUID={},header={}", requestId, headerJson);
        DiskFileUpload upload = new DiskFileUpload();
        upload.setSizeMax(MAX_SIZE);
        upload.setSizeThreshold(SIZETHRESHOLD);
        upload.setRepositoryPath(Constant.TMPDIR);

        try {
            List items = upload.parseRequest(request);
            Iterator iterator = items.iterator();

            FileItem fileItem = null;
            while (iterator.hasNext()) {
                FileItem item = (FileItem) iterator.next();
                LOGGER.debug("fileName={}", item.getFieldName());
                if (item.isFormField()) {//判断该表单项是否是普通类型
                    LOGGER.info("requestId={},表单参数名={},表单参数值={}", requestId, item.getFieldName(),
                            item.getString("UTF-8"));
                } else {// 文件类型
                    fileItem = item;
                    LOGGER.info("======================={}", item.getString("UTF-8"));
                }
            }
            if (fileItem == null) {
                response.sendError(400, "Bad Request");
                return;
            }
            File temp = new File(fileItem.getName());
            File file = new File(Constant.BASEPATH + FileUtil.SEPARATOR + temp.getName());
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            fileItem.write(file);
        } catch (FileUploadException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String requestId = UUIDUtil.getUUID();
        String IP = AppUtil.getIpAddr(request);
        String URI = request.getRequestURI();
        String headerJson = HttpHelper.getHeaderJsonString(request);
        LOGGER.debug("###requestId = {} , UploadServlet  doPut() start, IP={}, URI = {}, , header={}", requestId, IP,
                URI, headerJson);

        // 获取流的参数
        String contentMD5 = HttpHelper.getHeader(request, HttpParams.CONTENT_MD5);
        String contentType = HttpHelper.getHeader(request, HttpHeaders.Names.CONTENT_TYPE);
        String date = HttpHelper.getHeader(request, HttpHeaders.Names.DATE);
        String auth = HttpHelper.getHeader(request, HttpHeaders.Names.AUTHORIZATION);
        String userId = HttpHelper.getHeader(request, HttpParams.X_DZQ_UID);
        String lid = HttpHelper.getHeader(request, HttpParams.X_DZQ_LID);
        String objectType = HttpHelper.getHeader(request, HttpParams.X_DZQ_OBJTYPE);
        String objectId = HttpHelper.getHeader(request, HttpParams.X_DZQ_OBJID);

        // 校验请求合法性
        if (StringUtils.isBlank(date)) {
            LOGGER.debug("###requestId = {}, Need Date Header", requestId);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Need Date Header");
            return;
        }

        // 验证uid/lid
        if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(lid) && !AppUtil.isCanRequest(userId, lid)) {
            LOGGER.debug("###requestId = {},  userId ={}, lid={}, valid error", requestId, userId, lid);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "UserId Valid Error");
            return;
        }

        // 验证身份
        if (StringUtils.isBlank(auth)) {
            LOGGER.debug("###requestId = {}, auth is empty", requestId);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Sign Error");
            return;
        }

        // 认证时间
        String[] authDate = auth.replace(" ", ":").split(":");
        if (authDate.length < 3 || !authDate[0].equals("yrxf")) {
            LOGGER.debug("###requestId = {}, auth={}, authDate Sign error.", requestId, auth);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Sign Error");
            return;
        }

        // 验证签名
        String accessKeyId = authDate[1];
        String signature = AppUtil.getSignature("PUT", date, accessKeyId, URI, contentType, contentMD5, userId);
        if (!signature.equals(authDate[2])) {
            LOGGER.debug("###requestId = {}, 签名错误,signature={} ,header signature={}", requestId, signature, authDate[2]);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Sign error");
            return;
        }

        // 文件类型是否正确
        String ext = FileUtil.getExtension(URI);
        if (StringUtils.isBlank(ext) || !FileUtil.isArrowFileType(ext)) {
            LOGGER.debug("###requestId = {}, 不支持的文件类型 {}", requestId, ext);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad Request");
            return;
        }

        LOGGER.debug(" ================ 校验结束 ================");

        String basename = FileUtil.buildFilename(contentMD5, ext);
        String filename = basename + "." + ext;

        String fileSavePath = Constant.TMPDIR + "/" + filename;
        File tmpFile = new File(fileSavePath);
        Files.createParentDirs(tmpFile);

        LOGGER.debug("###requestId = {}, start save file ...", requestId);
        long start = System.currentTimeMillis();
        InputStream in = request.getInputStream();
        try {
            ByteSink byteSink = Files.asByteSink(tmpFile);
            byteSink.writeFrom(in);
        } catch (IOException e) {
            LOGGER.error("###requestId = {},  save file IOException:{},{} ", requestId, e, e.getStackTrace());
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "save file error");
            return;
        }
        long end = System.currentTimeMillis();
        LOGGER.debug("###requestId = {}, end save file, time(ms)={}", requestId, (end - start));


        String md5hex = AppUtil.md5(tmpFile);
        response.setHeader(HttpHeaders.Names.ETAG, md5hex);
        LOGGER.debug("###requestId = {}, fileMd5hex={},httpHeader ContentMd5={}", requestId, md5hex, contentMD5);
        if (!contentMD5.equals(md5hex)) {
            LOGGER.debug("###requestId = {}, contentMd5不正确", requestId);
            response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, "Not Acceptable");
            return;
        }

        File srcImageFile = FileUtil.getSrcFile(basename, ext);
        Files.createParentDirs(srcImageFile);

        String toImgPath = srcImageFile.getAbsolutePath();
        LOGGER.debug("###requestId = {}, tmpFile={},toImgPath={}", requestId, tmpFile.getAbsolutePath(), toImgPath);
        String cropParam = HttpHelper.getHeader(request, HttpParams.X_GMKERL_CROP);
        LOGGER.debug("###requestId = {}, http header X_GMKERL_CROP={}", requestId, cropParam);
        boolean saveOk = false;
        if (StringUtils.isBlank(cropParam)) {
            Files.move(tmpFile, srcImageFile);
            saveOk = true;
        } else {// 进行图片裁剪
            if (!ValidateUtil.isCropParams(cropParam)) {
                // 图片裁剪参数错误
                LOGGER.debug("###requestId = {}, 图片裁剪参数错误 {}", requestId, cropParam);
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Image Crop Invalid Parameters");
                return;
            }
            String[] strings = cropParam.split(",");
            if (strings.length >= 4) {
                try {
                    int x = Integer.valueOf(strings[0]);
                    int y = Integer.valueOf(strings[1]);
                    int width = Integer.valueOf(strings[2]);
                    int height = Integer.valueOf(strings[3]);
                    Im4javaImgUtil.cutImage(tmpFile.getAbsolutePath(), toImgPath, x, y, width, height);
                    saveOk = true;
                } catch (NumberFormatException e) {
                    LOGGER.error(requestId + "裁剪参数解析异常", e);
                } catch (Exception e) {
                    LOGGER.error(requestId + "裁剪异常", e);
                }
            }

        }

        if (saveOk) {
            JSONObject jsonObject = new JSONObject();
            Map<String, Object> infoMap = FileUtil.getFileInfo(toImgPath);
            int picWidth = 0;
            int picHeight = 0;
            if (infoMap != null) {
                jsonObject.putAll(infoMap);
                if (infoMap.containsKey("width")) {
                    picWidth = (Integer) infoMap.get("width");
                }
                if (infoMap.containsKey("height")) {
                    picHeight = (Integer) infoMap.get("height");
                }
            }
            if (picWidth > 0 && picHeight > 0) {
                String finalImageName = basename + Int2StrOnScale62.toStr(picWidth, picHeight);
                filename = finalImageName + "." + ext;
                File finalImageFile = FileUtil.getSrcFile(finalImageName, ext);
                LOGGER.debug("{} 图片名加上宽高,finalImageName={}", requestId, finalImageName);
                com.google.common.io.Files.move(srcImageFile, finalImageFile);
            }

            jsonObject.put("filename", filename);
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write(jsonObject.toString());
        } else {
            response.sendError(503, "save file error");
        }
        LOGGER.debug("###requestId = {}, end doPost ...", requestId);
    }

}
