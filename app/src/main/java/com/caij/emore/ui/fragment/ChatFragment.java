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
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.UserPrefs;
import com.caij.emore.bean.AccessToken;
import com.caij.emore.bean.Emotion;
import com.caij.emore.database.bean.DirectMessage;
import com.caij.emore.database.bean.User;
import com.caij.emore.present.ChatPresent;
import com.caij.emore.present.imp.ChatPresentImp;
import com.caij.emore.present.view.DirectMessageView;
import com.caij.emore.source.UserSource;
import com.caij.emore.source.local.LocalMessageSource;
import com.caij.emore.source.local.LocalUserSource;
import com.caij.emore.source.server.ServerMessageSource;
import com.caij.emore.ui.activity.DefaultFragmentActivity;
import com.caij.emore.ui.activity.ImagePrewActivity;
import com.caij.emore.ui.adapter.MessageAdapter;
import com.caij.emore.utils.DialogUtil;
import com.caij.emore.utils.DrawableUtil;
import com.caij.emore.utils.NavigationUtil;
import com.caij.emore.utils.SystemUtil;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.view.recyclerview.HeaderAndFooterRecyclerViewAdapter;
import com.caij.emore.view.recyclerview.LoadMoreRecyclerView;
import com.caij.emore.view.recyclerview.LoadMoreView;
import com.caij.emore.view.recyclerview.RecyclerViewOnItemClickListener;
import com.caij.emore.view.recyclerview.scroller.BaseSmoothScroller;
import com.caij.emore.view.recyclerview.scroller.SnapperSmoothScroller;

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
public class ChatFragment extends BaseFragment implements
        DefaultFragmentActivity.OnBackPressedListener, DirectMessageView, TextWatcher, RecyclerViewOnItemClickListener {

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

    private MessageAdapter mMessageAdapter;
    private ChatPresent mChatPresent;
    private LoadMoreView mLoadMoreView;
    Observable<Emotion> mEmotionObservable;
    Observable<Object> mEmotionDeleteObservable;
    private LinearLayoutManager mLinearLayoutManager;

    private long mRecipientId;

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
        mRecipientId = getArguments().getLong(Key.ID);
        String recipientName = getArguments().getString(Key.USERNAME);
        getActivity().setTitle(recipientName);
        initImage();
        getChildFragmentManager().beginTransaction().
                replace(R.id.fl_emotion, new EmotionFragment()).commit();
        initView();

        mEmotionObservable = RxBus.get().register(Key.ON_EMOTION_CLICK);
        mEmotionDeleteObservable = RxBus.get().register(Key.ON_EMOTION_DELETE_CLICK);
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

        mChatPresent = createPresent();
        mChatPresent.onCreate();
    }

    private void initView() {
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLinearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageAdapter = new MessageAdapter(getActivity());
        mMessageAdapter.setOnItemClickListener(this);
        HeaderAndFooterRecyclerViewAdapter headerAndFooterRecyclerViewAdapter
                = new HeaderAndFooterRecyclerViewAdapter(mMessageAdapter);
        mLoadMoreView = new LoadMoreView(getActivity());
        mLoadMoreView.setState(LoadMoreRecyclerView.STATE_EMPTY);
        headerAndFooterRecyclerViewAdapter.addHeaderView(mLoadMoreView);

        mRecyclerView.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisibleItemPosition = mLinearLayoutManager.findFirstVisibleItemPosition();
                if (firstVisibleItemPosition == 0 && mLoadMoreView.getState() == LoadMoreRecyclerView.STATE_NORMAL) {
                    mLoadMoreView.setState(LoadMoreRecyclerView.STATE_LOADING);
                    loadMore();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        mMessageAdapter.setItemLongClickListener(new RecyclerViewOnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                final DirectMessage directMessage = mMessageAdapter.getItem(position - 1);
                if (directMessage.getLocal_status() == DirectMessage.STATUS_FAIL) {
                    DialogUtil.showHintDialog(getActivity(), getString(R.string.hint), "是否重新发送",
                            getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mChatPresent.sendMessage(directMessage);
                                }
                            }, getString(R.string.cancel), null);
                }
            }
        });
        etContent.addTextChangedListener(this);
        mRecyclerView.setAdapter(headerAndFooterRecyclerViewAdapter);
    }

    protected void onEmotionDeleteClick() {
        KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
        etContent.dispatchKeyEvent(event);
    }

    protected void onEmotionClick(Emotion emotion) {
        etContent.append(emotion.key);
    }

    private void loadMore() {
        mChatPresent.loadMore();
    }

    private void initImage() {
        Drawable emotionDrawable = DrawableUtil.createSelectThemeDrawable(getActivity(),
                R.mipmap.compose_emoticonbutton_background,
                R.color.icon_normal_color, R.color.colorPrimary);
        ivEmotion.setImageDrawable(emotionDrawable);

        Drawable addDrawable = DrawableUtil.createSelectThemeDrawable(getActivity(),
                R.mipmap.navigationbar_subsribe_manage,
                R.color.icon_normal_color, R.color.colorPrimary);
        ivAdd.setImageDrawable(addDrawable);
    }

    protected ChatPresent createPresent() {
        AccessToken accessToken = UserPrefs.get().getWeiCoToken();
        return new ChatPresentImp(accessToken, mRecipientId, new ServerMessageSource(),
                new LocalMessageSource(), new LocalUserSource(), this);
    }


    @Override
    public void onLoadComplete(boolean isHaveMore) {
        if (isHaveMore) {
            mLoadMoreView.setState(LoadMoreRecyclerView.STATE_NORMAL);
        }else {
            mLoadMoreView.setState(LoadMoreRecyclerView.STATE_EMPTY);
        }
    }

    @Override
    public void onEmpty() {

    }

    @Override
    public void setEntities(List<DirectMessage> entities) {
        mMessageAdapter.setEntities(entities);
        mMessageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mChatPresent.onDestroy();
        RxBus.get().unregister(Key.ON_EMOTION_CLICK, mEmotionObservable);
        RxBus.get().unregister(Key.ON_EMOTION_DELETE_CLICK, mEmotionDeleteObservable);
    }

    @OnClick({R.id.et_content, R.id.iv_emotion, R.id.iv_add, R.id.tv_send})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.et_content:
                flEmotion.setVisibility(View.GONE);
                ivEmotion.setSelected(false);
                break;
            case R.id.iv_emotion:
                if (flEmotion.getVisibility() == View.VISIBLE) {
                    flEmotion.setVisibility(View.GONE);
                    ivEmotion.setSelected(false);
                    SystemUtil.showKeyBoard(getActivity());
                } else {
                    if (SystemUtil.isKeyBoardShow(getActivity())) {
                        flEmotion.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                flEmotion.setVisibility(View.VISIBLE);
                            }
                        }, 200);
                        SystemUtil.hideKeyBoard(getActivity());
                    } else {
                        flEmotion.setVisibility(View.VISIBLE);
                    }
                    ivEmotion.setSelected(true);
                }
                break;
            case R.id.iv_add:
                Intent intent = NavigationUtil.newSelectImageActivityIntent(getActivity(), 9);
                startActivityForResult(intent, Key.REQUEST_CODE_SELECT_IMAGE);
                break;
            case R.id.tv_send:
                mChatPresent.sendTextMessage(etContent.getText().toString());
                break;
        }
    }

    @Override
    public boolean onBackPressed() {
        if (flEmotion.getVisibility() == View.VISIBLE) {
            flEmotion.setVisibility(View.GONE);
            ivEmotion.setSelected(false);
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
            mRecyclerView.smoothScrollToPosition(mMessageAdapter.getEntities().size());
        }
    }

    @Override
    public void notifyDataChange() {
        mMessageAdapter.notifyDataSetChanged();
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
        mChatPresent.sendImageMessage(paths);
    }

    @Override
    public void onItemClick(View view, int position) {
        DirectMessage directMessage = mMessageAdapter.getItem(position - 1);
        int type = mMessageAdapter.getItemViewType(position - 1);
        if (type == MessageAdapter.TYPE_OTHER_IMAGE || type == MessageAdapter.TYPE_SELT_IMAGE) {
            ArrayList<String> images = new ArrayList<>(1);
            images.add(appImageUrl(directMessage.getLocakImage().getUrl()));
            Intent intent = ImagePrewActivity.newIntent(getActivity(), images, 0);
            startActivity(intent);
        }
    }

    private String appImageUrl(String url) {
        if (url.startsWith("http")) {
            return url + "&access_token=" + UserPrefs.get().getWeiCoToken().getAccess_token();
        }
        return url;
    }
}
