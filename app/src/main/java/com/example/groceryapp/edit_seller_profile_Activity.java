package com.example.groceryapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class edit_seller_profile_Activity extends AppCompatActivity {
    EditText name , pno ,deliveryFee,shopname, country , state , city , address;
    Button update;
    ImageView back;
    CircularImageView circularImageView;
    SwitchCompat shopSwitch;

    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    private final int GALLERY_REQUEST_CODE = 200;

    private Uri image_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_seller_profile);

        circularImageView = findViewById(R.id.editPhoto_seller);
        circularImageView.setImageResource(R.drawable.user);
        circularImageView.setBackgroundColor(getResources().getColor(R.color.grey1));
        circularImageView.setBorderWidth(5);
        circularImageView.setBorderWidth(10);
        circularImageView.setBorderColor(getResources().getColor(R.color.grey1));
        circularImageView.setShadowGravity(CircularImageView.ShadowGravity.BOTTOM);
        circularImageView.setShadowRadius(9);


        name = findViewById(R.id.Name_edit_seller);
        shopname = findViewById(R.id.ShopName_edit_seller);
        pno = findViewById(R.id.pno_edit_seller);
        country = findViewById(R.id.Country_edit_seller);
        state = findViewById(R.id.state_edit_seller);
        address = findViewById(R.id.address_edit_seller);
        update = findViewById(R.id.edit_btn_seller);
        deliveryFee = findViewById(R.id.fee_edit_seller);
        city = findViewById(R.id.city_edit_seller);
        back = findViewById((R.id.back_edit_seller));
        shopSwitch = findViewById(R.id.seller_switch_shop);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        checkUser();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputdata();
            }
        });

        circularImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePickDialog();

            }
        });
    }
    private  String fullName , shopName , phoneNo ,deleveryfee , Country , State , City , Adress;
    boolean shopOpen;

    private void inputdata() {
        fullName = name.getText().toString().trim();
        phoneNo = pno.getText().toString().trim();
        shopName = shopname.getText().toString().trim();
        Country = country.getText().toString().trim();
        State = state.getText().toString().trim();
        City = city.getText().toString().trim();
        Adress = address.getText().toString().trim();
        deleveryfee = deliveryFee.getText().toString().trim();
        shopOpen = shopSwitch.isChecked();

        if(TextUtils.isEmpty(fullName)) {
            Toast.makeText(this, "Enter Your Name", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(Adress)) {
            Toast.makeText(this, "Enter Your Address", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(phoneNo)) {
            Toast.makeText(this, "Enter Your Phone No", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(State)) {
            Toast.makeText(this, "Enter Your State", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(Country)) {
            Toast.makeText(this, "Enter Your Country", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(City)) {
            Toast.makeText(this, "Enter Your City", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(deleveryfee)) {
            Toast.makeText(this, "Enter Your Delevery Fee", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(shopName)) {
            Toast.makeText(this, "Enter Your Shop Name", Toast.LENGTH_SHORT).show();
            return;
        }
        updateProfile();
    }

    private void updateProfile() {
        progressDialog.setMessage("updating profile...");
        progressDialog.show();
        if(image_uri == null) {
            HashMap<String,Object> map = new HashMap<>();
            map.put("fullName",fullName);
            map.put("address",Adress);
            map.put("Country",Country);
            map.put("State",State);
            map.put("City",City);
            map.put("phoneNo",phoneNo);
            map.put("shopName",shopName);
            map.put("deleveryfee",deleveryfee);
            map.put("shopOpen",""+shopOpen);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).updateChildren(map)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();
                            Toast.makeText(edit_seller_profile_Activity.this, "profile updated...", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(edit_seller_profile_Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else {
            String filePathAndName = "profile_images/"+firebaseAuth.getUid();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                              @Override
                                              public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                  //get uri of uploaded image
                                                  Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();

                                                  while (!uriTask.isSuccessful()) ;
                                                  Uri downloadImageUri = uriTask.getResult();
                                                  Log.d("register", "onSuccess: ");
                                                  if (uriTask.isSuccessful()) {
                                                      HashMap<String, Object> map = new HashMap<>();
                                                      map.put("fullName", fullName);
                                                      map.put("address", Adress);
                                                      map.put("Country", Country);
                                                      map.put("State", State);
                                                      map.put("City", City);
                                                      map.put("phoneNo", phoneNo);
                                                      map.put("shopName", shopName);
                                                      map.put("deleveryfee", deleveryfee);
                                                      map.put("shopOpen", "" + shopOpen);
                                                      map.put("icon",""+downloadImageUri);

                                                      DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                                      ref.child(firebaseAuth.getUid()).updateChildren(map)
                                                              .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                  @Override
                                                                  public void onSuccess(Void unused) {
                                                                      progressDialog.dismiss();
                                                                      Toast.makeText(edit_seller_profile_Activity.this, "profile updated...", Toast.LENGTH_SHORT).show();
                                                                  }
                                                              })
                                                              .addOnFailureListener(new OnFailureListener() {
                                                                  @Override
                                                                  public void onFailure(@NonNull Exception e) {
                                                                      progressDialog.dismiss();
                                                                      Toast.makeText(edit_seller_profile_Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                  }
                                                              });
                                                  }
                                              }
                                          });


        }
;



    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user == null) {
            startActivity(new Intent(edit_seller_profile_Activity.this,LoginActivity.class));
        }
        else {
            loadMyInfo();
        }
    }
    private void loadMyInfo() {


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild(("uid")).equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds : snapshot.getChildren()) {
                            String Name = ""+ds.child("fullName").getValue();
                            String accountType = ""+ds.child("accountType").getValue();
                            String Pno = ""+ds.child("phoneNo").getValue();
                            String city_ = ""+ds.child("City").getValue();
                            String state_ = ""+ds.child("State").getValue();
                            String country_ = ""+ds.child("Country").getValue();
                            String address_ = ""+ds.child("address").getValue();
                            String deleveryFee_ = ""+ds.child("deleveryfee").getValue();
                            String shopName_ = ""+ds.child("shopName").getValue();
                            String openClose =""+ds.child("shopOpen").getValue();
                            String icon_ = ""+ds.child("icon").getValue();

                            name.setText(Name);
                            pno.setText(Pno);
                            city.setText(city_);
                            state.setText(state_);
                            country.setText(country_);
                            address.setText(address_);
                            deliveryFee.setText(deleveryFee_);
                            shopname.setText(shopName_);
                            try {
                                Picasso.get().load(icon_).placeholder(R.drawable.ic_baseline_store_24).into(circularImageView);
                            }
                            catch (Exception e) {
                                circularImageView.setImageResource(R.drawable.ic_baseline_store_24);
                            }

                            if(openClose.equals("true")) {
                                shopSwitch.setChecked(true);
                            }
                            else {
                                shopSwitch.setChecked(false);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
    private void showImagePickDialog() {
        String[] options = {"Gallery" };
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
                circularImageView.setImageURI(data.getData());
            }
        }
    }



}