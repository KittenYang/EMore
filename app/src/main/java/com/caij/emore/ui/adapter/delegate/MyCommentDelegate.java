package com.caij.emore.ui.adapter.delegate;

import com.caij.emore.R;
import com.caij.emore.widget.recyclerview.OnItemPartViewClickListener;
import com.caij.rvadapter.BaseViewHolder;

/**
 * Created by Ca1j on 2016/12/22.
 */

public class MyCommentDelegate extends CommentMesssageDelegate {
    public MyCommentDelegate(OnItemPartViewClickListener onClickListener) {
        super(onClickListener);
    }

    @Override
    public void onCreateViewHolder(BaseViewHolder baseViewHolder) {
        super.onCreateViewHolder(baseViewHolder);
        baseViewHolder.setVisible(R.id.tv_reply, false);
    }
}
