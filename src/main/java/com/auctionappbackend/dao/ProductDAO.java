package com.auctionappbackend.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.auctionappbackend.config.DataBaseConnection;
import com.auctionappbackend.model.Product;

/**
 * DAO (Data Access Object) para la clase Product.
 * Proporciona métodos para interactuar con la base de datos de productos.
 */
public class ProductDAO {

	private static ProductDAO instance = null;
	
	private ProductDAO() {}

    /**
     * Obtiene la instancia única de ProductDAO.
     * 
     * @return la instancia de ProductDAO.
     */
	public static ProductDAO getInstance() {
		if (instance == null) {
			instance = new ProductDAO();
		}
		return instance;
	}

    private Connection getConnection() throws SQLException {
        return DataBaseConnection.getConnection();
    }

    /**
     * Obtiene todos los productos de la base de datos.
     * 
     * @return una lista de todos los productos.
     * @throws SQLException si ocurre un error al acceder a la base de datos.
     */
    public List<Product> getAllProducts() throws SQLException {
        String sql = "SELECT p.idProduct, p.idCategory, p.prodName, p.prodDescription, p.year, c.catName " +
                     "FROM Products p JOIN Product_categories c ON p.idCategory = c.idCategory";
        List<Product> products = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("idProduct"),
                        rs.getInt("idCategory"),
                        rs.getString("prodName"),
                        rs.getString("prodDescription"),
                        rs.getInt("year"),
                        rs.getString("catName")
                );
                products.add(product);
            }
        }
        return products;
    }
}
