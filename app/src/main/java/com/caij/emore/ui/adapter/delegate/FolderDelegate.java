package com.caij.emore.ui.adapter.delegate;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.caij.emore.R;
import com.caij.emore.bean.ImageFolder;
import com.caij.emore.utils.ImageLoader;
import com.caij.emore.widget.recyclerview.OnItemPartViewClickListener;
import com.caij.rvadapter.BaseViewHolder;

/**
 * Created by Ca1j on 2016/12/21.
 */

public class FolderDelegate extends BaseItemViewDelegate<ImageFolder> {

    private final ImageLoader.ImageConfig mImageConfig;

    public FolderDelegate(OnItemPartViewClickListener onClickListener) {
        super(onClickListener);
        mImageConfig = new ImageLoader.ImageConfigBuild().
                setScaleType(ImageLoader.ScaleType.CENTER_CROP).build();
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
        ImageLoader.loadUrl(context, imageView, path, R.drawable.weibo_image_placeholder, mImageConfig);

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
