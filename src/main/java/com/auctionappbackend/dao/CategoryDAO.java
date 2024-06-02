package com.auctionappbackend.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.auctionappbackend.config.DataBaseConnection;
import com.auctionappbackend.model.Category;

public class CategoryDAO {
    private static CategoryDAO instance = null;

    private CategoryDAO() {}

    public static CategoryDAO getInstance() {
        if (instance == null) {
            instance = new CategoryDAO();
        }
        return instance;
    }

    private Connection getConnection() throws SQLException {
        return DataBaseConnection.getConnection();
    }

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
