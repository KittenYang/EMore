package com.caij.emore.ui.adapter.delegate;

import android.content.Context;
import android.widget.ImageView;

import com.caij.emore.R;
import com.caij.emore.bean.StatusImageInfo;
import com.caij.emore.utils.ImageLoader;
import com.caij.emore.widget.recyclerview.OnItemPartViewClickListener;
import com.caij.rvadapter.BaseViewHolder;

/**
 * Created by Ca1j on 2016/12/23.
 */

public class StatusImageDelegate extends BaseItemViewDelegate<StatusImageInfo> {

    private final ImageLoader.ImageConfig mImageConfig;

    public StatusImageDelegate(OnItemPartViewClickListener onClickListener) {
        super(onClickListener);
        mImageConfig = new ImageLoader.ImageConfigBuild().
                setScaleType(ImageLoader.ScaleType.CENTER_CROP).build();
    }

    @Override
    public void convert(BaseViewHolder baseViewHolder, StatusImageInfo statusImageInfo, int i) {
        Context context = baseViewHolder.getConvertView().getContext();
        ImageView imageView = baseViewHolder.getView(R.id.image_view);
        ImageLoader.loadUrl(context, imageView, statusImageInfo.getBmiddle().getUrl(), R.drawable.weibo_image_placeholder, mImageConfig);
    }

    @Override
    public boolean isForViewType(StatusImageInfo statusImageInfo, int i) {
        return true;
    }


    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_image;
    }

    @Override
    public void onCreateViewHolder(BaseViewHolder baseViewHolder) {
        baseViewHolder.setVisible(R.id.view_shaw, false);
        baseViewHolder.setVisible(R.id.select_check_box, false);
    }
}
