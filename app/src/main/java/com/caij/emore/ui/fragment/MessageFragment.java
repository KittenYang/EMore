package com.caij.emore.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.caij.emore.R;
import com.caij.emore.present.ListPresent;
import com.caij.emore.ui.activity.CommentsActivity;
import com.caij.emore.ui.activity.MentionActivity;
import com.caij.emore.ui.adapter.WeiboAdapter;
import com.caij.emore.utils.DensityUtil;
import com.caij.emore.view.recyclerview.BaseAdapter;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

/**
 * Created by Caij on 2016/7/4.
 */
public class MessageFragment extends SwipeRefreshRecyclerViewFragment {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSwipeRefreshLayout.setBackgroundColor(getResources().getColor(R.color.white));
        mLoadMoreLoadMoreRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        WeiboAdapter weiboAdapter = new WeiboAdapter(getActivity());
        mLoadMoreLoadMoreRecyclerView.setAdapter(weiboAdapter);
        mLoadMoreLoadMoreRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).
                color(getResources().getColor(R.color.divider_timeline_item)).
                margin(DensityUtil.dip2px(getActivity(), 72), 0).
                size(DensityUtil.dip2px(getActivity(), 0.5f)).
                build());

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View mentionView = layoutInflater.inflate(R.layout.item_message_head, mLoadMoreLoadMoreRecyclerView, false);
        View commentView = layoutInflater.inflate(R.layout.item_message_head, mLoadMoreLoadMoreRecyclerView, false);
        View priseView = layoutInflater.inflate(R.layout.item_message_head, mLoadMoreLoadMoreRecyclerView, false);

//        setValue(mentionView, "@我的", R.mipmap.messagescenter_at);
//        setValue(commentView, "评论", R.mipmap.messagescenter_comments);
//        setValue(priseView, "赞", R.mipmap.messagescenter_good);

        setValue(mentionView, "@我的", R.mipmap.ic_comment);
        setValue(commentView, "评论", R.mipmap.ic_comment);
        setValue(priseView, "赞", R.mipmap.ic_comment);

        mLoadMoreLoadMoreRecyclerView.getAdapter().addHeaderView(mentionView);
        mLoadMoreLoadMoreRecyclerView.getAdapter().addHeaderView(commentView);
        mLoadMoreLoadMoreRecyclerView.getAdapter().addHeaderView(priseView);

        weiboAdapter.notifyDataSetChanged();

        mentionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MentionActivity.class);
                startActivity(intent);
            }
        });
        commentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CommentsActivity.class);
                startActivity(intent);
            }
        });
        priseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected BaseAdapter createRecyclerViewAdapter() {
        return null;
    }

    @Override
    protected ListPresent createPresent() {
        return null;
    }

    private void setValue(View view, String title, int drawable) {
        TextView tvTitle  = (TextView) view.findViewById(R.id.tv_title);
        ImageView ivIcon  = (ImageView) view.findViewById(R.id.iv_icon);
        tvTitle.setText(title);
        ivIcon.setImageResource(drawable);
    }

    @Override
    protected void onUserFirstVisible() {

    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onEmpty() {

    }

    @Override
    public void onItemClick(View view, int position) {

    }
}
