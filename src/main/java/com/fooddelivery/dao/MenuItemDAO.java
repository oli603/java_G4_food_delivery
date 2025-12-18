package com.fooddelivery.dao;

import com.fooddelivery.model.MenuItem;

import java.util.List;

public interface MenuItemDAO {

    List<MenuItem> findByRestaurantId(int restaurantId);
}


