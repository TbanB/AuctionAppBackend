package com.auctionappbackend.model;

import java.sql.Timestamp;

/**
 * Clase que representa una subasta en la aplicación.
 */
public class Auction {
    private int idAuction;
    private int idUser;
    private Product product;
    private double initialValue;
    private Double finalValue;
    private double goalValue;
    private Timestamp startTime;
    private Timestamp endTime;
    private boolean isActive;

    public Auction(int idAuction, int idUser, Product product, double initialValue, Double finalValue, double goalValue, Timestamp startTime, Timestamp endTime, boolean isActive) {
        this.idAuction = idAuction;
        this.idUser = idUser;
        this.product = product;
        this.initialValue = initialValue;
        this.finalValue = finalValue;
        this.goalValue = goalValue;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isActive = isActive;
    }

    // Getters and Setters
    public int getIdAuction() {
        return idAuction;
    }

    public void setIdAuction(int idAuction) {
        this.idAuction = idAuction;
    }
    
    public int getIdUser() {
    	return idUser;
    }
    
    public void setIdUser(int idUser) {
    	this.idUser = idUser;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public double getInitialValue() {
        return initialValue;
    }

    public void setInitialValue(double initialValue) {
        this.initialValue = initialValue;
    }

    public Double getFinalValue() {
        return finalValue;
    }

    public void setFinalValue(Double finalValue) {
        this.finalValue = finalValue;
    }

    public double getGoalValue() {
        return goalValue;
    }

    public void setGoalValue(double goalValue) {
        this.goalValue = goalValue;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
    
    public String toString() {
        return "idAuction=" + idAuction +
                ", product=" + product +
                ", initialValue=" + initialValue +
                ", finalValue=" + finalValue +
                ", goalValue=" + goalValue +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", isActive=" + isActive;
    }

}
