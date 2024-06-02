package com.auctionappbackend.model;

public class Category {

    private int idCategory;
    private String catName;

    public Category(int idCategory, String catName) {
        this.idCategory = idCategory;
        this.catName = catName;
    }

    public int getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(int idCategory) {
        this.idCategory = idCategory;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }
    
    public String toString() {
    	return "idCategory = " + idCategory + '\'' +
    			"catName = " + catName;
    }
}
