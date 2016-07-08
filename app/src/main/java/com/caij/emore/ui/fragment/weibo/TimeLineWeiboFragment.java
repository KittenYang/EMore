package com.caij.emore.ui.fragment.weibo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;

import com.caij.emore.R;
import com.caij.emore.UserPrefs;
import com.caij.emore.bean.Weibo;
import com.caij.emore.present.TimeLinePresent;
import com.caij.emore.present.view.TimeLineWeiboView;
import com.caij.emore.ui.activity.WeiboDetialActivity;
import com.caij.emore.ui.adapter.WeiboAdapter;
import com.caij.emore.ui.fragment.RecyclerViewFragment;
import com.caij.emore.utils.DialogUtil;
import com.caij.emore.view.recyclerview.BaseAdapter;
import com.caij.emore.view.recyclerview.BaseViewHolder;
import com.caij.emore.view.recyclerview.LoadMoreRecyclerView;
import com.caij.emore.view.recyclerview.RecyclerViewOnItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Caij on 2016/6/4.
 */
public abstract class TimeLineWeiboFragment<P extends TimeLinePresent> extends RecyclerViewFragment<Weibo, P>
        implements TimeLineWeiboView, RecyclerViewOnItemClickListener, LoadMoreRecyclerView.OnLoadMoreListener {

    @Override
    protected BaseAdapter<Weibo, ? extends BaseViewHolder> createRecyclerViewAdapter() {
        return new WeiboAdapter(getActivity());
    }

    @Override
    public void onItemClick(View view, int position) {
        if (view.getId() == R.id.btn_menus) {
            onMenuClick(mRecyclerViewAdapter.getItem(position), position);
        }else {
            Intent intent = WeiboDetialActivity.newIntent(getActivity(), mRecyclerViewAdapter.getItem(position));
            startActivity(intent);
        }
    }

    @Override
    public void onLoadComplete(boolean isHaveMore) {
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

    private void onMenuClick(final Weibo weibo, final int position) {
        List<String> items = new ArrayList<>();
        if (weibo.isFavorited()) {
            items.add("取消收藏");
        }else {
            items.add("收藏");
        }
        long uid = Long.parseLong(UserPrefs.get().getEMoreToken().getUid());
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
                        deleteWeibo(weibo, position);
                        break;
                }
            }
        });
    }

    private void deleteWeibo(Weibo weibo, int position) {
        mPresent.deleteWeibo(weibo, position);
    }

    private void collectWeibo(Weibo weibo) {
        mPresent.collectWeibo(weibo);
    }

    private void uncollectWeibo(Weibo weibo) {
        mPresent.uncollectWeibo(weibo);
    }

    @Override
    public void onDeleteWeiboSuccess(Weibo weibo, int position) {
        mRecyclerViewAdapter.removeEntity(weibo);
        mRecyclerViewAdapter.notifyItemRemoved(position);
    }

    @Override
    public void onCollectSuccess(Weibo weibo) {
        weibo.setFavorited(true);
    }

    @Override
    public void onUncollectSuccess(Weibo weibo) {
        weibo.setFavorited(false);
    }

}
