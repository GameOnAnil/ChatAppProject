package com.example.chatappproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.accessibilityservice.GestureDescription;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.paging.DatabasePagingOptions;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.storage.network.ListNetworkRequest;
import com.iceteck.silicompressorr.SiliCompressor;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChattingPage extends AppCompatActivity {
    private static final String TAG = "ChattingPage";

    private ImageButton mBtn_add;
    private ImageButton mBtn_send;
    private EditText mEditText;
    private TextView mActionBarUserName;
    private TextView mLastSeen;
    private CircleImageView mActionBarProfileImage;

    private String mClickedUid;
    private RecyclerView mRecyclerView;
    private MessageAdapter messageAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private static int TOTAL_ITEM_LOAD;
    private int page_number = 0;

    DatabaseReference mUserDatabase;
    FirebaseAuth mAuth;
    FirebaseUser mCurrentUser;
    FirebaseDatabase mRootRef;
    private StorageReference mStorageRef;
    LinearLayoutManager mLinearLayoutManager;
    private static final int GALLERY_PICK = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting_page);

        Toolbar toolbar = findViewById(R.id.toolbar_chatting_page);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = layoutInflater.inflate(R.layout.custom_action_bar, null);

        actionBar.setCustomView(action_bar_view);

        mClickedUid = getIntent().getStringExtra("clickedUid");

        mActionBarUserName = findViewById(R.id.custombar_username);
        mLastSeen = findViewById(R.id.last_seen_txt);
        mActionBarProfileImage = findViewById(R.id.profile_image_inappbar);
        mEditText = findViewById(R.id.type_message);
        mBtn_send = findViewById(R.id.button_send);
        mBtn_add = findViewById(R.id.imageButton_add);


        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        if (mCurrentUser != null) {
            Log.d(TAG, "onCreate: ");
            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("User").child(mCurrentUser.getUid());
            mUserDatabase.child("online").setValue(true);

        }

        mRootRef = FirebaseDatabase.getInstance();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("User");

        //To add username to actionbar from receiver user
        mUserDatabase.child(mClickedUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String Username = snapshot.child("username").getValue().toString();
                    String profileImage = snapshot.child("image").getValue().toString();
                    Boolean online = Boolean.parseBoolean(snapshot.child("online").getValue().toString());
                    String lastSeen = snapshot.child("last seen").getValue().toString();
                    mActionBarUserName.setText(Username);

                    if (online == true) {
                        mLastSeen.setText("Online");
                    } else {

                        TimeAgo timeAgo = new TimeAgo();
                        long lastSeenTime = Long.parseLong(lastSeen);
                        String lastSeenTimeText = timeAgo.getTimeAgo(lastSeenTime, getApplicationContext());
                        mLastSeen.setText(lastSeenTimeText);
                    }
                    if (!profileImage.isEmpty()) {
                        Picasso.get().load(profileImage).into(mActionBarProfileImage);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //adding chat root
        mRootRef.getReference().child("Chat").child(mCurrentUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.hasChild(mClickedUid)) {
//Actual value to be updated
                            Map chatAppMap = new HashMap();
                            chatAppMap.put("seen", false);
                            chatAppMap.put("timestamp", ServerValue.TIMESTAMP);
//To make location easy: similar to .getReference().child("Chat").child(outUid).child(clickedUid)
// .updateChildren(map);
                            Map chatUserMap = new HashMap();
                            chatUserMap.put("Chat/" + mCurrentUser.getUid() + "/" + mClickedUid, chatAppMap);
                            chatUserMap.put("Chat/" + mClickedUid + "/" + mCurrentUser.getUid(), chatAppMap);

                            mRootRef.getReference().updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                    if (error != null) {
                                        Log.d(TAG, "onComplete: error: " + error);

                                    }
                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        mBtn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
                mEditText.setText("");
            }
        });

//-------------SETUP RECYCLER VIEW----------
        TOTAL_ITEM_LOAD = 10;
        LoadMessageToRecycler();


        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                messageAdapter.stopListening();
                mRecyclerView.clearOnScrollListeners();

                TOTAL_ITEM_LOAD += 5;
                LoadMessageToRecycler();
                messageAdapter.startListening();

                mSwipeRefreshLayout.setRefreshing(false);

            }
        });

        mBtn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "Select image"), GALLERY_PICK);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();

            final String current_user_ref = "Messages/" + mCurrentUser.getUid() + "/" + mClickedUid;
            final String clicked_user_ref = "Messages/" + mClickedUid + "/" + mCurrentUser.getUid();

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("Messages").push();
            final String pushId = databaseReference.getKey();

            final StorageReference filepath = mStorageRef.child("message_images").child(pushId+".jpg");
            filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String download_url = uri.toString();
                                Log.d(TAG, "onSuccess: download_url "+download_url);



                                Map messageMap = new HashMap();
                                messageMap.put("message", download_url);
                                messageMap.put("seen", false);
                                messageMap.put("type", "image");
                                messageMap.put("time", ServerValue.TIMESTAMP);
                                messageMap.put("from", mCurrentUser.getUid());

                                Map messageUserMap = new HashMap();
                                messageUserMap.put(current_user_ref + "/" + pushId, messageMap);
                                messageUserMap.put(clicked_user_ref + "/" + pushId, messageMap);

                                mRootRef.getReference().updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                        if (error != null) {
                                            Log.d(TAG, "onComplete: error: " + error);

                                        }
                                    }
                                });

                            }
                        });
                    }
                }
            });







        }


    }

    public void LoadMessageToRecycler() {
        mRecyclerView = findViewById(R.id.recycler_view_chattingPage);
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);


        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Messages")
                .child(mCurrentUser.getUid()).child(mClickedUid)
                .limitToLast(TOTAL_ITEM_LOAD);

        FirebaseRecyclerOptions<MessagesModel> options =
                new FirebaseRecyclerOptions.Builder<MessagesModel>()
                        .setQuery(query, MessagesModel.class)
                        .build();

        messageAdapter = new MessageAdapter(options);
        mLinearLayoutManager = new LinearLayoutManager(this);

        messageAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = messageAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
//                if (lastVisiblePosition == -1 ||(positionStart >= (friendlyMessageCount - 1) &&
//                                lastVisiblePosition == (positionStart - 1))) {
//                    mRecyclerView.scrollToPosition(positionStart);
//                }

                if (lastVisiblePosition == -1 || positionStart >= (friendlyMessageCount - 1)) {
                    mRecyclerView.scrollToPosition(positionStart);
                }
            }
        });
        mRecyclerView.hasFixedSize();
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(messageAdapter);
    }


    private void sendMessage() {
        String enteredMessage = mEditText.getText().toString();

        if (!enteredMessage.isEmpty()) {
            String current_user_ref = "Messages/" + mCurrentUser.getUid() + "/" + mClickedUid;
            String clicked_user_ref = "Messages/" + mClickedUid + "/" + mCurrentUser.getUid();

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("Messages").push();
            String pushId = databaseReference.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message", enteredMessage);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", mCurrentUser.getUid());

            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref + "/" + pushId, messageMap);
            messageUserMap.put(clicked_user_ref + "/" + pushId, messageMap);

            mRootRef.getReference().updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    if (error != null) {
                        Log.d(TAG, "onComplete: error: " + error);

                    }
                }
            });


        } else {
            Toast.makeText(this, "Enter message first", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        messageAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        messageAdapter.stopListening();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();

        }
        return super.onOptionsItemSelected(item);
    }
}