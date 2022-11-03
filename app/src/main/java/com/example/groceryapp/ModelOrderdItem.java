package com.example.groceryapp;

public class ModelOrderdItem {
    String title , totalPrice , price , quantity,pid;

    public ModelOrderdItem(String title, String totalPrice, String price, String quantity) {
        this.title = title;
        this.totalPrice = totalPrice;
        this.price = price;
        this.quantity = quantity;
    }

    public ModelOrderdItem(String title, String totalPrice, String price, String quantity, String pid) {
        this.title = title;
        this.totalPrice = totalPrice;
        this.price = price;
        this.quantity = quantity;
        this.pid = pid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
