package com.fooddelivery.servlet;

import com.fooddelivery.dao.MenuItemDAO;
import com.fooddelivery.dao.RestaurantDAO;
import com.fooddelivery.dao.impl.MenuItemDAOImpl;
import com.fooddelivery.dao.impl.RestaurantDAOImpl;
import com.fooddelivery.model.MenuItem;
import com.fooddelivery.model.Restaurant;
import com.fooddelivery.util.CurrencyUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/cart")
public class CartServlet extends HttpServlet {
    
    private MenuItemDAO menuItemDAO = new MenuItemDAOImpl();
    private RestaurantDAO restaurantDAO = new RestaurantDAOImpl();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        showCart(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if ("add".equals(action)) {
            addToCart(request, response);
        } else if ("remove".equals(action)) {
            removeFromCart(request, response);
        } else if ("update".equals(action)) {
            updateCart(request, response);
        } else if ("checkout".equals(action)) {
            checkout(request, response);
        } else {
            showCart(request, response);
        }
    }
    
    private void addToCart(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String itemIdStr = request.getParameter("itemId");
        String qtyStr = request.getParameter("quantity");
        
        if (itemIdStr == null) {
            response.sendRedirect("restaurants");
            return;
        }
        
        try {
            int itemId = Integer.parseInt(itemIdStr);
            int quantity = qtyStr != null ? Integer.parseInt(qtyStr) : 1;
            
            @SuppressWarnings("unchecked")
            Map<Integer, Integer> cart = (Map<Integer, Integer>) request.getSession().getAttribute("cart");
            if (cart == null) {
                cart = new HashMap<>();
                request.getSession().setAttribute("cart", cart);
            }
            
            cart.put(itemId, cart.getOrDefault(itemId, 0) + quantity);
            
            response.sendRedirect("cart");
        } catch (NumberFormatException e) {
            response.sendRedirect("restaurants");
        }
    }
    
    private void removeFromCart(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String itemIdStr = request.getParameter("itemId");
        
        if (itemIdStr != null) {
            try {
                int itemId = Integer.parseInt(itemIdStr);
                @SuppressWarnings("unchecked")
                Map<Integer, Integer> cart = (Map<Integer, Integer>) request.getSession().getAttribute("cart");
                if (cart != null) {
                    cart.remove(itemId);
                }
            } catch (NumberFormatException e) {
                // ignore
            }
        }
        
        response.sendRedirect("cart");
    }
    
    private void updateCart(HttpServletRequest request, HttpServletResponse response) throws IOException {
        @SuppressWarnings("unchecked")
        Map<Integer, Integer> cart = (Map<Integer, Integer>) request.getSession().getAttribute("cart");
        if (cart == null) {
            response.sendRedirect("cart");
            return;
        }
        
        for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
            String qtyParam = request.getParameter("qty_" + entry.getKey());
            if (qtyParam != null) {
                try {
                    int qty = Integer.parseInt(qtyParam);
                    if (qty > 0) {
                        cart.put(entry.getKey(), qty);
                    } else {
                        cart.remove(entry.getKey());
                    }
                } catch (NumberFormatException e) {
                    // ignore
                }
            }
        }
        
        response.sendRedirect("cart");
    }
    
    private void checkout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        @SuppressWarnings("unchecked")
        Map<Integer, Integer> cart = (Map<Integer, Integer>) request.getSession().getAttribute("cart");
        
        if (cart == null || cart.isEmpty()) {
            response.sendRedirect("cart");
            return;
        }
        
        // Get restaurant ID from first item
        MenuItem firstItem = menuItemDAO.findById(cart.keySet().iterator().next());
        if (firstItem == null) {
            response.sendRedirect("cart");
            return;
        }
        
        int restaurantId = firstItem.getRestaurantId();
        
        // Build query string for create-order
        StringBuilder query = new StringBuilder("restaurantId=" + restaurantId);
        for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
            query.append("&itemId=").append(entry.getKey());
            query.append("&qty_").append(entry.getKey()).append("=").append(entry.getValue());
        }
        
        // Clear cart
        request.getSession().removeAttribute("cart");
        
        // Redirect to create order
        response.sendRedirect("create-order?" + query.toString());
    }
    
    private void showCart(HttpServletRequest request, HttpServletResponse response) throws IOException {
        @SuppressWarnings("unchecked")
        Map<Integer, Integer> cart = (Map<Integer, Integer>) request.getSession().getAttribute("cart");
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Shopping Cart - Food Delivery</title>");
        out.println("<link rel='stylesheet' href='css/style.css'>");
        out.println("</head>");
        out.println("<body>");
        out.println(getNavigation());
        out.println("<div class='container'>");
        out.println("<h1 class='page-title'>🛒 Shopping Cart</h1>");
        
        if (cart == null || cart.isEmpty()) {
            out.println("<div class='section' style='text-align: center; padding: 3rem;'>");
            out.println("<div style='font-size: 4rem; margin-bottom: 1rem;'>🛒</div>");
            out.println("<h2>Your cart is empty</h2>");
            out.println("<p style='color: #f0f0f0; margin-bottom: 2rem;'>Add items from restaurants to your cart.</p>");
            out.println("<a href='restaurants' class='btn'>Browse Restaurants</a>");
            out.println("</div>");
        } else {
            List<CartItem> cartItems = new ArrayList<>();
            double total = 0.0;
            int restaurantId = -1;
            
            for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
                MenuItem item = menuItemDAO.findById(entry.getKey());
                if (item != null) {
                    int qty = entry.getValue();
                    double itemTotal = item.getPrice() * qty;
                    total += itemTotal;
                    cartItems.add(new CartItem(item, qty, itemTotal));
                    if (restaurantId == -1) {
                        restaurantId = item.getRestaurantId();
                    }
                }
            }
            
            Restaurant restaurant = restaurantId > 0 ? restaurantDAO.findById(restaurantId) : null;
            
            out.println("<div class='section'>");
            if (restaurant != null) {
                out.println("<h2>Restaurant: " + restaurant.getName() + "</h2>");
            }
            
            out.println("<form method='post' action='cart'>");
            out.println("<input type='hidden' name='action' value='update'>");
            out.println("<div class='table-container'>");
            out.println("<table class='data-table'>");
            out.println("<thead><tr><th>Item</th><th>Price</th><th>Quantity</th><th>Total</th><th>Action</th></tr></thead>");
            out.println("<tbody>");
            
            for (CartItem cartItem : cartItems) {
                MenuItem item = cartItem.item;
                out.println("<tr>");
                out.println("<td><strong>" + item.getName() + "</strong><br><small>" + item.getDescription() + "</small></td>");
                out.println("<td>" + CurrencyUtil.format(item.getPrice()) + "</td>");
                out.println("<td><input type='number' name='qty_" + item.getId() + "' value='" + cartItem.quantity + "' min='1' style='width: 60px; padding: 0.5rem;'></td>");
                out.println("<td>" + CurrencyUtil.format(cartItem.total) + "</td>");
                out.println("<td><a href='cart?action=remove&itemId=" + item.getId() + "' class='btn' style='padding: 0.25rem 0.5rem; font-size: 0.9rem;'>Remove</a></td>");
                out.println("</tr>");
            }
            
            out.println("</tbody>");
            out.println("<tfoot>");
            out.println("<tr style='background: var(--card-bg); font-weight: bold;'>");
            out.println("<td colspan='3' style='text-align: right;'>Total:</td>");
            out.println("<td colspan='2'>" + CurrencyUtil.format(total) + "</td>");
            out.println("</tr>");
            out.println("</tfoot>");
            out.println("</table>");
            out.println("</div>");
            
            out.println("<div style='margin-top: 2rem; display: flex; gap: 1rem; justify-content: center;'>");
            out.println("<button type='submit' class='btn'>Update Cart</button>");
            out.println("<a href='restaurants' class='btn btn-secondary'>Continue Shopping</a>");
            out.println("</div>");
            out.println("</form>");
            
            out.println("<form method='post' action='cart' style='margin-top: 1rem; text-align: center;'>");
            out.println("<input type='hidden' name='action' value='checkout'>");
            out.println("<button type='submit' class='btn' style='font-size: 1.2rem; padding: 1rem 2rem;'>Checkout & Place Order</button>");
            out.println("</form>");
            
            out.println("</div>");
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
               "<li><a href='cart'>Cart</a></li>" +
               "<li><a href='my-orders'>My Orders</a></li>" +
               "<li><a href='register'>Register</a></li>" +
               "<li><a href='login'>Login</a></li>" +
               "</ul>" +
               "</div>" +
               "</nav>";
    }
    
    private static class CartItem {
        MenuItem item;
        int quantity;
        double total;
        
        CartItem(MenuItem item, int quantity, double total) {
            this.item = item;
            this.quantity = quantity;
            this.total = total;
        }
    }
}

