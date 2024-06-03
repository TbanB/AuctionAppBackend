package com.auctionappbackend.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.auctionappbackend.config.DataBaseConnection;
import com.auctionappbackend.model.Category;

/**
 * DAO (Data Access Object) para la clase Category.
 * Proporciona métodos para interactuar con la base de datos de categorías.
 */
public class CategoryDAO {
    private static CategoryDAO instance = null;

    private CategoryDAO() {}

    /**
     * Obtiene la instancia única de CategoryDAO.
     * 
     * @return la instancia de CategoryDAO.
     */
    public static CategoryDAO getInstance() {
        if (instance == null) {
            instance = new CategoryDAO();
        }
        return instance;
    }

    private Connection getConnection() throws SQLException {
        return DataBaseConnection.getConnection();
    }

    /**
     * Obtiene todas las categorías de la base de datos.
     * 
     * @return una lista de todas las categorías.
     * @throws SQLException si ocurre un error al acceder a la base de datos.
     */
    public List<Category> getAllCategories() throws SQLException {
        String sql = "SELECT idCategory, catName FROM Product_categories";
        List<Category> categories = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Category category = new Category(
                        rs.getInt("idCategory"),
                        rs.getString("catName")
                );
                categories.add(category);
            }
        }
        return categories;
    }
	
}
