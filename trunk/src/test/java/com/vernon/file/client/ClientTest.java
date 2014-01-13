package com.vernon.file.client;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Vernon.Chen
 * Date: 14-1-8
 * Time: 下午4:49
 * To change this template use File | Settings | File Templates.
 */
public class ClientTest {

    private static final String apiDomain = "localhost:8867/upload";
    protected static final String accessKeyId = "lodawd98232";
    protected static final String secretAccessKey = "2313sd32131231321";
    private static final String SAMPLE_PIC_FILE = "E:/Picture/psb.jpg";
    private FileClient dzqClient = null;

    static {
        File picFile = new File(SAMPLE_PIC_FILE);
        if (!picFile.isFile()) {
            System.out.println("本地待上传的测试文件不存在!");
        }
    }

    private void initFileClient() throws Exception {
        dzqClient = new FileClient(accessKeyId, secretAccessKey);
        dzqClient.setDebug(true);
        dzqClient.setApiDomain(apiDomain);
    }

    /**
     * 上传文件
     *
     * @throws Exception
     */
    public void testWriteFile() throws Exception {
        initFileClient();
        File file = new File(SAMPLE_PIC_FILE);
        dzqClient.setContentMD5(FileUtil.fileMD5(file));
        dzqClient.setUserId("255511");
        dzqClient.setObjectType("user");
        dzqClient.setObjectId("25551");
        Map<String, String> params = new HashMap<String, String>();
        boolean isOk = dzqClient.writeFile(file);
        if (isOk) {
            System.out.println("上传服务器返回文件名：" + dzqClient.getFilename());
            System.out.println("上传服务器返回json：" + dzqClient.getResponseText());
        }
        System.out.println("上传 = " + isSuccess(isOk));
    }

    private static String isSuccess(boolean result) {
        return result ? " 成功" : " 失败";
    }

    public static void main(String[] args) throws Exception {
        ClientTest test = new ClientTest();
        test.testWriteFile();
    }
}
