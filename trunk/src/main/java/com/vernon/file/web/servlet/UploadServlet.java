package com.vernon.file.web.servlet;

import com.google.common.io.Files;
import com.vernon.file.core.AppUtil;
import com.vernon.file.core.Constant;
import com.vernon.file.core.common.http.HttpHeaders;
import com.vernon.file.core.common.http.HttpHelper;
import com.vernon.file.core.common.http.HttpParams;
import com.vernon.file.core.common.util.FileUtil;
import com.vernon.file.core.common.util.Im4javaImgUtil;
import com.vernon.file.core.common.util.UUIDUtil;
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
        response.sendError(HttpServletResponse.SC_NOT_FOUND,"Not Found");
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
                    LOGGER.info("======================={}",item.getString("UTF-8"));
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
        LOGGER.debug("UUID = {} , UploadServlet  doPut() start, IP={}", requestId, IP);
        LOGGER.debug("UUID = {} , URI = {}", requestId, URI);
        String headerJson = HttpHelper.getHeaderJsonString(request);
        LOGGER.info("UUID = {}, header={}", requestId, headerJson);

        // 获取流的参数
        String contentMD5 = HttpHelper.getHeader(request, HttpParams.CONTENT_MD5);
        String contentType = HttpHelper.getHeader(request, HttpHeaders.Names.CONTENT_TYPE);
        String date = HttpHelper.getHeader(request, HttpHeaders.Names.DATE);
        String auth = HttpHelper.getHeader(request, HttpHeaders.Names.AUTHORIZATION);
        String userId = HttpHelper.getHeader(request, HttpParams.X_DZQ_UID);
        String lid = HttpHelper.getHeader(request, HttpParams.X_DZQ_LID);
        String objectType = HttpHelper.getHeader(request, HttpParams.X_DZQ_OBJTYPE);
        String cropParam = HttpHelper.getHeader(request, HttpParams.X_GMKERL_CROP);

        // TODO 校验请求合法性
        if (StringUtils.isBlank(date)) {
            LOGGER.debug("UUID = {}, Need Date Header", requestId);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Need Date Header");
            return;
        }

        // 操作
        String ext = FileUtil.getExtension(URI);
        if (StringUtils.isBlank(ext) || !FileUtil.isArrowFileType(ext)) {
            LOGGER.debug("UUID = {}, 不支持该文件类型:{}", requestId, ext);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Not Support The File Type");
            return;
        }

        String basename = FileUtil.buildFilename(contentMD5,ext);
        String filename = basename + "." + ext;

        String fileSavePath = Constant.TMPDIR + "/" + filename;
        File tmpFile = new File(fileSavePath);
        if (!tmpFile.getParentFile().exists()) {
            tmpFile.getParentFile().mkdirs();
        }

        LOGGER.debug(" ================ Save Temp File Begin ================");
        FileOutputStream fileos = null;
        BufferedOutputStream bufferedos = null;
        InputStream in = null;
        try {
            in = request.getInputStream();
            fileos = new FileOutputStream(tmpFile);
            bufferedos = new BufferedOutputStream(fileos);
            byte[] buffer = new byte[1024];
            int bytesRead = -1;
            while ((bytesRead = in.read(buffer)) != -1) {
                bufferedos.write(buffer, 0, bytesRead);
            }
            bufferedos.flush();
        } catch (FileNotFoundException e) {
            LOGGER.error("FileNotFoundException", e);
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Not Found");
            return;
        } catch (IOException e) {
            LOGGER.error("UUID = {}, 上传文件失败.", requestId);
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Upload Failed");
            return;
        } finally {
            if (in != null) {
                in.close();
            }
            if (fileos != null) {
                fileos.close();
            }
            if (bufferedos != null) {
                bufferedos.close();
            }
        }
        LOGGER.debug(" ================ Save Temp File End ================");
        try {
            LOGGER.debug("UUID = {}, start md5hex ...", requestId);
            String md5hex = AppUtil.fileMD5(tmpFile);
            response.setHeader(HttpHeaders.Names.ETAG, md5hex);
            LOGGER.debug("UUID = {}, end md5hex = {}", requestId, md5hex);
            if (!contentMD5.equals(md5hex)) {
                LOGGER.debug("{} contentMd5不正确", requestId);
                response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, "The File Is Changed");
                return;
            }
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error(requestId + "NoSuchAlgorithmException ", e);
            response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, "Upload Failed");
            return;
        }

        File srcImageFile = FileUtil.getSrcFile(basename, ext);
        if (!srcImageFile.getParentFile().exists()) {
            srcImageFile.getParentFile().mkdirs();
        }
        LOGGER.debug("UUID = {}, tmpFile= {}", requestId, tmpFile.getAbsolutePath());
        String toImgPath = srcImageFile.getAbsolutePath();
        LOGGER.debug("UUID = {}, srcImageFile={}", requestId, toImgPath);

        LOGGER.debug("UUID = {}, http HttpParams.X_GMKERL_CROP ={}", requestId, cropParam);
        boolean saveOk = false;

        if (StringUtils.isBlank(cropParam)) {
            Files.move(tmpFile, srcImageFile);
            saveOk = true;
        }
        // 裁剪
        else {
            String[] strings = cropParam.split(",");
            if (strings != null && strings.length >= 4) {
                int x = Integer.valueOf(strings[0]);
                int y = Integer.valueOf(strings[1]);
                int width = Integer.valueOf(strings[2]);
                int height = Integer.valueOf(strings[3]);
                try {
                    Im4javaImgUtil.cutImage(tmpFile.getAbsolutePath(), toImgPath, x, y, width, height);
                    saveOk = true;
                } catch (InterruptedException e) {
                    LOGGER.error("UUID={}, 图片裁剪出错. {}", requestId, e);
                } catch (IM4JavaException e) {
                    LOGGER.error("UUID={}, 图片裁剪出错. {}", requestId, e);
                }
            }
        }

        if (saveOk) {
            //TODO 记录表中
        }

    }
}
