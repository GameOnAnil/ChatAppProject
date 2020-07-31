package com.example.chatappproject;

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

import java.sql.Timestamp;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestRecyclerAdapter extends FirebaseRecyclerAdapter<RequestModel,RequestRecyclerAdapter.RequestViewHolder> {
    private static final String TAG = "RequestRecyclerAdapter";
    DatabaseReference mUserDatabase;
    FirebaseUser mCurrentUser;
    public RequestListener requestListener;




    public RequestRecyclerAdapter(@NonNull FirebaseRecyclerOptions<RequestModel> options,RequestListener requestListener) {
        super(options);
        this.requestListener = requestListener;
    }

    @Override
    protected void onBindViewHolder(@NonNull final RequestViewHolder holder, int position, @NonNull RequestModel model) {
        String request_type = model.getRequest_type();

        Log.d(TAG, "onBindViewHolder: request_type is "+request_type);

        if(request_type.equals("received")){
            String listUserId = getRef(position).getKey();

            mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
            String currentUid = mCurrentUser.getUid();
            Log.d(TAG, "onBindViewHolder: listUserId is "+listUserId);
            Log.d(TAG, "onBindViewHolder: currentUserId is "+currentUid);

            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("User");
            mUserDatabase.child(listUserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        String username = snapshot.child("username").getValue().toString();
                        String profileUrl = snapshot.child("image").getValue().toString();
                        String status = snapshot.child("status").getValue().toString();
                        holder.mUsername.setText(username);
                        holder.mStatus.setText(status);

                        if(!profileUrl.isEmpty()){
                            Picasso.get().load(profileUrl).into(holder.mUserProfile);
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
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.friend_request_list,parent,false);

        return new RequestViewHolder(view);
    }


    public class  RequestViewHolder extends RecyclerView.ViewHolder{
        CircleImageView mUserProfile;
        TextView mUsername;
        TextView mStatus;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);

            mUserProfile = itemView.findViewById(R.id.image_in_friend_request);
            mUsername = itemView.findViewById(R.id.userName_in_friend_request);
            mStatus = itemView.findViewById(R.id.status_in_friends_request);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    requestListener.onItemClicked(getAdapterPosition());
                }
            });
        }
    }


    interface RequestListener {
        public void onItemClicked(int position);
    }
}
