package com.caij.emore.utils.weibo;

import android.content.Context;

import com.caij.emore.R;
import com.caij.emore.utils.SPUtil;

/**
 * Created by wangdan on 15/4/30.
 */
public class ThemeUtils {

    private static final String SP_THEME_NAME = "sp_theme";
    private static final String THEME_NAME = "theme";

    public static int[][] THEME_ARR = {
            { R.style.AppTheme_Default, R.style.AppTheme_Default_Main, R.style.AppTheme_Default_ImagePrewActivity, R.style.AppTheme_Default_VideoPlayActivity, R.style.AppTheme_Default_SplashActivity, R.style.AppTheme_Default_SearchActivity },
            { R.style.AppTheme_Pink, R.style.AppTheme_Pink_Main, R.style.AppTheme_Pink_ImagePrewActivity, R.style.AppTheme_Pink_VideoPlayActivity, R.style.AppTheme_Pink_SplashActivity, R.style.AppTheme_Pink_SearchActivity },
            { R.style.AppTheme_Blue, R.style.AppTheme_Blue_Main, R.style.AppTheme_Blue_ImagePrewActivity, R.style.AppTheme_Blue_VideoPlayActivity, R.style.AppTheme_Blue_SplashActivity, R.style.AppTheme_Blue_SearchActivity },
            { R.style.AppTheme_Green, R.style.AppTheme_Green_Main, R.style.AppTheme_Green_ImagePrewActivity, R.style.AppTheme_Green_VideoPlayActivity, R.style.AppTheme_Green_SplashActivity, R.style.AppTheme_Green_SearchActivity },
            { R.style.AppTheme_Yellow, R.style.AppTheme_Yellow_Main, R.style.AppTheme_Yellow_ImagePrewActivity, R.style.AppTheme_Yellow_VideoPlayActivity, R.style.AppTheme_Yellow_SplashActivity, R.style.AppTheme_Yellow_SearchActivity },
            { R.style.AppTheme_Red, R.style.AppTheme_Red_Main, R.style.AppTheme_Red_ImagePrewActivity, R.style.AppTheme_Red_VideoPlayActivity, R.style.AppTheme_Red_SplashActivity, R.style.AppTheme_Red_SearchActivity },
            { R.style.AppTheme_Grey, R.style.AppTheme_Grey_Main, R.style.AppTheme_Grey_ImagePrewActivity, R.style.AppTheme_Grey_VideoPlayActivity, R.style.AppTheme_Grey_SplashActivity, R.style.AppTheme_Grey_SearchActivity },
    };


    public static void changeTheme(Context context, int position) {
        new SPUtil.SPBuilder(context)
                .open(SP_THEME_NAME).edit()
                .putInt(THEME_NAME, position)
                .apply();
    }

    public static int getThemePosition(Context context) {
        return new SPUtil.SPBuilder(context)
                .open(SP_THEME_NAME)
                .getInt(THEME_NAME, 0);
    }

}
