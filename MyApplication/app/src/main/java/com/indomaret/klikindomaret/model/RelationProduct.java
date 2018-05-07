package com.indomaret.klikindomaret.model;

/**
 * Created by USER on 4/14/2016.
 */
public class RelationProduct {
    int productImage;
    String productName;
    String productPrice;

    public RelationProduct(int productImage, String productName, String productPrice){
        this.productImage = productImage;
        this.productName = productName;
        this.productPrice = productPrice;
    }

    public int getProductImage() {
        return productImage;
    }

    public void setProductImage(int productImage) {
        this.productImage = productImage;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }
}
