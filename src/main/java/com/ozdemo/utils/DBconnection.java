package com.ozdemo.utils;

import com.ozdemo.pojo.Configuration;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DBconnection {
    private static Logger log1 = Logger.getLogger("log-Test");
    /**
     * 线程内共享Connection，ThreadLocal通常是全局的，支持泛型
     */
    private static ThreadLocal<Connection> threadLocal = new ThreadLocal<>();

    public static Connection getCurrConnection(Configuration configuration) {
        // 获取当前线程内共享的Connection
        Connection conn = threadLocal.get();
        try {
            // 判断连接是否可用
            if (conn == null || conn.isClosed()) {
                conn = configuration.getDataSource().getConnection();
                // 创建新的Connection赋值给conn(略)
                // 保存Connection
                threadLocal.set(conn);
            }else{
                log1.info("get the db connection");
            }
        } catch (SQLException e) {
            // 异常处理
        }
        return conn;
    }

    /**
     * 关闭当前数据库连接
     */
    public static void close() {
        // 获取当前线程内共享的Connection
        Connection conn = threadLocal.get();
        try {
            // 判断是否已经关闭
            if (conn != null && !conn.isClosed()) {
                // 关闭资源
                conn.close();
                // 移除Connection
                threadLocal.remove();
                conn = null;
            }
        } catch (SQLException e) {
            // 异常处理
        }
    }
}
