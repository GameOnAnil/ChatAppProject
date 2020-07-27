package com.example.chatappproject;

import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentViewHolder;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsRecyclerAdapter extends FirebaseRecyclerAdapter<FriendsModel,FriendsRecyclerAdapter.FriendViewHolder> {
    private static final String TAG = "FriendsRecyclerAdapter";
    DatabaseReference mUserDatabase;
    public FriendsListener friendsListener;


    public FriendsRecyclerAdapter(@NonNull FirebaseRecyclerOptions<FriendsModel> options,FriendsListener friendsListener) {
        super(options);
        this.friendsListener = friendsListener;
    }

    @Override
    protected void onBindViewHolder(@NonNull final FriendViewHolder holder, int position, @NonNull FriendsModel model) {

        holder.dateTxt.setText(model.getDate());

        String listUserId = getRef(position).getKey();
        Log.d(TAG, "onBindViewHolder: listUserId "+listUserId);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("User").child(listUserId);
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String name = snapshot.child("username").getValue().toString();
                    String imageUri = snapshot.child("image").getValue().toString();

                    holder.usernameTxt.setText(name);
                    if(!imageUri.isEmpty()){
                        Picasso.get().load(imageUri).into(holder.circleImageView);

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater= LayoutInflater.from(parent.getContext());
        View v =  layoutInflater.inflate(R.layout.all_friends_list,parent,false);

        return new FriendViewHolder(v);
    }

    public class FriendViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView usernameTxt;
        TextView statusTxt;
        TextView dateTxt;


        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.image_in_friendlist);
            usernameTxt = itemView.findViewById(R.id.userName_in_friendlist);

            dateTxt = itemView.findViewById(R.id.date_in_friendsList);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    friendsListener.onItemClicked(getAdapterPosition());
                }
            });

        }

    }

    interface FriendsListener{
        public void onItemClicked(int position);
    }
}
