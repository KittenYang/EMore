package com.caij.emore.ui.adapter;

import android.content.Context;
import android.view.View;

/**
 * Created by Caij on 2016/7/5.
 */
public class MyMessageCommentAdapter extends MessageCommentAdapter {
    public MyMessageCommentAdapter(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(CommentMentionViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.tvReplay.setVisibility(View.GONE);
    }
}
