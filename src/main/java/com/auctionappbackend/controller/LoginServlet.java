package com.auctionappbackend.controller;

import com.auctionappbackend.dao.LoginDAO;
import com.auctionappbackend.dao.UserDAO;
import com.auctionappbackend.model.Login;
import com.auctionappbackend.model.LoginResponse;
import com.auctionappbackend.model.User;
import com.auctionappbackend.utils.Token;

import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Servlet que maneja las solicitudes de inicio de sesión y revocación de tokens.
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private UserDAO userDao;
    private LoginDAO loginDao;
    private Gson gson = new Gson();

    /**
     * Inicializa el servlet configurando los DAOs de usuario y login.
     * 
     * @throws ServletException si ocurre un error durante la inicialización.
     */
    @Override
    public void init() throws ServletException {
        userDao = UserDAO.getInstance();
        loginDao = LoginDAO.getInstance();
    }

    /**
     * Maneja las solicitudes POST para iniciar sesión.
     * 
     * @param req el objeto HttpServletRequest que contiene la solicitud del cliente.
     * @param resp el objeto HttpServletResponse que contiene la respuesta del servlet.
     * @throws ServletException si ocurre un error del servlet.
     * @throws IOException si ocurre un error de entrada/salida.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            Login loginRequest = gson.fromJson(req.getReader(), Login.class);
            User user = userDao.getUserByEmail(loginRequest.getEmail());
            System.out.print(user.toString());

            if (user != null && user.getLoginDetails().verifyPassword(loginRequest.getPassword())) {
                System.out.print(">>> Hay usuario y valida el password");
                loginDao.revokeAllUserToken(user.getIdUser()); // revocamos el token
                String token = Token.generateToken(user.getIdUser(), user.getRole(), user.getName());
                System.out.println("token: " + token);
                Boolean tokenCreated = loginDao.saveToken(user.getIdUser(), token); // guardamos el nuevo token
                System.out.println(">>> Accedemos a la app" + tokenCreated);
                // Devolver el token al usuario
                if (tokenCreated) {
                    LoginResponse loginResponse = new LoginResponse(user.getIdUser(), user.getRole(), token);
                    resp.getWriter().write(gson.toJson(loginResponse));
                } else {
                    System.out.println(">>> No se ha creado el token" + tokenCreated);
                    resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    resp.getWriter().write(gson.toJson("Token not created"));
                }
            } else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write(gson.toJson("Invalid email or password"));
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson("An error occurred: " + e.getMessage()));
        }
    }

    /**
     * Maneja las solicitudes PUT para revocar tokens de un usuario específico.
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
            try {
                int id = Integer.parseInt(pathInfo.substring(1));
                boolean result = loginDao.revokeAllUserToken(id);
                if (result) {
                    resp.getWriter().write(gson.toJson("Token revoked successfully"));
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write(gson.toJson("Problems to revoke token"));
                }
            } catch (SQLException e) {
                throw new ServletException("Error accessing database", e);
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson("User ID is required"));
        }
    }
}
