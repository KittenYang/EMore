package com.caij.emore.ui.view;

import android.content.Context;

import com.caij.emore.bean.PublishBean;
import com.caij.emore.database.bean.Weibo;


/**
 * Created by Caij on 2016/6/25.
 */
public interface PublishServiceView extends BaseView {

    void onPublishStart(PublishBean publishBean);

    void onPublishFail();

    void onPublishSuccess(Weibo weibo);

    Context getContent();

}