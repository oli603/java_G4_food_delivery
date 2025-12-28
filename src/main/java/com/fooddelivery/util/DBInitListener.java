package com.fooddelivery.util;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Automatically ensures the database schema (tables) exists on application startup
 * by executing src/main/resources/sql/create_db.sql from the classpath and then
 * seeding a very small set of sample data (1 restaurant + 6 menu items) if the
 * database is empty.
 */
@WebListener
public class DBInitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();
        ctx.log("[DBInitListener] Starting database initialization using create_db.sql");

        // create_db.sql is placed under src/main/resources/sql, so at runtime it is on classpath as sql/create_db.sql
        try (Connection conn = DBUtil.getConnection();
             InputStream is = DBInitListener.class.getClassLoader().getResourceAsStream("sql/create_db.sql")) {

            if (is == null) {
                ctx.log("[DBInitListener] create_db.sql not found on classpath; skipping DB init.");
                return;
            }

            String sql = loadSqlWithoutComments(is);
            if (sql.trim().isEmpty()) {
                ctx.log("[DBInitListener] create_db.sql is empty after removing comments; nothing to execute.");
                return;
            }

            String[] statements = sql.split(";");
            int executedCount = 0;
            for (String statementText : statements) {
                String trimmed = statementText.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(trimmed);
                    executedCount++;
                } catch (Exception ex) {
                    // Log and continue so that one failing statement does not block others
                    ctx.log("[DBInitListener] Failed to execute SQL statement: " + trimmed, ex);
                }
            }

            ctx.log("[DBInitListener] Database initialization completed. Executed statements: " + executedCount);

            // After schema is ready, seed a single restaurant with 6 menu items if empty
            seedMinimalSampleData(conn, ctx);
        } catch (Exception e) {
            ctx.log("[DBInitListener] Unexpected error during database initialization", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // nothing to clean up
    }

    /**
     * Insert exactly one demo restaurant and six demo menu items if the corresponding
     * tables are currently empty. This keeps the app very small and focused.
     */
    private void seedMinimalSampleData(Connection conn, ServletContext ctx) {
        try (Statement stmt = conn.createStatement()) {
            // Ensure there is at least one user to own the restaurant
            int userCount = 0;
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS cnt FROM users")) {
                if (rs.next()) {
                    userCount = rs.getInt("cnt");
                }
            }

            if (userCount == 0) {
                stmt.executeUpdate(
                        "INSERT INTO users (name, email, phone, password_hash, role, status) VALUES " +
                                "('Demo User', 'demo@fooddelivery.com', '1111111111', 'demo123', 'CUSTOMER', 'ACTIVE')");
                ctx.log("[DBInitListener] Inserted demo user.");
            }

            int restaurantCount = 0;
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS cnt FROM restaurants")) {
                if (rs.next()) {
                    restaurantCount = rs.getInt("cnt");
                }
            }

            int menuItemCount = 0;
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS cnt FROM menu_items")) {
                if (rs.next()) {
                    menuItemCount = rs.getInt("cnt");
                }
            }

            // Only seed if no restaurants and no menu items yet
            if (restaurantCount > 0 || menuItemCount > 0) {
                ctx.log("[DBInitListener] Existing restaurant/menu data found; skipping minimal sample seed.");
                return;
            }

            // Use the first user as owner
            int ownerUserId = 1;
            try (ResultSet rs = stmt.executeQuery("SELECT id FROM users ORDER BY id LIMIT 1")) {
                if (rs.next()) {
                    ownerUserId = rs.getInt("id");
                }
            }

            // Insert a single simple restaurant
            stmt.executeUpdate(
                    "INSERT INTO restaurants (owner_user_id, name, address, cuisine_type, rating, min_order_value, is_active) VALUES " +
                            "(" + ownerUserId + ", 'Demo Restaurant', 'Demo Street 1', 'Ethiopian', 4.5, 0.00, TRUE)");

            int restaurantId = -1;
            try (ResultSet rs = stmt.executeQuery("SELECT id FROM restaurants WHERE name = 'Demo Restaurant' ORDER BY id DESC LIMIT 1")) {
                if (rs.next()) {
                    restaurantId = rs.getInt("id");
                }
            }

            if (restaurantId == -1) {
                ctx.log("[DBInitListener] Could not determine demo restaurant id; aborting menu seed.");
                return;
            }

            // Insert exactly 6 simple menu items for the demo restaurant
            String[] inserts = new String[] {
                    "INSERT INTO menu_items (restaurant_id, name, description, price, category, is_available) VALUES (" + restaurantId + ", 'Doro Wat', 'Spicy chicken stew', 100.00, 'Main Course', TRUE)",
                    "INSERT INTO menu_items (restaurant_id, name, description, price, category, is_available) VALUES (" + restaurantId + ", 'Tibs', 'Saut\u00e9ed meat with vegetables', 90.00, 'Main Course', TRUE)",
                    "INSERT INTO menu_items (restaurant_id, name, description, price, category, is_available) VALUES (" + restaurantId + ", 'Shiro', 'Chickpea stew', 70.00, 'Vegetarian', TRUE)",
                    "INSERT INTO menu_items (restaurant_id, name, description, price, category, is_available) VALUES (" + restaurantId + ", 'Misir Wat', 'Red lentil stew', 65.00, 'Vegetarian', TRUE)",
                    "INSERT INTO menu_items (restaurant_id, name, description, price, category, is_available) VALUES (" + restaurantId + ", 'Injera', 'Traditional flatbread', 20.00, 'Bread', TRUE)",
                    "INSERT INTO menu_items (restaurant_id, name, description, price, category, is_available) VALUES (" + restaurantId + ", 'Ethiopian Coffee', 'Traditional coffee', 30.00, 'Beverage', TRUE)"
            };

            int added = 0;
            for (String insert : inserts) {
                try {
                    stmt.executeUpdate(insert);
                    added++;
                } catch (Exception ex) {
                    ctx.log("[DBInitListener] Failed to insert demo menu item.", ex);
                }
            }

            ctx.log("[DBInitListener] Minimal sample data seeded: 1 restaurant + " + added + " menu items.");
        } catch (Exception e) {
            ctx.log("[DBInitListener] Error while seeding minimal sample data", e);
        }
    }

    private String loadSqlWithoutComments(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                // Skip SQL single-line comments and empty lines
                if (trimmed.startsWith("--") || trimmed.isEmpty()) {
                    continue;
                }
                sb.append(line).append('\n');
            }
        }
        return sb.toString();
    }
}
