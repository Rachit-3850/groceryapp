package com.example.groceryapp;

import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterConfirmOrdersSeller extends RecyclerView.Adapter<AdapterConfirmOrdersSeller.HolderConfirmOrdersSeller> implements Filterable {
    Context context;
    ArrayList<ModelOrderSeller> ordersList,filterList;
    private filtersOrdersSeller filter;


    public AdapterConfirmOrdersSeller(Context context, ArrayList<ModelOrderSeller> ordersList) {
        this.context = context;
        this.ordersList = ordersList;
        this.filterList = ordersList;
        Log.d("ordersSeller", "AdapterConfirmOrdersSeller: "+ordersList.size());
    }

    @NonNull
    @Override
    public HolderConfirmOrdersSeller onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.orders_seller_card,parent,false);
        return new HolderConfirmOrdersSeller(view);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull HolderConfirmOrdersSeller holder, int position) {
        Log.d("ordersSeller", "AdapterConfirmOrdersSeller: "+ordersList.size());
        ModelOrderSeller modelOrderSeller = ordersList.get(position);
        String orderId = modelOrderSeller.getOrderId();
        Log.d("ordersSeller", "onBindViewHolder: "+orderId);
        String ctotalAmount = modelOrderSeller.getOrderCost();
        String cdate = modelOrderSeller.getOrderTime();
        String cstatus = modelOrderSeller.getOrderStatus();
        String orderTo = modelOrderSeller.getOrderTo();
        String orderBy = modelOrderSeller.getOrderBy();

        holder.status.setText(cstatus);
        holder.id.setText(orderId);
        holder.totalAmount.setText(ctotalAmount);

        loadUserInfo(modelOrderSeller, holder);

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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //details
                Intent intent = new Intent(context,OrdersDetailsSellerActivity.class);
                intent.putExtra("orderId",orderId);//to load order info
                intent.putExtra("orderBy" , orderBy);//to load info of the user who placed order
                context.startActivity(intent);
            }
        });
    }

    private void loadUserInfo(ModelOrderSeller modelOrderSeller, HolderConfirmOrdersSeller holder) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(modelOrderSeller.getOrderBy()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String email = "" +snapshot.child("Email").getValue();
                holder.email.setText(email);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter == null) {
            filter = new filtersOrdersSeller(filterList, this);
        }
        return filter;
    }

    class HolderConfirmOrdersSeller extends RecyclerView.ViewHolder {
        TextView id , email , totalAmount , date , status;
        public HolderConfirmOrdersSeller(@NonNull View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.id_card_placedOrder_seller);
            email = itemView.findViewById(R.id.email_card_placedOrder_seller);
            totalAmount = itemView.findViewById(R.id.totalPrice_card_placedOrder_seller);
            date = itemView.findViewById(R.id.date_card_placedOrder_seller);
            status = itemView.findViewById(R.id.status_card_placedOrder_seller);
        }
    }
}
