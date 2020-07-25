package com.example.chatappproject;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUserAdapter extends FirebaseRecyclerAdapter<UserModel,AllUserAdapter.ViewHolder> {

    public AllUserAdapter(@NonNull FirebaseRecyclerOptions options) {
        super(options);
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.all_users_list,parent,false);
        return new ViewHolder(v);
    }



    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull UserModel model) {
        holder.usernameTxt.setText(model.getUsername());
        holder.statusTxt.setText(model.getStatus());
        if(model.getImage()!=null){
            Picasso.get().load(model.getImage()).into(holder.circleImageView);
        }else{
            Picasso.get().load(R.drawable.profile).into(holder.circleImageView);
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView usernameTxt;
        public TextView statusTxt;
        public CircleImageView circleImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTxt = itemView.findViewById(R.id.userName_in_list);
            statusTxt = itemView.findViewById(R.id.status_in_list);
            circleImageView = itemView.findViewById(R.id.image_in_list);
        }
    }
}
