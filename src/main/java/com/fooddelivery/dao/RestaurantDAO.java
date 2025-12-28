package com.fooddelivery.dao;

import com.fooddelivery.model.Restaurant;

import java.util.List;

public interface RestaurantDAO {

    List<Restaurant> findAllActive();

    Restaurant findById(int id);
    
    List<Restaurant> findTopRestaurants(int limit);
}


