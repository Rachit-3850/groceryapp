package com.example.groceryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRatingBar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.HashMap;

public class review_shops_Activity extends AppCompatActivity {
    public String shopid;
    FloatingActionButton btn ;
    ImageView back;
    CircularImageView circularImageView;
    EditText text;
    TextView shop;
    AppCompatRatingBar rating;

    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_shops);
        shopid = getIntent().getStringExtra("shopUID");

        back = findViewById(R.id.back_review_shop_user);
        btn = findViewById(R.id.riview_btn_user);
        circularImageView = findViewById(R.id.review_photo_user);
        text = findViewById(R.id.review_text_user);
        rating = findViewById(R.id.review_rating_user);
        shop = findViewById(R.id.review_shopName_user);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        LayerDrawable stars=(LayerDrawable)rating.getProgressDrawable();

        //Use for changing the color of RatingBar
        stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);

        setImage();
//        // load data if user already rated the shop
        setDataIfAlreadyExist();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
//
//
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDataInDB();
            }
        });

    }

    private void setDataIfAlreadyExist() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopid).child("Ratings").child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            String rate =""+ snapshot.child("rating").getValue();
                            String data = ""+ snapshot.child("reviews").getValue();

                            text.setText(data);
                            float ratings = Float.parseFloat(rate);
                            rating.setRating(ratings);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void setDataInDB() {
        String rate =""+ rating.getRating();
        String data = ""+ text.getText().toString().trim();
        String timeStamp = ""+System.currentTimeMillis();

        HashMap<String , Object> map = new HashMap<>();
        map.put("rating" , rate);
        map.put("reviews" , data );
        map.put("uid" , firebaseAuth.getUid());
        map.put("timeStamp" , timeStamp);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopid).child("Ratings").child(firebaseAuth.getUid()).updateChildren(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        Toast.makeText(review_shops_Activity.this, "profile updated...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(review_shops_Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void setImage() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild(("uid")).equalTo(shopid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds : snapshot.getChildren()) {
                            String icon_ = ""+ds.child("icon").getValue();
                            String shopName = ""+ds.child("shopName").getValue();
                            shop.setText(shopName);
                            try {
                                Picasso.get().load(icon_).placeholder(R.drawable.ic_baseline_store_24).into(circularImageView);
                            }
                            catch (Exception e) {
                                circularImageView.setImageResource(R.drawable.ic_baseline_store_24);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}