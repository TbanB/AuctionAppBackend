package com.auctionappbackend.dao;

import com.auctionappbackend.config.DataBaseConnection;
import com.auctionappbackend.model.Login;
import com.auctionappbackend.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) para la clase User.
 * Proporciona métodos para interactuar con la base de datos de usuarios.
 */
public class UserDAO {

    private static UserDAO instance = null;

    private UserDAO() {}

    /**
     * Obtiene la instancia única de UserDAO.
     * 
     * @return la instancia de UserDAO.
     */
    public static UserDAO getInstance() {
        if (instance == null) {
        	instance = new UserDAO();
        }
        return instance;
    }

    private Connection getConnection() throws SQLException {
        return DataBaseConnection.getConnection();
    }

    /**
     * Verifica si un correo electrónico ya está registrado en la base de datos.
     * 
     * @param email el correo electrónico a verificar.
     * @return true si el correo electrónico ya existe, false en caso contrario.
     * @throws SQLException si ocurre un error al acceder a la base de datos.
     */
    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM Login_details WHERE email = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Crea un nuevo usuario en la base de datos.
     * 
     * @param user el objeto User que contiene los datos del usuario.
     * @return true si el usuario fue creado exitosamente, false en caso contrario.
     * @throws SQLException si ocurre un error al acceder a la base de datos.
     */
    public boolean createUser(User user) throws SQLException {
        String sqlLogin = "INSERT INTO Login_details (email, passwordHash) VALUES (?, ?)";
        String sqlUser = "INSERT INTO Users (name, surname, birthday, address, country, description, isAdmin, isStore, idLogin) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmtLogin = conn.prepareStatement(sqlLogin, Statement.RETURN_GENERATED_KEYS)) {
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

                try (PreparedStatement stmtUser = conn.prepareStatement(sqlUser)) {
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
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    /**
     * Obtiene un usuario por su ID.
     * 
     * @param idUser el ID del usuario.
     * @return el objeto User con los datos del usuario, o null si no se encuentra.
     * @throws SQLException si ocurre un error al acceder a la base de datos.
     */
    public User getUserById(int idUser) throws SQLException {
        String sql = "SELECT u.idUser, u.name, u.surname, u.birthday, u.address, u.country, u.description, u.isAdmin, u.isStore, l.idLogin, l.email, l.passwordHash " +
                "FROM Users u JOIN Login_details l ON u.idLogin = l.idLogin WHERE u.idUser = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUser);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Login loginDetails = new Login(
                            rs.getInt("idLogin"),
                            rs.getString("email"),
                            rs.getString("passwordHash")
                    );

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
            }
        }
        return null;
    }

    /**
     * Obtiene un usuario por su correo electrónico.
     * 
     * @param email el correo electrónico del usuario.
     * @return el objeto User con los datos del usuario, o null si no se encuentra.
     * @throws SQLException si ocurre un error al acceder a la base de datos.
     */
    public User getUserByEmail(String email) throws SQLException {
        String sql = "SELECT u.idUser, u.name, u.surname, u.birthday, u.address, u.country, u.description, u.isAdmin, u.isStore, l.idLogin, l.email, l.passwordHash " +
                "FROM Users u JOIN Login_details l ON u.idLogin = l.idLogin WHERE l.email = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
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
            }
        }
        return null;
    }

    /**
     * Obtiene todos los usuarios de la base de datos.
     * 
     * @return una lista de todos los usuarios.
     * @throws SQLException si ocurre un error al acceder a la base de datos.
     */
    public List<User> getAllUsers() throws SQLException {
        String sql = "SELECT u.idUser, u.name, u.surname, u.birthday, u.address, u.country, u.description, u.isAdmin, u.isStore, l.idLogin, l.email, l.passwordHash " +
                "FROM Users u JOIN Login_details l ON u.idLogin = l.idLogin";
        List<User> users = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Login loginDetails = new Login(
                        rs.getInt("idLogin"),
                        rs.getString("email"),
                        rs.getString("passwordHash")
                );

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
        }
        return users;
    }

    /**
     * Actualiza los datos de un usuario en la base de datos.
     * 
     * @param user el objeto User que contiene los nuevos datos del usuario.
     * @return true si el usuario fue actualizado exitosamente, false en caso contrario.
     * @throws SQLException si ocurre un error al acceder a la base de datos.
     */
    public boolean updateUser(User user) throws SQLException {
        String sql = "UPDATE Users SET name = ?, surname = ?, birthday = ?, address = ?, country = ?, description = ?, isAdmin = ?, isStore = ? WHERE idUser = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
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
        }
    }

    /**
     * Elimina un usuario de la base de datos.
     * 
     * @param idUser el ID del usuario a eliminar.
     * @return true si el usuario fue eliminado exitosamente, false en caso contrario.
     * @throws SQLException si ocurre un error al acceder a la base de datos.
     */
    public boolean deleteUser(int idUser) throws SQLException {
        String sqlSession = "DELETE FROM Sessions WHERE idUser = ?";
        String sqlUser = "DELETE FROM Users WHERE idUser = ?";
        String sqlLogin = "DELETE FROM Login_details WHERE idLogin = ?";
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            // Obtener el ID de login asociado al usuario
            int idLogin = 0;
            String sqlGetLoginId = "SELECT idLogin FROM Users WHERE idUser = ?";
            try (PreparedStatement stmtGetLoginId = conn.prepareStatement(sqlGetLoginId)) {
                stmtGetLoginId.setInt(1, idUser);
                try (ResultSet rs = stmtGetLoginId.executeQuery()) {
                    if (rs.next()) {
                        idLogin = rs.getInt("idLogin");
                    }
                }
            }

            try (PreparedStatement stmtSession = conn.prepareStatement(sqlSession);
                 PreparedStatement stmtUser = conn.prepareStatement(sqlUser);
                 PreparedStatement stmtLogin = conn.prepareStatement(sqlLogin)) {

                // Eliminar la session del usuario
                stmtSession.setInt(1, idUser);
                int resultSession = stmtSession.executeUpdate();

                // Eliminar el usuario
                stmtUser.setInt(1, idUser);
                int resultUser = stmtUser.executeUpdate();

                // Eliminar el login asociado
                stmtLogin.setInt(1, idLogin);
                int resultLogin = stmtLogin.executeUpdate();

                conn.commit(); // Confirmar la transacción
                return resultSession > 0 && resultUser > 0 && resultLogin > 0;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
}
