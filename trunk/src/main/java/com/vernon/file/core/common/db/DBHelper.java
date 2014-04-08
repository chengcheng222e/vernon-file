package com.vernon.file.core.common.db;

import com.vernon.file.core.JDBCConfig;
import com.vernon.file.domain.Metadata;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: chenyuan
 * Date: 4/8/14
 * Time: 14:19
 * To change this template use File | Settings | File Templates.
 */
public class DBHelper {

    private static Logger logger = LoggerFactory.getLogger(DBHelper.class);
    // ------------------------------- field names ---------------------------------
    private static String DRIVERCLASSNAME = JDBCConfig.INSTANCE.get("db-driverclassname");
    private static String URL = JDBCConfig.INSTANCE.get("db-url");
    private static String USERNAME = JDBCConfig.INSTANCE.get("db-username");
    private static String PASSWORD = JDBCConfig.INSTANCE.get("db-password");

    private static DataSource dataSource;
    private static ExecutorService DBPOOL = Executors.newFixedThreadPool(3);

    public DBHelper() {
    }

    // 这里如果高并发的情况下, 是存在问题的, 会创建几次 dataSource
    public static QueryRunner getQueryRunner() {
        if (DBHelper.dataSource == null) {
            // 初始化
            BasicDataSource source = new BasicDataSource();
            source.setUrl(URL);
            source.setDriverClassName(DRIVERCLASSNAME);
            source.setUsername(USERNAME);
            source.setPassword(PASSWORD);
            source.setDefaultAutoCommit(true);
            source.setMaxActive(50);
            source.setMaxIdle(5);
            source.setMaxWait(500);
            source.setMinEvictableIdleTimeMillis(3600 * 1000 * 3);// 3 hour
            source.setTimeBetweenEvictionRunsMillis(3600 * 1000);// 1 hour
            DBHelper.dataSource = source;
            logger.info("source init successful...");
        }
        return new QueryRunner(DBHelper.dataSource);
    }

    public static void add(Metadata metadata) throws SQLException {
        Metadata.Product product = metadata.getProduct();
        StringBuilder sqlSB = new StringBuilder();
        sqlSB.append("INSERT INTO metadata (");
        sqlSB.append("  user_id,");
        sqlSB.append("  request_id,");
        sqlSB.append("  contenttype,");
        sqlSB.append("  contentmd5,");
        sqlSB.append("  contentlength,");
        sqlSB.append("  useragent,");
        sqlSB.append("  accesskey,");
        sqlSB.append("  filename,");
        sqlSB.append("  pic_width,");
        sqlSB.append("  pic_height,");
        sqlSB.append("  file_type,");
        sqlSB.append("  ip,");
        sqlSB.append("  method,");
        sqlSB.append("  remark,");
        sqlSB.append("  product");
        sqlSB.append(")");
        sqlSB.append("VALUES");
        sqlSB.append("  (");
        sqlSB.append("    ?,");
        sqlSB.append("    ?,");
        sqlSB.append("    ?,");
        sqlSB.append("    ?,");
        sqlSB.append("    ?,");
        sqlSB.append("    ?,");
        sqlSB.append("    ?,");
        sqlSB.append("    ?,");
        sqlSB.append("    ?,");
        sqlSB.append("    ?,");
        sqlSB.append("    ?,");
        sqlSB.append("    ?,");
        sqlSB.append("    ?,");
        sqlSB.append("    ?,");
        sqlSB.append("    ?");
        sqlSB.append("  )");
        // ===
        QueryRunner queryRunner = getQueryRunner();
        String productName = null;
        if (product != null) {
            productName = product.getName();
        }
        queryRunner.update(sqlSB.toString(),
                metadata.getUserId(),
                metadata.getRequestId(),
                metadata.getContentType(),
                metadata.getContentMd5(),
                metadata.getContentLength(),
                metadata.getUserAgent(),
                metadata.getAccessKey(),
                metadata.getFilename(),
                metadata.getPicWidth(),
                metadata.getPicHeight(),
                metadata.getFileType(),
                metadata.getIp(),
                metadata.getMethod(),
                metadata.getRemark(),
                productName
        );
    }

    public static void saveRun(final Metadata metadata) {
        DBPOOL.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    DBHelper.add(metadata);
                    logger.info("save metadata successful, requestId={}", metadata.getRequestId());
                } catch (SQLException e) {
                    logger.error("save metadata failed, metadata={}, e={}", metadata.toString(), e);
                }
            }
        });
    }

    public static void main(String[] args) {
        Metadata metadata = new Metadata();
        metadata.setUserId(1);
        metadata.setRequestId("" + System.currentTimeMillis());
        metadata.setContentType("");
        metadata.setContentMd5("12323");
        metadata.setContentLength(123);
        metadata.setUserAgent("ua");
        metadata.setAccessKey("11");
        metadata.setFilename("1.jpg");
        metadata.setPicWidth(100);
        metadata.setPicHeight(120);
        metadata.setFileType("jpg");
        metadata.setIp("127.0.0.1");
        metadata.setMethod("post");
        metadata.setProduct(Metadata.Product.USER);
        DBHelper.saveRun(metadata);
    }
}
