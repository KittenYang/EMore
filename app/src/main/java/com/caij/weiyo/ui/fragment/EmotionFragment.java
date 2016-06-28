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
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.caij.weiyo.R;
import com.caij.weiyo.utils.EmotionsUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Caij on 2016/6/18.
 */
public class EmotionFragment extends BaseFragment implements RadioGroup.OnCheckedChangeListener, ViewPager.OnPageChangeListener {

    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.rg_emotion)
    RadioGroup rgEmotion;
    @BindView(R.id.rb_default)
    RadioButton rbDefault;
    @BindView(R.id.rb_emoji)
    RadioButton rbEmoji;
    @BindView(R.id.rb_langxiaohua)
    RadioButton rbLangxiaohua;

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
        rgEmotion.setOnCheckedChangeListener(this);
        viewPager.addOnPageChangeListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.rb_default) {
            viewPager.setCurrentItem(0);
        } else if (checkedId == R.id.rb_emoji) {
            viewPager.setCurrentItem(1);
        } else if (checkedId == R.id.rb_langxiaohua) {
            viewPager.setCurrentItem(2);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
            rbDefault.setChecked(true);
        } else if (position == 1) {
            rbEmoji.setChecked(true);
        } else if (position == 2) {
            rbLangxiaohua.setChecked(true);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

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
