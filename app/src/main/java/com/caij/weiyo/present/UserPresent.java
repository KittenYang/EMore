package com.caij.weiyo.present;

import com.caij.weiyo.bean.User;

import rx.Observable;

/**
 * Created by Caij on 2016/6/3.
 */
public interface UserPresent extends BasePresent{

    public void getWeiboUserInfoByName(String name);

    public void getWeiboUserInfoByUid(long uid);
}
