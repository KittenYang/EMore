package com.caij.emore.source;

import com.caij.emore.database.bean.LocakImage;

import java.io.IOException;

/**
 * Created by Caij on 2016/6/7.
 */
public interface ImageSouce {

    LocakImage get(String url) throws IOException;
    void save(LocakImage locakImage);

}
