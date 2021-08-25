package com.example.mercadolibrechallenge.model;

public class Installments {
    int quantity;
    int amount;
    int rate;
    String currency_id;

    public Installments(int quantity, int amount, int rate, String currency_id) {
        this.quantity = quantity;
        this.amount = amount;
        this.rate = rate;
        this.currency_id = currency_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getAmount() {
        return amount;
    }

    public int getRate() {
        return rate;
    }

    public String getCurrency_id() {
        return currency_id;
    }
}
