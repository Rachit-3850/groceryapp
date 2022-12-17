package com.example.groceryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class shopDetailsActivity extends AppCompatActivity {
    TextView  email, phoneNo ,deliveryFee,shopName , shopOpen , address , count;
    ImageView back , icon , call,cart , star;
    ImageButton btn;
    EditText search;
    RecyclerView shopDetailsRV;
    String shopUID;
    String sName , sEmail , sPhoneNo , sAddress , sOpen , sDeliveryFee;
//    EasyDB easyDB;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    private adapterProductUser adapterProductUser;
    ArrayList<ModelProduct> productList;
    ArrayList<ModelCardItem> cartItems;
    adapterCartItem adapterCartItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_details);

        email = findViewById(R.id.email_shop_details_user);
        shopName = findViewById(R.id.shopName_shop_details_user);
        phoneNo = findViewById(R.id.phoneNo_shop_details_user);
        shopOpen = findViewById(R.id.shopOpen_shop_details_user);
        address = findViewById(R.id.address_shop_details_user);
        back = findViewById(R.id.back_shop_details_user);
        deliveryFee = findViewById(R.id.deleiveryfee_shop_details_user);
        icon = findViewById(R.id.icon_shop_details_user);
        btn = findViewById(R.id.filter_shop_details_user);
        search = findViewById(R.id.search_shop_details_user);
        shopDetailsRV = findViewById(R.id.recycleView_shop_details_user);
        call = findViewById(R.id.call_shop);
        cart = findViewById(R.id.cart_shop_details_user);
        count = findViewById(R.id.cart_shop_details_count);
        star = findViewById(R.id.star_shop_details_user);

        shopUID = getIntent().getStringExtra("shopId");

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();
        loadMyInfo();
        loadShopDetails();
        loadShopProducts();
        deleteDB();
        cartCount();



        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    adapterProductUser.getFilter().filter(charSequence);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(shopDetailsActivity.this);
                builder.setItems(Constants.options2, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String selected = Constants.options2[i];
                                Log.d("user", "onClick: "+selected);
                                if(selected.equals("All")) {
                                    loadShopProducts();
                                }
                                else {
                                    loadFilterProducts(selected);
                                }
                            }
                        })
                        .show();
            }
        });
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callShopOwner();
            }
        });

        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cartDialog();
            }
        });
        star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(shopDetailsActivity.this , shop_riview_Activity.class);
                intent.putExtra("shopUID" , shopUID);
                startActivity(intent);
            }
        });
    }

    private void deleteDB() {
        EasyDB easyDB = EasyDB.init(this , "ITEM_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("item_ID" , new String[]{"text" , "unique"}))
                .addColumn(new Column("item_Pid" , new String[]{"text" , "not null"}))
                .addColumn(new Column("item_name" , new String[]{"text" , "not null"}))
                .addColumn(new Column("item_price" , new String[]{"text" , "not null"}))
                .addColumn(new Column("item_finalPrice" , new String[]{"text" , "not null"}))
                .addColumn(new Column("item_quantity" , new String[]{"text" , "not null"}))
                .doneTableColumn();
        easyDB.deleteAllDataFromTable();
    }

    public void cartCount() {
        EasyDB easyDB = EasyDB.init(this , "ITEM_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("item_ID" , new String[]{"text" , "unique"}))
                .addColumn(new Column("item_Pid" , new String[]{"text" , "not null"}))
                .addColumn(new Column("item_name" , new String[]{"text" , "not null"}))
                .addColumn(new Column("item_price" , new String[]{"text" , "not null"}))
                .addColumn(new Column("item_finalPrice" , new String[]{"text" , "not null"}))
                .addColumn(new Column("item_quantity" , new String[]{"text" , "not null"}))
                .doneTableColumn();

        int c = easyDB.getAllData().getCount();
        if(c == 0) {
            count.setVisibility(View.GONE);
        }
        else {
            count.setVisibility(View.VISIBLE);
            count.setText(""+c);
        }
    }

    TextView cprice  , ctitle , cdeliceryFee   , ctotalprice ;
    Button cartBtn;

    RecyclerView cartRV;
    double allTotalPrice  = 0;
    private void cartDialog() {
        cartItems = new ArrayList<>();
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_cart,null);

        ctitle = view.findViewById(R.id.orderTo_cart);
        cprice = view.findViewById(R.id.subTotal_price_cart);
        ctotalprice = view.findViewById(R.id.total_price_cart);
        cdeliceryFee = view.findViewById(R.id.delivery_price_cart);
        cartBtn = view.findViewById(R.id.btn_cart_placeOrder);
        cartRV = view.findViewById(R.id.recycleView_cart);

        AlertDialog.Builder builder = new AlertDialog.Builder(shopDetailsActivity.this);
        builder.setView(view);
        ctitle.setText(sName);
        EasyDB easyDB = EasyDB.init(this , "ITEM_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("item_ID" , new String[]{"text" , "unique"}))
                .addColumn(new Column("item_Pid" , new String[]{"text" , "not null"}))
                .addColumn(new Column("item_name" , new String[]{"text" , "not null"}))
                .addColumn(new Column("item_price" , new String[]{"text" , "not null"}))
                .addColumn(new Column("item_finalPrice" , new String[]{"text" , "not null"}))
                .addColumn(new Column("item_quantity" , new String[]{"text" , "not null"}))
                .doneTableColumn();
//        //get all the records from db
        Cursor res = easyDB.getAllData();
//        Log.d("cart", "cartDialog: ");
        int c = 0;
        while(res.moveToNext()) {
            String Id = res.getString(1);
//            Log.d("cart", "cartDialog: "+c+" "+ Id );
            String Pid = res.getString(2);
            String Name = res.getString(3);
//            Log.d("cart", "cartDialog: "+c+" "+ Name );
            String Price = res.getString(4);
            String Cost = res.getString(5);
            String Quantity = res.getString(6);
//
            int i = Cost.lastIndexOf(' ');
            int j = Price.lastIndexOf(' ');
            String tt = Cost.substring(i+1);
            String pt = Price.substring(j+1);
            allTotalPrice += Double.parseDouble(tt);
            Log.d("cart", "cartDialog: a"+pt);

            ModelCardItem modelCardItem = new ModelCardItem(Id , Pid ,Quantity,
                    Name , pt , tt);
//
            cartItems.add(modelCardItem);
            c++;
        }
        Log.d("cart", "cartDialog: "+c);
        cartRV.setLayoutManager(new LinearLayoutManager(shopDetailsActivity.this
        ));
        adapterCartItem =  new adapterCartItem(shopDetailsActivity.this,cartItems);
        //set adapter
        cartRV.setAdapter(adapterCartItem);

        //set details

        cdeliceryFee.setText(sDeliveryFee);
        cprice.setText(""+allTotalPrice);
        ctotalprice.setText(""+(allTotalPrice+Double.parseDouble(sDeliveryFee)));

        Log.d("hiiii", "cartDialog: deliveryFee"+sDeliveryFee);
        Log.d("hiiii", "cartDialog: deliveryFee"+allTotalPrice);



        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                allTotalPrice = 0.0;
            }
        });
        //place order

        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sPhoneNo.equals("") || sPhoneNo.equals("null")) {
                    Toast.makeText(shopDetailsActivity.this, "please enter your phone no in your profile before placing order...", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(sAddress.equals("") || sAddress.equals("null")) {
                    Toast.makeText(shopDetailsActivity.this, "please enter your address no in your profile before placing order...", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(cartItems.size() == 0) {
                    Toast.makeText(shopDetailsActivity.this, "your cart is empty...", Toast.LENGTH_SHORT).show();
                    return;
                }
                submitOrder();
            }
        });
    }

    private void submitOrder() {
        progressDialog.setMessage("Placing Order...");
        progressDialog.show();

        // for id and time
        String timestamp = ""+System.currentTimeMillis();

        double cost = allTotalPrice + Double.parseDouble(sDeliveryFee);
        HashMap<String , String> map = new HashMap<>();
        map.put("orderId",""+timestamp);
        map.put("orderTime" ,"" +timestamp);
        map.put("orderStatus" ,  "In Progress");
        map.put("orderCost" ,  ""+cost);
        map.put("orderBy" ,  ""+firebaseAuth.getUid());
        map.put("orderTo" ,  ""+shopUID);

        //add to database
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(shopUID).child("Orders");
        ref.child(timestamp).setValue(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // order info added now add order items
                        for(ModelCardItem modelCardItem : cartItems) {
                            String oPid = modelCardItem.getPid();
                            String oid = modelCardItem.getId();
                            String ocost = modelCardItem.getCost();
                            String oprice = modelCardItem.getPrice();
                            String oquantity = modelCardItem.getQuantity();
                            String oname = modelCardItem.getName();
                            HashMap<String , String> map2 = new HashMap<>();
                            map2.put("pid",""+oPid);
                            map2.put("name" ,"" +oname);
                            map2.put("cost" , ocost);
                            map2.put("price" ,  ""+oprice);
                            map2.put("quantity" ,  ""+oquantity);

                            ref.child(timestamp).child("Items").child(oPid).setValue(map2);

                        }
                        progressDialog.dismiss();
                        Toast.makeText(shopDetailsActivity.this, "Order placed Successfully...", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(shopDetailsActivity.this , order_Details_Activity.class);
                        intent.putExtra("orderId" , timestamp);
                        Log.d("confirm", "onSuccess: "+timestamp);


                        intent.putExtra("orderTo" , shopUID);

                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(shopDetailsActivity.this, "Failed Placing Order...", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void callShopOwner() {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+Uri.encode(sPhoneNo)));
        startActivity(intent);
    }

    private void loadFilterProducts(String selected) {
        Log.d("filter", "loadFilterProducts: "+selected);
        productList  = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUID).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productList.clear();
                        for(DataSnapshot ds : snapshot.getChildren()) {

                            String productCategory = ""+ds.child("category").getValue();
                            Log.d("filter", "onDataChange: "+productCategory);
                            //if selected category matches
                            if(selected.equals(productCategory)) {
                                ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                                productList.add(modelProduct);
                                Log.d("message", "size of list"+productList.size());
                            }
                            shopDetailsRV.setLayoutManager(new LinearLayoutManager(shopDetailsActivity.this
                            ));
                            adapterProductUser =  new adapterProductUser(shopDetailsActivity.this,productList);
                            //set adapter
                            shopDetailsRV.setAdapter(adapterProductUser);

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadShopProducts() {
        productList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUID).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productList.clear();
                        for(DataSnapshot ds : snapshot.getChildren()) {
                            //if selected category matches
                            ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                            Log.d("user", "onDataChange:"+modelProduct.getCategory());

                            productList.add(modelProduct);

                        }
                        shopDetailsRV.setLayoutManager(new LinearLayoutManager(shopDetailsActivity.this
                        ));
                        adapterProductUser =  new adapterProductUser(shopDetailsActivity.this,productList);
                        //set adapter
                        shopDetailsRV.setAdapter(adapterProductUser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void loadShopDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sPhoneNo = ""+snapshot.child("phoneNo").getValue();
                sEmail = ""+snapshot.child("Email").getValue();
                sAddress = ""+snapshot.child("address").getValue();
                sDeliveryFee = ""+snapshot.child("deleveryfee").getValue();
                sName = ""+snapshot.child("shopName").getValue();
                sOpen =""+snapshot.child("shopOpen").getValue();
                String sicon =""+snapshot.child("icon").getValue();
                shopName.setText(sName);
                email.setText(sEmail);
                phoneNo.setText(sPhoneNo);
                address.setText(sAddress);
                deliveryFee.setText(sDeliveryFee);

                try {
                    Picasso.get().load(sicon).fit().into(icon);
                }
                catch (Exception e) {
                    icon.setImageResource(R.drawable.shop);
                }

                if(sOpen.equals("true")) {
                    shopOpen.setText("Open");
                }
                else {
                    shopOpen.setText("Close");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadMyInfo() {
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

                            String Address = ""+ds.child("address").getValue();


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}