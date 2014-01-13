package com.vernon.file.test;

import org.apache.commons.io.FilenameUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: chenyuan
 * Date: 1/2/14
 * Time: 12:51
 * To change this template use File | Settings | File Templates.
 */
public class TestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestCase.class);

    @Test
    public void test1() {
        String uri = "http://img.dianziq.com/img/150x150f_yYzYne.jpg";
        String baseName = FilenameUtils.getBaseName(uri);
        LOGGER.debug("baseName = {}", baseName);
    }


    @Test
    public void testByteArrayInputStream1() throws Exception {
        String str = "AAAAACCCCcCBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
        ByteArrayInputStream byteInputStream = new ByteArrayInputStream(str.getBytes());
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream(100);
        int i = 0;
        byte[] b = new byte[100];
        while ((i = byteInputStream.read(b)) != -1) {
            byteOutput.write(b, 0, i);
        }
        LOGGER.debug("=============={}", new String(byteOutput.toByteArray()));
    }

    @Test
    public void testByteArrayInputStream2() throws Exception {
        String str = "AAAAACCCCcCBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
        ByteArrayInputStream byteInputStream = new ByteArrayInputStream(str.getBytes());
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream(100);

        byteInputStream.read(new byte[5]);

        int i = 0;
        byte[] b = new byte[100];
        while ((i = byteInputStream.read(b)) != -1) {
            byteOutput.write(b, 0, i);
        }
        LOGGER.debug("=============={}", new String(byteOutput.toByteArray()));
    }

    @Test
    public void testByteArrayInputStream3() throws Exception {
        String str = "AAAAACCCCcCBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
        ByteArrayInputStream byteInputStream = new ByteArrayInputStream(str.getBytes());
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream(100);

        byteInputStream.read(new byte[5]);
        byteInputStream.reset();
        int i = 0;
        byte[] b = new byte[100];
        while ((i = byteInputStream.read(b)) != -1) {
            byteOutput.write(b, 0, i);
        }
        LOGGER.debug("=============={}", new String(byteOutput.toByteArray()));
    }

    @Test
    public void testFile1() {
        String filePath = "/Users/chenyuan/Data/resources/images/thumbnail/";
        File file = new File(filePath);

        if (!file.exists()) {
            file.mkdirs();// 生成
        } else {

        }

    }

    @Test
    public void testString1(){
        String fileType = "jpg,bmp";
        System.out.println(fileType.contains(null));
    }
}
