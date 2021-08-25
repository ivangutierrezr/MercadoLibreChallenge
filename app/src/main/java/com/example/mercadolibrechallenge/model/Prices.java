package com.example.mercadolibrechallenge.model;

public class Prices {
    int amount;
    int regular_amount;

    public Prices(int amount, int regular_amount) {
        this.amount = amount;
        this.regular_amount = regular_amount;
    }

    public int getAmount() {
        return amount;
    }

    public int getRegular_amount() {
        return regular_amount;
    }
}
