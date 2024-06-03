package com.auctionappbackend.controller;

import com.auctionappbackend.dao.UserDAO;
import com.auctionappbackend.model.TokenDetail;
import com.auctionappbackend.model.User;
import com.auctionappbackend.utils.Token;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.google.gson.Gson;

/**
 * Gestiona las solicitudes relacionadas con los usuarios.
 * Métodos para crear, obtener, actualizar y eliminar usuarios.
 */
@WebServlet("/users/*")
public class UserServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private UserDAO userDao;
    private Gson gson = new Gson();

    /**
     * Inicializa el servlet configurando el DAO de usuarios.
     */
    @Override
    public void init() {
        userDao = UserDAO.getInstance();
    }

    /**
     * Maneja las solicitudes POST para crear un nuevo usuario.
     * 
     * @param req el objeto HttpServletRequest que contiene la solicitud del cliente.
     * @param resp el objeto HttpServletResponse que contiene la respuesta del servlet.
     * @throws ServletException si ocurre un error del servlet.
     * @throws IOException si ocurre un error de entrada/salida.
     */
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

    /**
     * Maneja las solicitudes GET para obtener usuarios.
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

    /**
     * Maneja las solicitudes PUT para actualizar un usuario.
     * 
     * @param req el objeto HttpServletRequest que contiene la solicitud del cliente.
     * @param resp el objeto HttpServletResponse que contiene la respuesta del servlet.
     * @throws ServletException si ocurre un error del servlet.
     * @throws IOException si ocurre un error de entrada/salida.
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if (pathInfo != null && !pathInfo.equals("/")) {
            String token = req.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                TokenDetail tokenDetails = Token.verifyToken(token);

                try {
                    int userIdToUpdate = Integer.parseInt(pathInfo.substring(1));
                    int userIdFromToken = tokenDetails.getUserId();
                    String roleFromToken = tokenDetails.getRole();

                    if (userIdFromToken == userIdToUpdate || "Admin".equals(roleFromToken)) {
                        User user = gson.fromJson(req.getReader(), User.class);
                        user.setIdUser(userIdToUpdate);
                        boolean result = userDao.updateUser(user);
                        if (result) {
                            resp.getWriter().write(gson.toJson("User updated successfully"));
                        } else {
                            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                            resp.getWriter().write(gson.toJson("User not found"));
                        }
                    } else {
                        resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        resp.getWriter().write(gson.toJson("You do not have permission to update this user"));
                    }
                } catch (NumberFormatException e) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write(gson.toJson("Invalid user ID"));
                } catch (SQLException e) {
                    throw new ServletException("Error accessing database", e);
                }
            } else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write(gson.toJson("Authentication is required"));
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson("User ID is required"));
        }
    }


    
    /**
     * Maneja las solicitudes DELETE para eliminar un usuario.
     * 
     * @param req el objeto HttpServletRequest que contiene la solicitud del cliente.
     * @param resp el objeto HttpServletResponse que contiene la respuesta del servlet.
     * @throws ServletException si ocurre un error del servlet.
     * @throws IOException si ocurre un error de entrada/salida.
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if (pathInfo != null && !pathInfo.equals("/")) {
            String token = req.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                TokenDetail tokenDetails = Token.verifyToken(token);

                try {
                    int userIdToDelete = Integer.parseInt(pathInfo.substring(1));
                    int userIdFromToken = tokenDetails.getUserId();
                    String roleFromToken = tokenDetails.getRole();

                    if (userIdFromToken == userIdToDelete || "Admin".equals(roleFromToken)) {
                        boolean result = userDao.deleteUser(userIdToDelete);
                        if (result) {
                            resp.getWriter().write(gson.toJson("User deleted successfully"));
                        } else {
                            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                            resp.getWriter().write(gson.toJson("User not found"));
                        }
                    } else {
                        resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        resp.getWriter().write(gson.toJson("You do not have permission to delete this user"));
                    }
                } catch (NumberFormatException e) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write(gson.toJson("Invalid user ID"));
                } catch (SQLException e) {
                    throw new ServletException("Error accessing database", e);
                }
            } else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write(gson.toJson("Authentication is required"));
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson("User ID is required"));
        }
    }
}
