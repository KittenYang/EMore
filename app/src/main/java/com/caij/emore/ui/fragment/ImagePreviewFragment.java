package com.caij.emore.ui.fragment;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;
import com.caij.emore.BuildConfig;
import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.bean.ImageInfo;
import com.caij.emore.present.ImagePrePresent;
import com.caij.emore.present.imp.ImagePrePresentImp;
import com.caij.emore.ui.view.ImagePreView;
import com.caij.emore.utils.DensityUtil;
import com.caij.emore.utils.DialogUtil;
import com.caij.emore.utils.ImageLoader;
import com.caij.emore.utils.LogUtil;
import com.caij.emore.utils.SystemUtil;
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
public class ImagePreviewFragment extends BaseFragment<ImagePrePresent> implements ImagePreView {

    @BindView(R.id.iv_image)
    ImageView mIvImage;
    @BindView(R.id.sciv)
    SubsamplingScaleImageView sciv;
    @BindView(R.id.pb_loading)
    ProgressBar pbLoading;
    @BindView(R.id.pv_loading)
    ProgressView pvLoading;
    @BindView(R.id.view_shadow)
    View viewShadow;

    public static ImagePreviewFragment newInstance(ImageInfo imageInfo, ImageInfo hdImageInfo) {
        ImagePreviewFragment fragment = new ImagePreviewFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Key.IMAGE_PATH, imageInfo);
        bundle.putSerializable(Key.HD_IMAGE_PATH, hdImageInfo);
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
        sciv.setDebug(BuildConfig.DEBUG);
        mPresent.loadImage();
    }

    @Override
    protected ImagePrePresent createPresent() {
        ImageInfo picUrl = (ImageInfo) getArguments().getSerializable(Key.IMAGE_PATH);
        ImageInfo hdPicUrl = (ImageInfo) getArguments().getSerializable(Key.HD_IMAGE_PATH);
        return new ImagePrePresentImp(getActivity(), picUrl, hdPicUrl, this);
    }

    @Override
    public void showGifImage(String picUrl) {
        sciv.setVisibility(View.GONE);
        mIvImage.setVisibility(View.VISIBLE);

        // TODO: 2016/10/24 暂时解决图片切换时闪的问题， 但是Glide为什么会闪的问题未找到原因  后面需要找到具体问题
        Glide.with(this).load(picUrl).dontAnimate().diskCacheStrategy(DiskCacheStrategy.SOURCE).skipMemoryCache(true).
                fitCenter().into(new ViewTarget<View, GlideDrawable>(mIvImage) {

            private GlideDrawable mGlideDrawable;

            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                mGlideDrawable = resource;
                mIvImage.setImageDrawable(resource);
                resource.start();
            }

            @Override
            public void onStart() {
                if (mGlideDrawable != null) {
                    mGlideDrawable.start();
                }
            }

            @Override
            public void onStop() {
                super.onStop();
                if (mGlideDrawable != null) {
                    mGlideDrawable.stop();
                }
            }
        });
    }

    @Override
    public void showLocalImage(String localFilePath) {
        sciv.setVisibility(View.VISIBLE);
        mIvImage.setVisibility(View.GONE);

        sciv.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_INSIDE);

        sciv.setImage(ImageSource.uri(localFilePath));
    }

    @Override
    public void showSelectDialog(String[] items, DialogInterface.OnClickListener onClickListener) {
        DialogUtil.showItemDialog(getActivity(), null, items, onClickListener);
    }

    @Override
    public void showProgress(boolean isShow) {
        if (isShow) {
            pvLoading.setVisibility(View.VISIBLE);
            viewShadow.setVisibility(View.VISIBLE);
        }else {
            pvLoading.setVisibility(View.GONE);
            viewShadow.setVisibility(View.GONE);
        }
    }

    @Override
    public void showProgress(long total, long progress) {
        pvLoading.setMax((int) total);
        pvLoading.setProgress((int) progress);
    }

    @Override
    public void showLocalImage(Bitmap bitmap) {
        sciv.setVisibility(View.VISIBLE);
        mIvImage.setVisibility(View.GONE);
        sciv.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_INSIDE);
        sciv.setImage(ImageSource.bitmap(bitmap));
    }

    @Override
    public void showLongHImage(String path) {
        sciv.setVisibility(View.VISIBLE);
        mIvImage.setVisibility(View.GONE);
        sciv.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_TOP_CROP);
        sciv.setImage(ImageSource.uri(path));
    }

    @Override
    public void showLongHImage(Bitmap bitmap) {
        sciv.setVisibility(View.VISIBLE);
        mIvImage.setVisibility(View.GONE);
        sciv.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_TOP_CROP);
        sciv.setImage(ImageSource.bitmap(bitmap));
    }

    @OnClick({R.id.sciv, R.id.iv_image})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sciv:
                getActivity().finish();
                break;
            case R.id.iv_image:
                getActivity().finish();
                break;
        }
    }

    @OnLongClick({R.id.iv_image, R.id.sciv, R.id.view_shadow})
    public boolean onLongClick(View view) {
        switch (view.getId()) {
            case R.id.sciv:
                onViewLongClick();
                break;
            case R.id.iv_image:
                onViewLongClick();
                break;
        }
        return true;
    }

    private void onViewLongClick() {
        mPresent.onViewLongClick();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        sciv.recycle();
    }
}
