package com.example.groceryapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class adapterRecycleUser extends RecyclerView.Adapter<adapterRecycleUser.holderShop> {
    private Context context;

    public adapterRecycleUser(Context context, ArrayList<ModelShop> shopList) {
        this.context = context;
        this.shopList = shopList;
    }

    public ArrayList<ModelShop> shopList;
//    private filterProduct filter;
    @NonNull
    @Override
    public holderShop onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.shop_design_card,parent,false);
        return new holderShop(view);
    }
    String shopUID;
    @Override
    public void onBindViewHolder(@NonNull holderShop holder, int position) {
        // get data
        ModelShop modelShop = shopList.get(position);
//
        String suid = modelShop.getUid();
        shopUID = suid;
//        String scity = modelShop.getCity();
//        String sstate = modelShop.getState();
//        String sdeleveryfee = modelShop.getDeleveryfee();
//        String saccountType = modelShop.getAccountType();
        String saddress = modelShop.getAddress();
//        String sfullName = modelShop.getFullName();
//        String semail = modelShop.getEmail();
//        String sonline = modelShop.getOnline();
        String sshopOpen = modelShop.getShopOpen();
        String sicon = modelShop.getIcon();
        String sshopName = modelShop.getShopName();
        String sphoneNo = modelShop.getPhoneNo();
        Log.d("user", "onBindViewHolder: "+sphoneNo);
        Log.d("user", "onBindViewHolder: "+sshopOpen);
        Log.d("user", "onBindViewHolder: "+sshopName);
        //set data

        holder.phoneNo.setText(sphoneNo);
        holder.address.setText(saddress);
        holder.title.setText(sshopName);
        setReview(holder);

        try {
            Picasso.get().load(sicon).placeholder(R.drawable.ic_baseline_store_24).into(holder.icon);
        }
        catch (Exception e) {
            holder.icon.setImageResource(R.drawable.ic_baseline_store_24);
        }

        if(sshopOpen.equals("true")) {
            holder.openClose.setVisibility(View.GONE);
        }
        else if(sshopOpen.equals("false")) {
            holder.openClose.setVisibility(View.VISIBLE);
        }
//        holder.icon.setImageResource(R.drawable.ic_baseline_store_24);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // show shop details
                Intent intent = new Intent(context , shopDetailsActivity.class);
                intent.putExtra("shopId",suid);
                context.startActivity(intent);
            }
        });
    }

    private void setReview(holderShop holder) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUID).child("Ratings")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ratingSum = 0;
                        for(DataSnapshot ds : snapshot.getChildren()) {
                            double rate = Double.parseDouble(""+ds.child("rating").getValue());
                            ratingSum = ratingSum + rate;

                        }

                        long noOfReviews = snapshot.getChildrenCount();
                        Log.d("reviews", "onDataChange: "+noOfReviews);
                        double avgRating = ratingSum / noOfReviews;
                        holder.rating.setRating((float)avgRating);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    double ratingSum = 0;


    @Override
    public int getItemCount() {
        return shopList.size();
    }

    class holderShop extends RecyclerView.ViewHolder {
        ImageView icon , forward;
        TextView phoneNo , address , title , openClose;
        RatingBar rating;

        public holderShop(@NonNull View itemView) {
            super(itemView);

            icon = itemView.findViewById(R.id.card_shop_photo);
            title = itemView.findViewById(R.id.card_shop_title);
            phoneNo = itemView.findViewById(R.id.card_shop_phoneNo);
            openClose = itemView.findViewById(R.id.card_shop_openClose);
            address = itemView.findViewById(R.id.card_shop_address);
            rating = itemView.findViewById(R.id.card_shop_rate);
            forward = itemView.findViewById(R.id.card_forward_shop);
        }
    }
}
