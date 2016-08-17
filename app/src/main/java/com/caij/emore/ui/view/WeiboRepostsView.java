package com.caij.emore.ui.view;


import com.caij.emore.database.bean.Weibo;

import java.util.List;


/**
 * Created by Caij on 2016/5/31.
 */
public interface WeiboRepostsView extends ListView<Weibo> {

    void onRepostWeiboSuccess(List<Weibo> weobos);
}
