package com.caij.weiyo.source.local;

import com.caij.weiyo.database.bean.LocakImage;
import com.caij.weiyo.database.dao.DBManager;
import com.caij.weiyo.source.ImageSouce;

/**
 * Created by Caij on 2016/6/7.
 */
public class LocalImageSource implements ImageSouce{

    @Override
    public LocakImage get(String url) {
        return DBManager.getDaoSession().getLocakImageDao().load(url);
    }

    @Override
    public void save(LocakImage image) {
        DBManager.getDaoSession().getLocakImageDao().insertOrReplace(image);
    }
}
