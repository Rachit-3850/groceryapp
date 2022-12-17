package com.example.groceryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Main_user_Activity extends AppCompatActivity {
    private TextView main_heading;
    private TextView address , shops , orders , phoneNo ;
    private ImageView logout_btn;
    ImageView editP;
    RecyclerView shopRV , orderRV;
    LinearLayout shop , order;
//    user_profile_main
    CircularImageView circularImageView;

    ArrayList<ModelShop> shopList;
    adapterRecycleUser adapterRecycleUser;

    ArrayList<ModelPlaceOrder> orderList;
    AdapterOrderUser adapterOrderUser;
    
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        main_heading = findViewById(R.id.Main_user_name);
        address = findViewById(R.id.Main_user_address);
        phoneNo = findViewById(R.id.Main_user_phoneNo);
        logout_btn = findViewById(R.id.logout_user);
        editP = findViewById(R.id.edit_user);
        shops = findViewById(R.id.shops_user);
        orders = findViewById(R.id.orders_user);
        shop = findViewById(R.id.products_layout_user);
        order = findViewById(R.id.orders_layout_user);
        shopRV = findViewById(R.id.recycleView_shop);
        orderRV = findViewById(R.id.recycleView_order_user);
        circularImageView = findViewById(R.id.user_profile_main);

        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Main_user_Activity.this);
                builder.setMessage("Are you sure you want to logout?")
                                .setPositiveButton("logout", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        firebaseAuth.signOut();
                                        checkUser();
                                    }
                                })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).show();

            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();
        editP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open edit profile
                Intent intent = new Intent(Main_user_Activity.this,edit_user_profile_Activity.class);
                startActivity(intent);
            }
        });

        shops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                changeBackground(true , false);
//                fragments(new productFragment() , 1);
                showShopUI();
            }
        });

        orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                changeBackground(false , true);
//                fragments(new ordersFragment() , 1);
                showOrderUI();
            }
        });
    }
    private void showShopUI() {shop.setVisibility(View.VISIBLE);
        order.setVisibility(View.GONE);
        changeBackground(true , false);
    }

    private void showOrderUI() {
        order.setVisibility(View.VISIBLE);shop.setVisibility(View.GONE);
        changeBackground(false , true);
    }
    public void changeBackground(boolean a1 , boolean a2) {
        if(a1) {
            shops.setBackgroundResource(R.drawable.design_product_orders_white);
            shops.setTextColor(getResources().getColor(R.color.black));
        }
        else {
            shops.setBackgroundResource(android.R.color.transparent);
            shops.setTextColor(getResources().getColor(R.color.white));
        }
        if(a2) {
            orders.setBackgroundResource(R.drawable.design_product_orders_white);
            orders.setTextColor(getResources().getColor(R.color.black));
        }
        else {
            orders.setBackgroundResource(android.R.color.transparent);
            orders.setTextColor(getResources().getColor(R.color.white));
        }

    }
    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user == null) {
            Intent intent = new Intent(Main_user_Activity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            loadMyInfo();
        }
    }

    private void loadMyInfo() {
        shopList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds : snapshot.getChildren()) {
                            String name = ""+ds.child("fullName").getValue();
                            String accountType = ""+ds.child("accountType").getValue();
                            String PhoneNo = ""+ds.child("phoneNo").getValue();
                            String Email = ""+ds.child("Email").getValue();
                            String City = ""+ds.child("City").getValue();
                            String icon = ""+ds.child("icon").getValue();
                            String Address = ""+ds.child("address").getValue();


                            main_heading.setText(name);
                            address.setText(Address);
                            phoneNo.setText(PhoneNo);
                            try {
                                Picasso.get().load(icon).placeholder(R.drawable.user).into(circularImageView);
                            }
                            catch (Exception e) {
                                circularImageView.setImageResource(R.drawable.user);
                            }

                            //load only those shops which are in city
                            loadsShop(City);
                            //
                            loadOrders();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadOrders() {
        orderList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();
                for(DataSnapshot ds : snapshot.getChildren()) {
                    String uid = ""+ ds.getRef().getKey();
//                    Log.d("place", "onDataChange: "+uid);
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users")
                            .child(uid).child("Orders");
                    String pp = "" +ds.child("orderBy").getValue();
//                    Log.d("problem", "onDataChange: "+firebaseAuth.getUid());
                    ref.orderByChild("orderBy").equalTo(firebaseAuth.getUid())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    String uuid = ""+ snapshot.getRef().getKey();
                                    if(snapshot.exists()) {
                                        for(DataSnapshot ds : snapshot.getChildren()) {
//                                            ModelPlaceOrder modelPlaceOrder = ds.getValue(ModelPlaceOrder.class);
//                                            orderList.add(modelPlaceOrder);
//
                                            String orderId , orderTime , orderStatus , orderCost , orderBy , orderTo;
                                            orderCost = "" +ds.child("orderCost").getValue();
                                            orderId = "" +ds.child("orderId").getValue();
                                            orderTime = "" +ds.child("orderTime").getValue();
                                            orderStatus = "" +ds.child("orderStatus").getValue();
                                            orderBy = "" +ds.child("orderBy").getValue();
                                            orderTo = "" +ds.child("orderTo").getValue();
                                            Log.d("problem", "onDataChange: "+orderTo);

                                            Log.d("placed", "onDataChange: "+orderCost);
                                            orderList.add(new ModelPlaceOrder(orderId , orderTime , orderStatus , orderCost , orderBy , orderTo));
                                        }
                                        orderRV.setLayoutManager(new LinearLayoutManager(Main_user_Activity.this
                                            ));
                                            adapterOrderUser =  new AdapterOrderUser(Main_user_Activity.this,orderList);
                                            //set adapter
                                            orderRV.setAdapter(adapterOrderUser);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
//
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadsShop(String City) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("accountType").equalTo("seller")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        shopList.clear();
                        for(DataSnapshot ds : snapshot.getChildren()) {
                            ModelShop modelShop = ds.getValue(ModelShop.class);
                            String shopcity = ""+ds.child("City").getValue();
                            //only in your city
//                            if(shopcity.equals(City)) {
//                                shopList.add(modelShop);
//                            }
                            //all the shops
                            shopList.add(modelShop);

//                            setup adapter
                            shopRV.setLayoutManager(new LinearLayoutManager(Main_user_Activity.this
                            ));
                             adapterRecycleUser =  new adapterRecycleUser(Main_user_Activity.this,shopList);
                            //set adapter
                            shopRV.setAdapter(adapterRecycleUser);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}