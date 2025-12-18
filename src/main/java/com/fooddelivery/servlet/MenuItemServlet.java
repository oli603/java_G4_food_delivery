package com.fooddelivery.servlet;

import com.fooddelivery.dao.MenuItemDAO;
import com.fooddelivery.dao.RestaurantDAO;
import com.fooddelivery.dao.impl.MenuItemDAOImpl;
import com.fooddelivery.dao.impl.RestaurantDAOImpl;
import com.fooddelivery.model.MenuItem;
import com.fooddelivery.model.Restaurant;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/menu-items")
public class MenuItemServlet extends HttpServlet {
    
    private MenuItemDAO menuItemDAO = new MenuItemDAOImpl();
    private RestaurantDAO restaurantDAO = new RestaurantDAOImpl();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String restaurantIdParam = request.getParameter("restaurantId");
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Menu Items - Food Delivery</title>");
        out.println("<link rel='stylesheet' href='css/style.css'>");
        out.println("</head>");
        out.println("<body>");
        out.println(getNavigation());
        
        out.println("<div class='container'>");
        out.println("<h1 class='page-title'>🍽️ Menu Items</h1>");
        
        // Restaurant Filter
        out.println("<div class='section'>");
        out.println("<h2>Filter by Restaurant</h2>");
        out.println("<form method='get' class='filter-form'>");
        out.println("<select name='restaurantId' class='form-group' style='display:inline-block;width:auto;margin-right:1rem;'>");
        out.println("<option value=''>All Restaurants</option>");
        
        List<Restaurant> restaurants = restaurantDAO.findAllActive();
        for (Restaurant r : restaurants) {
            String selected = (restaurantIdParam != null && restaurantIdParam.equals(String.valueOf(r.getId()))) ? "selected" : "";
            out.println("<option value='" + r.getId() + "' " + selected + ">" + r.getName() + "</option>");
        }
        
        out.println("</select>");
        out.println("<button type='submit' class='btn'>Filter</button>");
        out.println("</form>");
        out.println("</div>");
        
        // Menu Items Grid
        out.println("<div class='menu-items-grid'>");
        
        if (restaurantIdParam != null && !restaurantIdParam.isEmpty()) {
            int restaurantId = Integer.parseInt(restaurantIdParam);
            Restaurant restaurant = restaurantDAO.findById(restaurantId);
            if (restaurant != null) {
                out.println("<div class='section'>");
                out.println("<h2>Menu for " + restaurant.getName() + "</h2>");
                out.println("</div>");
                
                List<MenuItem> items = menuItemDAO.findByRestaurantId(restaurantId);
                displayMenuItems(out, items);
            }
        } else {
            // Show menu items from all restaurants
            for (Restaurant r : restaurants) {
                List<MenuItem> items = menuItemDAO.findByRestaurantId(r.getId());
                if (!items.isEmpty()) {
                    out.println("<div class='section'>");
                    out.println("<h2>🍴 " + r.getName() + "</h2>");
                    displayMenuItems(out, items);
                    out.println("</div>");
                }
            }
        }
        
        out.println("</div>");
        out.println("</div>");
        out.println("<script src='js/main.js'></script>");
        out.println("</body></html>");
    }
    
    private void displayMenuItems(PrintWriter out, List<MenuItem> items) {
        if (items.isEmpty()) {
            out.println("<p style='text-align:center;color:var(--white);'>No menu items available.</p>");
            return;
        }
        
        // Actual Ethiopian food photos mapped to dishes
        Map<String, String> foodImageMap = new HashMap<>();
        foodImageMap.put("Doro Wat", "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=400&h=300&fit=crop&q=85"); // Chicken stew
        foodImageMap.put("Tibs", "https://images.unsplash.com/photo-1565958011703-44f9829ba187?w=400&h=300&fit=crop&q=85"); // Sautéed meat
        foodImageMap.put("Kitfo", "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=400&h=300&fit=crop&q=85"); // Raw beef
        foodImageMap.put("Injera", "https://images.unsplash.com/photo-1571091718767-18b5b1457add?w=400&h=300&fit=crop&q=85"); // Ethiopian bread
        foodImageMap.put("Shiro", "https://images.unsplash.com/photo-1567620905732-2d1ec7ab7445?w=400&h=300&fit=crop&q=85"); // Chickpea stew
        foodImageMap.put("Misir Wat", "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=400&h=300&fit=crop&q=85"); // Lentil stew
        foodImageMap.put("Gomen", "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=400&h=300&fit=crop&q=85"); // Collard greens
        foodImageMap.put("Beyaynetu", "https://images.unsplash.com/photo-1571091718767-18b5b1457add?w=400&h=300&fit=crop&q=85"); // Vegetarian platter
        foodImageMap.put("Ethiopian Coffee", "https://images.unsplash.com/photo-1559056199-641a0ac8b55e?w=400&h=300&fit=crop&q=85"); // Coffee
        foodImageMap.put("Lamb Tibs", "https://images.unsplash.com/photo-1565958011703-44f9829ba187?w=400&h=300&fit=crop&q=85");
        foodImageMap.put("Doro Alicha", "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=400&h=300&fit=crop&q=85");
        foodImageMap.put("Tibs Firfir", "https://images.unsplash.com/photo-1565958011703-44f9829ba187?w=400&h=300&fit=crop&q=85");
        foodImageMap.put("Kik Alicha", "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=400&h=300&fit=crop&q=85");
        foodImageMap.put("Shiro Wat", "https://images.unsplash.com/photo-1567620905732-2d1ec7ab7445?w=400&h=300&fit=crop&q=85");
        foodImageMap.put("Gomen Wat", "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=400&h=300&fit=crop&q=85");
        foodImageMap.put("Awaze Tibs", "https://images.unsplash.com/photo-1565958011703-44f9829ba187?w=400&h=300&fit=crop&q=85");
        foodImageMap.put("Tibs Special", "https://images.unsplash.com/photo-1565958011703-44f9829ba187?w=400&h=300&fit=crop&q=85");
        foodImageMap.put("Vegetarian Combo", "https://images.unsplash.com/photo-1571091718767-18b5b1457add?w=400&h=300&fit=crop&q=85");
        foodImageMap.put("Ful", "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=400&h=300&fit=crop&q=85");
        foodImageMap.put("Fasolia", "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=400&h=300&fit=crop&q=85");
        foodImageMap.put("Atkilt", "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=400&h=300&fit=crop&q=85");
        foodImageMap.put("Ethiopian Tea", "https://images.unsplash.com/photo-1559056199-641a0ac8b55e?w=400&h=300&fit=crop&q=85");
        foodImageMap.put("Honey Wine (Tej)", "https://images.unsplash.com/photo-1559056199-641a0ac8b55e?w=400&h=300&fit=crop&q=85");
        foodImageMap.put("Tej", "https://images.unsplash.com/photo-1559056199-641a0ac8b55e?w=400&h=300&fit=crop&q=85");
        foodImageMap.put("Baklava", "https://images.unsplash.com/photo-1571091718767-18b5b1457add?w=400&h=300&fit=crop&q=85");
        foodImageMap.put("Ethiopian Honey", "https://images.unsplash.com/photo-1571091718767-18b5b1457add?w=400&h=300&fit=crop&q=85");
        foodImageMap.put("Ethiopian Salad", "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=400&h=300&fit=crop&q=85");
        foodImageMap.put("Doro Wat Special", "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=400&h=300&fit=crop&q=85");
        
        // Default images array for fallback
        String[] defaultFoodImages = {
            "https://images.unsplash.com/photo-1571091718767-18b5b1457add?w=300&h=200&fit=crop&q=80",
            "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=300&h=200&fit=crop&q=80",
            "https://images.unsplash.com/photo-1565958011703-44f9829ba187?w=300&h=200&fit=crop&q=80",
            "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=300&h=200&fit=crop&q=80",
            "https://images.unsplash.com/photo-1567620905732-2d1ec7ab7445?w=300&h=200&fit=crop&q=80"
        };
        
        out.println("<div class='menu-items-list'>");
        int index = 0;
        for (MenuItem item : items) {
            String imageUrl = foodImageMap.getOrDefault(item.getName(), defaultFoodImages[index % defaultFoodImages.length]);
            
            out.println("<div class='menu-item-card'>");
            out.println("<div class='menu-item-image'>");
            out.println("<img src='" + imageUrl + "' alt='" + item.getName() + "' loading='lazy'>");
            out.println("</div>");
            out.println("<div class='menu-item-info'>");
            out.println("<h3>" + item.getName() + "</h3>");
            out.println("<p class='menu-item-description'>" + (item.getDescription() != null ? item.getDescription() : "Delicious food item") + "</p>");
            if (item.getCategory() != null) {
                out.println("<span class='menu-item-category'>" + item.getCategory() + "</span>");
            }
            out.println("<div class='menu-item-footer'>");
                out.println("<span class='menu-item-price'>" + com.fooddelivery.util.CurrencyUtil.format(item.getPrice()) + "</span>");
            out.println("<span class='menu-item-status " + (item.isAvailable() ? "available" : "unavailable") + "'>");
            out.println(item.isAvailable() ? "✅ Available" : "❌ Unavailable");
            out.println("</span>");
            out.println("</div>");
            out.println("</div>");
            out.println("</div>");
            index++;
        }
        out.println("</div>");
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

