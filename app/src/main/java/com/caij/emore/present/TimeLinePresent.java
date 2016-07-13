package com.caij.emore.present;


import com.caij.emore.database.bean.Weibo;

/**
 * Created by Caij on 2016/6/29.
 */
public interface TimeLinePresent extends RefreshListPresent {

    void deleteWeibo(Weibo weibo, int position);

    void collectWeibo(Weibo weibo);

    void uncollectWeibo(Weibo weibo);

    void attitudesWeibo(Weibo weibo);

    void destoryAttitudesWeibo(Weibo weibo);
}
