package com.example.groceryapp;

public class ModelShop {
    public String uid , fullName , email ,address , country , city , state , phoneNo , shopName , deleveryfee,accountType,online,shopOpen,icon;

    public ModelShop() {
    }

    public ModelShop(String uid, String fullName, String email, String address, String country, String city, String state, String phoneNo, String shopName, String deleveryfee, String accountType, String online, String shopOpen , String icon) {
        this.uid = uid;
        this.fullName = fullName;
        this.email = email;
        this.address = address;
        this.country = country;
        this.city = city;
        this.state = state;
        this.phoneNo = phoneNo;
        this.shopName = shopName;
        this.deleveryfee = deleveryfee;
        this.accountType = accountType;
        this.online = online;
        this.shopOpen = shopOpen;
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getDeleveryfee() {
        return deleveryfee;
    }

    public void setDeleveryfee(String deleveryfee) {
        this.deleveryfee = deleveryfee;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getShopOpen() {
        return shopOpen;
    }

    public void setShopOpen(String shopOpen) {
        this.shopOpen = shopOpen;
    }
}
//map.put("uid",""+firebaseAuth.getUid());
//        map.put("fullName",fullName);
//        map.put("Email",Email);
//        map.put("address",Adress);
//        map.put("Country",Country);
//        map.put("State",State);
//        map.put("City",City);
//        map.put("phoneNo",phoneNo);
//        map.put("shopName",shopName);
//        map.put("deleveryfee",deleveryfee);
//        map.put("accountType","seller");
//        map.put("online","true");
//        map.put("shop open","true");
