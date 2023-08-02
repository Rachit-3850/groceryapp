package com.example.groceryapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class order_Details_Activity extends AppCompatActivity {
    String orderID ,  orderTo;
    ImageView back , riview;
    TextView orderId , date , address , amount , shopName , fee , status;
    RecyclerView orderRV;

    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    ArrayList<ModelOrderdItem> orderList;
    AdapterOrderdItems adapterOrderdItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        Intent intent = getIntent();
        orderID = intent.getStringExtra("orderId");
        orderTo = intent.getStringExtra("orderTo");

        orderId = findViewById(R.id.orderID_confirm_value);
        date = findViewById(R.id.date_confirm_value);
        address = findViewById(R.id.address_confirm_value);
        amount = findViewById(R.id.amount_confirm_value);
        fee = findViewById(R.id.items_confirm_value);
        shopName = findViewById(R.id.shopName_confirm_value);
        status = findViewById(R.id.status_confirm_value);
        back = findViewById(R.id.back_confirm_user);
        orderRV = findViewById(R.id.ordered_confirm_RV);
        riview = findViewById(R.id.review_confirm_user);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("please wait");
        progressDialog.setCanceledOnTouchOutside(false);




        loadShopInfo();
        loadOrderInfo();


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        riview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(order_Details_Activity.this , review_shops_Activity.class);
                intent1.putExtra("shopUID" , orderTo);
                startActivity(intent1);
            }
        });

    }
    String orderby;
    private void loadOrderInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(orderTo).child("Orders").child(orderID)
                .addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        orderby = ""+snapshot.child("orderBy").getValue();

                        String ocost = ""+snapshot.child("orderCost").getValue();
                        String cstatus = ""+snapshot.child("orderStatus").getValue();
                        String odate = ""+snapshot.child("orderTime").getValue();

//
                        String dateFormat = "dd/MM/yyyy";
                        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(Long.parseLong(odate));
                        String formatDate = formatter.format(calendar.getTime()).toString();

                        if(cstatus.equals("In Progress")) {
                            status.setTextColor(getResources().getColor(R.color.green));
                        }
                        else if(cstatus.equals("Completed")) {
                            status.setTextColor(getResources().getColor(R.color.purple_200));
                        }
                        else if(cstatus.equals("Cancelled")) {
                            status.setTextColor(getResources().getColor(R.color.red));
                        }

                        date.setText(formatDate);
                        status.setText(cstatus);
                        orderId.setText(orderID);
                        amount.setText(ocost);
                        loadAddressInfo();
                        loadOrderdItems();
//
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadOrderdItems() {
        orderList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(orderTo).child("Orders").child(orderID).child("Items")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        orderList.clear();
                        for(DataSnapshot ds : snapshot.getChildren()) {
//                            ModelOrderdItem modelOrderdItem = ds.getValue(ModelOrderdItem.class);
//                            orderList.add(modelOrderdItem);
                            String cost_ = ""+ds.child("cost").getValue();
                            String name_ = ""+ds.child("name").getValue();
                            String price_ = ""+ds.child("price").getValue();
                            String quantity_ = ""+ds.child("quantity").getValue();
                            String pid_ = ""+ds.child("pid").getValue();
                            orderList.add(new ModelOrderdItem(name_ , cost_ ,price_ , quantity_ , pid_));
                            Log.d("recycleview", "onDataChange: "+cost_);
                        }
                        Collections.reverse(orderList);
                        orderRV.setLayoutManager(new LinearLayoutManager(order_Details_Activity.this
                        ));
                        adapterOrderdItems =  new AdapterOrderdItems(order_Details_Activity.this,orderList);
                        //set adapter
                        orderRV.setAdapter(adapterOrderdItems);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void loadAddressInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(orderby)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String oaddress = ""+snapshot.child("address").getValue();
                        Log.d("orderDetails", "onDataChange: "+oaddress);
                        address.setText(oaddress);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    String odeliveryFee;

    private void loadShopInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(orderTo)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String oShopName = ""+snapshot.child("shopName").getValue();
                        shopName.setText(oShopName);
                        odeliveryFee = ""+snapshot.child("deleveryfee").getValue();
                        fee.setText("Rs "+odeliveryFee);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}