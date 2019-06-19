package com.example.driverappcg19;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity implements BookingsFragment.OnFragmentInteractionListener,HistoryFragment.OnFragmentInteractionListener, ProfileFragment.OnFragmentInteractionListener{

    BottomNavigationView navigation;
    Toolbar toolbar;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = findViewById(R.id.toolbar1);
        title = findViewById(R.id.titletoolbar);
        BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
                = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.work:
                        title.setText("Home");
                        loadFragment(new BookingsFragment());
                        return true;
                    case R.id.history:
                        title.setText("History");
                        loadFragment(new HistoryFragment());
                        return true;
                    case R.id.profile:
                        title.setText("Profile");
                        loadFragment(new ProfileFragment());
                        return true;
                }

                return false;
            }
        };
        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Fragment fragment = new BookingsFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.frame_container, fragment);
        transaction.disallowAddToBackStack();
        transaction.commit();

    }
    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.disallowAddToBackStack();
        transaction.commit();
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
