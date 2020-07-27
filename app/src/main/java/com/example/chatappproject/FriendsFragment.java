package com.example.chatappproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class FriendsFragment extends Fragment implements FriendsRecyclerAdapter.FriendsListener {
    private static final String TAG = "FriendsFragment";

    public RecyclerView mRecyclerView;
    public View mMainView;
    public FirebaseRecyclerAdapter adapter;

    public String mCurrentUserId;
    public FirebaseAuth mAuth;

    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_friends, container, false);

        mRecyclerView = mMainView.findViewById(R.id.friends_recycler_view);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId =mAuth.getCurrentUser().getUid();
        Log.d(TAG, "onCreateView: mcurrentuid is "+mCurrentUserId);

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Friend")
                .child(mCurrentUserId)
                .limitToLast(50);

        FirebaseRecyclerOptions<FriendsModel> options =
                new FirebaseRecyclerOptions.Builder<FriendsModel>()
                        .setQuery(query, FriendsModel.class)
                        .build();

        adapter = new FriendsRecyclerAdapter(options,this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.hasFixedSize();

        return mMainView;

    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onItemClicked(int position) {
        Log.d(TAG, "onItemClicked: item"+position +"clicked");

    }
}