package com.caij.emore.ui.view;


import com.caij.emore.database.bean.User;

/**
 * Created by Caij on 2016/6/3.
 */
public interface DetailUserView extends BaseView{

    public void setUser(User user);

    void onFollowSuccess();

    void onUnfollowSuccess();

    void onRefreshComplete();
}
