package com.caij.emore.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Caij on 2015/8/26.
 */
public class SPUtil {

    public static String DEFAULT_SP_NAME = "default_sp";

    public static class SPBuilder {

        private Context mContext;

        public SPBuilder(Context context) {
            mContext = context;
        }

        public SharedPreferences open(String name) {
            return mContext.getSharedPreferences(name, Context.MODE_PRIVATE);
        }

        public SharedPreferences openDefault() {
            return open(DEFAULT_SP_NAME);
        }

    }


}
