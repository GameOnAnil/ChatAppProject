package com.example.chatappproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.network.ListNetworkRequest;
import com.squareup.picasso.Picasso;

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

    DatabaseReference mUserDatabase;
    FirebaseAuth mAuth;
    FirebaseUser mCurrentUser;
    FirebaseDatabase mRootRef;

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

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        if(mCurrentUser !=null)
        {
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
                if(!snapshot.hasChild(mClickedUid)){
//Actual value to be updated
                    Map chatAppMap = new HashMap();
                    chatAppMap.put("seen",false);
                    chatAppMap.put("timestamp",ServerValue.TIMESTAMP);
//To make location easy: similar to .getReference().child("Chat").child(outUid).child(clickedUid)
// .updateChildren(map);
                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chat/"+mCurrentUser.getUid()+"/"+mClickedUid,chatAppMap);
                    chatUserMap.put("Chat/"+mClickedUid+"/"+mCurrentUser.getUid(),chatAppMap);

                    mRootRef.getReference().updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            if(error!=null){
                                Log.d(TAG, "onComplete: error: "+error);

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



        mRecyclerView = findViewById(R.id.recycler_view_chattingPage);
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Messages")
                .child(mCurrentUser.getUid()).child(mClickedUid)
                .limitToLast(50);

        FirebaseRecyclerOptions<MessagesModel> options =
                new FirebaseRecyclerOptions.Builder<MessagesModel>()
                        .setQuery(query, MessagesModel.class)
                        .build();

        messageAdapter = new MessageAdapter(options);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(messageAdapter);




    }

    private void sendMessage() {
        String enteredMessage = mEditText.getText().toString();

        if(!enteredMessage.isEmpty()){
            String current_user_ref = "Messages/"+mCurrentUser.getUid()+"/"+mClickedUid;
            String clicked_user_ref = "Messages/"+mClickedUid+"/"+mCurrentUser.getUid();

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("Messages").push();
            String pushId = databaseReference.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message",enteredMessage);
            messageMap.put("seen",false);
            messageMap.put("type","text");
            messageMap.put("time",ServerValue.TIMESTAMP);
            messageMap.put("from",mCurrentUser.getUid());

            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref+"/"+pushId,messageMap);
            messageUserMap.put(clicked_user_ref+"/"+pushId,messageMap);

            mRootRef.getReference().updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    if(error !=null){
                        Log.d(TAG, "onComplete: error: "+error);

                    }
                }
            });


        }else{
            Toast.makeText(this, "Enter message first", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: chattingpage onstart called");
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