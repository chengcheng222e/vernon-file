package com.vernon.file.servlet;

import com.vernon.file.core.Config;
import com.vernon.file.core.Constant;
import com.vernon.file.core.common.http.HttpHelper;
import com.vernon.file.core.common.util.FileUtil;
import com.vernon.file.core.common.util.UUIDUtil;
import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
        response.sendError(404);
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
                }
            }
            if (fileItem == null) {
                response.sendError(400, "Bad Request");
                return;
            }
            File temp = new File(fileItem.getName());
            File file = new File(Constant.BASEPATH + FileUtil.SEPARATOR + temp.getName());
            fileItem.write(file);
        } catch (FileUploadException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
