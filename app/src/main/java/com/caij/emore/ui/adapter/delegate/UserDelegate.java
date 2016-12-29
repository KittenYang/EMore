package com.caij.emore.ui.adapter.delegate;

import android.content.Context;
import android.widget.ImageView;

import com.caij.emore.R;
import com.caij.emore.database.bean.User;
import com.caij.emore.image.ImageLoadFactory;
import com.caij.emore.widget.recyclerview.OnItemPartViewClickListener;
import com.caij.rvadapter.BaseViewHolder;

/**
 * Created by Ca1j on 2016/12/22.
 */

public class UserDelegate extends BaseItemViewDelegate<User> {

    public UserDelegate(OnItemPartViewClickListener onClickListener) {
        super(onClickListener);
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_friendship;
    }

    @Override
    public void convert(BaseViewHolder baseViewHolder, User user, int i) {
        Context context = baseViewHolder.getConvertView().getContext();
        baseViewHolder.setText(R.id.txtName, user.getScreen_name());
        baseViewHolder.setText(R.id.txtDesc, user.getDescription());
        ImageView avatarImageView = baseViewHolder.getView(R.id.imgPhoto);
        ImageLoadFactory.getImageLoad().loadImageCircle(context, avatarImageView, user.getAvatar_large(),
                R.drawable.circle_image_placeholder);
    }

    @Override
    public boolean isForViewType(User user, int i) {
        return true;
    }

    @Override
    public void onCreateViewHolder(BaseViewHolder baseViewHolder) {

    }
}
