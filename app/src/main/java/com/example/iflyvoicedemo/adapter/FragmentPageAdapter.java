package com.example.iflyvoicedemo.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import java.util.List;

/**
 * Created by Administrator on 2017/1/17.
 */

public class FragmentPageAdapter extends FragmentPagerAdapter {
    private List<Fragment> mPages;
    private List<String> mTitles;

    public FragmentPageAdapter(FragmentManager fm, List<Fragment> pages, List<String> titles) {
        super(fm);
        this.mPages = pages;
        this.mTitles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return mPages.get(position);
    }

    @Override
    public int getCount() {
        return mPages.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }
}
