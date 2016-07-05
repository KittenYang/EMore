package com.caij.weiyo.ui.adapter;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.caij.weiyo.R;
import com.caij.weiyo.bean.Comment;
import com.caij.weiyo.utils.DateUtil;
import com.caij.weiyo.utils.ImageLoader;
import com.caij.weiyo.view.FixClickableSpanBugTextView;
import com.caij.weiyo.view.recyclerview.BaseAdapter;
import com.caij.weiyo.view.recyclerview.BaseViewHolder;
import com.caij.weiyo.view.recyclerview.RecyclerViewOnItemClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Caij on 2016/6/16.
 */
public class CommentAdapter extends BaseAdapter<Comment, CommentAdapter.CommentViewHolder> {

    public CommentAdapter(Context context) {
        super(context);
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CommentViewHolder(mInflater.inflate(R.layout.item_weibo_comment, parent, false), mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        Comment comment = getItem(position);
        holder.txtContent.setText(comment.getTextSpannableString());

        String createAt = DateUtil.convDate(holder.txtContent.getContext(), comment.getCreated_at());
        String from = String.format("%s", Html.fromHtml(comment.getSource()));
        String desc = String.format("%s %s", createAt, from);
        holder.txtDesc.setText(desc);

        holder.txtName.setText(comment.getUser().getName());

        ImageLoader.ImageConfig imageConfig = new ImageLoader.ImageConfigBuild().setCircle(true).build();
        ImageLoader.load(mContext, holder.imgPhoto, comment.getUser().getAvatar_large(),
                R.mipmap.ic_default_circle_head_image, imageConfig);
    }

    public static class CommentViewHolder extends BaseViewHolder {

        @BindView(R.id.imgPhoto)
        ImageView imgPhoto;
        @BindView(R.id.txtName)
        TextView txtName;
        @BindView(R.id.txtContent)
        FixClickableSpanBugTextView txtContent;
        @BindView(R.id.txtDesc)
        TextView txtDesc;

        public CommentViewHolder(View itemView, RecyclerViewOnItemClickListener onItemClickListener) {
            super(itemView, onItemClickListener);
            ButterKnife.bind(this, itemView);
        }
    }

}
