
package com.fooddelivery.servlet;

import com.fooddelivery.dao.RestaurantDAO;
import com.fooddelivery.dao.impl.RestaurantDAOImpl;
import com.fooddelivery.model.Restaurant;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/restaurants")
public class RestaurantListServlet extends HttpServlet {
    
    private RestaurantDAO restaurantDAO = new RestaurantDAOImpl();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Show only a few top restaurants instead of all
        List<Restaurant> restaurants = restaurantDAO.findTopRestaurants(6);
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Restaurants - Food Delivery</title>");
        out.println("<link rel='stylesheet' href='css/style.css'>");
        out.println("</head>");
        out.println("<body>");
        out.println(getNavigation());
        out.println("<div class='container'>");
        out.println("<h1 class='page-title'>Restaurants</h1>");

        // Quick search bar for filtering restaurants on this page
        out.println("<div class='section'>");
        out.println("<h2>🔍 Quick Search</h2>");
        out.println("<div class='search-container'>");
        out.println("<input type='text' id='restaurantSearchInput' class='search-input' placeholder='Search restaurants by name or cuisine...'>");
        out.println("<button type='button' class='btn search-btn' onclick='filterRestaurants()'>Search</button>");
        out.println("</div>");
        out.println("<div id='restaurantSearchInfo' class='search-results'></div>");
        out.println("</div>");
        
        if (restaurants.isEmpty()) {
            out.println("<div class='section'>");
            out.println("<p style='text-align:center;color:var(--white);font-size:1.2rem;'>No restaurants found.</p>");
            out.println("</div>");
        } else {
            out.println("<div class='restaurant-grid'>");
            
            // Actual Ethiopian food photos
            String[] restaurantImages = {
                "https://images.unsplash.com/photo-1571091718767-18b5b1457add?w=400&h=250&fit=crop&q=80", // Ethiopian food platter
                "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=400&h=250&fit=crop&q=80", // Injera with stews
                "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=400&h=250&fit=crop&q=80", // Doro Wat
                "https://images.unsplash.com/photo-1565958011703-44f9829ba187?w=400&h=250&fit=crop&q=80", // Tibs
                "https://images.unsplash.com/photo-1567620905732-2d1ec7ab7445?w=400&h=250&fit=crop&q=80", // Ethiopian cuisine
                "https://images.unsplash.com/photo-1571091718767-18b5b1457add?w=400&h=250&fit=crop&q=80" // Food platter
            };
            
            int imgIndex = 0;
            for (Restaurant r : restaurants) {
                String cuisine = r.getCuisineType() != null ? r.getCuisineType() : "";
                String name = r.getName() != null ? r.getName() : "";
                String address = r.getAddress() != null ? r.getAddress() : "";
                // escape single quotes for HTML attributes
                String safeName = name.replace("'", "&#39;");
                String safeCuisine = cuisine.replace("'", "&#39;");
                String safeAddress = address.replace("'", "&#39;");
                out.println("<div class='restaurant-card' data-name='" + safeName + "' data-cuisine='" + safeCuisine + "' data-address='" + safeAddress + "'>");
                out.println("<div class='restaurant-image-wrapper'>");
                out.println("<img src='" + restaurantImages[imgIndex % restaurantImages.length] + "' alt='" + r.getName() + "' class='restaurant-image'>");
                out.println("<div class='restaurant-rating'>⭐ " + String.format("%.1f", r.getRating()) + "</div>");
                out.println("</div>");
                out.println("<div class='restaurant-card-body'>");
                if (r.getAddress() != null && !r.getAddress().isEmpty()) {
                    out.println("<p><strong>📍 Address:</strong> " + r.getAddress() + "</p>");
                }
                if (r.getCuisineType() != null && !r.getCuisineType().isEmpty()) {
                    out.println("<p><strong>🍴 Cuisine:</strong> " + r.getCuisineType() + "</p>");
                }
                out.println("<p><strong>💰 Min Order:</strong> " + com.fooddelivery.util.CurrencyUtil.format(r.getMinOrderValue()) + "</p>");
                out.println("</div>");
                out.println("<div class='restaurant-card-footer'>");
                out.println("<a href='menu-items?restaurantId=" + r.getId() + "' class='btn' style='width:48%;'>View Menu</a>");
                out.println("<a href='addme?restaurantId=" + r.getId() + "' class='btn btn-secondary' style='width:48%;'>Order All Dishes</a>");
                out.println("</div>");
                out.println("</div>");
                imgIndex++;
            }
            
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
}

