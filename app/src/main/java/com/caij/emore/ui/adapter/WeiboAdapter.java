package com.caij.emore.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.caij.emore.R;
import com.caij.emore.bean.PageInfo;
import com.caij.emore.bean.ShortUrl;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.widget.recyclerview.BaseAdapter;
import com.caij.emore.widget.recyclerview.BaseViewHolder;
import com.caij.emore.widget.recyclerview.RecyclerViewOnItemClickListener;
import com.caij.emore.widget.weibo.list.RepostWeiboListArticleItemView;
import com.caij.emore.widget.weibo.list.RepostWeiboListImageItemView;
import com.caij.emore.widget.weibo.list.RepostWeiboListVideoItemView;
import com.caij.emore.widget.weibo.list.WeiboListArticleItemView;
import com.caij.emore.widget.weibo.list.WeiboListImageItemView;
import com.caij.emore.widget.weibo.list.WeiboListItemView;
import com.caij.emore.widget.weibo.list.WeiboListVideoItemView;

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
    private static final int TYPE_NORMAL_ARTICLE = 5;
    private static final int TYPE_REPOST_ARTICLE = 6;

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
        ViewGroup viewGroup = (ViewGroup) mInflater.inflate(R.layout.item_weibo, parent, false);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        WeiboListItemView weiboListItemView = null;
        if (viewType == TYPE_NORMAL_IMAGE) {
            weiboListItemView = new WeiboListImageItemView(parent.getContext());
        }else if (viewType == TYPE_REPOST_IMAGE){
            weiboListItemView = new RepostWeiboListImageItemView(parent.getContext());
        }else if (viewType == TYPE_NORMAL_VIDEO) {
            weiboListItemView = new WeiboListVideoItemView(parent.getContext());
        }else if (viewType == TYPE_REPOST_VIDEO) {
            weiboListItemView = new RepostWeiboListVideoItemView(parent.getContext());
        }else if (viewType == TYPE_NORMAL_ARTICLE) {
            weiboListItemView = new WeiboListArticleItemView(parent.getContext());
        }else if (viewType == TYPE_REPOST_ARTICLE) {
            weiboListItemView = new RepostWeiboListArticleItemView(parent.getContext());
        }
        if (weiboListItemView != null) {
            weiboListItemView.setId(R.id.weibo_item_view);
            viewGroup.addView(weiboListItemView, layoutParams);
        }
        return new WeiboBaseViewHolder(viewGroup,
                mOnItemClickListener, mOnItemActionClickListener);
    }

    @Override
    public void onBindViewHolder(WeiboBaseViewHolder holder, int position) {
        if (holder.weiboItemView != null) {
            holder.weiboItemView.setWeibo(getItem(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        Weibo weibo = getItem(position);
        if (weibo.getRetweeted_status() == null) {
            PageInfo pageInfo = weibo.getPage_info();
            if (pageInfo != null &&
                    (weibo.getPic_ids() == null || weibo.getPic_ids().size() == 0)) {
                int type = pageInfo.getPageType();
                if (type == ShortUrl.TYPE_VIDEO) {
                    return TYPE_NORMAL_VIDEO;
                } else if (type == ShortUrl.TYPE_ARTICLE){
                    return TYPE_NORMAL_ARTICLE;
                } else {
                    return TYPE_NORMAL_IMAGE;
                }
            }else {
                return TYPE_NORMAL_IMAGE;
            }
        }else {
            Weibo reWebo = weibo.getRetweeted_status();
            PageInfo pageInfo = weibo.getPage_info();
            if (pageInfo != null
                    && (reWebo.getPic_ids() == null || reWebo.getPic_ids().size() == 0)) {
                int type = pageInfo.getPageType();
                if (type == ShortUrl.TYPE_VIDEO) {
                    return TYPE_REPOST_VIDEO;
                } else if (type == ShortUrl.TYPE_ARTICLE){
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
