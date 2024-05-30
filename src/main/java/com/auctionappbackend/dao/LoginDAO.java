package com.auctionappbackend.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.auctionappbackend.config.DataBaseConnection;

public class LoginDAO {

    private static LoginDAO instance = null;

    private LoginDAO() {}

    public static LoginDAO getInstance() {
        if (instance == null) {
        	instance = new LoginDAO();
        }
        return instance;
    }

    private Connection getConnection() throws SQLException {
        return DataBaseConnection.getConnection();
    }

    public boolean saveToken(int userId, String token) throws SQLException {
        String sql = "INSERT INTO Sessions (idUser, token, startTime, isRevoked) VALUES (?, ?, NOW(), 0)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, token);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public boolean revokeAllUserToken(int userId) throws SQLException {
        String sql = "UPDATE Sessions SET endTime = NOW(), isRevoked = 1 WHERE idUser = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public boolean validateToken(int userId, String token) throws SQLException {
        String sql = "SELECT token FROM Sessions WHERE idUser = ? AND token = ? AND endTime IS NULL AND isRevoked = 0";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, token);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}
