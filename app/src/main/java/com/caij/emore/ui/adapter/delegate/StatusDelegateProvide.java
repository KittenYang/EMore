package com.caij.emore.ui.adapter.delegate;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.caij.emore.R;
import com.caij.emore.bean.PageInfo;
import com.caij.emore.bean.ShortUrl;
import com.caij.emore.database.bean.Status;
import com.caij.emore.widget.recyclerview.OnItemPartViewClickListener;
import com.caij.emore.widget.weibo.list.RepostStatusArticleItemView;
import com.caij.emore.widget.weibo.list.RepostStatusImageItemView;
import com.caij.emore.widget.weibo.list.RepostStatusVideoItemView;
import com.caij.emore.widget.weibo.list.StatusArticleItemView;
import com.caij.emore.widget.weibo.list.StatusImageItemView;
import com.caij.emore.widget.weibo.list.StatusListItemView;
import com.caij.emore.widget.weibo.list.StatusVideoItemView;
import com.caij.rvadapter.BaseViewHolder;

/**
 * Created by Ca1j on 2016/12/20.
 */

public interface StatusDelegateProvide {

    public static final int TYPE_NORMAL_IMAGE = 1;
    public static final int TYPE_REPOST_IMAGE = 2;
    public static final int TYPE_NORMAL_VIDEO = 3;
    public static final int TYPE_REPOST_VIDEO = 4;
    public static final int TYPE_NORMAL_ARTICLE = 5;
    public static final int TYPE_REPOST_ARTICLE = 6;

    public static abstract class StatusDelegate extends BaseItemViewDelegate<Status> {

        public StatusDelegate(OnItemPartViewClickListener onClickListener) {
            super(onClickListener);
        }

        @Override
        public void convert(BaseViewHolder baseViewHolder, Status status, int i) {
            StatusListItemView statusListItemView = baseViewHolder.getView(R.id.weibo_item_view);
            if (statusListItemView != null) {
                statusListItemView.setStatus(status);
            }
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_weibo;
        }

        @Override
        public void onCreateViewHolder(final BaseViewHolder baseViewHolder) {
            ViewGroup viewGroup = (ViewGroup) baseViewHolder.getConvertView();
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            StatusListItemView weiboListItemView = getStatusListItemView(viewGroup.getContext());
            if (weiboListItemView != null) {
                weiboListItemView.setId(R.id.weibo_item_view);
                viewGroup.addView(weiboListItemView, layoutParams);

                weiboListItemView.setOnMenuClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemPartViewClickListener.onClick(v, baseViewHolder.getAdapterPosition());
                    }
                });
                weiboListItemView.setLikeClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemPartViewClickListener.onClick(v, baseViewHolder.getAdapterPosition());
                    }
                });
            }
        }

        public abstract StatusListItemView getStatusListItemView(Context context);
    }

    public static class TextAndImageStatusDelegate extends StatusDelegate {

        public TextAndImageStatusDelegate(OnItemPartViewClickListener onClickListener) {
            super(onClickListener);
        }

        @Override
        public StatusListItemView getStatusListItemView(Context context) {
            return new StatusImageItemView(context);
        }

        @Override
        public boolean isForViewType(Status status, int i) {
            return Type.getStatusType(status) == TYPE_NORMAL_IMAGE;
        }
    }

    public static class RepostTextAndImageStatusDelegate extends StatusDelegate {

        public RepostTextAndImageStatusDelegate(OnItemPartViewClickListener onClickListener) {
            super(onClickListener);
        }

        @Override
        public StatusListItemView getStatusListItemView(Context context) {
            return new RepostStatusImageItemView(context);
        }

        @Override
        public boolean isForViewType(Status status, int i) {
            return Type.getStatusType(status) == TYPE_REPOST_IMAGE;
        }
    }

    public static class VideoStatusDelegate extends StatusDelegate {

        public VideoStatusDelegate(OnItemPartViewClickListener onClickListener) {
            super(onClickListener);
        }

        @Override
        public StatusListItemView getStatusListItemView(Context context) {
            return new StatusVideoItemView(context);
        }

        @Override
        public boolean isForViewType(Status status, int i) {
            return Type.getStatusType(status) == TYPE_NORMAL_VIDEO;
        }
    }

    public static class RepostVideoStatusDelegate extends StatusDelegate {

        public RepostVideoStatusDelegate(OnItemPartViewClickListener onClickListener) {
            super(onClickListener);
        }

        @Override
        public StatusListItemView getStatusListItemView(Context context) {
            return new RepostStatusVideoItemView(context);
        }

        @Override
        public boolean isForViewType(Status status, int i) {
            return Type.getStatusType(status) == TYPE_REPOST_VIDEO;
        }
    }

    public static class ArticleStatusDelegate extends StatusDelegate {

        public ArticleStatusDelegate(OnItemPartViewClickListener onClickListener) {
            super(onClickListener);
        }

        @Override
        public StatusListItemView getStatusListItemView(Context context) {
            return new StatusArticleItemView(context);
        }

        @Override
        public boolean isForViewType(Status status, int i) {
            return Type.getStatusType(status) == TYPE_NORMAL_ARTICLE;
        }
    }

    public static class RepostArticleStatusDelegate extends StatusDelegate {

        public RepostArticleStatusDelegate(OnItemPartViewClickListener onClickListener) {
            super(onClickListener);
        }

        @Override
        public StatusListItemView getStatusListItemView(Context context) {
            return new RepostStatusArticleItemView(context);
        }

        @Override
        public boolean isForViewType(Status status, int i) {
            return Type.getStatusType(status) == TYPE_REPOST_ARTICLE;
        }
    }

    public static class Type {
        public static int getStatusType(Status status) {
            if (status.getRetweeted_status() == null) {
                PageInfo pageInfo = status.getPage_info();
                if (pageInfo != null &&
                        (status.getPic_ids() == null || status.getPic_ids().size() == 0)) {
                    int type = pageInfo.getPageType();
                    if (type == ShortUrl.TYPE_VIDEO) {
                        return TYPE_NORMAL_VIDEO;
                    } else if (type == ShortUrl.TYPE_ARTICLE && pageInfo.getCards() != null) {
                        return TYPE_NORMAL_ARTICLE;
                    } else {
                        return TYPE_NORMAL_IMAGE;
                    }
                } else {
                    return TYPE_NORMAL_IMAGE;
                }
            } else {
                Status reWebo = status.getRetweeted_status();
                PageInfo pageInfo = status.getPage_info();
                if (pageInfo != null
                        && (reWebo.getPic_ids() == null || reWebo.getPic_ids().size() == 0)) {
                    int type = pageInfo.getPageType();
                    if (type == ShortUrl.TYPE_VIDEO) {
                        return TYPE_REPOST_VIDEO;
                    } else if (type == ShortUrl.TYPE_ARTICLE && pageInfo.getCards() != null) {
                        return TYPE_REPOST_ARTICLE;
                    } else {
                        return TYPE_REPOST_IMAGE;
                    }
                } else {
                    return TYPE_REPOST_IMAGE;
                }
            }
        }
    }

}
