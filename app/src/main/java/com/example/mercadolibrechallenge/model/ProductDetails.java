package com.example.mercadolibrechallenge.model;

import java.util.List;

public class ProductDetails {
    String title;
    int price;
    int base_price;
    int original_price;
    int available_quantity;
    List<String> pictures;
    Boolean accepts_mercadopago;
    Boolean free_shipping;
    List<ProductDetailsAttributes> productAttributes;

    public ProductDetails(String title, int price, int base_price, int original_price, int available_quantity, List<String> pictures, Boolean accepts_mercadopago, Boolean free_shipping, List<ProductDetailsAttributes> productAttributes) {
        this.title = title;
        this.price = price;
        this.base_price = base_price;
        this.original_price = original_price;
        this.available_quantity = available_quantity;
        this.pictures = pictures;
        this.accepts_mercadopago = accepts_mercadopago;
        this.free_shipping = free_shipping;
        this.productAttributes = productAttributes;
    }

    public String getTitle() {
        return title;
    }

    public int getPrice() {
        return price;
    }

    public int getBase_price() {
        return base_price;
    }
    public int getOriginal_price() {
        return original_price;
    }

    public int getAvailable_quantity() {
        return available_quantity;
    }

    public List<String> getPictures() {
        return pictures;
    }

    public Boolean getAccepts_mercadopago() {
        return accepts_mercadopago;
    }

    public Boolean getFree_shipping() {
        return free_shipping;
    }

    public List<ProductDetailsAttributes> getProductDetailsAttributes() {
        return productAttributes;
    }
}
