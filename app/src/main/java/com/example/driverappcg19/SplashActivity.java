package com.example.driverappcg19;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));

        new LogoLauncher().start();

    }


    public class LogoLauncher extends Thread{

        public void run(){
            try{
                sleep(3000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(intent);
            finish();

        }
    }
}
