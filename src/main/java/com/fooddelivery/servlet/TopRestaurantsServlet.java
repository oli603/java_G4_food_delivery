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

@WebServlet("/top-restaurants")
public class TopRestaurantsServlet extends HttpServlet {
    
    private RestaurantDAO restaurantDAO = new RestaurantDAOImpl();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Load a few top-rated restaurants for the homepage section
        List<Restaurant> topRestaurants = restaurantDAO.findTopRestaurants(6);
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        out.println("[");
        for (int i = 0; i < topRestaurants.size(); i++) {
            Restaurant r = topRestaurants.get(i);
            out.println("{");
            out.println("\"id\":" + r.getId() + ",");
            out.println("\"name\":\"" + escapeJson(r.getName()) + "\",");
            out.println("\"address\":\"" + escapeJson(r.getAddress()) + "\",");
            out.println("\"cuisineType\":\"" + escapeJson(r.getCuisineType()) + "\",");
            out.println("\"rating\":" + r.getRating() + ",");
            out.println("\"minOrderValue\":" + r.getMinOrderValue());
            out.println("}");
            if (i < topRestaurants.size() - 1) {
                out.println(",");
            }
        }
        out.println("]");
    }
    
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}

