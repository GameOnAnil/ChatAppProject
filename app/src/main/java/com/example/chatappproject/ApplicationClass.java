package com.example.chatappproject;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ApplicationClass extends Application {

    private static final String TAG = "ApplicationClass";
    DatabaseReference mUserDatabase;
    FirebaseAuth mAuth;
    FirebaseUser mCurrentUser;

    @Override
    public void onCreate() {
        super.onCreate();

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        if(mCurrentUser!=null){
            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("User").child(mCurrentUser.getUid());

            setOnline();
        }

        Log.d(TAG, "onCreate:  application created");
    }



    private void setOnline() {
        Map<String , Object> map = new HashMap<>();
        map.put("online",true);

     mUserDatabase.addValueEventListener(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot snapshot) {
             if(snapshot.exists()){
                 mUserDatabase.child("online").onDisconnect().setValue(false);


             }
         }

         @Override
         public void onCancelled(@NonNull DatabaseError error) {

         }
     });
    }


}
