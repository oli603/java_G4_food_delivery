package com.fooddelivery.servlet;

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

@WebServlet("/owner-orders")
public class OwnerOrdersServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Object sessionUserId = request.getSession().getAttribute("userId");
        if (sessionUserId == null) {
            response.sendRedirect("login");
            return;
        }
        int ownerUserId = (int) sessionUserId;

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>My Restaurant Orders - Food Delivery</title>");
        out.println("<link rel='stylesheet' href='css/style.css'>");
        out.println("</head>");
        out.println("<body>");
        out.println(getNavigation());

        out.println("<div class='container'>");
        out.println("<h1 class='page-title'>🏪 My Restaurant Orders</h1>");

        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT o.id, o.user_id, o.restaurant_id, o.total_amount, o.status, o.created_at, r.name AS restaurant_name " +
                    "FROM orders o JOIN restaurants r ON o.restaurant_id = r.id " +
                    "WHERE r.owner_user_id = ? ORDER BY o.created_at DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, ownerUserId);
            ResultSet rs = ps.executeQuery();

            out.println("<div class='section'>");
            out.println("<div class='table-container'>");
            out.println("<table class='data-table'>");
            out.println("<thead><tr><th>ID</th><th>Restaurant</th><th>User ID</th><th>Total</th><th>Status</th><th>Date</th></tr></thead>");
            out.println("<tbody>");

            boolean any = false;
            while (rs.next()) {
                any = true;
                out.println("<tr>");
                out.println("<td>#" + rs.getInt("id") + "</td>");
                out.println("<td>" + rs.getString("restaurant_name") + "</td>");
                out.println("<td>" + rs.getInt("user_id") + "</td>");
                out.println("<td>" + CurrencyUtil.format(rs.getDouble("total_amount")) + "</td>");
                String status = rs.getString("status");
                out.println("<td><span class='status-badge status-" + status.toLowerCase() + "'>" + status + "</span></td>");
                java.sql.Timestamp ts = rs.getTimestamp("created_at");
                String dateStr = ts != null ? ts.toString().substring(0, 16) : "N/A";
                out.println("<td>" + dateStr + "</td>");
                out.println("</tr>");
            }

            if (!any) {
                out.println("<tr><td colspan='6' style='text-align:center;padding:2rem;'>No orders found for your restaurants yet.</td></tr>");
            }

            out.println("</tbody></table>");
            out.println("</div>");
            out.println("</div>");
        } catch (Exception e) {
            out.println("<div class='section'>");
            out.println("<div class='alert alert-error'>Failed to load restaurant orders: " + e.getMessage() + "</div>");
            out.println("</div>");
            e.printStackTrace();
        }

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
               "<li><a href='cart'>🛒 Cart</a></li>" +
               "<li><a href='my-orders'>My Orders</a></li>" +
               "<li class='nav-account'>" +
               "<a href='login' class='nav-account-toggle'><span class='account-icon'>👤</span><span>Account</span></a>" +
               "<ul class='account-dropdown'>" +
               "<li><a href='login'>Login</a></li>" +
               "<li><a href='register'>Register</a></li>" +
               "<li><a href='my-orders'>My Orders</a></li>" +
               "</ul>" +
               "</li>" +
               "</ul>" +
               "</div>" +
               "</nav>";
    }
}