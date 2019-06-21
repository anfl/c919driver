package com.example.driverappcg19;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.JsonArray;
import com.google.zxing.Result;
import com.here.android.mpa.common.GeoCoordinate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Main activity which launches map view and handles Android run-time requesting permission.
 */

public class MainActivity extends AppCompatActivity  {

    String url1 = "https://wse.api.here.com/2/findsequence.json ?start=Berlin-Main-Station;52.52282,13.37011 &destination1=East-Side-Gallery;52.50341,13.44429 &destination2=Olympiastadion;52.51293,13.24021 &end=HERE-Berlin-Campus;52.53066,13.38511 &mode=fastest;car;&app_id= Q2PXMfWkluvQttaqa0Gi &app_code=vlrjsQAWKFWhTxCJ-1dWdg";
    String url2="https://weather.api.here.com/weather/1.0/report.json" +
            "?app_id=Q2PXMfWkluvQttaqa0Gi" +
            "&app_code=vlrjsQAWKFWhTxCJ-1dWdg" +
            "&product=observation" +
            "&name=Berlin";

    String url ="https://weather.api.here.com/weather/1.0/report.json?app_id=Q2PXMfWkluvQttaqa0Gi&app_code=vlrjsQAWKFWhTxCJ-1dWdg&product=observation&name=Chrompet";



    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private static final String[] RUNTIME_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.FOREGROUND_SERVICE
    };
    GeoCoordinate destination;
    Double l1;
    Double l2;
    private MapFragmentView m_mapFragmentView;
    ImageView scanner;
    Toolbar toolbar;
    int check=0;
    ProgressBar pgbar;
    TextView getdirections;
    FloatingActionButton locate,weather;
    String key,skyDescription,temperature,temperatureDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String lat=getIntent().getExtras().getString("lat");
        key=getIntent().getExtras().getString("key");
//        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        String lng=getIntent().getExtras().getString("long");
        toolbar = findViewById(R.id.toolbar);
        pgbar = findViewById(R.id.pg_bar);
        pgbar.setVisibility(View.VISIBLE);
        weather = findViewById(R.id.weather);
        getWeatherDetails();
        weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayWeather();
            }
        });
        locate = findViewById(R.id.locate);
        locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                JsonObjectRequest deleteNoticeRequest = new JsonObjectRequest(Request.Method.GET, url,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONObject observations = response.getJSONObject("observations");

                                    JSONArray location = observations.getJSONArray("location");

                                    JSONObject observation = location.getJSONObject(0);

                                    JSONArray observe = observation.getJSONArray("observation");

                                    JSONObject details = (JSONObject) observe.get(0);

                                    Log.d("description",details.toString());

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Log.d("Error",e.getMessage());
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
                queue.add(deleteNoticeRequest);

            }
        });


        getdirections = findViewById(R.id.get_directions);
        getdirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.support.v7.app.AlertDialog.Builder alertbuilder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
                alertbuilder.setCancelable(true);
                View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.rating,null);
                v.setVerticalScrollBarEnabled(false);
                alertbuilder.setView(v);
                final android.support.v7.app.AlertDialog dialog = alertbuilder.create();
                dialog.show();
                Button ok;
                ok = v.findViewById(R.id.okay_feed);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

            }
        });
        scanner = findViewById(R.id.scanner);
        scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Scanner_Activity.class);
                startActivityForResult(intent,2);
                check=1;

            }
        });
        l1 = Double.parseDouble(lat);

        l2 = Double.parseDouble(lng);

        Log.e("onCreate: ","lat"+lat+"  long"+lng );
        if (hasPermissions(this, RUNTIME_PERMISSIONS)) {
            setupMapFragmentView();
        } else {
            ActivityCompat
                    .requestPermissions(this, RUNTIME_PERMISSIONS, REQUEST_CODE_ASK_PERMISSIONS);
        }
    }




    /**
     * Only when the app's target SDK is 23 or higher, it requests each dangerous permissions it
     * needs when the app is running.
     */
    private static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==2){
                customerVerified();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS: {
                for (int index = 0; index < permissions.length; index++) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {

                        /*
                         * If the user turned down the permission request in the past and chose the
                         * Don't ask again option in the permission request system dialog.
                         */
                        if (!ActivityCompat
                                .shouldShowRequestPermissionRationale(this, permissions[index])) {
//                            Toast.makeText(this, "Required permission " + permissions[index]
//                                            + " not granted. "
//                                            + "Please go to settings and turn on for sample app",
//                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "Required permission " + permissions[index]
                                    + " not granted", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                setupMapFragmentView();
                break;
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void setupMapFragmentView() {
        // All permission requests are being handled. Create map fragment view. Please note
        // the HERE SDK requires all permissions defined above to operate properly.

        m_mapFragmentView = new MapFragmentView(this,l1,l2,pgbar,key);
    }

    @Override
    public void onDestroy() {
        m_mapFragmentView.onDestroy();
        super.onDestroy();
    }

    public void customerVerified(){

        android.support.v7.app.AlertDialog.Builder alertbuilder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
        alertbuilder.setCancelable(true);
        View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_loader,null);
        v.setVerticalScrollBarEnabled(false);
        alertbuilder.setView(v);
        final android.support.v7.app.AlertDialog dialog = alertbuilder.create();
        dialog.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("Customer Verified Successfully!!!");
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialog.show();
            }
        },5000);



    }

    private void displayWeather(){

        android.support.v7.app.AlertDialog.Builder alertbuilder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
        alertbuilder.setCancelable(true);
        View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_weather,null);
        v.setVerticalScrollBarEnabled(false);
        alertbuilder.setView(v);
        final android.support.v7.app.AlertDialog dialog = alertbuilder.create();
        dialog.show();
        TextView temp,tempdesc,skydesc;
        temp = v.findViewById(R.id.temp);
        tempdesc = v.findViewById(R.id.temp_desc);
        skydesc = v.findViewById(R.id.sky_desc);
        temp.setText(temperature);
        tempdesc.setText(temperatureDesc);
        skydesc.setText(skyDescription);


    }

    private void getWeatherDetails(){

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest deleteNoticeRequest = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject observations = response.getJSONObject("observations");

                            JSONArray location = observations.getJSONArray("location");

                            JSONObject observation = location.getJSONObject(0);

                            JSONArray observe = observation.getJSONArray("observation");

                            JSONObject details = (JSONObject) observe.get(0);

                            skyDescription = details.getString("skyDescription");
                            temperature = details.getString("temperature");
                            temperatureDesc = details.getString("temperatureDesc");


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("Error",e.getMessage());
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(deleteNoticeRequest);

    }



}
