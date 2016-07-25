package com.caij.emore.ui.fragment;

import android.app.Activity;
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
import com.caij.emore.database.bean.UnReadMessage;
import com.caij.emore.present.MessageUserPresent;
import com.caij.emore.present.imp.MessageUserPresentImp;
import com.caij.emore.present.view.MessageUserView;
import com.caij.emore.source.local.LocalMessageSource;
import com.caij.emore.source.server.ServerMessageSource;
import com.caij.emore.ui.activity.CommentsActivity;
import com.caij.emore.ui.activity.DefaultFragmentActivity;
import com.caij.emore.ui.activity.MentionActivity;
import com.caij.emore.ui.adapter.MessageUserAdapter;
import com.caij.emore.utils.DensityUtil;
import com.caij.emore.utils.LogUtil;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.utils.weibo.WeicoAuthUtil;
import com.caij.emore.view.recyclerview.BaseAdapter;
import com.caij.emore.view.recyclerview.BaseViewHolder;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by Caij on 2016/7/4.
 */
public class MessageUserFragment extends SwipeRefreshRecyclerViewFragment<MessageUser.UserListBean, MessageUserPresent> implements MessageUserView {

    private TextView tvMentionCount;
    private TextView tvCommentCount;
    private TextView tvAttitudeCount;

    private boolean isHasNewDm;
    private Observable<UnReadMessage> mUnReadMessageObservable;

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
        final View mentionView = layoutInflater.inflate(R.layout.item_message_head, mLoadMoreLoadMoreRecyclerView, false);
        View commentView = layoutInflater.inflate(R.layout.item_message_head, mLoadMoreLoadMoreRecyclerView, false);
        View priseView = layoutInflater.inflate(R.layout.item_message_head, mLoadMoreLoadMoreRecyclerView, false);

        tvMentionCount = (TextView) mentionView.findViewById(R.id.tv_unread_count);
        tvCommentCount = (TextView) commentView.findViewById(R.id.tv_unread_count);
        tvAttitudeCount = (TextView) priseView.findViewById(R.id.tv_unread_count);

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
                Intent intent = DefaultFragmentActivity.starFragmentV4(getActivity(), AttitudesToMeFragment.class, null);
                startActivity(intent);
            }
        });

        mUnReadMessageObservable = RxBus.get().register(Key.EVENT_HAS_NEW_DM);
        mUnReadMessageObservable.subscribe(new Action1<UnReadMessage>() {
            @Override
            public void call(UnReadMessage o) {
                isHasNewDm = true;
                if (isVisible()) {
                    mSwipeRefreshLayout.setRefreshing(true);
                    mPresent.refresh();
                    isHasNewDm = false;
                }
            }
        });

        if (mPresent != null) {
            mSwipeRefreshLayout.setRefreshing(true);
            mPresent.refresh();
        }
    }

    @Override
    protected BaseAdapter<MessageUser.UserListBean, ? extends BaseViewHolder> createRecyclerViewAdapter() {
        return new MessageUserAdapter(getActivity());
    }

    @Override
    protected MessageUserPresent createPresent() {
        if (WeicoAuthUtil.checkWeicoLogin(this, false)) {
            AccessToken accessToken = UserPrefs.get().getWeiCoToken();
            return new MessageUserPresentImp(accessToken.getAccess_token(), new ServerMessageSource(),
                    new LocalMessageSource(), this);
        }
        return null;
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
        bean.setUnread_count(0);
        Intent intent = DefaultFragmentActivity.starFragmentV4(getActivity(), ChatFragment.class,
                ChatFragment.newInstance(bean.getUser().getScreen_name(), bean.getUser().getId()).getArguments());
        startActivity(intent);
        mLoadMoreLoadMoreRecyclerView.getAdapter().notifyItemChanged(position);
    }

    @Override
    public void onEmpty() {

    }

    @Override
    public void onLoadUnReadMessageSuccess(UnReadMessage unReadMessage) {
        if (unReadMessage != null) {
            int mentionCount  = unReadMessage.getMention_cmt() + unReadMessage.getMention_status();
            if (mentionCount > 0) {
                tvMentionCount.setVisibility(View.VISIBLE);
                tvMentionCount.setText(String.valueOf(mentionCount));
            }else {
                tvMentionCount.setVisibility(View.GONE);
            }

            if (unReadMessage.getCmt() > 0) {
                tvCommentCount.setVisibility(View.VISIBLE);
                tvCommentCount.setText(String.valueOf(unReadMessage.getCmt()));
            }else {
                tvCommentCount.setVisibility(View.GONE);
            }

            if (unReadMessage.getAttitude() > 0) {
                tvAttitudeCount.setVisibility(View.VISIBLE);
                tvAttitudeCount.setText(String.valueOf(unReadMessage.getAttitude()));
            }else {
                tvAttitudeCount.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (isHasNewDm && !hidden) {
            LogUtil.d(this, "onHiddenChanged need refresh");
            mSwipeRefreshLayout.setRefreshing(true);
            mPresent.refresh();
            isHasNewDm = false;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.get().unregister(Key.EVENT_HAS_NEW_DM, mUnReadMessageObservable);
    }
}
