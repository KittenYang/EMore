package com.caij.emore.source.local;

import com.caij.emore.database.bean.ImageInfo;
import com.caij.emore.source.ImageSouce;
import com.caij.emore.utils.db.DBManager;

/**
 * Created by Caij on 2016/6/7.
 */
public class LocalImageSource implements ImageSouce{

    @Override
    public ImageInfo get(String url) {
        return DBManager.getDaoSession().getImageInfoDao().load(url);
    }

    @Override
    public void save(ImageInfo image) {
        DBManager.getDaoSession().getImageInfoDao().insertOrReplace(image);
    }
}
