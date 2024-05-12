package com.auctionappbackend.dao;

import com.auctionappbackend.config.DataBaseConnection;
import com.auctionappbackend.model.Login;
import com.auctionappbackend.model.User;
import java.sql.*;

import java.util.ArrayList;
import java.util.List;

public class UserDAO {
	
	public boolean emailExists(String email) {
		String sql = "SELECT 1 FROM Login_details WHERE email = ?";
		Connection conn = null;
		try {
        	conn = DataBaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseConnection.closeConnection(conn);
        }
        return false;
	}

    // Crear usuario
    public boolean createUser(User user) {
        String sqlLogin = "INSERT INTO Login_details (email, passwordHash) VALUES (?, ?)";
        String sqlUser = "INSERT INTO Users (name, surname, birthday, address, country, description, isAdmin, isStore, idLogin) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmtLogin = null;
        PreparedStatement stmtUser = null;
        try {
            conn = DataBaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Insertar en tabla Login_details
            stmtLogin = conn.prepareStatement(sqlLogin, Statement.RETURN_GENERATED_KEYS);
            stmtLogin.setString(1, user.getLoginDetails().getEmail());
            stmtLogin.setString(2, user.getLoginDetails().getPassword());
            stmtLogin.executeUpdate();

            ResultSet generatedKeys = stmtLogin.getGeneratedKeys();
            int idLogin;
            if (generatedKeys.next()) {
                idLogin = generatedKeys.getInt(1);
            } else {
                throw new SQLException("Failed login data creation");
            }

            // Insertar en tabla Users
            stmtUser = conn.prepareStatement(sqlUser);
            stmtUser.setString(1, user.getName());
            stmtUser.setString(2, user.getSurname());
            stmtUser.setDate(3, new Date(user.getBirthday().getTime()));
            stmtUser.setString(4, user.getAddress());
            stmtUser.setString(5, user.getCountry());
            stmtUser.setString(6, user.getDescription());
            stmtUser.setBoolean(7, user.getIsAdmin());
            stmtUser.setBoolean(8, user.isStore());
            stmtUser.setInt(9, idLogin);

            int result = stmtUser.executeUpdate();
            conn.commit(); // Confirmar la transacción
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stmtLogin != null) stmtLogin.close();
                if (stmtUser != null) stmtUser.close();
                DataBaseConnection.closeConnection(conn);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

 // Método para obtener un usuario por su ID
    public User getUserById(int idUser) {
        String sql = "SELECT u.idUser, u.name, u.surname, u.birthday, u.address, u.country, u.description, u.isAdmin, u.isStore, l.idLogin, l.email, l.passwordHash " +
                "FROM Users u JOIN Login_details l ON u.idLogin = l.idLogin WHERE u.idUser = ?";
        Connection conn = null;
        try {
        	conn = DataBaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idUser);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Crear el objeto Login
                Login loginDetails = new Login(
                        rs.getInt("idLogin"),
                        rs.getString("email"),
                        rs.getString("passwordHash")
                );

                // Crear el objeto User
                return new User(
                        rs.getInt("idUser"),
                        rs.getString("name"),
                        rs.getString("surname"),
                        rs.getDate("birthday"),
                        rs.getString("address"),
                        rs.getString("country"),
                        rs.getString("description"),
                        rs.getBoolean("isAdmin"),
                        rs.getBoolean("isStore"),
                        loginDetails
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataBaseConnection.closeConnection(conn);
        }
        return null;
    }
    
    // Método para obtener un usuario por su email
    public User getUserByEmail(String email) {
        String sql = "SELECT u.idUser, u.name, u.surname, u.birthday, u.address, u.country, u.description, u.isAdmin, u.isStore, l.idLogin, l.email, l.passwordHash " +
                "FROM Users u JOIN Login_details l ON u.idLogin = l.idLogin WHERE l.email = ?";
        Connection conn = null;
        try {
        	conn = DataBaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
            	Login loginDetails = new Login(rs.getInt("idLogin"), rs.getString("email"), rs.getString("passwordHash"));
                return new User(
                    rs.getInt("idUser"),
                    rs.getString("name"),
                    rs.getString("surname"),
                    rs.getDate("birthday"),
                    rs.getString("address"),
                    rs.getString("country"),
                    rs.getString("description"),
                    rs.getBoolean("isAdmin"),
                    rs.getBoolean("isStore"),
                    loginDetails
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
        	DataBaseConnection.closeConnection(conn);
        }
        return null;
    }

    // Método para obtener un listado de todos los usuarios
    public List<User> getAllUsers() {
        String sql = "SELECT u.idUser, u.name, u.surname, u.birthday, u.address, u.country, u.description, u.isAdmin, u.isStore, l.idLogin, l.email, l.passwordHash " +
                "FROM Users u JOIN Login_details l ON u.idLogin = l.idLogin";
        List<User> users = new ArrayList<>();
        Connection conn = null;
        try {
        	conn = DataBaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Crear el objeto Login para cada usuario
                Login loginDetails = new Login(
                        rs.getInt("idLogin"),
                        rs.getString("email"),
                        rs.getString("passwordHash")
                );

                // Crear el objeto User con el Login asociado
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
                        loginDetails
                );

                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
        	DataBaseConnection.closeConnection(conn);
        }

        return users;
    }

    // Método para actualizar un usuario
    public boolean updateUser(User user) {
        String sql = "UPDATE Users SET name = ?, surname = ?, birthday = ?, address = ?, country = ?, description = ?, isAdmin = ?, isStore = ? WHERE idUser = ?";
        Connection conn = null;
        try {
        	conn = DataBaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getSurname());
            stmt.setDate(3, new Date(user.getBirthday().getTime()));
            stmt.setString(4, user.getAddress());
            stmt.setString(5, user.getCountry());
            stmt.setString(6, user.getDescription());
            stmt.setBoolean(7, user.getIsAdmin());
            stmt.setBoolean(8, user.isStore());
            stmt.setInt(9, user.getIdUser());

            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
        	DataBaseConnection.closeConnection(conn);
        }
    }


    // Método para eliminar un usuario
    public boolean deleteUser(int idUser) {
        String sqlSession = "DELETE FROM Sessions WHERE idUser = ?";
        String sqlUser = "DELETE FROM Users WHERE idUser = ?";
        String sqlLogin = "DELETE FROM Login_details WHERE idLogin = ?";
        Connection conn = null;
        PreparedStatement stmtUser = null;
        PreparedStatement stmtLogin = null;

        try {
            conn = DataBaseConnection.getConnection();
            conn.setAutoCommit(false); // Empezar una transacción

            // Obtener el ID de login asociado al usuario
            int idLogin = 0;
            String sqlGetLoginId = "SELECT idLogin FROM Users WHERE idUser = ?";
            try (PreparedStatement stmtGetLoginId = conn.prepareStatement(sqlGetLoginId)) {
                stmtGetLoginId.setInt(1, idUser);
                ResultSet rs = stmtGetLoginId.executeQuery();
                if (rs.next()) {
                    idLogin = rs.getInt("idLogin");
                }
            }

            // Eliminar la session del usuario
            stmtUser = conn.prepareStatement(sqlSession);
            stmtUser.setInt(1, idUser);
            int resultSession = stmtUser.executeUpdate();

            // Eliminar el usuario
            stmtUser = conn.prepareStatement(sqlUser);
            stmtUser.setInt(1, idUser);
            int resultUser = stmtUser.executeUpdate();

            // Eliminar el login asociado
            stmtLogin = conn.prepareStatement(sqlLogin);
            stmtLogin.setInt(1, idLogin);
            int resultLogin = stmtLogin.executeUpdate();

            conn.commit(); // Confirmar la transacción
            return resultSession > 0 && resultUser > 0 && resultLogin > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stmtLogin != null) stmtLogin.close();
                if (stmtUser != null) stmtUser.close();
                DataBaseConnection.closeConnection(conn);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
