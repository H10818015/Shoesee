package com.example.shoesee;

public class Cart1{
    private String pbrand, pname, price, psize, quantity,id;
    private int total;

    public Cart1() {
    }

    public Cart1(String pbrand, String pname, String price, String psize, String quantity, String id, int total) {
        this.pbrand = pbrand;
        this.pname = pname;
        this.price = price;
        this.psize = psize;
        this.quantity = quantity;
        this.id = id;
        this.total = total;
    }

    public String getPbrand() {
        return pbrand;
    }

    public void setPbrand(String pbrand) {
        this.pbrand = pbrand;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPsize() {
        return psize;
    }

    public void setPsize(String psize) {
        this.psize = psize;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}





