package com.caij.emore.ui.activity.publish;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.UserPrefs;
import com.caij.emore.bean.Account;
import com.caij.emore.bean.Emotion;
import com.caij.emore.database.bean.Draft;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.present.WeiboPublishPresent;
import com.caij.emore.present.imp.WeiboPublishPresentImp;
import com.caij.emore.present.view.WeiboPublishView;
import com.caij.emore.source.local.LocalDraftSource;
import com.caij.emore.source.server.ServerWeiboSource;
import com.caij.emore.ui.activity.login.WeiCoLoginActivity;
import com.caij.emore.ui.adapter.PublishImageAdapter;
import com.caij.emore.utils.DialogUtil;
import com.caij.emore.utils.NavigationUtil;
import com.caij.emore.utils.ToastUtil;
import com.caij.emore.utils.weibo.WeicoAuthUtil;
import com.caij.emore.view.recyclerview.RecyclerViewOnItemClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Caij on 2016/6/22.
 */
public class PublishWeiboActivity extends PublishActivity implements RecyclerViewOnItemClickListener, WeiboPublishView {


    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private PublishImageAdapter mPublishImageAdapter;
    private WeiboPublishPresent mWeiboPublishPresent;

    private Draft mDraft;

    public static Intent newIntent(Context context, Draft draft) {
        Intent intent = new Intent(context, PublishWeiboActivity.class);
        intent.putExtra(Key.OBJ, draft);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPresent();
        setTitle(R.string.publish_weibo);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.bottom = getResources().getDimensionPixelSize(R.dimen.image_item_space);
                outRect.top = getResources().getDimensionPixelSize(R.dimen.image_item_space);
                outRect.left = getResources().getDimensionPixelSize(R.dimen.image_item_space);
                outRect.right = getResources().getDimensionPixelSize(R.dimen.image_item_space);
            }
        });
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mPublishImageAdapter = new PublishImageAdapter(this);
        mRecyclerView.setAdapter(mPublishImageAdapter);
        mPublishImageAdapter.setOnItemClickListener(this);

        mWeiboPublishPresent.onCreate();

        Draft draft = (Draft) getIntent().getSerializableExtra(Key.OBJ);
        mDraft = draft;
        if (draft != null) {
            fillData(draft);
        }
    }

    private void fillData(Draft draft) {
        etContent.setText(draft.getContent());
        if (draft.getImages() != null && draft.getImages().size() > 0) {
            List<String> images = new ArrayList<>(draft.getImages().size());
            images.addAll(draft.getImages());
            onSelectSuccess((ArrayList<String>) images);
        }
    }

    private void initPresent() {
        mWeiboPublishPresent = new WeiboPublishPresentImp(UserPrefs.get().getAccount(), this, new LocalDraftSource());
    }

    @Override
    protected void onEmotionDeleteClick() {
        KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
        etContent.dispatchKeyEvent(event);
    }

    @Override
    protected void onEmotionClick(Emotion emotion) {
        etContent.append(emotion.key);
    }

    @Override
    public int getWeiboContentLayoutId() {
        return R.layout.include_publish_weibo;
    }

    @Override
    protected void onSendClick() {
        long id = mDraft != null ? mDraft.getId() : System.currentTimeMillis();
        mWeiboPublishPresent.publishWeibo(id, etContent.getText().toString(), (ArrayList<String>) mPublishImageAdapter.getEntities());
    }

    @Override
    protected void onSelectSuccess(ArrayList<String> paths) {
        mRecyclerView.setVisibility(View.VISIBLE);
        mPublishImageAdapter.addEntities(paths);
        mPublishImageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = NavigationUtil.newImagePreActivityIntent(this,
                (ArrayList<String>) mPublishImageAdapter.getEntities(), position);
        startActivity(intent);
    }

    @Override
    public void finish() {
        if (isContentChange()) {
            DialogUtil.showHintDialog(this, getString(R.string.hint), "是否保存到草稿箱", getString(R.string.ok),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            long id = mDraft != null ? mDraft.getId() : System.currentTimeMillis();
                            mWeiboPublishPresent.saveToDraft(id, etContent.getText().toString(), (ArrayList<String>) mPublishImageAdapter.getEntities());
                            PublishWeiboActivity.super.finish();
                        }
                    }, getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PublishWeiboActivity.super.finish();
                        }
                    });
        }else {
            super.finish();
        }
    }

    private boolean isContentChange() {
        if (mDraft != null) {
            boolean textChange = !etContent.getText().toString().equals(mDraft.getContent());
            boolean imageChange;
            if (mDraft.getImages() != null) {
                imageChange = !mDraft.getImages().equals(mPublishImageAdapter.getEntities());
            }else {
                imageChange = mPublishImageAdapter.getEntities() != null && mPublishImageAdapter.getEntities().size() > 0;
            }
            return  textChange || imageChange;
        }else {
            return !TextUtils.isEmpty(etContent.getText()) ||
                    (mPublishImageAdapter.getEntities() != null && mPublishImageAdapter.getEntities().size() > 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWeiboPublishPresent.onDestroy();
    }

    @Override
    public void toAuthWeico() {
        WeicoAuthUtil.toAuthWeico(this, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Key.AUTH) {
                initPresent();
            }
        }
    }
}
