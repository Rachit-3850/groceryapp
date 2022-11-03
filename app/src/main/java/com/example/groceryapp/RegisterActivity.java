package com.example.groceryapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import android.util.Patterns;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    EditText email, name, pno, password, confirm_passsword, country, state, city, address;
    Button register;
    ImageView back;
    TextView seller;
    private final int REQUEST_CAMERA_CODE = 100;
    private final int GALLERY_REQUEST_CODE = 200;
    private final int IMAGE_REQUEST_GALLERY_CODE = 300;
    private final int CAMERA_REQUEST_GALLERY_CODE = 400;
    private final int REQUEST_STORAGE_CODE = 500;
    CircularImageView circularImageView;
    private String[] storagePermission;
    private String[] cameraPermission;

    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    private Uri image_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        circularImageView = findViewById(R.id.photo);
        circularImageView.setImageResource(R.drawable.user);
        circularImageView.setBackgroundColor(getResources().getColor(R.color.grey1));
        circularImageView.setBorderWidth(5);
        circularImageView.setBorderWidth(10);
        circularImageView.setBorderColor(getResources().getColor(R.color.grey1));
        circularImageView.setShadowGravity(CircularImageView.ShadowGravity.BOTTOM);
        circularImageView.setShadowRadius(9);

        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};


        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("please wait");
        progressDialog.setCanceledOnTouchOutside(false);


        email = findViewById(R.id.Email_register);
        name = findViewById(R.id.Name_register);
        pno = findViewById(R.id.pno_register);
        country = findViewById(R.id.Country_register);
        state = findViewById(R.id.state_register);
        city = findViewById(R.id.city_register);
        address = findViewById(R.id.address_register);
        password = findViewById(R.id.password_register);
        confirm_passsword = findViewById(R.id.confirm_password_register);
        register = findViewById(R.id.register_btn);
        seller = findViewById(R.id.seller);
        back = findViewById((R.id.back_register_user));

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
//                Intent intent = new Intent(RegisterActivity.this, Main_user_Activity.class);
//                startActivity(intent);
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

        seller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(RegisterActivity.this, RegisterSellerActivity.class);
                startActivity(intent);
            }
        });

    }

    private String fullName, phoneNo, Country, State, City, Adress, pass, confPass, Email;

    private void inputData() {
        fullName = name.getText().toString().trim();
        phoneNo = pno.getText().toString().trim();
        Country = country.getText().toString().trim();
        State = state.getText().toString().trim();
        City = city.getText().toString().trim();
        Adress = address.getText().toString().trim();
        pass = password.getText().toString().trim();
        confPass = confirm_passsword.getText().toString().trim();
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
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
            map.put("accountType", "User");
            map.put("icon" , "");
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).setValue(map)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();
                            Intent intent = new Intent(RegisterActivity.this, Main_user_Activity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Intent intent = new Intent(RegisterActivity.this, Main_user_Activity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
        }
        else {
            //if image is uploaded
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
                                map.put("accountType", "User");
                                map.put("icon" , ""+downloadImageUri);
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                ref.child(firebaseAuth.getUid()).setValue(map)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                Intent intent = new Intent(RegisterActivity.this, Main_user_Activity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                Intent intent = new Intent(RegisterActivity.this, Main_user_Activity.class);
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
                            Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
//        HashMap<String, Object> map = new HashMap<>();
//        map.put("uid", "" + firebaseAuth.getUid());
//        map.put("fullName", fullName);
//        map.put("Email", Email);
//        map.put("address", Adress);
//        map.put("Country", Country);
//        map.put("State", State);
//        map.put("City", City);
//        map.put("phoneNo", phoneNo);
//        map.put("accountType", "User");
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
//        ref.child(firebaseAuth.getUid()).setValue(map)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        progressDialog.dismiss();
//                        Intent intent = new Intent(RegisterActivity.this, Main_user_Activity.class);
//                        startActivity(intent);
//                        finish();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        progressDialog.dismiss();
//                        Intent intent = new Intent(RegisterActivity.this, Main_user_Activity.class);
//                        startActivity(intent);
//                        finish();
//                    }
//                });
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
