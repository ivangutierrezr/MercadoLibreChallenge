package com.example.mercadolibrechallenge.model;

public class Attribute {
    String value_name;
    String name;

    public Attribute(String value_name, String name) {
        this.value_name = value_name;
        this.name = name;
    }

    public String getValue_name() {
        return value_name;
    }

    public String getName() {
        return name;
    }
}
