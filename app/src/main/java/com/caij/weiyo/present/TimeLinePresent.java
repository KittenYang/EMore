package com.caij.weiyo.present;

import com.caij.weiyo.bean.Weibo;

/**
 * Created by Caij on 2016/6/29.
 */
public interface TimeLinePresent extends BasePresent {

    public void onLoadMore();

    public void onRefresh();

    void deleteWeibo(Weibo weibo, int position);

    void collectWeibo(Weibo weibo);

    void uncollectWeibo(Weibo weibo);
}
