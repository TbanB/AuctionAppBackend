package com.auctionappbackend.model;

/**
 * Clase que representa los detalles de un token de autenticación.
 */
public class TokenDetail {
    private int userId;
    private String role;

    public TokenDetail(int userId, String role) {
        this.userId = userId;
        this.role = role;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
