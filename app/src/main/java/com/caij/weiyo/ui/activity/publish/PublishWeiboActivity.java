package com.caij.weiyo.ui.activity.publish;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import com.caij.weiyo.Key;
import com.caij.weiyo.R;
import com.caij.weiyo.UserPrefs;
import com.caij.weiyo.bean.AccessToken;
import com.caij.weiyo.bean.Emotion;
import com.caij.weiyo.present.WeiboPublishPresent;
import com.caij.weiyo.present.imp.WeiboPublishPresentImp;
import com.caij.weiyo.source.server.ServerPublishWeiboSourceImp;
import com.caij.weiyo.ui.adapter.PublishImageAdapter;
import com.caij.weiyo.utils.NavigationUtil;
import com.caij.weiyo.view.recyclerview.RecyclerViewOnItemClickListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Caij on 2016/6/22.
 */
public class PublishWeiboActivity extends PublishActivity implements RecyclerViewOnItemClickListener {


    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private PublishImageAdapter mPublishImageAdapter;
    private WeiboPublishPresent mWeiboPublishPresent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        AccessToken token = UserPrefs.get().getToken();
        mWeiboPublishPresent = new WeiboPublishPresentImp(token.getAccess_token(), Key.WEIBO_APP_ID,
                new ServerPublishWeiboSourceImp());
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
}
