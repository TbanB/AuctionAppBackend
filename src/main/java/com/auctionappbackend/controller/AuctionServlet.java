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

/**
 * Servlet que maneja las solicitudes relacionadas con las subastas.
 * Proporciona puntos finales para obtener todas las subastas, subastas por categoría, 
 * subastas activas y una subasta específica por su ID.
 */
@WebServlet("/auctions/*")
public class AuctionServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private AuctionDAO auctionDao;
    private Gson gson = new Gson();

    /**
     * Inicializa el servlet configurando el DAO de subastas.
     * 
     * @throws ServletException si ocurre un error durante la inicialización.
     */
    @Override
    public void init() throws ServletException {
        auctionDao = AuctionDAO.getInstance();
    }

    /**
     * Maneja las solicitudes GET para obtener subastas.
     * 
     * @param req el objeto HttpServletRequest que contiene la solicitud del cliente.
     * @param resp el objeto HttpServletResponse que contiene la respuesta del servlet.
     * @throws ServletException si ocurre un error del servlet.
     * @throws IOException si ocurre un error de entrada/salida.
     */
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
            } else if (pathInfo.equals("/active")) {
                // Obtener subastas activas
                List<Auction> activeAuctions = auctionDao.getActiveAuctions();
                resp.getWriter().write(gson.toJson(activeAuctions));
            } else if (pathInfo.startsWith("/user/")) {
                // Obtener subastas por usuario
                try {
                    int userId = Integer.parseInt(pathInfo.substring(6));
                    List<Auction> auctionList = auctionDao.getAuctionsByUserId(userId);
                    resp.getWriter().write(gson.toJson(auctionList));
                } catch (NumberFormatException e) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write(gson.toJson("Invalid user ID"));
                }
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
