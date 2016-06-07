package com.caij.weiyo.view.recyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caij.weiyo.R;

/**
 * Author:  Caij
 * Email:   worldcaij@gmail.com
 * Date:    2015/11/21
 * Description:
 */
public class LoadMoreView extends RelativeLayout{

    private int mState;
    private ProgressBar mLoading;
    private TextView mHint;
    private View mLoadingContent;

    public LoadMoreView(Context context) {
        super(context);
        init(context);
    }

    public LoadMoreView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LoadMoreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.item_foot_loading, this);
        mLoading = (ProgressBar) findViewById(R.id.loading);
        mHint = (TextView) findViewById(R.id.hint);
        mLoadingContent = findViewById(R.id.ll_loading_container);
    }

    public void setState(int state) {
        this.mState = state;
        switch (state) {
            case LoadMoreRecyclerView.STATE_NORMAL:
                mLoadingContent.setVisibility(VISIBLE);
                mLoading.setVisibility(View.GONE);
                mHint.setVisibility(VISIBLE);
                mHint.setText(getContext().getString(R.string.pull_loading));
                break;
            case LoadMoreRecyclerView.STATE_LOADING:
                mLoadingContent.setVisibility(VISIBLE);
                mLoading.setVisibility(View.VISIBLE);
                mHint.setVisibility(VISIBLE);
                mHint.setText(getContext().getString(R.string.loading));
                break;
            case LoadMoreRecyclerView.STATE_NO_MORE:
                mHint.setVisibility(View.GONE);
                mLoading.setVisibility(View.GONE);
                mLoadingContent.setVisibility(GONE);
                break;
        }
    }

    public int getState() {
        return mState;
    }

}
