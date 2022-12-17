package com.example.groceryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class shop_riview_Activity extends AppCompatActivity {
    String shopUID;

    ImageView back;
    CircularImageView circularImageView;
    TextView shop;
    AppCompatRatingBar rating;
    TextView ratingDouble ;
    RecyclerView rvReview;

    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    ArrayList<ModelReview> reviewList = new ArrayList<>();
    AdapterReviewUser adapterReviewUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_riview);
        shopUID = getIntent().getStringExtra("shopUID");

        back = findViewById(R.id.back_shop_review_user);
        circularImageView = findViewById(R.id.photo_shop_review_user);
        rating = findViewById(R.id.rate_shop_review_user);
        ratingDouble = findViewById(R.id.double_shop_review_user);
        shop = findViewById(R.id.name_shop_review_user);
        rvReview = findViewById(R.id.rv_shop_review_user);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        LayerDrawable stars=(LayerDrawable)rating.getProgressDrawable();

        stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
        setImage();
        loadReviews();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }
    double ratingSum = 0;
    private void loadReviews() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUID).child("Ratings")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        reviewList.clear();
                        ratingSum = 0;
                        for(DataSnapshot ds : snapshot.getChildren()) {
                            double rate = Double.parseDouble(""+ds.child("rating").getValue());
                            ratingSum = ratingSum + rate;
                            String rateIt = ""+rate;
                            String discription_ = ""+ds.child("reviews").getValue();
                            String uid_ =  ""+ds.child("uid").getValue();
                            String timeStamp = ""+ds.child("timeStamp").getValue();
                            ModelReview modelReview = new ModelReview(rateIt , discription_ , uid_ , timeStamp);
                            reviewList.add(modelReview);
                        }
                        rvReview.setLayoutManager(new LinearLayoutManager(shop_riview_Activity.this
                        ));
                        adapterReviewUser =  new AdapterReviewUser(shop_riview_Activity.this,reviewList);
                        //set adapter
                        rvReview.setAdapter(adapterReviewUser);
//
                        long noOfReviews = snapshot.getChildrenCount();
                        Log.d("reviews", "onDataChange: "+noOfReviews);
                        double avgRating = ratingSum / noOfReviews;
                        rating.setRating((float) avgRating);
                        ratingDouble.setText(""+avgRating+"["+noOfReviews+"]");

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void setImage() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild(("uid")).equalTo(shopUID)
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