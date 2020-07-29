package com.example.chatappproject;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends FirebaseRecyclerAdapter<MessagesModel, RecyclerView.ViewHolder> {
    private static final String TAG = "MessageAdapter";
    FirebaseUser mCurrentUser;

    String mFromText;

    private static final int TYPE_SENDER = 1;
    private static final int TYPE_RECEIVER = 2;
    RecyclerView.ViewHolder mHolder;


    public MessageAdapter(@NonNull FirebaseRecyclerOptions<MessagesModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position, @NonNull MessagesModel model) {
        if (holder.getItemViewType() == TYPE_SENDER) {
            final MessageViewHolder messageViewHolder = (MessageViewHolder) holder;
            messageViewHolder.messageTxt.setText(model.getMessage());

            mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
            String currentUserId = mCurrentUser.getUid();
            String from_user_id = model.getFrom();


            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("User").child(currentUserId);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String imageUri = snapshot.child("image").getValue().toString();
                        if (!imageUri.isEmpty()) {
                            Picasso.get().load(imageUri).into(messageViewHolder.profileImage);
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        ///------------------Second View Holder--------------
        else {
            final MessageViewHolderSecond messageViewHolderSecond = (MessageViewHolderSecond) holder;


            messageViewHolderSecond.messageTxtSecond.setText(model.getMessage());

            mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
            String currentUserId = mCurrentUser.getUid();
            String from_user_id = model.getFrom();


            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("User").child(from_user_id);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String imageUri = snapshot.child("image").getValue().toString();
                        if (!imageUri.isEmpty()) {
                            Picasso.get().load(imageUri).into(messageViewHolderSecond.profileImageSecond);
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


    }


    public class MessageViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profileImage;
        TextView messageTxt;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profile_ours);
            messageTxt = itemView.findViewById(R.id.message_textfield);
        }
    }

    public class MessageViewHolderSecond extends RecyclerView.ViewHolder {
        CircleImageView profileImageSecond;
        TextView messageTxtSecond;

        public MessageViewHolderSecond(@NonNull View itemView) {
            super(itemView);
            profileImageSecond = itemView.findViewById(R.id.profile_ours_second);
            messageTxtSecond = itemView.findViewById(R.id.message_textfield_second);
        }

    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == 1) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View v = layoutInflater.inflate(R.layout.chatting_page_list, parent, false);
            return new MessageViewHolder(v);
        } else {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View v = layoutInflater.inflate(R.layout.chatting_page_list_second, parent, false);
            return new MessageViewHolderSecond(v);
        }

    }

    @Override
    public int getItemViewType(final int position) {
        String fromUid = getSnapshots().getSnapshot(position).child("from").getValue().toString();

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (currentUserId.equals(fromUid)) {
            return TYPE_SENDER;
        } else {
            return TYPE_RECEIVER;
        }
    }
}

