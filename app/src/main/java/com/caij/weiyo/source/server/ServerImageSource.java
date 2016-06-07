package com.caij.weiyo.source.server;

import android.graphics.BitmapFactory;

import com.caij.weiyo.database.bean.LocakImage;
import com.caij.weiyo.database.dao.DBManager;
import com.caij.weiyo.source.ImageSouce;
import com.caij.weiyo.utils.ImageUtil;

import java.io.IOException;

/**
 * Created by Caij on 2016/6/7.
 */
public class ServerImageSource implements ImageSouce{

    @Override
    public LocakImage get(String url) throws IOException {
        BitmapFactory.Options options = ImageUtil.getImageOptions(url);
        return new LocakImage(url, options.outWidth, options.outHeight);
    }

    @Override
    public void save(LocakImage locakImage) {

    }

}
