package com.caij.emore.ui.adapter;

import android.content.Context;
import android.text.Html;
import android.view.ViewGroup;

import com.caij.emore.R;
import com.caij.emore.bean.Weibo;
import com.caij.emore.utils.DateUtil;
import com.caij.emore.utils.ImageLoader;
import com.caij.emore.view.recyclerview.BaseAdapter;

/**
 * Created by Caij on 2016/6/16.
 */
public class RepostAdapter extends BaseAdapter<Weibo, CommentAdapter.CommentViewHolder> {

    public RepostAdapter(Context context) {
        super(context);
    }

    @Override
    public CommentAdapter.CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CommentAdapter.CommentViewHolder(mInflater.inflate(R.layout.item_weibo_comment, parent, false), mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(CommentAdapter.CommentViewHolder holder, int position) {
        Weibo weibo = getItem(position);
        holder.txtContent.setText(weibo.getContentSpannableString());

        String createAt = DateUtil.convDate(holder.txtContent.getContext(), weibo.getCreated_at());
        String from = String.format("%s", Html.fromHtml(weibo.getSource()));
        String desc = String.format("%s %s", createAt, from);
        holder.txtDesc.setText(desc);

        holder.txtName.setText(weibo.getUser().getName());

        ImageLoader.ImageConfig imageConfig = new ImageLoader.ImageConfigBuild().setCircle(true).build();
        ImageLoader.load(mContext, holder.imgPhoto, weibo.getUser().getAvatar_large(),
                R.drawable.circle_image_placeholder, imageConfig);
    }

}
