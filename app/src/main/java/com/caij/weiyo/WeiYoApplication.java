package com.caij.weiyo;

import android.app.Application;
import android.os.Build;

import com.caij.weiyo.bean.AccessToken;
import com.caij.weiyo.database.dao.DBManager;
import com.caij.weiyo.utils.SPUtil;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by Caij on 2016/5/27.
 */
public class WeiYoApplication extends Application{


    public void onCreate() {
        super.onCreate();
        SPUtil.init(this, Key.SP_CONFIG);
        initDB();
        CrashReport.initCrashReport(getApplicationContext(), Key.BUGLY_KEY, !BuildConfig.DEBUG);
    }

    private void initDB() {
        AccessToken accessToken = UserPrefs.get().getWeiYoToken();
        if (accessToken != null) {
            DBManager.initDB(this, Key.DB_NAME + accessToken.getUid());
        }
    }

}
