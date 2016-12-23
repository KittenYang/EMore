package com.caij.emore.ui.adapter.delegate;

import android.widget.ImageView;

import com.caij.emore.R;
import com.caij.emore.database.bean.User;
import com.caij.emore.utils.ImageLoader;
import com.caij.emore.widget.recyclerview.OnItemPartViewClickListener;
import com.caij.rvadapter.BaseViewHolder;

/**
 * Created by Ca1j on 2016/12/19.
 */

public class AttitudeDelegate extends BaseItemViewDelegate<User> {

    private final ImageLoader.ImageConfig mImageConfig;

    public AttitudeDelegate(OnItemPartViewClickListener onClickListener) {
        super(onClickListener);
        mImageConfig = new ImageLoader.ImageConfigBuild().
                setCircle(true).
                setScaleType(ImageLoader.ScaleType.CENTER_CROP).
                build();
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_weibo_attitude;
    }

    @Override
    public void convert(BaseViewHolder baseViewHolder, User user, int i) {
        baseViewHolder.setText(R.id.tv_name, user.getScreen_name());
        ImageView imageView = baseViewHolder.getView(R.id.iv_avatar);
        ImageLoader.loadUrl(baseViewHolder.getConvertView().getContext() ,imageView, user.getAvatar_large(),
                R.drawable.circle_image_placeholder, mImageConfig);
    }

    @Override
    public boolean isForViewType(User user, int i) {
        return true;
    }

    @Override
    public void onCreateViewHolder(BaseViewHolder baseViewHolder) {

    }
}
