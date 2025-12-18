package com.fooddelivery.servlet;

import com.fooddelivery.dao.OrderDAO;
import com.fooddelivery.dao.RestaurantDAO;
import com.fooddelivery.dao.UserDAO;
import com.fooddelivery.dao.impl.OrderDAOImpl;
import com.fooddelivery.dao.impl.RestaurantDAOImpl;
import com.fooddelivery.dao.impl.UserDAOImpl;
import com.fooddelivery.util.DBUtil;
import com.fooddelivery.util.CurrencyUtil;

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

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    
    private UserDAO userDAO = new UserDAOImpl();
    private RestaurantDAO restaurantDAO = new RestaurantDAOImpl();
    private OrderDAO orderDAO = new OrderDAOImpl();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        // Get statistics
        int totalUsers = getTotalUsers();
        int totalRestaurants = getTotalRestaurants();
        int totalOrders = orderDAO.getTotalOrders();
        double totalRevenue = orderDAO.getTotalRevenue();
        int pendingOrders = orderDAO.getOrdersByStatus("NEW");
        int activeRestaurants = getActiveRestaurants();
        
        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Dashboard - Food Delivery</title>");
        out.println("<link rel='stylesheet' href='css/style.css'>");
        out.println("</head>");
        out.println("<body>");
        
        // Navigation
        out.println(getNavigation());
        
        // Dashboard Content
        out.println("<div class='container'>");
        out.println("<h1 class='page-title'>Dashboard</h1>");
        
        // Quick Actions
        out.println("<div class='quick-actions'>");
        out.println("<a href='restaurants' class='action-btn'><span class='action-icon'>🍽️</span><span>Restaurants</span></a>");
        out.println("<a href='menu-items' class='action-btn'><span class='action-icon'>📋</span><span>Menu Items</span></a>");
        out.println("<a href='orders' class='action-btn'><span class='action-icon'>📦</span><span>Orders</span></a>");
        out.println("<a href='users' class='action-btn'><span class='action-icon'>👥</span><span>Users</span></a>");
        out.println("</div>");
        
        // Stats Cards with Images
        out.println("<div class='stats-grid'>");
        out.println("<div class='stat-card stat-card-users'>");
        out.println("<div class='stat-image'><img src='https://images.unsplash.com/photo-1522071820081-009f0129c71c?w=150&h=150&fit=crop' alt='Users'></div>");
        out.println("<div class='stat-icon'>👥</div>");
        out.println("<div class='stat-info'>");
        out.println("<h3>" + totalUsers + "</h3>");
        out.println("<p>Total Users</p>");
        out.println("<span class='stat-change'>+12% this month</span>");
        out.println("</div></div>");
        
        out.println("<div class='stat-card stat-card-restaurants'>");
        out.println("<div class='stat-image'><img src='https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=150&h=150&fit=crop' alt='Restaurants'></div>");
        out.println("<div class='stat-icon'>🍽️</div>");
        out.println("<div class='stat-info'>");
        out.println("<h3>" + totalRestaurants + "</h3>");
        out.println("<p>Total Restaurants</p>");
        out.println("<span class='stat-change'>+5% this month</span>");
        out.println("</div></div>");
        
        out.println("<div class='stat-card stat-card-orders'>");
        out.println("<div class='stat-image'><img src='https://images.unsplash.com/photo-1607082349566-187342175e2f?w=150&h=150&fit=crop' alt='Orders'></div>");
        out.println("<div class='stat-icon'>📦</div>");
        out.println("<div class='stat-info'>");
        out.println("<h3>" + totalOrders + "</h3>");
        out.println("<p>Total Orders</p>");
        out.println("<span class='stat-change'>+23% this month</span>");
        out.println("</div></div>");
        
        out.println("<div class='stat-card stat-card-revenue'>");
        out.println("<div class='stat-image'><img src='https://images.unsplash.com/photo-1554224155-6726b3ff858f?w=150&h=150&fit=crop' alt='Revenue'></div>");
        out.println("<div class='stat-icon'>💰</div>");
        out.println("<div class='stat-info'>");
        out.println("<h3>" + CurrencyUtil.format(totalRevenue) + "</h3>");
        out.println("<p>Total Revenue</p>");
        out.println("<span class='stat-change'>+18% this month</span>");
        out.println("</div></div>");
        
        out.println("<div class='stat-card stat-card-pending'>");
        out.println("<div class='stat-image'><img src='https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=150&h=150&fit=crop' alt='Pending'></div>");
        out.println("<div class='stat-icon'>⏳</div>");
        out.println("<div class='stat-info'>");
        out.println("<h3>" + pendingOrders + "</h3>");
        out.println("<p>Pending Orders</p>");
        out.println("<span class='stat-change urgent'>Requires attention</span>");
        out.println("</div></div>");
        
        out.println("<div class='stat-card stat-card-active'>");
        out.println("<div class='stat-image'><img src='https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=150&h=150&fit=crop' alt='Active'></div>");
        out.println("<div class='stat-icon'>✅</div>");
        out.println("<div class='stat-info'>");
        out.println("<h3>" + activeRestaurants + "</h3>");
        out.println("<p>Active Restaurants</p>");
        out.println("<span class='stat-change'>All systems operational</span>");
        out.println("</div></div>");
        
        out.println("</div>"); // stats-grid
        
        // Charts Section
        out.println("<div class='charts-section'>");
        out.println("<div class='section'>");
        out.println("<h2>📊 Order Statistics</h2>");
        out.println("<div class='chart-container'>");
        out.println("<canvas id='orderChart' width='400' height='200'></canvas>");
        out.println("</div>");
        out.println("</div>");
        
        out.println("<div class='section'>");
        out.println("<h2>📈 Revenue Trend</h2>");
        out.println("<div class='chart-container'>");
        out.println("<canvas id='revenueChart' width='400' height='200'></canvas>");
        out.println("</div>");
        out.println("</div>");
        out.println("</div>");
        
        // Top Restaurants Section with Images
        out.println("<div class='section'>");
        out.println("<h2>⭐ Top Restaurants</h2>");
        out.println("<div class='top-restaurants-grid'>");
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM restaurants WHERE is_active = 1 ORDER BY rating DESC LIMIT 6")) {
            int count = 0;
            // Actual Ethiopian food photos
            String[] restaurantImages = {
                "https://images.unsplash.com/photo-1571091718767-18b5b1457add?w=300&h=200&fit=crop&q=80", // Ethiopian food platter
                "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=300&h=200&fit=crop&q=80", // Injera with stews
                "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=300&h=200&fit=crop&q=80", // Doro Wat
                "https://images.unsplash.com/photo-1565958011703-44f9829ba187?w=300&h=200&fit=crop&q=80", // Tibs
                "https://images.unsplash.com/photo-1567620905732-2d1ec7ab7445?w=300&h=200&fit=crop&q=80", // Ethiopian cuisine
                "https://images.unsplash.com/photo-1571091718767-18b5b1457add?w=300&h=200&fit=crop&q=80" // Food platter
            };
            
            while (rs.next() && count < 6) {
                out.println("<div class='top-restaurant-card'>");
                out.println("<div class='restaurant-image-wrapper'>");
                out.println("<img src='" + restaurantImages[count % restaurantImages.length] + "' alt='" + rs.getString("name") + "' class='restaurant-image'>");
                out.println("<div class='restaurant-rating'>⭐ " + String.format("%.1f", rs.getDouble("rating")) + "</div>");
                out.println("</div>");
                out.println("<div class='restaurant-info'>");
                out.println("<h3>" + rs.getString("name") + "</h3>");
                out.println("<p>" + (rs.getString("cuisine_type") != null ? rs.getString("cuisine_type") : "Various") + "</p>");
                out.println("<p class='restaurant-address'>📍 " + (rs.getString("address") != null && !rs.getString("address").isEmpty() ? rs.getString("address").substring(0, Math.min(30, rs.getString("address").length())) + "..." : "Address not available") + "</p>");
                out.println("</div>");
                out.println("</div>");
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        out.println("</div>");
        out.println("</div>");
        
        // Recent Orders Section
        out.println("<div class='section'>");
        out.println("<h2>Recent Orders</h2>");
        out.println("<div class='table-container'>");
        out.println("<table class='data-table'>");
        out.println("<thead><tr><th>ID</th><th>User ID</th><th>Restaurant ID</th><th>Total</th><th>Status</th><th>Date</th></tr></thead>");
        out.println("<tbody>");
        
        orderDAO.findAll().stream().limit(10).forEach(order -> {
            out.println("<tr>");
            out.println("<td>" + order.getId() + "</td>");
            out.println("<td>" + order.getUserId() + "</td>");
            out.println("<td>" + order.getRestaurantId() + "</td>");
            out.println("<td>" + CurrencyUtil.format(order.getTotalAmount()) + "</td>");
            out.println("<td><span class='status-badge status-" + order.getOrderStatus().toLowerCase() + "'>" + order.getOrderStatus() + "</span></td>");
            out.println("<td>" + (order.getCreatedAt() != null ? order.getCreatedAt().toString() : "N/A") + "</td>");
            out.println("</tr>");
        });
        
        out.println("</tbody></table>");
        out.println("</div>");
        out.println("</div>"); // section
        
        // Search Section
        out.println("<div class='section'>");
        out.println("<h2>🔍 Quick Search</h2>");
        out.println("<div class='search-container'>");
        out.println("<input type='text' id='searchInput' class='search-input' placeholder='Search restaurants, users, orders...'>");
        out.println("<button class='btn search-btn' onclick='performSearch()'>Search</button>");
        out.println("</div>");
        out.println("<div id='searchResults' class='search-results'></div>");
        out.println("</div>");
        
        out.println("</div>"); // container
        out.println("<script src='https://cdn.jsdelivr.net/npm/chart.js'></script>");
        out.println("<script src='js/dashboard.js'></script>");
        out.println("<script src='js/main.js'></script>");
        out.println("</body></html>");
    }
    
    private int getTotalUsers() {
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS cnt FROM users")) {
            if (rs.next()) {
                return rs.getInt("cnt");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    private int getTotalRestaurants() {
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS cnt FROM restaurants")) {
            if (rs.next()) {
                return rs.getInt("cnt");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    private int getActiveRestaurants() {
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS cnt FROM restaurants WHERE is_active = 1")) {
            if (rs.next()) {
                return rs.getInt("cnt");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    private String getNavigation() {
        return "<nav class='navbar'>" +
               "<div class='nav-container'>" +
               "<div class='nav-brand'>🍕 Food Delivery</div>" +
               "<ul class='nav-menu'>" +
               "<li><a href='index.html'>Home</a></li>" +
               "<li><a href='dashboard'>Dashboard</a></li>" +
               "<li><a href='restaurants'>Restaurants</a></li>" +
               "<li><a href='register'>Register</a></li>" +
               "<li><a href='login'>Login</a></li>" +
               "<li><a href='test-db'>Test DB</a></li>" +
               "</ul>" +
               "</div>" +
               "</nav>";
    }
}

