package com.caij.emore.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.caij.emore.R;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.widget.recyclerview.BaseAdapter;
import com.caij.emore.widget.recyclerview.BaseViewHolder;
import com.caij.emore.widget.recyclerview.RecyclerViewOnItemClickListener;
import com.caij.emore.widget.weibo.list.RepostWeiboListTextAndImageItemView;
import com.caij.emore.widget.weibo.list.WeiboListItemView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Caij on 2016/6/6.
 */
public class WeiboAdapter extends BaseAdapter<Weibo, BaseViewHolder> {

    private static final int TYPE_NORMAL = 1;
    private static final int TYPE_REPOST = 2;

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
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_NORMAL) {
            return new WeiboViewHoller(mInflater.inflate(R.layout.item_weibo, parent, false),
                    mOnItemClickListener, mOnItemActionClickListener);
        }else {
            return new RepostWeiboViewHoller(mInflater.inflate(R.layout.item_repost_weibo, parent, false),
                    mOnItemClickListener, mOnItemActionClickListener);
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof  WeiboViewHoller) {
            ((WeiboViewHoller)holder).weiboItemView.setWeibo(getItem(position));
        }else if (holder instanceof RepostWeiboViewHoller) {
            ((RepostWeiboViewHoller)holder).weiboItemView.setWeibo(getItem(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        Weibo weibo = getItem(position);
        if (weibo.getRetweeted_status() == null) {
            return TYPE_NORMAL;
        }else {
            return TYPE_REPOST;
        }
    }

    static class WeiboViewHoller extends BaseViewHolder {

        @BindView(R.id.weibo_item_view)
        WeiboListItemView weiboItemView;

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

    static class RepostWeiboViewHoller extends BaseViewHolder {

        @BindView(R.id.weibo_item_view)
        RepostWeiboListTextAndImageItemView weiboItemView;

        public RepostWeiboViewHoller(View itemView, final RecyclerViewOnItemClickListener onItemClickListener,
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
