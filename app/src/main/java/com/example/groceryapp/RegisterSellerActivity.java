package com.example.groceryapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
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
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class RegisterSellerActivity extends AppCompatActivity {
    EditText email, name, pno, password, confirm_passsword, deliveryFee, shopname, country, state, city, address;
    Button register;
    ImageView back;

    private final int GALLERY_REQUEST_CODE = 200;

    CircularImageView circularImageView;
    private String[] storagePermission;


    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    private Uri image_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_seller);

        circularImageView = findViewById(R.id.photo2);
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

        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        email = findViewById(R.id.Email_register_seller);
        name = findViewById(R.id.Name_register_seller);
        shopname = findViewById(R.id.ShopName_register_seller);
        pno = findViewById(R.id.pno_register_seller);
        country = findViewById(R.id.Country_register_seller);
        state = findViewById(R.id.state_register_seller);
        address = findViewById(R.id.address_register_seller);
        password = findViewById(R.id.password_register_seller);
        confirm_passsword = findViewById(R.id.confirm_password_register_seller);
        register = findViewById(R.id.register_btn_seller);
        deliveryFee = findViewById(R.id.fee_register_seller);
        city = findViewById(R.id.city_register_seller);
        back = findViewById((R.id.back_register_seller));

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputData();

            }
        });

        circularImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePickDialog();

            }
        });

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
                circularImageView.setImageURI(data.getData());
            }
        }
    }

    private String fullName, shopName, phoneNo, deleveryfee, Country, State, City, Adress, pass, confPass, Email;

    private void inputData() {
        fullName = name.getText().toString().trim();
        phoneNo = pno.getText().toString().trim();
        shopName = shopname.getText().toString().trim();
        Country = country.getText().toString().trim();
        State = state.getText().toString().trim();
        City = city.getText().toString().trim();
        Adress = address.getText().toString().trim();
        pass = password.getText().toString().trim();
        confPass = confirm_passsword.getText().toString().trim();
        deleveryfee = deliveryFee.getText().toString().trim();
        Email = email.getText().toString().trim();

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

        if (TextUtils.isEmpty(deleveryfee)) {
            Toast.makeText(this, "Enter Your delevery free", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pass.length() < 6) {
            Toast.makeText(this, "Passward must be atleast 6 character long", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!pass.equals(confPass)) {
            Toast.makeText(this, "Password does't Match", Toast.LENGTH_SHORT).show();
            return;
        }
        createAccount();
    }

    private void createAccount() {
        progressDialog.setMessage("Creating account...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(Email, pass)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //account created
                        saveFireBaseData();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterSellerActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveFireBaseData() {
        progressDialog.setMessage("saving account info");
        if(image_uri == null) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("uid", "" + firebaseAuth.getUid());
            map.put("fullName", fullName);
            map.put("Email", Email);
            map.put("address", Adress);
            map.put("Country", Country);
            map.put("State", State);
            map.put("City", City);
            map.put("phoneNo", phoneNo);
            map.put("shopName", shopName);
            map.put("deleveryfee", deleveryfee);
            map.put("accountType", "seller");
            map.put("online", "true");
//        map.put("openClose" , "true");
            map.put("shopOpen", "true");
            map.put("icon", "");
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).setValue(map)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();
                            Intent intent = new Intent(RegisterSellerActivity.this, Main_Seller_Activity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Intent intent = new Intent(RegisterSellerActivity.this, Main_Seller_Activity.class);
                            startActivity(intent);
                            finish();
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

                            while (!uriTask.isSuccessful());
                            Uri downloadImageUri = uriTask.getResult();
                            Log.d("register", "onSuccess: ");
                            if(uriTask.isSuccessful()) {
                                HashMap<String, Object> map = new HashMap<>();
                                map.put("uid", "" + firebaseAuth.getUid());
                                map.put("fullName", fullName);
                                map.put("Email", Email);
                                map.put("address", Adress);
                                map.put("Country", Country);
                                map.put("State", State);
                                map.put("City", City);
                                map.put("phoneNo", phoneNo);
                                map.put("shopName", shopName);
                                map.put("deleveryfee", deleveryfee);
                                map.put("accountType", "seller");
                                map.put("online", "true");
//        map.put("openClose" , "true");
                                map.put("shopOpen", "true");
                                map.put("icon", ""+downloadImageUri);
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                ref.child(firebaseAuth.getUid()).setValue(map)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                Intent intent = new Intent(RegisterSellerActivity.this, Main_Seller_Activity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                Intent intent = new Intent(RegisterSellerActivity.this, Main_Seller_Activity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterSellerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }
}