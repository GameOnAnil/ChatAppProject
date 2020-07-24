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
import com.google.firebase.auth.FirebaseUser;

public class SignUpPage extends AppCompatActivity {
    private static final String TAG = "SignUpPage";

    EditText userNameTxt;
    EditText emailTxt;
    EditText passwordTxt;
    Button signUpBtn;

    ProgressDialog mProgressDialog;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        Toolbar toolbar = findViewById(R.id.toolbar_signup);
        toolbar.setTitle("Sign Up New Account");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userNameTxt = findViewById(R.id.signup_user_name);
        emailTxt = findViewById(R.id.signup_email);
        passwordTxt = findViewById(R.id.signup_password);
        signUpBtn = findViewById(R.id.signUp_btn);

        mAuth = FirebaseAuth.getInstance();

        mProgressDialog = new ProgressDialog(this);


        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = userNameTxt.getText().toString();
                String email = emailTxt.getText().toString();
                String pass = passwordTxt.getText().toString();

                if(user.isEmpty() || email.isEmpty() || pass.isEmpty()){
                    Toast.makeText(SignUpPage.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                }else{
                    mProgressDialog.setTitle("Registering user");
                    mProgressDialog.setMessage("Please wait while we create your account");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();
                    signUp(user,email, pass);
                }

            }
        });

    }

    private void signUp(final String user, String email, String pass) {
        Log.d(TAG, "signUp: ");
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mProgressDialog.dismiss();
                            Toast.makeText(SignUpPage.this, "SignUp successful", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onComplete: SignUp successful");

                            String uid = mAuth.getUid();

                            Intent intent = new Intent(SignUpPage.this, CreateProfile.class);
                            intent.putExtra("uid",uid);
                            intent.putExtra("user name",user);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();

                        } else {
                            mProgressDialog.dismiss();
                            task.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: task failed"+e);
                                    Toast.makeText(SignUpPage.this, "task failed:"+e, Toast.LENGTH_LONG).show();
                                }
                            });

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: failed" + e);
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