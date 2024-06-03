package com.auctionappbackend.model;

/**
 * Clase que representa la respuesta del servidor al intentar iniciar sesión.
 */
public class LoginResponse {
    private int userId;
    private String token;

    public LoginResponse(int userId, String token) {
        this.userId = userId;
        this.token = token;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
