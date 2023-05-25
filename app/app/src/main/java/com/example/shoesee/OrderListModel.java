package com.example.shoesee;

public class OrderListModel {

    private String name;
    private  int price;

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public OrderListModel(String name, int price) {
        this.name = name;
        this.price = price;
    }
}
