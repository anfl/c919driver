package com.example.driverappcg19;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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


    public MainViewHolder(View itemView)
    {
        super(itemView);
        name=itemView.findViewById(R.id.name);
        date=itemView.findViewById(R.id.date);
        address=itemView.findViewById(R.id.address);
        amount=itemView.findViewById(R.id.amount);
        accept=itemView.findViewById(R.id.accept);
        reject=itemView.findViewById(R.id.reject);

    }

    public void inflate(final NewsFeedData model, final Context context)
    {
        Log.e("Data",model.toString());
        name.setText(model.getName());
        name.setAllCaps(true);
        date.setText(model.getDate());
        address.setText(model.getAddress());
        amount.setText(model.getAmount());
        amount.setText("200");
        date.setText("23/05/2019");


    }

}
