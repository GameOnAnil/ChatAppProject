package com.example.chatappproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpPage extends AppCompatActivity {
    private static final String TAG = "SignUpPage";

    EditText userNameTxt;
    EditText emailTxt;
    EditText passwordTxt;
    Button signUpBtn;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        Toolbar toolbar = findViewById(R.id.toolbar_signup);
        toolbar.setTitle("Sign Up New Account");
        setSupportActionBar(toolbar);

        userNameTxt = findViewById(R.id.signup_user_name);
        emailTxt = findViewById(R.id.signup_email);
        passwordTxt = findViewById(R.id.signup_password);
        signUpBtn = findViewById(R.id.signUp_btn);

        mAuth = FirebaseAuth.getInstance();

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = userNameTxt.getText().toString();
                String email = emailTxt.getText().toString();
                String pass = passwordTxt.getText().toString();

                signUp(email, pass);

            }
        });

    }

    private void signUp(String email, String pass) {
        Log.d(TAG, "signUp: ");
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUpPage.this, "SignUp successful", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onComplete: SignUp successful");

                            Intent intent = new Intent(SignUpPage.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                        } else {
                            Log.d(TAG, "onComplete: failure some error ");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: failed" + e);
            }
        });
    }
}