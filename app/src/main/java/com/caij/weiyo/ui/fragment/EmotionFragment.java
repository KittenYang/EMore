package com.caij.weiyo.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.caij.weiyo.R;
import com.caij.weiyo.utils.EmotionsUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Caij on 2016/6/18.
 */
public class EmotionFragment extends BaseFragment {

    @BindView(R.id.view_pager)
    ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_emotion, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        List<BaseFragment> fragments = new ArrayList<>(3);
        fragments.add(EmotionItemFragment.newInstance(EmotionsUtil.getDefaultEmotions()));
        fragments.add(EmotionItemFragment.newInstance(EmotionsUtil.getSoftEmotions()));
        fragments.add(EmotionItemFragment.newInstance(EmotionsUtil.getLangEmotions()));
        viewPager.setAdapter(new EmotionPageAdapter(getChildFragmentManager(), fragments));
    }

    class EmotionPageAdapter extends FragmentPagerAdapter {

        List<BaseFragment> fragments;
        public EmotionPageAdapter(FragmentManager fm, List<BaseFragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}
