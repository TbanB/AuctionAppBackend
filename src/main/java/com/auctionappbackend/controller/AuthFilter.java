package com.auctionappbackend.controller;

import java.io.IOException;
import java.sql.SQLException;

import com.auctionappbackend.dao.LoginDAO;
import com.auctionappbackend.model.TokenDetail;
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

/**
 * Filtro de autenticación verifica el token y la configuración de CORS.
 */
@WebFilter("/*")
public class AuthFilter implements Filter {
    private LoginDAO loginDao;

    /**
     * Inicializa el filtro configurando el DAO de login.
     * 
     * @param filterConfig el objeto FilterConfig que contiene la configuración del filtro.
     * @throws ServletException si ocurre un error durante la inicialización.
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        loginDao = LoginDAO.getInstance();
    }

    /**
     * Filtra las solicitudes entrantes para aplicar políticas de CORS y verificar tokens de autenticación.
     * 
     * @param request el objeto ServletRequest que contiene la solicitud del cliente.
     * @param response el objeto ServletResponse que contiene la respuesta del servlet.
     * @param chain el objeto FilterChain para pasar la solicitud y la respuesta al siguiente filtro en la cadena.
     * @throws IOException si ocurre un error de entrada/salida.
     * @throws ServletException si ocurre un error del servlet.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // Soluciona problemas de CORS
        httpResponse.setHeader("Access-Control-Allow-Origin", "http://127.0.0.1:5500");
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");

        // Maneja solicitudes OPTIONS para CORS
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        String path = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();

        // Permitir solicitudes sin autenticación para ciertas rutas
        if ((path.equals("/AuctionAppBackend/users") && method.equals("POST"))
                || (path.equals("/AuctionAppBackend/users/") && method.equals("GET"))
                || (path.equals("/AuctionAppBackend/auctions")
                || path.startsWith("/AuctionAppBackend/auctions/category/")
                || path.startsWith("/AuctionAppBackend/auctions/user/")
                || path.startsWith("/AuctionAppBackend/auctions/active")
                || path.equals("/AuctionAppBackend/categories") && method.equals("GET"))
                || path.startsWith("/AuctionAppBackend/login")) {
            chain.doFilter(request, response);
            return;
        }

        // Verificación del token de autenticación
        String token = httpRequest.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            TokenDetail tokenDetail = Token.verifyToken(token);
            try {
                boolean validateToken = loginDao.validateToken(tokenDetail.getUserId(), token);
                if (tokenDetail.getUserId() > -1 && validateToken) {
                    chain.doFilter(request, response);
                } else if (tokenDetail.getUserId() > -1 && !validateToken) {
                    loginDao.revokeAllUserToken(tokenDetail.getUserId());
                    httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token, token revoked");
                } else if (tokenDetail.getUserId() == -2) {
                    httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token has expired, please log in again");
                } else {
                    httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
                }
            } catch (SQLException e) {
                httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Database error: " + e.getMessage());
            }
        } else {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication is required");
        }
    }
}
