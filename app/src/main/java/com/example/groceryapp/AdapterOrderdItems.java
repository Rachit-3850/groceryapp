package com.example.groceryapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterOrderdItems extends RecyclerView.Adapter<AdapterOrderdItems.HolderOrderedItems>{
    Context context ;
    ArrayList<ModelOrderdItem> orderdItems;

    @NonNull
    @Override
    public HolderOrderedItems onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.confirm_order_items,parent,false);
        return new HolderOrderedItems(view);
    }

    public AdapterOrderdItems(Context context, ArrayList<ModelOrderdItem> orderdItems) {
        this.context = context;
        this.orderdItems = orderdItems;
    }

    @Override
    public void onBindViewHolder(@NonNull HolderOrderedItems holder, int position) {
        ModelOrderdItem modelOrderdItem = orderdItems.get(position);
        String oPrice = modelOrderdItem.getPrice();
        String otatalPrice = modelOrderdItem.getTotalPrice();
        String oQuantity = modelOrderdItem.getQuantity();
        String oTitle = modelOrderdItem.getTitle();
        String pid = modelOrderdItem.getPid();

        holder.price.setText(oPrice);
        holder.title.setText(oTitle);
        holder.quantity.setText("["+oQuantity+"]");
        holder.totalPrice.setText(otatalPrice);
    }

    @Override
    public int getItemCount() {
        return orderdItems.size();
    }

    class HolderOrderedItems extends RecyclerView.ViewHolder {

        TextView title , quantity , totalPrice , price;
        public HolderOrderedItems(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title_confirm_order);
            price = itemView.findViewById(R.id.price_confirm_order);
            totalPrice = itemView.findViewById(R.id.finalprice_confirm_order);
            quantity = itemView.findViewById(R.id.quantity_confirm_order);
        }
    }
}
