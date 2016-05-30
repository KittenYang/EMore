package com.caij.weiyo;

import android.app.Application;

import com.caij.weiyo.utils.SPUtil;

/**
 * Created by Caij on 2016/5/27.
 */
public class WeiYoApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        SPUtil.init(this, Key.SP_CONFIG);
    }
}
