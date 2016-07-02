package com.caij.weiyo.ui.activity.publish;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.caij.weiyo.bean.Account;
import com.caij.weiyo.bean.Emotion;
import com.caij.weiyo.bean.Weibo;
import com.caij.weiyo.present.WeiboPublishPresent;
import com.caij.weiyo.present.imp.WeiboPublishPresentImp;
import com.caij.weiyo.present.view.WeiboPublishView;
import com.caij.weiyo.source.server.ServerPublishWeiboSourceImp;
import com.caij.weiyo.ui.activity.LoginActivity;
import com.caij.weiyo.ui.adapter.PublishImageAdapter;
import com.caij.weiyo.utils.DialogUtil;
import com.caij.weiyo.utils.NavigationUtil;
import com.caij.weiyo.utils.ToastUtil;
import com.caij.weiyo.view.recyclerview.RecyclerViewOnItemClickListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Caij on 2016/6/22.
 */
public class PublishWeiboActivity extends PublishActivity implements RecyclerViewOnItemClickListener, WeiboPublishView {


    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private Dialog mPublishDialog;

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
                new ServerPublishWeiboSourceImp(), this);
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
        DialogUtil.showHintDialog(this, "提示", "需要认证高级权限", "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Account account = UserPrefs.get().getAccount();
                Intent intent = LoginActivity.newWeiCoLoginIntent(PublishWeiboActivity.this,
                        account.getUsername(), account.getPwd());
                startActivityForResult(intent, Key.AUTH);
            }
        });
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
    public void showPublishLoading(boolean isShow) {
        if (isShow) {
            if (mPublishDialog == null) {
                mPublishDialog = DialogUtil.showProgressDialog(this, null, getString(R.string.publish_loading));
            }
        }else {
            mPublishDialog.dismiss();
        }
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
