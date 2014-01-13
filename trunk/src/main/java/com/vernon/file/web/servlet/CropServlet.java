package com.vernon.file.web.servlet;

import com.vernon.file.core.AppUtil;
import com.vernon.file.core.Constant;
import com.vernon.file.core.common.http.HttpHeaders;
import com.vernon.file.core.common.http.HttpHelper;
import com.vernon.file.core.common.http.HttpParams;
import com.vernon.file.core.common.util.FileUtil;
import com.vernon.file.core.common.util.UUIDUtil;
import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: chenyuan
 * Date: 1/3/14
 * Time: 9:38
 * To change this template use File | Settings | File Templates.
 */
public class CropServlet extends HttpServlet{

    private static Logger LOGGER = LoggerFactory.getLogger(CropServlet.class);

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Not Found");
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String requestId = UUIDUtil.getUUID();
        String IP = AppUtil.getIpAddr(request);
        String URI = request.getRequestURI();
        LOGGER.debug("UUID = {} , CropServlet doPost() start, IP={}", requestId, IP);
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
        String objectId = HttpHelper.getHeader(request, HttpParams.X_DZQ_OBJID);
        String cropParam = HttpHelper.getHeader(request, HttpParams.X_GMKERL_CROP);
        String cropPageSizeParam = HttpHelper.getHeader(request, HttpParams.X_GMKERL_CROP_PAGE_SIZE);

        // 操作
        String ext = FileUtil.getExtension(URI);
        if (StringUtils.isBlank(ext) || !FileUtil.isArrowFileType(ext)) {
            LOGGER.debug("UUID = {}, 不支持该文件类型:{}", requestId, ext);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Not Support The File Type");
            return;
        }

        File from = FileUtil.getSrcFile(URI);
        if (from == null || !from.exists()) {
            LOGGER.debug("UUID = {}, 资源文件不存在:{}", requestId, URI);
            return;
        }

        // 生成新文件
        String basename = FileUtil.buildFilename(contentMD5,ext);
        String filename = basename + "." + ext;
        File to = FileUtil.getSrcFile(basename, ext);
        if (!to.getParentFile().exists()) {
            to.getParentFile().mkdirs();
        }
        String toImagePath = to.getAbsolutePath();
        LOGGER.debug("UUID = {},http header X_GMKERL_CROP={}", requestId, cropParam);
        LOGGER.debug("UUID = {},http header X_GMKERL_CROP_PAGE_SIZE={}", requestId, cropPageSizeParam);
        String[] strings = cropParam.split(",");
        
    }
}
