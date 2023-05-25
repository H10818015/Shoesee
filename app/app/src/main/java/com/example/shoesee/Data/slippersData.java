package com.example.shoesee.Data;

public class slippersData {
    private String brand, name, price, stock, id, type, gender, img;

    public slippersData() {
    }

    public slippersData(String brand, String name, String price, String stock, String id, String type, String gender, String img) {
        this.brand = brand;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.id = id;
        this.type = type;
        this.gender = gender;
        this.img = img;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
