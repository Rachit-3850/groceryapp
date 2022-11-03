package com.example.groceryapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
public class productFragment extends Fragment {
    String productID , title , price , category , discription , discountPrice , discountNote , discountAvailable;

    public String getProductID() {
        return productID;
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

    public productFragment(String productID, String title, String price, String category, String discription, String discountPrice, String discountNote, String discountAvailable) {
        this.productID = productID;
        this.title = title;
        this.price = price;
        this.category = category;
        this.discription = discription;
        this.discountPrice = discountPrice;
        this.discountNote = discountNote;
        this.discountAvailable = discountAvailable;
    }

    public productFragment() {

    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product, container, false);
    }
}
// map.put("productID",""+TimeStamp);
//         map.put("uid",""+firebaseAuth.getUid());
//         map.put("title",""+Title);
//         map.put("price",""+Price);
//         map.put("category",""+Category);
//         map.put("discription",""+Description);
//         map.put("icon",downloadImageUri);
//         map.put("discountPrice",""+DiscountPrice);
//         map.put("discountNote",""+DiscountNote);
//         map.put("discountAvailable",""+DiscountAvailable);
//         map.put("TimeStamp",""+ TimeStamp);