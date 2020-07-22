package com.example.chatappproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LogInPage extends AppCompatActivity {
    EditText emailTxt;
    EditText passwordTxt;
    Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_page);

        Toolbar toolbar = findViewById(R.id.toolbar_login);
        toolbar.setTitle("Sign Up New Account");
        setSupportActionBar(toolbar);


        emailTxt = findViewById(R.id.login_email);
        passwordTxt = findViewById(R.id.login_password);
        loginBtn = findViewById(R.id.login_btn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}