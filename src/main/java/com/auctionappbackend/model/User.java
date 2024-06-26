package com.auctionappbackend.model;

import java.util.Date;

/**
 * Clase que representa un usuario en la aplicación.
 */
public class User {
    private int idUser;
    private String name;
    private String surname;
    private Date birthday;
    private String address;
    private String country;
    private String description;
    private boolean isAdmin;
    private boolean isStore;
    private Login loginDetails;

    public User() {
    }

    public User(int idUser, String name, String surname, Date birthday, String address, String country, String description, boolean isAdmin, boolean isStore, Login loginDetails) {
    	this.idUser = idUser;
        this.name = name;
        this.surname = surname;
        this.birthday = birthday;
        this.address = address;
        this.country = country;
        this.description = description;
        this.isAdmin = isAdmin;
        this.isStore = isStore;
        this.loginDetails = loginDetails;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean isStore() {
        return isStore;
    }

    public void setStore(boolean store) {
        isStore = store;
    }

    public Login getLoginDetails() {
        return loginDetails;
    }

    public void setLoginDetails(Login loginDetails) {
        this.loginDetails = loginDetails;
    }
    
    public String getRole() {
    	return (isAdmin) ? "Admin" : "SellerBuyer";
    } 

    @Override
    public String toString() {
        return "idUser=" + idUser +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", birthday=" + birthday +
                ", address='" + address + '\'' +
                ", country='" + country + '\'' +
                ", description='" + description + '\'' +
                ", isAdmin=" + isAdmin +
                ", isStore=" + isStore +
                ", loginDetails= " + loginDetails;
    }
}
