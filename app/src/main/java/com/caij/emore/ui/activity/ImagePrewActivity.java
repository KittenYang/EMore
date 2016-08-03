package com.caij.emore.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.ui.adapter.WeiboFragmentPagerAdapter;
import com.caij.emore.ui.fragment.BaseFragment;
import com.caij.emore.ui.fragment.ImagePrewFragment;
import com.caij.emore.utils.ImageLoader;
import com.caij.emore.view.HackyViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Caij on 2016/6/24.
 */
public class ImagePrewActivity extends FragmentActivity {

    @BindView(R.id.vp_image)
    HackyViewPager mVpImage;
    @BindView(R.id.tv_image_count)
    TextView mTvImageCount;

    public static Intent newIntent(Context context, ArrayList<String> paths, int position) {
        Intent intent = new Intent(context, ImagePrewActivity.class);
        intent.putStringArrayListExtra(Key.IMAGE_PATHS, paths);
        intent.putExtra(Key.POSITION, position);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_prew);
        ButterKnife.bind(this);
//        ViewCompat.setTransitionName(mVpImage, Key.TRANSIT_PIC);
        final ArrayList<String> paths = getIntent().getStringArrayListExtra(Key.IMAGE_PATHS);
        final int position = getIntent().getIntExtra(Key.POSITION, 0);
        init(paths, position);

//        ImageAdapter adapter = new ImageAdapter(paths, position);
//        mVpImage.setAdapter(adapter);
//        mVpImage.setCurrentItem(position);
    }

    private void init( ArrayList<String> paths, int position) {
        final List<BaseFragment> fragments = new ArrayList<>();
        for (String path : paths) {
            ImagePrewFragment fragment = ImagePrewFragment.newInstance(path);
            fragments.add(fragment);
        }

        ImageFragmentAdapter adapter = new ImageFragmentAdapter(getSupportFragmentManager(), fragments, null);
        mVpImage.setAdapter(adapter);
        mVpImage.setCurrentItem(position);

        if (fragments.size() <= 1) {
            mTvImageCount.setVisibility(View.GONE);
        }else {
            mTvImageCount.setText((position + 1) + "/" + fragments.size());
        }

        mVpImage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mTvImageCount.setText((position + 1) + "/" + fragments.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private static class ImageFragmentAdapter extends WeiboFragmentPagerAdapter {

        public ImageFragmentAdapter(FragmentManager fm, List<BaseFragment> fragments, List<String> titles) {
            super(fm, fragments, titles);
        }
    }
}
