package com.caij.weiyo.present.view;

import android.content.Context;

import com.caij.weiyo.bean.Weibo;

import java.util.List;

/**
 * Created by Caij on 2016/5/31.
 */
public interface FriendWeiboView extends RefreshListView {

    public void setFriendWeibo(List<Weibo> weibos);

    void toRefresh();

    Context getContent();
}
