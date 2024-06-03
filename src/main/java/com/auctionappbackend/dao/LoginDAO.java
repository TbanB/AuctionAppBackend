package com.auctionappbackend.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.auctionappbackend.config.DataBaseConnection;

/**
 * DAO (Data Access Object) para la clase Login.
 * Proporciona métodos para interactuar con la base de datos de sesiones y tokens.
 */
public class LoginDAO {

    private static LoginDAO instance = null;

    private LoginDAO() {}

    /**
     * Obtiene la instancia única de LoginDAO.
     * 
     * @return la instancia de LoginDAO.
     */
    public static LoginDAO getInstance() {
        if (instance == null) {
        	instance = new LoginDAO();
        }
        return instance;
    }

    private Connection getConnection() throws SQLException {
        return DataBaseConnection.getConnection();
    }

    /**
     * Guarda un token de sesión en la base de datos.
     * 
     * @param userId el ID del usuario.
     * @param token el token de sesión.
     * @return true si el token se guardó correctamente, false en caso contrario.
     * @throws SQLException si ocurre un error al acceder a la base de datos.
     */
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

    /**
     * Revoca todos los tokens de sesión de un usuario específico.
     * 
     * @param userId el ID del usuario.
     * @return true si los tokens se revocaron correctamente, false en caso contrario.
     * @throws SQLException si ocurre un error al acceder a la base de datos.
     */
    public boolean revokeAllUserToken(int userId) throws SQLException {
        String sql = "UPDATE Sessions SET endTime = NOW(), isRevoked = 1 WHERE idUser = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Valida un token de sesión para un usuario específico.
     * 
     * @param userId el ID del usuario.
     * @param token el token de sesión.
     * @return true si el token es válido, false en caso contrario.
     * @throws SQLException si ocurre un error al acceder a la base de datos.
     */
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
