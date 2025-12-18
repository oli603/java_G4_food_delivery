package com.fooddelivery.dao.impl;

import com.fooddelivery.dao.RestaurantDAO;
import com.fooddelivery.model.Restaurant;
import com.fooddelivery.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RestaurantDAOImpl implements RestaurantDAO {

    @Override
    public List<Restaurant> findAllActive() {
        List<Restaurant> list = new ArrayList<>();
        String sql = "SELECT * FROM restaurants WHERE is_active = 1";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Restaurant findById(int id) {
        String sql = "SELECT * FROM restaurants WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Restaurant r = mapRow(rs);
                rs.close();
                return r;
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Restaurant mapRow(ResultSet rs) throws SQLException {
        Restaurant r = new Restaurant();
        r.setId(rs.getInt("id"));
        r.setOwnerUserId(rs.getInt("owner_user_id"));
        r.setName(rs.getString("name"));
        r.setAddress(rs.getString("address"));
        r.setCuisineType(rs.getString("cuisine_type"));
        r.setRating(rs.getDouble("rating"));
        r.setMinOrderValue(rs.getDouble("min_order_value"));
        r.setActive(rs.getBoolean("is_active"));
        return r;
    }
}


