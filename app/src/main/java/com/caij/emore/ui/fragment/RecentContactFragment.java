package com.caij.emore.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.caij.emore.EventTag;
import com.caij.emore.R;
import com.caij.emore.account.Token;
import com.caij.emore.account.UserPrefs;
import com.caij.emore.bean.MessageUser;
import com.caij.emore.database.bean.UnReadMessage;
import com.caij.emore.manager.imp.NotifyManagerImp;
import com.caij.emore.present.MessageUserPresent;
import com.caij.emore.present.imp.MessageUserPresentImp;
import com.caij.emore.remote.imp.MessageApiImp;
import com.caij.emore.ui.adapter.delegate.RecentContactDelegate;
import com.caij.emore.ui.fragment.mention.StatusMentionFragment;
import com.caij.emore.ui.view.MessageUserView;
import com.caij.emore.ui.activity.CommentsActivity;
import com.caij.emore.ui.activity.DefaultFragmentActivity;
import com.caij.emore.ui.activity.MentionActivity;
import com.caij.emore.utils.DensityUtil;
import com.caij.emore.utils.DialogUtil;
import com.caij.emore.utils.LogUtil;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.utils.weibo.WeicoAuthUtil;
import com.caij.rvadapter.BaseViewHolder;
import com.caij.rvadapter.RecyclerViewOnItemLongClickListener;
import com.caij.rvadapter.adapter.BaseAdapter;
import com.caij.rvadapter.adapter.MultiItemTypeAdapter;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.List;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by Caij on 2016/7/4.
 */
public class RecentContactFragment extends SwipeRefreshRecyclerViewFragment<MessageUser.UserListBean, MessageUserPresent> implements MessageUserView, RecyclerViewOnItemLongClickListener {

    private TextView tvMentionCount;
    private TextView tvCommentCount;
    private TextView tvAttitudeCount;
    private TextView tvStrangeMessageCount;

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
        mSwipeRefreshLayout.setBackgroundColor(getResources().getColor(R.color.ui_background));
        xRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).
                color(getResources().getColor(R.color.divider_timeline_item)).
                margin(DensityUtil.dip2px(getActivity(), 72), 0).
                size(DensityUtil.dip2px(getActivity(), 0.5f)).
                build());

        mRecyclerViewAdapter.setOnItemLongClickListener(this);

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View mentionView = layoutInflater.inflate(R.layout.item_message_head, xRecyclerView, false);
        View commentView = layoutInflater.inflate(R.layout.item_message_head, xRecyclerView, false);
        View priseView = layoutInflater.inflate(R.layout.item_message_head, xRecyclerView, false);
        View strangerDmView = layoutInflater.inflate(R.layout.item_message_head, xRecyclerView, false);

        tvMentionCount = (TextView) mentionView.findViewById(R.id.tv_unread_count);
        tvCommentCount = (TextView) commentView.findViewById(R.id.tv_unread_count);
        tvAttitudeCount = (TextView) priseView.findViewById(R.id.tv_unread_count);
        tvStrangeMessageCount = (TextView) strangerDmView.findViewById(R.id.tv_unread_count);

        setValue(mentionView, getString(R.string.mention), R.mipmap.messagescenter_at);
        setValue(commentView, getString(R.string.comment), R.mipmap.messagescenter_comments);
        setValue(priseView, getString(R.string.attitude), R.mipmap.messagescenter_good);
        setValue(strangerDmView, getString(R.string.stranger_dm), R.mipmap.messagescenter_messagebox);

        xRecyclerView.getAdapter().addHeaderView(mentionView);
        xRecyclerView.getAdapter().addHeaderView(commentView);
        xRecyclerView.getAdapter().addHeaderView(priseView);
        xRecyclerView.getAdapter().addHeaderView(strangerDmView);

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
        strangerDmView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = DefaultFragmentActivity.starFragmentV4(getActivity(),  getString(R.string.stranger_dm), StrangerMessageFragment.class, null);
                startActivity(intent);
            }
        });
    }

    private void initEvent() {
        mUnReadMessageObservable = RxBus.getDefault().register(EventTag.EVENT_HAS_NEW_DM);
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
        MultiItemTypeAdapter<MessageUser.UserListBean> multiItemTypeAdapter = new MultiItemTypeAdapter<MessageUser.UserListBean>(getActivity());
        multiItemTypeAdapter.addItemViewDelegate(new RecentContactDelegate(null));
        return multiItemTypeAdapter;
    }

    @Override
    protected MessageUserPresent createPresent() {
        if (WeicoAuthUtil.checkWeicoLogin(this, false)) {
            Token accessToken = UserPrefs.get(getActivity()).getToken();
            return new MessageUserPresentImp(Long.parseLong(accessToken.getUid()), new MessageApiImp(),
                    new NotifyManagerImp(), this);
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
        MessageUser.UserListBean bean = mRecyclerViewAdapter.getItem(position - xRecyclerView.getAdapter().getHeaderViewsCount());
        bean.setUnread_count(0);
        Intent intent = DefaultFragmentActivity.starFragmentV4(getActivity(), bean.getUser().getScreen_name(), ChatFragment.class,
                ChatFragment.newInstance(bean.getUser().getId()).getArguments());
        startActivity(intent);
        xRecyclerView.getAdapter().notifyItemChanged(position);
    }


    @Override
    public boolean onItemLongClick(View view, int position) {
        final MessageUser.UserListBean bean = mRecyclerViewAdapter.getItem(position - xRecyclerView.getAdapter().getHeaderViewsCount());
        DialogUtil.showItemDialog(getActivity(), null, new String[]{getString(R.string.clear_conversion)}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPresent.deleteMessageConversation(bean);
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

            if (unReadMessage.getMsgbox() > 0) {
                tvStrangeMessageCount.setVisibility(View.VISIBLE);
                tvStrangeMessageCount.setText(String.valueOf(unReadMessage.getMsgbox()));
            }else {
                tvStrangeMessageCount.setVisibility(View.GONE);
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
        RxBus.getDefault().unregister(EventTag.EVENT_HAS_NEW_DM, mUnReadMessageObservable);
    }

}
