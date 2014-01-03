package com.vernon.file;

import com.vernon.file.core.common.http.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: chenyuan
 * Date: 1/2/14
 * Time: 9:36
 * To change this template use File | Settings | File Templates.
 */
public class Main {

    // --------------------------------- field names ------------------------------
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);


    public static void main(String[] args) {
        HttpServer httpServer = new HttpServer();
        httpServer.start();
        LOGGER.info("--------------------------- httpServer start ---------------------------");


        JettyRun jettyRun = new JettyRun();
        try {
            LOGGER.info(" ----------------------- jettyRun started --------------------------- ");
            jettyRun.start();
        } catch (Exception e) {
            LOGGER.error("启动jetty失败", e);
            System.exit(1);
        }
    }
}
