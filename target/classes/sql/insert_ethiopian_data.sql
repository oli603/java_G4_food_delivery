-- Insert Ethiopian Restaurants and Menu Items (Minimized - 3 Restaurants, 3 Menu Items Total)
USE food_delivery_db;

-- Insert 3 Ethiopian Restaurants (Prices in Ethiopian Birr)
INSERT INTO restaurants (owner_user_id, name, address, cuisine_type, rating, min_order_value, is_active) VALUES
(1, 'Lucy Adama Restaurant', '789 Awash Street, Adama', 'Ethiopian', 4.9, 990.00, TRUE),
(1, 'Adama Habesha Restaurant', '123 Bole Road, Adama', 'Ethiopian', 4.8, 825.00, TRUE),
(1, 'Adama Mesob Kitchen', '456 Central Market, Adama', 'Ethiopian', 4.7, 660.00, TRUE);

-- Get restaurant IDs
SET @restaurant1 = (SELECT id FROM restaurants WHERE name = 'Lucy Adama Restaurant' LIMIT 1);
SET @restaurant2 = (SELECT id FROM restaurants WHERE name = 'Adama Habesha Restaurant' LIMIT 1);
SET @restaurant3 = (SELECT id FROM restaurants WHERE name = 'Adama Mesob Kitchen' LIMIT 1);

-- Insert Only 3 Menu Items Total (1 per restaurant)
INSERT INTO menu_items (restaurant_id, name, description, price, category, is_available) VALUES
(@restaurant1, 'Doro Wat', 'Spicy chicken stew', 1099.45, 'Main Course', TRUE),
(@restaurant2, 'Tibs', 'Sautéed beef', 934.45, 'Main Course', TRUE),
(@restaurant3, 'Kitfo', 'Minced raw beef', 1099.45, 'Main Course', TRUE);


