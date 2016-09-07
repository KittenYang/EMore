package com.caij.emore.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.caij.emore.Event;
import com.caij.emore.R;
import com.caij.emore.account.Token;
import com.caij.emore.account.UserPrefs;
import com.caij.emore.bean.MessageUser;
import com.caij.emore.database.bean.UnReadMessage;
import com.caij.emore.present.MessageUserPresent;
import com.caij.emore.present.imp.MessageUserPresentImp;
import com.caij.emore.ui.view.MessageUserView;
import com.caij.emore.source.local.LocalMessageSource;
import com.caij.emore.source.server.ServerMessageSource;
import com.caij.emore.ui.activity.CommentsActivity;
import com.caij.emore.ui.activity.DefaultFragmentActivity;
import com.caij.emore.ui.activity.MentionActivity;
import com.caij.emore.ui.adapter.MessageUserAdapter;
import com.caij.emore.utils.DensityUtil;
import com.caij.emore.utils.DialogUtil;
import com.caij.emore.utils.LogUtil;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.utils.weibo.WeicoAuthUtil;
import com.caij.emore.widget.recyclerview.BaseAdapter;
import com.caij.emore.widget.recyclerview.BaseViewHolder;
import com.caij.emore.widget.recyclerview.RecyclerViewOnItemLongClickListener;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.List;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by Caij on 2016/7/4.
 */
public class MessageUserFragment extends SwipeRefreshRecyclerViewFragment<MessageUser.UserListBean, MessageUserPresent> implements MessageUserView, RecyclerViewOnItemLongClickListener {

    private TextView tvMentionCount;
    private TextView tvCommentCount;
    private TextView tvAttitudeCount;

    private Observable<UnReadMessage> mUnReadMessageObservable;
    private boolean isHasNewDm;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initEvent();

        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                mPresent.refresh();
            }
        });
    }

    private void initView() {
        mSwipeRefreshLayout.setBackgroundColor(getResources().getColor(R.color.white));
        xRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).
                color(getResources().getColor(R.color.divider_timeline_item)).
                margin(DensityUtil.dip2px(getActivity(), 72), 0).
                size(DensityUtil.dip2px(getActivity(), 0.5f)).
                build());

        mRecyclerViewAdapter.setOnItemLongClickListener(this);

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        final View mentionView = layoutInflater.inflate(R.layout.item_message_head, xRecyclerView, false);
        View commentView = layoutInflater.inflate(R.layout.item_message_head, xRecyclerView, false);
        View priseView = layoutInflater.inflate(R.layout.item_message_head, xRecyclerView, false);

        tvMentionCount = (TextView) mentionView.findViewById(R.id.tv_unread_count);
        tvCommentCount = (TextView) commentView.findViewById(R.id.tv_unread_count);
        tvAttitudeCount = (TextView) priseView.findViewById(R.id.tv_unread_count);

        setValue(mentionView, getString(R.string.mention), R.mipmap.messagescenter_at);
        setValue(commentView, getString(R.string.comment), R.mipmap.messagescenter_comments);
        setValue(priseView, getString(R.string.attitude), R.mipmap.messagescenter_good);

        xRecyclerView.getAdapter().addHeaderView(mentionView);
        xRecyclerView.getAdapter().addHeaderView(commentView);
        xRecyclerView.getAdapter().addHeaderView(priseView);

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
                Intent intent = DefaultFragmentActivity.starFragmentV4(getActivity(), getString(R.string.attitude), AttitudesToMeFragment.class, null);
                startActivity(intent);
            }
        });
    }

    private void initEvent() {
        mUnReadMessageObservable = RxBus.getDefault().register(Event.EVENT_HAS_NEW_DM);
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
    }

    @Override
    protected BaseAdapter<MessageUser.UserListBean, ? extends BaseViewHolder> createRecyclerViewAdapter() {
        return new MessageUserAdapter(getActivity());
    }

    @Override
    protected MessageUserPresent createPresent() {
        if (WeicoAuthUtil.checkWeicoLogin(this, false)) {
            Token accessToken = UserPrefs.get(getActivity()).getWeiCoToken();
            return new MessageUserPresentImp(accessToken.getAccess_token(), Long.parseLong(accessToken.getUid()),
                    new ServerMessageSource(), new LocalMessageSource(), this);
        }
        return null;
    }

    @Override
    protected View createEmptyView() {
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
        Intent intent = DefaultFragmentActivity.starFragmentV4(getActivity(), bean.getUser().getScreen_name(), ChatFragment.class,
                ChatFragment.newInstance(bean.getUser().getScreen_name(), bean.getUser().getId()).getArguments());
        startActivity(intent);
        xRecyclerView.getAdapter().notifyItemChanged(position);
    }


    @Override
    public boolean onItemLongClick(View view, int position) {
        final MessageUser.UserListBean bean = mRecyclerViewAdapter.getItem(position - 3);
        DialogUtil.showItemDialog(getActivity(), null, new String[]{"清除会话"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPresent.deleteMessageConversation(bean.getUser().getId());
            }
        });
        return true;
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
    public void notifyItemRemove(List<MessageUser.UserListBean> mUserListBeens, int position) {
        mRecyclerViewAdapter.setEntities(mUserListBeens);
        mRecyclerViewAdapter.notifyItemRemoved(position);
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
        RxBus.getDefault().unregister(Event.EVENT_HAS_NEW_DM, mUnReadMessageObservable);
    }

}
