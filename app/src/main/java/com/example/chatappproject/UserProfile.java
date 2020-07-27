package com.example.chatappproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity {
    private static final String TAG = "UserProfile";
    CircleImageView mImageView;
    TextView mUsername;
    TextView mStatus;
    Button mFriendRequest;
    Button mDeclineFriendRequest;
    String clickedUserId;

    FirebaseDatabase db;

    DatabaseReference mPressedUserRef;
    DatabaseReference mDatabaseReference;
    DatabaseReference mFriendsDatabase;
    FirebaseUser mCurrentUser;

    String mUser_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Toolbar toolbar = findViewById(R.id.toolbar_user_profile);
        toolbar.setTitle("User Profile");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mImageView = findViewById(R.id.image_user_profile);
        mUsername = findViewById(R.id.username_userprofile);
        mStatus = findViewById(R.id.status_userprofile);
        mFriendRequest = findViewById(R.id.friendRequest_btn);
        mDeclineFriendRequest = findViewById(R.id.decline_friend_request);

        Intent intent = getIntent();
        clickedUserId = intent.getStringExtra("userId");

        mUser_state = "not_friends";
        db = FirebaseDatabase.getInstance();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friend");

        String currentUid = mCurrentUser.getUid();
        Log.d(TAG, "onCreate: clickedUserId" + clickedUserId);
        Log.d(TAG, "onCreate: currentUid" + currentUid);

        initUserData();

        mFriendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFriendRequest.setEnabled(false);
                Log.d(TAG, "onClick: mUserState is : " + mUser_state);

                //-------WHEN THEY  ARE NOT FRIENDS-------
                if (mUser_state == "not_friends") {
                    mDatabaseReference = db.getReference().child("Friend Request").child(mCurrentUser.getUid()).child(clickedUserId).child("request_type");
                    mDatabaseReference.setValue("sent").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mDatabaseReference = db.getReference().child("Friend Request").child(clickedUserId).child(mCurrentUser.getUid()).child("request_type");
                            mDatabaseReference.setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mFriendRequest.setEnabled(true);
                                    mUser_state = "req_sent";
                                    mFriendRequest.setText("Cancel Friend Request");
                                    Log.d(TAG, "onSuccess: Request sent successfully");
                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mFriendRequest.setEnabled(true);
                            Toast.makeText(UserProfile.this, "failure due to " + e, Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onFailure: " + e);
                        }
                    });
                }
//----------AFTER REQUEST IS SENT ;FOR THE SENDER USER--------
                if (mUser_state == "req_sent") {
                    mDatabaseReference = db.getReference().child("Friend Request").child(mCurrentUser.getUid()).child(clickedUserId);
                    mDatabaseReference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mDatabaseReference = db.getReference().child("Friend Request").child(clickedUserId).child(mCurrentUser.getUid());
                            mDatabaseReference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mFriendRequest.setEnabled(true);
                                    mFriendRequest.setText("Send Friend Request");
                                    mUser_state = "not_friends";
                                    Log.d(TAG, "onSuccess: Request deleted");


                                }
                            });

                        }
                    });

                }

                //----------FOR REQ_RECEIVED;-----------
                if (mUser_state == "req_received") {
                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                    final Map<String,Object> map = new HashMap<>();
                    map.put("date",currentDate);

                    mFriendsDatabase.child(mCurrentUser.getUid()).child(clickedUserId)
                            .setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendsDatabase.child(clickedUserId).child(mCurrentUser.getUid()).setValue(map)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            mDatabaseReference = db.getReference().child("Friend Request").child(mCurrentUser.getUid()).child(clickedUserId);
                                            mDatabaseReference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    mDatabaseReference = db.getReference().child("Friend Request").child(clickedUserId).child(mCurrentUser.getUid());
                                                    mDatabaseReference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                            mDeclineFriendRequest.setVisibility(View.GONE);

                                                            mFriendRequest.setEnabled(true);
                                                            mFriendRequest.setText("UnFriend This User");
                                                            mUser_state = "friends";

                                                        }
                                                    });
                                                }
                                            });


                                        }
                                    });

                        }
                    });


                }

                //-----------FOR ALREADY A FRIEND----------
                if(mUser_state.equals("friends")){
                    mFriendsDatabase.child(mCurrentUser.getUid()).child(clickedUserId).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendsDatabase.child(clickedUserId).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mFriendRequest.setEnabled(true);
                                    mFriendRequest.setText("Send Friend Request");
                                    mUser_state = "not_friends";
                                }
                            });
                        }
                    });

                }


            }
        });

        mDeclineFriendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabaseReference = db.getReference().child("Friend Request").child(mCurrentUser.getUid()).child(clickedUserId);
                mDatabaseReference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mDatabaseReference = db.getReference().child("Friend Request").child(clickedUserId).child(mCurrentUser.getUid());
                        mDatabaseReference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                mDeclineFriendRequest.setVisibility(View.GONE);

                                mFriendRequest.setEnabled(true);
                                mFriendRequest.setText("Send Friend Request");
                                mUser_state = "not_friends";

                            }
                        });
                    }
                });
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

    private void initUserData() {
        mDeclineFriendRequest.setVisibility(View.GONE);


        mPressedUserRef = FirebaseDatabase.getInstance().getReference().child("User").child(clickedUserId);

        mPressedUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String username = snapshot.child("username").getValue().toString();
                    String status = snapshot.child("status").getValue().toString();
                    String imageUri = snapshot.child("image").getValue().toString();

                    mUsername.setText(username);
                    mStatus.setText(status);

                    if (!imageUri.isEmpty()) {
                        Picasso.get().load(imageUri).into(mImageView);
                    }

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Friend Request").child(mCurrentUser.getUid());
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild(clickedUserId)) {
                                Log.d(TAG, "onDataChange: has child");
                                String req_type = snapshot.child(clickedUserId).child("request_type").getValue().toString();
                                Log.d(TAG, "onDataChange: req_type is " + req_type);

                                if (req_type.equals("received")) {
                                    mDeclineFriendRequest.setVisibility(View.VISIBLE);
                                    mUser_state = "req_received";
                                    mFriendRequest.setText("Accept Friend Request");

                                } else if (req_type.equals("sent")) {
                                    mFriendRequest.setEnabled(true);
                                    mUser_state = "req_sent";
                                    mFriendRequest.setText("Cancel Friend Request");

                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("Friend").child(mCurrentUser.getUid());
                    databaseReference2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild(clickedUserId)) {
                                Log.d(TAG, "onDataChange: Friends node exists");
                                mFriendRequest.setEnabled(true);
                                mUser_state = "friends";
                                mFriendRequest.setText("UnFriend This User");


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}