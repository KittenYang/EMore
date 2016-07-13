package com.caij.emore.present.view;

import android.content.Context;

import com.caij.emore.database.bean.Weibo;


/**
 * Created by Caij on 2016/5/31.
 */
public interface TimeLineWeiboView extends RefreshListView<Weibo> {

    Context getContent();

    void onDeleteWeiboSuccess(Weibo weibo, int position);

    void onCollectSuccess(Weibo weibo);

    void onUncollectSuccess(Weibo weibo);

    void onAttitudesSuccess(Weibo weibo);
}
