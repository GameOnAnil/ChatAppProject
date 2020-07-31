package com.example.chatappproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class RequestFragment extends Fragment implements RequestRecyclerAdapter.RequestListener {

    View mView;
    RecyclerView mRecyclerView;
    RequestRecyclerAdapter mAdapter;
    DatabaseReference mFriendRequestRef;
    FirebaseUser mCurrentUser;

    public RequestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_request, container, false);


        mRecyclerView = mView.findViewById(R.id.recycler_view_request_fragment);
        mFriendRequestRef = FirebaseDatabase.getInstance().getReference().child("Friend Request");
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Friend Request")
                .child(mCurrentUser.getUid())
                .child("received_req")
                .limitToLast(50);

        FirebaseRecyclerOptions<RequestModel> options =
                new FirebaseRecyclerOptions.Builder<RequestModel>()
                        .setQuery(query, RequestModel.class)
                        .build();


        mAdapter = new RequestRecyclerAdapter(options,this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.hasFixedSize();


        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    @Override
    public void onItemClicked(int position) {
        String userId = mAdapter.getRef(position).getKey();
        Intent intent = new Intent(getContext(),UserProfile.class);
        intent.putExtra("userId",userId);
        startActivity(intent);


    }
}