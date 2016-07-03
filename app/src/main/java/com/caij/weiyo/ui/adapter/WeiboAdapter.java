package com.caij.weiyo.ui.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;

import com.caij.weiyo.R;
import com.caij.weiyo.bean.Weibo;
import com.caij.weiyo.view.weibo.WeiboItemView;
import com.caij.weiyo.view.recyclerview.RecyclerViewOnItemClickListener;
import com.caij.weiyo.view.weibo.WeiboListItemView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Caij on 2016/6/6.
 */
public class WeiboAdapter extends BaseAdapter<Weibo, WeiboAdapter.WeiboViewHoller> {

    private View.OnClickListener mMenuClickListener;

    public WeiboAdapter(Context context) {
        super(context);
    }

    public WeiboAdapter(Context context, List<Weibo> entities) {
        super(context, entities);
    }

    public WeiboAdapter(Context context, RecyclerViewOnItemClickListener onItemClickListener,
                        View.OnClickListener menuClickListener) {
        this(context);
        mOnItemClickListener = onItemClickListener;
        mMenuClickListener= menuClickListener;
    }

    @Override
    public WeiboViewHoller onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WeiboViewHoller(mInflater.inflate(R.layout.item_weibo, parent, false), mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(WeiboViewHoller holder, int position) {
        holder.weiboItemView.setWeibo(getItem(position));
        holder.weiboItemView.setOnMenuClickListenet(mMenuClickListener);
    }

    static class WeiboViewHoller extends BaseViewHolder {

        @BindView(R.id.weibo_item_view)
        WeiboListItemView weiboItemView;
        @BindView(R.id.cardView)
        CardView cardView;

        public WeiboViewHoller(View itemView, RecyclerViewOnItemClickListener onItemClickListener) {
            super(itemView, onItemClickListener);
            ButterKnife.bind(this, itemView);
        }
    }

}
