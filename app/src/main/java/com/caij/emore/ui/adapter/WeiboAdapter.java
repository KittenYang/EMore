package com.caij.emore.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.caij.emore.R;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.widget.recyclerview.BaseAdapter;
import com.caij.emore.widget.recyclerview.BaseViewHolder;
import com.caij.emore.widget.recyclerview.RecyclerViewOnItemClickListener;
import com.caij.emore.widget.weibo.list.WeiboListItemView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Caij on 2016/6/6.
 */
public class WeiboAdapter extends BaseAdapter<Weibo, WeiboAdapter.WeiboBaseViewHolder> {

    private static final int TYPE_NORMAL_IMAGE = 1;
    private static final int TYPE_REPOST_IMAGE = 2;
    private static final int TYPE_NORMAL_VIDEO = 3;
    private static final int TYPE_REPOST_VIDEO = 4;

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
    public WeiboBaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_NORMAL_IMAGE) {
            return new WeiboBaseViewHolder(mInflater.inflate(R.layout.item_weibo_image, parent, false),
                    mOnItemClickListener, mOnItemActionClickListener);
        }else if (viewType == TYPE_REPOST_IMAGE){
            return new WeiboBaseViewHolder(mInflater.inflate(R.layout.item_weibo_repost_image, parent, false),
                    mOnItemClickListener, mOnItemActionClickListener);
        }else if (viewType == TYPE_NORMAL_VIDEO) {
            return new WeiboBaseViewHolder(mInflater.inflate(R.layout.item_weibo_video, parent, false),
                    mOnItemClickListener, mOnItemActionClickListener);
        }else if (viewType == TYPE_REPOST_VIDEO) {
            return new WeiboBaseViewHolder(mInflater.inflate(R.layout.item_weibo_repost_video, parent, false),
                    mOnItemClickListener, mOnItemActionClickListener);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(WeiboBaseViewHolder holder, int position) {
        holder.weiboItemView.setWeibo(getItem(position));
    }

    @Override
    public int getItemViewType(int position) {
        Weibo weibo = getItem(position);
        if (weibo.getRetweeted_status() == null) {
            if (weibo.getPage_info() != null &&
                    (weibo.getPic_ids() == null || weibo.getPic_ids().size() == 0)) {
                return TYPE_NORMAL_VIDEO;
            }else {
                return TYPE_NORMAL_IMAGE;
            }
        }else {
            if (weibo.getPage_info() != null
                    && (weibo.getPic_ids() == null || weibo.getPic_ids().size() == 0)) {
                return TYPE_REPOST_VIDEO;
            } else {
                return TYPE_REPOST_IMAGE;
            }
        }
    }

    public static class WeiboBaseViewHolder extends BaseViewHolder {

        @BindView(R.id.weibo_item_view)
        WeiboListItemView weiboItemView;

        public WeiboBaseViewHolder(View itemView, RecyclerViewOnItemClickListener onItemClickListener,
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
