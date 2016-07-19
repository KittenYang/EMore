package com.caij.emore.present.view;

import com.caij.emore.database.bean.Weibo;

/**
 * Created by Caij on 2016/7/6.
 */
public interface FriendWeiboView extends TimeLineWeiboView {

    void toRefresh();

    void onWeiboPublishSuccess(Weibo weibo);
}
