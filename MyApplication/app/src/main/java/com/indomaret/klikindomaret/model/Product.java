package com.indomaret.klikindomaret.model;

/**
 * Created by USER on 4/11/2016.
 */
public class Product {

    String productName;
    int productPrice;
    int productInitialPrice;
    String sendFrom;
    int productDiscount;
    int productInstallment;

    public Product(String productName, int productPrice, int productInitialPrice, String sendFrom, int productDiscount, int productInstallment){
        this.productName = productName;
        this.productPrice = productPrice;
        this.productInitialPrice = productInitialPrice;
        this.sendFrom = sendFrom;
        this.productDiscount = productDiscount;
        this.productInstallment = productInstallment;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(int productPrice) {
        this.productPrice = productPrice;
    }

    public int getProductInitialPrice() {
        return productInitialPrice;
    }

    public void setProductInitialPrice(int productInitialPrice) {
        this.productInitialPrice = productInitialPrice;
    }

    public String getSendFrom() {
        return sendFrom;
    }

    public void setSendFrom(String sendFrom) {
        this.sendFrom = sendFrom;
    }

    public int getProductDiscount() {
        return productDiscount;
    }

    public void setProductDiscount(int productDiscount) {
        this.productDiscount = productDiscount;
    }

    public int getProductInstallment() {
        return productInstallment;
    }

    public void setProductInstallment(int productInstallment) {
        this.productInstallment = productInstallment;
    }
}
