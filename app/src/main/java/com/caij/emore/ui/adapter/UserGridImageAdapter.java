package com.caij.emore.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.caij.emore.R;
import com.caij.emore.bean.StatusImageInfo;
import com.caij.emore.utils.ImageLoader;
import com.caij.emore.widget.RatioImageView;
import com.caij.rvadapter.BaseViewHolder;
import com.caij.rvadapter.adapter.BaseAdapter;

/**
 * Created by Caij on 2016/6/23.
 */
public class UserGridImageAdapter extends BaseAdapter<StatusImageInfo, BaseViewHolder> {

    private ImageLoader.ImageConfig mImageConfig;

    public UserGridImageAdapter(Context context) {
        super(context);
        mImageConfig = new ImageLoader.ImageConfigBuild().
                setScaleType(ImageLoader.ScaleType.CENTER_CROP).build();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        RatioImageView imageView = new RatioImageView(mContext);
        imageView.setLayoutParams(params);
        imageView.setRatio(1);
        ImageViewHolder viewHolder = new ImageViewHolder(imageView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        ImageViewHolder imageView = (ImageViewHolder) holder;
        StatusImageInfo image = getItem(position);
        ImageLoader.loadUrl(mContext, imageView.imageView, image.getBmiddle().getUrl(), R.drawable.weibo_image_placeholder, mImageConfig);
    }

    public static class ImageViewHolder extends BaseViewHolder {

        RatioImageView imageView;

        public ImageViewHolder(final View itemView) {
            super(itemView);
            imageView = (RatioImageView) itemView;
        }
    }

}
