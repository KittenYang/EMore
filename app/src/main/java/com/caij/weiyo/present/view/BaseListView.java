package com.caij.weiyo.present.view;

/**
 * Created by Caij on 2016/6/16.
 */
public interface BaseListView extends BaseView{
    void onLoadComplite(boolean isHaveMore);
    void onEmpty();
}
