package com.auctionappbackend.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.auctionappbackend.dao.CategoryDAO;
import com.auctionappbackend.model.Category;
import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Gestiona las solicitudes relacionadas con las categorías.
 * Método para obtener todas las categorías.
 */
@WebServlet("/categories/*")
public class CategoryServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private CategoryDAO categoryDao;
    private Gson gson = new Gson();

    /**
     * Inicializa el servlet configurando el DAO de categorías.
     * 
     * @throws ServletException si ocurre un error durante la inicialización.
     */
    @Override
    public void init() throws ServletException {
        categoryDao = CategoryDAO.getInstance();
    }

    /**
     * Maneja las solicitudes GET para obtener todas las categorías.
     * 
     * @param req el objeto HttpServletRequest que contiene la solicitud del cliente.
     * @param resp el objeto HttpServletResponse que contiene la respuesta del servlet.
     * @throws ServletException si ocurre un error del servlet.
     * @throws IOException si ocurre un error de entrada/salida.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            List<Category> categoryList = categoryDao.getAllCategories();
            resp.getWriter().write(gson.toJson(categoryList));
        } catch (SQLException e) {
            throw new ServletException("Error accessing database", e);
        }
    }
}
