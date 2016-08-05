package com.caij.emore.utils;

import android.content.Context;

import com.caij.emore.BuildConfig;
import com.caij.emore.Key;
import com.caij.emore.service.EMoreService;
import com.caij.emore.utils.db.DBManager;

/**
 * Created by Caij on 2016/7/15.
 */
public class AppUtil {

    public static void resetConfig(Context context, long uid) {
        stop(context);
        start(context, uid);
    }

    public static void stop(Context context) {
        if (DBManager.getDaoSession() != null && DBManager.getDaoSession().getDatabase().isOpen()) {
            DBManager.getDaoSession().getDatabase().close();
        }
        EMoreService.stop(context);
    }

    public static void start(Context context, long uid) {
        DBManager.initDB(context, Key.DB_NAME + uid, BuildConfig.DEBUG);
        startService(context);
    }

    public static void startService(Context context) {
        EMoreService.start(context);
    }
}
