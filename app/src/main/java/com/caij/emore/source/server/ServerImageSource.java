package com.caij.emore.source.server;

import android.graphics.BitmapFactory;

import com.caij.emore.database.bean.ImageInfo;
import com.caij.emore.source.ImageSouce;
import com.caij.emore.utils.ImageUtil;

import java.io.IOException;

/**
 * Created by Caij on 2016/6/7.
 */
public class ServerImageSource implements ImageSouce{

    @Override
    public ImageInfo get(String url) throws IOException {
        BitmapFactory.Options options = ImageUtil.getImageOptions(url);
        return new ImageInfo(url, options.outWidth, options.outHeight);
    }

    @Override
    public void save(ImageInfo locakImage) {

    }

}
