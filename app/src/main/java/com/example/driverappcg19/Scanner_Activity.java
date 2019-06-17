package com.example.driverappcg19;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.zxing.Result;
import com.here.android.mpa.urbanmobility.Alert;

import java.util.Scanner;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission_group.CAMERA;

public class Scanner_Activity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;
    MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorBlack));
        setTitle("Scanning Customer QR...");
        mainActivity = new MainActivity();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission()) {
                Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                requestPermission();
            }
        }
    }

    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(Scanner_Activity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }


    public void onRequestPermissionsResult(int requestCode, String permission[], int grantResults[]) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted) {
                        Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                        if (shouldShowRequestPermissionRationale(CAMERA)) {
                            displayAlertMessage("You need to allow access for both permissions", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                                        requestPermissions(new String[]{CAMERA},REQUEST_CAMERA);
                                    }

                                }
                            });
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkPermission()){
                if (scannerView==null){
                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);
                }
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            }else{
                requestPermission();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }

    public void displayAlertMessage(String message, DialogInterface.OnClickListener listener) {

        new AlertDialog.Builder(Scanner_Activity.this)
                .setMessage(message)
                .setPositiveButton("OK",listener)
                .setNegativeButton("Cancel",null)
                .create()
                .show();

    }



    @Override
    public void handleResult(Result result) {

        final String scanResult = result.getText();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scan Result");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                scannerView.resumeCameraPreview(Scanner_Activity.this);
            }
        });
        builder.setNeutralButton("Visit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(scanResult));
                startActivity(intent);
            }
        });
//        builder.setMessage(scanResult);
//        AlertDialog alert = builder.create();
//        alert.show();


//        android.support.v7.app.AlertDialog.Builder alertbuilder = new android.support.v7.app.AlertDialog.Builder(Scanner_Activity.this);
//        alertbuilder.setCancelable(true);
//        View v = LayoutInflater.from(Scanner_Activity.this).inflate(R.layout.dialog_loader,null);
//        v.setVerticalScrollBarEnabled(false);
//        alertbuilder.setView(v);
//        final android.support.v7.app.AlertDialog dialog = alertbuilder.create();
//        dialog.show();
        onBackPressed();

//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
////                dialog.dismiss();
//                onBackPressed();
////                final android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(Scanner_Activity.this);
////                alertDialog.setTitle("Customer Verified Successfully!!!");
////                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
////                    @Override
////                    public void onClick(DialogInterface dialog, int which) {
////
////                    }
////                });
////                alertDialog.show();
//
//            }
//        },5000);
//        onBackPressed();

    }





}