package com.example.driverappcg19;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignupActivity extends AppCompatActivity {

    TextView login;
    SharedPreferences sharedPreferences;
    EditText name,email,password;
    AppCompatButton register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        login = findViewById(R.id.link_signup);
        name = findViewById(R.id.input_name);
        email = findViewById(R.id.input_email);
        register = findViewById(R.id.btn_login);
        sharedPreferences = getSharedPreferences("myPref",MODE_PRIVATE);
        password = findViewById(R.id.input_password);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String n = name.getText().toString();
                String e = email.getText().toString();
                String p = password.getText().toString();
                if (n.isEmpty()||e.isEmpty()||p.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please fill the required fields!!!", Toast.LENGTH_SHORT).show();
                }
                else{
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("username",n);
                    editor.putString("password",p);
                    editor.putString("email",e);
                    editor.apply();
                    editor.commit();
                }
            }
        });


    }
}
