package com.auctionappbackend.model;

/**
 * Clase que representa la respuesta del servidor al intentar iniciar sesión.
 */
public class LoginResponse {
    private int userId;
    private String role;
    private String token;

    public LoginResponse(int userId, String role, String token) {
        this.userId = userId;
        this.role = role;
        this.token = token;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRole() {
        return userId;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
