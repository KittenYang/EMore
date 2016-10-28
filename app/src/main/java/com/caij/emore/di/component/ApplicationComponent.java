package com.caij.emore.di.component;

import android.app.Application;

import com.caij.emore.di.an.ImageClient;
import com.caij.emore.di.module.ApplicationModule;

import javax.inject.Singleton;

import dagger.Component;
import okhttp3.OkHttpClient;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {


    Application application();

    @ImageClient
    OkHttpClient imageOkHttpClient();
}
