package com.example.chatappproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


public class AllUserActivity extends AppCompatActivity {
    Toolbar mToolbar;
    RecyclerView recyclerView;
    AllUserAdapter allUserAdapter;
    DatabaseReference databaseReference;
    FirebaseDatabase mDb;
    Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);



        mToolbar = findViewById(R.id.toolbar_all_user);
        mToolbar.setTitle("All Users Available");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        recyclerView = findViewById(R.id.recycler_view);
        mDb = FirebaseDatabase.getInstance();

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("User")
                .limitToLast(50);

        FirebaseRecyclerOptions<UserModel> options =
                new FirebaseRecyclerOptions.Builder<UserModel>()
                        .setQuery(query, UserModel.class)
                        .build();

        allUserAdapter = new AllUserAdapter(options);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(allUserAdapter);


    }

    @Override
    protected void onStart() {
        super.onStart();
        allUserAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();

        allUserAdapter.stopListening();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }
}