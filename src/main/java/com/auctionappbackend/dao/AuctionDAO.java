package com.auctionappbackend.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.auctionappbackend.config.DataBaseConnection;
import com.auctionappbackend.model.Auction;
import com.auctionappbackend.model.Product;

public class AuctionDAO {

    private static AuctionDAO instance = null;

    private AuctionDAO() {}

    public static AuctionDAO getInstance() {
        if (instance == null) {
            instance = new AuctionDAO();
        }
        return instance;
    }

    private Connection getConnection() throws SQLException {
        return DataBaseConnection.getConnection();
    }
    
    public Auction getAuctionById(int idAuction) throws SQLException {
        String sql = "SELECT a.idAuction, a.idUser, a.initialValue, a.finalValue, a.goalValue, a.startTime, a.endTime, a.isActive, " +
                     "p.idProduct, p.idCategory, p.prodName, p.prodDescription, p.year, c.catName " +
                     "FROM Auctions a " +
                     "JOIN Products p ON a.idProduct = p.idProduct " +
                     "JOIN Product_categories c ON p.idCategory = c.idCategory " +
                     "WHERE a.idAuction = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idAuction);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Product product = new Product(
                            rs.getInt("idProduct"),
                            rs.getInt("idCategory"),
                            rs.getString("prodName"),
                            rs.getString("prodDescription"),
                            rs.getInt("year"),
                            rs.getString("catName")
                    );

                    return new Auction(
                            rs.getInt("idAuction"),
                            rs.getInt("idUser"),
                            product,
                            rs.getDouble("initialValue"),
                            rs.getObject("finalValue") != null ? rs.getDouble("finalValue") : null,
                            rs.getDouble("goalValue"),
                            rs.getTimestamp("startTime"),
                            rs.getTimestamp("endTime"),
                            rs.getBoolean("isActive")
                    );
                }
            }
        }
        return null;
    }


    public List<Auction> getAllAuctions() throws SQLException {
        String sql = "SELECT a.idAuction, a.idUser, a.initialValue, a.finalValue, a.goalValue, a.startTime, a.endTime, a.isActive, " +
                     "p.idProduct, p.idCategory, p.prodName, p.prodDescription, p.year, c.catName " +
                     "FROM Auctions a " +
                     "JOIN Products p ON a.idProduct = p.idProduct " +
                     "JOIN Product_categories c ON p.idCategory = c.idCategory";
        
        List<Auction> auctions = new ArrayList<>();
        
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

                Auction auction = new Auction(
                        rs.getInt("idAuction"),
                        rs.getInt("idUser"),
                        product,
                        rs.getDouble("initialValue"),
                        rs.getObject("finalValue") != null ? rs.getDouble("finalValue") : null,
                        rs.getDouble("goalValue"),
                        rs.getTimestamp("startTime"),
                        rs.getTimestamp("endTime"),
                        rs.getBoolean("isActive")
                );

                auctions.add(auction);
            }
        }
        return auctions;
    }
    
    public List<Auction> getAuctionsByCategory(int categoryId) throws SQLException {
        String sql = "SELECT a.idAuction, a.idUser, a.initialValue, a.finalValue, a.goalValue, a.startTime, a.endTime, a.isActive, " +
                     "p.idProduct, p.idCategory, p.prodName, p.prodDescription, p.year, c.catName " +
                     "FROM Auctions a " +
                     "JOIN Products p ON a.idProduct = p.idProduct " +
                     "JOIN Product_categories c ON p.idCategory = c.idCategory " +
                     "WHERE p.idCategory = ?";
        
        List<Auction> auctions = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product(
                            rs.getInt("idProduct"),
                            rs.getInt("idCategory"),
                            rs.getString("prodName"),
                            rs.getString("prodDescription"),
                            rs.getInt("year"),
                            rs.getString("catName")
                    );

                    Auction auction = new Auction(
                            rs.getInt("idAuction"),
                            rs.getInt("idUser"),
                            product,
                            rs.getDouble("initialValue"),
                            rs.getObject("finalValue") != null ? rs.getDouble("finalValue") : null,
                            rs.getDouble("goalValue"),
                            rs.getTimestamp("startTime"),
                            rs.getTimestamp("endTime"),
                            rs.getBoolean("isActive")
                    );

                    auctions.add(auction);
                }
            }
        }
        return auctions;
    }

    public List<Auction> getActiveAuctions() throws SQLException {
        String sql = "SELECT a.idAuction, a.idUser, a.initialValue, a.finalValue, a.goalValue, a.startTime, a.endTime, a.isActive, " +
                     "p.idProduct, p.idCategory, p.prodName, p.prodDescription, p.year, c.catName " +
                     "FROM Auctions a " +
                     "JOIN Products p ON a.idProduct = p.idProduct " +
                     "JOIN Product_categories c ON p.idCategory = c.idCategory " +
                     "WHERE a.isActive = TRUE";
        
        List<Auction> activeAuctions = new ArrayList<>();
        
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

                Auction auction = new Auction(
                        rs.getInt("idAuction"),
                        rs.getInt("idUser"),
                        product,
                        rs.getDouble("initialValue"),
                        rs.getObject("finalValue") != null ? rs.getDouble("finalValue") : null,
                        rs.getDouble("goalValue"),
                        rs.getTimestamp("startTime"),
                        rs.getTimestamp("endTime"),
                        rs.getBoolean("isActive")
                );

                activeAuctions.add(auction);
            }
        }
        return activeAuctions;
    }

}
