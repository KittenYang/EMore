package com.caij.emore.ui.view;

import com.caij.emore.database.bean.Status;

/**
 * Created by Caij on 2016/12/9.
 */

public interface ListStatusActionView extends WeiboActionView {

    void onStatusAttitudeUpdate(Status status, int index);

    void onStatusAttitudeCountUpdate(Status status, int index);

    void onStatusCommentCountUpdate(Status status, int index);

    void onStatusRelayCountUpdate(Status status, int index);
}
