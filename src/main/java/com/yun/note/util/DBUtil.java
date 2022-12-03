package com.yun.note.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

import static java.lang.System.in;

public class DBUtil {
    //使用JDBC操作数据库
    private static Properties properties = new Properties();

    static {
//        InputStream is = DBUtil.class.getClassLoader().getResourceAsStream("db.properties");
        try {
            //加载配置文件（输入流）
            InputStream is = DBUtil.class.getClassLoader().getResourceAsStream("db.properties");
            //通过load()方法将输入流加载到配置文件对象中(properties中)
            properties.load(is);
            // 通过配置对象的getProperty()方法得到驱动名，加载驱动
            Class.forName(properties.getProperty("jdbcName"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 得到数据库连接
     * @return
     */
    public static Connection getConnection(){
            // 得到数据库连接信息
            Connection connection = null;
            String dbUrl = properties.getProperty("dbUrl");
            String dbName = properties.getProperty("dbName");
            String dbPwd = properties.getProperty("dbPwd");
        try {
            connection = DriverManager.getConnection(dbUrl,dbName,dbPwd);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return connection;
    }

    /**
     * 关闭资源（当资源对象不为空时，关闭资源）
     * 关闭：结果集对象、预编译对象、连接对象（一般是这几个对象）
     * @param resultSet
     * @param preparedStatement
     * @param connection
     */
    public static void close(ResultSet resultSet, PreparedStatement preparedStatement,Connection connection){
        if (resultSet != null){
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (preparedStatement != null){
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }




}
