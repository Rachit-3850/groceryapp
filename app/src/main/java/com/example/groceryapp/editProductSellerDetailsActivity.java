package com.example.groceryapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class editProductSellerDetailsActivity extends AppCompatActivity {
    private String productId;
    ImageView back;
    EditText title, discountPrice, discountPercentage, discription, price, quantity;
    Button editItem;
    TextView category;
    SwitchCompat discount;
    ImageView icon;

    private final int GALLERY_REQUEST_CODE = 200;


    FirebaseAuth firebaseAuth;
    Uri image_uri;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product_seller_details);

        productId = getIntent().getStringExtra("productId");
        title = findViewById(R.id.title_editItem_seller);
        category = findViewById(R.id.category_editItem_seller);
        price = findViewById(R.id.price_editItem_seller);
        discription = findViewById(R.id.description_editItem_seller);
        discount = findViewById(R.id.seller_editItem_discount);
        discountPrice = findViewById(R.id.discountPrice_editItem_seller);
        discountPercentage = findViewById(R.id.discountPercentage_editItem_seller);
        editItem = findViewById(R.id.editItem_btn_seller);
        back = findViewById(R.id.back_editItem_seller);
        icon = findViewById(R.id.editItemPhoto_seller);
        quantity = findViewById(R.id.quantity_editItem_seller);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        loadProductDetails();

        discount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    discountPercentage.setVisibility(View.VISIBLE);
                    discountPrice.setVisibility(View.VISIBLE);
                } else {
                    discountPercentage.setVisibility(View.GONE);
                    discountPrice.setVisibility(View.GONE);
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePickDialog();
            }
        });

        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryDialog();
            }
        });

        editItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputData();

            }
        });

    }

    private void loadProductDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Products").child(productId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        //get data
                        String dId = "" + snapshot.child("productID").getValue();
                        String dPrice = "" + snapshot.child("price").getValue();
                        String dQuantity = "" + snapshot.child("quantity").getValue();
                        String dDiscountNote = "" + snapshot.child("discountNote").getValue();
                        String dDiscountP = "" + snapshot.child("discountPrice").getValue();
                        String dDiscountAvailable = "" + snapshot.child("discountAvailable").getValue();
                        String dTitle = "" + snapshot.child("title").getValue();
                        String dDiscription = "" + snapshot.child("discription").getValue();
                        String dUid = "" + snapshot.child("uid").getValue();
                        String dIcon = "" + snapshot.child("icon").getValue();
                        String dCategory = "" + snapshot.child("category").getValue();

                        //set data
                        if (dDiscountAvailable.equals("true")) {
                            discount.setChecked(true);

                            discountPrice.setVisibility(View.VISIBLE);
                            discountPercentage.setVisibility(View.VISIBLE);
                        } else {
                            discountPrice.setVisibility(View.GONE);
                            discountPercentage.setVisibility(View.GONE);

                        }
                        title.setText(dTitle);
                        price.setText(dPrice);
                        discription.setText(dDiscription);
                        quantity.setText(dQuantity);
                        discountPrice.setText(dDiscountP);
                        discountPercentage.setText(dDiscountNote);
                        category.setText(dCategory);

//
                        try {
                            Picasso.get().load(dIcon).placeholder(R.drawable.ic_baseline_add_shopping_cart_24_color).into(icon);
                        } catch (Exception e) {
                            icon.setImageResource(R.drawable.ic_baseline_add_shopping_cart_24_color);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    String Title, Description = "", Category = "", Price, DiscountPrice = "", DiscountNote = "", Quantity;
    boolean DiscountAvailable;

    private void inputData() {
        Title = title.getText().toString().trim();
        Price = price.getText().toString().trim();
        Description = discription.getText().toString().trim();
        Category = category.getText().toString().trim();
        DiscountAvailable = discount.isChecked();
        Quantity = quantity.getText().toString().trim();

        if (TextUtils.isEmpty(Title)) {
            Toast.makeText(this, "Title is required...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(Quantity)) {
            Toast.makeText(this, "Title is required...", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(Price)) {
            Toast.makeText(this, "Price is required...", Toast.LENGTH_SHORT).show();
            return;
        }

        if (DiscountAvailable) {
            DiscountPrice = discountPrice.getText().toString().trim();
            DiscountNote = discountPercentage.getText().toString().trim();

            if (TextUtils.isEmpty(DiscountPrice)) {
                Toast.makeText(this, "Discount Price is required...", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        updateToDB();
    }

    private void updateToDB() {
        progressDialog.setMessage("Updating product...");
        progressDialog.show();
        if(image_uri == null) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("uid", "" + firebaseAuth.getUid());
            map.put("title", "" + Title);
            map.put("price", "" + Price);
            map.put("category", "" + Category);
            map.put("discription", "" + Description);
            map.put("discountPrice", "" + DiscountPrice);
            map.put("discountNote", "" + DiscountNote);
            map.put("discountAvailable", "" + DiscountAvailable);
            map.put("quantity", "" + Quantity);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).child("Products").child(productId)
                    .updateChildren(map)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();
                            Toast.makeText(editProductSellerDetailsActivity.this, "Updated...", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(editProductSellerDetailsActivity.this , Main_Seller_Activity.class);
                            startActivity(intent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(editProductSellerDetailsActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else {
            String filiPathAndName = "product_images/"+productId;
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filiPathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            Log.d("photoImage", "onSuccess: "+taskSnapshot);
//                            progressDialog.dismiss();
//                            Toast.makeText(AddProductActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            //image uploaded
                            //get url of upleaded image
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();

                            while (!uriTask.isSuccessful());
                            Uri downloadImageUri = uriTask.getResult();
                            Log.d("photoImage", "onSuccess: uploaded"+downloadImageUri);
                            if(uriTask.isSuccessful()) {
                                //url of loaded image recived
                                HashMap<String, Object> map = new HashMap<>();
                                map.put("uid", "" + firebaseAuth.getUid());
                                map.put("title", "" + Title);
                                map.put("price", "" + Price);
                                map.put("category", "" + Category);
                                map.put("discription", "" + Description);
                                map.put("discountPrice", "" + DiscountPrice);
                                map.put("discountNote", "" + DiscountNote);
                                map.put("discountAvailable", "" + DiscountAvailable);
                                map.put("quantity", "" + Quantity);
                                map.put("icon",""+downloadImageUri);

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                ref.child(firebaseAuth.getUid()).child("Products").child(productId)
                                        .updateChildren(map)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                Toast.makeText(editProductSellerDetailsActivity.this, "Updated...", Toast.LENGTH_SHORT).show();
                                                Intent intenti = new Intent(editProductSellerDetailsActivity.this , Main_Seller_Activity.class);
                                                startActivity(intenti);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                Toast.makeText(editProductSellerDetailsActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(editProductSellerDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }



    }

    private void categoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Product Category")
                .setItems(Constants.options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String item = Constants.options[i];
                        category.setText(item);
                    }
                }).show();

    }
    private void showImagePickDialog() {
        String[] options = {"Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i == 0) {

                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent,GALLERY_REQUEST_CODE);
                        }

                    }
                })
                .show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {

             if(requestCode == GALLERY_REQUEST_CODE) {
                image_uri = data.getData();
                icon.setImageURI(data.getData());
            }
        }
    }


}