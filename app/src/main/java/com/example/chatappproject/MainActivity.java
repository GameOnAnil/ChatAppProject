package com.example.chatappproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    ViewPager mViewPager;
    TabLayout mTabLayout;
    private ViewPagerAdapter viewPagerAdapter;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle("Main page");
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();

        mViewPager = findViewById(R.id.main_viewPager);
        mTabLayout =findViewById(R.id.tab_layout_main);


        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(viewPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser mCurrentUser = mAuth.getCurrentUser();

        if(mCurrentUser ==null){
            Log.d(TAG, "onStart:  user not logged in ");
            Intent intent = new  Intent(MainActivity.this,ChoiceActivity.class);
            startActivity(intent);
            finish();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this,ChoiceActivity.class);
                startActivity(intent);
                return true;
            case R.id.item_edit_profile:
                Intent intent1 = new Intent(MainActivity.this,EditProfile.class);
                startActivity(intent1);
                return true;
            case R.id.item_all_users:
                Intent intent2 = new Intent(MainActivity.this,AllUserActivity.class);
                startActivity(intent2);
                return  true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}