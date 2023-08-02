package com.example.groceryapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class adapterRecycleSeller extends RecyclerView.Adapter<adapterRecycleSeller.HolderProductSeller> implements Filterable{
    private Context context;
    public ArrayList<ModelProduct> list,filterList;
    private filterProduct filter;

    public adapterRecycleSeller(Context context, ArrayList<ModelProduct> list) {
        this.context = context;
        this.list = list;
        this.filterList = list;
    }

    @NonNull
    @Override
    public HolderProductSeller onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //initialize layout
        View view = LayoutInflater.from(context).inflate(R.layout.product_design_card,parent,false);

        return new HolderProductSeller(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductSeller holder, int position) {
    //get data
        Log.d("message", "onBindViewHolder: position" + position);
        ModelProduct modelProduct = list.get(position);
        String id = modelProduct.getProductID();
        String uid = modelProduct.getUid();
        String discountAvailable = modelProduct.getDiscountAvailable();
        String discountNote = modelProduct.getDiscountNote();
        String discountPrice = modelProduct.getDiscountPrice();
        String quantity = modelProduct.getQuantity();
        String price = modelProduct.getPrice();
        String title = modelProduct.getTitle();
        String icon = modelProduct.getIcon();

        holder.title.setText(title);
        holder.price.setText("Rs "+price);
        holder.discountNote.setText(discountNote);
        holder.discountPrice.setText("Rs "+discountPrice);
        holder.quantity.setText(quantity);
        Log.d("message", "onBindViewHolder: title "+title);
        Log.d("message", "onBindViewHolder: price "+price);
        Log.d("message", "onBindViewHolder: quantity "+ quantity);


        if(discountAvailable.equals("true")) {
            holder.discountPrice.setVisibility(View.VISIBLE);
            holder.discountNote.setVisibility(View.VISIBLE);
            holder.price.setPaintFlags(holder.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else {
            holder.discountPrice.setVisibility(View.GONE);
            holder.discountNote.setVisibility(View.GONE);
        }
//        holder.icon.setImageResource(R.drawable.ic_baseline_add_shopping_cart_24_color);
        try {
            Picasso.get().load(icon).placeholder(R.drawable.ic_baseline_add_shopping_cart_24_color).into(holder.icon);
        }
        catch (Exception e) {
            holder.icon.setImageResource(R.drawable.ic_baseline_add_shopping_cart_24_color);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detailBottomSheet(modelProduct);
            }
        });

    }

    private void detailBottomSheet(ModelProduct modelProduct) {
        //bottom sheet
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        //inflate view
        View view = LayoutInflater.from(context).inflate(R.layout.edit_product_details,null);
//        set view to bootom sheet
        bottomSheetDialog.setContentView(view);

//        init views of bottom sheet
        ImageView backbtn = view.findViewById(R.id.back_edit_product_details);
        ImageView delete = view.findViewById(R.id.delete_edit_product_details);
        ImageView edit = view.findViewById(R.id.edit_edit_product_details);
        ImageView dIcon = view.findViewById(R.id.edit_photo_product);
        TextView dPrice = view.findViewById(R.id.edit_product_price);
        TextView dCategory = view.findViewById(R.id.edit_product_category);
        TextView dQuantity = view.findViewById(R.id.edit_product_quantity);
        TextView dDiscountP = view.findViewById(R.id.edit_product_discountP);
        TextView dDiscountNote = view.findViewById(R.id.edit_product_discountNote);
        TextView dTitle = view.findViewById(R.id.edit_product_title);
        //get data
        String id = modelProduct.getProductID();
        String uid = modelProduct.getUid();
        String discountAvailable = modelProduct.getDiscountAvailable();
        String discountNote = modelProduct.getDiscountNote();
        String discountPrice = modelProduct.getDiscountPrice();
        String quantity = modelProduct.getQuantity();
        String price = modelProduct.getPrice();
        String title = modelProduct.getTitle();
        String category = modelProduct.getCategory();
        String icon = modelProduct.getIcon();

        //set data
        dPrice.setText(price);
        dDiscountNote.setText(discountNote);
        dDiscountP.setText(discountPrice);
        dQuantity.setText(quantity);
        dCategory.setText(category);
        dTitle.setText(title);

        if(discountAvailable.equals("true")) {
            dDiscountP.setVisibility(View.VISIBLE);
            dDiscountNote.setVisibility(View.VISIBLE);
            dPrice.setPaintFlags(dPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        }
        else {
            dDiscountP.setVisibility(View.GONE);
            dDiscountNote.setVisibility(View.GONE);
        }
//        dIcon.setImageResource(R.drawable.ic_baseline_add_shopping_cart_24);
        try {
            Picasso.get().load(icon).placeholder(R.drawable.ic_baseline_add_shopping_cart_24_color).into(dIcon);
        }
        catch (Exception e) {
            dIcon.setImageResource(R.drawable.ic_baseline_add_shopping_cart_24_color);
        }

        bottomSheetDialog.show();

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });
//        edit click
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
                Intent intent = new Intent(context,editProductSellerDetailsActivity.class);
                intent.putExtra("productId",id);
                context.startActivity(intent);
            }
        });
//        delete click
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete")
                        .setMessage("Are you sure you want to delete product "+title+"?")
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                deleteProduct(id);
                                bottomSheetDialog.dismiss();
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                AlertDialog dialog = builder.create();

                                bottomSheetDialog.dismiss();
                            }
                        }).show();
            }
        });
    }

    private void deleteProduct(String id) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Products").child(id).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Product Deleted...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        if(filter == null) {
            filter = new filterProduct(filterList, this);
        }
        return filter;
    }

    class HolderProductSeller extends RecyclerView.ViewHolder {
        private ImageView icon , forward;
        private TextView title , quantity , discountPrice , price , discountNote;

        public HolderProductSeller(@NonNull View itemView) {
            super(itemView);

            icon = itemView.findViewById(R.id.card_product_photo);
            title = itemView.findViewById(R.id.card_product_title);
            price = itemView.findViewById(R.id.card_product_price);
            discountNote = itemView.findViewById(R.id.card_product_discountNote);
            discountPrice = itemView.findViewById(R.id.card_product_discountP);
            quantity = itemView.findViewById(R.id.card_product_quantity);
            forward = itemView.findViewById(R.id.card_forward);

        }


    }
}
