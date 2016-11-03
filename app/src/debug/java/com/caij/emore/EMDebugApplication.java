package com.caij.emore;


import android.os.Looper;

import com.caij.emore.utils.LogUtil;
import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;


/**
 * Created by Caij on 15/11/03.
 */
public class EMDebugApplication extends EMApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());
        if (!LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            LeakCanary.install(this);
        }

        final String SEPARATOR = "\r\n";
        Looper.getMainLooper().setMessageLogging(new LooperMonitor(new LooperMonitor.BlockListener() {
            @Override
            public void onBlockEvent(long realStartTime, long realTimeEnd, long threadTimeStart, long threadTimeEnd) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("realStartTime").append(" : ").append(realStartTime)
                        .append(SEPARATOR)
                        .append("realTimeEnd").append(" : ").append(realTimeEnd)
                        .append(SEPARATOR);
//                for (StackTraceElement stackTraceElement : Looper.getMainLooper().getThread().getStackTrace()) {
//                    stringBuilder
//                            .append(stackTraceElement.toString())
//                            .append(SEPARATOR);
//                }
                LogUtil.e("LooperMonitor", stringBuilder.toString());
            }
        }, 3000));
    }
}
