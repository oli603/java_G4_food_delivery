package com.fooddelivery.servlet;

import com.fooddelivery.dao.OrderDAO;
import com.fooddelivery.dao.impl.OrderDAOImpl;
import com.fooddelivery.model.Order;
import com.fooddelivery.util.CurrencyUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/orders")
public class OrderManagementServlet extends HttpServlet {
    
    private OrderDAO orderDAO = new OrderDAOImpl();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String statusFilter = request.getParameter("status");
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Order Management - Food Delivery</title>");
        out.println("<link rel='stylesheet' href='css/style.css'>");
        out.println("</head>");
        out.println("<body>");
        out.println(getNavigation());
        
        out.println("<div class='container'>");
        out.println("<h1 class='page-title'>📦 Order Management</h1>");
        
        // Order Statistics
        int totalOrders = orderDAO.getTotalOrders();
        int newOrders = orderDAO.getOrdersByStatus("NEW");
        int preparingOrders = orderDAO.getOrdersByStatus("PREPARING");
        int deliveredOrders = orderDAO.getOrdersByStatus("DELIVERED");
        double totalRevenue = orderDAO.getTotalRevenue();
        
        out.println("<div class='order-stats'>");
        out.println("<div class='order-stat-card'>");
        out.println("<h3>" + totalOrders + "</h3>");
        out.println("<p>Total Orders</p>");
        out.println("</div>");
        out.println("<div class='order-stat-card new'>");
        out.println("<h3>" + newOrders + "</h3>");
        out.println("<p>New Orders</p>");
        out.println("</div>");
        out.println("<div class='order-stat-card preparing'>");
        out.println("<h3>" + preparingOrders + "</h3>");
        out.println("<p>Preparing</p>");
        out.println("</div>");
        out.println("<div class='order-stat-card delivered'>");
        out.println("<h3>" + deliveredOrders + "</h3>");
        out.println("<p>Delivered</p>");
        out.println("</div>");
        out.println("<div class='order-stat-card revenue'>");
        out.println("<h3>" + CurrencyUtil.format(totalRevenue) + "</h3>");
        out.println("<p>Total Revenue</p>");
        out.println("</div>");
        out.println("</div>");
        
        // Filter Section
        out.println("<div class='section'>");
        out.println("<h2>Filter Orders</h2>");
        out.println("<div class='filter-buttons'>");
        out.println("<a href='orders' class='btn " + (statusFilter == null ? "btn-active" : "") + "'>All</a>");
        out.println("<a href='orders?status=NEW' class='btn " + ("NEW".equals(statusFilter) ? "btn-active" : "") + "'>New</a>");
        out.println("<a href='orders?status=PREPARING' class='btn " + ("PREPARING".equals(statusFilter) ? "btn-active" : "") + "'>Preparing</a>");
        out.println("<a href='orders?status=DELIVERED' class='btn " + ("DELIVERED".equals(statusFilter) ? "btn-active" : "") + "'>Delivered</a>");
        out.println("<a href='orders?status=CANCELLED' class='btn " + ("CANCELLED".equals(statusFilter) ? "btn-active" : "") + "'>Cancelled</a>");
        out.println("</div>");
        out.println("</div>");
        
        // Orders Table
        out.println("<div class='section'>");
        out.println("<h2>Orders List</h2>");
        out.println("<div class='table-container'>");
        out.println("<table class='data-table'>");
        out.println("<thead>");
        out.println("<tr>");
        out.println("<th>Order ID</th>");
        out.println("<th>User ID</th>");
        out.println("<th>Restaurant ID</th>");
        out.println("<th>Total Amount</th>");
        out.println("<th>Status</th>");
        out.println("<th>Date</th>");
        out.println("<th>Actions</th>");
        out.println("</tr>");
        out.println("</thead>");
        out.println("<tbody>");
        
        List<Order> orders;
        if (statusFilter != null && !statusFilter.isEmpty()) {
            orders = orderDAO.findAll().stream()
                    .filter(o -> statusFilter.equals(o.getOrderStatus()))
                    .collect(java.util.stream.Collectors.toList());
        } else {
            orders = orderDAO.findAll();
        }
        
        if (orders.isEmpty()) {
            out.println("<tr><td colspan='7' style='text-align:center;padding:2rem;'>No orders found.</td></tr>");
        } else {
            for (Order order : orders) {
                out.println("<tr>");
                out.println("<td><strong>#" + order.getId() + "</strong></td>");
                out.println("<td>" + order.getUserId() + "</td>");
                out.println("<td>" + order.getRestaurantId() + "</td>");
                out.println("<td><strong>" + CurrencyUtil.format(order.getTotalAmount()) + "</strong></td>");
                out.println("<td><span class='status-badge status-" + order.getOrderStatus().toLowerCase() + "'>" + order.getOrderStatus() + "</span></td>");
                out.println("<td>" + (order.getCreatedAt() != null ? order.getCreatedAt().toString().substring(0, 16) : "N/A") + "</td>");
                out.println("<td>");
                out.println("<a href='#' class='btn btn-sm' onclick='updateOrderStatus(" + order.getId() + ")'>Update</a>");
                out.println("</td>");
                out.println("</tr>");
            }
        }
        
        out.println("</tbody>");
        out.println("</table>");
        out.println("</div>");
        out.println("</div>");
        
        out.println("</div>");
        out.println("<script src='js/main.js'></script>");
        out.println("<script>");
        out.println("function updateOrderStatus(orderId) {");
        out.println("  const newStatus = prompt('Enter new status (NEW, PREPARING, DELIVERED, CANCELLED):');");
        out.println("  if (newStatus) {");
        out.println("    alert('Order ' + orderId + ' status updated to ' + newStatus);");
        out.println("  }");
        out.println("}");
        out.println("</script>");
        out.println("</body></html>");
    }
    
    private String getNavigation() {
        return "<nav class='navbar'>" +
               "<div class='nav-container'>" +
               "<div class='nav-brand'>🍕 Food Delivery</div>" +
               "<ul class='nav-menu'>" +
               "<li><a href='index.html'>Home</a></li>" +
               "<li><a href='dashboard'>Dashboard</a></li>" +
               "<li><a href='restaurants'>Restaurants</a></li>" +
               "<li><a href='menu-items'>Menu Items</a></li>" +
               "<li><a href='orders'>Orders</a></li>" +
               "<li><a href='register'>Register</a></li>" +
               "<li><a href='login'>Login</a></li>" +
               "</ul>" +
               "</div>" +
               "</nav>";
    }
}

