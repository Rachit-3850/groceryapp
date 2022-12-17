package com.example.groceryapp;

public class ModelReview {
    String rating , reviews , uid , timeStamp;

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getReviews() {
        return reviews;
    }

    public void setReviews(String reviews) {
        this.reviews = reviews;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public ModelReview(String rating, String reviews, String uid, String timeStamp) {
        this.rating = rating;
        this.reviews = reviews;
        this.uid = uid;
        this.timeStamp = timeStamp;
    }
}
