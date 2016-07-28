package com.caij.emore.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.transition.Transition;
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

import java.io.File;
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

    private static class ImageAdapter extends PagerAdapter {

        List<String> imags;
        int position;

        public ImageAdapter(List<String> imags, int position) {
            this.imags = imags;
            this.position = position;
        }

        @Override
        public int getCount() {
            return imags.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(container.getContext());
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            container.addView(imageView, layoutParams);
            ImageLoader.ImageConfig config = new ImageLoader.ImageConfigBuild()
                    .setScaleType(ImageLoader.ScaleType.FIT_CENTER)
                    .setSupportGif(true)
                    .setCacheMemory(false)
                    .setDiskCacheStrategy(ImageLoader.CacheConfig.SOURCE)
                    .build();
            ImageLoader.load(container.getContext(), imageView, imags.get(position), android.R.color.black, config);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }

    private static class ImageFragmentAdapter extends WeiboFragmentPagerAdapter {

        public ImageFragmentAdapter(FragmentManager fm, List<BaseFragment> fragments, List<String> titles) {
            super(fm, fragments, titles);
        }
    }
}
