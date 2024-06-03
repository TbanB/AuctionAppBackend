package com.auctionappbackend.model;

/**
 * Clase que representa un producto en la aplicación de subastas.
 */
public class Product {

    private int idProduct;
    private int idCategory;
    private String prodName;
    private String prodDescription;
    private int year;
    private String catName;

    public Product(int idProduct, int idCategory, String prodName, String prodDescription, int year, String catName) {
        this.idProduct = idProduct;
        this.idCategory = idCategory;
        this.prodName = prodName;
        this.prodDescription = prodDescription;
        this.year = year;
        this.catName = catName;
    }

    public int getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(int idProduct) {
        this.idProduct = idProduct;
    }

    public int getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(int idCategory) {
        this.idCategory = idCategory;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public String getProdDescription() {
        return prodDescription;
    }

    public void setProdDescription(String prodDescription) {
        this.prodDescription = prodDescription;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }
    
    public String toString() {
    	return "idProduct: " + idProduct + '\'' +
    			 ", idCategory:'" + idCategory + '\'' +
                 ", prodName:'" + prodName + '\'' +
                 ", prodDescription=" + prodDescription +
                 ", year='" + year + '\'' +
                 ", catName='" + catName;
    }
}
