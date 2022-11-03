package com.example.groceryapp;

import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AdapterOrderUser extends RecyclerView.Adapter<AdapterOrderUser.HolderOrderUser> {
    Context context;
    ArrayList<ModelPlaceOrder> orderList;

    public AdapterOrderUser(Context context, ArrayList<ModelPlaceOrder> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public HolderOrderUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_placed_order,parent,false);
        return new HolderOrderUser(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull HolderOrderUser holder, int position) {
        ModelPlaceOrder modelPlaceOrder = orderList.get(position);

        String cId = modelPlaceOrder.getOrderId();
        String cshopby = modelPlaceOrder.getOrderBy();
        String ctotalAmount = modelPlaceOrder.getOrderCost();
        String cdate = modelPlaceOrder.getOrderTime();
        String cstatus = modelPlaceOrder.getOrderStatus();
        String orderTo = modelPlaceOrder.getOrderTo();
        String orderBy = modelPlaceOrder.getOrderBy();

        loadShopInfo(modelPlaceOrder , holder);

        holder.totalAmount.setText("Amount : Rs "+ctotalAmount);
        holder.status.setText(cstatus);
        holder.id.setText("Order Id : "+cId);
        if(cstatus.equals("In Progress")) {
            holder.status.setTextColor(context.getResources().getColor(R.color.green));
        }
        else if(cstatus.equals("Completed")) {
            holder.status.setTextColor(context.getResources().getColor(R.color.purple_200));
        }
        else if(cstatus.equals("Cancelled")) {
            holder.status.setTextColor(context.getResources().getColor(R.color.red));
        }
        String dateFormat = "dd/MM/yyyy";
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(cdate));
        String formatDate = formatter.format(calendar.getTime()).toString();

        holder.date.setText(formatDate);

        //show order details
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context , order_Details_Activity.class);
                intent.putExtra("orderId" , cId);
                intent.putExtra("orderTo" , orderTo);

                context.startActivity(intent);
            }
        });
        
    }

    private void loadShopInfo(ModelPlaceOrder modelPlaceOrder, HolderOrderUser holder) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(modelPlaceOrder.getOrderTo()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String shopNameo = "" +snapshot.child("shopName").getValue();
                holder.shopName.setText(shopNameo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    class HolderOrderUser extends RecyclerView.ViewHolder {
        TextView id , shopName , totalAmount , date , status;
        public HolderOrderUser(@NonNull View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.id_card_placedOrder);
            shopName = itemView.findViewById(R.id.shopName_card_placedOrder);
            totalAmount = itemView.findViewById(R.id.totalPrice_card_placedOrder);
            date = itemView.findViewById(R.id.date_card_placedOrder);
            status = itemView.findViewById(R.id.status_card_placedOrder);


        }
    }
}
