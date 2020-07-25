package com.example.chatappproject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class EditProfile extends AppCompatActivity {
    private static final String TAG = "EditProfile";
    private ImageView mImageView;
    private TextView mUserName;
    private TextView mUserStatus;
    private Button changeImage;
    private Button changeStatus;
    private Button confirmChanges;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    private String imageUrl;


    private static final int GALLERY_PICK = 1;
    private StorageReference mStorageRef;
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Toolbar toolbar = findViewById(R.id.toolbar_edit_profile);
        toolbar.setTitle("Edit profile");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mImageView = findViewById(R.id.profile_image_edit);
        mUserName = findViewById(R.id.userNameTextEdit);
        mUserStatus = findViewById(R.id.userStatusEdit);
        changeImage = findViewById(R.id.changeImageBtnEdit);
        changeStatus = findViewById(R.id.changeStatusEdit);
        confirmChanges = findViewById(R.id.confirmChangesEdit);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        Intent intent1 = getIntent();
        final String uid = intent1.getStringExtra("uid");
        String username = intent1.getStringExtra("user name");

        mUserName.setText(username);

        changeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText editText = new EditText(EditProfile.this);
                float dpi = editText.getResources().getDisplayMetrics().density;
                editText.setHint("Enter list name");
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);


                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditProfile.this);
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
                Map<String, Object> user = new HashMap<>();
                user.put("user name", mUserName.getText().toString());
                user.put("status", mUserStatus.getText().toString());
                user.put("image", imageUrl);

                myRef = database.getInstance().getReference().child("User").child(mCurrentUser.getUid());
                myRef.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(EditProfile.this, "data added", Toast.LENGTH_SHORT).show();

                        finish();
                    }
                });
            }
        });

        initDetail();


    }

    public void initDetail() {
        Log.d(TAG, "initDetail: ");
        myRef = database.getInstance().getReference().child("User").child(mCurrentUser.getUid());

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("user name").getValue().toString();
                String status = snapshot.child("status").getValue().toString();
                if(snapshot.child("image").getValue()!=null){
                    String image = snapshot.child("image").getValue().toString();
                    Log.d(TAG, "onDataChange: image from db : " + image);
                    Picasso.get().load(image).into(mImageView);

                }

                mUserName.setText(name);
                mUserStatus.setText(status);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        mStorageRef.child("Profile picture").child(mCurrentUser.getUid()+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//                Log.d(TAG, "onSuccess: uri:"+uri);
//                // Got the download URL for 'users/me/profile.png'
//                Picasso.get().load(uri).into(mImageView);
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle any errors
//            }
//        });

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
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setTitle("Uploading image");
                mProgressDialog.setMessage("Please wait while the image is being uploaded.");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                String uid = mCurrentUser.getUid();


                final Uri resultUri = result.getUri();
                final StorageReference filePath = mStorageRef.child("Profile picture").child(uid + ".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(EditProfile.this, "data to storage added", Toast.LENGTH_SHORT).show();

                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.d(TAG, "onSuccess: uri: " + uri);
                                    imageUrl = uri.toString();

                                    Picasso.get().load(uri).into(mImageView);
                                    mProgressDialog.dismiss();
                                }
                            });
                        } else {
                            task.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(EditProfile.this, "failed" + e, Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "onFailure: failed due to : " + e);
                                    mProgressDialog.dismiss();

                                }
                            });

                        }
                    }
                });


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d(TAG, "onActivityResult: error" + error);
            }
        }

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