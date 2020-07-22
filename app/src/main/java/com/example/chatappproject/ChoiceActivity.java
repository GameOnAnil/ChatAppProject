package com.example.chatappproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

public class ChoiceActivity extends AppCompatActivity {
    Button loginExistingBtn;
    Button signUpNewBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);

        Toolbar toolbar = findViewById(R.id.toolbar_choice);
        toolbar.setTitle("Choose Account Option");
        setSupportActionBar(toolbar);

        loginExistingBtn = findViewById(R.id.btn_login_exists);
        signUpNewBtn = findViewById(R.id.btn_create_new );

        loginExistingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChoiceActivity.this,LogInPage.class);
                startActivity(intent);
            }
        });

        signUpNewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ChoiceActivity.this,SignUpPage.class);
                startActivity(intent);
            }
        });
    }
}