package com.caij.emore.source;

import com.caij.emore.database.bean.ImageInfo;

import java.io.IOException;

/**
 * Created by Caij on 2016/6/7.
 */
public interface ImageSouce {

    ImageInfo get(String url) throws IOException;
    void save(ImageInfo locakImage);

}
