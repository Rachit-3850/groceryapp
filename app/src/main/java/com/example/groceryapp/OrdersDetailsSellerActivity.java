package com.example.groceryapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

public class OrdersDetailsSellerActivity extends AppCompatActivity {
    String orderBy , orderId;

    ImageView back,edit;
    TextView orderIdd , date , address , amount , email , phonoNo , status;
    RecyclerView orderRV;

    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    ArrayList<ModelOrderdItem> orderList;
    AdapterOrderdItems adapterOrderdItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_details_seller);
        orderBy = getIntent().getStringExtra("orderBy");
        orderId = getIntent().getStringExtra("orderId");

        orderIdd = findViewById(R.id.orderID_confirm_value_seller);
        date = findViewById(R.id.date_confirm_value_seller);
        address = findViewById(R.id.address_confirm_value_seller);
        amount = findViewById(R.id.amount_confirm_value_seller);
        phonoNo = findViewById(R.id.phoneNo_confirm_value_seller);
        email = findViewById(R.id.email_confirm_value_seller);
        status = findViewById(R.id.status_confirm_value_seller);
        back = findViewById(R.id.back_confirm_seller);
        edit = findViewById(R.id.edit_confirm_seller);
        orderRV = findViewById(R.id.ordered_confirm_seller_RV);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        loadMyInfo();
        loadoderInfo();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editOrderStatusDialog();
            }
        });
    }

    private void editOrderStatusDialog() {
        String options[] = {"In Progress"
        ,"Completed"
        ,"Cancelled"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Order Status")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String selected = options[i];
                        editOrderStatus(selected);
                    }
                }).show();
    }

    private void editOrderStatus(String selected) {
        HashMap<String , Object> map = new HashMap<>();
        map.put("orderStatus",selected);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Orders").child(orderId)
                .updateChildren(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(OrdersDetailsSellerActivity.this, "Order Status is "+selected, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(OrdersDetailsSellerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadoderInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Orders").child(orderId)
                .addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String ocost = ""+snapshot.child("orderCost").getValue();
                        String cstatus = ""+snapshot.child("orderStatus").getValue();
                        String odate = ""+snapshot.child("orderTime").getValue();
//                        Log.d("orderDetails", "onDataChange: "+cstatus);
//                        Log.d("orderDetails", "onDataChange: "+ocost);
//                        Log.d("orderDetails", "onDataChange: "+odate);
//                        Log.d("orderDetails", "onDataChange: "+orderTo);
//                        Log.d("orderDetails", "onDataChange: "+orderID);
//                        Log.d("orderDetails", "onDataChange: "+orderby);
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
                        orderIdd.setText(orderId);
                        amount.setText(ocost);
                        loadOrderItems();
//
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadOrderItems() {
        orderList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Orders").child(orderId).child("Items")
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
                        orderRV.setLayoutManager(new LinearLayoutManager(OrdersDetailsSellerActivity.this
                        ));
                        adapterOrderdItems =  new AdapterOrderdItems(OrdersDetailsSellerActivity.this,orderList);
                        //set adapter
                        orderRV.setAdapter(adapterOrderdItems);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(orderBy)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String oaddress = ""+snapshot.child("address").getValue();
                        Log.d("orderDetails", "onDataChange: "+oaddress);
                        address.setText(oaddress);
                        String oemail = ""+snapshot.child("Email").getValue();
                        email.setText(oemail);
                        String ophoneNo = ""+snapshot.child("phoneNo").getValue();
                        phonoNo.setText(ophoneNo);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}