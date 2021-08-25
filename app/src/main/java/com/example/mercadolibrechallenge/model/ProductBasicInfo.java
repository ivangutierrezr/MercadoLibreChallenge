package com.example.mercadolibrechallenge.model;

import java.util.List;

public class ProductBasicInfo {
    String id;
    String title;
    int price;
    int available_quantity;
    String condition;
    String permalink;
    String thumbnail;
    Boolean accepts_mercadopago;
    Installments installments;
    Address address;
    List<Attribute> attributes;
    Prices prices;
    Boolean free_shipping;

    public ProductBasicInfo(String id, String title, int price, int available_quantity, String condition, String permalink, String thumbnail, Boolean accepts_mercadopago, Installments installments, Address address, List<Attribute> attributes, Prices prices, Boolean free_shipping) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.available_quantity = available_quantity;
        this.condition = condition;
        this.permalink = permalink;
        this.thumbnail = thumbnail;
        this.accepts_mercadopago = accepts_mercadopago;
        this.installments = installments;
        this.address = address;
        this.attributes = attributes;
        this.prices = prices;
        this.free_shipping = free_shipping;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getPrice() {
        return price;
    }

    public int getAvailable_quantity() {
        return available_quantity;
    }

    public String getCondition() {
        return condition;
    }

    public String getPermalink() {
        return permalink;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public Boolean getAccepts_mercadopago() {
        return accepts_mercadopago;
    }

    public Installments getInstallments() {
        return installments;
    }

    public Address getAddress() {
        return address;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public Prices getPrices() {
        return prices;
    }

    public Boolean getFree_shipping() {
        return free_shipping;
    }
}
