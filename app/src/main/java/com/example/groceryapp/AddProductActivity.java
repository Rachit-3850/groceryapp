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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.sql.Time;
import java.util.HashMap;

public class AddProductActivity extends AppCompatActivity {
    ImageView back;
    EditText title , discountPrice , discountPercentage , discription , price , quantity;
    Button addItem;
    TextView category;
    SwitchCompat discount;
    CircularImageView circularImageView;
    private final int REQUEST_CAMERA_CODE = 100;
    private final int GALLERY_REQUEST_CODE = 200;
    private final int CAMERA = 300;
    private final int GALLERY = 400;

    FirebaseAuth firebaseAuth;
    Bitmap camera;
    Uri image_uri;
    ProgressDialog progressDialog;

    private String[] storagePermission;
    private String[] cameraPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        title = findViewById(R.id.title_addItem_seller);
        category = findViewById(R.id.category_addItem_seller);
        price = findViewById(R.id.price_addItem_seller);
        discription = findViewById(R.id.description_addItem_seller);
        discount = findViewById(R.id.seller_addItem_discount);
        discountPrice = findViewById(R.id.discountPrice_addItem_seller);
        discountPercentage = findViewById(R.id.discountPercentage_addItem_seller);
        addItem = findViewById(R.id.addItem_btn_seller);
        back = findViewById(R.id.back_addItem_seller);
        circularImageView = findViewById(R.id.addItemPhoto_seller);
        quantity = findViewById(R.id.quantity_addItem_seller);
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        cameraPermission = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};



        discount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    discountPercentage.setVisibility(View.VISIBLE);
                    discountPrice.setVisibility(View.VISIBLE);
                }
                else {
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

        circularImageView.setOnClickListener(new View.OnClickListener() {
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

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputData();

            }
        });
    }

    private void addItemToDB() {
        progressDialog.setMessage("Adding Product...");
        progressDialog.show();

        String TimeStamp = ""+System.currentTimeMillis();

        if(image_uri == null) {
            HashMap<String,Object> map = new HashMap<>();
            map.put("productID",""+TimeStamp);
            map.put("uid",""+firebaseAuth.getUid());
            map.put("title",""+Title);
            map.put("price",""+Price);
            map.put("category",""+Category);
            map.put("discription",""+Description);
            map.put("icon","");
            map.put("discountPrice",""+DiscountPrice);
            map.put("discountNote",""+DiscountNote);
            map.put("discountAvailable",""+DiscountAvailable);
            map.put("quantity" , ""+Quantity);
            map.put("TimeStamp",""+ TimeStamp);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).child("Products").child(TimeStamp).setValue(map)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();
                            Toast.makeText(AddProductActivity.this, "Product added...", Toast.LENGTH_SHORT).show();
                            clearData();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AddProductActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else {
            String filiPathAndName = "product_images/"+TimeStamp;
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
                                HashMap<String,Object> map = new HashMap<>();
                                map.put("productID",""+TimeStamp);
                                map.put("uid",""+firebaseAuth.getUid());
                                map.put("title",""+Title);
                                map.put("price",""+Price);
                                map.put("category",""+Category);
                                map.put("discription",""+Description);
                                map.put("icon",""+downloadImageUri);
                                map.put("discountPrice",""+DiscountPrice);
                                map.put("discountNote",""+DiscountNote);
                                map.put("discountAvailable",""+DiscountAvailable);
                                map.put("quantity" , ""+Quantity);
                                map.put("TimeStamp",""+ TimeStamp);

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                ref.child(firebaseAuth.getUid()).child("Products").child(TimeStamp).setValue(map)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                Toast.makeText(AddProductActivity.this, "Product added...", Toast.LENGTH_SHORT).show();
                                                clearData();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                Toast.makeText(AddProductActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AddProductActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }

    private void clearData() {
        //clear data after uploading product
        title.setText("");
        category.setText("");
        price.setText("");
        discription.setText("");
        discountPrice.setText("");
        discountPercentage.setText("");
        circularImageView.setImageResource(R.drawable.ic_baseline_add_shopping_cart_24);
        image_uri = null;
    }

    String Title ,Description = "" , Category  = "", Price , DiscountPrice = "" , DiscountNote = ""  , Quantity;
    boolean DiscountAvailable;
    private void inputData() {
        Title = title.getText().toString().trim();
        Price = price.getText().toString().trim();
        Description = discription.getText().toString().trim();
        Category = category.getText().toString().trim();
        DiscountAvailable = discount.isChecked();
        Quantity = quantity.getText().toString().trim();

        if(TextUtils.isEmpty(Title)) {
            Toast.makeText(this, "Title is required...", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(Quantity)) {
            Toast.makeText(this, "Title is required...", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(Price)) {
            Toast.makeText(this, "Price is required...", Toast.LENGTH_SHORT).show();
            return;
        }

        if(DiscountAvailable) {
            DiscountPrice = discountPrice.getText().toString().trim();
            DiscountNote = discountPercentage.getText().toString().trim();

            if(TextUtils.isEmpty(DiscountPrice)) {
                Toast.makeText(this, "Discount Price is required...", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        addItemToDB();
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
        String[] options = {"Gallery" , "Camera"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i == 0) {
                            //Gallery clicked
//                            if(checkStoragePermission()){
                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(intent,GALLERY_REQUEST_CODE);
//                            }
//                            else {
//                                requestStoragePermission();
//                            }
                        }
                        else {
                            //Camera clicked
//                            if(checkCameraPermissions()){
//
//                            }
//                            else {
//                                requestCameraPermission();
//                            }
                            pickFromCamera();
//                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                            startActivityForResult(intent,REQUEST_CAMERA_CODE);
                        }
                    }
                })
                .show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if(requestCode == REQUEST_CAMERA_CODE) {
//                Bitmap img = (Bitmap)data.getExtras().get("data");
                circularImageView.setImageURI(image_uri);
            }
            else if(requestCode == GALLERY_REQUEST_CODE) {
                image_uri = data.getData();
                circularImageView.setImageURI(data.getData());
            }
        }
    }
    private void pickFromCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE,"Temp img title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"Temp img discription");

        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(intent , REQUEST_CAMERA_CODE);
    }
//    private boolean checkStoragePermission() {
//        boolean result = ContextCompat.checkSelfPermission(this , Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
//                (PackageManager.PERMISSION_GRANTED);
//
//        return result;
//    }
//
//    private void requestStoragePermission() {
//
//        ActivityCompat.requestPermissions(this , storagePermission,GALLERY);
//    }
//
//    private boolean checkCameraPermissions() {
//        boolean result = ContextCompat.checkSelfPermission(this , Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
//                (PackageManager.PERMISSION_GRANTED);
//
//        boolean resultl = ContextCompat.checkSelfPermission(this , Manifest.permission.CAMERA) ==
//                (PackageManager.PERMISSION_GRANTED);
//        return result && resultl;
//    }
//
//    private void requestCameraPermission() {
//        ActivityCompat.requestPermissions(this , cameraPermission,CAMERA);
//    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA: {
                if(grantResults.length>0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && storageAccepted) {
                        pickFromCamera();
                    }
                    else {
                        Toast.makeText(this, "Camera & Storage permissions are required...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            case GALLERY: {
                if(grantResults.length>0) {
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(storageAccepted) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent,GALLERY_REQUEST_CODE);
                    }
                    else {
                        Toast.makeText(this, "Storage permissions are required...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}