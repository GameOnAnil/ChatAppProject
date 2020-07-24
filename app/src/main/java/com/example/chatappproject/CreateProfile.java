package com.example.chatappproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;


import java.util.HashMap;
import java.util.Map;

public class CreateProfile extends AppCompatActivity {
    private static final String TAG = "CreateProfile";
    ImageView mImageView;
    TextView mUserName;
    TextView mUserStatus;
    Button changeImage;
    Button confirmChanges;
    Button changeStatus;

    FirebaseDatabase database ;
    DatabaseReference myRef;
    FirebaseAuth mAuth;
    FirebaseUser mCurrentUser;

    String imageUrl;

    private static final int GALLERY_PICK = 1;
    private StorageReference mStorageRef;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        Toolbar toolbar = findViewById(R.id.toolbar_create_profile);
        toolbar.setTitle("Add profile detail");
        setSupportActionBar(toolbar);

        mImageView = findViewById(R.id.profile_image);
        mUserName = findViewById(R.id.userNameText);
        mUserStatus = findViewById(R.id.userStatus);
        changeImage = findViewById(R.id.changeImageBtn);
        changeStatus = findViewById(R.id.changeStatus);
        confirmChanges = findViewById(R.id.confirmChanges);

        Intent intent1 = getIntent();
        final String uid = intent1.getStringExtra("uid");
        String username = intent1.getStringExtra("user name");

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        mUserName.setText(username);
        Toast.makeText(CreateProfile.this, "uid is : "+uid, Toast.LENGTH_SHORT).show();

        changeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText editText = new EditText(CreateProfile.this);
                float dpi = editText.getResources().getDisplayMetrics().density;
                editText.setHint("Enter list name");
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);


                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(CreateProfile.this);
                alertDialog.setTitle("Add status");
                alertDialog.setView(editText);
                alertDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mUserStatus.setText(editText.getText().toString());

                    }
                });
                alertDialog.show();
            }
        });

        changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "Select image"), GALLERY_PICK);

            }
        });

        confirmChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String,Object> user = new  HashMap<>();
                user.put("user name",mUserName.getText().toString());
                user.put("status",mUserStatus.getText().toString());
                user.put("image",imageUrl);

                myRef = database.getInstance().getReference().child("User").child(mCurrentUser.getUid());
                myRef.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(CreateProfile.this, "data added", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CreateProfile.this,MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                String uid = mCurrentUser.getUid();

                final Uri resultUri = result.getUri();
                final StorageReference filePath = mStorageRef.child("Profile picture").child(uid + ".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(CreateProfile.this, "data added", Toast.LENGTH_SHORT).show();

                            Log.d(TAG, "onComplete: resultUri"+resultUri);
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.d(TAG, "onSuccess: uri: "+uri);
                                    imageUrl = uri.toString();

                                    Picasso.get().load(uri).into(mImageView);
                                }
                            });

                        } else {
                            task.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(CreateProfile.this, "failed" + e, Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "onFailure: failed due to : " + e);
                                }
                            });

                        }
                    }
                });
//setting up imageUrl



            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d(TAG, "onActivityResult: error" + error);
            }
        }

    }

}