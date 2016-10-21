package com.caij.emore.ui.activity;

import android.content.Context;
import android.content.Intent;

import com.caij.emore.Key;
import com.caij.emore.bean.ImageInfo;
import com.caij.emore.ui.fragment.BaseFragment;
import com.caij.emore.ui.fragment.LocalImagePreviewFragment;

import java.util.ArrayList;


/**
 * Created by Caij on 2016/6/24.
 */
public class LocalImagePrewActivity extends ImagePrewActivity {

    public static Intent newIntent(Context context, ArrayList<ImageInfo> paths, ArrayList<ImageInfo> hdPaths, int position) {
        return new Intent(context, LocalImagePrewActivity.class)
                .putExtra(Key.IMAGE_PATHS, paths)
                .putExtra(Key.POSITION, position)
                .putExtra(Key.HD_IMAGE_PATHS, hdPaths);
    }

    @Override
    protected BaseFragment createFragment(ArrayList<ImageInfo> paths, ArrayList<ImageInfo> hdPaths, int i) {
        return LocalImagePreviewFragment.newInstance(paths.get(i), hdPaths == null ? null : hdPaths.get(i));
    }
}
