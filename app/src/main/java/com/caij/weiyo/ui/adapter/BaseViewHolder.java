package com.caij.weiyo.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.caij.weiyo.view.recyclerview.RecyclerViewOnItemClickListener;

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
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, getLayoutPosition());
        }
    }
}
