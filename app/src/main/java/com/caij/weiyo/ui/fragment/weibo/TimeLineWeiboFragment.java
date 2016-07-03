package com.caij.weiyo.ui.fragment.weibo;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.caij.weiyo.R;
import com.caij.weiyo.UserPrefs;
import com.caij.weiyo.bean.Weibo;
import com.caij.weiyo.present.TimeLinePresent;
import com.caij.weiyo.present.imp.UserWeiboPresentImp;
import com.caij.weiyo.present.view.TimeLineWeiboView;
import com.caij.weiyo.ui.activity.WeiboDetialActivity;
import com.caij.weiyo.ui.adapter.WeiboAdapter;
import com.caij.weiyo.ui.fragment.RecyclerViewFragment;
import com.caij.weiyo.ui.fragment.SwipeRefreshRecyclerViewFragment;
import com.caij.weiyo.utils.DialogUtil;
import com.caij.weiyo.view.recyclerview.LoadMoreRecyclerView;
import com.caij.weiyo.view.recyclerview.RecyclerViewOnItemClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Caij on 2016/6/4.
 */
public abstract class TimeLineWeiboFragment<P extends TimeLinePresent> extends RecyclerViewFragment
        implements TimeLineWeiboView, RecyclerViewOnItemClickListener, LoadMoreRecyclerView.OnLoadMoreListener {

    P mTimeLineWeiboPresent;
    WeiboAdapter mAdapter;

    private Dialog mLoadDialog;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new WeiboAdapter(getActivity(), this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Weibo weibo = (Weibo) v.getTag();
                onMenuClick(weibo);
            }
        });
        mLoadMoreLoadMoreRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mLoadMoreLoadMoreRecyclerView.setAdapter(mAdapter);
        mLoadMoreLoadMoreRecyclerView.setOnLoadMoreListener(this);
        mTimeLineWeiboPresent = createPresent();
        if (mTimeLineWeiboPresent != null) {
            mTimeLineWeiboPresent.onCreate();
        }
    }

    protected abstract P createPresent();

    @Override
    public void onLoadMore() {
        mTimeLineWeiboPresent.onLoadMore();
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = WeiboDetialActivity.newIntent(getActivity(), mAdapter.getItem(position));
        startActivity(intent);
    }

    @Override
    public void setWeibos(List<Weibo> weibos) {
        mAdapter.setEntities(weibos);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void toRefresh() {
        mTimeLineWeiboPresent.onRefresh();
    }

    @Override
    public void onRefreshComplite() {
    }

    @Override
    public void onLoadComplite(boolean isHaveMore) {
        if (isHaveMore) {
            mLoadMoreLoadMoreRecyclerView.completeLoading();
        }else {
            mLoadMoreLoadMoreRecyclerView.setFooterState(LoadMoreRecyclerView.STATE_NO_MORE);
        }
    }

    @Override
    public Context getContent() {
        return getActivity().getApplication();
    }

    private void onMenuClick(final Weibo weibo) {
        List<String> items = new ArrayList<>();
        if (weibo.isFavorited()) {
            items.add("取消收藏");
        }else {
            items.add("收藏");
        }
        long uid = Long.parseLong(UserPrefs.get().getWeiYoToken().getUid());
        if (weibo.getUser().getId() == uid) {
            items.add("删除");
        }
        String[] array = new String[items.size()];
        DialogUtil.showItemDialog(getActivity(), null, items.toArray(array), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        if (weibo.isFavorited()) {
                            uncollectWeibo(weibo);
                        }else {
                            collectWeibo(weibo);
                        }
                        break;

                    case 1:
                        deleteWeibo(weibo);
                        break;
                }
            }
        });
    }

    private void deleteWeibo(Weibo weibo) {
        mTimeLineWeiboPresent.deleteWeibo(weibo);
    }

    private void collectWeibo(Weibo weibo) {
        mTimeLineWeiboPresent.collectWeibo(weibo);
    }

    private void uncollectWeibo(Weibo weibo) {
        mTimeLineWeiboPresent.uncollectWeibo(weibo);
    }

    @Override
    public void showDialogLoging(boolean isShow) {
        if (isShow) {
            if (mLoadDialog == null) {
                mLoadDialog = DialogUtil.showProgressDialog(getActivity(), null, getString(R.string.requesting));
            }else {
                mLoadDialog.show();
            }
        }else {
            if (mLoadDialog != null) {
                mLoadDialog.dismiss();
            }
        }

    }

    @Override
    public void onDeleteWeiboSuccess(Weibo weibo) {
        mAdapter.removeEntity(weibo);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCollectSuccess(Weibo weibo) {
        weibo.setFavorited(true);
    }

    @Override
    public void onUncollectSuccess(Weibo weibo) {
        weibo.setFavorited(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimeLineWeiboPresent.onDestroy();
    }
}
