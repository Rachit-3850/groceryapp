package com.example.groceryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

public class Main_Seller_Activity extends AppCompatActivity {
    private TextView main_heading , shopName , address , products , orders ,filterNoteOrders ,filterNoteProducts;
    private ImageView logout_btn , addProduct;
    private ImageView editP;
    ImageButton btn , orderbtn;
    EditText search;
    RecyclerView productRV, ordersRV;
    LinearLayout  product , order;
    CircularImageView circularImageView;


    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    private ArrayList<ModelProduct> productList;
    private adapterRecycleSeller adapterRecycleSeller;

    private ArrayList<ModelOrderSeller> ordersList;
    AdapterConfirmOrdersSeller adapterConfirmOrdersSeller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_seller);

        main_heading = findViewById(R.id.Main_seller_name);
        logout_btn = findViewById(R.id.logout_seller);
        editP = findViewById(R.id.edit_seller);
        shopName = findViewById(R.id.Main_seller_shopName);
        address = findViewById(R.id.Main_seller_address);
        addProduct = findViewById(R.id.cart_seller);
        products = findViewById(R.id.products_seller);
        orders = findViewById(R.id.orders_seller);
        product = findViewById(R.id.products_layout_seller);
        order = findViewById(R.id.orders_layout_seller);
        productRV = findViewById(R.id.recycleView_product);
        search = findViewById(R.id.product_search);
        btn = findViewById(R.id.filter_btn_product);
        circularImageView = findViewById(R.id.seller_profile_main);
        orderbtn = findViewById(R.id.orders_filter_seller);
        ordersRV = findViewById(R.id.recycleView_orders);
        filterNoteOrders = findViewById(R.id.orders_show_seller);
        filterNoteProducts = findViewById(R.id.filter_product_text);

        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Main_Seller_Activity.this);
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

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();
        loadAllProducts();
        loadAllOrders();

        showProductUI();

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    adapterRecycleSeller.getFilter().filter(charSequence);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        editP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open edit profile
                Intent intent = new Intent(Main_Seller_Activity.this,edit_seller_profile_Activity.class);
                startActivity(intent);
            }
        });

        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Main_Seller_Activity.this , AddProductActivity.class);
                startActivity(intent);
            }
        });


        products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showProductUI();
            }
        });

        orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showOrderUI();
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Main_Seller_Activity.this);
                builder.setTitle("Filter Products")
                        .setItems(Constants.options2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String selected = Constants.options2[i];
                        Log.d("message", "onClick: "+selected);
                        if(selected.equals("All")) {
                            filterNoteProducts.setText("Showing All Products");
                            loadAllProducts();
                        }
                        else {
                            filterNoteProducts.setText("Showing Products : "+ selected);
                            loadFilterProducts(selected);
                        }
                    }
                })
                        .show();
            }
        });
        orderbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Main_Seller_Activity.this);
                String options[] = {"All" , "In Progress","Completed","Cancelled"};
                builder.setTitle("Filter Orders")
                    .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String selected = options[i];
                        if(selected.equals("All")) {
                            filterNoteOrders.setText("Showing All Status");
                            adapterConfirmOrdersSeller.getFilter().filter("");
                        }
                        else {
                            filterNoteOrders.setText("Showing Status : "+selected);
                            adapterConfirmOrdersSeller.getFilter().filter(selected);

                        }
                    }
                })
                        .show();
            }
        });

    }

    private void leadFilterOrders() {
    }

    private void loadAllOrders() {
        ordersList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Orders")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ordersList.clear();
                        for(DataSnapshot ds : snapshot.getChildren()) {

                            String orderIdo = ""+ ds.child("orderId").getValue();
                            String orderByo = ""+ ds.child("orderBy").getValue();
                            String orderToo = ""+ ds.child("orderTo").getValue();
                            String orderCosto = ""+ ds.child("orderCost").getValue();
                            String orderStatuso = ""+ ds.child("orderStatus").getValue();
                            String orderTimeo = ""+ ds.child("orderTime").getValue();

                            ModelOrderSeller modelOrderSeller = new ModelOrderSeller(orderIdo , orderTimeo , orderStatuso,orderCosto,orderByo,orderToo,df);

                            ordersList.add(modelOrderSeller);
                        }
                        Log.d("ordersSeller", "onDataChange: "+ordersList.size());
                        //setup adapter
                        ordersRV.setLayoutManager(new LinearLayoutManager(Main_Seller_Activity.this
                        ));
                        adapterConfirmOrdersSeller =  new AdapterConfirmOrdersSeller(Main_Seller_Activity.this,ordersList);
                        //set adapter
                        ordersRV.setAdapter(adapterConfirmOrdersSeller);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadAllProducts() {
        productList  = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productList.clear();
                        for(DataSnapshot ds : snapshot.getChildren()) {
                            ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                            Log.d("message", "onDataChange:"+modelProduct.getCategory());
                            productList.add(modelProduct);
                            Log.d("message", "size of list"+productList.size());
                        }
                        //setup adapter
                        productRV.setLayoutManager(new LinearLayoutManager(Main_Seller_Activity.this
                        ));
                        adapterRecycleSeller =  new adapterRecycleSeller(Main_Seller_Activity.this,productList);
                        //set adapter
                        productRV.setAdapter(adapterRecycleSeller);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadFilterProducts(final String selected) {
        productList  = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productList.clear();
                        for(DataSnapshot ds : snapshot.getChildren()) {

                            String productCategory = ""+ds.child("category").getValue();
                            //if selected category matches
                            if(selected.equals(productCategory)) {
                                ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                                productList.add(modelProduct);
                                Log.d("message", "size of list"+productList.size());
                            }

                        }
                        //setup adapter
                        adapterRecycleSeller =  new adapterRecycleSeller(Main_Seller_Activity.this,productList);
                        //set adapter
                        productRV.setAdapter(adapterRecycleSeller);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void showProductUI() {
        product.setVisibility(View.VISIBLE);
        order.setVisibility(View.GONE);
        changeBackground(true , false);
    }

    private void showOrderUI() {
        order.setVisibility(View.VISIBLE);
        product.setVisibility(View.GONE);
        changeBackground(false , true);
    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user == null) {
            Intent intent = new Intent(Main_Seller_Activity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            loadMyInfo();
        }
    }

    public void changeBackground(boolean a1 , boolean a2) {
        if(a1) {
            products.setBackgroundResource(R.drawable.design_product_orders_white);
            products.setTextColor(getResources().getColor(R.color.black));
        }
        else {
            products.setBackgroundResource(android.R.color.transparent);
            products.setTextColor(getResources().getColor(R.color.white));
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
    String df;
    private void loadMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds : snapshot.getChildren()) {
                            String name = ""+ds.child("fullName").getValue();
                            String shop = ""+ds.child("shopName").getValue();
                            String addr = ""+ds.child("address").getValue();
                            String accountType = ""+ds.child("accountType").getValue();
                            String icon = ""+ds.child("icon").getValue();
                            String df = ""+ds.child("deleveryfee").getValue();

                            try {
                                Picasso.get().load(icon).placeholder(R.drawable.user).into(circularImageView);
                            }
                            catch (Exception e) {
                                circularImageView.setImageResource(R.drawable.user);
                            }

                            main_heading.setText(name);
                            shopName.setText(shop);
                            address.setText(addr);

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