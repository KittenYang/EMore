package com.caij.emore.ui.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.caij.emore.R;
import com.caij.emore.bean.Image;
import com.caij.emore.utils.ImageLoader;
import com.caij.emore.view.RatioImageView;
import com.caij.emore.view.recyclerview.BaseAdapter;
import com.caij.emore.view.recyclerview.BaseViewHolder;
import com.caij.emore.view.recyclerview.RecyclerViewOnItemClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Caij on 2016/6/23.
 */
public class GridImageAdapter extends BaseAdapter<Image, BaseViewHolder> {

    private ImageLoader.ImageConfig mImageConfig;
    private ImageSelectListener mImageSelectListener;

    public GridImageAdapter(Context context) {
        super(context);
        mImageConfig = new ImageLoader.ImageConfigBuild().
                setScaleType(ImageLoader.ScaleType.CENTER_CROP).build();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 2) {
            View view = mInflater.inflate(R.layout.item_image, parent, false);
            ImageViewHolder imageViewHolder = new ImageViewHolder(view, mOnItemClickListener, mImageSelectListener);
            return imageViewHolder;
        }else {
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            RatioImageView imageView = new RatioImageView(mContext);
            imageView.setRatio(1);
            imageView.setLayoutParams(params);
            CameraViewHolder viewHolder = new CameraViewHolder(imageView, mOnItemClickListener);
            return viewHolder;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getType();
    }

    public void setOnSelectListener(ImageSelectListener imageSelectListener) {
        this.mImageSelectListener = imageSelectListener;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof  ImageViewHolder) {
            ImageViewHolder imageView = (ImageViewHolder) holder;
            Image image = getItem(position);
            imageView.selectCheckBox.setSelected(image.isSelected());
            imageView.selectCheckBox.setTag(image);
            imageView.viewShaw.setVisibility(image.isSelected() ? View.VISIBLE : View.GONE);
            String path = "file://" + image.getPath();
            ImageLoader.load(mContext, imageView.imageView, path, R.drawable.weibo_image_placeholder, mImageConfig);
        }else if (holder instanceof  CameraViewHolder){
            CameraViewHolder cameraViewHolder = (CameraViewHolder) holder;
            cameraViewHolder.imageView.setBackgroundResource(R.mipmap.image_picker_take_image);
        }
    }

    public static class ImageViewHolder extends BaseViewHolder {

        @BindView(R.id.image_view)
        RatioImageView imageView;
        @BindView(R.id.select_check_box)
        ImageView selectCheckBox;
        @BindView(R.id.view_shaw)
        View viewShaw;

        public ImageViewHolder(final View itemView,
                               RecyclerViewOnItemClickListener onItemClickListener,
                               final ImageSelectListener imageSelectListener) {
            super(itemView, onItemClickListener);
            ButterKnife.bind(this, itemView);
            selectCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Image image = (Image) v.getTag();
                    if (imageSelectListener != null && imageSelectListener.onSelect(!image.isSelected(), image)) {
                        image.setSelected(!image.isSelected());

                        v.setSelected(image.isSelected());
                        if (image.isSelected()) {
                            viewShaw.setVisibility(View.VISIBLE);
                            AnimatorSet animatorSet = new AnimatorSet();
                            ObjectAnimator scaleAnimatorX = ObjectAnimator.ofFloat(v, View.SCALE_X, 1f, 1.15f, 1f);
                            ObjectAnimator scaleAnimatorY = ObjectAnimator.ofFloat(v, View.SCALE_Y, 1f, 1.15f, 1f);
                            animatorSet.playTogether(scaleAnimatorY, scaleAnimatorX);
                            animatorSet.start();
                        }else {
                            viewShaw.setVisibility(View.GONE);
                        }
                    }
                }
            });
        }
    }

    public static class CameraViewHolder extends BaseViewHolder {

        RatioImageView imageView;

        public CameraViewHolder(final View itemView, RecyclerViewOnItemClickListener onItemClickListener) {
            super(itemView, onItemClickListener);
            imageView = (RatioImageView) itemView;
        }
    }

    public static interface ImageSelectListener {
        public boolean onSelect(boolean isSelect, Image image);
    }
}
