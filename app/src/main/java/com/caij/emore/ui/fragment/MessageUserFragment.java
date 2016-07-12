package com.caij.emore.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.UserPrefs;
import com.caij.emore.bean.AccessToken;
import com.caij.emore.bean.MessageUser;
import com.caij.emore.present.MessageUserPresent;
import com.caij.emore.present.imp.MessageUserPresentImp;
import com.caij.emore.source.server.ServerMessageSource;
import com.caij.emore.ui.activity.CommentsActivity;
import com.caij.emore.ui.activity.DefaultFragmentActivity;
import com.caij.emore.ui.activity.MentionActivity;
import com.caij.emore.ui.adapter.MessageUserAdapter;
import com.caij.emore.utils.DensityUtil;
import com.caij.emore.view.recyclerview.BaseAdapter;
import com.caij.emore.view.recyclerview.BaseViewHolder;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

/**
 * Created by Caij on 2016/7/4.
 */
public class MessageUserFragment extends SwipeRefreshRecyclerViewFragment<MessageUser.UserListBean, MessageUserPresent> {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSwipeRefreshLayout.setBackgroundColor(getResources().getColor(R.color.white));
        mLoadMoreLoadMoreRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).
                color(getResources().getColor(R.color.divider_timeline_item)).
                margin(DensityUtil.dip2px(getActivity(), 72), 0).
                size(DensityUtil.dip2px(getActivity(), 0.5f)).
                build());

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View mentionView = layoutInflater.inflate(R.layout.item_message_head, mLoadMoreLoadMoreRecyclerView, false);
        View commentView = layoutInflater.inflate(R.layout.item_message_head, mLoadMoreLoadMoreRecyclerView, false);
        View priseView = layoutInflater.inflate(R.layout.item_message_head, mLoadMoreLoadMoreRecyclerView, false);

        setValue(mentionView, "@我的", R.mipmap.messagescenter_at);
        setValue(commentView, "评论", R.mipmap.messagescenter_comments);
        setValue(priseView, "赞", R.mipmap.messagescenter_good);

        mLoadMoreLoadMoreRecyclerView.getAdapter().addHeaderView(mentionView);
        mLoadMoreLoadMoreRecyclerView.getAdapter().addHeaderView(commentView);
        mLoadMoreLoadMoreRecyclerView.getAdapter().addHeaderView(priseView);

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

        mSwipeRefreshLayout.setRefreshing(true);
        mPresent.refresh();
    }

    @Override
    protected BaseAdapter<MessageUser.UserListBean, ? extends BaseViewHolder> createRecyclerViewAdapter() {
        return new MessageUserAdapter(getActivity());
    }

    @Override
    protected MessageUserPresent createPresent() {
        AccessToken accessToken = UserPrefs.get().getWeiCoToken();
        return new MessageUserPresentImp(accessToken.getAccess_token(), new ServerMessageSource(),
                null, this);
    }

    private void setValue(View view, String title, int drawable) {
        TextView tvTitle  = (TextView) view.findViewById(R.id.tv_title);
        ImageView ivIcon  = (ImageView) view.findViewById(R.id.iv_icon);
        tvTitle.setText(title);
        ivIcon.setImageResource(drawable);
    }

    @Override
    public void onItemClick(View view, int position) {
        MessageUser.UserListBean bean = mRecyclerViewAdapter.getItem(position - 3);
        Intent intent = DefaultFragmentActivity.starFragmentV4(getActivity(), ChatFragment.class,
                ChatFragment.newInstance(bean.getUser().getScreen_name(), bean.getUser().getId()).getArguments());
        startActivity(intent);
    }

    @Override
    public void onEmpty() {

    }
}
