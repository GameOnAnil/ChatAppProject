package com.example.chatappproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class AllUserFragment extends Fragment implements AllUserAdapter.AdapterListener {
    private static final String TAG = "AllUserFragment";
    public View mMainView;
    Toolbar mToolbar;
    RecyclerView recyclerView;
    AllUserAdapter allUserAdapter;
    DatabaseReference databaseReference;
    FirebaseDatabase mDb;
    Query query;
    FirebaseAuth mAuth;
    FirebaseUser mCurrentUser;
    DatabaseReference mUserDatabase;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        mMainView = inflater.inflate(R.layout.fragment_all_user, container, false);


        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        recyclerView = mMainView.findViewById(R.id.recycler_view_alluser_fragment);
        mDb = FirebaseDatabase.getInstance();

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("User")
                .limitToLast(50);

        FirebaseRecyclerOptions<UserModel> options =
                new FirebaseRecyclerOptions.Builder<UserModel>()
                        .setQuery(query, UserModel.class)
                        .build();

        allUserAdapter = new AllUserAdapter(options,this );
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(allUserAdapter);



        return  mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();
        allUserAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        allUserAdapter.stopListening();
    }

    @Override
    public void onItemClicked(int position) {

        String userId = allUserAdapter.getRef(position).getKey();

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Log.d(TAG, "onItemClicked: currentUserId"+currentUserId);
        Log.d(TAG, "ViewHolder: userId is: "+userId);
        if(!userId.equals(currentUserId)){
            Intent intent = new Intent(getContext(),UserProfile.class);
            intent.putExtra("userId",userId);
            startActivity(intent);
        }else{
            Toast.makeText(getContext(), "You are trying to send friend request to yourself idiot!!", Toast.LENGTH_SHORT).show();
        }

    }
}
