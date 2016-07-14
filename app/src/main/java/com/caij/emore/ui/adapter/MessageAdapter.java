package com.caij.emore.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.caij.emore.R;
import com.caij.emore.UserPrefs;
import com.caij.emore.bean.DirectMessage;
import com.caij.emore.utils.ImageLoader;
import com.caij.emore.view.recyclerview.BaseAdapter;
import com.caij.emore.view.recyclerview.BaseViewHolder;
import com.caij.emore.view.recyclerview.RecyclerViewOnItemClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Caij on 2016/7/10.
 */
public class MessageAdapter extends BaseAdapter<DirectMessage, BaseViewHolder> {

    public static final int TYPE_SELT = 1;
    public static final int TYPE_OTHER = 2;
    private final ImageLoader.ImageConfig mImageConfig;

    public MessageAdapter(Context context) {
        super(context);
        mImageConfig = new ImageLoader.ImageConfigBuild().
                setScaleType(ImageLoader.ScaleType.CENTER_CROP)
                .setCircle(true)
                .build();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_OTHER) {
            View view = mInflater.inflate(R.layout.item_chat_other_send_message, parent, false);
            return new OtherMessageViewHolder(view, mOnItemClickListener);
        } else if (viewType == TYPE_SELT) {
            View view = mInflater.inflate(R.layout.item_chat_self_send_message, parent, false);
            return new SelfMessageViewHolder(view, mOnItemClickListener);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        DirectMessage message = getItem(position);
        if (holder instanceof OtherMessageViewHolder) {
            OtherMessageViewHolder viewHolder = (OtherMessageViewHolder) holder;
            viewHolder.tvMessage.setText(message.getText());
            ImageLoader.load(mContext, viewHolder.ivAvatar, message.getSender().getAvatar_large(),
                    R.drawable.circle_image_placeholder, mImageConfig);

        }else if (holder instanceof SelfMessageViewHolder) {
            SelfMessageViewHolder viewHolder = (SelfMessageViewHolder) holder;
            viewHolder.tvMessage.setText(message.getText());
            ImageLoader.load(mContext, ((SelfMessageViewHolder) holder).ivAvatar, message.getSender().getAvatar_large(),
                    R.drawable.circle_image_placeholder, mImageConfig);
            if (message.getStatus() == DirectMessage.STATUS_SEND) {
                viewHolder.pbLoading.setVisibility(View.VISIBLE);
                viewHolder.ivFail.setVisibility(View.INVISIBLE);
            }else if (message.getStatus() == DirectMessage.STATUS_SUCCESS
                    || message.getStatus() == DirectMessage.STATUS_SERVER) {
                viewHolder.pbLoading.setVisibility(View.INVISIBLE);
                viewHolder.ivFail.setVisibility(View.INVISIBLE);
            }else if (message.getStatus() == DirectMessage.STATUS_FAIL) {
                viewHolder.ivFail.setVisibility(View.VISIBLE);
                viewHolder.pbLoading.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        DirectMessage directMessage = getItem(position);
        if (directMessage.getSender_id() == Long.parseLong(UserPrefs.get().getEMoreToken().getUid())) {
            return TYPE_SELT;
        } else {
            return TYPE_OTHER;
        }
    }

    public static class OtherMessageViewHolder extends BaseViewHolder {

        @BindView(R.id.iv_avatar)
        ImageView ivAvatar;
        @BindView(R.id.tv_message)
        TextView tvMessage;

        public OtherMessageViewHolder(View itemView, RecyclerViewOnItemClickListener onItemClickListener) {
            super(itemView, onItemClickListener);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class SelfMessageViewHolder extends BaseViewHolder {

        @BindView(R.id.iv_avatar)
        ImageView ivAvatar;
        @BindView(R.id.tv_message)
        TextView tvMessage;
        @BindView(R.id.pb_loading)
        ProgressBar pbLoading;
        @BindView(R.id.iv_fail)
        ImageView ivFail;

        public SelfMessageViewHolder(View itemView, RecyclerViewOnItemClickListener onItemClickListener) {
            super(itemView, onItemClickListener);
            ButterKnife.bind(this, itemView);
        }
    }

}