package com.auctionappbackend.controller;

import java.io.IOException;

import com.auctionappbackend.dao.LoginDAO;
import com.auctionappbackend.utils.Token;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebFilter("/*")
public class AuthFilter implements Filter {
	private LoginDAO loginDao;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	    loginDao = new LoginDAO();
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpServletRequest httpRequest = (HttpServletRequest) request;
 
        // Soluciona problemas de CORS
        httpResponse.setHeader("Access-Control-Allow-Origin", "http://127.0.0.1:5500");
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");

        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }

	    String path = httpRequest.getRequestURI();
	    String method = httpRequest.getMethod();

	    // Permitir solicitudes de POST a /users para permitir la creación de nuevos usuarios
	    if (path.equals("/AuctionAppBackend/users") && method.equals("POST")) {
	        chain.doFilter(request, response);
	        return;
	    }

	    // Omitir verificación en ruta login
	    if (path.startsWith("/AuctionAppBackend/login")) {
	        chain.doFilter(request, response);
	        return;
	    }

	    // Aquí se continúa con la verificación del token como antes
	    String token = httpRequest.getHeader("Authorization");
	    if (token != null && token.startsWith("Bearer ")) {
	        token = token.substring(7);
	        int userId = Token.verifyToken(token);
	        boolean validateToken = loginDao.validateToken(userId, token);
	        if (userId > -1 && validateToken) {
	            chain.doFilter(request, response);
	        } else if (userId > -1 && !validateToken) {
	        	loginDao.revokeToken(userId, token);
	        	httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token, token revoked");
	        } else if (userId == -2) {
	        	httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token has expired, please log in again");
	        } else {
	        	httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
	        }
	    } else {
	    	httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication is required");
	    }
	}
}
