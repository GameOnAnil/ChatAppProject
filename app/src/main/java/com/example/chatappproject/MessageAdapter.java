package com.example.chatappproject;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends FirebaseRecyclerAdapter<MessagesModel, MessageAdapter.MessageViewHolder> {
    private static final String TAG = "MessageAdapter";
    FirebaseUser mCurrentUser;


    public MessageAdapter(@NonNull FirebaseRecyclerOptions<MessagesModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final MessageViewHolder holder, int position, @NonNull MessagesModel model) {
        holder.messageTxt.setText(model.getMessage());

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = mCurrentUser.getUid();
        String from_user_id = model.getFrom();
        Log.d(TAG, "onBindViewHolder: currentUid "+currentUserId);
        Log.d(TAG, "onBindViewHolder: from_user_id"+from_user_id);


        if(currentUserId.equals(from_user_id)){
            holder.messageTxt.setBackgroundResource(R.drawable.chatting_sent_textbox);
            holder.messageTxt.setTextColor(Color.WHITE);

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("User").child(currentUserId);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        String imageUri = snapshot.child("image").getValue().toString();
                        if(!imageUri.isEmpty()){
                            Picasso.get().load(imageUri).into(holder.profileImage);
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else{
            holder.messageTxt.setBackgroundResource(R.drawable.chatting_sent_textbox_white);
            holder.messageTxt.setTextColor(Color.BLACK);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("User").child(from_user_id);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        String imageUri = snapshot.child("image").getValue().toString();
                        if(!imageUri.isEmpty()){
                            Picasso.get().load(imageUri).into(holder.profileImage);
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }



    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.chatting_page_list,parent,false);

        return new MessageViewHolder(view);
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{
        CircleImageView profileImage;
        TextView messageTxt;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profile_ours);
            messageTxt = itemView.findViewById(R.id.message_textfield);
        }
    }
}
