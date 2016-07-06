package com.caij.weiyo.present.view;

import android.content.Context;

import com.caij.weiyo.bean.Weibo;

import java.util.List;

/**
 * Created by Caij on 2016/5/31.
 */
public interface TimeLineWeiboView extends RefreshListView<Weibo> {

    Context getContent();

    void onDeleteWeiboSuccess(Weibo weibo, int position);

    void onCollectSuccess(Weibo weibo);

    void onUncollectSuccess(Weibo weibo);
}
