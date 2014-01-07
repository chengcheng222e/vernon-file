package com.vernon.file;

import com.vernon.file.core.Config;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.thread.QueuedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: chenyuan
 * Date: 1/3/14
 * Time: 9:28
 * To change this template use File | Settings | File Templates.
 */
public class JettyRun {

    private static final Logger LOGGER = LoggerFactory.getLogger(JettyRun.class);
    private static final int WEB_SERVER_PORT = Config.INSTANCE.getInt("web_server_port", 80);

    public void start() throws Exception {
        Server server = new Server();
        // 线程
        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setMaxThreads(300);
        server.setThreadPool(threadPool);
        // 连接
        Connector connector = new SelectChannelConnector();
        connector.setPort(WEB_SERVER_PORT);
        server.setConnectors(new Connector[]{connector});
        //Handler
        WebAppContext context = new WebAppContext("src/main/webapp","/");
        context.setDescriptor("src/main/webapp/WEB-INF/web.xml");
        context.setResourceBase("src/main/webapp");
        context.setDisplayName("trunk");
        context.setClassLoader(Thread.currentThread().getContextClassLoader());
        context.setParentLoaderPriority(true);

        LOGGER.info(context.getContextPath());
        LOGGER.info(context.getDescriptor());
        LOGGER.info(context.getResourceBase());
        LOGGER.info(context.getBaseResource().toString());

        server.setHandler(context);
        server.start();
    }


    public static void main(String[] args) throws Exception {
        new JettyRun().start();
    }
}
