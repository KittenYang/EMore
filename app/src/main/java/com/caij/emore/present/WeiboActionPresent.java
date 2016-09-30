package com.caij.emore.present;

import com.caij.emore.database.bean.Weibo;

/**
 * Created by Caij on 2016/7/14.
 */
public interface WeiboActionPresent  extends BasePresent{


    void deleteWeibo(Weibo weibo, int position);

    void collectWeibo(Weibo weibo);

    void uncollectWeibo(Weibo weibo);

    void attitudesWeibo(Weibo weibo);

    void destroyAttitudesWeibo(Weibo weibo);
}
