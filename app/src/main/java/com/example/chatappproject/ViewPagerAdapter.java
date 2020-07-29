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
               ChatFragment chatFragment = new ChatFragment();
               return  chatFragment;
            case 1:
                FriendsFragment friendsFragment = new FriendsFragment();
                return  friendsFragment;
            case 2:
                AllUserFragment allUserFragment = new AllUserFragment();
                return  allUserFragment;
            case 3:
                RequestFragment requestFragment = new RequestFragment();
                return  requestFragment;
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
                return "Chat";
            case 1:
                return "Friends";
            case 2:
                return "Users";
            case 3:
                return "Requests";
            default:
                return null;
        }
    }


}
