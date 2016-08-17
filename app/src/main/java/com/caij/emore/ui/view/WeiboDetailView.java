package com.caij.emore.ui.view;

import com.caij.emore.database.bean.Weibo;

/**
 * Created by Caij on 2016/7/14.
 */
public interface WeiboDetailView extends WeiboActionView {

    void setWeibo(Weibo weibo);

    void onRefreshComplete();

    void onWeiboUpdate(Weibo weibo);
}
