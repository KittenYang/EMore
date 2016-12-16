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
import com.caij.emore.bean.Comment;
import com.caij.emore.database.bean.Status;
import com.caij.emore.ui.activity.StatusDetailActivity;
import com.caij.emore.ui.activity.UserInfoActivity;
import com.caij.emore.ui.activity.publish.ReplyCommentActivity;
import com.caij.emore.utils.DateUtil;
import com.caij.emore.utils.ImageLoader;
import com.caij.rvadapter.BaseViewHolder;
import com.caij.rvadapter.RecyclerViewOnItemClickListener;
import com.caij.rvadapter.adapter.BaseAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Caij on 2016/7/4.
 */
public class MessageCommentAdapter extends BaseAdapter<Comment, MessageCommentAdapter.CommentMentionViewHolder> {

    private ImageLoader.ImageConfig mImageConfig;

    public MessageCommentAdapter(Context context) {
        super(context);
        mImageConfig = new ImageLoader.ImageConfigBuild().setScaleType(ImageLoader.ScaleType.CENTER_CROP)
                .setCircle(true).build();
    }

    @Override
    public CommentMentionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_comment_mention, parent, false);
        return new CommentMentionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentMentionViewHolder holder, int position) {
        Comment comment = getItem(position);
        ImageLoader.loadUrl(mContext, holder.sdvAvatar, comment.getUser().getAvatar_large(),
                R.drawable.circle_image_placeholder, mImageConfig);
        holder.tvHeadName.setText(comment.getUser().getName());
        holder.tvComment.setText(comment.getText());

        Status weibo = comment.getStatus();
        holder.tvBottomName.setText("@" + weibo.getUser().getName());
        holder.tvWeibo.setText(weibo.getText());
        ImageLoader.load(mContext, holder.imageView,
                weibo.getBmiddle_pic() != null ?  weibo.getBmiddle_pic() : weibo.getUser().getAvatar_large(),
                R.drawable.weibo_image_placeholder);

        String createAt = DateUtil.convWeiboDate(mContext, weibo.getCreated_at().getTime());
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
        @BindView(R.id.tv_status)
        TextView tvWeibo;
        @BindView(R.id.tv_reply)
        TextView tvReplay;

        TextView tvHeadName;
        TextView tvBottomName;

        public CommentMentionViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            tvHeadName = (TextView) itemView.findViewById(R.id.item_head).findViewById(R.id.tv_name);
            View bottomView = itemView.findViewById(R.id.item_bottom);
            tvBottomName = (TextView) bottomView.findViewById(R.id.tv_name);
        }
    }

}
