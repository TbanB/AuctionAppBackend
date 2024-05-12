package com.auctionappbackend.controller;

import com.auctionappbackend.dao.LoginDAO;
import com.auctionappbackend.dao.UserDAO;
import com.auctionappbackend.model.Login;
import com.auctionappbackend.model.User;
import com.auctionappbackend.utils.Token;

import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private UserDAO userDao;
	private LoginDAO loginDao;
    private Gson gson = new Gson();

    @Override
    public void init() throws ServletException {
        userDao = new UserDAO();
        loginDao = new LoginDAO();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            Login loginRequest = gson.fromJson(req.getReader(), Login.class);
            User user = userDao.getUserByEmail(loginRequest.getEmail());

            if (user != null && user.getLoginDetails().verifyPassword(loginRequest.getPassword())) {
                String token = Token.generateToken(user.getIdUser(), user.getRole(), user.getName());
                loginDao.saveToken(user.getIdUser(), token);
                System.out.print(">>> Accedemos a la app");
                // Devolver el token al usuario
                resp.getWriter().write(gson.toJson(token));
            } else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write(gson.toJson("Invalid email or password"));
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson("An error occurred: " + e.getMessage()));
        }
    }
}
