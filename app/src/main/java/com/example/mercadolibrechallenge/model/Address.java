package com.example.mercadolibrechallenge.model;

public class Address {
    String state_name;
    String city_name;

    public Address(String state_name, String city_name) {
        this.state_name = state_name;
        this.city_name = city_name;
    }

    public String getState_name() {
        return state_name;
    }

    public String getCity_name() {
        return city_name;
    }
}
