package com.vernon.file.core.common.util;

import com.vernon.file.client.ResponseResult;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: chenyuan
 * Date: 7/14/14
 * Time: 20:10
 * To change this template use File | Settings | File Templates.
 */
public class JsonUtilTest {

    private Logger logger = LoggerFactory.getLogger(JsonUtilTest.class);

    @Test
    public void testGetObjectMapper() throws Exception {

    }

    @Test
    public void testJson2GenericObject() throws Exception {

    }

    @Test
    public void testToJson() throws Exception {

    }

    @Test
    public void testJson2Object() throws Exception {
        String json = "{\"fileType\":\"image\",\"height\":800,\"width\":449,\"filename\":\"nAv6fqAHPAM4.jpg\",\"fileExt\":\"jpg\"}";
        ResponseResult response = (ResponseResult) JsonUtil.json2Object(json, ResponseResult.class);
        logger.info("response={}", JsonUtil.toJson(response));
    }

    @Test
    public void testJson2Map() throws Exception {

    }
}
