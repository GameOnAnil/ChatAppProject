package com.example.chatappproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.lang.reflect.Field;

public class LogInPage extends AppCompatActivity {
    private static final String TAG = "LogInPage";
    EditText emailTxt;
    EditText passwordTxt;
    Button loginBtn;
    FirebaseAuth mAuth;
    ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_page);

        Toolbar toolbar = findViewById(R.id.toolbar_login);
        toolbar.setTitle("Sign Up New Account");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();


        emailTxt = findViewById(R.id.login_email);
        passwordTxt = findViewById(R.id.login_password);
        loginBtn = findViewById(R.id.login_btn);

        mProgress = new ProgressDialog(this);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailTxt.getText().toString();
                String pass = passwordTxt.getText().toString();



                if(email.isEmpty() && pass.isEmpty()){
                    Toast.makeText(LogInPage.this, "Please enter both email and password", Toast.LENGTH_SHORT).show();

                }else if(email.isEmpty()){
                    Toast.makeText(LogInPage.this, "Please enter email", Toast.LENGTH_SHORT).show();
                }
                else if(pass.isEmpty()){
                    Toast.makeText(LogInPage.this, "Please enter password", Toast.LENGTH_SHORT).show();
                }
                else{
                    mProgress.setCanceledOnTouchOutside(false);
                    mProgress.setTitle("Logging into your account ");
                    mProgress.setMessage("Please wait while we check your credentials");
                    mProgress.show();
                    login(email,pass);
                }
            }
        });
    }

    private void login(String email, String pass) {
        mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    mProgress.dismiss();
                    Toast.makeText(LogInPage.this, "login successful", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onComplete: login successful");
                    Intent intent = new Intent(LogInPage.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    task.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mProgress.dismiss();
                            Log.d(TAG, "onFailure: task failure "+e);
                            Toast.makeText(LogInPage.this, "failure:"+e, Toast.LENGTH_LONG).show();
                        }
                    });
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: "+e);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}