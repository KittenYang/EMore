package com.caij.emore.ui.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.caij.emore.ui.fragment.BaseFragment;

import java.util.List;

/**
 * Created by Caij on 2016/6/14.
 */
public class WeiboFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<BaseFragment> mFragments;
    private List<String> mTitles;

    public WeiboFragmentPagerAdapter(FragmentManager fm, List<BaseFragment> fragments,
                                     List<String> titles) {
        super(fm);
        this.mFragments = fragments;
        this.mTitles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments == null ? 0 : mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (mTitles != null) {
            return mTitles.get(position);
        }
        return null;
    }
}
