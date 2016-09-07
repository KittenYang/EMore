package com.caij.emore.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.caij.emore.R;
import com.caij.emore.bean.MessageUser;
import com.caij.emore.utils.DateUtil;
import com.caij.emore.utils.ImageLoader;
import com.caij.emore.widget.recyclerview.BaseAdapter;
import com.caij.emore.widget.recyclerview.BaseViewHolder;
import com.caij.emore.widget.recyclerview.RecyclerViewOnItemClickListener;
import com.caij.emore.widget.recyclerview.RecyclerViewOnItemLongClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Caij on 2016/7/10.
 */
public class MessageUserAdapter extends BaseAdapter<MessageUser.UserListBean, MessageUserAdapter.ViewHolder> {

    private ImageLoader.ImageConfig mImageConfig;

    public MessageUserAdapter(Context context) {
        super(context);
        mImageConfig = new ImageLoader.ImageConfigBuild().
                setScaleType(ImageLoader.ScaleType.CENTER_CROP)
                .setCircle(true)
                .build();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_message_user, parent, false);
        return new ViewHolder(view, mOnItemClickListener, mRecyclerViewOnItemLongClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MessageUser.UserListBean userBean = getItem(position);
        ImageLoader.loadUrl(mContext, holder.ivIcon, userBean.getUser().getAvatar_large(),
                R.drawable.circle_image_placeholder, mImageConfig);
        holder.tvName.setText(userBean.getUser().getScreen_name());
        holder.tvMessage.setText(userBean.getDirect_message().getText());
        holder.tvTime.setText(DateUtil.convWeiboDate(mContext, userBean.getDirect_message().getCreated_at()));

        if (userBean.getUnread_count() > 0) {
            holder.tvUnreadCount.setVisibility(View.VISIBLE);
            holder.tvUnreadCount.setText(String.valueOf(userBean.getUnread_count() > 99 ?
                    99 : userBean.getUnread_count()));
        }else {
            holder.tvUnreadCount.setVisibility(View.GONE);
        }
    }

    public static class ViewHolder extends BaseViewHolder {

        @BindView(R.id.iv_icon)
        ImageView ivIcon;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_message)
        TextView tvMessage;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.tv_unread_count)
        TextView tvUnreadCount;

        public ViewHolder(View itemView, RecyclerViewOnItemClickListener onItemClickListener, final RecyclerViewOnItemLongClickListener longClickListener) {
            super(itemView, onItemClickListener);
            ButterKnife.bind(this, itemView);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return longClickListener.onItemLongClick(v, getLayoutPosition());
                }
            });
        }
    }

}
