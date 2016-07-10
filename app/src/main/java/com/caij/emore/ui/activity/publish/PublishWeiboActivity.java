package com.caij.emore.ui.activity.publish;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.UserPrefs;
import com.caij.emore.bean.Account;
import com.caij.emore.bean.Emotion;
import com.caij.emore.bean.Weibo;
import com.caij.emore.present.WeiboPublishPresent;
import com.caij.emore.present.imp.WeiboPublishPresentImp;
import com.caij.emore.present.view.WeiboPublishView;
import com.caij.emore.source.server.ServerWeiboSource;
import com.caij.emore.ui.activity.login.WeiCoLoginActivity;
import com.caij.emore.ui.adapter.PublishImageAdapter;
import com.caij.emore.utils.DialogUtil;
import com.caij.emore.utils.NavigationUtil;
import com.caij.emore.utils.ToastUtil;
import com.caij.emore.utils.weibo.WeicoAuthUtil;
import com.caij.emore.view.recyclerview.RecyclerViewOnItemClickListener;

import java.util.ArrayList;

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
    }

    private void initPresent() {
        mWeiboPublishPresent = new WeiboPublishPresentImp(UserPrefs.get().getAccount(),
                new ServerWeiboSource(), this);
    }

    @Override
    protected void onEmotionDeleteClick() {

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
        mWeiboPublishPresent.publishWeibo(etContent.getText().toString(), (ArrayList<String>) mPublishImageAdapter.getEntities());
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
    protected void onDestroy() {
        super.onDestroy();
        mWeiboPublishPresent.onDestroy();
    }

    @Override
    public void toAuthWeico() {
        WeicoAuthUtil.toAuthWeico(this, false);
    }

    @Override
    public Context getContent() {
        return this;
    }

    @Override
    public void onPublishSuccess(Weibo weibo) {
        Intent intent = new Intent();
        intent.putExtra(Key.OBJ, weibo);
        setResult(RESULT_OK, intent);
        ToastUtil.show(this, getString(R.string.publish_success));
        finish();
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
