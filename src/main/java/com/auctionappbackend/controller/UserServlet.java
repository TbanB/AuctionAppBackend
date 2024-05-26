package com.auctionappbackend.controller;

import com.auctionappbackend.dao.UserDAO;
import com.auctionappbackend.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.google.gson.Gson;

@WebServlet("/users/*")
public class UserServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private UserDAO userDao;
    private Gson gson = new Gson();

    @Override
    public void init() throws ServletException {
        try {
            userDao = UserDAO.getInstance();
        } catch (SQLException e) {
            throw new ServletException("Unable to initialize UserDAO", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = gson.fromJson(req.getReader(), User.class);
        String hashedPassword = user.getLoginDetails().hashPassword(user.getLoginDetails().getPassword());
        user.getLoginDetails().setPassword(hashedPassword);
        try {
            if (userDao.emailExists(user.getLoginDetails().getEmail())) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson("Failed to create user, email already exists"));
            } else {
                boolean result = userDao.createUser(user);
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");

                if (result) {
                    resp.setStatus(HttpServletResponse.SC_CREATED);
                    resp.getWriter().write(gson.toJson("User created successfully"));
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write(gson.toJson("Failed to create user"));
                }
            }
        } catch (SQLException e) {
            throw new ServletException("Error accessing database", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // Obtener la lista de todos los usuarios
                List<User> userList = userDao.getAllUsers();
                resp.getWriter().write(gson.toJson(userList));
            } else {
                // Obtener un usuario por su Id
                try {
                    int id = Integer.parseInt(pathInfo.substring(1));
                    User user = userDao.getUserById(id);
                    if (user != null) {
                        resp.getWriter().write(gson.toJson(user));
                    } else {
                        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        resp.getWriter().write(gson.toJson("User not found"));
                    }
                } catch (NumberFormatException e) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write(gson.toJson("Invalid user ID"));
                }
            }
        } catch (SQLException e) {
            throw new ServletException("Error accessing database", e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if (pathInfo != null && !pathInfo.equals("/")) {
            try {
                int id = Integer.parseInt(pathInfo.substring(1));
                User user = gson.fromJson(req.getReader(), User.class);
                user.setIdUser(id);
                boolean result = userDao.updateUser(user);
                if (result) {
                    resp.getWriter().write(gson.toJson("User updated successfully"));
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write(gson.toJson("User not found"));
                }
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson("Invalid user ID"));
            } catch (SQLException e) {
                throw new ServletException("Error accessing database", e);
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson("User ID is required"));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if (pathInfo != null && !pathInfo.equals("/")) {
            try {
                int id = Integer.parseInt(pathInfo.substring(1));
                boolean result = userDao.deleteUser(id);
                if (result) {
                    resp.getWriter().write(gson.toJson("User deleted successfully"));
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write(gson.toJson("User not found"));
                }
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson("Invalid user ID"));
            } catch (SQLException e) {
                throw new ServletException("Error accessing database", e);
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson("User ID is required"));
        }
    }
}
