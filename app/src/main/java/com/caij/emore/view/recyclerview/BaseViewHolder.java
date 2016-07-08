package com.caij.emore.view.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Caij on 2016/6/6.
 */
public class BaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private RecyclerViewOnItemClickListener mOnItemClickListener;

    public BaseViewHolder(View itemView, RecyclerViewOnItemClickListener onItemClickListener) {
        super(itemView);
        itemView.setOnClickListener(this);
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public void onClick(View v) {
        if (v == itemView) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, getLayoutPosition());
            }
        }
    }
}
