package com.example.groceryapp;

public class ModelProduct {
    String productID , title , price , category , discription , discountPrice , discountNote , discountAvailable , uid , quantity,shopOpen , icon;

    public ModelProduct(String productID, String title, String price, String category, String discription, String discountPrice, String discountNote, String discountAvailable, String uid, String quantity, String shopOpen, String icon) {
        this.productID = productID;
        this.title = title;
        this.price = price;
        this.category = category;
        this.discription = discription;
        this.discountPrice = discountPrice;
        this.discountNote = discountNote;
        this.discountAvailable = discountAvailable;
        this.uid = uid;
        this.quantity = quantity;
        this.shopOpen = shopOpen;
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getProductID() {
        return productID;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDiscription() {
        return discription;
    }

    public void setDiscription(String discription) {
        this.discription = discription;
    }

    public String getDiscountPrice() {
        return discountPrice;
    }

    public String getShopOpen() {
        return shopOpen;
    }

    public void setShopOpen(String shopOpen) {
        this.shopOpen = shopOpen;
    }

    public void setDiscountPrice(String discountPrice) {
        this.discountPrice = discountPrice;
    }

    public String getDiscountNote() {
        return discountNote;
    }

    public void setDiscountNote(String discountNote) {
        this.discountNote = discountNote;
    }

    public String getDiscountAvailable() {
        return discountAvailable;
    }

    public void setDiscountAvailable(String discountAvailable) {
        this.discountAvailable = discountAvailable;
    }

    public ModelProduct() {

    }

    public ModelProduct(String productID, String title, String price,
                        String category, String discription,
                        String discountPrice, String discountNote,
                        String discountAvailable,String uid , String quantity , String shopOpen) {
        this.productID = productID;
        this.title = title;
        this.price = price;
        this.category = category;
        this.discription = discription;
        this.discountPrice = discountPrice;
        this.discountNote = discountNote;
        this.discountAvailable = discountAvailable;
        this.uid = uid;
        this.quantity = quantity;
        this.shopOpen = shopOpen;
    }
}
