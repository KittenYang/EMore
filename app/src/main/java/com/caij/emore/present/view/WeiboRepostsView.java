package com.caij.emore.present.view;


import android.content.Context;

import com.caij.emore.database.bean.Weibo;

import java.util.List;


/**
 * Created by Caij on 2016/5/31.
 */
public interface WeiboRepostsView extends BaseListView<Weibo> {

    void onRepostWeiboSuccess(List<Weibo> weobos);
}
