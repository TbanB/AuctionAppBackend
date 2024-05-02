package com.auctionappbackend.controller;

import com.auctionappbackend.dao.UserDao;
import com.auctionappbackend.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.text.SimpleDateFormat;

@WebServlet(name = "UserServlet", urlPatterns = {"/user"})
public class UserServlet extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private UserDao userDao;

    @Override
    public void init() {
        userDao = new UserDao(); // Inicializa UserDao para interactuar con la base de datos.
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        String action = request.getParameter("action");

        if ("create".equals(action)) {
            User user = new User();
            user.setName(request.getParameter("name"));
            user.setSurname(request.getParameter("surname"));
            try {
                user.setBirthday(new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd").parse(request.getParameter("birthday")).getTime()));
            } catch (Exception e) {
                response.getWriter().write("{\"error\": \"Invalid date format\"}");
                return;
            }
            user.setAddress(request.getParameter("address"));
            user.setCountry(request.getParameter("country"));
            user.setDescription(request.getParameter("description"));
            user.setAdmin(Boolean.parseBoolean(request.getParameter("isAdmin")));
            user.setStore(Boolean.parseBoolean(request.getParameter("isStore")));
            user.setIdLogin(Integer.parseInt(request.getParameter("idLogin")));
            
            String result = userDao.createUser(user);
            response.getWriter().write(result);
        } else {
            response.getWriter().write("{\"error\": \"Unsupported action\"}");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        String action = request.getParameter("action");
        if ("getById".equals(action)) {
            int id = Integer.parseInt(request.getParameter("idUser"));
            String result = userDao.getUserById(id);
            response.getWriter().write(result);
        } else if ("listAll".equals(action)) {
            String result = userDao.getUsersList();
            response.getWriter().write(result);
        } else {
            response.getWriter().write("{\"error\": \"Unsupported action\"}");
        }
    }
}
