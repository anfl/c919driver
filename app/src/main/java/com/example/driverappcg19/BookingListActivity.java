package com.example.driverappcg19;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class BookingListActivity extends AppCompatActivity implements OnBookingAccepted{
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_list);
        recyclerView=findViewById(R.id.recyclerview);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("Bookings");
        Query query = myRef;
        FirebaseRecyclerOptions<NewsFeedData> options =
                new FirebaseRecyclerOptions.Builder<NewsFeedData>()
                        .setQuery(query,  NewsFeedData.class)
                        .setLifecycleOwner(this)
                        .build();
        final FirebaseRecyclerAdapter firebaseRecyclerAdapter;
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<NewsFeedData, MainViewHolder>(options) {
            @Override
            public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = getLayoutInflater()
                        .inflate(R.layout.booked_history_item, parent,false);

                return new MainViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final MainViewHolder holder, final int position, final NewsFeedData model) {
                //Do settext here
                final OnBookingAccepted onBookingAccepted=BookingListActivity.this;
                holder.inflate(model,getApplicationContext());
                holder.accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBookingAccepted.moveToNavigationPage(holder,position,model);
                    }
                });
                holder.reject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

//                        Query query1 = myRef.equalTo("Sivakasi");
//                        query1.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
//                                    appleSnapshot.getRef().removeValue();
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                            }
//                        });
                    }
                });
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                Log.e("Data","Got DAta");
            }

            @Override
            public void onError(@NonNull DatabaseError error) {
                super.onError(error);
                Log.e("Error",error.getMessage());
            }
        };

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    public void moveToNavigationPage(MainViewHolder holder, int pos, NewsFeedData model) {

        Intent intent=new Intent(BookingListActivity.this,MainActivity.class);

        intent.putExtra("lat",model.getLatitude());
        intent.putExtra("long",model.getLongitude());

        Log.e("moveToNavigationPage: ","lat"+model.getLatitude()+"   long"+model.getLongitude() );
        startActivity(intent);


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
