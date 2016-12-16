package com.caij.emore.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.caij.emore.R;
import com.caij.emore.bean.PageInfo;
import com.caij.emore.bean.ShortUrl;
import com.caij.emore.database.bean.Status;
import com.caij.emore.widget.weibo.list.RepostStatusListArticleItemView;
import com.caij.emore.widget.weibo.list.RepostStatusListImageItemView;
import com.caij.emore.widget.weibo.list.RepostStatusListVideoItemView;
import com.caij.emore.widget.weibo.list.StatusListArticleItemView;
import com.caij.emore.widget.weibo.list.StatusListImageItemView;
import com.caij.emore.widget.weibo.list.StatusListItemView;
import com.caij.emore.widget.weibo.list.StatusListVideoItemView;
import com.caij.rvadapter.BaseViewHolder;
import com.caij.rvadapter.RecyclerViewOnItemClickListener;
import com.caij.rvadapter.RecyclerViewOnItemLongClickListener;
import com.caij.rvadapter.adapter.BaseAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Caij on 2016/6/6.
 */
public class StatusAdapter extends BaseAdapter<Status, StatusAdapter.WeiboBaseViewHolder> {

    public static final int TYPE_NORMAL_IMAGE = 1;
    public static final int TYPE_REPOST_IMAGE = 2;
    public static final int TYPE_NORMAL_VIDEO = 3;
    public static final int TYPE_REPOST_VIDEO = 4;
    public static final int TYPE_NORMAL_ARTICLE = 5;
    public static final int TYPE_REPOST_ARTICLE = 6;

    private OnItemActionClickListener mOnItemActionClickListener;

    public StatusAdapter(Context context) {
        super(context);
    }

    public StatusAdapter(Context context, List<Status> entities) {
        super(context, entities);
    }

    public StatusAdapter(Context context, RecyclerViewOnItemClickListener onItemClickListener, OnItemActionClickListener onItemActionClickListener) {
        this(context);
        mOnItemClickListener = onItemClickListener;
        mOnItemActionClickListener = onItemActionClickListener;
    }

    @Override
    public WeiboBaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewGroup viewGroup = (ViewGroup) mInflater.inflate(R.layout.item_weibo, parent, false);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        StatusListItemView weiboListItemView = null;
        if (viewType == TYPE_NORMAL_IMAGE) {
            weiboListItemView = new StatusListImageItemView(parent.getContext());
        }else if (viewType == TYPE_REPOST_IMAGE){
            weiboListItemView = new RepostStatusListImageItemView(parent.getContext());
        }else if (viewType == TYPE_NORMAL_VIDEO) {
            weiboListItemView = new StatusListVideoItemView(parent.getContext());
        }else if (viewType == TYPE_REPOST_VIDEO) {
            weiboListItemView = new RepostStatusListVideoItemView(parent.getContext());
        }else if (viewType == TYPE_NORMAL_ARTICLE) {
            weiboListItemView = new StatusListArticleItemView(parent.getContext());
        }else if (viewType == TYPE_REPOST_ARTICLE) {
            weiboListItemView = new RepostStatusListArticleItemView(parent.getContext());
        }
        if (weiboListItemView != null) {
            weiboListItemView.setId(R.id.weibo_item_view);
            viewGroup.addView(weiboListItemView, layoutParams);
        }
        return new WeiboBaseViewHolder(viewGroup, mOnItemActionClickListener);
    }

    @Override
    public void onBindViewHolder(WeiboBaseViewHolder holder, int position) {
        if (holder.weiboItemView != null) {
            holder.weiboItemView.setStatus(getItem(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        Status status = getItem(position);
        return getStatusType(status);
    }

    public static int getStatusType(Status status) {
        if (status.getRetweeted_status() == null) {
            PageInfo pageInfo = status.getPage_info();
            if (pageInfo != null &&
                    (status.getPic_ids() == null || status.getPic_ids().size() == 0)) {
                int type = pageInfo.getPageType();
                if (type == ShortUrl.TYPE_VIDEO) {
                    return TYPE_NORMAL_VIDEO;
                } else if (type == ShortUrl.TYPE_ARTICLE && pageInfo.getCards() != null){
                    return TYPE_NORMAL_ARTICLE;
                } else {
                    return TYPE_NORMAL_IMAGE;
                }
            }else {
                return TYPE_NORMAL_IMAGE;
            }
        }else {
            Status reWebo = status.getRetweeted_status();
            PageInfo pageInfo = status.getPage_info();
            if (pageInfo != null
                    && (reWebo.getPic_ids() == null || reWebo.getPic_ids().size() == 0)) {
                int type = pageInfo.getPageType();
                if (type == ShortUrl.TYPE_VIDEO) {
                    return TYPE_REPOST_VIDEO;
                } else if (type == ShortUrl.TYPE_ARTICLE && pageInfo.getCards() != null){
                    return TYPE_REPOST_ARTICLE;
                } else {
                    return TYPE_REPOST_IMAGE;
                }
            } else {
                return TYPE_REPOST_IMAGE;
            }
        }
    }

    public static class WeiboBaseViewHolder extends BaseViewHolder {

        @BindView(R.id.weibo_item_view)
        StatusListItemView weiboItemView;

        public WeiboBaseViewHolder(View itemView, final OnItemActionClickListener onItemActionClickListener) {
            super(itemView);
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

        public void onStatusAttitudeUpdate(Status status) {
            weiboItemView.setLikeSelected(status);
            weiboItemView.setLikeCount(status);
        }

        public void onStatusCommentCountUpdate(Status status) {
            weiboItemView.setCommentCount(status);
        }

        public void onStatusRelayCountUpdate(Status status) {
            weiboItemView.setRelayCount(status);
        }
    }

    public static interface OnItemActionClickListener {
        void onMenuClick(View v, int position);
        void onLikeClick(View v, int position);
    }
}
