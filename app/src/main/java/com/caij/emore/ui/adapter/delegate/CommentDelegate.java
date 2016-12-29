package com.caij.emore.ui.adapter.delegate;

import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.caij.emore.R;
import com.caij.emore.bean.Comment;
import com.caij.emore.image.ImageLoadFactory;
import com.caij.emore.utils.DateUtil;
import com.caij.emore.widget.recyclerview.OnItemPartViewClickListener;
import com.caij.rvadapter.BaseViewHolder;

/**
 * Created by Ca1j on 2016/12/20.
 */

public class CommentDelegate extends BaseItemViewDelegate<Comment> {

    public CommentDelegate(OnItemPartViewClickListener onClickListener) {
        super(onClickListener);
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_weibo_comment;
    }

    @Override
    public void convert(BaseViewHolder baseViewHolder, Comment comment, int i) {

        TextView textView = baseViewHolder.getView(R.id.txtContent);
        textView.setText(comment.getTextSpannableString());

        String createAt = DateUtil.convWeiboDate(textView.getContext(), comment.getCreated_at());
        String from = String.format("%s", Html.fromHtml(comment.getSource()));
        String desc = String.format("%s %s", createAt, from);

        baseViewHolder.setText(R.id.txtDesc, desc);

        baseViewHolder.setText(R.id.txtName, comment.getUser().getName());

        ImageView imageView = baseViewHolder.getView(R.id.imgPhoto);

        ImageLoadFactory.getImageLoad().loadImageCircle(baseViewHolder.getConvertView().getContext(),
                imageView, comment.getUser().getAvatar_large(), R.drawable.circle_image_placeholder);
    }

    @Override
    public boolean isForViewType(Comment comment, int i) {
        return true;
    }

    @Override
    public void onCreateViewHolder(final BaseViewHolder baseViewHolder) {
        baseViewHolder.setOnClickListener(R.id.imgPhoto, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemPartViewClickListener.onClick(v, baseViewHolder.getAdapterPosition());
            }
        });
    }
}
