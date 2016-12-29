package com.caij.emore.ui.fragment;

import android.os.Bundle;

import com.caij.emore.Key;
import com.caij.emore.bean.ImageInfo;
import com.caij.emore.present.ImagePrePresent;
import com.caij.emore.present.imp.LocalImagePrePresentImp;

/**
 * Created by Caij on 2016/6/24.
 */
public class LocalImagePreviewFragment extends ImagePreviewFragment {

    public static ImagePreviewFragment newInstance(ImageInfo imageInfo) {
        LocalImagePreviewFragment fragment = new LocalImagePreviewFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Key.IMAGE_PATH, imageInfo);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected ImagePrePresent createPresent() {
        ImageInfo picUrl = (ImageInfo) getArguments().getSerializable(Key.IMAGE_PATH);
        return new LocalImagePrePresentImp(picUrl, this);
    }

}
