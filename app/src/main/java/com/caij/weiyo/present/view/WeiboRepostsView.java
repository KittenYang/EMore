package com.caij.weiyo.present.view;


import android.content.Context;

import com.caij.weiyo.bean.Weibo;

import java.util.List;

/**
 * Created by Caij on 2016/5/31.
 */
public interface WeiboRepostsView extends BaseListView {

    public void setWeibos(List<Weibo> weibos);

    Context getContent();
}
