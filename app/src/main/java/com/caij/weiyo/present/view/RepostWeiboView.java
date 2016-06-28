package com.caij.weiyo.present.view;

import com.caij.weiyo.bean.Weibo;

/**
 * Created by Caij on 2016/6/27.
 */
public interface RepostWeiboView extends BaseView{

    public void onRepostSuccess(Weibo weibo);

    void showLoading(boolean isShow);
}
