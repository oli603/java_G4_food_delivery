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
            out.println("<p>No available menu items for this restaurant.</p>");
            out.println("<a href='restaurants' class='btn'>Back</a>");
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
            conn.setAutoCommit(false);

            String insertOrderSql = "INSERT INTO orders(user_id, restaurant_id, total, status) VALUES(?,?,?,?)";
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

            out.println("<div class='container'><h2>✅ Order created</h2>");
            out.println("<p>Order #" + orderId + " has been created with " + items.size() + " items. Total: " + total + "</p>");
            out.println("<a href='orders' class='btn'>View Orders</a> <a href='restaurants' class='btn'>Back to Restaurants</a>");
            out.println("</div>");

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            out.println("<p style='color:red;'>Failed to create order: " + e.getMessage() + "</p>");
            out.println("<a href='restaurants' class='btn'>Back</a>");
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException ignored) {}
            try { if (psOrder != null) psOrder.close(); } catch (SQLException ignored) {}
            try { if (psItem != null) psItem.close(); } catch (SQLException ignored) {}
            try { if (conn != null) conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
        }
    }
}
