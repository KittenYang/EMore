package com.caij.emore.ui.view;

import com.caij.emore.database.bean.Status;

/**
 * Created by Caij on 2016/7/6.
 */
public interface FriendStatusView extends TimeLineStatusView {

    void toRefresh();

    void onStatusPublishSuccess(Status weibo);
}
