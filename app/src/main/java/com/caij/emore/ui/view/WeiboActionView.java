package com.caij.emore.ui.view;

import com.caij.emore.database.bean.Status;

/**
 * Created by Caij on 2016/7/14.
 */
public interface WeiboActionView extends BaseView{

    void onDeleteStatusSuccess(Status weibo, int position);

    void onCollectSuccess(Status weibo);

    void onUnCollectSuccess(Status weibo);

}
