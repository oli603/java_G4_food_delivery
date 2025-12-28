package com.fooddelivery.servlet;

import com.fooddelivery.dao.OrderDAO;
import com.fooddelivery.dao.impl.OrderDAOImpl;
import com.fooddelivery.model.Order;
import com.fooddelivery.util.CurrencyUtil;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/my-orders")
public class MyOrdersServlet extends HttpServlet {

    private final OrderDAO orderDAO = new OrderDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Use default user ID 1 if not logged in (simplified - no login required)
        Object sessionUserId = request.getSession().getAttribute("userId");
        int userId = 1; // Default user
        if (sessionUserId != null) {
            userId = (int) sessionUserId;
        }

        // Load restaurant names so we can display names instead of IDs
        Map<Integer, String> restaurantNames = new HashMap<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT id, name FROM restaurants");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                restaurantNames.put(rs.getInt("id"), rs.getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Order> orders = orderDAO.findByUserId(userId);

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>My Orders - Food Delivery</title>");
        out.println("<link rel='stylesheet' href='css/style.css'>");
        out.println("</head>");
        out.println("<body>");
        out.println(getNavigation());

        out.println("<div class='container'>");
        out.println("<h1 class='page-title'>🧾 My Orders</h1>");

        out.println("<div class='section'>");
        if (orders.isEmpty()) {
            out.println("<p style='text-align:center;'>You do not have any orders yet. Go to <a href='restaurants'>Restaurants</a> and order Ethiopian food.</p>");
        } else {
            out.println("<div class='table-container'>");
            out.println("<table class='data-table'>");
            out.println("<thead><tr><th>ID</th><th>Restaurant</th><th>Total</th><th>Status</th><th>Date</th></tr></thead>");
            out.println("<tbody>");
            for (Order order : orders) {
                out.println("<tr>");
                out.println("<td>#" + order.getId() + "</td>");
                String restaurantName = restaurantNames.getOrDefault(
                        order.getRestaurantId(), "#" + order.getRestaurantId());
                out.println("<td>" + restaurantName + "</td>");
                out.println("<td>" + CurrencyUtil.format(order.getTotalAmount()) + "</td>");
                out.println("<td><span class='status-badge status-" + order.getOrderStatus().toLowerCase() + "'>" + order.getOrderStatus() + "</span></td>");
                out.println("<td>" + (order.getCreatedAt() != null ? order.getCreatedAt().toString().substring(0, 16) : "N/A") + "</td>");
                out.println("</tr>");
            }
            out.println("</tbody></table>");
            out.println("</div>");
        }
        out.println("</div>");
        out.println("</div>");
        out.println("<script src='js/main.js'></script>");
        out.println("</body></html>");
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