package com.qcp.facebookapp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.qcp.facebookapp.fragment.FriendsFragment;
import com.qcp.facebookapp.fragment.HomeFragment;
import com.qcp.facebookapp.fragment.ProfileFragment;
import com.qcp.facebookapp.fragment.ShowSearchResultFragment;

public class PagerAdapter extends FragmentPagerAdapter {
    public PagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: {
                return HomeFragment.getINSTANCE();
            }
            case 1: {
                return FriendsFragment.getINSTANCE();
            }
            case 2: {
                return ProfileFragment.getINSTANCE();
            }
            case 3: {
                return ShowSearchResultFragment.getINSTANCE();
            }
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
