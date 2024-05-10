package com.auctionappbackend.model;

import com.auctionappbackend.utils.Password;

public class Login {
    private int idLogin;
    private String email;
    private String password;

    public Login() {
    }

    public Login(int idLogin, String email, String password) {
        this.idLogin = idLogin;
        this.email = email;
        setPassword(password); // Cifrar la contraseña
    }

    public int getIdLogin() {
        return idLogin;
    }

    public void setIdLogin(int idLogin) {
        this.idLogin = idLogin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return password;
    }

    /**
     * Cifra la contraseña usando BCrypt y la guarda.
     * 
     * @param password La contraseña en texto plano
     */
    public void setPassword(String password) {
        this.password = Password.hashPassword(password);
    }

    /**
     * Verifica si una contraseña coincide con el hash almacenado.
     * 
     * @param password La contraseña en texto plano
     * @return true si coinciden, false en caso contrario
     */
    public boolean verifyPassword(String password) {
        return Password.verifyPassword(password, this.password);
    }

    @Override
    public String toString() {
        return "idLogin=" + idLogin +
                ", email='" + email + '\'' +
                ", passwordHash='" + password;
    }
}
