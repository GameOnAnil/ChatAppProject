package com.example.chatappproject;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                FriendsFragment friendsFragment = new FriendsFragment();
                return  friendsFragment;
            case 1:
                RequestFragment requestFragment = new RequestFragment();
                return  requestFragment;
            case 2:
                AllUserFragment allUserFragment = new AllUserFragment();
                return  allUserFragment;
            case 3:
                ChatFragment chatFragment = new ChatFragment();
                return  chatFragment;

            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 3;
    }

    public CharSequence getPageTitle(int position){
        switch (position){
            case 0:
                return "Friends";
            case 1:
                return "Requests";
            case 2:
                return "Users";
            case 3:
                return "Chat";
            default:
                return null;
        }
    }


}
