package com.caij.emore.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.caij.emore.R;
import com.caij.emore.bean.Attitude;
import com.caij.emore.bean.Comment;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.ui.activity.WeiboDetialActivity;
import com.caij.emore.ui.activity.publish.ReplyCommentActivity;
import com.caij.emore.utils.DateUtil;
import com.caij.emore.utils.ImageLoader;
import com.caij.emore.view.recyclerview.BaseAdapter;
import com.caij.emore.view.recyclerview.BaseViewHolder;
import com.caij.emore.view.recyclerview.RecyclerViewOnItemClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Caij on 2016/7/4.
 */
public class ToMeAttitudeAdapter extends BaseAdapter<Attitude, ToMeAttitudeAdapter.CommentMentionViewHolder> {

    private ImageLoader.ImageConfig mImageConfig;

    public ToMeAttitudeAdapter(Context context) {
        super(context);
        mImageConfig = new ImageLoader.ImageConfigBuild().setScaleType(ImageLoader.ScaleType.CENTER_CROP)
                .setCircle(true).build();
    }

    @Override
    public CommentMentionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_comment_mention, parent, false);
        return new CommentMentionViewHolder(view, mOnItemClickListener, new RecyclerViewOnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = WeiboDetialActivity.newIntent(mContext, getItem(position).getStatus().getId());
                mContext.startActivity(intent);
            }
        }, new RecyclerViewOnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Attitude attitude = getItem(position);
                Intent intent = ReplyCommentActivity.newIntent(mContext, attitude.getStatus().getId(), attitude.getId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public void onBindViewHolder(CommentMentionViewHolder holder, int position) {
        Attitude attitude = getItem(position);
        ImageLoader.load(mContext, holder.sdvAvatar, attitude.getUser().getAvatar_large(),
                R.drawable.circle_image_placeholder, mImageConfig);
        holder.tvHeadName.setText(attitude.getUser().getName());
        holder.tvComment.setText(attitude.getText());

        Weibo weibo = attitude.getStatus();
        holder.tvBottomName.setText("@" + weibo.getUser().getName());
        holder.tvWeibo.setText(weibo.getText());
        ImageLoader.load(mContext, holder.imageView,
                weibo.getBmiddle_pic() != null ?  weibo.getBmiddle_pic() : weibo.getUser().getAvatar_large(),
                R.drawable.weibo_image_placeholder);

        String createAt = "";
        if (!TextUtils.isEmpty(weibo.getCreated_at()))
            createAt = DateUtil.convWeiboDate(mContext, weibo.getCreated_at());
        String from = "";
        if (!TextUtils.isEmpty(weibo.getSource()))
            from = String.format("%s", Html.fromHtml(weibo.getSource()));
        String desc = String.format("%s %s", createAt, from);
        holder.tvSource.setText(desc);

    }

    public static class CommentMentionViewHolder extends BaseViewHolder {

        @BindView(R.id.sdv_avatar)
        ImageView sdvAvatar;
        @BindView(R.id.img_verified)
        ImageView imgVerified;
        @BindView(R.id.tv_source)
        TextView tvSource;
        @BindView(R.id.tv_comment)
        TextView tvComment;
        @BindView(R.id.image_view)
        ImageView imageView;
        @BindView(R.id.tv_weibo)
        TextView tvWeibo;
        @BindView(R.id.tv_reply)
        TextView tvReplay;

        TextView tvHeadName;
        TextView tvBottomName;

        private RecyclerViewOnItemClickListener onWeiboViewClickListener;
        private RecyclerViewOnItemClickListener onReplayClickListener;

        public CommentMentionViewHolder(final View itemView, RecyclerViewOnItemClickListener onItemClickListener,
                                        final RecyclerViewOnItemClickListener onWeiboViewClickListener,
                                        final RecyclerViewOnItemClickListener onReplayClickListener) {
            super(itemView, onItemClickListener);
            ButterKnife.bind(this, itemView);
            this.onWeiboViewClickListener = onWeiboViewClickListener;
            this.onReplayClickListener = onReplayClickListener;
            tvHeadName = (TextView) itemView.findViewById(R.id.item_head).findViewById(R.id.tv_name);
            View bottomView = itemView.findViewById(R.id.item_bottom);
            tvBottomName = (TextView) bottomView.findViewById(R.id.tv_name);
            bottomView.setOnClickListener(this);
            tvReplay.setOnClickListener(this);
            tvReplay.setVisibility(View.GONE);
        }

        @Override
        public void onClick(View v) {
            super.onClick(v);
            if (v.getId() == R.id.item_bottom) {
                if (onWeiboViewClickListener != null) {
                    onWeiboViewClickListener.onItemClick(v, getLayoutPosition());
                }
            }else if (v.getId() ==  R.id.tv_reply) {
                if (onReplayClickListener != null) {
                    onReplayClickListener.onItemClick(v, getLayoutPosition());
                }
            }
        }
    }

}
