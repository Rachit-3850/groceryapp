package com.example.groceryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        firebaseAuth = FirebaseAuth.getInstance();

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(2000);
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if(user == null) {
                        Intent intent = new Intent(SplashActivity.this ,LoginActivity.class);
                        startActivity(intent);
                    }
                    else {
                        Intent intent = new Intent(SplashActivity.this ,LoginActivity.class);
                        startActivity(intent);
                        checkUserType();
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };thread.start();

    }
    private void checkUserType() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds : snapshot.getChildren()) {
                            String accountType = ""+ds.child("accountType").getValue();
                            if(accountType.equals("seller")) {
                                Intent intent = new Intent(SplashActivity.this,Main_Seller_Activity.class);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                Intent intent = new Intent(SplashActivity.this,Main_user_Activity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}