package com.caij.weiyo;

import android.app.Application;

import com.caij.weiyo.bean.AccessToken;
import com.caij.weiyo.database.dao.DBManager;
import com.caij.weiyo.utils.SPUtil;

/**
 * Created by Caij on 2016/5/27.
 */
public class WeiYoApplication extends Application{


    public void onCreate() {
        super.onCreate();
        SPUtil.init(this, Key.SP_CONFIG);
        initDB();
    }

    private void initDB() {
        AccessToken accessToken = UserPrefs.get().getToken();
        if (accessToken != null) {
            DBManager.initDB(this, Key.DB_NAME + accessToken.getUid());
        }
    }

}
