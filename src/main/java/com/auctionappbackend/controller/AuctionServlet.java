package com.auctionappbackend.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.auctionappbackend.dao.AuctionDAO;
import com.auctionappbackend.model.Auction;
import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/auctions/*")
public class AuctionServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private AuctionDAO auctionDao;
    private Gson gson = new Gson();

    @Override
    public void init() throws ServletException {
        auctionDao = AuctionDAO.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // Obtener la lista de todas las subastas
                List<Auction> auctionList = auctionDao.getAllAuctions();
                resp.getWriter().write(gson.toJson(auctionList));
            } else if (pathInfo.startsWith("/category/")) {
                // Obtener subastas por categoría
                try {
                    int categoryId = Integer.parseInt(pathInfo.substring(10));
                    List<Auction> auctionList = auctionDao.getAuctionsByCategory(categoryId);
                    resp.getWriter().write(gson.toJson(auctionList));
                } catch (NumberFormatException e) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write(gson.toJson("Invalid category ID"));
                }
            } else if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/active")) {
                    List<Auction> activeAuctions = auctionDao.getActiveAuctions();
                    resp.getWriter().write(gson.toJson(activeAuctions));
            } else {
                // Obtener una subasta por su Id
                try {
                    int id = Integer.parseInt(pathInfo.substring(1));
                    Auction auction = auctionDao.getAuctionById(id);
                    if (auction != null) {
                        resp.getWriter().write(gson.toJson(auction));
                    } else {
                        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        resp.getWriter().write(gson.toJson("Auction not found"));
                    }
                } catch (NumberFormatException e) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write(gson.toJson("Invalid auction ID"));
                }
            }
        } catch (SQLException e) {
            throw new ServletException("Error accessing database", e);
        }
    }
}