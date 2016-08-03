package com.caij.emore.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.caij.emore.BuildConfig;
import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.present.ImagePrePresent;
import com.caij.emore.present.imp.ImagePrePresentImp;
import com.caij.emore.present.view.ImagePreView;
import com.caij.emore.utils.ImageLoader;
import com.caij.emore.utils.LogUtil;
import com.caij.emore.view.subscaleview.ImageSource;
import com.caij.emore.view.subscaleview.SubsamplingScaleImageView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Caij on 2016/6/24.
 */
public class ImagePrewFragment extends BaseFragment implements ImagePreView {

    @BindView(R.id.iv_image)
    ImageView mIvImage;
    @BindView(R.id.sciv)
    SubsamplingScaleImageView sciv;

    ImagePrePresent mImagePrePresent;

    public static ImagePrewFragment newInstance(String url) {
        ImagePrewFragment fragment = new ImagePrewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Key.IMAGE_PATH, url);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_prew, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String picUrl = getArguments().getString(Key.IMAGE_PATH);
        mImagePrePresent = new ImagePrePresentImp(getActivity(), picUrl, this);
        mImagePrePresent.onCreate();

        mImagePrePresent.loadImage();
    }

    @Override
    public void showWebImage(String picUrl) {
        sciv.setVisibility(View.GONE);
        mIvImage.setVisibility(View.VISIBLE);

        ImageLoader.ImageConfig config = new ImageLoader.ImageConfigBuild()
            .setScaleType(ImageLoader.ScaleType.FIT_CENTER)
            .setSupportGif(true)
//            .setCacheMemory(false)
            .setDiskCacheStrategy(ImageLoader.CacheConfig.SOURCE)
            .build();
        ImageLoader.loadUrl(getActivity(), mIvImage, picUrl, android.R.color.black, config);
    }

    @Override
    public void showBigImage(String localFilePath) {
        LogUtil.d(this, "showBigImage %s", localFilePath);
        sciv.setVisibility(View.VISIBLE);
        mIvImage.setVisibility(View.GONE);

        sciv.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_TOP_CROP);
        sciv.setImage(ImageSource.uri(localFilePath));
        sciv.setDebug(BuildConfig.DEBUG);
    }

    @Override
    public void showLocalImage(String localFilePath) {
        sciv.setVisibility(View.GONE);
        mIvImage.setVisibility(View.VISIBLE);

        ImageLoader.ImageConfig config = new ImageLoader.ImageConfigBuild()
                .setScaleType(ImageLoader.ScaleType.FIT_CENTER)
                .setSupportGif(true)
//                .setCacheMemory(false)
                .setDiskCacheStrategy(ImageLoader.CacheConfig.SOURCE)
                .build();
        ImageLoader.loadFile(getActivity(), mIvImage, new File(localFilePath), android.R.color.black, config);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mImagePrePresent.onDestroy();
    }
}
