package com.caij.emore.ui.fragment;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.caij.emore.EventTag;
import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.account.Token;
import com.caij.emore.account.UserPrefs;
import com.caij.emore.bean.Emotion;
import com.caij.emore.bean.ImageInfo;
import com.caij.emore.manager.imp.MessageManagerImp;
import com.caij.emore.manager.imp.NotifyManagerImp;
import com.caij.emore.manager.imp.UserManagerImp;
import com.caij.emore.database.bean.DirectMessage;
import com.caij.emore.present.ChatPresent;
import com.caij.emore.present.imp.ChatPresentImp;
import com.caij.emore.remote.imp.MessageApiImp;
import com.caij.emore.ui.activity.UserInfoActivity;
import com.caij.emore.ui.adapter.delegate.MessageDelegateProvider;
import com.caij.emore.ui.view.DirectMessageView;
import com.caij.emore.ui.activity.DefaultFragmentActivity;
import com.caij.emore.ui.activity.ImagePrewActivity;
import com.caij.emore.utils.DialogUtil;
import com.caij.emore.utils.DrawableUtil;
import com.caij.emore.utils.NavigationUtil;
import com.caij.emore.utils.SystemUtil;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.widget.recyclerview.HeaderAndFooterRecyclerViewAdapter;
import com.caij.emore.widget.recyclerview.OnItemPartViewClickListener;
import com.caij.emore.widget.recyclerview.XRecyclerView;
import com.caij.emore.widget.recyclerview.LoadMoreView;
import com.caij.rvadapter.RecyclerViewOnItemClickListener;
import com.caij.rvadapter.RecyclerViewOnItemLongClickListener;
import com.caij.rvadapter.adapter.MultiItemTypeAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.functions.Action1;

import static android.support.v7.widget.RecyclerView.*;

/**
 * Created by Caij on 2016/7/10.
 */
public class ChatFragment extends BaseFragment<ChatPresent> implements
        DefaultFragmentActivity.OnBackPressedListener, DirectMessageView,
        TextWatcher, RecyclerViewOnItemClickListener, OnItemPartViewClickListener,
        RecyclerViewOnItemLongClickListener, OnTouchListener {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.iv_emotion)
    ImageView ivEmotion;
    @BindView(R.id.iv_add)
    ImageView ivAdd;
    @BindView(R.id.tv_send)
    TextView tvSend;
    @BindView(R.id.fl_emotion)
    FrameLayout flEmotion;
    @BindView(R.id.et_content)
    EditText etContent;

    private MultiItemTypeAdapter<DirectMessage> mMessageAdapter;
    private LoadMoreView mLoadMoreView;
    private LinearLayoutManager mLinearLayoutManager;
    private HeaderAndFooterRecyclerViewAdapter headerAndFooterRecyclerViewAdapter;

    private Observable<Emotion> mEmotionObservable;
    private Observable<Object> mEmotionDeleteObservable;

    public static ChatFragment newInstance(String name, long uid) {
        Bundle args = new Bundle();
        args.putLong(Key.ID, uid);
        args.putString(Key.USERNAME, name);
        ChatFragment fragment = new ChatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initIconImage();
        getChildFragmentManager().beginTransaction().
                replace(R.id.fl_emotion, new EmotionFragment()).commit();

        mEmotionObservable = RxBus.getDefault().register(EventTag.ON_EMOTION_CLICK);
        mEmotionDeleteObservable = RxBus.getDefault().register(EventTag.ON_EMOTION_DELETE_CLICK);
        mEmotionObservable.subscribe(new Action1<Emotion>() {
            @Override
            public void call(Emotion emotion) {
                onEmotionClick(emotion);
            }
        });
        mEmotionDeleteObservable.subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                onEmotionDeleteClick();
            }
        });
    }

    private void initView() {
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLinearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageAdapter = new MultiItemTypeAdapter<DirectMessage>(getActivity());
        mMessageAdapter.setOnItemClickListener(this);
        mMessageAdapter.addItemViewDelegate(new MessageDelegateProvider.OutImageMessageDelegate(this));
        mMessageAdapter.addItemViewDelegate(new MessageDelegateProvider.OutTextMessageDelegate(this));
        mMessageAdapter.addItemViewDelegate(new MessageDelegateProvider.ReceiveImageMessageDelegate(this));
        mMessageAdapter.addItemViewDelegate(new MessageDelegateProvider.ReceiveTextMessageDelegate(this));
        headerAndFooterRecyclerViewAdapter
                = new HeaderAndFooterRecyclerViewAdapter(mMessageAdapter);
        mLoadMoreView = new LoadMoreView(getActivity());
        mLoadMoreView.setState(XRecyclerView.STATE_EMPTY);
        headerAndFooterRecyclerViewAdapter.addHeaderView(mLoadMoreView);

        mMessageAdapter.setOnItemLongClickListener(this);
        etContent.addTextChangedListener(this);
        mRecyclerView.setAdapter(headerAndFooterRecyclerViewAdapter);
        etContent.setOnTouchListener(this);

        mRecyclerView.addOnScrollListener(new ScrollListener());
    }

    private void hideBottom() {
        SystemUtil.hideKeyBoard(getActivity());
        showEmotionView(false);
    }

    protected void onEmotionDeleteClick() {
        KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
        etContent.dispatchKeyEvent(event);
    }

    protected void onEmotionClick(Emotion emotion) {
        Editable editAble = etContent.getEditableText();
        int start = etContent.getSelectionStart();
        editAble.insert(start, emotion.key);
    }

    private void loadMore() {
        mPresent.loadMore();
    }

    private void initIconImage() {
        Drawable emotionDrawable = DrawableUtil.createSelectThemeDrawable(getActivity(),
                R.mipmap.compose_emoticonbutton_background,
                R.color.icon_normal_color, R.color.colorPrimary);
        ivEmotion.setImageDrawable(emotionDrawable);

        Drawable addDrawable = DrawableUtil.createSelectThemeDrawable(getActivity(),
                R.mipmap.navigationbar_subsribe_manage,
                R.color.icon_normal_color, R.color.colorPrimary);
        ivAdd.setImageDrawable(addDrawable);
    }

    @Override
    protected ChatPresent createPresent() {
        Token accessToken = UserPrefs.get(getActivity()).getToken();
        long recipientId = getArguments().getLong(Key.ID);
        return new ChatPresentImp(accessToken, recipientId, Long.parseLong(accessToken.getUid()),
                new MessageApiImp(), new MessageManagerImp(), new UserManagerImp(), new NotifyManagerImp(), this);
    }


    @Override
    public void onLoadComplete(boolean isHaveMore) {
        if (isHaveMore) {
            mLoadMoreView.setState(XRecyclerView.STATE_NORMAL);
        }else {
            mLoadMoreView.setState(XRecyclerView.STATE_EMPTY);
        }
    }

    @Override
    public void setEntities(List<DirectMessage> entities) {
        mMessageAdapter.setEntities(entities);
        mMessageAdapter.notifyDataSetChanged();
    }

    @Override
    public void showErrorView() {

    }

    @Override
    public void notifyItemChanged(List<DirectMessage> entities, int index) {
        mMessageAdapter.setEntities(entities);
        mMessageAdapter.notifyItemChanged(index);
    }

    @Override
    public void notifyItemRangeInserted(List<DirectMessage> entities, int position, int count) {
        mMessageAdapter.setEntities(entities);
        mMessageAdapter.notifyItemRangeInserted(position, count);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.getDefault().unregister(EventTag.ON_EMOTION_CLICK, mEmotionObservable);
        RxBus.getDefault().unregister(EventTag.ON_EMOTION_DELETE_CLICK, mEmotionDeleteObservable);
    }

    @OnClick({R.id.et_content, R.id.iv_emotion, R.id.iv_add, R.id.tv_send})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.et_content:
                showEmotionView(false);
                break;
            case R.id.iv_emotion:
                onEmotionIconClick();
                break;
            case R.id.iv_add:
                Intent intent = NavigationUtil.newSelectImageActivityIntent(getActivity(), 9);
                startActivityForResult(intent, Key.REQUEST_CODE_SELECT_IMAGE);
                break;
            case R.id.tv_send:
                mPresent.sendTextMessage(etContent.getText().toString());
                etContent.setText("");
                break;
        }
    }

    private void onEmotionIconClick() {
        if (flEmotion.getVisibility() == View.VISIBLE) {
            showEmotionView(false);
            SystemUtil.showKeyBoard(getActivity());
        } else {
            if (SystemUtil.isKeyBoardShow(getActivity())) {
                flEmotion.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showEmotionView(true);
                    }
                }, 200);
                SystemUtil.hideKeyBoard(getActivity());
            } else {
                showEmotionView(true);
            }
        }
    }

    private void showEmotionView(boolean isShow) {
        if (isShow) {
            flEmotion.setVisibility(View.VISIBLE);
            ivEmotion.setSelected(true);
        }else {
            flEmotion.setVisibility(View.GONE);
            ivEmotion.setSelected(false);
        }
    }

    @Override
    public boolean onBackPressed() {
        if (flEmotion.getVisibility() == View.VISIBLE) {
            showEmotionView(false);
            return true;
        }
        return false;
    }

    @Override
    public void toScrollToPosition(int position) {
        mRecyclerView.scrollToPosition(position);
    }

    @Override
    public void attemptSmoothScrollToBottom() {
        int last = mMessageAdapter.getItemCount();
        if (mLinearLayoutManager.findLastVisibleItemPosition() + 3 >= last) {
            mRecyclerView.smoothScrollToPosition(mMessageAdapter.getEntities().size()
                    + headerAndFooterRecyclerViewAdapter.getHeaderViewsCount() - 1);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (TextUtils.isEmpty(s)) {
            tvSend.setVisibility(GONE);
            ivAdd.setVisibility(VISIBLE);
        }else {
            tvSend.setVisibility(VISIBLE);
            ivAdd.setVisibility(GONE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Key.REQUEST_CODE_SELECT_IMAGE && resultCode == Activity.RESULT_OK) {
            ArrayList<String> paths = data.getStringArrayListExtra(Key.IMAGE_PATHS);
            onSelectSuccess(paths);
        }
    }

    private void onSelectSuccess(ArrayList<String> paths) {
        mPresent.sendImageMessage(paths);
    }

    @Override
    public void onItemClick(View view, int position) {
        DirectMessage directMessage = mMessageAdapter.getItem(position - headerAndFooterRecyclerViewAdapter.getHeaderViewsCount());
        int type = mMessageAdapter.getItemViewType(position - 1);
        if (type == MessageDelegateProvider.TYPE_OTHER_IMAGE || type == MessageDelegateProvider.TYPE_SELT_IMAGE) {
            ArrayList<ImageInfo> images = new ArrayList<>(1);
            images.add(directMessage.getImageInfo());

            ArrayList<ImageInfo> hdPaths = new ArrayList<>(1);
            ImageInfo hdImageInfo = new ImageInfo(mPresent.getMessageImageHdUrl(directMessage), directMessage.getImageInfo().getWidth(),
                    directMessage.getImageInfo().getWidth(), directMessage.getImageInfo().getImageType());
            hdPaths.add(hdImageInfo);

            Intent intent = ImagePrewActivity.newIntent(getActivity(), images, hdPaths, 0);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View view, int position) {
        final DirectMessage directMessage = mMessageAdapter.getItem(position - headerAndFooterRecyclerViewAdapter.getHeaderViewsCount());
        if (view.getId() == R.id.iv_avatar) {
            Intent intent = UserInfoActivity.newIntent(getActivity(), directMessage.getSender_screen_name());
            startActivity(intent);
        }else if (view.getId() == R.id.iv_fail) {
            if (directMessage.getLocal_status() == DirectMessage.STATUS_FAIL) {
                DialogUtil.showHintDialog(getActivity(), getString(R.string.hint), "是否重新发送",
                        getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mPresent.sendMessage(directMessage);
                            }
                        }, getString(R.string.cancel), null);
            }
        }
    }

    @Override
    public boolean onItemLongClick(View view, int i) {
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.et_content) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                showEmotionView(false);
            }
        }
        return false;
    }

    private class ScrollListener extends  OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int firstVisibleItemPosition = mLinearLayoutManager.findFirstVisibleItemPosition();
            if (firstVisibleItemPosition == 0 && mLoadMoreView.getState() == XRecyclerView.STATE_NORMAL) {
                mLoadMoreView.setState(XRecyclerView.STATE_LOADING);
                loadMore();
            }
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                hideBottom();
            }
        }
    }
}
