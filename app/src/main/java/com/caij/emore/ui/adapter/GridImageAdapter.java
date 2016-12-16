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
import com.caij.emore.utils.ToastUtil;
import com.caij.emore.widget.RatioImageView;
import com.caij.rvadapter.BaseViewHolder;
import com.caij.rvadapter.adapter.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Caij on 2016/6/23.
 */
public class GridImageAdapter extends BaseAdapter<Image, BaseViewHolder> {

    private ImageLoader.ImageConfig mImageConfig;
    private ImageSelectListener mImageSelectListener;

    private List<String> selectImages;
    private int mMaxImageSelectCount;

    public GridImageAdapter(Context context, int maxImageSelectCount) {
        super(context);
        mImageConfig = new ImageLoader.ImageConfigBuild().
                setScaleType(ImageLoader.ScaleType.CENTER_CROP).build();
        selectImages = new ArrayList<>(maxImageSelectCount);
        mMaxImageSelectCount = maxImageSelectCount;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 2) {
            View view = mInflater.inflate(R.layout.item_image, parent, false);
            ImageViewHolder imageViewHolder = new ImageViewHolder(view);
            return imageViewHolder;
        }else {
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            RatioImageView imageView = new RatioImageView(mContext);
            imageView.setRatio(1);
            imageView.setLayoutParams(params);
            CameraViewHolder viewHolder = new CameraViewHolder(imageView);
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

    public List<String> getSelectImages() {
        return selectImages;
    }

    public void setSelectImages(List<String> selectImages) {
        this.selectImages = selectImages;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof  ImageViewHolder) {
            ImageViewHolder imageView = (ImageViewHolder) holder;
            Image image = getItem(position);
            boolean isSelect = selectImages.contains(image.getPath());
            imageView.selectCheckBox.setSelected(isSelect);
            imageView.selectCheckBox.setTag(image);
            imageView.viewShaw.setVisibility(isSelect ? View.VISIBLE : View.GONE);
            String path = "file://" + image.getPath();
            ImageLoader.loadUrl(mContext, imageView.imageView, path, R.drawable.weibo_image_placeholder, mImageConfig);
        }else if (holder instanceof  CameraViewHolder){
            CameraViewHolder cameraViewHolder = (CameraViewHolder) holder;
            cameraViewHolder.imageView.setBackgroundResource(R.mipmap.image_picker_take_image);
        }
    }

    public class ImageViewHolder extends BaseViewHolder {

        @BindView(R.id.image_view)
        RatioImageView imageView;
        @BindView(R.id.select_check_box)
        ImageView selectCheckBox;
        @BindView(R.id.view_shaw)
        View viewShaw;

        public ImageViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            selectCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Image image = (Image) v.getTag();
                    if (!selectImages.contains(image.getPath())) {
                        v.setSelected(true);
                        viewShaw.setVisibility(View.VISIBLE);
                        AnimatorSet animatorSet = new AnimatorSet();
                        ObjectAnimator scaleAnimatorX = ObjectAnimator.ofFloat(v, View.SCALE_X, 1f, 1.15f, 1f);
                        ObjectAnimator scaleAnimatorY = ObjectAnimator.ofFloat(v, View.SCALE_Y, 1f, 1.15f, 1f);
                        animatorSet.playTogether(scaleAnimatorY, scaleAnimatorX);
                        animatorSet.start();

                        selectImages.add(image.getPath());
                    }else {
                        if (getSelectImages().size() >= mMaxImageSelectCount) {
                            ToastUtil.show(mContext, String.format(mContext.getString(R.string.max_image_select_hint), mMaxImageSelectCount));
                            return;
                        }else {
                            v.setSelected(false);
                            viewShaw.setVisibility(View.GONE);
                            selectImages.remove(image.getPath());
                        }
                    }

                    if (mImageSelectListener != null) {
                        mImageSelectListener.onSelect(selectImages.contains(image.getPath()), image);
                    }
                }
            });
        }
    }

    public static class CameraViewHolder extends BaseViewHolder {

        RatioImageView imageView;

        public CameraViewHolder(final View itemView) {
            super(itemView);
            imageView = (RatioImageView) itemView;
        }
    }

    public static interface ImageSelectListener {
        public boolean onSelect(boolean isSelect, Image image);
    }
}
