package com.example.driverappcg19;

import com.google.firebase.database.DatabaseReference;

public interface OnBookingAccepted {
    void moveToNavigationPage(MainViewHolder holder, int pos, NewsFeedData model, DatabaseReference dr);
}
