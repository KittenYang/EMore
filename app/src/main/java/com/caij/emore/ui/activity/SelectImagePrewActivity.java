package com.caij.emore.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.bean.ImageInfo;
import com.caij.emore.present.BasePresent;
import com.caij.emore.ui.fragment.BaseFragment;
import com.caij.emore.ui.fragment.LocalImagePreviewFragment;
import com.caij.emore.utils.ToastUtil;
import com.caij.emore.widget.HackyViewPager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Caij on 2016/6/24.
 */
public class SelectImagePrewActivity extends BaseToolBarActivity {

    private MenuItem mMiImageCount;

    @BindView(R.id.vp_image)
    HackyViewPager mVpImage;

    private ArrayList<String> selects;
    private ArrayList<String> paths;
    private int position;

    public static Intent newIntent(Context context, ArrayList<String> paths, int position) {
        return new Intent(context, SelectImagePrewActivity.class)
                .putExtra(Key.IMAGE_PATHS, paths)
                .putExtra(Key.POSITION, position);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setTitle("图片预览");

        paths = (ArrayList<String>) getIntent().getSerializableExtra(Key.IMAGE_PATHS);
        position = getIntent().getIntExtra(Key.POSITION, 0);

        selects = new ArrayList<>(paths.size());

        for (String path : paths) {
            selects.add(path);
        }

        init(paths, position);
    }

    private void init(final ArrayList<String> paths, int position) {
        final List<BaseFragment> fragments = new ArrayList<>();
        for (int i = 0; i < paths.size(); i ++) {
            fragments.add(createFragment(paths,  i));
        }

        ImagePrewActivity.ImageFragmentAdapter adapter = new ImagePrewActivity.ImageFragmentAdapter(getSupportFragmentManager(), fragments, null);
        mVpImage.setAdapter(adapter);
        mVpImage.setCurrentItem(position);

        mVpImage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mMiImageCount.setIcon(selects.contains(paths.get(position)) ? R.mipmap.check_box_checked : R.mipmap.check_box_unchecked);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private BaseFragment createFragment(ArrayList<String> paths,  int i) {
        ImageInfo imageInfo = new ImageInfo();
        imageInfo.setUrl(paths.get(i));
        return LocalImagePreviewFragment.newInstance(imageInfo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pre_select_image, menu);
        mMiImageCount = menu.findItem(R.id.image_select);

        mMiImageCount.setIcon(selects.contains(paths.get(position)) ? R.mipmap.check_box_checked : R.mipmap.check_box_unchecked);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.image_select) {
           if (selects.contains(paths.get(mVpImage.getCurrentItem()))) {
               selects.remove(paths.get(mVpImage.getCurrentItem()));
            }else {
               selects.add(paths.get(mVpImage.getCurrentItem()));
           }
            mMiImageCount.setIcon(selects.contains(paths.get(mVpImage.getCurrentItem())) ? R.mipmap.check_box_checked : R.mipmap.check_box_unchecked);
        }else if (item.getItemId() == R.id.select) {
            if (selects.size() > 0) {
                Intent intent = new Intent();
                intent.putExtra(Key.TYPE, true);
                intent.putStringArrayListExtra(Key.IMAGE_PATHS, selects);
                setResult(Activity.RESULT_OK, intent);

                finish();
            }else {
                ToastUtil.show(this, R.string.unselect_image);
            }
        }else if (item.getItemId() ==  android.R.id.home) {
            Intent intent = new Intent();
            intent.putExtra(Key.TYPE, false);
            intent.putStringArrayListExtra(Key.IMAGE_PATHS, selects);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(Key.TYPE, false);
        intent.putStringArrayListExtra(Key.IMAGE_PATHS, selects);
        setResult(Activity.RESULT_OK, intent);
        super.onBackPressed();
    }

    @Override
    public int getAttachLayoutId() {
        return R.layout.activity_pre_select;
    }

    @Override
    protected BasePresent createPresent() {
        return null;
    }
}
