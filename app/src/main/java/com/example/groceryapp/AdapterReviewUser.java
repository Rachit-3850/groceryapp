package com.example.groceryapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterReviewUser extends RecyclerView.Adapter<AdapterReviewUser.HolderReview> {
    private Context context;

    public AdapterReviewUser(Context context, ArrayList<ModelReview> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
    }

    ArrayList<ModelReview> reviewList = new ArrayList<>();

    @NonNull
    @Override
    public HolderReview onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.review_user_design,parent,false);
        return new HolderReview(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull HolderReview holder, int position) {
        ModelReview modelReview = reviewList.get(position);
        String uid = modelReview.getUid();
        String time = modelReview.getTimeStamp();
        String rating = modelReview.getRating();
        String dis = modelReview.getReviews();

        String dateFormat = "dd/MM/yyyy";
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(time));
        String formatDate = formatter.format(calendar.getTime()).toString();
        holder.date.setText(formatDate);
        holder.discriptions.setText(dis);
        float ratings = Float.parseFloat(rating);
        holder.rating.setRating(ratings);
//        LayerDrawable stars=(LayerDrawable)holder.rating.getProgressDrawable();
//
//        stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);f
        loadUserDetails(holder , modelReview);
    }

    private void loadUserDetails(HolderReview holder, ModelReview modelReview) {
        String uid = modelReview.getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name = ""+snapshot.child("fullName").getValue();
                        String icon = ""+snapshot.child("icon").getValue();
                        holder.name.setText(name);
                        try {
                            Picasso.get().load(icon).placeholder(R.drawable.user).into(holder.photo);
                        }
                        catch (Exception e) {
                            holder.photo.setImageResource(R.drawable.user);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    class HolderReview extends RecyclerView.ViewHolder {
        ImageView photo ;
        TextView date , discriptions , name;
        AppCompatRatingBar rating;
        public HolderReview(@NonNull View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.card_review_user_photo);
            date = itemView.findViewById(R.id.card_review_user_date);
            discriptions = itemView.findViewById(R.id.card_review_user_discription);
            rating = itemView.findViewById(R.id.card_review_user_rating);
            name = itemView.findViewById(R.id.card_review_user_title);
        }
    }
}
