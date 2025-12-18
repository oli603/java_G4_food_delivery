package com.fooddelivery.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBUtil {
    
    // JDBC URL for XAMPP MySQL
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/food_delivery_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";
    
    private static String url = JDBC_URL;
    private static String username = USERNAME;
    private static String password = PASSWORD;
    
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            InputStream inputStream = DBUtil.class.getClassLoader()
                    .getResourceAsStream("db.properties");
            
            if (inputStream != null) {
                Properties props = new Properties();
                props.load(inputStream);
                url = props.getProperty("db.url", JDBC_URL);
                username = props.getProperty("db.username", USERNAME);
                password = props.getProperty("db.password", PASSWORD);
                inputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private DBUtil() {}
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
    
    public static String getUrl() {
        return url;
    }
    
    public static String getUsername() {
        return username;
    }
}