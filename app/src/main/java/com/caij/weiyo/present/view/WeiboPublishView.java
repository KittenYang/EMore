package com.caij.weiyo.present.view;

import android.content.Context;

import com.caij.weiyo.bean.Weibo;

/**
 * Created by Caij on 2016/6/25.
 */
public interface WeiboPublishView extends BaseView {
    void toAuthWeico();

    Context getContent();

    void onPublishSuccess(Weibo weibo);

    void finish();

    void showPublishLoading(boolean isShow);
}
