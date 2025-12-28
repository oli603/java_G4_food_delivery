package com.fooddelivery;

import com.fooddelivery.util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet("/test-db")
public class TestDBServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html lang='en'>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            out.println("<title>Database Test - Food Delivery</title>");
            out.println("<link rel='stylesheet' href='css/style.css'>");
            out.println("</head>");
            out.println("<body>");
            out.println(getNavigation());
            out.println("<div class='container'>");
            out.println("<h1 class='page-title'>Database Connection Test</h1>");
            
            // Show connection info
            out.println("<div class='section'>");
            out.println("<h2>Connection Details</h2>");
            out.println("<div class='card'>");
            out.println("<p><strong>JDBC URL:</strong> " + DBUtil.getUrl() + "</p>");
            out.println("<p><strong>Username:</strong> " + DBUtil.getUsername() + "</p>");
            out.println("</div>");
            out.println("</div>");
            
            try (Connection conn = DBUtil.getConnection();
                 Statement stmt = conn.createStatement()) {
                
                out.println("<div class='alert alert-success'>");
                out.println("<h2>✅ Connected Successfully to XAMPP MySQL!</h2>");
                out.println("</div>");
                
                // Test database and tables
                ResultSet rs = stmt.executeQuery("SELECT DATABASE() AS db");
                if (rs.next()) {
                    out.println("<div class='card'>");
                    out.println("<p><strong>Current Database:</strong> " + rs.getString("db") + "</p>");
                    out.println("</div>");
                }
                rs.close();
                
                // Count users
                rs = stmt.executeQuery("SELECT COUNT(*) AS cnt FROM users");
                if (rs.next()) {
                    out.println("<div class='card'>");
                    out.println("<p><strong>Users in database:</strong> " + rs.getInt("cnt") + "</p>");
                    out.println("</div>");
                }
                rs.close();
                
                // Count restaurants
                rs = stmt.executeQuery("SELECT COUNT(*) AS cnt FROM restaurants");
                if (rs.next()) {
                    out.println("<div class='card'>");
                    out.println("<p><strong>Restaurants in database:</strong> " + rs.getInt("cnt") + "</p>");
                    out.println("</div>");
                }
                rs.close();
                
            } catch (Exception e) {
                out.println("<div class='alert alert-error'>");
                out.println("<h2>❌ Connection Failed!</h2>");
                out.println("<p><strong>Error:</strong> " + e.getMessage() + "</p>");
                out.println("<h3>XAMPP Troubleshooting:</h3>");
                out.println("<ul>");
                out.println("<li>Make sure XAMPP MySQL is running (check XAMPP Control Panel)</li>");
                out.println("<li>Default XAMPP MySQL: root user with empty password</li>");
                out.println("<li>If you set a password, update db.properties file</li>");
                out.println("<li>Check that MySQL is listening on port 3306</li>");
                out.println("</ul>");
                out.println("</div>");
                e.printStackTrace();
            }
            
            out.println("<div style='text-align:center;margin-top:2rem;'>");
            out.println("<a href='index.html' class='btn'>← Back to Home</a>");
            out.println("</div>");
            out.println("</div>");
            out.println("<script src='js/main.js'></script>");
            out.println("</body></html>");
        }    }
    
    private String getNavigation() {
        return "<nav class='navbar'>" +
               "<div class='nav-container'>" +
               "<div class='nav-brand'>🍕 Food Delivery</div>" +
               "<ul class='nav-menu'>" +
               "<li><a href='index.html'>Home</a></li>" +
               "<li><a href='restaurants'>Restaurants</a></li>" +
               "<li><a href='cart'>🛒 Cart</a></li>" +
               "<li><a href='my-orders'>My Orders</a></li>" +
               "<li class='nav-auth'><a href='register'>Register</a></li>" +
               "<li class='nav-auth'><a href='login'>Login</a></li>" +
               "</ul>" +
               "</div>" +
               "</nav>";
    }
}


