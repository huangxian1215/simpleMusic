package com.example.simplemusic.adapter;


import java.util.ArrayList;

import com.example.simplemusic.fragment.LocalMusicFragment;
import com.example.simplemusic.fragment.NetMusicFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PageAdapter extends FragmentPagerAdapter {
    private ArrayList<String> mTitleArray;

    public PageAdapter(FragmentManager fm, ArrayList<String> titleArray) {
        super(fm);
        mTitleArray = titleArray;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new LocalMusicFragment();
        } else if (position == 1) {
            return new NetMusicFragment();
        }
        return new LocalMusicFragment();
    }

    @Override
    public int getCount() {
        return mTitleArray.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitleArray.get(position);
    }
}
