-- Create database (if not already done)
CREATE DATABASE IF NOT EXISTS food_delivery_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE food_delivery_db;

-- 1. users (must be first, many tables reference it)
CREATE TABLE IF NOT EXISTS users (
                                     id INT AUTO_INCREMENT PRIMARY KEY,
                                     name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('CUSTOMER', 'RESTAURANT_OWNER', 'ADMIN') DEFAULT 'CUSTOMER',
    status ENUM('ACTIVE', 'INACTIVE', 'BANNED') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    ) ENGINE=InnoDB;

-- 2. restaurants (references users)
CREATE TABLE IF NOT EXISTS restaurants (
                                           id INT AUTO_INCREMENT PRIMARY KEY,
                                           owner_user_id INT NOT NULL,
                                           name VARCHAR(255) NOT NULL,
    address TEXT NOT NULL,
    cuisine_type VARCHAR(100),
    rating DECIMAL(3,2) DEFAULT 0.00,
    min_order_value DECIMAL(10,2) DEFAULT 0.00,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (owner_user_id) REFERENCES users(id) ON DELETE RESTRICT
    ) ENGINE=InnoDB;

-- 3. menu_items (references restaurants)
CREATE TABLE IF NOT EXISTS menu_items (
                                          id INT AUTO_INCREMENT PRIMARY KEY,
                                          restaurant_id INT NOT NULL,
                                          name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL CHECK (price >= 0),
    category VARCHAR(100) DEFAULT 'Main Course',
    is_available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(id) ON DELETE CASCADE
    ) ENGINE=InnoDB;

-- 4. delivery_addresses (references users) -- MUST come BEFORE orders
CREATE TABLE IF NOT EXISTS delivery_addresses (
                                                  id INT AUTO_INCREMENT PRIMARY KEY,
                                                  user_id INT NOT NULL,
                                                  address_line VARCHAR(512) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(100) DEFAULT 'Ethiopia',
    is_default BOOLEAN DEFAULT FALSE,
    label VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    ) ENGINE=InnoDB;

-- 5. orders (now safe: references users, restaurants, and delivery_addresses)
CREATE TABLE IF NOT EXISTS orders (
                                      id INT AUTO_INCREMENT PRIMARY KEY,
                                      user_id INT NOT NULL,
                                      restaurant_id INT NOT NULL,
                                      delivery_address_id INT,
                                      one_time_address TEXT,
                                      total_amount DECIMAL(10,2) NOT NULL CHECK (total_amount >= 0),
    status ENUM('NEW', 'CONFIRMED', 'PREPARING', 'OUT_FOR_DELIVERY', 'DELIVERED', 'CANCELLED')
    DEFAULT 'NEW',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(id) ON DELETE SET NULL,
    FOREIGN KEY (delivery_address_id) REFERENCES delivery_addresses(id) ON DELETE SET NULL
    ) ENGINE=InnoDB;

-- 6. order_items (last, references orders and menu_items)
CREATE TABLE IF NOT EXISTS order_items (
                                           id INT AUTO_INCREMENT PRIMARY KEY,
                                           order_id INT NOT NULL,
                                           menu_item_id INT NOT NULL,
                                           quantity INT NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(10,2) NOT NULL CHECK (unit_price >= 0),
    subtotal DECIMAL(10,2) GENERATED ALWAYS AS (quantity * unit_price) STORED,

    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (menu_item_id) REFERENCES menu_items(id) ON DELETE SET NULL
    ) ENGINE=InnoDB;