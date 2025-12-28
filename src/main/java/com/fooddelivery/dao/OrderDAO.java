package com.fooddelivery.dao;

import com.fooddelivery.model.Order;
import java.util.List;

public interface OrderDAO {
    List<Order> findAll();
    List<Order> findByUserId(int userId);
    List<Order> findByRestaurantId(int restaurantId);
    Order findById(int id);
    boolean create(Order order);
    int getTotalOrders();
    double getTotalRevenue();
    int getOrdersByStatus(String status);

    /**
     * Update the status of an order (e.g. NEW, PREPARING, DELIVERED, CANCELLED).
     */
    boolean updateStatus(int orderId, String status);
}

