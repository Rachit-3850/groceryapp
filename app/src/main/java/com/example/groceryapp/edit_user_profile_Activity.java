package com.example.groceryapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.TextView;
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

public class edit_user_profile_Activity extends AppCompatActivity {
    EditText name, pno, country, state, city, address;
    Button update;
    ImageView back;

    private final int REQUEST_CAMERA_CODE = 100;
    private final int GALLERY_REQUEST_CODE = 200;

    private Uri image_uri;

    CircularImageView circularImageView;
//    private String[] storagePermission;
//    private String[] cameraPermission;

    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);

        circularImageView = findViewById(R.id.editPhoto_user);
        circularImageView.setImageResource(R.drawable.user);
        circularImageView.setBackgroundColor(getResources().getColor(R.color.grey1));
        circularImageView.setBorderWidth(5);
        circularImageView.setBorderWidth(10);
        circularImageView.setBorderColor(getResources().getColor(R.color.grey1));
        circularImageView.setShadowGravity(CircularImageView.ShadowGravity.BOTTOM);
        circularImageView.setShadowRadius(9);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        name = findViewById(R.id.Name_edit);
        pno = findViewById(R.id.pno_edit);
        country = findViewById(R.id.Country_edit);
        state = findViewById(R.id.state_edit);
        city = findViewById(R.id.city_edit);
        address = findViewById(R.id.address_edit);
        update = findViewById(R.id.edit_btn);
        circularImageView = findViewById(R.id.editPhoto_user);

        back = findViewById((R.id.back_edit_user));

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
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intent,REQUEST_CAMERA_CODE);
            }
        });
    }

    private String fullName, phoneNo, Country, State, City, Adress;

    private void inputdata() {
        fullName = name.getText().toString().trim();
        phoneNo = pno.getText().toString().trim();
        Country = country.getText().toString().trim();
        State = state.getText().toString().trim();
        City = city.getText().toString().trim();
        Adress = address.getText().toString().trim();

        if (TextUtils.isEmpty(fullName)) {
            Toast.makeText(this, "Enter Your Name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(Adress)) {
            Toast.makeText(this, "Enter Your Address", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(phoneNo)) {
            Toast.makeText(this, "Enter Your Phone No", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(State)) {
            Toast.makeText(this, "Enter Your State", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(Country)) {
            Toast.makeText(this, "Enter Your Country", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(City)) {
            Toast.makeText(this, "Enter Your City", Toast.LENGTH_SHORT).show();
            return;
        }
        updateProfile();
    }

    private void updateProfile() {
        progressDialog.setMessage("updating profile...");
        progressDialog.show();
        if(image_uri == null) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("fullName", fullName);
            map.put("address", Adress);
            map.put("Country", Country);
            map.put("State", State);
            map.put("City", City);
            map.put("phoneNo", phoneNo);


            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).updateChildren(map)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();
                            Toast.makeText(edit_user_profile_Activity.this, "profile updated...", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(edit_user_profile_Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                                map.put("icon" ,""+ downloadImageUri);


                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                ref.child(firebaseAuth.getUid()).updateChildren(map)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                Toast.makeText(edit_user_profile_Activity.this, "profile updated...", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                Toast.makeText(edit_user_profile_Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    });
        }
//        HashMap<String, Object> map = new HashMap<>();
//        map.put("fullName", fullName);
//        map.put("address", Adress);
//        map.put("Country", Country);
//        map.put("State", State);
//        map.put("City", City);
//        map.put("phoneNo", phoneNo);
//
//
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
//        ref.child(firebaseAuth.getUid()).updateChildren(map)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        progressDialog.dismiss();
//                        Toast.makeText(edit_user_profile_Activity.this, "profile updated...", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        progressDialog.dismiss();
//                        Toast.makeText(edit_user_profile_Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });


    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(edit_user_profile_Activity.this, LoginActivity.class));
        } else {
            loadMyInfo();
        }
    }

    private void loadMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild(("uid")).equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String Name = "" + ds.child("fullName").getValue();
                            String Pno = "" + ds.child("phoneNo").getValue();
                            String city_ = "" + ds.child("City").getValue();
                            String state_ = "" + ds.child("State").getValue();
                            String country_ = "" + ds.child("Country").getValue();
                            String address_ = "" + ds.child("address").getValue();
                            String icon_ = "" + ds.child("icon").getValue();

                            try {
                                Picasso.get().load(icon_).placeholder(R.drawable.user).into(circularImageView);
                            }
                            catch (Exception e) {
                                circularImageView.setImageResource(R.drawable.user);
                            }

                            name.setText(Name);
                            pno.setText(Pno);
                            city.setText(city_);
                            state.setText(state_);
                            country.setText(country_);
                            address.setText(address_);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

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
    private void pickFromCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE,"Temp img title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"Temp img discription");

        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(intent , REQUEST_CAMERA_CODE);
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

}