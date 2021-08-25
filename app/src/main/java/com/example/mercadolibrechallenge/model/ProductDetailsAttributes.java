package com.example.mercadolibrechallenge.model;

public class ProductDetailsAttributes {
    String name;
    String value_name;

    public ProductDetailsAttributes(String name, String value_name) {
        this.name = name;
        this.value_name = value_name;
    }

    public String getName() {
        return name;
    }

    public String getValue_name() {
        return value_name;
    }
}
