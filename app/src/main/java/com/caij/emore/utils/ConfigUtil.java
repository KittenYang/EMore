package com.caij.emore.utils;

import android.content.Context;

import com.caij.emore.BuildConfig;
import com.caij.emore.Key;
import com.caij.emore.service.EMoreService;
import com.caij.emore.utils.db.DBManager;

/**
 * Created by Caij on 2016/7/15.
 */
public class ConfigUtil {

    public static void resetConfig(Context context, long uid) {
        DBManager.initDB(context, Key.DB_NAME + uid, BuildConfig.DEBUG);
        EMoreService.stop(context);
        EMoreService.start(context);
    }
}
