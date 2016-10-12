package com.caij.emore.ui.view;

import com.caij.emore.database.bean.Status;

/**
 * Created by Caij on 2016/7/14.
 */
public interface StatusDetailView extends WeiboActionView {

    void setStatus(Status status);

    void onRefreshComplete();

    void onStatusAttitudeCountUpdate(int count);

    void onStatusAttitudeUpdate(boolean isAttitude);

    void onStatusCommentCountUpdate(int count);

    void onStatusRelayCountUpdate(int count);
}
