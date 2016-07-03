package com.caij.weiyo.present.view;

import android.content.Context;

import com.caij.weiyo.bean.Weibo;

import java.util.List;

/**
 * Created by Caij on 2016/5/31.
 */
public interface TimeLineWeiboView extends RefreshListView {

    public void setWeibos(List<Weibo> weibos);

    void toRefresh();

    Context getContent();

    void onDeleteWeiboSuccess(Weibo weibo);

    void showDialogLoging(boolean isShow);

    void onCollectSuccess(Weibo weibo);

    void onUncollectSuccess(Weibo weibo);
}
