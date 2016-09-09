package com.caij.emore;


import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;


/**
 * Created by Caij on 15/11/03.
 */
public class EMoreDebugApplication extends AppApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());
        LeakCanary.install(this);
    }
}
