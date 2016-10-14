package com.caij.emore.ui.view;

import android.content.Context;

import com.caij.emore.bean.PublishBean;
import com.caij.emore.database.bean.Status;


/**
 * Created by Caij on 2016/6/25.
 */
public interface PublishStatusView extends BaseView {

    void onPublishStart(PublishBean publishBean);

    void onPublishFail();

    void onPublishSuccess(Status weibo);

    Context getContent();

}
