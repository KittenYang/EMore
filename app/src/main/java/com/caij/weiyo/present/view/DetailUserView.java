package com.caij.weiyo.present.view;

import com.caij.weiyo.bean.User;

/**
 * Created by Caij on 2016/6/3.
 */
public interface DetailUserView extends BaseView{

    public void setUser(User user);

    public void showGetUserLoading(boolean isShow);

    void showFollowLoading(boolean b);

    void onFollowSuccess();

    void onUnfollowSuccess();
}
