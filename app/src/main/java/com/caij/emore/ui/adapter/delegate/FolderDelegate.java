package com.caij.emore.ui.adapter.delegate;

import android.content.Context;
import android.widget.ImageView;

import com.caij.emore.R;
import com.caij.emore.bean.ImageFolder;
import com.caij.emore.image.ImageLoadFactory;
import com.caij.emore.widget.recyclerview.OnItemPartViewClickListener;
import com.caij.rvadapter.BaseViewHolder;

/**
 * Created by Ca1j on 2016/12/21.
 */

public class FolderDelegate extends BaseItemViewDelegate<ImageFolder> {

    public FolderDelegate(OnItemPartViewClickListener onClickListener) {
        super(onClickListener);
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.list_item_folder;
    }

    @Override
    public void convert(BaseViewHolder baseViewHolder, ImageFolder imageFolder, int i) {
        Context context = baseViewHolder.getConvertView().getContext();
        String path = "file://" + imageFolder.getImgPath();
        ImageView imageView = baseViewHolder.getView(R.id.imageView);
        ImageLoadFactory.getImageLoad().loadImageCenterCrop(context, imageView, path, R.drawable.weibo_image_placeholder);

        baseViewHolder.setText(R.id.tv_folder_name, imageFolder.getName());
        baseViewHolder.setText(R.id.tv_folder_image_count, imageFolder.getCount() + context.getString(R.string.image_count));
        baseViewHolder.setVisible(R.id.iv_select, imageFolder.isSelected());
    }

    @Override
    public boolean isForViewType(ImageFolder imageFolder, int i) {
        return true;
    }

    @Override
    public void onCreateViewHolder(BaseViewHolder baseViewHolder) {

    }
}
