package com.caij.emore.ui.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;

import com.caij.emore.R;
import com.caij.emore.bean.Weibo;
import com.caij.emore.view.recyclerview.BaseAdapter;
import com.caij.emore.view.recyclerview.BaseViewHolder;
import com.caij.emore.view.recyclerview.RecyclerViewOnItemClickListener;
import com.caij.emore.view.weibo.WeiboListItemView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Caij on 2016/6/6.
 */
public class WeiboAdapter extends BaseAdapter<Weibo, WeiboAdapter.WeiboViewHoller> {

    private OnItemActionClickListener mOnItemActionClickListener;

    public WeiboAdapter(Context context) {
        super(context);
    }

    public WeiboAdapter(Context context, List<Weibo> entities) {
        super(context, entities);
    }

    public WeiboAdapter(Context context, RecyclerViewOnItemClickListener onItemClickListener, OnItemActionClickListener onItemActionClickListener) {
        this(context);
        mOnItemClickListener = onItemClickListener;
        mOnItemActionClickListener = onItemActionClickListener;
    }

    @Override
    public WeiboViewHoller onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WeiboViewHoller(mInflater.inflate(R.layout.item_weibo, parent, false),
                mOnItemClickListener, mOnItemActionClickListener);
    }

    @Override
    public void onBindViewHolder(WeiboViewHoller holder, int position) {
        holder.weiboItemView.setWeibo(getItem(position));
    }

    static class WeiboViewHoller extends BaseViewHolder {

        @BindView(R.id.weibo_item_view)
        WeiboListItemView weiboItemView;
        @BindView(R.id.cardView)
        CardView cardView;

        public WeiboViewHoller(View itemView, final RecyclerViewOnItemClickListener onItemClickListener,
                               final OnItemActionClickListener onItemActionClickListener) {
            super(itemView, onItemClickListener);
            ButterKnife.bind(this, itemView);
            weiboItemView.setOnMenuClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemActionClickListener != null) {
                        onItemActionClickListener.onMenuClick(v, getLayoutPosition());
                    }
                }
            });
            weiboItemView.setLikeClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemActionClickListener != null) {
                        onItemActionClickListener.onLikeClick(v, getLayoutPosition());
                    }
                }
            });
        }
    }

    public static interface OnItemActionClickListener {
        void onMenuClick(View v, int position);
        void onLikeClick(View v, int position);
    }
}
