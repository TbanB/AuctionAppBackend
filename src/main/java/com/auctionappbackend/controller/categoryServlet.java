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

@WebServlet("/categories/*")
public class categoryServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private CategoryDAO categoryDao;
    private Gson gson = new Gson();

    @Override
    public void init() throws ServletException {
        categoryDao = CategoryDAO.getInstance();
    }

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