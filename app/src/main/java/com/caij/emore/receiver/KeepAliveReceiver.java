package com.caij.emore.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.caij.emore.UserPrefs;
import com.caij.emore.service.EMoreService;
import com.caij.emore.utils.AppUtil;
import com.caij.emore.utils.LogUtil;

public class KeepAliveReceiver extends BroadcastReceiver {

    @Override public void onReceive(Context context, Intent intent) {
        LogUtil.d(this, "onReceive");
        if (UserPrefs.get().getEMoreToken() != null && UserPrefs.get().getWeiCoToken() != null) {
            AppUtil.startService(context);
        }
    }

}
