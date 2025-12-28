package com.fooddelivery.servlet;

import com.fooddelivery.dao.MenuItemDAO;
import com.fooddelivery.dao.impl.MenuItemDAOImpl;
import com.fooddelivery.model.MenuItem;
import com.fooddelivery.util.DBUtil;

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

@WebServlet("/create-order")
public class CreateOrderServlet extends HttpServlet {

    private final MenuItemDAO menuItemDAO = new MenuItemDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Allow GET (from redirects like cart checkout) to create the order
        // using the same logic as POST.
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String restaurantIdStr = request.getParameter("restaurantId");
        String[] itemIds = request.getParameterValues("itemId");
        Object sessionUserId = request.getSession().getAttribute("userId");
        String userIdStr = sessionUserId != null ? String.valueOf(sessionUserId) : request.getParameter("userId");

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        if (restaurantIdStr == null || itemIds == null || itemIds.length == 0) {
            out.println("<div class='container'>");
            out.println("<h2 style='color:red;'>No items selected or missing restaurant.</h2>");
            out.println("<a href='restaurants' class='btn'>Back to Restaurants</a>");
            out.println("</div>");
            return;
        }

        int restaurantId;
        int userId = 1; // fallback default if not logged in
        try {
            restaurantId = Integer.parseInt(restaurantIdStr);
            if (userIdStr != null) {
                userId = Integer.parseInt(userIdStr);
            }
        } catch (NumberFormatException e) {
            out.println("<div class='container'>");
            out.println("<h2 style='color:red;'>Invalid id parameter.</h2>");
            out.println("<a href='restaurants' class='btn'>Back</a>");
            out.println("</div>");
            return;
        }

        // Load selected items and quantities
        java.util.List<MenuItem> selectedItems = new java.util.ArrayList<>();
        java.util.Map<Integer, Integer> quantities = new java.util.HashMap<>();
        double total = 0.0;

        for (String idStr : itemIds) {
            try {
                int menuItemId = Integer.parseInt(idStr);
                MenuItem mi = menuItemDAO.findById(menuItemId);
                if (mi == null) {
                    continue;
                }
                String qtyParam = request.getParameter("qty_" + menuItemId);
                int qty = 1;
                if (qtyParam != null && !qtyParam.isEmpty()) {
                    try {
                        qty = Integer.parseInt(qtyParam);
                    } catch (NumberFormatException ignored) {
                        qty = 1;
                    }
                }
                if (qty <= 0) {
                    qty = 1;
                }

                selectedItems.add(mi);
                quantities.put(menuItemId, qty);
                total += mi.getPrice() * qty;
            } catch (NumberFormatException ignored) {
                // skip invalid id
            }
        }

        if (selectedItems.isEmpty()) {
            out.println("<div class='container'>");
            out.println("<h2 style='color:red;'>No valid menu items selected.</h2>");
            out.println("<a href='restaurants' class='btn'>Back to Restaurants</a>");
            out.println("</div>");
            return;
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
            for (MenuItem mi : selectedItems) {
                int qty = quantities.getOrDefault(mi.getId(), 1);
                psItem.setInt(1, orderId);
                psItem.setInt(2, mi.getId());
                psItem.setInt(3, qty);
                psItem.setDouble(4, mi.getPrice());
                psItem.addBatch();
            }
            psItem.executeBatch();

            conn.commit();

            out.println("<div class='container'>");
            out.println("<h2>✅ Order created successfully</h2>");
            out.println("<p>Order #" + orderId + " has been created with " + selectedItems.size() + " items. Total: " + total + "</p>");
            out.println("<a href='my-orders' class='btn'>View My Orders</a> ");
            out.println("<a href='orders' class='btn btn-secondary'>Admin Orders</a> ");
            out.println("<a href='restaurants' class='btn'>Back to Restaurants</a>");
            out.println("</div>");

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            out.println("<div class='container'>");
            out.println("<h2 style='color:red;'>Failed to create order: " + e.getMessage() + "</h2>");
            out.println("<a href='restaurants' class='btn'>Back</a>");
            out.println("</div>");
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException ignored) {}
            try { if (psOrder != null) psOrder.close(); } catch (SQLException ignored) {}
            try { if (psItem != null) psItem.close(); } catch (SQLException ignored) {}
            try { if (conn != null) { conn.setAutoCommit(true); conn.close(); } } catch (SQLException ignored) {}
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
}