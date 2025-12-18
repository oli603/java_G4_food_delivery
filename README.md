## Food Delivery System (Servlet-Based)

A Java Servlet/JSP based web application for managing a food delivery platform with users, restaurants, delivery personnel, and an admin panel.

### 1. Overview

The **Food Delivery System** allows customers to browse restaurants and menus, place and pay for orders, track delivery in real time, and rate/review restaurants. Restaurants can manage their menus and orders, delivery personnel can manage assigned deliveries, and admins can manage the whole platform.

### 1.1 Actors

- **Customer (End User)**:  
  - Registers and logs in to the system.  
  - Browses restaurants and menus, manages a shopping cart, places orders, selects payment methods, and tracks orders.  
  - Rates and reviews restaurants and views their own order history.

- **Restaurant Owner / Restaurant Staff**:  
  - Registers a restaurant and manages its profile (details, opening hours, online/offline).  
  - Adds/updates/removes menu items and manages incoming orders (accept, prepare, mark ready).  
  - Views basic performance information (orders received, ratings).

- **Delivery Personnel (Rider/Driver)**:  
  - Logs in and views assigned delivery orders.  
  - Sees customer address and contact details for delivery.  
  - Updates order status (picked up, on the way, delivered) and views delivery history/earnings.

- **Administrator (Admin)**:  
  - Manages users, restaurants, and delivery personnel (approve, block/unblock, update).  
  - Monitors system activity and orders via dashboards.  
  - Generates and views reports (sales, orders, performance) and configures global settings (fees, taxes, promotions).

Target stack:
- **Backend**: Java, Servlets, JSP, JSTL
- **Web Container**: Apache Tomcat (or any Servlet 4+ compatible container)
- **Database**: MySQL / PostgreSQL (relational)
- **Architecture**: MVC (Model–View–Controller) with DAO layer

### 2. Functional Requirements

#### 2.1 User Management
- **FR-1.1**: The system shall allow users to create an account (customer, restaurant owner, delivery personnel).
- **FR-1.2**: The system shall allow users to log in using email or phone number and a password.
- **FR-1.3**: The system shall allow users to update their profile information (name, contact, address, profile picture).
- **FR-1.4**: The system shall allow users to reset their password if forgotten (via email/OTP).
- **FR-1.5**: The system shall validate inputs and prevent duplicate accounts using the same email/phone.
- **FR-1.6**: The system shall allow users to view their order history.

#### 2.2 Restaurant Management
- **FR-2.1**: The system shall allow restaurants to register and create a profile (name, address, cuisine type, opening hours, logo).
- **FR-2.2**: The system shall allow restaurants to add, edit, or remove food items (name, price, description, category, availability).
- **FR-2.3**: The system shall display restaurant details such as name, location, ratings, delivery time estimate, and minimum order value.
- **FR-2.4**: The system shall allow restaurants to manage order statuses (accepted, preparing, ready for pickup).
- **FR-2.5**: The system shall allow restaurants to enable/disable their restaurant (online/offline).

#### 2.3 Menu and Ordering System
- **FR-3.1**: The system shall display a list of available restaurants to users with filtering and search (by cuisine, name, rating).
- **FR-3.2**: The system shall display menu items with names, prices, descriptions, and optional images.
- **FR-3.3**: The system shall allow users to add items to their shopping cart.
- **FR-3.4**: The system shall allow users to modify or remove items in their cart and update quantities.
- **FR-3.5**: The system shall allow users to place an order with delivery address selection.
- **FR-3.6**: The system shall validate stock/availability before confirming orders.
- **FR-3.7**: The system shall show an estimated delivery time and breakdown of the bill (subtotal, tax, delivery fee, total).

#### 2.4 Payment Processing
- **FR-4.1**: The system shall allow users to select a payment option: Cash on Delivery, Mobile Money (or other configured methods).
- **FR-4.2**: The system shall calculate the total order cost including delivery fees, discounts, and taxes.
- **FR-4.3**: The system shall generate a payment receipt after order confirmation.
- **FR-4.4**: The system shall securely store payment transaction references (not sensitive card data).
- **FR-4.5**: The system shall mark orders as paid/unpaid depending on payment mode and confirmation.

#### 2.5 Order Tracking
- **FR-5.1**: The system shall display the real-time status of the order (placed, accepted, preparing, out for delivery, delivered, cancelled).
- **FR-5.2**: The system shall notify users when the order status changes (on-page updates, optional email/SMS).
- **FR-5.3**: Delivery personnel shall be able to update the order status (picked up, on the way, delivered).
- **FR-5.4**: The system shall show basic tracking information (driver name, phone, approximate delivery time).

#### 2.6 Delivery Management
- **FR-6.1**: The system shall assign orders to delivery personnel (auto-assignment or manual by restaurant/admin).
- **FR-6.2**: Delivery personnel shall be able to view delivery details such as customer address, contact, and order items.
- **FR-6.3**: Delivery personnel shall confirm when an order has been delivered.
- **FR-6.4**: The system shall allow delivery personnel to view their delivery history and earnings summary.

#### 2.7 Ratings and Reviews
- **FR-7.1**: The system shall allow users to rate restaurants after receiving their order (e.g., 1–5 stars).
- **FR-7.2**: The system shall allow users to write reviews about food quality and service.
- **FR-7.3**: The system shall calculate and display average ratings per restaurant.
- **FR-7.4**: The system shall allow admins to moderate or remove inappropriate reviews.

#### 2.8 Admin Management
- **FR-8.1**: The system shall allow the admin to manage users, restaurants, and delivery workers (create, block/unblock, update).
- **FR-8.2**: The system shall allow the admin to monitor orders and system activities in real time (dashboard).
- **FR-8.3**: The system shall generate administrative reports (daily/weekly/monthly sales, order volume, restaurant performance, delivery performance).
- **FR-8.4**: The system shall allow the admin to configure global settings (delivery fees, tax rate, promotional banners).

### 3. Non-Functional Requirements (Added)

- **NFR-1 – Security**: Use input validation, prepared statements, and session management to prevent SQL Injection, XSS, and session hijacking.
- **NFR-2 – Performance**: The system should respond to typical user actions within 2–3 seconds under normal load.
- **NFR-3 – Usability**: UI should be mobile-friendly and easy to use for both customers and staff.
- **NFR-4 – Reliability**: Critical operations (order placement, payment, status update) should be ACID-compliant at the DB level.
- **NFR-5 – Maintainability**: Use layered architecture (Servlet + Service + DAO + Model) with clear separation of concerns.
- **NFR-6 – Scalability**: Design DB tables and code so that multiple servers / nodes can be used in future.

### 4. High-Level Servlet-Based Architecture

The project follows an MVC style structure:

- **Model (JavaBeans / POJOs)**:
  - `User`, `Restaurant`, `MenuItem`, `CartItem`, `Order`, `OrderItem`, `Payment`, `Delivery`, `Review`, etc.
- **DAO Layer**:
  - `UserDAO`, `RestaurantDAO`, `MenuItemDAO`, `OrderDAO`, `PaymentDAO`, `DeliveryDAO`, `ReviewDAO` – for all DB operations using JDBC.
- **Service Layer (optional but recommended)**:
  - `UserService`, `OrderService`, `RestaurantService`, etc. – business logic, validation, transaction orchestration.
- **Controller (Servlets)**:
  - `AuthServlet`, `UserProfileServlet`, `RestaurantServlet`, `MenuServlet`, `CartServlet`, `OrderServlet`, `PaymentServlet`, `DeliveryServlet`, `AdminServlet`, `ReviewServlet`.
- **View (JSP pages)**:
  - User-facing pages: `index.jsp`, `login.jsp`, `register.jsp`, `restaurants.jsp`, `menu.jsp`, `cart.jsp`, `checkout.jsp`, `orderStatus.jsp`, `profile.jsp`, `orders.jsp`, etc.
  - Admin pages: `adminDashboard.jsp`, `manageUsers.jsp`, `manageRestaurants.jsp`, `reports.jsp`.
  - Restaurant owner pages: `restaurantDashboard.jsp`, `manageMenu.jsp`, `restaurantOrders.jsp`.
  - Delivery pages: `deliveryDashboard.jsp`, `assignedOrders.jsp`.

### 5. Suggested Project Structure (Servlet Structure)

Assuming a standard Maven-style Java web app:

```text
food_delivery/
  ├─ src/
  │   └─ main/
  │       ├─ java/
  │       │   └─ com/fooddelivery/
  │       │       ├─ controller/        (Servlets)
  │       │       ├─ model/             (POJOs / entities)
  │       │       ├─ dao/               (Data Access Objects)
  │       │       └─ service/           (Business logic)
  │       ├─ webapp/
  │       │   ├─ WEB-INF/
  │       │   │   ├─ web.xml            (Servlet mappings, filters)
  │       │   │   └─ views/             (JSP files)
  │       │   ├─ assets/                (CSS, JS, images)
  │       │   └─ index.jsp
  │       └─ resources/
  │           └─ db.properties           (DB connection config)
  ├─ pom.xml                             (Maven configuration)
  └─ README.md
```

### 6. Database Design (Core Tables)

At minimum, you will need:

- **users**: id, name, email, phone, password_hash, role (CUSTOMER/RESTAURANT/DELIVERY/ADMIN), status, created_at.
- **restaurants**: id, owner_user_id, name, address, cuisine_type, rating, min_order_value, is_active.
- **menu_items**: id, restaurant_id, name, description, price, category, is_available, image_url.
- **orders**: id, user_id, restaurant_id, total_amount, delivery_fee, tax_amount, payment_status, order_status, delivery_address, created_at.
- **order_items**: id, order_id, menu_item_id, quantity, price.
- **payments**: id, order_id, payment_method, payment_reference, amount, status, created_at.
- **deliveries**: id, order_id, delivery_person_id, status, assigned_at, delivered_at.
- **reviews**: id, order_id, restaurant_id, user_id, rating, comment, created_at.

### 7. How to Run (High-Level)

1. **Set up Java & Tomcat**  
   - Install JDK 8+ and Apache Tomcat 9+.

2. **Configure the Database**  
   - Create a database (e.g., `food_delivery_db`) in MySQL/PostgreSQL.  
   - Create tables as per the DB design.  
   - Update `db.properties` (URL, username, password).

3. **Build & Deploy**  
   - If using Maven, run `mvn clean package`.  
   - Deploy the generated WAR file to Tomcat’s `webapps` directory or configure directly in your IDE.

4. **Access the Application**  
   - Open `http://localhost:8080/food_delivery` in your browser.

### 8. Next Steps / Implementation Guide

- Implement core models (`User`, `Restaurant`, `MenuItem`, `Order`, etc.).
- Implement `UserDAO`, `RestaurantDAO`, `OrderDAO` using JDBC + connection pooling.
- Implement authentication (`AuthServlet`) and session handling.
- Build JSP views with a clean, mobile-friendly UI (Bootstrap or similar).
- Add order placement, payment handling (mock for Mobile Money if desired), and tracking flow.
- Add admin dashboard and reports using aggregate SQL queries.

You can now use this README as the main project specification and extend each module step by step.


