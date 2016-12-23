package com.caij.emore.ui.adapter.delegate;

import com.caij.emore.R;
import com.caij.emore.bean.Image;
import com.caij.emore.widget.recyclerview.OnItemPartViewClickListener;
import com.caij.rvadapter.BaseViewHolder;

/**
 * Created by Ca1j on 2016/12/21.
 */

public class CameraDelegate extends BaseItemViewDelegate<Image> {

    private static final int TYPE_CAMERA = 1;

    public CameraDelegate(OnItemPartViewClickListener onClickListener) {
        super(onClickListener);
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_image;
    }

    @Override
    public void convert(BaseViewHolder baseViewHolder, Image image, int i) {
        baseViewHolder.setImageResource(R.id.image_view, R.mipmap.image_picker_take_image);
    }

    @Override
    public boolean isForViewType(Image image, int i) {
        return image.getType() == TYPE_CAMERA;
    }

    @Override
    public void onCreateViewHolder(BaseViewHolder baseViewHolder) {
        baseViewHolder.setVisible(R.id.view_shaw, false);
        baseViewHolder.setVisible(R.id.select_check_box, false);
    }
}
