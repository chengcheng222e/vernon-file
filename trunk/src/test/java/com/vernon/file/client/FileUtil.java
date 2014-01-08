package com.vernon.file.client;

import com.google.common.io.Files;
import org.apache.commons.codec.binary.Hex;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created with IntelliJ IDEA.
 * User: Vernon.Chen
 * Date: 14-1-8
 * Time: 下午5:30
 * To change this template use File | Settings | File Templates.
 */
public class FileUtil {

    /**
     * 文件
     *
     * @param srcImageFile
     * @return
     * @throws java.io.IOException
     * @throws java.security.NoSuchAlgorithmException
     */
    public static String fileMD5(File srcImageFile) throws IOException, NoSuchAlgorithmException {
        byte[] md5Bytes = Files.getDigest(srcImageFile, MessageDigest.getInstance("MD5"));
        return new String(Hex.encodeHex(md5Bytes));
    }
}
