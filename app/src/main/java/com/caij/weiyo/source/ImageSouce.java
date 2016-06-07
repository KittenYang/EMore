package com.caij.weiyo.source;

import android.graphics.BitmapFactory;

import com.caij.weiyo.database.bean.LocakImage;

import java.io.IOException;

/**
 * Created by Caij on 2016/6/7.
 */
public interface ImageSouce {

    LocakImage get(String url) throws IOException;
    void save(LocakImage locakImage);
}
