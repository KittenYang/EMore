package com.caij.emore.ui.adapter.delegate;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.caij.emore.R;
import com.caij.emore.bean.Image;
import com.caij.emore.utils.ImageLoader;
import com.caij.emore.widget.recyclerview.OnItemPartViewClickListener;
import com.caij.rvadapter.BaseViewHolder;

import java.util.List;

/**
 * Created by Ca1j on 2016/12/21.
 */

public class GridImageDelegate extends BaseItemViewDelegate<Image> {

    private final ImageLoader.ImageConfig mImageConfig;

    private final static int TYPE_IMAGE = 2;
    private List<String> selectImages;

    public GridImageDelegate(OnItemPartViewClickListener onClickListener) {
        super(onClickListener);
        mImageConfig = new ImageLoader.ImageConfigBuild().
                setScaleType(ImageLoader.ScaleType.CENTER_CROP).build();
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_image;
    }

    @Override
    public void convert(BaseViewHolder baseViewHolder, Image image, int i) {
        Context context = baseViewHolder.getConvertView().getContext();

        boolean isSelect = selectImages != null && selectImages.contains(image.getPath());
        ImageView checkBox = baseViewHolder.getView(R.id.select_check_box);

        checkBox.setSelected(isSelect);
        checkBox.setTag(image);
        baseViewHolder.setVisible(R.id.view_shaw, isSelect);
        String path = "file://" + image.getPath();

        ImageView imageView = baseViewHolder.getView(R.id.image_view);
        ImageLoader.loadUrl(context, imageView, path, R.drawable.weibo_image_placeholder, mImageConfig);
    }

    public void setSelectImagePaths(List<String> selectImages) {
        this.selectImages = selectImages;
    }

    @Override
    public boolean isForViewType(Image image, int i) {
        return image.getType() == TYPE_IMAGE;
    }

    @Override
    public void onCreateViewHolder(final BaseViewHolder baseViewHolder) {
        baseViewHolder.setOnClickListener(R.id.select_check_box, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemPartViewClickListener.onClick(v, baseViewHolder.getAdapterPosition());
            }
        });
    }
}
