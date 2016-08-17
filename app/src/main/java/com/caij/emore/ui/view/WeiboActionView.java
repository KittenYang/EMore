package com.caij.emore.ui.view;

import com.caij.emore.database.bean.Weibo;

/**
 * Created by Caij on 2016/7/14.
 */
public interface WeiboActionView extends BaseView{

    void onDeleteWeiboSuccess(Weibo weibo, int position);

    void onCollectSuccess(Weibo weibo);

    void onUncollectSuccess(Weibo weibo);

}
