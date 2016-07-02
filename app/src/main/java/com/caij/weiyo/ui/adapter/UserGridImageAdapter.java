package com.caij.weiyo.ui.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.caij.weiyo.R;
import com.caij.weiyo.bean.Image;
import com.caij.weiyo.bean.PicUrl;
import com.caij.weiyo.utils.ImageLoader;
import com.caij.weiyo.view.RatioImageView;
import com.caij.weiyo.view.recyclerview.RecyclerViewOnItemClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Caij on 2016/6/23.
 */
public class UserGridImageAdapter extends BaseAdapter<PicUrl, BaseViewHolder> {

    private ImageLoader.ImageConfig mImageConfig;

    public UserGridImageAdapter(Context context) {
        super(context);
        mImageConfig = new ImageLoader.ImageConfigBuild().
                setScaleType(ImageLoader.ScaleType.CENTER_CROP).build();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        RatioImageView imageView = new RatioImageView(context);
        imageView.setLayoutParams(params);
        imageView.setRatio(1);
        ImageViewHolder viewHolder = new ImageViewHolder(imageView, mOnItemClickListener);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        ImageViewHolder imageView = (ImageViewHolder) holder;
        PicUrl image = getItem(position);
        ImageLoader.load(context, imageView.imageView, image.getBmiddle_pic(), R.drawable.weibo_image_placeholder, mImageConfig);
    }

    public static class ImageViewHolder extends BaseViewHolder {

        RatioImageView imageView;

        public ImageViewHolder(final View itemView, RecyclerViewOnItemClickListener onItemClickListener) {
            super(itemView, onItemClickListener);
            imageView = (RatioImageView) itemView;
        }
    }

}
