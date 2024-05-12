package com.auctionappbackend.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.auctionappbackend.config.DataBaseConnection;

public class LoginDAO {
	public boolean saveToken(int userId, String token) {
	    String sql = "INSERT INTO Sessions (idUser, token, startTime, isRevoked) VALUES (?, ?, NOW(), 0)";
	    Connection conn = null;
	    try {
	    	conn = DataBaseConnection.getConnection();
	        PreparedStatement stmt = conn.prepareStatement(sql);
	        stmt.setInt(1, userId);
	        stmt.setString(2, token);
	        int affectedRows = stmt.executeUpdate();
	        return affectedRows > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    } finally {
        	DataBaseConnection.closeConnection(conn);
        }
	}

	public boolean revokeToken(int userId, String token) {
	    String sql = "UPDATE Sessions SET endTime = NOW(), isRevoked = 1 WHERE idUser = ? AND token = ?";
	    Connection conn = null;
	    try {
	    	conn = DataBaseConnection.getConnection();
	        PreparedStatement stmt = conn.prepareStatement(sql);
	        stmt.setInt(1, userId);
	        stmt.setString(2, token);
	        int affectedRows = stmt.executeUpdate();
	        return affectedRows > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    } finally {
        	DataBaseConnection.closeConnection(conn);
        }
	}
	
	public boolean validateToken(int userId, String token) {
	    String sql = "SELECT token FROM Sessions WHERE idUser = ? AND token = ? AND endTime IS NULL AND isRevoked = 0";
	    try (Connection conn = DataBaseConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setInt(1, userId);
	        stmt.setString(2, token);
	        ResultSet rs = stmt.executeQuery();
	        return rs.next();
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
}
