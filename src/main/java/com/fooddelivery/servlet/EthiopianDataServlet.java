package com.fooddelivery.servlet;

import com.fooddelivery.util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Statement;

@WebServlet("/add-ethiopian-data")
public class EthiopianDataServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Add Ethiopian Data - Food Delivery</title>");
        out.println("<link rel='stylesheet' href='css/style.css'>");
        out.println("</head>");
        out.println("<body>");
        out.println(getNavigation());
        
        out.println("<div class='container'>");
        out.println("<h1 class='page-title'>🇪🇹 Add Ethiopian Restaurants & Menu</h1>");
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Get or create a default owner user
            int ownerUserId = 1;
            try {
                java.sql.ResultSet userCheck = stmt.executeQuery("SELECT id FROM users LIMIT 1");
                if (userCheck.next()) {
                    ownerUserId = userCheck.getInt("id");
                } else {
                    // Create a default owner user
                    stmt.executeUpdate("INSERT INTO users (name, email, phone, password_hash, role, status) VALUES " +
                            "('System Admin', 'admin@fooddelivery.com', '1234567890', 'admin123', 'ADMIN', 'ACTIVE')");
                    ownerUserId = 1;
                }
                userCheck.close();
            } catch (Exception e) {
                // Try to use user ID 1
                ownerUserId = 1;
            }
            
            // Insert Ethiopian Restaurants from Adama (prices in Ethiopian Birr)
            String[] restaurantInserts = {
                "INSERT IGNORE INTO restaurants (owner_user_id, name, address, cuisine_type, rating, min_order_value, is_active) VALUES " +
                "(" + ownerUserId + ", 'Adama Habesha Restaurant', '123 Bole Road, Adama, Ethiopia', 'Ethiopian', 4.8, 825.00, TRUE)",
                
                "INSERT IGNORE INTO restaurants (owner_user_id, name, address, cuisine_type, rating, min_order_value, is_active) VALUES " +
                "(" + ownerUserId + ", 'Adama Mesob Kitchen', '456 Central Market, Adama, Ethiopia', 'Ethiopian', 4.7, 660.00, TRUE)",
                
                "INSERT IGNORE INTO restaurants (owner_user_id, name, address, cuisine_type, rating, min_order_value, is_active) VALUES " +
                "(" + ownerUserId + ", 'Lucy Adama Restaurant', '789 Awash Street, Adama, Ethiopia', 'Ethiopian', 4.9, 990.00, TRUE)",
                
                "INSERT IGNORE INTO restaurants (owner_user_id, name, address, cuisine_type, rating, min_order_value, is_active) VALUES " +
                "(" + ownerUserId + ", 'Adama Traditional Cuisine', '321 Stadium Road, Adama, Ethiopia', 'Ethiopian', 4.6, 770.00, TRUE)",
                
                "INSERT IGNORE INTO restaurants (owner_user_id, name, address, cuisine_type, rating, min_order_value, is_active) VALUES " +
                "(" + ownerUserId + ", 'Queen of Sheba Adama', '654 Railway Station Area, Adama, Ethiopia', 'Ethiopian', 4.5, 880.00, TRUE)"
            };
            
            int restaurantsAdded = 0;
            for (String sql : restaurantInserts) {
                try {
                    stmt.executeUpdate(sql);
                    restaurantsAdded++;
                } catch (Exception e) {
                    // Restaurant might already exist, try to update instead
                    try {
                        // Extract restaurant name from SQL
                        String name = sql.contains("'Adama Habesha") ? "Adama Habesha Restaurant" :
                                     sql.contains("'Adama Mesob") ? "Adama Mesob Kitchen" :
                                     sql.contains("'Lucy Adama") ? "Lucy Adama Restaurant" :
                                     sql.contains("'Adama Traditional") ? "Adama Traditional Cuisine" :
                                     "Queen of Sheba Adama";
                        
                        // Check if restaurant exists
                        java.sql.ResultSet check = stmt.executeQuery("SELECT id FROM restaurants WHERE name = '" + name + "'");
                        if (!check.next()) {
                            // Doesn't exist, try insert again with different approach
                            stmt.executeUpdate(sql.replace("INSERT INTO", "INSERT IGNORE INTO"));
                            restaurantsAdded++;
                        }
                        check.close();
                    } catch (Exception e2) {
                        // Skip if can't insert
                    }
                }
            }
            
            // Get restaurant IDs
            java.sql.ResultSet rs = stmt.executeQuery("SELECT id FROM restaurants WHERE cuisine_type = 'Ethiopian' ORDER BY id LIMIT 5");
            java.util.List<Integer> restaurantIds = new java.util.ArrayList<>();
            while (rs.next()) {
                restaurantIds.add(rs.getInt("id"));
            }
            rs.close();
            
            // Insert Menu Items
            int menuItemsAdded = 0;
            if (!restaurantIds.isEmpty()) {
                // Prices converted to Ethiopian Birr (approximately 1 USD = 55 ETB)
                String[][] menuItems = {
                    // Addis Ababa Restaurant
                    {"Doro Wat", "Spicy chicken stew with hard-boiled eggs, served with injera", "1044.45", "Main Course"},
                    {"Tibs", "Sautéed beef or lamb with onions, peppers, and spices", "934.45", "Main Course"},
                    {"Kitfo", "Minced raw beef marinated in mitmita and niter kibbeh", "1099.45", "Main Course"},
                    {"Injera", "Traditional Ethiopian sourdough flatbread", "219.45", "Bread"},
                    {"Shiro", "Ground chickpea stew with berbere spice", "714.45", "Vegetarian"},
                    {"Misir Wat", "Spicy red lentil stew", "659.45", "Vegetarian"},
                    {"Gomen", "Collard greens cooked with garlic and spices", "604.45", "Vegetarian"},
                    {"Fasolia", "Green beans and carrots in tomato sauce", "604.45", "Vegetarian"},
                    {"Atkilt", "Cabbage, potatoes, and carrots in turmeric", "604.45", "Vegetarian"},
                    {"Ethiopian Coffee", "Traditional Ethiopian coffee ceremony", "329.45", "Beverage"},
                    
                    // Habesha Kitchen
                    {"Doro Wat", "Spicy chicken stew with hard-boiled eggs", "989.45", "Main Course"},
                    {"Lamb Tibs", "Tender lamb sautéed with onions and peppers", "1044.45", "Main Course"},
                    {"Beyaynetu", "Vegetarian platter with various stews", "824.45", "Vegetarian"},
                    {"Injera", "Traditional sourdough flatbread", "219.45", "Bread"},
                    {"Kik Alicha", "Mild yellow split pea stew", "659.45", "Vegetarian"},
                    {"Doro Alicha", "Mild chicken stew", "934.45", "Main Course"},
                    {"Tibs Firfir", "Shredded injera mixed with tibs", "879.45", "Main Course"},
                    {"Ethiopian Tea", "Spiced Ethiopian tea", "219.45", "Beverage"},
                    {"Honey Wine (Tej)", "Traditional Ethiopian honey wine", "494.45", "Beverage"},
                    {"Baklava", "Sweet pastry with honey and nuts", "384.45", "Dessert"},
                    
                    // Lucy Ethiopian Restaurant
                    {"Doro Wat", "Signature spicy chicken stew", "1099.45", "Main Course"},
                    {"Kitfo", "Premium minced raw beef with spices", "1209.45", "Main Course"},
                    {"Awaze Tibs", "Spicy beef sautéed in awaze sauce", "1154.45", "Main Course"},
                    {"Injera", "Fresh traditional injera", "274.45", "Bread"},
                    {"Vegetarian Combo", "Assorted vegetarian dishes", "934.45", "Vegetarian"},
                    {"Shiro Wat", "Spicy chickpea stew", "769.45", "Vegetarian"},
                    {"Gomen Wat", "Spicy collard greens", "659.45", "Vegetarian"},
                    {"Ethiopian Coffee", "Traditional coffee ceremony", "384.45", "Beverage"},
                    {"Tibs Special", "Premium beef tibs with special spices", "1264.45", "Main Course"},
                    {"Ful", "Fava beans with spices and vegetables", "714.45", "Vegetarian"},
                    
                    // Mesob Ethiopian Cuisine
                    {"Doro Wat", "Classic spicy chicken stew", "989.45", "Main Course"},
                    {"Tibs", "Beef sautéed with vegetables", "934.45", "Main Course"},
                    {"Injera", "Traditional Ethiopian bread", "219.45", "Bread"},
                    {"Misir Wat", "Spicy red lentil stew", "659.45", "Vegetarian"},
                    {"Shiro", "Chickpea stew with berbere", "714.45", "Vegetarian"},
                    {"Gomen", "Collard greens with garlic", "604.45", "Vegetarian"},
                    {"Doro Alicha", "Mild chicken stew", "879.45", "Main Course"},
                    {"Ethiopian Salad", "Fresh vegetables with vinaigrette", "494.45", "Salad"},
                    {"Ethiopian Coffee", "Traditional coffee", "329.45", "Beverage"},
                    {"Tej", "Honey wine", "439.45", "Beverage"},
                    
                    // Queen of Sheba Restaurant
                    {"Doro Wat", "Royal spicy chicken stew", "1044.45", "Main Course"},
                    {"Kitfo", "Traditional minced raw beef", "1154.45", "Main Course"},
                    {"Lamb Tibs", "Tender lamb with spices", "1099.45", "Main Course"},
                    {"Injera", "Traditional sourdough bread", "274.45", "Bread"},
                    {"Beyaynetu", "Vegetarian platter", "879.45", "Vegetarian"},
                    {"Shiro Wat", "Spicy chickpea stew", "769.45", "Vegetarian"},
                    {"Kik Alicha", "Mild yellow split peas", "659.45", "Vegetarian"},
                    {"Ethiopian Coffee", "Coffee ceremony", "384.45", "Beverage"},
                    {"Doro Wat Special", "Premium chicken stew with extra eggs", "1264.45", "Main Course"},
                    {"Ethiopian Honey", "Pure Ethiopian honey", "439.45", "Dessert"}
                };
                
                int itemsPerRestaurant = 10;
                for (int i = 0; i < restaurantIds.size() && i < 5; i++) {
                    int restaurantId = restaurantIds.get(i);
                    int startIdx = i * itemsPerRestaurant;
                    int endIdx = Math.min(startIdx + itemsPerRestaurant, menuItems.length);
                    
                    for (int j = startIdx; j < endIdx; j++) {
                        String name = menuItems[j][0];
                        String description = menuItems[j][1];
                        String price = menuItems[j][2];
                        String category = menuItems[j][3];
                        
                        String insertSQL = "INSERT IGNORE INTO menu_items (restaurant_id, name, description, price, category, is_available) VALUES " +
                                "(" + restaurantId + ", '" + name.replace("'", "''") + "', '" + 
                                description.replace("'", "''") + "', " + price + ", '" + category + "', TRUE)";
                        
                        try {
                            stmt.executeUpdate(insertSQL);
                            menuItemsAdded++;
                        } catch (Exception e) {
                            // Item might already exist
                        }
                    }
                }
            }
            
            out.println("<div class='alert alert-success'>");
            out.println("<h2>✅ Ethiopian Data Added Successfully!</h2>");
            out.println("<p><strong>Restaurants Added:</strong> " + restaurantsAdded + "</p>");
            out.println("<p><strong>Menu Items Added:</strong> " + menuItemsAdded + "</p>");
            out.println("</div>");
            
            out.println("<div class='section'>");
            out.println("<h2>🇪🇹 Ethiopian Restaurants from Adama:</h2>");
            out.println("<ul style='list-style:none;padding:0;'>");
            out.println("<li>🍽️ Adama Habesha Restaurant - Bole Road, Adama</li>");
            out.println("<li>🍽️ Adama Mesob Kitchen - Central Market, Adama</li>");
            out.println("<li>🍽️ Lucy Adama Restaurant - Awash Street, Adama</li>");
            out.println("<li>🍽️ Adama Traditional Cuisine - Stadium Road, Adama</li>");
            out.println("<li>🍽️ Queen of Sheba Adama - Railway Station Area, Adama</li>");
            out.println("</ul>");
            out.println("</div>");
            
            out.println("<div class='section'>");
            out.println("<h2>🍴 Popular Ethiopian Dishes:</h2>");
            out.println("<ul style='list-style:none;padding:0;'>");
            out.println("<li>🍗 Doro Wat - Spicy chicken stew</li>");
            out.println("<li>🥩 Tibs - Sautéed meat</li>");
            out.println("<li>🥩 Kitfo - Minced raw beef</li>");
            out.println("<li>🫓 Injera - Traditional bread</li>");
            out.println("<li>🥘 Shiro - Chickpea stew</li>");
            out.println("<li>🥘 Misir Wat - Red lentil stew</li>");
            out.println("<li>🥬 Gomen - Collard greens</li>");
            out.println("<li>☕ Ethiopian Coffee</li>");
            out.println("</ul>");
            out.println("</div>");
            
            out.println("<div style='text-align:center;margin-top:2rem;'>");
            out.println("<a href='restaurants' class='btn'>View Restaurants</a> ");
            out.println("<a href='menu-items' class='btn btn-secondary'>View Menu Items</a>");
            out.println("</div>");
            
        } catch (Exception e) {
            out.println("<div class='alert alert-error'>");
            out.println("<h2>❌ Error Adding Data</h2>");
            out.println("<p>" + e.getMessage() + "</p>");
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
               "<li><a href='cart'>Cart</a></li>" +
               "<li><a href='my-orders'>My Orders</a></li>" +
               "<li><a href='register'>Register</a></li>" +
               "<li><a href='login'>Login</a></li>" +
               "</ul>" +
               "</div>" +
               "</nav>";
    }
}

