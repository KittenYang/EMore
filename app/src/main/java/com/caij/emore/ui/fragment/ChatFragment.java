package com.caij.emore.ui.fragment;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.caij.emore.bean.DirectMessage;
import com.caij.emore.present.ChatPresent;
import com.caij.emore.present.imp.ChatPresentImp;
import com.caij.emore.present.view.BaseListView;
import com.caij.emore.source.server.ServerMessageSource;
import com.caij.emore.ui.activity.DefaultFragmentActivity;
import com.caij.emore.ui.adapter.MessageAdapter;
import com.caij.emore.utils.DrawableUtil;
import com.caij.emore.utils.SystemUtil;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Caij on 2016/7/10.
 */
public class ChatFragment extends BaseFragment implements BaseListView<DirectMessage>, DefaultFragmentActivity.OnBackPressedListener {

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
        initImage();
        getChildFragmentManager().beginTransaction().
                replace(R.id.fl_emotion, new EmotionFragment()).commit();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mMessageAdapter = new MessageAdapter(getActivity());
        mRecyclerView.setAdapter(mMessageAdapter);
        mChatPresent = createPresent();
        mChatPresent.onCreate();
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
        long uid = getArguments().getLong(Key.ID);
        return new ChatPresentImp(accessToken.getAccess_token(), uid, new ServerMessageSource(),
                null, this);
    }


    @Override
    public void onLoadComplete(boolean isHaveMore) {

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
        mChatPresent.onCreate();
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
                break;
            case R.id.tv_send:
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
}
