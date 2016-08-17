package com.caij.emore.widget.recyclerview;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.caij.emore.R;


/**
 * Created by Caij on 2015/11/4.
 */
public class XRecyclerView extends RelativeLayout {

    public static final int STATE_NORMAL = 1;
    public static final int STATE_LOADING = 2;
    public static final int STATE_NO_MORE = 3;
    public static final int STATE_EMPTY = 4;

    private OnLoadMoreListener mOnLoadMoreListener;
    private LoadMoreView mLoadMoreView;
    private RecyclerView mRecyclerView;
    private View mEmptyView;
    private boolean mIsLoadMoreEnable = true;

    public XRecyclerView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public XRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    public XRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        mRecyclerView = new RecyclerView(context, attrs, defStyle);
        mRecyclerView.setId(R.id.recycler_view);
        addView(mRecyclerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        mRecyclerView.addOnScrollListener(mOpOnScrollChangeListener);
        mLoadMoreView = new LoadMoreView(context);
        mLoadMoreView.setState(STATE_EMPTY);
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.mOnLoadMoreListener = onLoadMoreListener;
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        HeaderAndFooterRecyclerViewAdapter headerAndFooterRecyclerViewAdapter = new HeaderAndFooterRecyclerViewAdapter(adapter);
        setAdapter(headerAndFooterRecyclerViewAdapter);
    }

    public void addOnScrollListener(RecyclerView.OnScrollListener onScrollListener) {
        mRecyclerView.addOnScrollListener(onScrollListener);
    }

    public void setAdapter(HeaderAndFooterRecyclerViewAdapter adapter)  {
        if (mRecyclerView.getAdapter() != null) {
            mRecyclerView.getAdapter().unregisterAdapterDataObserver(mDataObserver);
        }

        if (mIsLoadMoreEnable) {
            adapter.addFooterView(mLoadMoreView);
        }

        adapter.registerAdapterDataObserver(mDataObserver);
        mRecyclerView.setAdapter(adapter);
    }

    public HeaderAndFooterRecyclerViewAdapter getAdapter() {
        return (HeaderAndFooterRecyclerViewAdapter) mRecyclerView.getAdapter();
    }

    private void onLoadMore() {
        setFooterState(STATE_LOADING);
        if (mOnLoadMoreListener != null) {
            mOnLoadMoreListener.onLoadMore();
        }
    }

    public void completeLoading(boolean isHaveMore) {
        if (isHaveMore) {
            setFooterState(STATE_NORMAL);
        }else {
            setFooterState(STATE_NO_MORE);
        }
        checkShowEmptyView();
    }

    public void setFooterState(int state) {
        if (mIsLoadMoreEnable && mLoadMoreView != null) {
            mLoadMoreView.setState(state);
        }
    }

    public void setLoadMoreEnable(boolean enable) {
        this.mIsLoadMoreEnable = enable;
        if (mRecyclerView.getAdapter() != null) {
            HeaderAndFooterRecyclerViewAdapter headerAndFooterRecyclerViewAdapter = (HeaderAndFooterRecyclerViewAdapter) mRecyclerView.getAdapter();
            if (enable) {
                headerAndFooterRecyclerViewAdapter.removeFooterView(mLoadMoreView);
                headerAndFooterRecyclerViewAdapter.addFooterView(mLoadMoreView);
                mLoadMoreView.setState(STATE_NORMAL);
            }else {
                headerAndFooterRecyclerViewAdapter.removeFooterView(mLoadMoreView);
            }
        }
    }

    public void setEmptyView(View emptyView) {
         if (mEmptyView != null) {
            removeView(mEmptyView);
         }
        addView(emptyView);
        checkShowEmptyView();
        this.mEmptyView = emptyView;
    }

    public View getEmptyView() {
        return mEmptyView;
    }

    private void checkShowEmptyView() {
        HeaderAndFooterRecyclerViewAdapter adapter = (HeaderAndFooterRecyclerViewAdapter) mRecyclerView.getAdapter();
        if (mEmptyView != null
                &&  adapter != null && adapter.getInnerAdapter() != null) {
            if (adapter.getInnerAdapter().getItemCount() == 0) {
                mEmptyView.setVisibility(VISIBLE);
                setFooterState(STATE_EMPTY);
            }else {
                mEmptyView.setVisibility(GONE);
            }
        }
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    private RecyclerView.OnScrollListener mOpOnScrollChangeListener = new RecyclerView.OnScrollListener(){
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
            final int totalItemCount = layoutManager.getItemCount();
            int lastVisibleItemPosition = 0;

            if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
            }else if (layoutManager instanceof StaggeredGridLayoutManager) {
                StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                int[] into = new int[staggeredGridLayoutManager.getSpanCount()];
                ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(into);
                lastVisibleItemPosition = findMax(into);
            }

            if (lastVisibleItemPosition >= totalItemCount - 1 && mIsLoadMoreEnable
                    && mLoadMoreView.getState() == STATE_NORMAL) {
                onLoadMore();
            }
        }
    };

    private static int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    private RecyclerView.AdapterDataObserver mDataObserver = new RecyclerView.AdapterDataObserver() {

        @Override
        public void onChanged() {
            super.onChanged();
            checkShowEmptyView();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            checkShowEmptyView();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            checkShowEmptyView();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            checkShowEmptyView();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            checkShowEmptyView();
        }
    };

    public RecyclerView.LayoutManager getLayoutManager() {
        return mRecyclerView.getLayoutManager();
    }

    public void setLayoutManager(RecyclerView.LayoutManager recyclerLayoutManager) {
        mRecyclerView.setLayoutManager(recyclerLayoutManager);
    }

    public void smoothScrollToPosition(int position) {
        mRecyclerView.smoothScrollToPosition(position);
    }

    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        mRecyclerView.addItemDecoration(itemDecoration);
    }


    public static interface OnLoadMoreListener {
        public void onLoadMore();
    }

}
