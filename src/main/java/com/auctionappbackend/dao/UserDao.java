package com.auctionappbackend.dao;

import com.auctionappbackend.model.User;
import com.auctionappbackend.config.DataSourceConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDao {
	
    // Método para crear un usuario
    public boolean createUser(User user) {
        String sql = "INSERT INTO Users (name, surname, birthday, address, country, description, isAdmin, isStore, idLogin) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DataSourceConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getSurname());
            stmt.setDate(3, new Date(user.getBirthday().getTime()));
            stmt.setString(4, user.getAddress());
            stmt.setString(5, user.getCountry());
            stmt.setString(6, user.getDescription());
            stmt.setBoolean(7, user.isAdmin());
            stmt.setBoolean(8, user.isStore());
            stmt.setInt(9, user.getIdLogin());
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Método para obtener todos los usuarios
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users";
        try (Connection conn = DataSourceConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                User user = new User(
                    rs.getInt("idUser"),
                    rs.getString("name"),
                    rs.getString("surname"),
                    rs.getDate("birthday"),
                    rs.getString("address"),
                    rs.getString("country"),
                    rs.getString("description"),
                    rs.getBoolean("isAdmin"),
                    rs.getBoolean("isStore"),
                    rs.getInt("idLogin")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
}