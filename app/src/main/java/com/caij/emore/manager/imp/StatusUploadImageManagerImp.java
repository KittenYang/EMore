package com.caij.emore.manager.imp;

import com.caij.emore.manager.StatusUploadImageManager;
import com.caij.emore.database.bean.UploadImageResponse;
import com.caij.emore.database.dao.UploadImageResponseDao;
import com.caij.emore.utils.db.DBManager;

import java.util.List;

/**
 * Created by Caij on 2016/10/10.
 */

public class StatusUploadImageManagerImp implements StatusUploadImageManager {
    @Override
    public void insert(UploadImageResponse uploadImageResponse) {
        DBManager.getDaoSession().getUploadImageResponseDao().insertOrReplace(uploadImageResponse);
    }

    @Override
    public UploadImageResponse getByPath(String path) {
        UploadImageResponseDao dao = DBManager.getDaoSession().getUploadImageResponseDao();
        List<UploadImageResponse> uploadImageResponses = dao.queryBuilder().
                where(UploadImageResponseDao.Properties.ImagePath.eq(path)).list();
        UploadImageResponse uploadImageResponse = null;
        if (uploadImageResponses != null && uploadImageResponses.size() > 0) {
            uploadImageResponse = uploadImageResponses.get(0);
        }
        return uploadImageResponse;
    }
}
