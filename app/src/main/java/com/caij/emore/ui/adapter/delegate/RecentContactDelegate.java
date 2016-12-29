package com.caij.emore.ui.adapter.delegate;

import android.content.Context;
import android.widget.ImageView;

import com.caij.emore.R;
import com.caij.emore.bean.MessageUser;
import com.caij.emore.image.ImageLoadFactory;
import com.caij.emore.utils.DateUtil;
import com.caij.emore.widget.recyclerview.OnItemPartViewClickListener;
import com.caij.rvadapter.BaseViewHolder;

/**
 * Created by Ca1j on 2016/12/22.
 */

public class RecentContactDelegate extends BaseItemViewDelegate<MessageUser.UserListBean> {


    public RecentContactDelegate(OnItemPartViewClickListener onClickListener) {
        super(onClickListener);
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_message_user;
    }

    @Override
    public void convert(BaseViewHolder baseViewHolder, MessageUser.UserListBean userBean, int i) {
        Context context = baseViewHolder.getConvertView().getContext();
        ImageView avatarImageView = baseViewHolder.getView(R.id.iv_icon);
        ImageLoadFactory.getImageLoad().loadImageCircle(context, avatarImageView, userBean.getUser().getAvatar_large(),
                R.drawable.circle_image_placeholder);
        baseViewHolder.setText(R.id.tv_name, userBean.getUser().getScreen_name());
        baseViewHolder.setText(R.id.tv_message, userBean.getDirect_message().getText());
        baseViewHolder.setText(R.id.tv_time, DateUtil.convWeiboDate(context, userBean.getDirect_message().getCreated_at().getTime()));

        if (userBean.getUnread_count() > 0) {
            baseViewHolder.setVisible(R.id.tv_unread_count, true);
            baseViewHolder.setText(R.id.tv_unread_count, String.valueOf(userBean.getUnread_count() > 99 ?
                    99 : userBean.getUnread_count()));
        }else {
            baseViewHolder.setVisible(R.id.tv_unread_count, false);
        }
    }

    @Override
    public boolean isForViewType(MessageUser.UserListBean userListBean, int i) {
        return true;
    }

    @Override
    public void onCreateViewHolder(BaseViewHolder baseViewHolder) {

    }
}
