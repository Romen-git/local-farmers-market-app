package com.lfm.app.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.lfm.app.Fragments.HomeFragment;
import com.lfm.app.Fragments.ProfileSettingsFragment;
import com.lfm.app.Fragments.SearchFragment;

public class HomePageFragmentAdapter extends FragmentPagerAdapter {

    public HomePageFragmentAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {

            case 0:
                return new HomeFragment();
            case 1:
                return new SearchFragment();
            default:
                return new ProfileSettingsFragment();
        }

    }

    @Override
    public int getCount() {
        return 3;
    }
}
