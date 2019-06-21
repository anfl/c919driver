package com.example.driverappcg19;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by ADITHYA AN on 01-06-2018.
 */

class MainViewHolder extends RecyclerView.ViewHolder
{

    TextView name,address,date,amount;
    Button accept,reject;
    Button start_bid;
    CardView cv;
    ImageView wash,dry,iron;


    public MainViewHolder(View itemView)
    {
        super(itemView);
        name=itemView.findViewById(R.id.name);
        date=itemView.findViewById(R.id.date);
        address=itemView.findViewById(R.id.address);
        amount=itemView.findViewById(R.id.amount);
        accept=itemView.findViewById(R.id.accept);
        reject=itemView.findViewById(R.id.reject);
        wash = itemView.findViewById(R.id.washicon);
        dry = itemView.findViewById(R.id.dryicon);
        iron = itemView.findViewById(R.id.ironicon);
    }

    public void inflate(final NewsFeedData model, final Context context)
    {
        Log.e("Data",model.toString());
        name.setText(model.getName());
        date.setText(model.getDate());
        address.setText(model.getAddress());
        amount.setText(model.getAmount());
        amount.setText("200");
        date.setText("21/06/2019");
        String icons = model.getCount();
        if (icons.contains("wash"))
            wash.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary));
        if (icons.contains("dry"))
            dry.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary));
        if (icons.contains("iron"))
            iron.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary));
//        Log.d("Total data",model.getCount());
//        Log.d("clothes",model.getCount().substring(0,2));




    }

}
