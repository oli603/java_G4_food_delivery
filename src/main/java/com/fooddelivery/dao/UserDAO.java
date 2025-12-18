package com.fooddelivery.dao;

import com.fooddelivery.model.User;

public interface UserDAO {

    User findByEmailOrPhone(String emailOrPhone);

    User findById(int id);

    boolean create(User user);
}


