package com.caij.emore.ui.activity.publish;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.account.UserPrefs;
import com.caij.emore.bean.Emotion;
import com.caij.emore.manager.imp.DraftManagerImp;
import com.caij.emore.database.bean.Draft;
import com.caij.emore.present.StatusPublishPresent;
import com.caij.emore.present.imp.StatusPublishPresentImp;
import com.caij.emore.ui.view.StatusPublishView;
import com.caij.emore.utils.DialogUtil;
import com.caij.emore.utils.ImageLoader;
import com.caij.emore.utils.NavigationUtil;
import com.caij.emore.utils.weibo.WeicoAuthUtil;
import com.caij.rvadapter.BaseViewHolder;
import com.caij.rvadapter.RecyclerViewOnItemClickListener;
import com.caij.rvadapter.adapter.MultiItemTypeAdapter;
import com.caij.rvadapter.delegate.SingleItemViewDelegate;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Caij on 2016/6/22.
 */
public class PublishStatusActivity extends PublishActivity<StatusPublishPresent> implements RecyclerViewOnItemClickListener, StatusPublishView, TextWatcher {


    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private MultiItemTypeAdapter<String> mPublishImageAdapter;

    private Draft mDraft;

    public static Intent newIntent(Context context, Draft draft) {
        Intent intent = new Intent(context, PublishStatusActivity.class);
        intent.putExtra(Key.OBJ, draft);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        mPublishImageAdapter = new MultiItemTypeAdapter<String>(this);

        mPublishImageAdapter.addItemViewDelegate(new SingleItemViewDelegate<String>(R.layout.item_publish_image) {

            @Override
            public void convert(BaseViewHolder baseViewHolder, String s, int i) {
                ImageView imageView = baseViewHolder.getView(R.id.siv_image);
                ImageLoader.load(PublishStatusActivity.this, imageView, s, R.drawable.weibo_image_placeholder);
                baseViewHolder.getView(R.id.iv_delete).setTag(s);
            }

            @Override
            public void onCreateViewHolder(BaseViewHolder baseViewHolder) {

            }
        });

        mRecyclerView.setAdapter(mPublishImageAdapter);
        mPublishImageAdapter.setOnItemClickListener(this);

        etContent.addTextChangedListener(this);

        Draft draft = (Draft) getIntent().getSerializableExtra(Key.OBJ);
        mDraft = draft;
        if (draft != null) {
            fillData(draft);
        }

        if (savedInstanceState != null) {
            String text = savedInstanceState.getString(Key.TEXT);
            ArrayList<String> images = savedInstanceState.getStringArrayList(Key.IMAGE_PATHS);
            if (!TextUtils.isEmpty(text)) {
                etContent.setText(text);
            }

            if (images != null && images.size() > 0) {
                onSelectImageSuccess(images);
            }
        }
    }

    @Override
    protected StatusPublishPresent createPresent() {
        return  new StatusPublishPresentImp(UserPrefs.get(this).getAccount(), this, new DraftManagerImp());
    }

    private void fillData(Draft draft) {
        etContent.setText(draft.getContent());
        if (draft.getImages() != null && draft.getImages().size() > 0) {
            List<String> images = new ArrayList<>(draft.getImages().size());
            images.addAll(draft.getImages());
            onSelectImageSuccess((ArrayList<String>) images);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (!TextUtils.isEmpty(etContent.getText())) {
            outState.putString(Key.TEXT, etContent.getText().toString());
        }

        if (mPublishImageAdapter.getEntities() != null && mPublishImageAdapter.getEntities().size() > 0) {
            outState.putStringArrayList(Key.IMAGE_PATHS, (ArrayList<String>) mPublishImageAdapter.getEntities());
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onEmotionDeleteClick() {
        KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
        etContent.dispatchKeyEvent(event);
    }

    @Override
    protected void onEmotionClick(Emotion emotion) {
        Editable editAble = etContent.getEditableText();
        int start = etContent.getSelectionStart();
        editAble.insert(start, emotion.key);
    }

    @Override
    public int getWeiboContentLayoutId() {
        return R.layout.include_publish_weibo;
    }

    @Override
    protected void onSendClick() {
        long id = mDraft != null ? mDraft.getId() : System.currentTimeMillis();
        mPresent.publishStatus(id, etContent.getText().toString(), (ArrayList<String>) mPublishImageAdapter.getEntities());
    }

    @Override
    protected void onSelectImageSuccess(ArrayList<String> paths) {
        mRecyclerView.setVisibility(View.VISIBLE);
        mPublishImageAdapter.addEntities(paths);
        mPublishImageAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onSelectMentionSuccess(String name) {
        etContent.append("@" + name + " ");
    }

    @Override
    public void onItemClick(View view, int position) {
        NavigationUtil.startLocalImagePreActivity(this, view,
                (ArrayList<String>) mPublishImageAdapter.getEntities(), position);
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
    public void toAuthWeico() {
        WeicoAuthUtil.toAuthWeico(this, false);
    }

    @Override
    public void onBackPressed() {
        if (!super.checkflEmotion()) {
            if (isContentChange()) {
                DialogUtil.showHintDialog(this, getString(R.string.hint), "是否保存到草稿箱", getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                long id = mDraft != null ? mDraft.getId() : System.currentTimeMillis();
                                mPresent.saveToDraft(id, etContent.getText().toString(),
                                        (ArrayList<String>) mPublishImageAdapter.getEntities());
                                PublishStatusActivity.super.onBackPressed();
                            }
                        }, getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                PublishStatusActivity.super.onBackPressed();
                            }
                        });
            }else {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected boolean checkflEmotion() {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (isContentChange()) {
                    DialogUtil.showHintDialog(this, getString(R.string.hint), "是否保存到草稿箱", getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    long id = mDraft != null ? mDraft.getId() : System.currentTimeMillis();
                                    mPresent.saveToDraft(id, etContent.getText().toString(),
                                            (ArrayList<String>) mPublishImageAdapter.getEntities());
                                    finish();
                                }
                            }, getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });
                }else {
                    finish();
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (count >= 140) {
            showHint(R.string.text_full_hint);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
