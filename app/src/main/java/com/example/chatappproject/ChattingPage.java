package com.example.chatappproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

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

    DatabaseReference mUserDatabase;

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
        View action_bar_view = layoutInflater.inflate(R.layout.custom_action_bar,null);

        actionBar.setCustomView(action_bar_view);

        mClickedUid = getIntent().getStringExtra("clickedUid");

        mActionBarUserName = findViewById(R.id.custombar_username);
        mLastSeen = findViewById(R.id.last_seen_txt);
        mActionBarProfileImage = findViewById(R.id.profile_image_inappbar);
        mEditText = findViewById(R.id.type_message);
        mBtn_send = findViewById(R.id.button_send);
        mBtn_add = findViewById(R.id.imageButton_add);

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("User");

        //To add username to actionbar from receiver user
        mUserDatabase.child(mClickedUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String Username = snapshot.child("username").getValue().toString();
                    String profileImage = snapshot.child("image").getValue().toString();
                    Boolean online = Boolean.parseBoolean(snapshot.child("online").getValue().toString()) ;
                    String lastSeen = snapshot.child("last seen").getValue().toString();
                    mActionBarUserName.setText(Username);

                    if(online==true){
                        mLastSeen.setText("Online");
                    }else{

                        TimeAgo timeAgo = new TimeAgo();
                        long lastSeenTime = Long.parseLong(lastSeen);
                        String lastSeenTimeText = timeAgo.getTimeAgo(lastSeenTime,getApplicationContext());

                        mLastSeen.setText(lastSeenTimeText);


                    }

                    if(!profileImage.isEmpty()){
                        Picasso.get().load(profileImage).into(mActionBarProfileImage);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //To add our profile image to action bar profile image



    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();

        }
        return super.onOptionsItemSelected(item);
    }
}