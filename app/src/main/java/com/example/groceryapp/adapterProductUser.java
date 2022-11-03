package com.example.groceryapp;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class adapterProductUser extends RecyclerView.Adapter<adapterProductUser.holderProductUser> implements Filterable {
    private Context context;
    public ArrayList<ModelProduct> productList,filterList;
    private filterProductUser filter;

    public adapterProductUser(Context context, ArrayList<ModelProduct> productList) {
        this.context = context;
        this.productList = productList;
        this.filterList = productList;
    }

    @NonNull
    @Override
    public holderProductUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_product_design_card,parent,false);
        return new holderProductUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull holderProductUser holder, int position) {
        ModelProduct modelProduct = productList.get(position);
        String sid = modelProduct.getProductID();
        String uid = modelProduct.getUid();
        String sdiscountAvailable = modelProduct.getDiscountAvailable();
        String sdiscountNote = modelProduct.getDiscountNote();
        String sdiscountPrice = modelProduct.getDiscountPrice();
        String squantity = modelProduct.getQuantity();
        String sdiscription = modelProduct.getDiscription();
        String sprice = modelProduct.getPrice();
        String stitle = modelProduct.getTitle();
        String sicon = modelProduct.getIcon();

        holder.title.setText(stitle);
        holder.price.setText("Rs "+sprice);
        holder.discountNote.setText(sdiscountNote);
        holder.discountPrice.setText("Rs "+sdiscountPrice);
        holder.discription.setText(sdiscription);

        if(sdiscountAvailable.equals("true")) {
            holder.discountPrice.setVisibility(View.VISIBLE);
            holder.discountNote.setVisibility(View.VISIBLE);
            holder.price.setPaintFlags(holder.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else {
            holder.discountPrice.setVisibility(View.GONE);
            holder.discountNote.setVisibility(View.GONE);
        }
        try {
            Picasso.get().load(sicon).placeholder(R.drawable.ic_baseline_add_shopping_cart_24_color).into(holder.icon);
        }
        catch (Exception e) {
            holder.icon.setImageResource(R.drawable.ic_baseline_add_shopping_cart_24_color);
        }
        holder.addtocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showQuantityDialog(modelProduct);
            }
        });
    }
 double cost = 0 , finalCost = 0 ;
    int count = 1;
    private void showQuantityDialog(ModelProduct modelProduct) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_add_to_cart,null);
        ImageView aicon , aplus , aminus;
        TextView ashopName , aquantity , acategory , adiscription , aprice , adiscountP , finalPrice;
        Button addToCartBtn;

        aicon = view.findViewById(R.id.addItemPhoto_addtocart);
        aplus = view.findViewById(R.id.plus_addtocart_seller);
        aminus = view.findViewById(R.id.minus_addtocart_seller);
        acategory = view.findViewById(R.id.category_addtocart_user);
        adiscription = view.findViewById(R.id.discription_addtocart_user);
        ashopName = view.findViewById(R.id.shopName_addtocart_user);
        aquantity = view.findViewById(R.id.quantity_addtocart_seller);
        addToCartBtn = view.findViewById(R.id.btn_addtocart_seller);
        aprice = view.findViewById(R.id.price_addtocart_user);
        adiscountP = view.findViewById(R.id.discountPrice_addtocart_user);
        finalPrice = view.findViewById(R.id.finalPrice_addtocart_user);

        String productIDa = modelProduct.getProductID();
        String titlea = modelProduct.getTitle();
        String discriptiona = modelProduct.getDiscription();
        String pricea = modelProduct.getPrice();
        String discountPa = modelProduct.getDiscountPrice();
        String categorya = modelProduct.getCategory();
        String icona = modelProduct.getIcon();
        String priceaa = pricea;
        String discountAvailable = modelProduct.getDiscountAvailable();

        if(modelProduct.getDiscountAvailable().equals("true")) {
            pricea = discountPa;
            aprice.setPaintFlags(aprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else {
            adiscountP.setVisibility(View.GONE);
        }
        try {
            Picasso.get().load(icona).placeholder(R.drawable.ic_baseline_add_shopping_cart_24_color).into(aicon);
        }
        catch (Exception e) {
            aicon.setImageResource(R.drawable.ic_baseline_add_shopping_cart_24_color);
        }
        cost = Double.parseDouble(pricea);
        finalCost = Double.parseDouble(pricea);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
//
        ashopName.setText(titlea);
        adiscription.setText(discriptiona);
        acategory.setText(categorya);
        adiscountP.setText("Rs "+discountPa);
        aprice.setText("Rs "+priceaa);
        aquantity.setText(" "+1);
        finalPrice.setText("Final Cost: Rs "+finalCost);
        AlertDialog dialog = builder.create();
        dialog.show();
        aplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count++;
                finalCost = finalCost + cost;
                finalPrice.setText("Final Cost: Rs "+finalCost);
                aquantity.setText(""+count);
            }
        });
        aminus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(count > 1)  {
                    count--;
                    finalCost = finalCost - cost;
                    finalPrice.setText("Final Cost: Rs "+finalCost);
                    aquantity.setText(""+count);
                }
            }
        });
        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String PP ,fp ,quan;
                PP = aprice.getText().toString().trim();
                if(discountAvailable.equals("true")) {
                    PP = adiscountP.getText().toString().trim();
                }
                fp = finalPrice.getText().toString().trim();
                Log.d("orderTo", "onClick: "+fp+" "+PP);
                quan = aquantity.getText().toString().trim();
                addtoCart(productIDa , titlea , PP ,fp , quan );
                count = 1;
                dialog.dismiss();
            }
        });
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                count = 1;
                finalPrice.setText("Final Cost: Rs "+cost);
            }
        });
    }
    int itemId = 0;
    private void addtoCart(String productIDa, String titlea, String pp, String fp, String quan) {
        itemId++;
        EasyDB easyDB = EasyDB.init(context , "ITEM_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("item_ID" , new String[]{"text" , "unique"}))
                .addColumn(new Column("item_Pid" , new String[]{"text" , "not null"}))
                .addColumn(new Column("item_name" , new String[]{"text" , "not null"}))
                .addColumn(new Column("item_price" , new String[]{"text" , "not null"}))
                .addColumn(new Column("item_finalPrice" , new String[]{"text" , "not null"}))
                .addColumn(new Column("item_quantity" , new String[]{"text" , "not null"}))
                .doneTableColumn();

        boolean b = easyDB.addData("item_ID" , itemId)
                .addData("item_Pid" , productIDa)
                .addData("item_name" , titlea)
                .addData("item_price" , pp)
                .addData("item_finalPrice" , fp)
                .addData("item_quantity" , quan)
                .doneDataAdding();
        Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show();



        ((shopDetailsActivity)context).cartCount();



    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new filterProductUser(filterList , this);
        }
        return filter;
    }

    class holderProductUser extends RecyclerView.ViewHolder {
        private ImageView icon , forward;
        private TextView title , discription , discountPrice , price , discountNote;
        LinearLayout addtocart;
        public holderProductUser(@NonNull View itemView) {
            super(itemView);

            icon = itemView.findViewById(R.id.card_product_user_photo);
            title = itemView.findViewById(R.id.card_product_user_title);
            price = itemView.findViewById(R.id.card_product_user_price);
            discountNote = itemView.findViewById(R.id.card_product_user_discountNote);
            discountPrice = itemView.findViewById(R.id.card_product_user_discountP);
            discription = itemView.findViewById(R.id.card_product_user_description);
            addtocart = itemView.findViewById(R.id.add_to_cart_user_products);

        }
    }
}
