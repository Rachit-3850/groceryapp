package com.example.groceryapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class adapterCartItem extends RecyclerView.Adapter<adapterCartItem.holderCartItem>{
    Context context;
    ArrayList <ModelCardItem> cartItems;

    public adapterCartItem(Context context, ArrayList<ModelCardItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public holderCartItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_cart_items,parent,false);
        return new holderCartItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull holderCartItem holder, int position) {
    ModelCardItem modelCardItem = cartItems.get(position);
    String cId = modelCardItem.getId();
    String cPid = modelCardItem.getPid();
    String cTitle = modelCardItem.getName();
    String cQuantity = modelCardItem.getQuantity();
    String cPrice = modelCardItem.getPrice();
    String cTotalPrice = modelCardItem.getCost();

        Log.d("totalPrice", "onBindViewHolder: total Price "+cTotalPrice);
        Log.d("totalPrice", "onBindViewHolder: quantity "+cQuantity);
        Log.d("totalPrice", "onBindViewHolder: price "+cPrice);
        Log.d("totalPrice", "onBindViewHolder: cTitle"+cTitle);

    // set data
        holder.title.setText(cTitle);
        holder.price.setText(cPrice);
        holder.totalPrice.setText(cTotalPrice);
        holder.quantity.setText("["+cQuantity+"]");

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EasyDB easyDB = EasyDB.init(context , "ITEM_DB")
                        .setTableName("ITEMS_TABLE")
                        .addColumn(new Column("item_ID" , new String[]{"text" , "unique"}))
                        .addColumn(new Column("item_Pid" , new String[]{"text" , "not null"}))
                        .addColumn(new Column("item_name" , new String[]{"text" , "not null"}))
                        .addColumn(new Column("item_price" , new String[]{"text" , "not null"}))
                        .addColumn(new Column("item_finalPrice" , new String[]{"text" , "not null"}))
                        .addColumn(new Column("item_quantity" , new String[]{"text" , "not null"}))
                        .doneTableColumn();
                easyDB.deleteRow(1 , cId);
                Toast.makeText(context, "Removed from Cart...", Toast.LENGTH_SHORT).show();
                Log.d("totalPrice", "onClick: total Price "+cTotalPrice);
                // refresh list
                cartItems.remove(position);
                notifyItemChanged(position);
                notifyDataSetChanged();


                double tx  = ((shopDetailsActivity)context).allTotalPrice;
                tx = tx - Double.parseDouble(cTotalPrice);
                double delveryFee = Double.parseDouble(((shopDetailsActivity)context).sDeliveryFee);
                double tp = delveryFee + tx;

//                Log.d("cart", "onClick: "+tx);
//                Log.d("cart", "onClick: "+Double.parseDouble(cTotalPrice));
//                double totalPrice = tx - Double.parseDouble(cTotalPrice);
//                double delveryFee = Double.parseDouble(((shopDetailsActivity)context).sDeliveryFee);
////                double stotalPrice =  totalPrice - delveryFee;
                ((shopDetailsActivity)context).allTotalPrice = tx;
                ((shopDetailsActivity)context).cprice.setText(""+tx);
                ((shopDetailsActivity)context).ctotalprice.setText(""+tp);
                ((shopDetailsActivity)context).cartCount();
            }
        });

    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    class holderCartItem extends RecyclerView.ViewHolder {
        TextView price , title , totalPrice , quantity , remove;
        Button btn;
        public holderCartItem(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_cart);
            price = itemView.findViewById(R.id.price_cart);
            totalPrice = itemView.findViewById(R.id.finalprice_cart);
            remove = itemView.findViewById(R.id.remove_cart);
            btn = itemView.findViewById(R.id.btn_cart_placeOrder);
            quantity = itemView.findViewById(R.id.quantity_cart);

        }
    }
}
