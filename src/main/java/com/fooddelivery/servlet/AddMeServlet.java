package com.fooddelivery.servlet;

import com.fooddelivery.model.MenuItem;
import com.fooddelivery.util.DBUtil;
import com.fooddelivery.dao.MenuItemDAO;
import com.fooddelivery.dao.impl.MenuItemDAOImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/addme")
public class AddMeServlet extends HttpServlet {

    private MenuItemDAO menuItemDAO = new MenuItemDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String restaurantIdStr = request.getParameter("restaurantId");
        Object sessionUserId = request.getSession().getAttribute("userId");
        String userIdStr = sessionUserId != null ? String.valueOf(sessionUserId) : request.getParameter("userId");

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        if (restaurantIdStr == null) {
            out.println("<p style='color:red;'>Missing restaurantId parameter.</p>");
            return;
        }

        int restaurantId;
        int userId = 1; // fallback default for legacy calls
        try {
            restaurantId = Integer.parseInt(restaurantIdStr);
            if (userIdStr != null) {
                userId = Integer.parseInt(userIdStr);
            }
        } catch (NumberFormatException e) {
            out.println("<p style='color:red;'>Invalid id parameter.</p>");
            return;
        }

        List<MenuItem> items = menuItemDAO.findByRestaurantId(restaurantId);
        if (items.isEmpty()) {
            out.println("<!DOCTYPE html>");
            out.println("<html lang='en'>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            out.println("<title>No Items - Food Delivery</title>");
            out.println("<link rel='stylesheet' href='css/style.css'>");
            out.println("</head>");
            out.println("<body>");
            out.println(getNavigation());
            out.println("<div class='container'>");
            out.println("<div class='section' style='text-align: center; padding: 3rem;'>");
            out.println("<h2>No Menu Items Available</h2>");
            out.println("<p style='color: #f0f0f0; margin-bottom: 2rem;'>This restaurant doesn't have any menu items yet.</p>");
            out.println("<a href='restaurants' class='btn'>Back to Restaurants</a>");
            out.println("</div>");
            out.println("</div>");
            out.println("</body></html>");
            return;
        }

        double total = 0.0;
        for (MenuItem mi : items) {
            total += mi.getPrice();
        }

        Connection conn = null;
        PreparedStatement psOrder = null;
        PreparedStatement psItem = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            
            // Ensure orders table exists
            ensureOrdersTableExists(conn);
            
            conn.setAutoCommit(false);

            // Match orders table schema: use total_amount column
            String insertOrderSql = "INSERT INTO orders(user_id, restaurant_id, total_amount, status) VALUES(?,?,?,?)";
            psOrder = conn.prepareStatement(insertOrderSql, PreparedStatement.RETURN_GENERATED_KEYS);
            psOrder.setInt(1, userId);
            psOrder.setInt(2, restaurantId);
            psOrder.setDouble(3, total);
            psOrder.setString(4, "NEW");
            int affected = psOrder.executeUpdate();
            if (affected == 0) throw new SQLException("Creating order failed, no rows affected.");

            rs = psOrder.getGeneratedKeys();
            int orderId = -1;
            if (rs.next()) {
                orderId = rs.getInt(1);
            } else {
                throw new SQLException("Creating order failed, no ID obtained.");
            }

            String insertItemSql = "INSERT INTO order_items(order_id, menu_item_id, quantity, unit_price) VALUES(?,?,?,?)";
            psItem = conn.prepareStatement(insertItemSql);
            for (MenuItem mi : items) {
                psItem.setInt(1, orderId);
                psItem.setInt(2, mi.getId());
                psItem.setInt(3, 1);
                psItem.setDouble(4, mi.getPrice());
                psItem.addBatch();
            }
            psItem.executeBatch();

            conn.commit();

            // Enhanced UI for order confirmation
            out.println("<!DOCTYPE html>");
            out.println("<html lang='en'>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            out.println("<title>Order Confirmed - Food Delivery</title>");
            out.println("<link rel='stylesheet' href='css/style.css'>");
            out.println("</head>");
            out.println("<body>");
            out.println(getNavigation());
            out.println("<div class='container'>");
            out.println("<div class='section' style='text-align: center; padding: 3rem;'>");
            out.println("<div style='font-size: 4rem; margin-bottom: 1rem;'>✅</div>");
            out.println("<h1 style='color: var(--primary); margin-bottom: 1rem;'>Order Created Successfully!</h1>");
            out.println("<div style='background: var(--card-bg); padding: 2rem; border-radius: 12px; margin: 2rem auto; max-width: 500px;'>");
            out.println("<p style='font-size: 1.2rem; margin-bottom: 1rem;'><strong>Order #" + orderId + "</strong></p>");
            out.println("<p style='margin-bottom: 0.5rem;'>📦 <strong>" + items.size() + " items</strong> added to your order</p>");
            out.println("<p style='font-size: 1.5rem; color: var(--primary); margin-top: 1rem;'><strong>Total: " + com.fooddelivery.util.CurrencyUtil.format(total) + "</strong></p>");
            out.println("</div>");
            out.println("<div class='quick-actions' style='justify-content: center; margin-top: 2rem;'>");
            out.println("<a href='my-orders' class='action-btn'><span class='action-icon'>📋</span><span>View My Orders</span></a>");
            out.println("<a href='restaurants' class='action-btn'><span class='action-icon'>🍽️</span><span>Browse More Restaurants</span></a>");
            out.println("</div>");
            out.println("</div>");
            out.println("</div>");
            out.println("<script src='js/main.js'></script>");
            out.println("</body></html>");

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            out.println("<!DOCTYPE html>");
            out.println("<html lang='en'>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            out.println("<title>Order Error - Food Delivery</title>");
            out.println("<link rel='stylesheet' href='css/style.css'>");
            out.println("</head>");
            out.println("<body>");
            out.println(getNavigation());
            out.println("<div class='container'>");
            out.println("<div class='section' style='text-align: center; padding: 3rem;'>");
            out.println("<div style='font-size: 4rem; margin-bottom: 1rem;'>❌</div>");
            out.println("<h1 style='color: #ff4444; margin-bottom: 1rem;'>Order Failed</h1>");
            out.println("<p style='color: #f0f0f0; margin-bottom: 2rem;'>" + e.getMessage() + "</p>");
            out.println("<a href='restaurants' class='btn'>Back to Restaurants</a>");
            out.println("</div>");
            out.println("</div>");
            out.println("</body></html>");
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException ignored) {}
            try { if (psOrder != null) psOrder.close(); } catch (SQLException ignored) {}
            try { if (psItem != null) psItem.close(); } catch (SQLException ignored) {}
            try { if (conn != null) conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
        }
    }
    
    private void ensureOrdersTableExists(Connection conn) throws SQLException {
        try (java.sql.Statement stmt = conn.createStatement()) {
            // Create orders table if it doesn't exist (IF NOT EXISTS handles it safely)
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS orders (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "user_id INT DEFAULT NULL, " +
                "restaurant_id INT DEFAULT NULL, " +
                "delivery_address_id INT DEFAULT NULL, " +
                "one_time_address TEXT, " +
                "total_amount DECIMAL(10,2) NOT NULL CHECK (total_amount >= 0), " +
                "status ENUM('NEW', 'CONFIRMED', 'PREPARING', 'OUT_FOR_DELIVERY', 'DELIVERED', 'CANCELLED') DEFAULT 'NEW', " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL, " +
                "FOREIGN KEY (restaurant_id) REFERENCES restaurants(id) ON DELETE SET NULL" +
                ") ENGINE=InnoDB"
            );
            
            // Create order_items table if it doesn't exist
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS order_items (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "order_id INT NOT NULL, " +
                "menu_item_id INT DEFAULT NULL, " +
                "quantity INT NOT NULL CHECK (quantity > 0), " +
                "unit_price DECIMAL(10,2) NOT NULL CHECK (unit_price >= 0), " +
                "subtotal DECIMAL(10,2) GENERATED ALWAYS AS (quantity * unit_price) STORED, " +
                "FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE, " +
                "FOREIGN KEY (menu_item_id) REFERENCES menu_items(id) ON DELETE SET NULL" +
                ") ENGINE=InnoDB"
            );
        }
    }
    
    private String getNavigation() {
        return "<nav class='navbar'>" +
               "<div class='nav-container'>" +
               "<div class='nav-brand'>🍕 Food Delivery</div>" +
               "<ul class='nav-menu'>" +
               "<li><a href='index.html'>Home</a></li>" +
               "<li><a href='restaurants'>Restaurants</a></li>" +
               "<li><a href='cart'>Cart</a></li>" +
               "<li><a href='my-orders'>My Orders</a></li>" +
               "<li><a href='register'>Register</a></li>" +
               "<li><a href='login'>Login</a></li>" +
               "</ul>" +
               "</div>" +
               "</nav>";
    }
}
