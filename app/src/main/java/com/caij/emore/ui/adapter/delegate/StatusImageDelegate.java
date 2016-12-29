package com.caij.emore.ui.adapter.delegate;

import android.content.Context;
import android.widget.ImageView;

import com.caij.emore.R;
import com.caij.emore.bean.StatusImageInfo;
import com.caij.emore.image.ImageLoadFactory;
import com.caij.emore.widget.recyclerview.OnItemPartViewClickListener;
import com.caij.rvadapter.BaseViewHolder;

/**
 * Created by Ca1j on 2016/12/23.
 */

public class StatusImageDelegate extends BaseItemViewDelegate<StatusImageInfo> {


    public StatusImageDelegate(OnItemPartViewClickListener onClickListener) {
        super(onClickListener);
    }

    @Override
    public void convert(BaseViewHolder baseViewHolder, StatusImageInfo statusImageInfo, int i) {
        Context context = baseViewHolder.getConvertView().getContext();
        ImageView imageView = baseViewHolder.getView(R.id.image_view);
        ImageLoadFactory.getImageLoad().loadImageCenterCrop(context, imageView,
                statusImageInfo.getBmiddle().getUrl(), R.drawable.weibo_image_placeholder);
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
