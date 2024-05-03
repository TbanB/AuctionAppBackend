package com.auctionappbackend.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.auctionappbackend.config.DataBaseConnection;
import com.auctionappbackend.model.User;
import com.google.gson.*;

public class UserDAO {

    private Gson gson = new Gson();

    // Método para crear un usuario
    public String createUser(User user) {
        String sql = "INSERT INTO Users (name, surname, birthday, address, country, description, isAdmin, isStore, idLogin) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DataBaseConnection.getConnection();
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
            if (result > 0) {
                return gson.toJson(new Message("User created successfully"));
            } else {
                return gson.toJson(new Message("User creation failed"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return gson.toJson(new Message("Error: " + e.getMessage()));
        }
    }

    // Método para obtener un usuario por su ID
    public String getUserById(int idUser) {
        String sql = "SELECT * FROM Users WHERE idUser = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUser);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
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
                return gson.toJson(user);
            } else {
                return gson.toJson(new Message("User not found"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return gson.toJson(new Message("Error: " + e.getMessage()));
        }
    }

    // Método para obtener la lista de todos los usuarios
    public String getUsersList() {
        String sql = "SELECT * FROM Users";
        List<User> users = new ArrayList<>();
        try (Connection conn = DataBaseConnection.getConnection();
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
            return gson.toJson(users);
        } catch (SQLException e) {
            e.printStackTrace();
            return gson.toJson(new Message("Error: " + e.getMessage()));
        }
    }

    // Clase interna para manejar mensajes
    class Message {
        String message;
        Message(String message) {
            this.message = message;
        }
    }
}
