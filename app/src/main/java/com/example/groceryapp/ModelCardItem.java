package com.example.groceryapp;

public class ModelCardItem {
    String id , pid , quantity , name , price , cost;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
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

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public ModelCardItem(String id, String pid, String quantity, String name, String price, String cost) {
        this.id = id;
        this.pid = pid;
        this.quantity = quantity;
        this.name = name;
        this.price = price;
        this.cost = cost;
    }
}
