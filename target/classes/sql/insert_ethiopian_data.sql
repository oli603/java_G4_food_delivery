-- Insert Ethiopian Restaurants and Menu Items
USE food_delivery_db;

-- Insert Ethiopian Restaurants from Adama (Prices in Ethiopian Birr)
INSERT INTO restaurants (owner_user_id, name, address, cuisine_type, rating, min_order_value, is_active) VALUES
(1, 'Adama Habesha Restaurant', '123 Bole Road, Adama, Ethiopia', 'Ethiopian', 4.8, 825.00, TRUE),
(1, 'Adama Mesob Kitchen', '456 Central Market, Adama, Ethiopia', 'Ethiopian', 4.7, 660.00, TRUE),
(1, 'Lucy Adama Restaurant', '789 Awash Street, Adama, Ethiopia', 'Ethiopian', 4.9, 990.00, TRUE),
(1, 'Adama Traditional Cuisine', '321 Stadium Road, Adama, Ethiopia', 'Ethiopian', 4.6, 770.00, TRUE),
(1, 'Queen of Sheba Adama', '654 Railway Station Area, Adama, Ethiopia', 'Ethiopian', 4.5, 880.00, TRUE);

-- Get restaurant IDs (assuming they were just inserted)
SET @restaurant1 = (SELECT id FROM restaurants WHERE name = 'Adama Habesha Restaurant' LIMIT 1);
SET @restaurant2 = (SELECT id FROM restaurants WHERE name = 'Adama Mesob Kitchen' LIMIT 1);
SET @restaurant3 = (SELECT id FROM restaurants WHERE name = 'Lucy Adama Restaurant' LIMIT 1);
SET @restaurant4 = (SELECT id FROM restaurants WHERE name = 'Adama Traditional Cuisine' LIMIT 1);
SET @restaurant5 = (SELECT id FROM restaurants WHERE name = 'Queen of Sheba Adama' LIMIT 1);

-- Insert Ethiopian Menu Items for Adama Habesha Restaurant (Prices in Ethiopian Birr)
INSERT INTO menu_items (restaurant_id, name, description, price, category, is_available) VALUES
(@restaurant1, 'Doro Wat', 'Spicy chicken stew with hard-boiled eggs, served with injera', 1044.45, 'Main Course', TRUE),
(@restaurant1, 'Tibs', 'Sautéed beef or lamb with onions, peppers, and spices', 934.45, 'Main Course', TRUE),
(@restaurant1, 'Kitfo', 'Minced raw beef marinated in mitmita and niter kibbeh', 1099.45, 'Main Course', TRUE),
(@restaurant1, 'Injera', 'Traditional Ethiopian sourdough flatbread', 219.45, 'Bread', TRUE),
(@restaurant1, 'Shiro', 'Ground chickpea stew with berbere spice', 714.45, 'Vegetarian', TRUE),
(@restaurant1, 'Misir Wat', 'Spicy red lentil stew', 659.45, 'Vegetarian', TRUE),
(@restaurant1, 'Gomen', 'Collard greens cooked with garlic and spices', 604.45, 'Vegetarian', TRUE),
(@restaurant1, 'Fasolia', 'Green beans and carrots in tomato sauce', 604.45, 'Vegetarian', TRUE),
(@restaurant1, 'Atkilt', 'Cabbage, potatoes, and carrots in turmeric', 604.45, 'Vegetarian', TRUE),
(@restaurant1, 'Ethiopian Coffee', 'Traditional Ethiopian coffee ceremony', 329.45, 'Beverage', TRUE);

-- Insert Ethiopian Menu Items for Adama Mesob Kitchen (Prices in Ethiopian Birr)
INSERT INTO menu_items (restaurant_id, name, description, price, category, is_available) VALUES
(@restaurant2, 'Doro Wat', 'Spicy chicken stew with hard-boiled eggs', 989.45, 'Main Course', TRUE),
(@restaurant2, 'Lamb Tibs', 'Tender lamb sautéed with onions and peppers', 1044.45, 'Main Course', TRUE),
(@restaurant2, 'Beyaynetu', 'Vegetarian platter with various stews', 824.45, 'Vegetarian', TRUE),
(@restaurant2, 'Injera', 'Traditional sourdough flatbread', 219.45, 'Bread', TRUE),
(@restaurant2, 'Kik Alicha', 'Mild yellow split pea stew', 659.45, 'Vegetarian', TRUE),
(@restaurant2, 'Doro Alicha', 'Mild chicken stew', 934.45, 'Main Course', TRUE),
(@restaurant2, 'Tibs Firfir', 'Shredded injera mixed with tibs', 879.45, 'Main Course', TRUE),
(@restaurant2, 'Ethiopian Tea', 'Spiced Ethiopian tea', 219.45, 'Beverage', TRUE),
(@restaurant2, 'Honey Wine (Tej)', 'Traditional Ethiopian honey wine', 494.45, 'Beverage', TRUE),
(@restaurant2, 'Baklava', 'Sweet pastry with honey and nuts', 384.45, 'Dessert', TRUE);

-- Insert Ethiopian Menu Items for Lucy Adama Restaurant (Prices in Ethiopian Birr)
INSERT INTO menu_items (restaurant_id, name, description, price, category, is_available) VALUES
(@restaurant3, 'Doro Wat', 'Signature spicy chicken stew', 1099.45, 'Main Course', TRUE),
(@restaurant3, 'Kitfo', 'Premium minced raw beef with spices', 1209.45, 'Main Course', TRUE),
(@restaurant3, 'Awaze Tibs', 'Spicy beef sautéed in awaze sauce', 1154.45, 'Main Course', TRUE),
(@restaurant3, 'Injera', 'Fresh traditional injera', 274.45, 'Bread', TRUE),
(@restaurant3, 'Vegetarian Combo', 'Assorted vegetarian dishes', 934.45, 'Vegetarian', TRUE),
(@restaurant3, 'Shiro Wat', 'Spicy chickpea stew', 769.45, 'Vegetarian', TRUE),
(@restaurant3, 'Gomen Wat', 'Spicy collard greens', 659.45, 'Vegetarian', TRUE),
(@restaurant3, 'Ethiopian Coffee', 'Traditional coffee ceremony', 384.45, 'Beverage', TRUE),
(@restaurant3, 'Tibs Special', 'Premium beef tibs with special spices', 1264.45, 'Main Course', TRUE),
(@restaurant3, 'Ful', 'Fava beans with spices and vegetables', 714.45, 'Vegetarian', TRUE);

-- Insert Ethiopian Menu Items for Adama Traditional Cuisine (Prices in Ethiopian Birr)
INSERT INTO menu_items (restaurant_id, name, description, price, category, is_available) VALUES
(@restaurant4, 'Doro Wat', 'Classic spicy chicken stew', 989.45, 'Main Course', TRUE),
(@restaurant4, 'Tibs', 'Beef sautéed with vegetables', 934.45, 'Main Course', TRUE),
(@restaurant4, 'Injera', 'Traditional Ethiopian bread', 219.45, 'Bread', TRUE),
(@restaurant4, 'Misir Wat', 'Spicy red lentil stew', 659.45, 'Vegetarian', TRUE),
(@restaurant4, 'Shiro', 'Chickpea stew with berbere', 714.45, 'Vegetarian', TRUE),
(@restaurant4, 'Gomen', 'Collard greens with garlic', 604.45, 'Vegetarian', TRUE),
(@restaurant4, 'Doro Alicha', 'Mild chicken stew', 879.45, 'Main Course', TRUE),
(@restaurant4, 'Ethiopian Salad', 'Fresh vegetables with vinaigrette', 494.45, 'Salad', TRUE),
(@restaurant4, 'Ethiopian Coffee', 'Traditional coffee', 329.45, 'Beverage', TRUE),
(@restaurant4, 'Tej', 'Honey wine', 439.45, 'Beverage', TRUE);

-- Insert Ethiopian Menu Items for Queen of Sheba Adama (Prices in Ethiopian Birr)
INSERT INTO menu_items (restaurant_id, name, description, price, category, is_available) VALUES
(@restaurant5, 'Doro Wat', 'Royal spicy chicken stew', 1044.45, 'Main Course', TRUE),
(@restaurant5, 'Kitfo', 'Traditional minced raw beef', 1154.45, 'Main Course', TRUE),
(@restaurant5, 'Lamb Tibs', 'Tender lamb with spices', 1099.45, 'Main Course', TRUE),
(@restaurant5, 'Injera', 'Traditional sourdough bread', 274.45, 'Bread', TRUE),
(@restaurant5, 'Beyaynetu', 'Vegetarian platter', 879.45, 'Vegetarian', TRUE),
(@restaurant5, 'Shiro Wat', 'Spicy chickpea stew', 769.45, 'Vegetarian', TRUE),
(@restaurant5, 'Kik Alicha', 'Mild yellow split peas', 659.45, 'Vegetarian', TRUE),
(@restaurant5, 'Ethiopian Coffee', 'Coffee ceremony', 384.45, 'Beverage', TRUE),
(@restaurant5, 'Doro Wat Special', 'Premium chicken stew with extra eggs', 1264.45, 'Main Course', TRUE),
(@restaurant5, 'Ethiopian Honey', 'Pure Ethiopian honey', 439.45, 'Dessert', TRUE);

