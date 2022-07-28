package com.abstact.revisisister.Model;

public class Cart {
    private int id;
    private String name, quantity, price;

    public Cart(int id, String name, String quantity, String price) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String setName(String name) {
        this.name = name;
        return name;
    }

    public String getQuantity() {
        return quantity;
    }

    public String setQuantity(String quantity) {
        this.quantity = quantity;
        return quantity;
    }

    public String getPrice() {
        return price;
    }

    public String setPrice(String price) {
        this.price = price;
        return price;
    }

}
