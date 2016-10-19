package com.caij.emore.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.present.imp.AbsBasePresent;
import com.caij.emore.ui.adapter.StatusFragmentPagerAdapter;
import com.caij.emore.ui.fragment.BaseFragment;
import com.caij.emore.ui.fragment.ImagePreviewFragment;
import com.caij.emore.utils.weibo.ThemeUtils;
import com.caij.emore.widget.HackyViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Caij on 2016/6/24.
 */
public class ImagePrewActivity extends BaseActivity {

    @BindView(R.id.vp_image)
    HackyViewPager mVpImage;
    @BindView(R.id.tv_image_count)
    TextView mTvImageCount;

    public static Intent newIntent(Context context, ArrayList<String> paths, ArrayList<String> hdPaths, int position) {
        return new Intent(context, ImagePrewActivity.class)
            .putStringArrayListExtra(Key.IMAGE_PATHS, paths)
            .putExtra(Key.POSITION, position)
            .putExtra(Key.HD_IMAGE_PATHS, hdPaths);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_prew);
        ButterKnife.bind(this);
//        ViewCompat.setTransitionName(mVpImage, Key.TRANSIT_PIC);
        final ArrayList<String> paths = getIntent().getStringArrayListExtra(Key.IMAGE_PATHS);
        final ArrayList<String> hdPaths = getIntent().getStringArrayListExtra(Key.HD_IMAGE_PATHS);
        final int position = getIntent().getIntExtra(Key.POSITION, 0);
        init(paths, hdPaths, position);
    }

    @Override
    protected void setTheme() {
        int themePosition = ThemeUtils.getThemePosition(this);
        setTheme(ThemeUtils.THEME_ARR[themePosition][ThemeUtils.APP_IMAGE_PRE_THEME_POSITION]);
    }

    @Override
    protected AbsBasePresent createPresent() {
        return null;
    }

    private void init(ArrayList<String> paths, ArrayList<String> hdPaths, int position) {
        final List<BaseFragment> fragments = new ArrayList<>();
        for (int i = 0; i < paths.size(); i ++) {
            ImagePreviewFragment fragment = ImagePreviewFragment.newInstance(paths.get(i), hdPaths == null ? null : hdPaths.get(i));
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

    private static class ImageFragmentAdapter extends StatusFragmentPagerAdapter {

        public ImageFragmentAdapter(FragmentManager fm, List<BaseFragment> fragments, List<String> titles) {
            super(fm, fragments, titles);
        }
    }
}
