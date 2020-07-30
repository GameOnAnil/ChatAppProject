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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class SignUpPage extends AppCompatActivity {
    private static final String TAG = "SignUpPage";

    TextInputEditText userNameTxt;
    TextInputEditText emailTxt;
    TextInputEditText passwordTxt;
    TextInputLayout passwordLayout;
    TextInputLayout userNameLayout;
    TextInputLayout emailLayout;
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
        userNameLayout = findViewById(R.id.signup_user_name_layout);
        emailLayout = findViewById(R.id.signup_email_layout);
        passwordLayout = findViewById(R.id.signup_password_layout);

        mAuth = FirebaseAuth.getInstance();

        mProgressDialog = new ProgressDialog(this);


        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = userNameTxt.getText().toString();
                String email = emailTxt.getText().toString();
                String pass = passwordTxt.getText().toString();

                if (user.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(SignUpPage.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                } else {
                    mProgressDialog.setTitle("Registering user");
                    mProgressDialog.setMessage("Please wait while we create your account");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();
                    signUp(user, email, pass);
                }

            }
        });

    }

    private void signUp(final String user, String email, final String pass) {
        Log.d(TAG, "signUp: ");
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            mProgressDialog.dismiss();
                            Toast.makeText(SignUpPage.this, "SignUp successful", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onComplete: SignUp successful");
                            String uid = mAuth.getUid();
                            Intent intent = new Intent(SignUpPage.this, CreateProfile.class);
                            intent.putExtra("uid", uid);
                            intent.putExtra("user name", user);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();


                        } else {
                            mProgressDialog.dismiss();

                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException ei) {

                                passwordLayout.setError(getString(R.string.error_weak_password));
                                passwordLayout.requestFocus();
                            } catch (FirebaseAuthInvalidCredentialsException ei) {
                                emailLayout.setError(getString(R.string.error_invalid_email));
                                emailTxt.requestFocus();
                            } catch (FirebaseAuthUserCollisionException ei) {
                                userNameLayout.setError(getString(R.string.error_user_exists));
                                userNameTxt.requestFocus();
                            } catch (Exception ei) {
                                Log.e(TAG, ei.getMessage());
                            }
                        }


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