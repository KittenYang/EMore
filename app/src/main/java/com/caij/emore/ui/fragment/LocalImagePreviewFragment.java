package com.caij.emore.ui.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.caij.emore.BuildConfig;
import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.bean.ImageInfo;
import com.caij.emore.present.ImagePrePresent;
import com.caij.emore.present.imp.ImagePrePresentImp;
import com.caij.emore.present.imp.LocalImagePrePresentImp;
import com.caij.emore.ui.view.ImagePreView;
import com.caij.emore.utils.DialogUtil;
import com.caij.emore.utils.ImageLoader;
import com.caij.emore.utils.LogUtil;
import com.caij.emore.widget.subscaleview.ImageSource;
import com.caij.emore.widget.subscaleview.SubsamplingScaleImageView;
import com.caij.progressview.ProgressView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * Created by Caij on 2016/6/24.
 */
public class LocalImagePreviewFragment extends ImagePreviewFragment {

    public static ImagePreviewFragment newInstance(ImageInfo imageInfo, ImageInfo hdImageInfo) {
        LocalImagePreviewFragment fragment = new LocalImagePreviewFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Key.IMAGE_PATH, imageInfo);
        bundle.putSerializable(Key.HD_IMAGE_PATH, hdImageInfo);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected ImagePrePresent createPresent() {
        ImageInfo picUrl = (ImageInfo) getArguments().getSerializable(Key.IMAGE_PATH);
        return new LocalImagePrePresentImp(getActivity(), picUrl, this);
    }

}
